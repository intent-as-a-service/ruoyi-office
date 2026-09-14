package cn.iocoder.yudao.module.asset.dal.mysql.category;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.asset.dal.dataobject.category.CategoryDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.asset.controller.admin.category.vo.*;

/**
 * 资产类别 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface CategoryMapper extends BaseMapperX<CategoryDO> {

    default List<CategoryDO> selectList(CategoryListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<CategoryDO>()
                .eqIfPresent(CategoryDO::getCategoryCode, reqVO.getCategoryCode())
                .likeIfPresent(CategoryDO::getCategoryName, reqVO.getCategoryName())
                .eqIfPresent(CategoryDO::getParentId, reqVO.getParentId())
                .eqIfPresent(CategoryDO::getLevel, reqVO.getLevel())
                .eqIfPresent(CategoryDO::getSort, reqVO.getSort())
                .eqIfPresent(CategoryDO::getStatus, reqVO.getStatus())
                .eqIfPresent(CategoryDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(CategoryDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CategoryDO::getId));
    }

	default CategoryDO selectByParentIdAndCategoryName(String categoryName) {
	    return selectOne(CategoryDO::getCategoryName, categoryName);
	}
    default CategoryDO selectByCategoryCode(String categoryCode) {
        return selectOne(CategoryDO::getCategoryCode, categoryCode);
    }
    default Long selectCountByParentId(Long parentId) {
        return selectCount(CategoryDO::getParentId, parentId);
    }

}