package cn.iocoder.yudao.module.system.dal.mysql.home;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomeAppConfigDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统级应用配置 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface HomeAppConfigMapper extends BaseMapperX<HomeAppConfigDO> {

    /**
     * 查询已启用的系统应用配置列表
     *
     * @param status 状态
     * @return 应用配置列表
     */
    default List<HomeAppConfigDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<HomeAppConfigDO>()
                .eq(HomeAppConfigDO::getStatus, status)
                .orderByAsc(HomeAppConfigDO::getSort)
                .orderByDesc(HomeAppConfigDO::getId));
    }

    /**
     * 根据菜单ID查询应用配置
     *
     * @param menuId 菜单ID
     * @return 应用配置
     */
    default HomeAppConfigDO selectByMenuId(Long menuId) {
        return selectOne(HomeAppConfigDO::getMenuId, menuId);
    }

}

