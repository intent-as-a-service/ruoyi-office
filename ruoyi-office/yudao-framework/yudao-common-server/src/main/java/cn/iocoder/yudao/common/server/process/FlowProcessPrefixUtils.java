package cn.iocoder.yudao.common.server.process;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.SystemEnum;

/**
 * 流程定义 Key 前缀工具类
 *
 * <p>统一使用 {@link SystemEnum#getCode()} 的前两位（大小写不敏感）作为流程前缀，
 * 通过前缀判断流程是否属于当前业务模块。</p>
 *
 * @author 宇擎源码
 */
public final class FlowProcessPrefixUtils {

    private FlowProcessPrefixUtils() {
    }

    /**
     * 构建流程前缀，格式为 {@code systemCode.toLowerCase() + "_"}
     *
     * @param systemEnum 模块系统枚举
     * @return 规范化前缀
     */
    public static String buildPrefix(SystemEnum systemEnum) {
        return systemEnum.getCode().toLowerCase() + "_";
    }

    /**
     * 判断流程定义 Key 是否属于指定模块
     *
     * @param systemEnum            模块系统枚举
     * @param processDefinitionKey  流程定义 Key
     * @return true 表示属于当前模块
     */
    public static boolean match(SystemEnum systemEnum, String processDefinitionKey) {
        if (systemEnum == null || StrUtil.isBlank(processDefinitionKey)) {
            return false;
        }
        String normalizeKey = processDefinitionKey.toLowerCase();
        return normalizeKey.startsWith(buildPrefix(systemEnum));
    }
}

