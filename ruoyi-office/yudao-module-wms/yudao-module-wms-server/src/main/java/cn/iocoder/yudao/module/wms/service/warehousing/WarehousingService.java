package cn.iocoder.yudao.module.wms.service.warehousing;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.wms.controller.admin.warehousing.vo.*;
import cn.iocoder.yudao.module.wms.dal.dataobject.warehousing.WarehousingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 仓库信息 Service 接口
 *
 * @author 宇擎源码
 */
public interface WarehousingService {

    /**
     * 创建仓库信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createWarehousing(@Valid WarehousingSaveReqVO createReqVO);

    /**
     * 更新仓库信息
     *
     * @param updateReqVO 更新信息
     */
    void updateWarehousing(@Valid WarehousingSaveReqVO updateReqVO);

    /**
     * 删除仓库信息
     *
     * @param id 编号
     */
    void deleteWarehousing(Long id);


    /**
     * 获得仓库信息
     *
     * @param id 编号
     * @return 仓库信息
     */
    WarehousingDO getWarehousing(Long id);

    /**
     * 获得仓库信息列表
     *
     * @param listReqVO 查询条件
     * @return 仓库信息列表
     */
    List<WarehousingDO> getWarehousingList(WarehousingListReqVO listReqVO);

}