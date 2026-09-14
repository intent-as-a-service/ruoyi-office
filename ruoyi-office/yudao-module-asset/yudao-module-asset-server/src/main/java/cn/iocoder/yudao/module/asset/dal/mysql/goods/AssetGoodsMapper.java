package cn.iocoder.yudao.module.asset.dal.mysql.goods;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.asset.dal.dataobject.category.CategoryDO;
import cn.iocoder.yudao.module.asset.dal.dataobject.goods.AssetGoodsDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.asset.controller.admin.goods.vo.*;

/**
 * 物品信息 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface AssetGoodsMapper extends BaseMapperX<AssetGoodsDO> {

    default PageResult<AssetGoodsDO> selectPage(AssetGoodsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AssetGoodsDO>()
                .eqIfPresent(AssetGoodsDO::getGoodsCode, reqVO.getGoodsCode())
                .likeIfPresent(AssetGoodsDO::getGoodsName, reqVO.getGoodsName())
                .eqIfPresent(AssetGoodsDO::getAssetCategoryCode, reqVO.getAssetCategoryCode())
                .likeIfPresent(AssetGoodsDO::getAssetCategoryName, reqVO.getAssetCategoryName())
                .eqIfPresent(AssetGoodsDO::getAssetModel, reqVO.getAssetModel())
                .eqIfPresent(AssetGoodsDO::getAssetUnit, reqVO.getAssetUnit())
                .eqIfPresent(AssetGoodsDO::getManufacturer, reqVO.getManufacturer())
                .eqIfPresent(AssetGoodsDO::getBrand, reqVO.getBrand())
                .eqIfPresent(AssetGoodsDO::getResidualValueRate, reqVO.getResidualValueRate())
                .eqIfPresent(AssetGoodsDO::getInventoryLowerLimit, reqVO.getInventoryLowerLimit())
                .eqIfPresent(AssetGoodsDO::getInventoryLimit, reqVO.getInventoryLimit())
                .eqIfPresent(AssetGoodsDO::getIsJoinAsset, reqVO.getIsJoinAsset())
                .eqIfPresent(AssetGoodsDO::getAssetIcon, reqVO.getAssetIcon())
                .eqIfPresent(AssetGoodsDO::getAssetFile, reqVO.getAssetFile())
                .eqIfPresent(AssetGoodsDO::getSort, reqVO.getSort())
                .eqIfPresent(AssetGoodsDO::getStatus, reqVO.getStatus())
                .eqIfPresent(AssetGoodsDO::getStoreAddress, reqVO.getStoreAddress())
                .eqIfPresent(AssetGoodsDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(AssetGoodsDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AssetGoodsDO::getId));
    }
    default AssetGoodsDO selectByGoodsCode(String goodsCode) {
        return selectOne(AssetGoodsDO::getGoodsCode, goodsCode);
    }
}