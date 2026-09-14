package cn.iocoder.yudao.module.wms.service.goodscommonoperationorder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.module.wms.dal.dataobject.goodswarehousingdetail.GoodsWarehousingDetailDO;
import cn.iocoder.yudao.module.wms.dal.mysql.goodswarehousingdetail.GoodsWarehousingDetailMapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.wms.controller.admin.goodscommonoperationorder.vo.*;
import cn.iocoder.yudao.module.wms.dal.dataobject.goodscommonoperationorder.GoodsCommonOperationOrderDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.wms.dal.mysql.goodscommonoperationorder.GoodsCommonOperationOrderMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.*;

/**
 * 领用、退库、归还、借用、调拨主 Service 实现类
 *
 * @author 宇擎源码
 */
@Service
@Validated
public class GoodsCommonOperationOrderServiceImpl implements GoodsCommonOperationOrderService {

    @Resource
    private GoodsCommonOperationOrderMapper goodsCommonOperationOrderMapper;
    @Resource
    private GoodsWarehousingDetailMapper goodsWarehousingDetailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createGoodsCommonOperationOrder(GoodsCommonOperationOrderSaveReqVO createReqVO) {
        // 插入
        GoodsCommonOperationOrderDO goodsCommonOperationOrder = BeanUtils.toBean(createReqVO, GoodsCommonOperationOrderDO.class);
        goodsCommonOperationOrderMapper.insert(goodsCommonOperationOrder);


        // 插入子表
        createGoodsWarehousingDetailList(goodsCommonOperationOrder.getId(), createReqVO.getGoodsWarehousingDetails());
        // 返回
        return goodsCommonOperationOrder.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGoodsCommonOperationOrder(GoodsCommonOperationOrderSaveReqVO updateReqVO) {
        // 校验存在
        validateGoodsCommonOperationOrderExists(updateReqVO.getId());
        // 更新
        GoodsCommonOperationOrderDO updateObj = BeanUtils.toBean(updateReqVO, GoodsCommonOperationOrderDO.class);
        goodsCommonOperationOrderMapper.updateById(updateObj);

        // 更新子表
        updateGoodsWarehousingDetailList(updateReqVO.getId(), updateReqVO.getGoodsWarehousingDetails());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGoodsCommonOperationOrder(Long id) {
        // 校验存在
        validateGoodsCommonOperationOrderExists(id);
        // 删除
        goodsCommonOperationOrderMapper.deleteById(id);

        // 删除子表
        deleteGoodsWarehousingDetailByCommonOperationId(id);
    }

    @Override
        @Transactional(rollbackFor = Exception.class)
    public void deleteGoodsCommonOperationOrderListByIds(List<Long> ids) {
        // 删除
        goodsCommonOperationOrderMapper.deleteByIds(ids);
    
    // 删除子表
            deleteGoodsWarehousingDetailByCommonOperationIds(ids);
    }


    private void validateGoodsCommonOperationOrderExists(Long id) {
        if (goodsCommonOperationOrderMapper.selectById(id) == null) {
            throw exception(GOODS_COMMON_OPERATION_ORDER_NOT_EXISTS);
        }
    }

    @Override
    public GoodsCommonOperationOrderDO getGoodsCommonOperationOrder(Long id) {
        return goodsCommonOperationOrderMapper.selectById(id);
    }

    @Override
    public PageResult<GoodsCommonOperationOrderDO> getGoodsCommonOperationOrderPage(GoodsCommonOperationOrderPageReqVO pageReqVO) {
        return goodsCommonOperationOrderMapper.selectPage(pageReqVO);
    }

    // ==================== 子表（采购入库、领用、退库、归还、借用、调拨明细） ====================

    @Override
    public List<GoodsWarehousingDetailDO> getGoodsWarehousingDetailListByCommonOperationId(Long commonOperationId) {
        return goodsWarehousingDetailMapper.selectListByCommonOperationId(commonOperationId);
    }

    private void createGoodsWarehousingDetailList(Long commonOperationId, List<GoodsWarehousingDetailDO> list) {
        list.forEach(o -> o.setCommonOperationId(commonOperationId).clean());
        goodsWarehousingDetailMapper.insertBatch(list);
    }

    private void updateGoodsWarehousingDetailList(Long commonOperationId, List<GoodsWarehousingDetailDO> list) {
	    list.forEach(o -> o.setCommonOperationId(commonOperationId).clean());
	    List<GoodsWarehousingDetailDO> oldList = goodsWarehousingDetailMapper.selectListByCommonOperationId(commonOperationId);
	    List<List<GoodsWarehousingDetailDO>> diffList = diffList(oldList, list, (oldVal, newVal) -> {
            boolean same = ObjectUtil.equal(oldVal.getId(), newVal.getId());
            if (same) {
                newVal.setId(oldVal.getId()).clean(); // 解决更新情况下：updateTime 不更新
            }
            return same;
	    });

	    // 第二步，批量添加、修改、删除
	    if (CollUtil.isNotEmpty(diffList.get(0))) {
	        goodsWarehousingDetailMapper.insertBatch(diffList.get(0));
	    }
	    if (CollUtil.isNotEmpty(diffList.get(1))) {
	        goodsWarehousingDetailMapper.updateBatch(diffList.get(1));
	    }
	    if (CollUtil.isNotEmpty(diffList.get(2))) {
	        goodsWarehousingDetailMapper.deleteByIds(convertList(diffList.get(2), GoodsWarehousingDetailDO::getId));
	    }
    }

    private void deleteGoodsWarehousingDetailByCommonOperationId(Long commonOperationId) {
        goodsWarehousingDetailMapper.deleteByCommonOperationId(commonOperationId);
    }

	private void deleteGoodsWarehousingDetailByCommonOperationIds(List<Long> commonOperationIds) {
        goodsWarehousingDetailMapper.deleteByCommonOperationIds(commonOperationIds);
	}

}