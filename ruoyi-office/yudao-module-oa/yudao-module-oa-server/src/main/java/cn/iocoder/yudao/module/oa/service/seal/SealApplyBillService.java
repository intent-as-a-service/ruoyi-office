package cn.iocoder.yudao.module.oa.service.seal;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.oa.controller.admin.seal.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.seal.SealApplyBillDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 用印申请单 Service 接口
 *
 * @author 宇擎源码
 */
public interface SealApplyBillService {

    /**
     * 保存用印申请单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long saveSealApplyBill(@Valid SealApplyBillSaveReqVO saveReqVO);

    /**
     * 提交用印申请单
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long submitSealApplyBill(@Valid SealApplyBillSaveReqVO saveReqVO);

    /**
     * 创建用印申请单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSealApplyBill(@Valid SealApplyBillSaveReqVO createReqVO);

    /**
     * 更新用印申请单
     *
     * @param updateReqVO 更新信息
     */
    void updateSealApplyBill(@Valid SealApplyBillSaveReqVO updateReqVO);

    /**
     * 删除用印申请单
     *
     * @param id 编号
     */
    void deleteSealApplyBill(Long id);

    /**
     * 批量删除用印申请单
     *
     * @param ids 编号列表
     */
    void deleteSealApplyBillListByIds(List<Long> ids);

    /**
     * 获得用印申请单
     *
     * @param id 编号
     * @return 用印申请单
     */
    SealApplyBillDO getSealApplyBill(Long id);

    /**
     * 获得用印申请单（包含附件信息）
     *
     * @param id 编号
     * @return 用印申请单响应VO
     */
    SealApplyBillRespVO getSealApplyBillInfo(Long id);

    /**
     * 根据单据编号获得用印申请单
     *
     * @param code 单据编号
     * @return 用印申请单
     */
    SealApplyBillDO getSealApplyBillByCode(String code);

    /**
     * 获得用印申请单分页
     *
     * @param pageReqVO 分页查询
     * @return 用印申请单分页
     */
    PageResult<SealApplyBillDO> getSealApplyBillPage(SealApplyBillPageReqVO pageReqVO);

    /**
     * 更新用印状态
     *
     * @param id 编号
     * @param useStatus 用印状态
     */
    void updateUseStatus(Long id, Integer useStatus);

    /**
     * 标记为已完成
     *
     * @param id 编号
     */
    void markAsCompleted(Long id);

    /**
     * 标记为外借中
     *
     * @param id 编号
     */
    void markAsBorrowed(Long id);

    /**
     * 标记为已归还
     *
     * @param id 编号
     */
    void markAsReturned(Long id);

    /**
     * 标记为已逾期
     *
     * @param id 编号
     */
    void markAsOverdue(Long id);

}
