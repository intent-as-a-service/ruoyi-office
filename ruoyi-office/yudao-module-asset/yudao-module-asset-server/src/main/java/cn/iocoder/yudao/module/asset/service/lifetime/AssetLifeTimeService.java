package cn.iocoder.yudao.module.asset.service.lifetime;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.asset.controller.admin.lifetime.vo.*;
import cn.iocoder.yudao.module.asset.dal.dataobject.lifetime.AssetLifeTimeDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 资产历史记录 Service 接口
 *
 * @author 宇擎源码
 */
public interface AssetLifeTimeService {

    /**
     * 创建资产历史记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createLifeTime(@Valid AssetLifeTimeSaveReqVO createReqVO);

    /**
     * 更新资产历史记录
     *
     * @param updateReqVO 更新信息
     */
    void updateLifeTime(@Valid AssetLifeTimeSaveReqVO updateReqVO);

    /**
     * 删除资产历史记录
     *
     * @param id 编号
     */
    void deleteLifeTime(Long id);

    /**
    * 批量删除资产历史记录
    *
    * @param ids 编号
    */
    void deleteLifeTimeListByIds(List<Long> ids);

    /**
     * 获得资产历史记录
     *
     * @param id 编号
     * @return 资产历史记录
     */
    AssetLifeTimeDO getLifeTime(Long id);

    /**
     * 获得资产历史记录分页
     *
     * @param pageReqVO 分页查询
     * @return 资产历史记录分页
     */
    PageResult<AssetLifeTimeDO> getLifeTimePage(AssetLifeTimePageReqVO pageReqVO);

}