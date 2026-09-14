package cn.iocoder.yudao.module.wms.service.warehousing;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.wms.controller.admin.warehousing.vo.*;
import cn.iocoder.yudao.module.wms.dal.dataobject.warehousing.WarehousingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.wms.dal.mysql.warehousing.WarehousingMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.*;

/**
 * 仓库信息 Service 实现类
 *
 * @author 宇擎源码
 */
@Service
@Validated
public class WarehousingServiceImpl implements WarehousingService {

    @Resource
    private WarehousingMapper warehousingMapper;

    @Override
    public Long createWarehousing(WarehousingSaveReqVO createReqVO) {
        // 校验上级id的有效性
        validateParentWarehousing(null, createReqVO.getParentId());
        // 校验仓库名称的唯一性
        validateWarehousingWarehousingNameUnique(null,  createReqVO.getWarehousingName());
        // 校验仓库编码的唯一性
        validateWarehousingWarehousingCodeUnique(null, createReqVO.getWarehousingCode());
        // 插入
        WarehousingDO warehousing = BeanUtils.toBean(createReqVO, WarehousingDO.class);
        warehousingMapper.insert(warehousing);

        // 返回
        return warehousing.getId();
    }

    @Override
    public void updateWarehousing(WarehousingSaveReqVO updateReqVO) {
        // 校验存在
        validateWarehousingExists(updateReqVO.getId());
        // 校验上级id的有效性
        validateParentWarehousing(updateReqVO.getId(), updateReqVO.getParentId());
        // 校验仓库名称的唯一性
        validateWarehousingWarehousingNameUnique(updateReqVO.getId(), updateReqVO.getWarehousingName());
        // 校验仓库编码的唯一性
        validateWarehousingWarehousingCodeUnique(updateReqVO.getId(), updateReqVO.getWarehousingCode());
        // 更新
        WarehousingDO updateObj = BeanUtils.toBean(updateReqVO, WarehousingDO.class);
        warehousingMapper.updateById(updateObj);
    }

    @Override
    public void deleteWarehousing(Long id) {
        // 校验存在
        validateWarehousingExists(id);
        // 校验是否有子仓库信息
        if (warehousingMapper.selectCountByParentId(id) > 0) {
            throw exception(WAREHOUSING_EXITS_CHILDREN);
        }
        // 删除
        warehousingMapper.deleteById(id);
    }


    private void validateWarehousingExists(Long id) {
        if (warehousingMapper.selectById(id) == null) {
            throw exception(WAREHOUSING_NOT_EXISTS);
        }
    }

    private void validateParentWarehousing(Long id, Long parentId) {
        if (parentId == null || WarehousingDO.PARENT_ID_ROOT.equals(parentId)) {
            return;
        }
        // 1. 不能设置自己为父仓库信息
        if (Objects.equals(id, parentId)) {
            throw exception(WAREHOUSING_PARENT_ERROR);
        }
        // 2. 父仓库信息不存在
        WarehousingDO parentWarehousing = warehousingMapper.selectById(parentId);
        if (parentWarehousing == null) {
            throw exception(WAREHOUSING_PARENT_NOT_EXITS);
        }
        // 3. 递归校验父仓库信息，如果父仓库信息是自己的子仓库信息，则报错，避免形成环路
        if (id == null) { // id 为空，说明新增，不需要考虑环路
            return;
        }
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            // 3.1 校验环路
            parentId = parentWarehousing.getParentId();
            if (Objects.equals(id, parentId)) {
                throw exception(WAREHOUSING_PARENT_IS_CHILD);
            }
            // 3.2 继续递归下一级父仓库信息
            if (parentId == null || WarehousingDO.PARENT_ID_ROOT.equals(parentId)) {
                break;
            }
            parentWarehousing = warehousingMapper.selectById(parentId);
            if (parentWarehousing == null) {
                break;
            }
        }
    }

    private void validateWarehousingWarehousingNameUnique(Long id,String warehousingName) {
        WarehousingDO warehousing = warehousingMapper.selectByParentIdAndWarehousingName( warehousingName);
        if (warehousing == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的仓库信息
        if (id == null) {
            throw exception(WAREHOUSING_WAREHOUSING_NAME_DUPLICATE);
        }
        if (!Objects.equals(warehousing.getId(), id)) {
            throw exception(WAREHOUSING_WAREHOUSING_NAME_DUPLICATE);
        }
    }

    private void validateWarehousingWarehousingCodeUnique(Long id, String warehousingCode) {
        WarehousingDO warehousing = warehousingMapper.selectByParentIdAndWarehousingCode(warehousingCode);
        if (warehousing == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的仓库信息
        if (id == null) {
            throw exception(WAREHOUSING_WAREHOUSING_CODE_DUPLICATE);
        }
        if (!Objects.equals(warehousing.getId(), id)) {
            throw exception(WAREHOUSING_WAREHOUSING_CODE_DUPLICATE);
        }
    }


    @Override
    public WarehousingDO getWarehousing(Long id) {
        return warehousingMapper.selectById(id);
    }

    @Override
    public List<WarehousingDO> getWarehousingList(WarehousingListReqVO listReqVO) {
        return warehousingMapper.selectList(listReqVO);
    }

}