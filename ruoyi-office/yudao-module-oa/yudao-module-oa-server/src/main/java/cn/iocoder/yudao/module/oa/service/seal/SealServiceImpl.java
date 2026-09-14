package cn.iocoder.yudao.module.oa.service.seal;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.oa.controller.admin.seal.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.seal.SealDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.oa.dal.mysql.seal.SealMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.oa.enums.ErrorCodeConstants.*;

/**
 * 印章信息 Service 实现类
 *
 * @author 宇擎源码
 */
@Service
@Validated
public class SealServiceImpl implements SealService {

    @Resource
    private SealMapper sealMapper;

    @Override
    public Long createSeal(SealSaveReqVO createReqVO) {
        // 校验印章编号唯一性
        validateSealNoUnique(null, createReqVO.getSealNo());
        // 插入
        SealDO seal = BeanUtils.toBean(createReqVO, SealDO.class);
        sealMapper.insert(seal);
        // 返回
        return seal.getId();
    }

    @Override
    public void updateSeal(SealSaveReqVO updateReqVO) {
        // 校验存在
        validateSealExists(updateReqVO.getId());
        // 校验印章编号唯一性
        validateSealNoUnique(updateReqVO.getId(), updateReqVO.getSealNo());
        // 更新
        SealDO updateObj = BeanUtils.toBean(updateReqVO, SealDO.class);
        sealMapper.updateById(updateObj);
    }

    @Override
    public void deleteSeal(Long id) {
        // 校验存在
        validateSealExists(id);
        // 删除
        sealMapper.deleteById(id);
    }

    @Override
    public void deleteSealListByIds(List<Long> ids) {
        // 校验存在
        validateSealExists(ids);
        // 删除
        sealMapper.deleteByIds(ids);
    }

    private void validateSealExists(List<Long> ids) {
        List<SealDO> list = sealMapper.selectByIds(ids);
        if (CollUtil.isEmpty(list) || list.size() != ids.size()) {
            throw exception(SEAL_NOT_EXISTS);
        }
    }

    private void validateSealExists(Long id) {
        if (sealMapper.selectById(id) == null) {
            throw exception(SEAL_NOT_EXISTS);
        }
    }

    /**
     * 校验印章编号唯一性
     *
     * @param id 印章ID（更新时传入，新增时传null）
     * @param sealNo 印章编号
     */
    private void validateSealNoUnique(Long id, String sealNo) {
        SealDO seal = sealMapper.selectBySealNo(sealNo);
        if (seal == null) {
            return;
        }
        // 如果 id 为空，说明是新增，判断是否存在
        if (id == null) {
            throw exception(SEAL_NO_DUPLICATE);
        }
        // 如果 id 不为空，说明是更新，判断是否是自己
        if (!seal.getId().equals(id)) {
            throw exception(SEAL_NO_DUPLICATE);
        }
    }

    @Override
    public SealDO getSeal(Long id) {
        return sealMapper.selectById(id);
    }

    @Override
    public PageResult<SealDO> getSealPage(SealPageReqVO pageReqVO) {
        return sealMapper.selectPage(pageReqVO);
    }

}

