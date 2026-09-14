package cn.iocoder.yudao.framework.common.util.bill;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.iocoder.yudao.framework.common.enums.BillTypeEnum;
import cn.iocoder.yudao.framework.common.enums.SystemEnum;
import cn.iocoder.yudao.framework.common.util.spring.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 单据编号生成工具类
 * 生成格式：系统代码+前缀+"-"+日期+流水号
 * 示例：OA001-2025061400001
 *
 * @author 宇擎源码
 */
@Component
public class BillCodeUtils {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String REDIS_KEY_PREFIX = "bill_code";
    private static final String SEPARATOR = "-";
    private static final int SEQUENCE_LENGTH = 5; // 流水号长度

    /**
     * 生成单据编号（实例方法）
     * 格式：系统代码+前缀+"-"+日期+流水号
     *
     * @param sysCode 系统代码 (如: OA, CRM, ERP)
     * @param billTypeCode 单据类型编号 (如: 001, 002, 003)
     * @return 单据编号 (如: OA001-2025061400001)
     */
    public String generate(String sysCode, String billTypeCode) {
        // 获取当前日期，格式：yyyyMMdd
        String dateStr = DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATE_PATTERN);

        // 构建Redis key: bill_code:系统代码:前缀:日期
        String redisKey = REDIS_KEY_PREFIX + ":" + sysCode + ":" + billTypeCode + ":" + dateStr;

        // 递增序号
        Long sequenceNo = stringRedisTemplate.opsForValue().increment(redisKey);

        // 设置过期时间为2天，确保跨日期不会冲突
        stringRedisTemplate.expire(redisKey, Duration.ofDays(2L));

        // 构建单据编号：系统代码+前缀+"-"+日期+流水号
        return sysCode + billTypeCode + SEPARATOR + dateStr + String.format("%0" + SEQUENCE_LENGTH + "d", sequenceNo);
    }

    /**
     * 生成单据编号（静态方法，便捷入口）
     * 格式：系统代码+前缀+"-"+日期+流水号
     *
     * @param sysEnum 系统枚举
     * @param billTypeEnum 单据类型枚举
     * @return 单据编号 (如: OA001-2025061400001)
     */
    public static String generateBillCode(SystemEnum sysEnum, BillTypeEnum billTypeEnum) {
        return SpringUtils.getBean(BillCodeUtils.class).generate(sysEnum.getCode(), billTypeEnum.getTypeCode());
    }

}
