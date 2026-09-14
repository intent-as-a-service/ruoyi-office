package cn.iocoder.yudao.module.crm.intent;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.dict.core.DictFrameworkUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerSaveReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.customer.CrmCustomerDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.followup.CrmFollowUpRecordDO;
import cn.iocoder.yudao.module.crm.enums.DictTypeConstants;
import cn.iocoder.yudao.module.crm.enums.common.CrmBizTypeEnum;
import cn.iocoder.yudao.module.crm.service.business.CrmBusinessService;
import cn.iocoder.yudao.module.crm.service.clue.CrmClueService;
import cn.iocoder.yudao.module.crm.service.contract.CrmContractService;
import cn.iocoder.yudao.module.crm.service.customer.CrmCustomerService;
import cn.iocoder.yudao.module.crm.service.receivable.CrmReceivableService;
import cn.iocoder.yudao.module.crm.service.followup.CrmFollowUpRecordService;
import cn.iocoder.yudao.module.crm.service.followup.bo.CrmFollowUpCreateReqBO;
import dev.intent.sdk.tool.IntentToolContext;
import dev.intent.sdk.tool.IntentTool;
import dev.intent.sdk.tool.Schemas;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * CRM 宿主工具（嵌入式 AI SDK · HostTool 声明处）。
 *
 * <p>每个工具包装一个 CRM 内部服务调用——<b>进程内直调</b>：以当前登录用户
 * 上下文在宿主系统内执行，权限、数据权限与事务天然沿用宿主系统，
 * 无需跨系统权限改造。</p>
 *
 * <p>业务系统接入 SDK 的全部工作量 = 像这样把内部 API 声明成 AgentTool Bean。
 * 工具会被平台侧 {@code IntentAutoConfiguration} 自动收集。</p>
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class CrmIntentConfiguration {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int BIZ_TYPE_CUSTOMER = CrmBizTypeEnum.CRM_CUSTOMER.getType();
    /** AI 标签哨兵：写入客户备注时单独成行，重跑替换不堆叠，且能用一条 SQL 清干净。 */
    private static final String AI_TAG_PREFIX = "【AI标签】";
    /** crm_customer.remark 的列长，写入前按此裁剪，避免超长被数据库截断。 */
    private static final int REMARK_MAX_LENGTH = 500;

    @Bean
    public IntentTool crmGetCustomerTool(CrmCustomerService customerService) {
        Map<String, Object> params = Schemas.object(
                Map.of("customerId", Schemas.string("客户编号（crm_customer.id）")),
                List.of("customerId"));
        return ofData("crm_get_customer",
                "按客户编号读取 CRM 客户详情（名称、等级、行业、来源、成交状态、联系方式、地址、备注等）",
                params,
                (args, signal) -> {
                    Long id = requireLong(args, "customerId");
                    CrmCustomerDO customer = customerService.getCustomer(id);
                    if (customer == null) {
                        return Map.of("found", false, "message", "客户不存在: " + id);
                    }
                    return Map.of("found", true, "customer", toCustomerMap(customer));
                });
    }

    @Bean
    public IntentTool crmSearchCustomersTool(CrmCustomerService customerService) {
        Map<String, Object> params = Schemas.object(
                Map.of("keyword", Schemas.string("客户名称关键词（模糊匹配，空串表示全部）"),
                        "limit", Schemas.number("返回条数上限，默认 10，最大 50")),
                List.of());
        return ofData("crm_search_customers",
                "按名称关键词搜索当前用户可见的 CRM 客户列表（含等级/成交状态/最后跟进内容）",
                params,
                (args, signal) -> {
                    String keyword = args.get("keyword") == null ? "" : String.valueOf(args.get("keyword"));
                    int limit = parseInt(args.get("limit"), 10);
                    CrmCustomerPageReqVO reqVO = new CrmCustomerPageReqVO();
                    reqVO.setName(keyword.isBlank() ? null : keyword);
                    reqVO.setPageSize(Math.min(50, Math.max(1, limit)));
                    reqVO.setPageNo(1);
                    PageResult<CrmCustomerDO> page = customerService.getCustomerPage(reqVO,
                            SecurityFrameworkUtils.getLoginUserId());
                    List<Map<String, Object>> customers = new ArrayList<>();
                    for (CrmCustomerDO customer : page.getList()) {
                        Map<String, Object> item = new LinkedHashMap<>();
                        item.put("id", customer.getId());
                        item.put("name", customer.getName());
                        item.put("level", dictLabel(DictTypeConstants.CRM_CUSTOMER_LEVEL, customer.getLevel()));
                        item.put("dealStatus", Boolean.TRUE.equals(customer.getDealStatus()) ? "已成交" : "未成交");
                        item.put("followUpStatus", Boolean.TRUE.equals(customer.getFollowUpStatus()));
                        item.put("contactLastContent", customer.getContactLastContent());
                        customers.add(item);
                    }
                    return Map.of("total", page.getTotal(), "customers", customers);
                });
    }

    @Bean
    public IntentTool crmListFollowUpsTool(CrmFollowUpRecordService followUpRecordService) {
        Map<String, Object> params = Schemas.object(
                Map.of("customerId", Schemas.string("客户编号（等价于 bizType=2 时的 bizId）"),
                        "bizType", Schemas.enumeration(List.of("1", "2", "3", "4", "5", "7"),
                                "归属对象：1=线索 2=客户 3=联系人 4=商机 5=合同 7=回款；缺省 2（客户）"),
                        "bizId", Schemas.string("归属对象编号（不填时取 customerId）"),
                        "limit", Schemas.number("返回条数上限，默认 10，最大 50")),
                List.of());
        return ofData("crm_list_follow_ups",
                "读取 CRM 历史跟进记录（时间、方式、内容、下次联系时间），按时间倒序——"
                        + "默认读客户，可用 bizType/bizId 读线索、联系人、商机、合同、回款",
                params,
                (args, signal) -> {
                    int bizType = args.get("bizType") == null ? BIZ_TYPE_CUSTOMER
                            : parseInt(args.get("bizType"), BIZ_TYPE_CUSTOMER);
                    String rawId = args.get("bizId") != null && !String.valueOf(args.get("bizId")).isBlank()
                            ? String.valueOf(args.get("bizId"))
                            : (args.get("customerId") == null ? null : String.valueOf(args.get("customerId")));
                    if (rawId == null || rawId.isBlank()) {
                        return Map.of("count", 0, "records", List.of(),
                                "message", "缺少对象编号：请提供 customerId 或 bizId");
                    }
                    Long id = Long.parseLong(rawId.trim());
                    int limit = parseInt(args.get("limit"), 10);
                    Collection<CrmFollowUpRecordDO> records =
                            followUpRecordService.getFollowUpRecordByBiz(bizType, List.of(id));
                    List<Map<String, Object>> items = new ArrayList<>();
                    records.stream()
                            .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                            .limit(Math.min(50, Math.max(1, limit)))
                            .forEach(record -> {
                                Map<String, Object> item = new LinkedHashMap<>();
                                item.put("time", formatTime(record.getCreateTime()));
                                item.put("type", dictLabel(DictTypeConstants.CRM_FOLLOW_UP_TYPE, record.getType()));
                                item.put("content", record.getContent());
                                item.put("nextTime", formatTime(record.getNextTime()));
                                items.add(item);
                            });
                    return Map.of("count", items.size(), "records", items);
                });
    }

    @Bean
    public IntentTool crmCreateFollowUpTool(CrmFollowUpRecordService followUpRecordService) {
        Map<String, Object> params = Schemas.object(
                Map.of("customerId", Schemas.string("客户编号（bizType=2 客户时使用）"),
                        "bizType", Schemas.enumeration(List.of("1", "2", "3", "4", "5", "7"),
                                "记录归属对象：1=线索 2=客户 3=联系人 4=商机 5=合同 7=回款；缺省 2（客户）"),
                        "bizId", Schemas.string("归属对象编号（不填时取 customerId）"),
                        "content", Schemas.string("跟进内容（客观陈述，200 字以内）"),
                        "type", Schemas.enumeration(List.of("1", "2", "3", "4"),
                                "跟进方式：1=电话 2=邮件 3=上门拜访 4=会议"),
                        "nextTime", Schemas.string("建议的下次联系时间，格式 yyyy-MM-dd HH:mm，可为空")),
                List.of("content"));
        return (IntentTool) dev.intent.sdk.tool.LambdaHostTool.ofData("crm_create_follow_up",
                "在 CRM 中新建一条跟进记录，可挂到客户/线索/联系人/商机/合同/回款上"
                        + "（写操作，事务与权限沿用宿主系统）",
                params,
                (Map<String, Object> args, IntentToolContext signal) -> {
                    int bizType = args.get("bizType") == null ? BIZ_TYPE_CUSTOMER
                            : parseInt(args.get("bizType"), BIZ_TYPE_CUSTOMER);
                    if (bizType < 1 || bizType > 7) {
                        throw new IllegalArgumentException("不支持的归属对象: " + bizType);
                    }
                    Long id = args.get("bizId") != null && !String.valueOf(args.get("bizId")).isBlank()
                            ? requireLong(args, "bizId")
                            : requireLong(args, "customerId");
                    CrmFollowUpCreateReqBO bo = new CrmFollowUpCreateReqBO();
                    bo.setBizType(bizType);
                    bo.setBizId(id);
                    bo.setContent(String.valueOf(args.get("content")));
                    if (args.get("type") != null && !String.valueOf(args.get("type")).isBlank()) {
                        bo.setType(parseInt(args.get("type"), 1));
                    }
                    if (args.get("nextTime") != null && !String.valueOf(args.get("nextTime")).isBlank()) {
                        bo.setNextTime(LocalDateTime.parse(String.valueOf(args.get("nextTime")).strip()
                                .replace('T', ' '), DATE_TIME_FORMATTER));
                    }
                    followUpRecordService.createFollowUpRecordBatch(List.of(bo));
                    log.info("[intent] 宿主工具写入跟进记录: customer={}, content={}", id, bo.getContent());
                    return Map.of("success", true, "message", "跟进记录已写入 CRM");
                });
    }

    @Bean
    public IntentTool crmUpdateCustomerTagsTool(CrmCustomerService customerService) {
        Map<String, Object> params = Schemas.object(
                Map.of("customerId", Schemas.string("客户编号"),
                        "tags", Schemas.string("客户标签，多个用顿号/逗号分隔（建议 2~5 个，每个不超过 8 字）")),
                List.of("customerId", "tags"));
        return ofData("crm_update_customer_tags",
                "把 AI 推荐的客户标签写入客户档案备注（写操作；重复执行只保留最新一次 AI 标签，不堆叠）",
                params,
                (args, signal) -> {
                    Long customerId = requireLong(args, "customerId");
                    String tags = String.valueOf(args.get("tags")).replace("，", "、").replace(",", "、").trim();
                    CrmCustomerDO customer = customerService.getCustomer(customerId);
                    if (customer == null) {
                        return Map.of("success", false, "message", "客户不存在: " + customerId);
                    }
                    if (customer.getOwnerUserId() == null) {
                        return Map.of("success", false, "message",
                                "客户 " + customerId + " 还在公海、没有负责人，无法写入备注；请先分配负责人");
                    }
                    String merged = mergeAiTagLine(customer.getRemark(), tags);
                    CrmCustomerSaveReqVO update = new CrmCustomerSaveReqVO();
                    update.setId(customerId);
                    // 宿主 updateCustomer 的方法级校验要求 name / ownerUserId 必填（方法内部会把 ownerUserId 置空，不会改负责人）
                    update.setName(customer.getName());
                    update.setOwnerUserId(customer.getOwnerUserId());
                    update.setRemark(merged);
                    // 走宿主 Service：写权限（CrmPermission WRITE）与操作日志沿用宿主，不绕过治理
                    customerService.updateCustomer(update);
                    log.info("[intent] 宿主工具写入客户标签: customer={}, tags={}", customerId, tags);
                    return Map.of("success", true, "message", "标签已写入客户备注",
                            "remark", merged, "tags", tags);
                });
    }

    /** 备注里的 AI 标签单独成行：重跑替换、真人备注原样保留、超长先裁旧内容再写标签行。 */
    private static String mergeAiTagLine(String remark, String tags) {
        String base = remark == null ? "" : remark;
        int start = base.indexOf(AI_TAG_PREFIX);
        if (start >= 0) {
            int end = base.indexOf('\n', start);
            base = end < 0 ? base.substring(0, start) : base.substring(0, start) + base.substring(end + 1);
        }
        base = base.strip();
        String line = AI_TAG_PREFIX + tags;
        int maxBase = REMARK_MAX_LENGTH - line.length() - 1;
        if (maxBase <= 0) {
            return line.length() > REMARK_MAX_LENGTH ? line.substring(0, REMARK_MAX_LENGTH) : line;
        }
        if (base.length() > maxBase) {
            base = base.substring(0, maxBase);
        }
        return base.isEmpty() ? line : base + "\n" + line;
    }

    @Bean
    public IntentTool crmGetClueTool(CrmClueService clueService) {
        Map<String, Object> params = Schemas.object(
                Map.of("clueId", Schemas.string("线索编号")),
                List.of("clueId"));
        return ofData("crm_get_clue", "按线索编号读取 CRM 线索详情（名称、转化状态、联系方式、来源、备注）",
                params,
                (args, signal) -> {
                    Long id = requireLong(args, "clueId");
                    cn.iocoder.yudao.module.crm.dal.dataobject.clue.CrmClueDO clue = clueService.getClue(id);
                    if (clue == null) {
                        return Map.of("found", false, "message", "线索不存在: " + id);
                    }
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", clue.getId());
                    map.put("name", clue.getName());
                    map.put("transformStatus", Boolean.TRUE.equals(clue.getTransformStatus()) ? "已转化" : "未转化");
                    map.put("mobile", clue.getMobile());
                    map.put("email", clue.getEmail());
                    map.put("industry", clue.getIndustryId());
                    map.put("level", clue.getLevel());
                    map.put("source", clue.getSource());
                    map.put("contactLastContent", clue.getContactLastContent());
                    map.put("remark", clue.getRemark());
                    return map;
                });
    }

    @Bean
    public IntentTool crmGetContractTool(CrmContractService contractService, CrmCustomerService customerService) {
        Map<String, Object> params = Schemas.object(
                Map.of("contractId", Schemas.string("合同编号")),
                List.of("contractId"));
        return ofData("crm_get_contract", "按合同编号读取 CRM 合同详情（编号、金额、审批状态、签署期、归属客户）",
                params,
                (args, signal) -> {
                    Long id = requireLong(args, "contractId");
                    cn.iocoder.yudao.module.crm.dal.dataobject.contract.CrmContractDO contract = contractService.getContract(id);
                    if (contract == null) {
                        return Map.of("found", false, "message", "合同不存在: " + id);
                    }
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", contract.getId());
                    map.put("name", contract.getName());
                    map.put("no", contract.getNo());
                    CrmCustomerDO customer = contract.getCustomerId() == null ? null
                            : customerService.getCustomer(contract.getCustomerId());
                    map.put("customerName", customer == null ? null : customer.getName());
                    map.put("auditStatus", contract.getAuditStatus());
                    map.put("orderDate", formatTime(contract.getOrderDate()));
                    map.put("startTime", formatTime(contract.getStartTime()));
                    map.put("endTime", formatTime(contract.getEndTime()));
                    map.put("totalProductPrice", contract.getTotalProductPrice());
                    map.put("discountPercent", contract.getDiscountPercent());
                    map.put("totalPrice", contract.getTotalPrice());
                    map.put("remark", contract.getRemark());
                    return map;
                });
    }

    @Bean
    public IntentTool crmGetBusinessTool(CrmBusinessService businessService) {
        Map<String, Object> params = Schemas.object(
                Map.of("businessId", Schemas.string("商机编号")),
                List.of("businessId"));
        return ofData("crm_get_business", "按商机编号读取 CRM 商机详情（金额、折扣、阶段、结束状态、成交时间）",
                params,
                (args, signal) -> {
                    Long id = requireLong(args, "businessId");
                    cn.iocoder.yudao.module.crm.dal.dataobject.business.CrmBusinessDO business = businessService.getBusiness(id);
                    if (business == null) {
                        return Map.of("found", false, "message", "商机不存在: " + id);
                    }
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", business.getId());
                    map.put("name", business.getName());
                    map.put("customerId", business.getCustomerId());
                    map.put("statusTypeId", business.getStatusTypeId());
                    map.put("statusId", business.getStatusId());
                    map.put("endStatus", business.getEndStatus());
                    map.put("totalProductPrice", business.getTotalProductPrice());
                    map.put("discountPercent", business.getDiscountPercent());
                    map.put("totalPrice", business.getTotalPrice());
                    map.put("dealTime", formatTime(business.getDealTime()));
                    map.put("remark", business.getRemark());
                    return map;
                });
    }

    @Bean
    public IntentTool crmGetReceivableTool(CrmReceivableService receivableService) {
        Map<String, Object> params = Schemas.object(
                Map.of("receivableId", Schemas.string("回款编号")),
                List.of("receivableId"));
        return ofData("crm_get_receivable", "按回款编号读取 CRM 回款详情（金额、回款方式、回款时间、关联合同）",
                params,
                (args, signal) -> {
                    Long id = requireLong(args, "receivableId");
                    cn.iocoder.yudao.module.crm.dal.dataobject.receivable.CrmReceivableDO receivable = receivableService.getReceivable(id);
                    if (receivable == null) {
                        return Map.of("found", false, "message", "回款不存在: " + id);
                    }
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", receivable.getId());
                    map.put("no", receivable.getNo());
                    map.put("price", receivable.getPrice());
                    map.put("returnType", receivable.getReturnType());
                    map.put("returnTime", formatTime(receivable.getReturnTime()));
                    map.put("customerId", receivable.getCustomerId());
                    map.put("contractId", receivable.getContractId());
                    map.put("remark", receivable.getRemark());
                    return map;
                });
    }

    // ------------------------------------------------------------ helpers

    private static IntentTool ofData(String name, String description, Map<String, Object> parameters,
            BiFunction<Map<String, Object>, IntentToolContext, Map<String, Object>> executor) {
        return dev.intent.sdk.tool.LambdaHostTool.ofData(name, description, parameters, executor);
    }

    private static Long requireLong(Map<String, Object> args, String key) {
        Object value = args.get(key);
        if (value == null) {
            throw new IllegalArgumentException("缺少参数: " + key);
        }
        return value instanceof Number number ? number.longValue() : Long.parseLong(String.valueOf(value));
    }

    private static int parseInt(Object value, int fallback) {
        if (value == null) {
            return fallback;
        }
        return (int) Double.parseDouble(String.valueOf(value));
    }

    private static Map<String, Object> toCustomerMap(CrmCustomerDO customer) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", customer.getId());
        map.put("name", customer.getName());
        map.put("level", dictLabel(DictTypeConstants.CRM_CUSTOMER_LEVEL, customer.getLevel()));
        map.put("industry", dictLabel(DictTypeConstants.CRM_CUSTOMER_INDUSTRY, customer.getIndustryId()));
        map.put("source", dictLabel(DictTypeConstants.CRM_CUSTOMER_SOURCE, customer.getSource()));
        map.put("dealStatus", Boolean.TRUE.equals(customer.getDealStatus()) ? "已成交" : "未成交");
        map.put("followUpStatus", Boolean.TRUE.equals(customer.getFollowUpStatus()));
        map.put("mobile", customer.getMobile());
        map.put("email", customer.getEmail());
        map.put("address", customer.getDetailAddress());
        map.put("contactLastTime", formatTime(customer.getContactLastTime()));
        map.put("contactLastContent", customer.getContactLastContent());
        map.put("contactNextTime", formatTime(customer.getContactNextTime()));
        map.put("remark", customer.getRemark());
        return map;
    }

    private static String dictLabel(String dictType, Object value) {
        if (value == null) {
            return null;
        }
        try {
            String label = DictFrameworkUtils.parseDictDataLabel(dictType,
                    (int) Double.parseDouble(String.valueOf(value)));
            return label != null ? label : String.valueOf(value);
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }

    private static String formatTime(LocalDateTime time) {
        return time == null ? null : DATE_TIME_FORMATTER.format(time);
    }
}
