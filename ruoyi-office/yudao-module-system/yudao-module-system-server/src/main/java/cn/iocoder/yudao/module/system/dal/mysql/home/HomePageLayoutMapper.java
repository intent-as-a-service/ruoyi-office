package cn.iocoder.yudao.module.system.dal.mysql.home;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomePageLayoutDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 首页布局配置 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface HomePageLayoutMapper extends BaseMapperX<HomePageLayoutDO> {

    default HomePageLayoutDO selectByPageId(Long pageId) {
        return selectOne(HomePageLayoutDO::getPageId, pageId);
    }

    default List<HomePageLayoutDO> selectListByPageId(Long pageId) {
        return selectList(new LambdaQueryWrapperX<HomePageLayoutDO>()
                .eq(HomePageLayoutDO::getPageId, pageId)
                .orderByAsc(HomePageLayoutDO::getSort));
    }

    default void deleteByPageId(Long pageId) {
        delete(new LambdaQueryWrapperX<HomePageLayoutDO>()
                .eq(HomePageLayoutDO::getPageId, pageId));
    }

}
