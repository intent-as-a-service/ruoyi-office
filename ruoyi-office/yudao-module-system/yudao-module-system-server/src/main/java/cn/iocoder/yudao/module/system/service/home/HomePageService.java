package cn.iocoder.yudao.module.system.service.home;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.page.HomePageLayoutSaveReqVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.page.HomePagePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.page.HomePageSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomePageDO;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomePageLayoutDO;

import java.util.List;

/**
 * 首页配置 Service 接口
 *
 * @author 宇擎源码
 */
public interface HomePageService {

    /**
     * 创建首页
     *
     * @param createReqVO 首页信息
     * @return 首页编号
     */
    Long createHomePage(HomePageSaveReqVO createReqVO);

    /**
     * 更新首页
     *
     * @param updateReqVO 首页信息
     */
    void updateHomePage(HomePageSaveReqVO updateReqVO);

    /**
     * 删除首页
     *
     * @param id 首页编号
     */
    void deleteHomePage(Long id);

    /**
     * 获取首页详情
     *
     * @param id 首页编号
     * @return 首页信息
     */
    HomePageDO getHomePage(Long id);

    /**
     * 获取首页分页
     *
     * @param pageReqVO 分页查询条件
     * @return 首页分页列表
     */
    PageResult<HomePageDO> getHomePagePage(HomePagePageReqVO pageReqVO);

    /**
     * 获取默认首页
     *
     * @return 默认首页
     */
    HomePageDO getDefaultHomePage();

    /**
     * 设置默认首页
     *
     * @param id 首页编号
     */
    void setMyHomePage(Long id);

    /**
     * 获取用户启用的首页（如果用户未配置，返回默认首页）
     *
     * @param userId 用户ID
     * @return 用户首页
     */
    HomePageDO getUserHomePage(Long userId);

    /**
     * 用户启用首页
     *
     * @param userId 用户ID
     * @param pageId 首页ID
     */
    void enableUserHomePage(Long userId, Long pageId);

    /**
     * 获取简单首页列表（供用户选择）
     *
     * @return 首页列表
     */
    List<HomePageDO> getSimpleHomePageList();

    /**
     * 保存首页布局（批量保存布局项）
     *
     * @param saveReqVO 布局保存请求
     */
    void saveHomePageLayout(HomePageLayoutSaveReqVO saveReqVO);

    /**
     * 获取首页布局列表
     *
     * @param pageId 首页ID
     * @return 布局列表
     */
    List<HomePageLayoutDO> getHomePageLayoutList(Long pageId);

    /**
     * 删除首页所有布局
     *
     * @param pageId 首页ID
     */
    void deleteHomePageLayouts(Long pageId);

}
