package cn.iocoder.yudao.module.crm.intent;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.dict.core.DictFrameworkUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.crm.controller.admin.business.vo.business.CrmBusinessPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.clue.vo.CrmCluePageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.contact.vo.CrmContactPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.contract.vo.contract.CrmContractPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.product.vo.product.CrmProductPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.receivable.vo.plan.CrmReceivablePlanPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.receivable.vo.receivable.CrmReceivablePageReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.business.CrmBusinessDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.business.CrmBusinessProductDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.business.CrmBusinessStatusDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.clue.CrmClueDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.contact.CrmContactDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.contract.CrmContractDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.customer.CrmCustomerDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.followup.CrmFollowUpRecordDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.product.CrmProductDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.receivable.CrmReceivableDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.receivable.CrmReceivablePlanDO;
import cn.iocoder.yudao.module.crm.enums.DictTypeConstants;
import cn.iocoder.yudao.module.crm.enums.common.CrmBizTypeEnum;
import cn.iocoder.yudao.module.crm.enums.common.CrmSceneTypeEnum;
import cn.iocoder.yudao.module.crm.service.business.CrmBusinessService;
import cn.iocoder.yudao.module.crm.service.business.CrmBusinessStatusService;
import cn.iocoder.yudao.module.crm.service.clue.CrmClueService;
import cn.iocoder.yudao.module.crm.service.contact.CrmContactService;
import cn.iocoder.yudao.module.crm.service.contract.CrmContractService;
import cn.iocoder.yudao.module.crm.service.customer.CrmCustomerService;
import cn.iocoder.yudao.module.crm.service.followup.CrmFollowUpRecordService;
import cn.iocoder.yudao.module.crm.service.product.CrmProductService;
import cn.iocoder.yudao.module.crm.service.receivable.CrmReceivablePlanService;
import cn.iocoder.yudao.module.crm.service.receivable.CrmReceivableService;
import dev.intent.sdk.tool.IntentTool;
import dev.intent.sdk.tool.IntentToolContext;
import dev.intent.sdk.tool.LambdaHostTool;
import dev.intent.sdk.tool.Schemas;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/**
 * CRM 意图只读工具（第二批）：跨对象查询 + 事实聚合。
 *
 * <p>与 {@link CrmIntentConfiguration} 的分工：那边是"单个对象详情 + 写回"，
 * 这边是"让 AI 像销售经理一样横向看数据"——一个客户下面有哪些联系人、
 * 哪些商机、合同与回款进度如何，以及今天该跟进谁。</p>
 *
 * <p>全部工具只读且进程内直调宿主 Service：数据权限、租户隔离与用户上下文
 * 天然沿用宿主系统，无需额外鉴权改造。</p>
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class CrmIntentQueryConfiguration {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int MAX_LIMIT = 50;
    private static final Integer SCENE_OWNER = CrmSceneTypeEnum.OWNER.getType();

    // ============================================================ 联系人

    @Bean
    public IntentTool crmGetContactTool(CrmContactService contactService) {
        Map<String, Object> params = Schemas.object(
                Map.of("contactId", Schemas.string("联系人编号")),
                List.of("contactId"));
        return ofData("crm_get_contact",
                "按联系人编号读取 CRM 联系人详情（姓名、职务、所属客户、是否关键人、联系方式、最近联系记录）",
                params,
                (args, signal) -> {
                    CrmContactDO contact = contactService.getContact(requireLong(args, "contactId"));
                    return contact == null ? Map.of("found", false)
                            : Map.of("found", true, "contact", toContactMap(contact));
                });
    }

    @Bean
    public IntentTool crmListContactsTool(CrmContactService contactService) {
        Map<String, Object> params = Schemas.object(
                Map.of("customerId", Schemas.string("客户编号"),
                        "limit", Schemas.number("返回条数上限，默认 20，最大 50")),
                List.of("customerId"));
        return ofData("crm_list_contacts",
                "读取指定客户下的联系人列表（用于识别决策链：谁是关键人、谁在什么岗位）",
                params,
                (args, signal) -> {
                    CrmContactPageReqVO reqVO = new CrmContactPageReqVO();
                    reqVO.setCustomerId(requireLong(args, "customerId"));
                    reqVO.setPageNo(1);
                    reqVO.setPageSize(limit(args, 20));
                    PageResult<CrmContactDO> page = contactService.getContactPageByCustomerId(reqVO);
                    List<Map<String, Object>> items = new ArrayList<>();
                    for (CrmContactDO contact : page.getList()) {
                        items.add(toContactMap(contact));
                    }
                    return Map.of("total", page.getTotal(), "contacts", items);
                });
    }

    // ============================================================ 商机

    @Bean
    public IntentTool crmListBusinessesTool(CrmBusinessService businessService,
            CrmBusinessStatusService statusService) {
        Map<String, Object> params = Schemas.object(
                Map.of("customerId", Schemas.string("客户编号（可选，不传则查当前用户负责的全部商机）"),
                        "onlyOpen", Schemas.string("是否只看未结束的商机：true/false，默认 false"),
                        "limit", Schemas.number("返回条数上限，默认 20，最大 50")),
                List.of());
        return ofData("crm_list_businesses",
                "读取商机列表（阶段、金额、折扣、是否结束、距上次更新天数），可按客户过滤——"
                        + "用于判断商机是否停滞、客户在手机会有多少",
                params,
                (args, signal) -> {
                    CrmBusinessPageReqVO reqVO = new CrmBusinessPageReqVO();
                    reqVO.setSceneType(SCENE_OWNER);
                    reqVO.setPageNo(1);
                    reqVO.setPageSize(limit(args, 20));
                    if (hasText(args, "customerId")) {
                        reqVO.setCustomerId(requireLong(args, "customerId"));
                    }
                    PageResult<CrmBusinessDO> page = businessService.getBusinessPage(reqVO,
                            SecurityFrameworkUtils.getLoginUserId());
                    boolean onlyOpen = Boolean.parseBoolean(String.valueOf(args.getOrDefault("onlyOpen", "false")));
                    List<Map<String, Object>> items = new ArrayList<>();
                    for (CrmBusinessDO business : page.getList()) {
                        if (onlyOpen && business.getEndStatus() != null) {
                            continue;
                        }
                        items.add(toBusinessMap(business, statusService));
                    }
                    return Map.of("total", page.getTotal(), "count", items.size(), "businesses", items);
                });
    }

    @Bean
    public IntentTool crmBusinessProductsTool(CrmBusinessService businessService,
            CrmProductService productService) {
        Map<String, Object> params = Schemas.object(
                Map.of("businessId", Schemas.string("商机编号")),
                List.of("businessId"));
        return ofData("crm_business_products",
                "读取商机的产品明细（产品、标准价、成交价、数量、小计）——用于分析报价结构与折扣空间",
                params,
                (args, signal) -> {
                    Long businessId = requireLong(args, "businessId");
                    List<CrmBusinessProductDO> lines = businessService.getBusinessProductListByBusinessId(businessId);
                    List<Map<String, Object>> items = new ArrayList<>();
                    for (CrmBusinessProductDO line : lines) {
                        Map<String, Object> item = new LinkedHashMap<>();
                        item.put("productId", line.getProductId());
                        CrmProductDO product = line.getProductId() == null ? null
                                : productService.getProduct(line.getProductId());
                        item.put("productName", product == null ? null : product.getName());
                        item.put("productUnit", product == null ? null : product.getUnit());
                        item.put("productPrice", line.getProductPrice());
                        item.put("businessPrice", line.getBusinessPrice());
                        item.put("count", line.getCount());
                        item.put("totalPrice", line.getTotalPrice());
                        items.add(item);
                    }
                    return Map.of("count", items.size(), "products", items);
                });
    }

    // ============================================================ 合同

    @Bean
    public IntentTool crmListContractsTool(CrmContractService contractService) {
        Map<String, Object> params = Schemas.object(
                Map.of("customerId", Schemas.string("客户编号（可选）"),
                        "expiryType", Schemas.enumeration(List.of("1", "2"),
                                "到期筛选：1=即将到期（按合同配置的提醒窗口）2=已过期；不传=全部"),
                        "auditStatus", Schemas.enumeration(List.of("0", "10", "20", "30", "40"),
                                "审批状态：0=未提交 10=审批中 20=审核通过 30=审核不通过 40=已取消"),
                        "limit", Schemas.number("返回条数上限，默认 20，最大 50")),
                List.of());
        return ofData("crm_list_contracts",
                "读取合同列表（编号、客户、金额、审批状态、起止日期、距到期天数）——可按客户/到期状态/审批状态过滤",
                params,
                (args, signal) -> {
                    CrmContractPageReqVO reqVO = new CrmContractPageReqVO();
                    reqVO.setSceneType(SCENE_OWNER);
                    reqVO.setPageNo(1);
                    reqVO.setPageSize(limit(args, 20));
                    if (hasText(args, "customerId")) {
                        reqVO.setCustomerId(requireLong(args, "customerId"));
                    }
                    if (hasText(args, "expiryType")) {
                        reqVO.setExpiryType(asInt(args.get("expiryType")));
                    }
                    if (hasText(args, "auditStatus")) {
                        reqVO.setAuditStatus(asInt(args.get("auditStatus")));
                    }
                    PageResult<CrmContractDO> page = contractService.getContractPage(reqVO,
                            SecurityFrameworkUtils.getLoginUserId());
                    List<Map<String, Object>> items = new ArrayList<>();
                    for (CrmContractDO contract : page.getList()) {
                        Map<String, Object> item = new LinkedHashMap<>();
                        item.put("id", contract.getId());
                        item.put("no", contract.getNo());
                        item.put("name", contract.getName());
                        item.put("customerId", contract.getCustomerId());
                        item.put("totalPrice", contract.getTotalPrice());
                        item.put("auditStatus", dictLabel(DictTypeConstants.CRM_AUDIT_STATUS, contract.getAuditStatus()));
                        item.put("startTime", formatDate(contract.getStartTime()));
                        item.put("endTime", formatDate(contract.getEndTime()));
                        item.put("daysToExpire", contract.getEndTime() == null ? null
                                : daysUntil(contract.getEndTime()));
                        item.put("remark", contract.getRemark());
                        items.add(item);
                    }
                    return Map.of("total", page.getTotal(), "count", items.size(), "contracts", items);
                });
    }

    // ============================================================ 回款

    @Bean
    public IntentTool crmListReceivablesTool(CrmReceivableService receivableService) {
        Map<String, Object> params = Schemas.object(
                Map.of("customerId", Schemas.string("客户编号（可选）"),
                        "contractId", Schemas.string("合同编号（可选）"),
                        "auditStatus", Schemas.enumeration(List.of("0", "10", "20", "30", "40"),
                                "审批状态：0=未提交 10=审批中 20=审核通过 30=审核不通过 40=已取消"),
                        "limit", Schemas.number("返回条数上限，默认 20，最大 50")),
                List.of());
        return ofData("crm_list_receivables",
                "读取已登记的回款记录（回款单号、金额、回款方式、回款时间、关联合同）——用于对账与回款进度分析",
                params,
                (args, signal) -> {
                    CrmReceivablePageReqVO reqVO = new CrmReceivablePageReqVO();
                    reqVO.setSceneType(SCENE_OWNER);
                    reqVO.setPageNo(1);
                    reqVO.setPageSize(limit(args, 20));
                    if (hasText(args, "customerId")) {
                        reqVO.setCustomerId(requireLong(args, "customerId"));
                    }
                    if (hasText(args, "contractId")) {
                        reqVO.setContractId(requireLong(args, "contractId"));
                    }
                    if (hasText(args, "auditStatus")) {
                        reqVO.setAuditStatus(asInt(args.get("auditStatus")));
                    }
                    PageResult<CrmReceivableDO> page = receivableService.getReceivablePage(reqVO,
                            SecurityFrameworkUtils.getLoginUserId());
                    List<Map<String, Object>> items = new ArrayList<>();
                    BigDecimal sum = BigDecimal.ZERO;
                    for (CrmReceivableDO receivable : page.getList()) {
                        Map<String, Object> item = new LinkedHashMap<>();
                        item.put("id", receivable.getId());
                        item.put("no", receivable.getNo());
                        item.put("price", receivable.getPrice());
                        item.put("contractId", receivable.getContractId());
                        item.put("returnTime", formatDate(receivable.getReturnTime()));
                        item.put("returnType", dictLabel(DictTypeConstants.CRM_RECEIVABLE_RETURN_TYPE,
                                receivable.getReturnType()));
                        item.put("auditStatus", dictLabel(DictTypeConstants.CRM_AUDIT_STATUS,
                                receivable.getAuditStatus()));
                        items.add(item);
                        if (receivable.getPrice() != null) {
                            sum = sum.add(receivable.getPrice());
                        }
                    }
                    return Map.of("total", page.getTotal(), "count", items.size(),
                            "returnedAmount", sum, "receivables", items);
                });
    }

    @Bean
    public IntentTool crmListReceivablePlansTool(CrmReceivablePlanService planService) {
        Map<String, Object> params = Schemas.object(
                Map.of("customerId", Schemas.string("客户编号（可选）"),
                        "contractId", Schemas.string("合同编号（可选）"),
                        "remindType", Schemas.enumeration(List.of("1", "2", "3"),
                                "提醒类型：1=待回款（到期需回款）2=已逾期未回款 3=已回款；不传=全部"),
                        "limit", Schemas.number("返回条数上限，默认 20，最大 50")),
                List.of());
        return ofData("crm_list_receivable_plans",
                "读取回款计划（期数、应收金额、约定回款日、提醒日、是否已回款、逾期天数）——"
                        + "用于判断账期风险与生成催收计划",
                params,
                (args, signal) -> {
                    CrmReceivablePlanPageReqVO reqVO = new CrmReceivablePlanPageReqVO();
                    reqVO.setSceneType(SCENE_OWNER);
                    reqVO.setPageNo(1);
                    reqVO.setPageSize(limit(args, 20));
                    if (hasText(args, "customerId")) {
                        reqVO.setCustomerId(requireLong(args, "customerId"));
                    }
                    if (hasText(args, "contractId")) {
                        reqVO.setContractId(requireLong(args, "contractId"));
                    }
                    if (hasText(args, "remindType")) {
                        reqVO.setRemindType(asInt(args.get("remindType")));
                    }
                    PageResult<CrmReceivablePlanDO> page = planService.getReceivablePlanPage(reqVO,
                            SecurityFrameworkUtils.getLoginUserId());
                    List<Map<String, Object>> items = new ArrayList<>();
                    BigDecimal overdueAmount = BigDecimal.ZERO;
                    for (CrmReceivablePlanDO plan : page.getList()) {
                        Map<String, Object> item = new LinkedHashMap<>();
                        item.put("id", plan.getId());
                        item.put("period", plan.getPeriod());
                        item.put("contractId", plan.getContractId());
                        item.put("customerId", plan.getCustomerId());
                        item.put("price", plan.getPrice());
                        item.put("returnTime", formatDate(plan.getReturnTime()));
                        item.put("remindTime", formatDate(plan.getRemindTime()));
                        item.put("received", plan.getReceivableId() != null);
                        Long overdueDays = null;
                        if (plan.getReceivableId() == null && plan.getReturnTime() != null
                                && plan.getReturnTime().toLocalDate().isBefore(LocalDate.now())) {
                            overdueDays = daysSince(plan.getReturnTime());
                            if (plan.getPrice() != null) {
                                overdueAmount = overdueAmount.add(plan.getPrice());
                            }
                        }
                        item.put("overdueDays", overdueDays);
                        item.put("remark", plan.getRemark());
                        items.add(item);
                    }
                    return Map.of("total", page.getTotal(), "count", items.size(),
                            "overdueAmount", overdueAmount, "plans", items);
                });
    }

    // ============================================================ 线索

    @Bean
    public IntentTool crmListCluesTool(CrmClueService clueService) {
        Map<String, Object> params = Schemas.object(
                Map.of("followUpStatus", Schemas.string("跟进状态：true=已跟进 false=待跟进；不传=全部"),
                        "transformStatus", Schemas.string("转化状态：true=已转化 false=未转化；不传=全部"),
                        "limit", Schemas.number("返回条数上限，默认 20，最大 50")),
                List.of());
        return ofData("crm_list_clues",
                "读取线索列表（名称、等级、来源、行业、跟进状态、转化状态、距上次联系天数）——"
                        + "用于筛选优先跟进名单与批量分级",
                params,
                (args, signal) -> {
                    CrmCluePageReqVO reqVO = new CrmCluePageReqVO();
                    reqVO.setSceneType(SCENE_OWNER);
                    reqVO.setPageNo(1);
                    reqVO.setPageSize(limit(args, 20));
                    if (hasText(args, "followUpStatus")) {
                        reqVO.setFollowUpStatus(Boolean.parseBoolean(String.valueOf(args.get("followUpStatus"))));
                    }
                    if (hasText(args, "transformStatus")) {
                        reqVO.setTransformStatus(Boolean.parseBoolean(String.valueOf(args.get("transformStatus"))));
                    }
                    PageResult<CrmClueDO> page = clueService.getCluePage(reqVO,
                            SecurityFrameworkUtils.getLoginUserId());
                    List<Map<String, Object>> items = new ArrayList<>();
                    for (CrmClueDO clue : page.getList()) {
                        Map<String, Object> item = new LinkedHashMap<>();
                        item.put("id", clue.getId());
                        item.put("name", clue.getName());
                        item.put("level", clue.getLevel());
                        item.put("source", clue.getSource());
                        item.put("industryId", clue.getIndustryId());
                        item.put("transformStatus", Boolean.TRUE.equals(clue.getTransformStatus()) ? "已转化" : "未转化");
                        item.put("followUpStatus", Boolean.TRUE.equals(clue.getFollowUpStatus()) ? "已跟进" : "待跟进");
                        item.put("contactLastTime", formatDate(clue.getContactLastTime()));
                        item.put("daysSinceLastContact", clue.getContactLastTime() == null ? null
                                : Duration.between(clue.getContactLastTime(), LocalDateTime.now()).toDays());
                        item.put("contactLastContent", clue.getContactLastContent());
                        item.put("remark", clue.getRemark());
                        items.add(item);
                    }
                    return Map.of("total", page.getTotal(), "count", items.size(), "clues", items);
                });
    }

    // ============================================================ 产品

    @Bean
    public IntentTool crmGetProductTool(CrmProductService productService) {
        Map<String, Object> params = Schemas.object(
                Map.of("productId", Schemas.string("产品编号")),
                List.of("productId"));
        return ofData("crm_get_product",
                "按产品编号读取产品详情（名称、编码、单位、标准价、状态、分类、描述）",
                params,
                (args, signal) -> {
                    CrmProductDO product = productService.getProduct(requireLong(args, "productId"));
                    return product == null ? Map.of("found", false)
                            : Map.of("found", true, "product", toProductMap(product));
                });
    }

    @Bean
    public IntentTool crmSearchProductsTool(CrmProductService productService) {
        Map<String, Object> params = Schemas.object(
                Map.of("keyword", Schemas.string("产品名称关键词（模糊匹配，空串表示全部）"),
                        "limit", Schemas.number("返回条数上限，默认 20，最大 50")),
                List.of());
        return ofData("crm_search_products",
                "按名称关键词检索产品目录（含标准价、单位、分类）——用于组合推荐与报价测算",
                params,
                (args, signal) -> {
                    CrmProductPageReqVO reqVO = new CrmProductPageReqVO();
                    String keyword = args.get("keyword") == null ? "" : String.valueOf(args.get("keyword"));
                    reqVO.setName(keyword.isBlank() ? null : keyword);
                    reqVO.setPageNo(1);
                    reqVO.setPageSize(limit(args, 20));
                    PageResult<CrmProductDO> page = productService.getProductPage(reqVO);
                    List<Map<String, Object>> items = new ArrayList<>();
                    for (CrmProductDO product : page.getList()) {
                        items.add(toProductMap(product));
                    }
                    return Map.of("total", page.getTotal(), "count", items.size(), "products", items);
                });
    }

    // ============================================================ 查重 / 公海（AI 主动找活干）

    @Bean
    public IntentTool crmCheckCustomerDuplicateTool(CrmCustomerService customerService,
            cn.iocoder.yudao.module.crm.dal.mysql.customer.CrmCustomerMapper customerMapper) {
        Map<String, Object> params = Schemas.object(
                Map.of("name", Schemas.string("待查重的客户名称（模糊匹配）"),
                        "mobile", Schemas.string("手机号（精确匹配）"),
                        "email", Schemas.string("邮箱（精确匹配）"),
                        "limit", Schemas.number("每个维度的返回条数上限，默认 5，最大 20")),
                List.of());
        return ofData("crm_check_customer_duplicate",
                "按名称/手机/邮箱三个维度查重，返回疑似重复的客户与命中字段——"
                        + "用于新建客户前的查重、存量客户的合并与关联判断",
                params,
                (args, signal) -> {
                    String name = hasText(args, "name") ? String.valueOf(args.get("name")).trim() : "";
                    String mobile = hasText(args, "mobile") ? String.valueOf(args.get("mobile")).trim() : "";
                    String email = hasText(args, "email") ? String.valueOf(args.get("email")).trim() : "";
                    if (name.isEmpty() && mobile.isEmpty() && email.isEmpty()) {
                        throw new IllegalArgumentException("至少提供 name / mobile / email 之一");
                    }
                    int size = Math.min(20, Math.max(1, hasText(args, "limit") ? asInt(args.get("limit")) : 5));
                    Long userId = SecurityFrameworkUtils.getLoginUserId();
                    Map<Long, Map<String, Object>> candidates = new LinkedHashMap<>();
                    List<String> checked = new ArrayList<>();
                    if (!name.isEmpty()) {
                        checked.add("name");
                        collectCandidates(candidates, searchCustomers(customerService, userId, name, null, size),
                                "名称相似");
                    }
                    if (!mobile.isEmpty()) {
                        checked.add("mobile");
                        collectCandidates(candidates, searchCustomers(customerService, userId, null, mobile, size),
                                "手机号相同");
                    }
                    if (!email.isEmpty()) {
                        checked.add("email");
                        collectCandidates(candidates, emailCustomers(customerMapper, email, size), "邮箱相同");
                    }
                    boolean duplicate = !candidates.isEmpty();
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("duplicate", duplicate);
                    result.put("checked", checked);
                    result.put("count", candidates.size());
                    result.put("candidates", new ArrayList<>(candidates.values()));
                    result.put("hint", duplicate
                            ? "存在疑似重复客户：请逐条核对是否同一主体，再决定合并或仅建立关联"
                            : "未发现重复客户，可正常建档");
                    return result;
                });
    }

    @Bean
    public IntentTool crmListPoolCustomersTool(CrmCustomerService customerService) {
        Map<String, Object> params = Schemas.object(
                Map.of("level", Schemas.enumeration(List.of("1", "2", "3"),
                                "客户等级过滤：1=A（重点）2=B 3=C；不传 = 全部"),
                        "limit", Schemas.number("返回条数上限，默认 20，最大 50")),
                List.of());
        return ofData("crm_list_pool_customers",
                "读取公海客户（未分配负责人的客户）清单：名称、等级、行业、来源与库龄——"
                        + "用于客户分配与销售负载平衡",
                params,
                (args, signal) -> {
                    CrmCustomerPageReqVO reqVO = new CrmCustomerPageReqVO();
                    reqVO.setPool(true);
                    reqVO.setPageNo(1);
                    reqVO.setPageSize(limit(args, 20));
                    if (hasText(args, "level")) {
                        reqVO.setLevel(asInt(args.get("level")));
                    }
                    PageResult<CrmCustomerDO> page = customerService.getCustomerPage(reqVO,
                            SecurityFrameworkUtils.getLoginUserId());
                    List<Map<String, Object>> items = new ArrayList<>();
                    List<CrmCustomerDO> poolList = page.getList() == null ? List.of() : page.getList();
                    for (CrmCustomerDO customer : poolList) {
                        Map<String, Object> item = toCustomerMap(customer);
                        item.put("mobile", customer.getMobile());
                        item.put("createTime", formatDate(customer.getCreateTime()));
                        item.put("poolDays", customer.getCreateTime() == null ? null : daysSince(customer.getCreateTime()));
                        items.add(item);
                    }
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("total", page.getTotal());
                    result.put("customers", items);
                    return result;
                });
    }

    /** 查重候选合并：同一客户被多个维度命中时合并命中原因，而不是重复出现两行。 */
    private static void collectCandidates(Map<Long, Map<String, Object>> target,
            List<CrmCustomerDO> customers, String reason) {
        for (CrmCustomerDO customer : customers) {
            Map<String, Object> item = target.computeIfAbsent(customer.getId(), id -> {
                Map<String, Object> row = toCustomerMap(customer);
                row.put("mobile", customer.getMobile());
                row.put("email", customer.getEmail());
                row.put("ownerUserId", customer.getOwnerUserId());
                row.put("matchReasons", new ArrayList<String>());
                return row;
            });
            @SuppressWarnings("unchecked")
            List<String> reasons = (List<String>) item.get("matchReasons");
            if (!reasons.contains(reason)) {
                reasons.add(reason);
            }
        }
    }

    private static List<CrmCustomerDO> searchCustomers(CrmCustomerService customerService, Long userId,
            String name, String mobile, int limit) {
        CrmCustomerPageReqVO reqVO = new CrmCustomerPageReqVO();
        reqVO.setName(name);
        reqVO.setMobile(mobile);
        reqVO.setPageNo(1);
        reqVO.setPageSize(limit);
        List<CrmCustomerDO> rows = customerService.getCustomerPage(reqVO, userId).getList();
        return rows == null ? List.of() : rows;
    }

    /** 邮箱不在宿主列表检索条件里，用精确匹配直查——查重是"找一个已知标识"，不是浏览数据。 */
    private static List<CrmCustomerDO> emailCustomers(
            cn.iocoder.yudao.module.crm.dal.mysql.customer.CrmCustomerMapper customerMapper,
            String email, int limit) {
        List<CrmCustomerDO> rows = customerMapper.selectList(CrmCustomerDO::getEmail, email);
        return rows == null ? List.of() : rows.stream().limit(limit).toList();
    }

    // ============================================================ 事实聚合

    /**
     * 客户事实包：健康度诊断、拜访准备、流失预警共用。
     *
     * <p>把"算健康度要用到的数字"一次性取好交给模型，
     * 避免模型为了凑指标反复调用多个工具（又慢又容易算错）。</p>
     */
    @Bean
    public IntentTool crmCustomerFactsTool(CrmCustomerService customerService,
            CrmBusinessService businessService, CrmContractService contractService,
            CrmReceivableService receivableService, CrmReceivablePlanService planService,
            CrmContactService contactService, CrmFollowUpRecordService followUpRecordService) {
        Map<String, Object> params = Schemas.object(
                Map.of("customerId", Schemas.string("客户编号")),
                List.of("customerId"));
        return ofData("crm_customer_facts",
                "一次性读取客户的关键经营事实：基础档案、最近联系、跟进频次、在手商机、"
                        + "合同存量与到期、回款与逾期、联系人覆盖——"
                        + "用于客户健康度诊断、拜访准备、流失预警",
                params,
                (args, signal) -> {
                    Long customerId = requireLong(args, "customerId");
                    Long userId = SecurityFrameworkUtils.getLoginUserId();
                    CrmCustomerDO customer = customerService.getCustomer(customerId);
                    if (customer == null) {
                        return Map.of("found", false, "message", "客户不存在: " + customerId);
                    }
                    Map<String, Object> facts = new LinkedHashMap<>();
                    facts.put("found", true);
                    facts.put("customer", toCustomerMap(customer));
                    facts.put("now", formatDateTime(LocalDateTime.now()));
                    facts.put("daysSinceLastContact", customer.getContactLastTime() == null ? null
                            : Duration.between(customer.getContactLastTime(), LocalDateTime.now()).toDays());
                    facts.put("daysSinceOwner", customer.getOwnerTime() == null ? null
                            : Duration.between(customer.getOwnerTime(), LocalDateTime.now()).toDays());

                    // 跟进频次
                    Collection<CrmFollowUpRecordDO> records = followUpRecordService
                            .getFollowUpRecordByBiz(CrmBizTypeEnum.CRM_CUSTOMER.getType(), List.of(customerId));
                    long last90 = records.stream()
                            .filter(r -> r.getCreateTime() != null
                                    && r.getCreateTime().isAfter(LocalDateTime.now().minusDays(90)))
                            .count();
                    facts.put("followUpTotal", records.size());
                    facts.put("followUpLast90Days", last90);
                    facts.put("lastFollowUpTime", records.stream()
                            .map(CrmFollowUpRecordDO::getCreateTime).filter(Objects::nonNull)
                            .max(LocalDateTime::compareTo).map(CrmIntentQueryConfiguration::formatDateTime)
                            .orElse(null));

                    // 联系人覆盖
                    CrmContactPageReqVO contactReq = new CrmContactPageReqVO();
                    contactReq.setCustomerId(customerId);
                    contactReq.setPageNo(1);
                    contactReq.setPageSize(MAX_LIMIT);
                    PageResult<CrmContactDO> contacts = contactService.getContactPageByCustomerId(contactReq);
                    facts.put("contactCount", contacts.getTotal());
                    facts.put("masterContactCount", contacts.getList().stream()
                            .filter(c -> Boolean.TRUE.equals(c.getMaster())).count());

                    // 商机
                    CrmBusinessPageReqVO businessReq = new CrmBusinessPageReqVO();
                    businessReq.setCustomerId(customerId);
                    businessReq.setSceneType(SCENE_OWNER);
                    businessReq.setPageNo(1);
                    businessReq.setPageSize(MAX_LIMIT);
                    List<CrmBusinessDO> businesses = businessService.getBusinessPage(businessReq, userId).getList();
                    List<CrmBusinessDO> openBusinesses = businesses.stream()
                            .filter(b -> b.getEndStatus() == null).toList();
                    facts.put("businessTotal", businesses.size());
                    facts.put("openBusinessCount", openBusinesses.size());
                    facts.put("openBusinessAmount", sum(openBusinesses.stream()
                            .map(CrmBusinessDO::getTotalPrice).toList()));
                    facts.put("openBusinesses", openBusinesses.stream().map(b -> {
                        Map<String, Object> item = new LinkedHashMap<>();
                        item.put("id", b.getId());
                        item.put("name", b.getName());
                        item.put("totalPrice", b.getTotalPrice());
                        item.put("statusId", b.getStatusId());
                        item.put("daysSinceUpdate", b.getUpdateTime() == null ? null
                                : Duration.between(b.getUpdateTime(), LocalDateTime.now()).toDays());
                        return item;
                    }).toList());

                    // 合同
                    CrmContractPageReqVO contractReq = new CrmContractPageReqVO();
                    contractReq.setCustomerId(customerId);
                    contractReq.setSceneType(SCENE_OWNER);
                    contractReq.setPageNo(1);
                    contractReq.setPageSize(MAX_LIMIT);
                    List<CrmContractDO> contracts = contractService.getContractPage(contractReq, userId).getList();
                    facts.put("contractCount", contracts.size());
                    facts.put("contractAmount", sum(contracts.stream()
                            .map(CrmContractDO::getTotalPrice).toList()));
                    facts.put("expiringContracts", contracts.stream()
                            .filter(c -> c.getEndTime() != null && c.getEndTime().isAfter(LocalDateTime.now())
                                    && c.getEndTime().isBefore(LocalDateTime.now().plusDays(90)))
                            .map(c -> {
                                Map<String, Object> item = new LinkedHashMap<>();
                                item.put("id", c.getId());
                                item.put("name", c.getName());
                                item.put("totalPrice", c.getTotalPrice());
                                item.put("endTime", formatDate(c.getEndTime()));
                                item.put("daysToExpire", daysUntil(c.getEndTime()));
                                return item;
                            }).toList());

                    // 回款与回款计划
                    CrmReceivablePageReqVO receivableReq = new CrmReceivablePageReqVO();
                    receivableReq.setCustomerId(customerId);
                    receivableReq.setSceneType(SCENE_OWNER);
                    receivableReq.setPageNo(1);
                    receivableReq.setPageSize(MAX_LIMIT);
                    List<CrmReceivableDO> receivables = receivableService
                            .getReceivablePage(receivableReq, userId).getList();
                    facts.put("receivedAmount", sum(receivables.stream()
                            .map(CrmReceivableDO::getPrice).toList()));

                    CrmReceivablePlanPageReqVO planReq = new CrmReceivablePlanPageReqVO();
                    planReq.setCustomerId(customerId);
                    planReq.setSceneType(SCENE_OWNER);
                    planReq.setPageNo(1);
                    planReq.setPageSize(MAX_LIMIT);
                    List<CrmReceivablePlanDO> plans = planService.getReceivablePlanPage(planReq, userId).getList();
                    facts.put("planAmount", sum(plans.stream().map(CrmReceivablePlanDO::getPrice).toList()));
                    facts.put("uncollectedPlanAmount", sum(plans.stream()
                            .filter(p -> p.getReceivableId() == null)
                            .map(CrmReceivablePlanDO::getPrice).toList()));
                    List<Map<String, Object>> overdue = new ArrayList<>();
                    for (CrmReceivablePlanDO plan : plans) {
                        if (plan.getReceivableId() == null && plan.getReturnTime() != null
                                && plan.getReturnTime().toLocalDate().isBefore(LocalDate.now())) {
                            Map<String, Object> item = new LinkedHashMap<>();
                            item.put("id", plan.getId());
                            item.put("contractId", plan.getContractId());
                            item.put("period", plan.getPeriod());
                            item.put("price", plan.getPrice());
                            item.put("returnTime", formatDate(plan.getReturnTime()));
                            item.put("overdueDays", daysSince(plan.getReturnTime()));
                            overdue.add(item);
                        }
                    }
                    facts.put("overduePlans", overdue);
                    return facts;
                });
    }

    /**
     * 待办事实包：与宿主「待办事项」页同一批 Service 方法、同一口径，
     * 保证徽标数字、待办列表、意图建议三处一致。
     */
    @Bean
    public IntentTool crmBacklogFactsTool(CrmCustomerService customerService,
            CrmClueService clueService, CrmContractService contractService,
            CrmReceivableService receivableService, CrmReceivablePlanService planService) {
        Map<String, Object> params = Schemas.object(Map.of(), List.of());
        return ofData("crm_backlog_facts",
                "读取当前用户 CRM 待办事实（今日需联系客户、待跟进客户/线索、合同待审核、"
                        + "合同即将到期、回款待审核、回款计划逾期、客户待进公海）——与待办事项页口径一致",
                params,
                (args, signal) -> {
                    Long userId = SecurityFrameworkUtils.getLoginUserId();
                    Map<String, Object> facts = new LinkedHashMap<>();
                    facts.put("now", formatDateTime(LocalDateTime.now()));
                    facts.put("todayContactCustomers", customerService.getTodayContactCustomerCount(userId));
                    facts.put("followCustomers", customerService.getFollowCustomerCount(userId));
                    facts.put("followClues", clueService.getFollowClueCount(userId));
                    facts.put("auditContracts", contractService.getAuditContractCount(userId));
                    facts.put("expiringContracts", contractService.getRemindContractCount(userId));
                    facts.put("auditReceivables", receivableService.getAuditReceivableCount(userId));
                    facts.put("overdueReceivablePlans", planService.getReceivablePlanRemindCount(userId));
                    facts.put("putPoolRemindCustomers", customerService.getPutPoolRemindCustomerCount(userId));
                    return facts;
                });
    }

    // ============================================================ helpers

    private static IntentTool ofData(String name, String description, Map<String, Object> parameters,
            BiFunction<Map<String, Object>, IntentToolContext, Map<String, Object>> executor) {
        return LambdaHostTool.ofData(name, description, parameters, executor);
    }

    private static Map<String, Object> toContactMap(CrmContactDO contact) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", contact.getId());
        map.put("name", contact.getName());
        map.put("post", contact.getPost());
        map.put("master", Boolean.TRUE.equals(contact.getMaster()) ? "关键人" : "普通联系人");
        map.put("customerId", contact.getCustomerId());
        map.put("mobile", contact.getMobile());
        map.put("telephone", contact.getTelephone());
        map.put("email", contact.getEmail());
        map.put("wechat", contact.getWechat());
        map.put("contactLastTime", formatDateTime(contact.getContactLastTime()));
        map.put("contactLastContent", contact.getContactLastContent());
        map.put("contactNextTime", formatDateTime(contact.getContactNextTime()));
        map.put("remark", contact.getRemark());
        return map;
    }

    private static Map<String, Object> toBusinessMap(CrmBusinessDO business, CrmBusinessStatusService statusService) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", business.getId());
        map.put("name", business.getName());
        map.put("customerId", business.getCustomerId());
        String statusName = null;
        try {
            CrmBusinessStatusDO status = business.getStatusId() == null ? null
                    : statusService.getBusinessStatus(business.getStatusId());
            statusName = status == null ? null : status.getName();
        } catch (Exception ignored) {
            // 阶段字典读不到不影响主流程
        }
        map.put("status", statusName);
        int endStatus = business.getEndStatus() == null ? -1 : business.getEndStatus();
        map.put("endStatus", switch (endStatus) {
            case 1 -> "赢单";
            case 2 -> "输单";
            case 3 -> "无效";
            default -> "进行中";
        });
        map.put("totalProductPrice", business.getTotalProductPrice());
        map.put("discountPercent", business.getDiscountPercent());
        map.put("totalPrice", business.getTotalPrice());
        map.put("dealTime", formatDateTime(business.getDealTime()));
        map.put("daysSinceUpdate", business.getUpdateTime() == null ? null
                : Duration.between(business.getUpdateTime(), LocalDateTime.now()).toDays());
        map.put("remark", business.getRemark());
        return map;
    }

    private static Map<String, Object> toCustomerMap(CrmCustomerDO customer) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", customer.getId());
        map.put("name", customer.getName());
        map.put("level", dictLabel(DictTypeConstants.CRM_CUSTOMER_LEVEL, customer.getLevel()));
        map.put("industry", dictLabel(DictTypeConstants.CRM_CUSTOMER_INDUSTRY, customer.getIndustryId()));
        map.put("source", dictLabel(DictTypeConstants.CRM_CUSTOMER_SOURCE, customer.getSource()));
        map.put("dealStatus", Boolean.TRUE.equals(customer.getDealStatus()) ? "已成交" : "未成交");
        map.put("contactLastTime", formatDateTime(customer.getContactLastTime()));
        map.put("contactLastContent", customer.getContactLastContent());
        map.put("contactNextTime", formatDateTime(customer.getContactNextTime()));
        map.put("remark", customer.getRemark());
        return map;
    }

    private static Map<String, Object> toProductMap(CrmProductDO product) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", product.getId());
        map.put("name", product.getName());
        map.put("no", product.getNo());
        map.put("unit", product.getUnit());
        map.put("price", product.getPrice());
        map.put("status", dictLabel(DictTypeConstants.CRM_PRODUCT_STATUS, product.getStatus()));
        map.put("categoryId", product.getCategoryId());
        map.put("description", product.getDescription());
        return map;
    }

    private static BigDecimal sum(List<BigDecimal> values) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            if (value != null) {
                total = total.add(value);
            }
        }
        return total;
    }

    private static boolean hasText(Map<String, Object> args, String key) {
        return args.get(key) != null && !String.valueOf(args.get(key)).isBlank();
    }

    private static int limit(Map<String, Object> args, int fallback) {
        Object value = args.get("limit");
        if (value == null) {
            return Math.min(MAX_LIMIT, fallback);
        }
        return Math.min(MAX_LIMIT, Math.max(1, asInt(value)));
    }

    private static int asInt(Object value) {
        return (int) Double.parseDouble(String.valueOf(value));
    }

    private static Long requireLong(Map<String, Object> args, String key) {
        Object value = args.get(key);
        if (value == null) {
            throw new IllegalArgumentException("缺少参数: " + key);
        }
        return value instanceof Number number ? number.longValue() : Long.parseLong(String.valueOf(value));
    }

    private static String dictLabel(String dictType, Object value) {
        if (value == null) {
            return null;
        }
        try {
            String label = DictFrameworkUtils.parseDictDataLabel(dictType, asInt(value));
            return label != null ? label : String.valueOf(value);
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }

    private static String formatDateTime(LocalDateTime time) {
        return time == null ? null : DATE_TIME_FORMATTER.format(time);
    }

    private static String formatDate(LocalDateTime time) {
        return time == null ? null : DATE_FORMATTER.format(time);
    }

    /**
     * 距到期天数：按自然日计算（今天到期 = 0，明天到期 = 1），
     * 与待办徽标「还有 N 天到期」口径一致，避免同一份合同出现两个数字。
     */
    private static long daysUntil(LocalDateTime time) {
        return ChronoUnit.DAYS.between(LocalDate.now(), time.toLocalDate());
    }

    /** 已逾期天数：同样按自然日计算（昨天到期 = 1）。 */
    private static long daysSince(LocalDateTime time) {
        return ChronoUnit.DAYS.between(time.toLocalDate(), LocalDate.now());
    }
}
