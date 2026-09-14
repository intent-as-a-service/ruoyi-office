package cn.iocoder.yudao.module.system.controller.admin.home;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.app.HomeAppUserCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.app.HomeAppUserRespVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.app.HomeAppUserUpdateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.app.HomeAppUserUpdateSortReqVO;
import cn.iocoder.yudao.module.system.service.home.HomeAppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 用户应用配置 Controller
 *
 * @author 宇擎源码
 */
@Tag(name = "管理后台 - 用户应用配置")
@RestController
@RequestMapping("/system/home-app-user")
@Validated
public class HomeAppUserController {

    @Resource
    private HomeAppUserService appUserService;

    @GetMapping("/my-list")
    @Operation(summary = "获取我的应用列表")
    public CommonResult<List<HomeAppUserRespVO>> getMyAppList() {
        List<HomeAppUserRespVO> list = appUserService.getMyAppList();
        return success(list);
    }

    @PostMapping("/create")
    @Operation(summary = "新增用户应用")
    public CommonResult<Long> createUserApp(@Valid @RequestBody HomeAppUserCreateReqVO createReqVO) {
        Long id = appUserService.createUserApp(createReqVO);
        return success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户应用")
    public CommonResult<Boolean> updateUserApp(@Valid @RequestBody HomeAppUserUpdateReqVO updateReqVO) {
        appUserService.updateUserApp(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户应用")
    public CommonResult<Boolean> deleteUserApp(@RequestParam("id") Long id) {
        appUserService.deleteUserApp(id);
        return success(true);
    }

    @PutMapping("/update-sort")
    @Operation(summary = "批量更新用户应用排序")
    public CommonResult<Boolean> updateUserAppSort(@Valid @RequestBody List<HomeAppUserUpdateSortReqVO> sortList) {
        appUserService.updateUserAppSort(sortList);
        return success(true);
    }

    @PostMapping("/init")
    @Operation(summary = "初始化用户应用（从系统配置复制）")
    public CommonResult<Boolean> initUserApp() {
        appUserService.initUserApp();
        return success(true);
    }

    @PostMapping("/reset")
    @Operation(summary = "重置用户应用（恢复为系统默认）")
    public CommonResult<Boolean> resetUserApp() {
        appUserService.resetUserApp();
        return success(true);
    }

}

