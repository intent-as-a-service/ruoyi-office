package cn.iocoder.yudao.module.oa.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * oa 错误码枚举类
 *
 * oa 系统，使用 1-101-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== oa车辆管理 1-101-000-000 ============
    ErrorCode CAR_NOT_EXISTS = new ErrorCode(1_101_000_000, "车辆信息不存在");

    // ========== 用车申请单 ==========
    ErrorCode CAR_APPLY_BILL_NOT_EXISTS = new ErrorCode(1_101_000_001, "用车申请单不存在");
    ErrorCode CAR_APPLY_SAVE_INFO_NOT_NULL = new ErrorCode(1_101_000_002, "保存信息不能为空");
    // ========== 还车申请单  ==========
    ErrorCode CAR_RETURN_BILL_NOT_EXISTS = new ErrorCode(1_101_000_003, "还车申请单不存在");
    ErrorCode CAR_APPLY_BILL_ALREADY_RETURNED = new ErrorCode(1_101_000_004, "用车申请单已还车，不能重复还车");
    ErrorCode CAR_TIME_CONFLICT = new ErrorCode(1_101_000_005, "车辆使用时间冲突，该时间段已有其他申请单");

    // ========== 印章管理 1-101-001-000 ============
    ErrorCode SEAL_NOT_EXISTS = new ErrorCode(1_101_001_000, "印章信息不存在");
    ErrorCode SEAL_NO_DUPLICATE = new ErrorCode(1_101_001_001, "印章编号已存在");
    
    // ========== 用印申请单 1-101-001-100 ============
    ErrorCode SEAL_APPLY_BILL_NOT_EXISTS = new ErrorCode(1_101_001_100, "用印申请单不存在");
    ErrorCode SEAL_APPLY_SAVE_INFO_NOT_NULL = new ErrorCode(1_101_001_101, "保存信息不能为空");
    ErrorCode SEAL_TIME_CONFLICT = new ErrorCode(1_101_001_102, "印章使用时间冲突，该时间段已有其他申请单");
    ErrorCode SEAL_APPLY_BILL_ALREADY_USED = new ErrorCode(1_101_001_103, "用印申请单已使用，不能重复使用");
    ErrorCode SEAL_APPLY_BILL_NOT_APPROVED = new ErrorCode(1_101_001_104, "用印申请单未审批通过，不能使用");
    ErrorCode SEAL_APPLY_BILL_OVERDUE = new ErrorCode(1_101_001_105, "印章借用已逾期，请及时归还");

    // ========== 通用附件 1-101-002-000 ============
    ErrorCode ATTACHMENT_NOT_EXISTS = new ErrorCode(1_101_002_000, "附件不存在");

    // ========== 企业云盘 1-101-003-000 ============
    ErrorCode FILE_INFO_NOT_EXISTS = new ErrorCode(1_101_003_000, "文件/文件夹不存在");
    ErrorCode FILE_SHARE_NOT_OWNER = new ErrorCode(1_101_003_001, "只有文件所有者才能分享文件");
    ErrorCode FILE_SHARE_NO_PERMISSION = new ErrorCode(1_101_003_002, "无权限访问");
    ErrorCode FILE_CANCEL_SHARE_NOT_OWNER = new ErrorCode(1_101_003_003, "只有文件所有者才能取消分享");

    // ========== 会议室管理 1-101-004-000 ============
    ErrorCode MEETING_ROOM_NOT_EXISTS = new ErrorCode(1_101_004_000, "会议室信息不存在");

    // ========== 会议室预定 1-101-005-000 ============
    ErrorCode MEETING_ROOM_BOOKING_NOT_EXISTS = new ErrorCode(1_101_005_000, "会议室预定申请单不存在");
    ErrorCode MEETING_ROOM_BOOKING_TIME_INVALID = new ErrorCode(1_101_005_001, "会议时间无效，请检查开始时间和结束时间");
    ErrorCode MEETING_ROOM_BOOKING_TIME_PAST = new ErrorCode(1_101_005_002, "会议开始时间不能是过去时间");
    ErrorCode MEETING_ROOM_BOOKING_TIME_CONFLICT = new ErrorCode(1_101_005_003, "该时间段会议室已被预定，请选择其他时间");
    ErrorCode MEETING_ROOM_BOOKING_CANNOT_DELETE = new ErrorCode(1_101_005_004, "只能删除草稿状态的预定申请单");
}
