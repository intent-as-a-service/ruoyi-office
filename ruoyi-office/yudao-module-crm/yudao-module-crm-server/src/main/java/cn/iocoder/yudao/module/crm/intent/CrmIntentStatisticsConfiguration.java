package cn.iocoder.yudao.module.crm.intent;

import cn.iocoder.yudao.framework.common.enums.DateIntervalEnum;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.crm.controller.admin.statistics.vo.funnel.CrmStatisticsFunnelReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.statistics.vo.performance.CrmStatisticsPerformanceReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.statistics.vo.portrait.CrmStatisticsPortraitReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.statistics.vo.rank.CrmStatisticsRankReqVO;
import cn.iocoder.yudao.module.crm.service.statistics.CrmStatisticsFunnelService;
import cn.iocoder.yudao.module.crm.service.statistics.CrmStatisticsPerformanceService;
import cn.iocoder.yudao.module.crm.service.statistics.CrmStatisticsPortraitService;
import cn.iocoder.yudao.module.crm.service.statistics.CrmStatisticsRankService;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.intent.sdk.tool.IntentTool;
import dev.intent.sdk.tool.IntentToolContext;
import dev.intent.sdk.tool.LambdaHostTool;
import dev.intent.sdk.tool.Schemas;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * CRM 意图统计工具：把宿主既有的统计接口（销售漏斗 / 业绩 / 排行榜 / 客户画像）
 * 变成 AI 可调用的工具。
 *
 * <p>这些接口本身就是商业智能页面在用的，口径不会和页面打架；
 * 工具只负责"取数 + 兜默认参数"，解读与建议交给模型。</p>
 *
 * <p>注意：统计接口按部门（deptId）聚合，这里默认取当前登录用户的部门，
 * 与页面默认口径一致。</p>
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class CrmIntentStatisticsConfiguration {

    private static final ObjectMapper JSON = new ObjectMapper();
    private static final int DEFAULT_DAYS = 90;
    private static final Integer DEFAULT_INTERVAL = DateIntervalEnum.MONTH.getInterval();

    @Bean
    public IntentTool crmStatsFunnelTool(CrmStatisticsFunnelService funnelService) {
        Map<String, Object> params = Schemas.object(
                Map.of("days", Schemas.number("统计最近多少天，默认 90"),
                        "interval", Schemas.enumeration(List.of("1", "2", "3", "4", "5"),
                                "时间粒度：1=天 2=周 3=月 4=季度 5=年，默认 3（月）")),
                List.of());
        return ofData("crm_stats_funnel",
                "读取销售漏斗统计（商机总数、赢单数、客户数）与商机结束状态分布——"
                        + "用于诊断漏斗卡点、预测成交",
                params,
                (args, signal) -> {
                    CrmStatisticsFunnelReqVO reqVO = new CrmStatisticsFunnelReqVO();
                    fill(reqVO, args);
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("range", describe(args));
                    result.put("summary", toMap(funnelService.getFunnelSummary(reqVO)));
                    result.put("byEndStatus", toList(funnelService.getBusinessSummaryByEndStatus(reqVO)));
                    result.put("byDate", toList(funnelService.getBusinessSummaryByDate(reqVO)));
                    return result;
                });
    }

    @Bean
    public IntentTool crmStatsPerformanceTool(CrmStatisticsPerformanceService performanceService) {
        Map<String, Object> params = Schemas.object(
                Map.of("days", Schemas.number("统计最近多少天，默认 90；填 0 表示本年度")),
                List.of());
        return ofData("crm_stats_performance",
                "读取员工业绩统计（合同数量、合同金额、回款金额，含环比与同比口径）——"
                        + "用于业绩达成预测与差距分析",
                params,
                (args, signal) -> {
                    CrmStatisticsPerformanceReqVO reqVO = new CrmStatisticsPerformanceReqVO();
                    reqVO.setDeptId(deptId());
                    reqVO.setTimes(range(args));
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("range", describe(args));
                    result.put("contractCount", toList(performanceService.getContractCountPerformance(reqVO)));
                    result.put("contractPrice", toList(performanceService.getContractPricePerformance(reqVO)));
                    result.put("receivablePrice", toList(performanceService.getReceivablePricePerformance(reqVO)));
                    return result;
                });
    }

    @Bean
    public IntentTool crmStatsRankTool(CrmStatisticsRankService rankService) {
        Map<String, Object> params = Schemas.object(
                Map.of("dimension", Schemas.enumeration(
                                List.of("contractPrice", "receivablePrice", "contractCount", "productSales",
                                        "customerCount", "followCount"),
                                "排行维度：contractPrice=合同金额 receivablePrice=回款金额 "
                                        + "contractCount=签约数量 productSales=产品销量 "
                                        + "customerCount=客户数 followCount=跟进次数"),
                        "days", Schemas.number("统计最近多少天，默认 90；填 0 表示本年度")),
                List.of("dimension"));
        return ofData("crm_stats_rank",
                "读取排行榜数据（按合同金额/回款金额/签约数量/产品销量/客户数/跟进次数）——"
                        + "用于团队对比、辅导建议与资源分配",
                params,
                (args, signal) -> {
                    CrmStatisticsRankReqVO reqVO = new CrmStatisticsRankReqVO();
                    reqVO.setDeptId(deptId());
                    reqVO.setTimes(range(args));
                    String dimension = String.valueOf(args.get("dimension"));
                    List<?> rows = switch (dimension) {
                        case "receivablePrice" -> rankService.getReceivablePriceRank(reqVO);
                        case "contractCount" -> rankService.getContractCountRank(reqVO);
                        case "productSales" -> rankService.getProductSalesRank(reqVO);
                        case "customerCount" -> rankService.getCustomerCountRank(reqVO);
                        case "followCount" -> rankService.getFollowCountRank(reqVO);
                        default -> rankService.getContractPriceRank(reqVO);
                    };
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("dimension", dimension);
                    result.put("range", describe(args));
                    result.put("rows", toList(rows));
                    return result;
                });
    }

    @Bean
    public IntentTool crmStatsPortraitTool(CrmStatisticsPortraitService portraitService) {
        Map<String, Object> params = Schemas.object(
                Map.of("days", Schemas.number("统计最近多少天，默认 90；填 0 表示本年度")),
                List.of());
        return ofData("crm_stats_customer_structure",
                "读取客户结构统计：按等级、行业、来源、地区分布，含各维度成交数量——"
                        + "用于市场投放建议与客户结构分析",
                params,
                (args, signal) -> {
                    CrmStatisticsPortraitReqVO reqVO = new CrmStatisticsPortraitReqVO();
                    reqVO.setDeptId(deptId());
                    reqVO.setTimes(range(args));
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("range", describe(args));
                    result.put("byLevel", toList(portraitService.getCustomerSummaryByLevel(reqVO)));
                    result.put("byIndustry", toList(portraitService.getCustomerSummaryByIndustry(reqVO)));
                    result.put("bySource", toList(portraitService.getCustomerSummaryBySource(reqVO)));
                    result.put("byArea", toList(portraitService.getCustomerSummaryByArea(reqVO)));
                    return result;
                });
    }

    // ============================================================ helpers

    private static void fill(CrmStatisticsFunnelReqVO reqVO, Map<String, Object> args) {
        reqVO.setDeptId(deptId());
        reqVO.setTimes(range(args));
        Object interval = args.get("interval");
        reqVO.setInterval(interval == null ? DEFAULT_INTERVAL : (int) Double.parseDouble(String.valueOf(interval)));
    }

    private static Long deptId() {
        Long deptId = SecurityFrameworkUtils.getLoginUserDeptId();
        return deptId == null ? 100L : deptId;
    }

    /** days=0 表示本年度 1 月 1 日至今；缺省为最近 90 天。 */
    private static LocalDateTime[] range(Map<String, Object> args) {
        Object value = args.get("days");
        int days = value == null ? DEFAULT_DAYS : (int) Double.parseDouble(String.valueOf(value));
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime begin = days <= 0
                ? LocalDateTime.of(end.getYear(), 1, 1, 0, 0)
                : end.minusDays(days);
        return new LocalDateTime[]{begin, end};
    }

    private static String describe(Map<String, Object> args) {
        LocalDateTime[] range = range(args);
        return range[0].toLocalDate() + " ~ " + range[1].toLocalDate();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> toMap(Object vo) {
        if (vo == null) {
            return Map.of();
        }
        try {
            return JSON.convertValue(vo, Map.class);
        } catch (Exception e) {
            log.warn("[intent] 统计结果转换失败: {}", e.getMessage());
            return Map.of("raw", String.valueOf(vo));
        }
    }

    private static List<Map<String, Object>> toList(List<?> rows) {
        if (rows == null || rows.isEmpty()) {
            return List.of();
        }
        return rows.stream().map(CrmIntentStatisticsConfiguration::toMap).toList();
    }

    private static IntentTool ofData(String name, String description, Map<String, Object> parameters,
            BiFunction<Map<String, Object>, IntentToolContext, Map<String, Object>> executor) {
        return LambdaHostTool.ofData(name, description, parameters, executor);
    }
}
