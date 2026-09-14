package cn.iocoder.yudao.module.oa.dal.mysql.seal;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.oa.dal.dataobject.seal.SealApplyBillDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.oa.controller.admin.seal.vo.*;

/**
 * 用印申请单 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface SealApplyBillMapper extends BaseMapperX<SealApplyBillDO> {

    default PageResult<SealApplyBillDO> selectPage(SealApplyBillPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SealApplyBillDO>()
                .likeIfPresent(SealApplyBillDO::getBillCode, reqVO.getBillCode())
                .eqIfPresent(SealApplyBillDO::getProcessStatus, reqVO.getProcessStatus())
                .eqIfPresent(SealApplyBillDO::getSealId, reqVO.getSealId())
                .likeIfPresent(SealApplyBillDO::getSealNo, reqVO.getSealNo())
                .likeIfPresent(SealApplyBillDO::getSealName, reqVO.getSealName())
                .eqIfPresent(SealApplyBillDO::getUseType, reqVO.getUseType())
                .eqIfPresent(SealApplyBillDO::getUseMode, reqVO.getUseMode())
                .eqIfPresent(SealApplyBillDO::getUseStatus, reqVO.getUseStatus())
                .eqIfPresent(SealApplyBillDO::getIsUrgent, reqVO.getIsUrgent())
                .eqIfPresent(SealApplyBillDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(SealApplyBillDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(SealApplyBillDO::getDeptId, reqVO.getDeptId())
                .likeIfPresent(SealApplyBillDO::getDeptName, reqVO.getDeptName())
                .betweenIfPresent(SealApplyBillDO::getExpectedUseTime, reqVO.getExpectedUseTime())
                .eqIfPresent(SealApplyBillDO::getCreator, reqVO.getCreator())
                .betweenIfPresent(SealApplyBillDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SealApplyBillDO::getId));
    }

}
