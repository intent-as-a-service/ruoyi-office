package cn.iocoder.yudao.module.oa.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用车申请单还车状态枚举
 *
 * @author 宇擎源码
 */
@AllArgsConstructor
@Getter
public enum CarReturnStatusEnum {

    /**
     * 未生效
     */
    NOT_EFFECTIVE(0, "未生效"),
    
    /**
     * 待还车
     */
    PENDING_RETURN(1, "待还车"),
    
    /**
     * 还车中
     */
    RETURNING(2, "还车中"),
    
    /**
     * 已还车
     */
    RETURNED(3, "已还车");

    /**
     * 状态值
     */
    private final Integer status;
    
    /**
     * 状态名称
     */
    private final String name;

    /**
     * 根据状态值获取枚举
     *
     * @param status 状态值
     * @return 枚举
     */
    public static CarReturnStatusEnum getByStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (CarReturnStatusEnum value : values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return null;
    }
}
