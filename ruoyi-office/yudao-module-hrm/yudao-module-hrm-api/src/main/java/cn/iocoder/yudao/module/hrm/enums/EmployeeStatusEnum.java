package cn.iocoder.yudao.module.hrm.enums;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 员工状态枚举
 *
 * @author 宇擎源码
 */
@Getter
@AllArgsConstructor
public enum EmployeeStatusEnum implements ArrayValuable<Integer> {

    /**
     * 正式
     */
    FORMAL(1, "正式"),

    /**
     * 试用期
     */
    PROBATIONARY(2, "试用期"),

    /**
     * 实习生
     */
    INTERN(3, "实习生"),

    /**
     * 临时工
     */
    TEMPORARY(5, "临时工"),

    /**
     * 离职
     */
    RESIGNED(6, "离职"),

    /**
     * 退休
     */
    RETIRED(7, "退休");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(EmployeeStatusEnum::getStatus).toArray(Integer[]::new);

    /**
     * 状态值
     */
    private final Integer status;

    /**
     * 状态名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    /**
     * 判断是否为正式状态
     *
     * @param status 状态值
     * @return 是否为正式状态
     */
    public static boolean isFormal(Integer status) {
        return ObjUtil.equal(FORMAL.status, status);
    }

    /**
     * 判断是否为试用期状态
     *
     * @param status 状态值
     * @return 是否为试用期状态
     */
    public static boolean isProbationary(Integer status) {
        return ObjUtil.equal(PROBATIONARY.status, status);
    }

    /**
     * 判断是否为实习生状态
     *
     * @param status 状态值
     * @return 是否为实习生状态
     */
    public static boolean isIntern(Integer status) {
        return ObjUtil.equal(INTERN.status, status);
    }

    /**
     * 判断是否为临时工状态
     *
     * @param status 状态值
     * @return 是否为临时工状态
     */
    public static boolean isTemporary(Integer status) {
        return ObjUtil.equal(TEMPORARY.status, status);
    }

    /**
     * 判断是否为离职状态
     *
     * @param status 状态值
     * @return 是否为离职状态
     */
    public static boolean isResigned(Integer status) {
        return ObjUtil.equal(RESIGNED.status, status);
    }

    /**
     * 判断是否为退休状态
     *
     * @param status 状态值
     * @return 是否为退休状态
     */
    public static boolean isRetired(Integer status) {
        return ObjUtil.equal(RETIRED.status, status);
    }

    /**
     * 根据状态值获取枚举
     *
     * @param status 状态值
     * @return 枚举对象，如果不存在则返回 null
     */
    public static EmployeeStatusEnum valueOf(Integer status) {
        return Arrays.stream(values())
                .filter(item -> ObjUtil.equal(item.status, status))
                .findFirst()
                .orElse(null);
    }

}

