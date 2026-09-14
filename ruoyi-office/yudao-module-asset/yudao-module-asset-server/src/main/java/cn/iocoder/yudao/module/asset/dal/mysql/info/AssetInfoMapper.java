package cn.iocoder.yudao.module.asset.dal.mysql.info;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.asset.dal.dataobject.info.AssetInfoDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.asset.controller.admin.info.vo.*;

/**
 * 资产信息 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface AssetInfoMapper extends BaseMapperX<AssetInfoDO> {

    default PageResult<AssetInfoDO> selectPage(AssetInfoPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AssetInfoDO>()
                .eqIfPresent(AssetInfoDO::getPurchaseOrderId, reqVO.getPurchaseOrderId())
                .eqIfPresent(AssetInfoDO::getPurchaseOrderCode, reqVO.getPurchaseOrderCode())
                .likeIfPresent(AssetInfoDO::getPurchaseOrderName, reqVO.getPurchaseOrderName())
                .eqIfPresent(AssetInfoDO::getInWarehousingId, reqVO.getInWarehousingId())
                .eqIfPresent(AssetInfoDO::getWarehousingEntryCode, reqVO.getWarehousingEntryCode())
                .likeIfPresent(AssetInfoDO::getWarehousingEntryName, reqVO.getWarehousingEntryName())
                .eqIfPresent(AssetInfoDO::getAssetCode, reqVO.getAssetCode())
                .likeIfPresent(AssetInfoDO::getAssetName, reqVO.getAssetName())
                .eqIfPresent(AssetInfoDO::getAssetCategoryCode, reqVO.getAssetCategoryCode())
                .likeIfPresent(AssetInfoDO::getAssetCategoryName, reqVO.getAssetCategoryName())
                .eqIfPresent(AssetInfoDO::getAssetModel, reqVO.getAssetModel())
                .eqIfPresent(AssetInfoDO::getAssetUnit, reqVO.getAssetUnit())
                .eqIfPresent(AssetInfoDO::getManufacturer, reqVO.getManufacturer())
                .eqIfPresent(AssetInfoDO::getBrand, reqVO.getBrand())
                .eqIfPresent(AssetInfoDO::getSerialNumber, reqVO.getSerialNumber())
                .eqIfPresent(AssetInfoDO::getAssetStatusCode, reqVO.getAssetStatusCode())
                .likeIfPresent(AssetInfoDO::getAssetStatusName, reqVO.getAssetStatusName())
                .eqIfPresent(AssetInfoDO::getAssetSourceCode, reqVO.getAssetSourceCode())
                .likeIfPresent(AssetInfoDO::getAssetSourceName, reqVO.getAssetSourceName())
                .betweenIfPresent(AssetInfoDO::getPurchaseDate, reqVO.getPurchaseDate())
                .eqIfPresent(AssetInfoDO::getPurchasePrice, reqVO.getPurchasePrice())
                .eqIfPresent(AssetInfoDO::getDateOfProduction, reqVO.getDateOfProduction())
                .eqIfPresent(AssetInfoDO::getAdminCompanyId, reqVO.getAdminCompanyId())
                .likeIfPresent(AssetInfoDO::getAdminCompanyName, reqVO.getAdminCompanyName())
                .eqIfPresent(AssetInfoDO::getAdminDeptId, reqVO.getAdminDeptId())
                .likeIfPresent(AssetInfoDO::getAdminDeptName, reqVO.getAdminDeptName())
                .eqIfPresent(AssetInfoDO::getAdminManagerId, reqVO.getAdminManagerId())
                .likeIfPresent(AssetInfoDO::getAdminManagerName, reqVO.getAdminManagerName())
                .eqIfPresent(AssetInfoDO::getUseCompanyId, reqVO.getUseCompanyId())
                .likeIfPresent(AssetInfoDO::getUseCompanyName, reqVO.getUseCompanyName())
                .eqIfPresent(AssetInfoDO::getUseDeptId, reqVO.getUseDeptId())
                .likeIfPresent(AssetInfoDO::getUseDeptName, reqVO.getUseDeptName())
                .eqIfPresent(AssetInfoDO::getUseAccountId, reqVO.getUseAccountId())
                .likeIfPresent(AssetInfoDO::getUseAccountName, reqVO.getUseAccountName())
                .eqIfPresent(AssetInfoDO::getWmsStoreCode, reqVO.getWmsStoreCode())
                .likeIfPresent(AssetInfoDO::getWmsStoreName, reqVO.getWmsStoreName())
                .eqIfPresent(AssetInfoDO::getResidualValueRate, reqVO.getResidualValueRate())
                .eqIfPresent(AssetInfoDO::getAssetIcon, reqVO.getAssetIcon())
                .eqIfPresent(AssetInfoDO::getAssetFile, reqVO.getAssetFile())
                .eqIfPresent(AssetInfoDO::getSort, reqVO.getSort())
                .eqIfPresent(AssetInfoDO::getStatus, reqVO.getStatus())
                .eqIfPresent(AssetInfoDO::getStoreAddress, reqVO.getStoreAddress())
                .eqIfPresent(AssetInfoDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(AssetInfoDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AssetInfoDO::getId));
    }

}