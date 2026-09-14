package cn.iocoder.yudao.module.asset.service.goods;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.asset.dal.dataobject.category.CategoryDO;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.asset.controller.admin.goods.vo.*;
import cn.iocoder.yudao.module.asset.dal.dataobject.goods.AssetGoodsDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.asset.dal.mysql.goods.AssetGoodsMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.asset.enums.ErrorCodeConstants.*;

/**
 * 物品信息 Service 实现类
 *
 * @author 宇擎源码
 */
@Service
@Validated
public class AssetGoodsServiceImpl implements AssetGoodsService {

    @Resource
    private AssetGoodsMapper goodsMapper;

    @Override
    public Long createGoods(AssetGoodsSaveReqVO createReqVO) {
        // 校验存在
        validateGoodsCodeUnique(null, createReqVO.getGoodsCode());
        // 插入
        AssetGoodsDO goods = BeanUtils.toBean(createReqVO, AssetGoodsDO.class);
        goodsMapper.insert(goods);

        // 返回
        return goods.getId();
    }

    @Override
    public void updateGoods(AssetGoodsSaveReqVO updateReqVO) {
        // 校验存在
        validateGoodsExists(updateReqVO.getId());
        // 校验存在
        validateGoodsCodeUnique(updateReqVO.getId(), updateReqVO.getGoodsCode());
        // 更新
        AssetGoodsDO updateObj = BeanUtils.toBean(updateReqVO, AssetGoodsDO.class);
        goodsMapper.updateById(updateObj);
    }

    @Override
    public void deleteGoods(Long id) {
        // 校验存在
        validateGoodsExists(id);
        // 删除
        goodsMapper.deleteById(id);
    }

    @Override
        public void deleteGoodsListByIds(List<Long> ids) {
        // 删除
        goodsMapper.deleteByIds(ids);
        }


    private void validateGoodsExists(Long id) {
        if (goodsMapper.selectById(id) == null) {
            throw exception(GOODS_NOT_EXISTS);
        }
    }

    private void validateGoodsCodeUnique(Long id, String goodsCode) {
        AssetGoodsDO assetGoods = goodsMapper.selectByGoodsCode(goodsCode);
        if (assetGoods == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的资产类别
        if (id == null) {
            throw exception(GOODS_CODE_DUPLICATE);
        }
        if (!Objects.equals(assetGoods.getId(), id)) {
            throw exception(GOODS_CODE_DUPLICATE);
        }
    }


    @Override
    public AssetGoodsDO getGoods(Long id) {
        return goodsMapper.selectById(id);
    }

    @Override
    public PageResult<AssetGoodsDO> getGoodsPage(AssetGoodsPageReqVO pageReqVO) {
        return goodsMapper.selectPage(pageReqVO);
    }

}