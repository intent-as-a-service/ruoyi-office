package cn.iocoder.yudao.framework.common.enums;

/**
 * 系统枚举
 *
 * @author 宇擎源码
 */
public enum SystemEnum {
    /**
     * 系统
     */
    SYSTEM("SY", "系统管理"),
    /**
     * 基础设施
     */
    INFRA("IN", "基础设施"),

    /**
     * OA系统
     */
    OA("OA", "OA系统"),

    /**
     * 人力资源系统
     */
    HRM("HR", "人力资源系统");

    /**
     * 系统代码
     */
    private final String code;
    /**
     * 系统名称
     */
    private final String name;

    /**
     * 构造方法
     *
     * @param code 系统代码
     * @param name 系统名称
     */
    SystemEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 获取系统代码
     *
     * @return 系统代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取系统名称
     *
     * @return 系统名称
     */
    public String getName() {
        return name;
    }
}
