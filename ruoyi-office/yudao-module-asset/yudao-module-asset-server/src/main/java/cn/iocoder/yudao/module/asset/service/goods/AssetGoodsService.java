package cn.iocoder.yudao.module.asset.service.goods;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.asset.controller.admin.goods.vo.*;
import cn.iocoder.yudao.module.asset.dal.dataobject.goods.AssetGoodsDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 物品信息 Service 接口
 *
 * @author 宇擎源码
 */
public interface AssetGoodsService {

    /**
     * 创建物品信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createGoods(@Valid AssetGoodsSaveReqVO createReqVO);

    /**
     * 更新物品信息
     *
     * @param updateReqVO 更新信息
     */
    void updateGoods(@Valid AssetGoodsSaveReqVO updateReqVO);

    /**
     * 删除物品信息
     *
     * @param id 编号
     */
    void deleteGoods(Long id);

    /**
    * 批量删除物品信息
    *
    * @param ids 编号
    */
    void deleteGoodsListByIds(List<Long> ids);

    /**
     * 获得物品信息
     *
     * @param id 编号
     * @return 物品信息
     */
    AssetGoodsDO getGoods(Long id);

    /**
     * 获得物品信息分页
     *
     * @param pageReqVO 分页查询
     * @return 物品信息分页
     */
    PageResult<AssetGoodsDO> getGoodsPage(AssetGoodsPageReqVO pageReqVO);

}