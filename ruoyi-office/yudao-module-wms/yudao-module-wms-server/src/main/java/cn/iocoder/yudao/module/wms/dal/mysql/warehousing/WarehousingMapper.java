package cn.iocoder.yudao.module.wms.dal.mysql.warehousing;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.wms.dal.dataobject.warehousing.WarehousingDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.wms.controller.admin.warehousing.vo.*;

/**
 * 仓库信息 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface WarehousingMapper extends BaseMapperX<WarehousingDO> {

    default List<WarehousingDO> selectList(WarehousingListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<WarehousingDO>()
                .eqIfPresent(WarehousingDO::getWarehousingCode, reqVO.getWarehousingCode())
                .likeIfPresent(WarehousingDO::getWarehousingName, reqVO.getWarehousingName())
                .eqIfPresent(WarehousingDO::getParentId, reqVO.getParentId())
                .eqIfPresent(WarehousingDO::getWarehousingCategoryCode, reqVO.getWarehousingCategoryCode())
                .eqIfPresent(WarehousingDO::getWarehousingAddress, reqVO.getWarehousingAddress())
                .eqIfPresent(WarehousingDO::getCompanyId, reqVO.getCompanyId())
                .eqIfPresent(WarehousingDO::getStatus, reqVO.getStatus())
                .eqIfPresent(WarehousingDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(WarehousingDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(WarehousingDO::getId));
    }

	default WarehousingDO selectByParentIdAndWarehousingName(String warehousingName) {
	    return selectOne(WarehousingDO::getWarehousingName, warehousingName);
	}

    default WarehousingDO selectByParentIdAndWarehousingCode(String warehousingCode) {
        return selectOne( WarehousingDO::getWarehousingCode, warehousingCode);
    }


    default Long selectCountByParentId(Long parentId) {
        return selectCount(WarehousingDO::getParentId, parentId);
    }

}