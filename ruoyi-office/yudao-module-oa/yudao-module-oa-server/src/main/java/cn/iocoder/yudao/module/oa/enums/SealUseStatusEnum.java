package cn.iocoder.yudao.module.oa.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用印状态枚举
 *
 * @author 宇擎源码
 */
@AllArgsConstructor
@Getter
public enum SealUseStatusEnum {

    PENDING(0, "待处理"),
    COMPLETED(1, "已完成"),
    BORROWED(2, "外借中"),
    RETURNED(3, "已归还"),
    OVERDUE(4, "已逾期");

    /**
     * 状态
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;

}
