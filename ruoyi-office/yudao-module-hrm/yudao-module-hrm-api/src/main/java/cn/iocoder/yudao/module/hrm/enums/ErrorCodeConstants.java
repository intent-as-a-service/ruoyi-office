package cn.iocoder.yudao.module.hrm.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * HRM 错误码枚举类
 * <p>
 * HRM 系统，使用 1-050-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 员工档案 1-050-001-000 ==========
    ErrorCode EMPLOYEE_ARCHIVE_NOT_EXISTS = new ErrorCode(1_050_001_001, "员工档案不存在");

    // ========== 员工入职申请单 1-050-002-000 ==========
    ErrorCode EMPLOYEE_ENTRY_BILL_NOT_EXISTS = new ErrorCode(1_050_002_001, "员工入职申请单不存在");
    ErrorCode EMPLOYEE_ENTRY_BILL_MOBILE_EXISTS = new ErrorCode(1_050_002_002, "手机号已存在，无法重复录入");
    ErrorCode EMPLOYEE_ENTRY_BILL_ID_CARD_EXISTS = new ErrorCode(1_050_002_003, "身份证号已存在，无法重复录入");

    // ========== 员工转正申请单 1-050-003-000 ==========
    ErrorCode EMPLOYEE_REGULAR_BILL_NOT_EXISTS = new ErrorCode(1_050_003_001, "员工转正申请单不存在");

    // ========== 人事调动申请单 1-050-004-000 ==========
    ErrorCode EMPLOYEE_TRANSFER_BILL_NOT_EXISTS = new ErrorCode(1_050_004_001, "人事调动申请单不存在");

    // ========== 员工离职申请单 1-050-005-000 ==========
    ErrorCode EMPLOYEE_RESIGNATION_BILL_NOT_EXISTS = new ErrorCode(1_050_005_001, "员工离职申请单不存在");

}

