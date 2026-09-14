package cn.iocoder.yudao.module.asset.dal.mysql.lifetime;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.asset.dal.dataobject.lifetime.AssetLifeTimeDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.asset.controller.admin.lifetime.vo.*;

/**
 * 资产历史记录 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface AssetLifeTimeMapper extends BaseMapperX<AssetLifeTimeDO> {

    default PageResult<AssetLifeTimeDO> selectPage(AssetLifeTimePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AssetLifeTimeDO>()
                .eqIfPresent(AssetLifeTimeDO::getAssetId, reqVO.getAssetId())
                .eqIfPresent(AssetLifeTimeDO::getAssetCode, reqVO.getAssetCode())
                .likeIfPresent(AssetLifeTimeDO::getAssetName, reqVO.getAssetName())
                .eqIfPresent(AssetLifeTimeDO::getAssetMilestone, reqVO.getAssetMilestone())
                .eqIfPresent(AssetLifeTimeDO::getSort, reqVO.getSort())
                .eqIfPresent(AssetLifeTimeDO::getStatus, reqVO.getStatus())
                .eqIfPresent(AssetLifeTimeDO::getStoreAddress, reqVO.getStoreAddress())
                .eqIfPresent(AssetLifeTimeDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(AssetLifeTimeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AssetLifeTimeDO::getId));
    }

}