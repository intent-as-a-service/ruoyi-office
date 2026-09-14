package cn.iocoder.yudao.module.system.dal.mysql.home;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.home.UserHomePageDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户首页关联 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface UserHomePageMapper extends BaseMapperX<UserHomePageDO> {

    default UserHomePageDO selectByUserId(Long userId) {
        return selectOne(UserHomePageDO::getUserId, userId);
    }

}
