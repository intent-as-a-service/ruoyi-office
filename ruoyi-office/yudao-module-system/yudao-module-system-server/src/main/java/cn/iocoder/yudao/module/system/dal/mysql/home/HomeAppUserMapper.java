package cn.iocoder.yudao.module.system.dal.mysql.home;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomeAppUserDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户级应用配置 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface HomeAppUserMapper extends BaseMapperX<HomeAppUserDO> {

    /**
     * 查询用户的应用配置列表
     *
     * @param userId 用户ID
     * @return 应用配置列表
     */
    default List<HomeAppUserDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<HomeAppUserDO>()
                .eq(HomeAppUserDO::getUserId, userId)
                .orderByAsc(HomeAppUserDO::getSort)
                .orderByDesc(HomeAppUserDO::getId));
    }

    /**
     * 查询用户的应用配置列表（仅显示状态）
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 应用配置列表
     */
    default List<HomeAppUserDO> selectListByUserIdAndStatus(Long userId, Integer status) {
        return selectList(new LambdaQueryWrapperX<HomeAppUserDO>()
                .eq(HomeAppUserDO::getUserId, userId)
                .eq(HomeAppUserDO::getStatus, status)
                .orderByAsc(HomeAppUserDO::getSort)
                .orderByDesc(HomeAppUserDO::getId));
    }

    /**
     * 查询用户的某个菜单应用配置
     *
     * @param userId 用户ID
     * @param menuId 菜单ID
     * @return 应用配置
     */
    default HomeAppUserDO selectByUserIdAndMenuId(Long userId, Long menuId) {
        return selectOne(new LambdaQueryWrapperX<HomeAppUserDO>()
                .eq(HomeAppUserDO::getUserId, userId)
                .eq(HomeAppUserDO::getMenuId, menuId));
    }

    /**
     * 删除用户的所有应用配置
     *
     * @param userId 用户ID
     * @return 删除数量
     */
    default int deleteByUserId(Long userId) {
        return delete(new LambdaQueryWrapperX<HomeAppUserDO>()
                .eq(HomeAppUserDO::getUserId, userId));
    }

}
