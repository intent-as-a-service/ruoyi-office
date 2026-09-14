package cn.iocoder.yudao.module.system.enums;

public enum OrgTypeEnum {
    COMPANY("1"),
    DEPARTMENT("0");

    private final String value;

    OrgTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
