package cn.iocoder.yudao.module.asset.service.info;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.asset.controller.admin.info.vo.*;
import cn.iocoder.yudao.module.asset.dal.dataobject.info.AssetInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 资产信息 Service 接口
 *
 * @author 宇擎源码
 */
public interface AssetInfoService {

    /**
     * 创建资产信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createInfo(@Valid AssetInfoSaveReqVO createReqVO);

    /**
     * 更新资产信息
     *
     * @param updateReqVO 更新信息
     */
    void updateInfo(@Valid AssetInfoSaveReqVO updateReqVO);

    /**
     * 删除资产信息
     *
     * @param id 编号
     */
    void deleteInfo(Long id);

    /**
    * 批量删除资产信息
    *
    * @param ids 编号
    */
    void deleteInfoListByIds(List<Long> ids);

    /**
     * 获得资产信息
     *
     * @param id 编号
     * @return 资产信息
     */
    AssetInfoDO getInfo(Long id);

    /**
     * 获得资产信息分页
     *
     * @param pageReqVO 分页查询
     * @return 资产信息分页
     */
    PageResult<AssetInfoDO> getInfoPage(AssetInfoPageReqVO pageReqVO);

    /**
     * 批量保存资产信息
     *
     * @param createReqVOList 创建信息
     * @return 编号
     */
    boolean batchSave(List<AssetInfoSaveReqVO> createReqVOList);
}