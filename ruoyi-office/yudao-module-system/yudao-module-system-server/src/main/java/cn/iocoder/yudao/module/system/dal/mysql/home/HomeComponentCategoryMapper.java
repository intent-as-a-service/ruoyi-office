package cn.iocoder.yudao.module.system.dal.mysql.home;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomeComponentCategoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 首页组件分类 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface HomeComponentCategoryMapper extends BaseMapperX<HomeComponentCategoryDO> {

    default HomeComponentCategoryDO selectByCode(String code) {
        return selectOne(HomeComponentCategoryDO::getCode, code);
    }

    default List<HomeComponentCategoryDO> selectAllOrdered() {
        return selectList(new LambdaQueryWrapperX<HomeComponentCategoryDO>()
                .orderByAsc(HomeComponentCategoryDO::getSort));
    }

}
