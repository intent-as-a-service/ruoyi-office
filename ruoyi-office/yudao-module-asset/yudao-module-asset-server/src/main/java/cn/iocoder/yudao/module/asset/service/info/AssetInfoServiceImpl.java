package cn.iocoder.yudao.module.asset.service.info;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.asset.controller.admin.info.vo.*;
import cn.iocoder.yudao.module.asset.dal.dataobject.info.AssetInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.asset.dal.mysql.info.AssetInfoMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.asset.enums.ErrorCodeConstants.*;

/**
 * 资产信息 Service 实现类
 *
 * @author 宇擎源码
 */
@Service
@Validated
public class AssetInfoServiceImpl implements AssetInfoService {

    @Resource
    private AssetInfoMapper infoMapper;

    @Override
    public Long createInfo(AssetInfoSaveReqVO createReqVO) {
        // 插入
        AssetInfoDO info = BeanUtils.toBean(createReqVO, AssetInfoDO.class);
        infoMapper.insert(info);

        // 返回
        return info.getId();
    }

    @Override
    public void updateInfo(AssetInfoSaveReqVO updateReqVO) {
        // 校验存在
        validateInfoExists(updateReqVO.getId());
        // 更新
        AssetInfoDO updateObj = BeanUtils.toBean(updateReqVO, AssetInfoDO.class);
        infoMapper.updateById(updateObj);
    }

    @Override
    public void deleteInfo(Long id) {
        // 校验存在
        validateInfoExists(id);
        // 删除
        infoMapper.deleteById(id);
    }

    @Override
        public void deleteInfoListByIds(List<Long> ids) {
        // 删除
        infoMapper.deleteByIds(ids);
        }


    private void validateInfoExists(Long id) {
        if (infoMapper.selectById(id) == null) {
            throw exception(INFO_NOT_EXISTS);
        }
    }

    @Override
    public AssetInfoDO getInfo(Long id) {
        return infoMapper.selectById(id);
    }

    @Override
    public PageResult<AssetInfoDO> getInfoPage(AssetInfoPageReqVO pageReqVO) {
        return infoMapper.selectPage(pageReqVO);
    }

    /**
     * 批量保存资产信息
     *
     * @param createReqVOList 创建信息
     * @return 编号
     */
    @Override
    public boolean batchSave(List<AssetInfoSaveReqVO> createReqVOList) {
        // 插入
        List<AssetInfoDO> info = BeanUtils.toBean(createReqVOList, AssetInfoDO.class);
        return infoMapper.insertBatch(info);
    }

}