package cn.iocoder.yudao.module.asset.service.lifetime;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.asset.controller.admin.lifetime.vo.*;
import cn.iocoder.yudao.module.asset.dal.dataobject.lifetime.AssetLifeTimeDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.asset.dal.mysql.lifetime.AssetLifeTimeMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.asset.enums.ErrorCodeConstants.*;

/**
 * 资产历史记录 Service 实现类
 *
 * @author 宇擎源码
 */
@Service
@Validated
public class AssetLifeTimeServiceImpl implements AssetLifeTimeService {

    @Resource
    private AssetLifeTimeMapper lifeTimeMapper;

    @Override
    public Long createLifeTime(AssetLifeTimeSaveReqVO createReqVO) {
        // 插入
        AssetLifeTimeDO lifeTime = BeanUtils.toBean(createReqVO, AssetLifeTimeDO.class);
        lifeTimeMapper.insert(lifeTime);

        // 返回
        return lifeTime.getId();
    }

    @Override
    public void updateLifeTime(AssetLifeTimeSaveReqVO updateReqVO) {
        // 更新
        AssetLifeTimeDO updateObj = BeanUtils.toBean(updateReqVO, AssetLifeTimeDO.class);
        lifeTimeMapper.updateById(updateObj);
    }

    @Override
    public void deleteLifeTime(Long id) {
        // 删除
        lifeTimeMapper.deleteById(id);
    }

    @Override
        public void deleteLifeTimeListByIds(List<Long> ids) {
        // 删除
        lifeTimeMapper.deleteByIds(ids);
        }


    @Override
    public AssetLifeTimeDO getLifeTime(Long id) {
        return lifeTimeMapper.selectById(id);
    }

    @Override
    public PageResult<AssetLifeTimeDO> getLifeTimePage(AssetLifeTimePageReqVO pageReqVO) {
        return lifeTimeMapper.selectPage(pageReqVO);
    }

}