package cn.iocoder.yudao.module.bpm.enums;

/**
 * BPM 流程变量常量
 * 
 * 定义流程中通用的变量名称，用于在待办列表中显示关键业务信息
 * 
 * @author 宇擎源码
 */
public interface BpmProcessVariableConstants {

    /**
     * 单据编号变量名
     * 
     * 用于在待办列表中显示业务单据的编号
     */
    String BILL_CODE = "billCode";

    /**
     * 事由/说明变量名
     * 
     * 用于在待办列表中显示业务事由或说明信息
     */
    String CAUSE = "cause";

    /**
     * 部门名称变量名
     * 
     * 用于在待办列表中显示申请人所属部门名称
     */
    String DEPT_NAME = "deptName";

    /**
     * 部门ID变量名
     * 
     * 用于在待办列表中显示申请人所属部门ID
     */
    String DEPT_ID = "deptId";

    /**
     * 公司名称变量名
     * 
     * 用于在待办列表中显示申请人所属公司名称
     */
    String COMPANY_NAME = "companyName";

    /**
     * 公司ID变量名
     * 
     * 用于在待办列表中显示申请人所属公司ID
     */
    String COMPANY_ID = "companyId";
}
