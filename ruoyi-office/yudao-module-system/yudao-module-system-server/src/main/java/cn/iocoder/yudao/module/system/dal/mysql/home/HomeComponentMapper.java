package cn.iocoder.yudao.module.system.dal.mysql.home;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.component.HomeComponentPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomeComponentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 首页组件 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface HomeComponentMapper extends BaseMapperX<HomeComponentDO> {

    default PageResult<HomeComponentDO> selectPage(HomeComponentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<HomeComponentDO>()
                .likeIfPresent(HomeComponentDO::getName, reqVO.getName())
                .eqIfPresent(HomeComponentDO::getCode, reqVO.getCode())
                .eqIfPresent(HomeComponentDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(HomeComponentDO::getStatus, reqVO.getStatus())
                .orderByAsc(HomeComponentDO::getSort)
                .orderByDesc(HomeComponentDO::getId));
    }

    default HomeComponentDO selectByCode(String code) {
        return selectOne(HomeComponentDO::getCode, code);
    }

    default List<HomeComponentDO> selectByCategoryId(Long categoryId) {
        return selectList(HomeComponentDO::getCategoryId, categoryId);
    }

}
