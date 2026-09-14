/*
 * @Author: zhanghui
 * @Date: 2025-07-26 17:16:44
 * @LastEditTime: 2025-08-08 16:23:31
 * @LastEditors: zhanghui
 * @Description:
 */
export type headerDataProps = {
  /**
   * 申请单编号
   */
  billCode?: string;

  /**
   * 标题
   */
  billName?: string;

  /**
   * 所属单位
   */
  companyId?: number;

  companyName?: string;
  /**
   * 申请日期
   */
  createTime?: Date | string;
  /**
   * 申请人
   */
  creatorName?: string;
  /**
   * 所属部门
   */
  deptId?: number;
  /**
   * 所属部门名称
   */
  deptName?: string;

  /**
   * 单据ID
   */
  id?: number;

  /**
   * 流程实例ID
   */
  processInstanceId?: string;

  /**
   * 审批状态
   */
  processStatus?: number;
};
