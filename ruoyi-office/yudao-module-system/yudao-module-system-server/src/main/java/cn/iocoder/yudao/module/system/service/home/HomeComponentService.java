package cn.iocoder.yudao.module.system.service.home;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.component.HomeComponentPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.component.HomeComponentSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomeComponentCategoryDO;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomeComponentDO;

import java.util.List;
import java.util.Map;

/**
 * 首页组件 Service 接口
 *
 * @author 宇擎源码
 */
public interface HomeComponentService {

    /**
     * 创建组件
     *
     * @param createReqVO 组件信息
     * @return 组件编号
     */
    Long createComponent(HomeComponentSaveReqVO createReqVO);

    /**
     * 更新组件
     *
     * @param updateReqVO 组件信息
     */
    void updateComponent(HomeComponentSaveReqVO updateReqVO);

    /**
     * 删除组件
     *
     * @param id 组件编号
     */
    void deleteComponent(Long id);

    /**
     * 获取组件详情
     *
     * @param id 组件编号
     * @return 组件信息
     */
    HomeComponentDO getComponent(Long id);

    /**
     * 获取组件分页
     *
     * @param pageReqVO 分页查询条件
     * @return 组件分页列表
     */
    PageResult<HomeComponentDO> getComponentPage(HomeComponentPageReqVO pageReqVO);

    /**
     * 获取可用组件列表（供设计器使用）
     *
     * @return 可用组件列表
     */
    List<HomeComponentDO> getAvailableComponentList();

    /**
     * 按分类获取组件
     *
     * @return 分类ID -> 组件列表
     */
    Map<Long, List<HomeComponentDO>> getComponentsByCategory();

    /**
     * 获取组件分类列表
     *
     * @return 分类列表
     */
    List<HomeComponentCategoryDO> getCategoryList();

    /**
     * 获取组件分类详情
     *
     * @param id 分类编号
     * @return 分类信息
     */
    HomeComponentCategoryDO getCategory(Long id);

}
