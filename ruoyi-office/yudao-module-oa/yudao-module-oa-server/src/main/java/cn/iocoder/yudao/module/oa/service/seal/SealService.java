package cn.iocoder.yudao.module.oa.service.seal;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.oa.controller.admin.seal.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.seal.SealDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 印章信息 Service 接口
 *
 * @author 宇擎源码
 */
public interface SealService {

    /**
     * 创建印章信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSeal(@Valid SealSaveReqVO createReqVO);

    /**
     * 更新印章信息
     *
     * @param updateReqVO 更新信息
     */
    void updateSeal(@Valid SealSaveReqVO updateReqVO);

    /**
     * 删除印章信息
     *
     * @param id 编号
     */
    void deleteSeal(Long id);

    /**
     * 批量删除印章信息
     *
     * @param ids 编号
     */
    void deleteSealListByIds(List<Long> ids);

    /**
     * 获得印章信息
     *
     * @param id 编号
     * @return 印章信息
     */
    SealDO getSeal(Long id);

    /**
     * 获得印章信息分页
     *
     * @param pageReqVO 分页查询
     * @return 印章信息分页
     */
    PageResult<SealDO> getSealPage(SealPageReqVO pageReqVO);

}

