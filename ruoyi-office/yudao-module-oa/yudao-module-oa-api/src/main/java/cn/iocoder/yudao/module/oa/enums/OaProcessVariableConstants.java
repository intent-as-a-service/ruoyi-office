package cn.iocoder.yudao.module.oa.enums;

/**
 * OA 模块流程变量常量
 * 
 * 定义OA模块中各种业务流程的变量名称，用于在BPM流程中传递业务数据
 * 
 * @author 宇擎源码
 */
public interface OaProcessVariableConstants {

    // ========== 用印申请单流程变量 ==========

    /**
     * 用印方式变量名
     * 
     * 用于在用印申请单流程中传递用印方式信息
     * 1-现场用章，2-借用印章
     */
    String PV_SEAL_USE_MODE = "sealUseMode";

    // ========== 会议室预定申请单流程变量 ==========

    /**
     * 预定需审批变量名
     * 
     * 用于在会议室预定申请单流程中传递是否需要审批信息
     * true-需要审批，false-不需要审批
     */
    String PV_MEETING_ROOM_NEED_APPROVAL = "meetingRoomNeedApproval";


}
