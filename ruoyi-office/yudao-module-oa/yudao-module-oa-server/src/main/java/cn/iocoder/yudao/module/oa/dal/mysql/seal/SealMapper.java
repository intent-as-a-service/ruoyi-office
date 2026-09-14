package cn.iocoder.yudao.module.oa.dal.mysql.seal;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.oa.dal.dataobject.seal.SealDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.oa.controller.admin.seal.vo.*;

/**
 * 印章信息 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface SealMapper extends BaseMapperX<SealDO> {

    default PageResult<SealDO> selectPage(SealPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SealDO>()
                .eqIfPresent(SealDO::getCompanyId, reqVO.getCompanyId())
                .likeIfPresent(SealDO::getCompanyName, reqVO.getCompanyName())
                .likeIfPresent(SealDO::getSealNo, reqVO.getSealNo())
                .likeIfPresent(SealDO::getSealName, reqVO.getSealName())
                .eqIfPresent(SealDO::getSealType, reqVO.getSealType())
                .eqIfPresent(SealDO::getSealCls, reqVO.getSealCls())
                .eqIfPresent(SealDO::getKeeperId, reqVO.getKeeperId())
                .likeIfPresent(SealDO::getKeeperName, reqVO.getKeeperName())
                .eqIfPresent(SealDO::getKeeperDeptId, reqVO.getKeeperDeptId())
                .likeIfPresent(SealDO::getKeeperDeptName, reqVO.getKeeperDeptName())
                .eqIfPresent(SealDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(SealDO::getPurchaseDate, reqVO.getPurchaseDate())
                .betweenIfPresent(SealDO::getEnableDate, reqVO.getEnableDate())
                .betweenIfPresent(SealDO::getDisableDate, reqVO.getDisableDate())
                .eqIfPresent(SealDO::getPicUrl, reqVO.getPicUrl())
                .eqIfPresent(SealDO::getSort, reqVO.getSort())
                .eqIfPresent(SealDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(SealDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(SealDO::getSort)
                .orderByAsc(SealDO::getCreateTime));
    }

    default SealDO selectBySealNo(String sealNo) {
        return selectOne(SealDO::getSealNo, sealNo);
    }

}

