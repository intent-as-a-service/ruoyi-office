package cn.iocoder.yudao.module.wms.service.purchaseinwarehousing;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.module.wms.dal.dataobject.goodswarehousingdetail.GoodsWarehousingDetailDO;
import cn.iocoder.yudao.module.wms.dal.mysql.goodswarehousingdetail.GoodsWarehousingDetailMapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.wms.controller.admin.purchaseinwarehousing.vo.*;
import cn.iocoder.yudao.module.wms.dal.dataobject.purchaseinwarehousing.PurchaseInWarehousingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.wms.dal.mysql.purchaseinwarehousing.PurchaseInWarehousingMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.*;

/**
 * 采购入库 Service 实现类
 *
 * @author 宇擎源码
 */
@Service
@Validated
public class PurchaseInWarehousingServiceImpl implements PurchaseInWarehousingService {

    @Resource
    private PurchaseInWarehousingMapper purchaseInWarehousingMapper;
    @Resource
    private GoodsWarehousingDetailMapper goodsWarehousingDetailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPurchaseInWarehousing(PurchaseInWarehousingSaveReqVO createReqVO) {
        // 插入
        PurchaseInWarehousingDO purchaseInWarehousing = BeanUtils.toBean(createReqVO, PurchaseInWarehousingDO.class);
        purchaseInWarehousingMapper.insert(purchaseInWarehousing);


        // 插入子表
        createGoodsWarehousingDetailList(purchaseInWarehousing.getId(), createReqVO.getGoodsWarehousingDetails());
        // 返回
        return purchaseInWarehousing.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePurchaseInWarehousing(PurchaseInWarehousingSaveReqVO updateReqVO) {
        // 校验存在
        validatePurchaseInWarehousingExists(updateReqVO.getId());
        // 更新
        PurchaseInWarehousingDO updateObj = BeanUtils.toBean(updateReqVO, PurchaseInWarehousingDO.class);
        purchaseInWarehousingMapper.updateById(updateObj);

        // 更新子表
        updateGoodsWarehousingDetailList(updateReqVO.getId(), updateReqVO.getGoodsWarehousingDetails());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePurchaseInWarehousing(Long id) {
        // 校验存在
        validatePurchaseInWarehousingExists(id);
        // 删除
        purchaseInWarehousingMapper.deleteById(id);

        // 删除子表
        deleteGoodsWarehousingDetailByPurchaseOrderId(id);
    }

    @Override
        @Transactional(rollbackFor = Exception.class)
    public void deletePurchaseInWarehousingListByIds(List<Long> ids) {
        // 删除
        purchaseInWarehousingMapper.deleteByIds(ids);
    
    // 删除子表
            deleteGoodsWarehousingDetailByPurchaseOrderIds(ids);
    }


    private void validatePurchaseInWarehousingExists(Long id) {
        if (purchaseInWarehousingMapper.selectById(id) == null) {
            throw exception(PURCHASE_IN_WAREHOUSING_NOT_EXISTS);
        }
    }

    @Override
    public PurchaseInWarehousingDO getPurchaseInWarehousing(Long id) {
        return purchaseInWarehousingMapper.selectById(id);
    }

    @Override
    public PageResult<PurchaseInWarehousingDO> getPurchaseInWarehousingPage(PurchaseInWarehousingPageReqVO pageReqVO) {
        return purchaseInWarehousingMapper.selectPage(pageReqVO);
    }

    // ==================== 子表（采购入库、领用、退库、归还、借用、调拨明细） ====================

    @Override
    public List<GoodsWarehousingDetailDO> getGoodsWarehousingDetailListByPurchaseOrderId(Long purchaseOrderId) {
        return goodsWarehousingDetailMapper.selectListByPurchaseOrderId(purchaseOrderId);
    }

    private void createGoodsWarehousingDetailList(Long purchaseOrderId, List<GoodsWarehousingDetailDO> list) {
        list.forEach(o -> o.setPurchaseOrderId(purchaseOrderId).clean());
        goodsWarehousingDetailMapper.insertBatch(list);
    }

    private void updateGoodsWarehousingDetailList(Long purchaseOrderId, List<GoodsWarehousingDetailDO> list) {
	    list.forEach(o -> o.setPurchaseOrderId(purchaseOrderId).clean());
	    List<GoodsWarehousingDetailDO> oldList = goodsWarehousingDetailMapper.selectListByPurchaseOrderId(purchaseOrderId);
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

    private void deleteGoodsWarehousingDetailByPurchaseOrderId(Long purchaseOrderId) {
        goodsWarehousingDetailMapper.deleteByPurchaseOrderId(purchaseOrderId);
    }

	private void deleteGoodsWarehousingDetailByPurchaseOrderIds(List<Long> purchaseOrderIds) {
        goodsWarehousingDetailMapper.deleteByPurchaseOrderIds(purchaseOrderIds);
	}

}