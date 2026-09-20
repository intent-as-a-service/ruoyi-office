package cn.iocoder.yudao.module.crm.intent;

import cn.iocoder.yudao.framework.dict.core.DictFrameworkUtils;
import cn.iocoder.yudao.module.crm.controller.admin.business.vo.business.CrmBusinessPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.clue.vo.CrmCluePageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.contract.vo.contract.CrmContractPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.receivable.vo.plan.CrmReceivablePlanPageReqVO;
import cn.iocoder.yudao.module.crm.dal.dataobject.business.CrmBusinessDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.clue.CrmClueDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.contract.CrmContractDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.customer.CrmCustomerDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.receivable.CrmReceivablePlanDO;
import cn.iocoder.yudao.module.crm.enums.DictTypeConstants;
import cn.iocoder.yudao.module.crm.enums.common.CrmAuditStatusEnum;
import cn.iocoder.yudao.module.crm.enums.common.CrmSceneTypeEnum;
import cn.iocoder.yudao.module.crm.service.business.CrmBusinessService;
import cn.iocoder.yudao.module.crm.service.clue.CrmClueService;
import cn.iocoder.yudao.module.crm.service.contract.CrmContractService;
import cn.iocoder.yudao.module.crm.service.customer.CrmCustomerService;
import cn.iocoder.yudao.module.crm.service.receivable.CrmReceivablePlanService;
import dev.intent.sdk.catalog.IntentCatalogContext;
import dev.intent.sdk.host.rule.IntentFactProvider;
import dev.intent.sdk.host.rule.DatasetSpec;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CRM 事实源：把 CRM 的待办事实喂给声明式规则引擎。
 *
 * <p>每个数据集的口径都与「待办事项」页完全一致（同一个宿主 Service 查询），
 * 因此面板上的数字与待办页徽标天然对得上，且随 CRM 配置（提醒开关、提前天数、公海规则）
 * 自动联动——不需要在这里重复实现业务口径。</p>
 *
 * <p>本类<b>只负责取数</b>，"什么时候提示、提示什么、点了跳到哪个意图"全部写在
 * {@code src/main/resources/intent-rules/*.yaml} 里——调整文案或挂载页面改配置即可，
 * 不用改代码、不用发版。规则文件名前缀（10/20/30…）即待办分组的展示优先级。</p>
 */
@Slf4j
@Component
public class CrmIntentFactProvider implements IntentFactProvider {

    /** 数据集：我负责的、即将到期的合同。 */
    public static final String DATASET_EXPIRING_CONTRACTS = "crm.expiring-contracts";
    /** 数据集：分配给我的、未跟进的客户。 */
    public static final String DATASET_FOLLOW_CUSTOMERS = "crm.follow-customers";
    /** 数据集：今天需要联系的客户。 */
    public static final String DATASET_TODAY_CONTACT_CUSTOMERS = "crm.today-contact-customers";
    /** 数据集：分配给我的、未跟进且未转化的线索。 */
    public static final String DATASET_FOLLOW_CLUES = "crm.follow-clues";
    /** 数据集：我负责的在手商机中停滞超过 30 天的。 */
    public static final String DATASET_STAGNANT_BUSINESSES = "crm.stagnant-businesses";
    /** 数据集：等待我审批的合同。 */
    public static final String DATASET_AUDIT_CONTRACTS = "crm.audit-contracts";
    /** 数据集：未回款且已逾期、已到提醒时间的回款计划。 */
    public static final String DATASET_OVERDUE_RECEIVABLE_PLANS = "crm.overdue-receivable-plans";

    /** 商机停滞阈值（天）。 */
    private static final int STAGNANT_DAYS = 30;

    /** 单次取数硬上限（防止规则误配把整库拉出来）。 */
    private static final int MAX_ROWS = 50;
    /** 单次查询的页大小：比 MAX_ROWS 大，过滤后仍能凑够条目配额。 */
    private static final int QUERY_PAGE_SIZE = 100;

    @Resource
    private CrmContractService contractService;
    @Resource
    private CrmCustomerService customerService;
    @Resource
    private CrmClueService clueService;
    @Resource
    private CrmBusinessService businessService;
    @Resource
    private CrmReceivablePlanService receivablePlanService;

    /**
     * 数据集自描述：把"能用哪些数据、每行有哪些列"一并声明出来。
     *
     * <p>「AI 生成自动跟进规则」要靠它把自然语言落到真实列上——列名不声明，
     * 生成出来的条件就会写在不存在的字段上，规则语法合法但永远不命中。</p>
     */
    @Override
    public List<DatasetSpec> datasets() {
        return List.of(
                DatasetSpec.of(DATASET_EXPIRING_CONTRACTS, "我负责的、即将到期（含已过期）的合同",
                        Map.of("id", "合同编号", "name", "合同名称", "no", "合同编号（业务单号）",
                                "endTime", "合同结束时间", "customerId", "客户编号")),
                DatasetSpec.of(DATASET_FOLLOW_CUSTOMERS, "分配给我、且到了跟进时间仍未跟进的客户",
                        Map.of("id", "客户编号", "name", "客户名称", "level", "客户等级",
                                "contactLastTime", "最后联系时间", "contactNextTime", "计划下次联系时间",
                                "daysSinceLastContact", "距上次联系天数")),
                DatasetSpec.of(DATASET_TODAY_CONTACT_CUSTOMERS, "今天需要联系的客户",
                        Map.of("id", "客户编号", "name", "客户名称", "level", "客户等级",
                                "contactNextTime", "计划下次联系时间", "daysSinceLastContact", "距上次联系天数")),
                DatasetSpec.of(DATASET_FOLLOW_CLUES, "分配给我、未跟进且未转化的线索",
                        Map.of("id", "线索编号", "name", "线索名称", "level", "线索等级",
                                "source", "线索来源", "contactLastTime", "最后联系时间",
                                "daysSinceLastContact", "距上次联系天数")),
                DatasetSpec.of(DATASET_STAGNANT_BUSINESSES, "我负责的、超过 30 天没有更新的在手商机",
                        Map.of("id", "商机编号", "name", "商机名称", "customerId", "客户编号",
                                "totalPrice", "商机金额", "daysSinceUpdate", "距上次更新天数",
                                "updateTime", "最后更新时间")),
                DatasetSpec.of(DATASET_AUDIT_CONTRACTS, "等待我审批的合同",
                        Map.of("id", "合同编号", "name", "合同名称", "no", "合同编号（业务单号）",
                                "price", "合同金额", "customerId", "客户编号", "endTime", "合同结束时间")),
                DatasetSpec.of(DATASET_OVERDUE_RECEIVABLE_PLANS, "未回款、已逾期且到了提醒时间的回款计划",
                        Map.of("id", "回款计划编号", "period", "期数", "price", "应收金额",
                                "returnTime", "约定回款时间", "overdueDays", "已逾期天数",
                                "customerId", "客户编号", "customerName", "客户名称",
                                "contractId", "合同编号")));
    }

    @Override
    public boolean supports(String datasetId) {
        return switch (datasetId == null ? "" : datasetId) {
            case DATASET_EXPIRING_CONTRACTS, DATASET_FOLLOW_CUSTOMERS, DATASET_TODAY_CONTACT_CUSTOMERS,
                 DATASET_FOLLOW_CLUES, DATASET_STAGNANT_BUSINESSES, DATASET_AUDIT_CONTRACTS,
                 DATASET_OVERDUE_RECEIVABLE_PLANS -> true;
            default -> false;
        };
    }

    @Override
    public List<Map<String, Object>> rows(String datasetId, IntentCatalogContext context, int limit) {
        Long userId = userIdOf(context);
        if (userId == null) {
            return List.of();
        }
        int size = limit <= 0 ? MAX_ROWS : Math.min(limit, MAX_ROWS);
        try {
            return switch (datasetId == null ? "" : datasetId) {
                case DATASET_EXPIRING_CONTRACTS -> expiringContractRows(userId, size);
                case DATASET_FOLLOW_CUSTOMERS -> followCustomerRows(userId, size);
                case DATASET_TODAY_CONTACT_CUSTOMERS -> todayContactCustomerRows(userId, size);
                case DATASET_FOLLOW_CLUES -> followClueRows(userId, size);
                case DATASET_STAGNANT_BUSINESSES -> stagnantBusinessRows(userId, size);
                case DATASET_AUDIT_CONTRACTS -> auditContractRows(userId, size);
                case DATASET_OVERDUE_RECEIVABLE_PLANS -> overdueReceivablePlanRows(userId, size);
                default -> List.of();
            };
        } catch (Exception e) {
            log.warn("[intent] 事实取数失败（降级为无建议）: dataset={} msg={}", datasetId, e.getMessage());
            return List.of();
        }
    }

    @Override
    public int count(String datasetId, IntentCatalogContext context) {
        Long userId = userIdOf(context);
        if (userId == null) {
            return 0;
        }
        try {
            Long count = switch (datasetId == null ? "" : datasetId) {
                case DATASET_EXPIRING_CONTRACTS -> contractService.getRemindContractCount(userId);
                case DATASET_FOLLOW_CUSTOMERS -> customerService.getFollowCustomerCount(userId);
                case DATASET_TODAY_CONTACT_CUSTOMERS -> customerService.getTodayContactCustomerCount(userId);
                case DATASET_FOLLOW_CLUES -> clueService.getFollowClueCount(userId);
                case DATASET_STAGNANT_BUSINESSES -> (long) stagnantBusinessRows(userId, MAX_ROWS).size();
                case DATASET_AUDIT_CONTRACTS -> contractService.getAuditContractCount(userId);
                case DATASET_OVERDUE_RECEIVABLE_PLANS -> receivablePlanService.getReceivablePlanRemindCount(userId);
                default -> 0L;
            };
            return count == null ? 0 : count.intValue();
        } catch (Exception e) {
            log.warn("[intent] 事实计数失败（降级为无提示）: dataset={} msg={}", datasetId, e.getMessage());
            return 0;
        }
    }

    // ============================================================ 各数据集取数

    /** 我负责的、即将到期的合同（口径同「待办事项 → 即将到期的合同」）。 */
    private List<Map<String, Object>> expiringContractRows(Long userId, int size) {
        CrmContractPageReqVO reqVO = new CrmContractPageReqVO();
        reqVO.setExpiryType(CrmContractPageReqVO.EXPIRY_TYPE_ABOUT_TO_EXPIRE);
        reqVO.setSceneType(CrmSceneTypeEnum.OWNER.getType());
        reqVO.setPageNo(1);
        reqVO.setPageSize(size);
        List<CrmContractDO> list = contractService.getContractPage(reqVO, userId).getList();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (CrmContractDO contract : safe(list)) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", contract.getId());
            row.put("name", contract.getName());
            row.put("no", contract.getNo());
            row.put("endTime", contract.getEndTime());
            row.put("customerId", contract.getCustomerId());
            rows.add(row);
        }
        return rows;
    }

    /** 分配给我的、未跟进的客户（口径同「待办事项 → 分配给我的客户」）。 */
    private List<Map<String, Object>> followCustomerRows(Long userId, int size) {
        CrmCustomerPageReqVO reqVO = new CrmCustomerPageReqVO();
        reqVO.setFollowUpStatus(false);
        reqVO.setSceneType(CrmSceneTypeEnum.OWNER.getType());
        reqVO.setPageNo(1);
        reqVO.setPageSize(size);
        List<CrmCustomerDO> list = customerService.getCustomerPage(reqVO, userId).getList();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (CrmCustomerDO customer : safe(list)) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", customer.getId());
            row.put("name", customer.getName());
            row.put("level", dictLabel(DictTypeConstants.CRM_CUSTOMER_LEVEL, customer.getLevel()));
            row.put("contactLastTime", customer.getContactLastTime());
            row.put("contactNextTime", customer.getContactNextTime());
            row.put("daysSinceLastContact", daysBetween(customer.getContactLastTime()));
            rows.add(row);
        }
        return rows;
    }

    /** 今天需要联系的客户（口径同「待办事项 → 今日需联系客户」）。 */
    private List<Map<String, Object>> todayContactCustomerRows(Long userId, int size) {
        CrmCustomerPageReqVO reqVO = new CrmCustomerPageReqVO();
        reqVO.setContactStatus(CrmCustomerPageReqVO.CONTACT_TODAY);
        reqVO.setSceneType(CrmSceneTypeEnum.OWNER.getType());
        reqVO.setPageNo(1);
        reqVO.setPageSize(size);
        List<CrmCustomerDO> list = customerService.getCustomerPage(reqVO, userId).getList();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (CrmCustomerDO customer : safe(list)) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", customer.getId());
            row.put("name", customer.getName());
            row.put("level", dictLabel(DictTypeConstants.CRM_CUSTOMER_LEVEL, customer.getLevel()));
            row.put("contactNextTime", customer.getContactNextTime());
            row.put("daysSinceLastContact", daysBetween(customer.getContactLastTime()));
            rows.add(row);
        }
        return rows;
    }

    /** 分配给我的、未跟进且未转化的线索（口径同「待办事项 → 分配给我的线索」）。 */
    private List<Map<String, Object>> followClueRows(Long userId, int size) {
        CrmCluePageReqVO reqVO = new CrmCluePageReqVO();
        reqVO.setFollowUpStatus(false);
        reqVO.setTransformStatus(false);
        reqVO.setSceneType(CrmSceneTypeEnum.OWNER.getType());
        reqVO.setPageNo(1);
        reqVO.setPageSize(size);
        List<CrmClueDO> list = clueService.getCluePage(reqVO, userId).getList();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (CrmClueDO clue : safe(list)) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", clue.getId());
            row.put("name", clue.getName());
            row.put("level", dictLabel(DictTypeConstants.CRM_CUSTOMER_LEVEL, clue.getLevel()));
            row.put("source", dictLabel(DictTypeConstants.CRM_CUSTOMER_SOURCE, clue.getSource()));
            row.put("contactLastTime", clue.getContactLastTime());
            row.put("daysSinceLastContact", daysBetween(clue.getContactLastTime()));
            rows.add(row);
        }
        return rows;
    }

    /** 我负责的在手商机里，距上次更新超过 {@link #STAGNANT_DAYS} 天的。 */
    private List<Map<String, Object>> stagnantBusinessRows(Long userId, int size) {
        CrmBusinessPageReqVO reqVO = new CrmBusinessPageReqVO();
        reqVO.setSceneType(CrmSceneTypeEnum.OWNER.getType());
        reqVO.setPageNo(1);
        reqVO.setPageSize(QUERY_PAGE_SIZE);
        List<CrmBusinessDO> list = businessService.getBusinessPage(reqVO, userId).getList();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (CrmBusinessDO business : safe(list)) {
            if (business.getEndStatus() != null) {
                continue; // 已赢单/输单/无效的不算停滞
            }
            long days = daysBetween(business.getUpdateTime());
            if (days < STAGNANT_DAYS) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", business.getId());
            row.put("name", business.getName());
            row.put("customerId", business.getCustomerId());
            row.put("totalPrice", business.getTotalPrice());
            row.put("daysSinceUpdate", days);
            row.put("updateTime", business.getUpdateTime());
            rows.add(row);
            if (rows.size() >= size) {
                break;
            }
        }
        return rows;
    }

    /** 等待我审批的合同（口径同「待办事项 → 待审核合同」）。 */
    private List<Map<String, Object>> auditContractRows(Long userId, int size) {
        CrmContractPageReqVO reqVO = new CrmContractPageReqVO();
        reqVO.setAuditStatus(CrmAuditStatusEnum.PROCESS.getStatus());
        reqVO.setSceneType(CrmSceneTypeEnum.OWNER.getType());
        reqVO.setPageNo(1);
        reqVO.setPageSize(size);
        List<CrmContractDO> list = contractService.getContractPage(reqVO, userId).getList();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (CrmContractDO contract : safe(list)) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", contract.getId());
            row.put("name", contract.getName());
            row.put("no", contract.getNo());
            row.put("price", contract.getTotalPrice());
            row.put("customerId", contract.getCustomerId());
            row.put("endTime", contract.getEndTime());
            rows.add(row);
        }
        return rows;
    }

    /** 未回款、已逾期且已到提醒时间的回款计划（口径同「待办事项 → 待回款提醒」）。 */
    private List<Map<String, Object>> overdueReceivablePlanRows(Long userId, int size) {
        CrmReceivablePlanPageReqVO reqVO = new CrmReceivablePlanPageReqVO();
        reqVO.setRemindType(CrmReceivablePlanPageReqVO.REMIND_TYPE_EXPIRED);
        reqVO.setSceneType(CrmSceneTypeEnum.OWNER.getType());
        reqVO.setPageNo(1);
        reqVO.setPageSize(QUERY_PAGE_SIZE);
        LocalDateTime beginOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
        List<CrmReceivablePlanDO> list = receivablePlanService.getReceivablePlanPage(reqVO, userId).getList();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (CrmReceivablePlanDO plan : safe(list)) {
            if (plan.getRemindTime() != null && !plan.getRemindTime().isBefore(beginOfToday)) {
                continue; // 与待办口径一致：尚未到提醒时间的不提示
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", plan.getId());
            row.put("period", plan.getPeriod());
            row.put("price", plan.getPrice());
            row.put("returnTime", plan.getReturnTime());
            row.put("overdueDays", calendarDaysSince(plan.getReturnTime()));
            row.put("customerId", plan.getCustomerId());
            row.put("contractId", plan.getContractId());
            rows.add(row);
            if (rows.size() >= size) {
                break;
            }
        }
        fillCustomerNames(rows);
        return rows;
    }

    /**
     * 给一批行补上 {@code customerName}（一次批量查询，不做 N+1）。
     *
     * <p><b>为什么必须有这一列</b>：事实规则只能引用数据集声明过的列。这份数据以前只有
     * {@code customerId}，于是标题模板想写「客户「X」第 N 期回款已逾期 M 天」也写不出来，
     * 用户看到的就只有「第 1 期回款已逾期 23 天」——<b>三条并排摆着，一个客户名都没有，
     * 不知道该打给谁</b>。同一个系统里别的规则（如 {@code crm.customer.follow}）是带对象名的，
     * 只有这条不带，属于口径不一致。</p>
     *
     * <p>取不到名字时留 null，模板侧会退化成不带客户名的写法——不编造，也不阻塞。</p>
     */
    private void fillCustomerNames(List<Map<String, Object>> rows) {
        List<Long> ids = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Long id = asLong(row.get("customerId"));
            if (id != null && !ids.contains(id)) {
                ids.add(id);
            }
        }
        if (ids.isEmpty()) {
            return;
        }
        Map<Long, String> names = new LinkedHashMap<>();
        for (CrmCustomerDO customer : safe(customerService.getCustomerList(ids))) {
            names.put(customer.getId(), customer.getName());
        }
        for (Map<String, Object> row : rows) {
            Long id = asLong(row.get("customerId"));
            if (id != null) {
                row.put("customerName", names.get(id));
            }
        }
    }

    private static Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return Long.valueOf(String.valueOf(value).trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ============================================================ helpers

    private static Long userIdOf(IntentCatalogContext context) {
        if (context == null || context.userId() == null || context.userId().isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(context.userId());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static long daysBetween(LocalDateTime time) {
        return time == null ? 0 : Math.max(0, Duration.between(time, LocalDateTime.now()).toDays());
    }

    /** 已逾期天数按自然日计算（昨天到期 = 1），与待办页与查询工具口径一致。 */
    private static long calendarDaysSince(LocalDateTime time) {
        return time == null ? 0 : Math.max(0,
                java.time.temporal.ChronoUnit.DAYS.between(time.toLocalDate(), java.time.LocalDate.now()));
    }

    private static String dictLabel(String dictType, Object value) {
        if (value == null) {
            return null;
        }
        try {
            String label = DictFrameworkUtils.parseDictDataLabel(dictType, Integer.valueOf(String.valueOf(value)));
            return label != null ? label : String.valueOf(value);
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }

    private static <T> Collection<T> safe(List<T> list) {
        return list == null ? List.of() : list;
    }
}
