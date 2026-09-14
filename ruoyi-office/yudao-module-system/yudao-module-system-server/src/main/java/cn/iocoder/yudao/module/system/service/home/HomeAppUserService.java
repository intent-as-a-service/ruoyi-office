package cn.iocoder.yudao.module.system.service.home;

import cn.iocoder.yudao.module.system.controller.admin.home.vo.app.HomeAppUserCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.app.HomeAppUserRespVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.app.HomeAppUserUpdateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.app.HomeAppUserUpdateSortReqVO;

import java.util.List;

/**
 * 用户应用配置 Service 接口
 *
 * @author 宇擎源码
 */
public interface HomeAppUserService {

    /**
     * 获取当前用户的应用列表
     *
     * @return 应用列表
     */
    List<HomeAppUserRespVO> getMyAppList();

    /**
     * 新增用户应用
     *
     * @param createReqVO 创建信息
     * @return 应用ID
     */
    Long createUserApp(HomeAppUserCreateReqVO createReqVO);

    /**
     * 更新用户应用
     *
     * @param updateReqVO 更新信息
     */
    void updateUserApp(HomeAppUserUpdateReqVO updateReqVO);

    /**
     * 删除用户应用
     *
     * @param id 应用ID
     */
    void deleteUserApp(Long id);

    /**
     * 批量更新用户应用排序
     *
     * @param sortList 排序列表
     */
    void updateUserAppSort(List<HomeAppUserUpdateSortReqVO> sortList);

    /**
     * 初始化用户应用（从系统配置复制）
     */
    void initUserApp();

    /**
     * 重置用户应用（恢复为系统默认）
     */
    void resetUserApp();

}
