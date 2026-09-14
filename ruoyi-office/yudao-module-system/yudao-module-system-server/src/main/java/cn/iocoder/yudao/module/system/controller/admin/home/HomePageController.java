package cn.iocoder.yudao.module.system.controller.admin.home;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.page.HomePageLayoutRespVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.page.HomePageLayoutSaveReqVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.page.HomePagePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.page.HomePageRespVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.page.HomePageSaveReqVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.page.HomePageSimpleRespVO;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomePageDO;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomePageLayoutDO;
import cn.iocoder.yudao.module.system.service.home.HomePageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 首页管理
 *
 * @author 宇擎源码
 */
@Tag(name = "管理后台 - 首页管理")
@RestController
@RequestMapping("/system/home/page")
@Validated
public class HomePageController {

    @Resource
    private HomePageService homePageService;

    @PostMapping("/create")
    @Operation(summary = "创建首页")
    @PreAuthorize("@ss.hasPermission('system:home:create')")
    public CommonResult<Long> createHomePage(@Valid @RequestBody HomePageSaveReqVO createReqVO) {
        return success(homePageService.createHomePage(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新首页")
    @PreAuthorize("@ss.hasPermission('system:home:update')")
    public CommonResult<Boolean> updateHomePage(@Valid @RequestBody HomePageSaveReqVO updateReqVO) {
        // 如果是 default_workspace，需要管理员权限（通过权限注解控制）
        // 其他首页：在 Service 层检查是否是创建者
        homePageService.updateHomePage(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除首页")
    @Parameter(name = "id", description = "首页编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:home:delete')")
    public CommonResult<Boolean> deleteHomePage(@RequestParam("id") Long id) {
        homePageService.deleteHomePage(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得首页")
    @Parameter(name = "id", description = "首页编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:home:query')")
    public CommonResult<HomePageRespVO> getHomePage(@RequestParam("id") Long id) {
        HomePageDO homePage = homePageService.getHomePage(id);
        return success(BeanUtils.toBean(homePage, HomePageRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得首页分页")
    @PreAuthorize("@ss.hasPermission('system:home:query')")
    public CommonResult<PageResult<HomePageRespVO>> getHomePagePage(@Valid HomePagePageReqVO pageReqVO) {
        // 查询当前用户首页
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        HomePageDO userHomePage = homePageService.getUserHomePage(userId);
        Long usingPageId = userHomePage != null ? userHomePage.getId() : null;

        // 查询分页列表
        PageResult<HomePageDO> pageResult = homePageService.getHomePagePage(pageReqVO);
        PageResult<HomePageRespVO> voResult = BeanUtils.toBean(pageResult, HomePageRespVO.class);

        // 标记当前用户正在使用的首页
        if (usingPageId != null && voResult.getList() != null) {
            voResult.getList().forEach(vo -> {
                if (usingPageId.equals(vo.getId())) {
                    vo.setUseStatus("使用中");
                }
            });
        }
        return success(voResult);
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获取简单首页列表", description = "只包含被开启的首页，主要用于前端的下拉选项")
    public CommonResult<List<HomePageSimpleRespVO>> getSimpleHomePageList() {
        List<HomePageDO> list = homePageService.getSimpleHomePageList();
        return success(BeanUtils.toBean(list, HomePageSimpleRespVO.class));
    }

    @GetMapping("/my-home")
    @Operation(summary = "获取当前用户的首页")
    public CommonResult<HomePageRespVO> getMyHomePage() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        HomePageDO homePage = homePageService.getUserHomePage(userId);
        return success(BeanUtils.toBean(homePage, HomePageRespVO.class));
    }

    @PostMapping("/enable")
    @Operation(summary = "启用首页")
    @Parameter(name = "pageId", description = "首页编号", required = true, example = "1024")
    public CommonResult<Boolean> enableHomePage(@RequestParam("pageId") Long pageId) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        homePageService.enableUserHomePage(userId, pageId);
        return success(true);
    }

    @PutMapping("/set-my-home")
    @Operation(summary = "设置为我的首页")
    @Parameter(name = "id", description = "首页编号", required = true, example = "1024")
    public CommonResult<Boolean> setMyHomePage(@RequestParam("id") Long id) {
        homePageService.setMyHomePage(id);
        return success(true);
    }

    @PostMapping("/layout/save")
    @Operation(summary = "保存首页布局")
    @PreAuthorize("@ss.hasPermission('system:home:update')")
    public CommonResult<Boolean> saveHomePageLayout(@Valid @RequestBody HomePageLayoutSaveReqVO saveReqVO) {
        // 权限检查：
        // 1. 如果是 default_workspace，需要管理员权限（通过 @PreAuthorize 注解控制）
        // 2. 其他首页：在 Service 层检查是否是创建者
        homePageService.saveHomePageLayout(saveReqVO);
        return success(true);
    }

    @GetMapping("/layout/list")
    @Operation(summary = "获取首页布局列表")
    @Parameter(name = "pageId", description = "首页编号", required = true, example = "1024")
    public CommonResult<List<HomePageLayoutRespVO>> getHomePageLayoutList(@RequestParam("pageId") Long pageId) {
        List<HomePageLayoutDO> list = homePageService.getHomePageLayoutList(pageId);
        return success(BeanUtils.toBean(list, HomePageLayoutRespVO.class));
    }

}
