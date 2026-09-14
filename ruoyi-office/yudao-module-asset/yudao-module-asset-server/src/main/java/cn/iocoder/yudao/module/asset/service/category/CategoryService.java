package cn.iocoder.yudao.module.asset.service.category;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.asset.controller.admin.category.vo.*;
import cn.iocoder.yudao.module.asset.dal.dataobject.category.CategoryDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 资产类别 Service 接口
 *
 * @author 宇擎源码
 */
public interface CategoryService {

    /**
     * 创建资产类别
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCategory(@Valid CategorySaveReqVO createReqVO);

    /**
     * 更新资产类别
     *
     * @param updateReqVO 更新信息
     */
    void updateCategory(@Valid CategorySaveReqVO updateReqVO);

    /**
     * 删除资产类别
     *
     * @param id 编号
     */
    void deleteCategory(Long id);


    /**
     * 获得资产类别
     *
     * @param id 编号
     * @return 资产类别
     */
    CategoryDO getCategory(Long id);

    /**
     * 获得资产类别列表
     *
     * @param listReqVO 查询条件
     * @return 资产类别列表
     */
    List<CategoryDO> getCategoryList(CategoryListReqVO listReqVO);

}