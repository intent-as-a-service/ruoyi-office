package cn.iocoder.yudao.module.system.controller.admin.home;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.component.HomeComponentCategoryRespVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.component.HomeComponentPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.component.HomeComponentRespVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.component.HomeComponentSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomeComponentCategoryDO;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomeComponentDO;
import cn.iocoder.yudao.module.system.service.home.HomeComponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 首页组件管理
 *
 * @author 宇擎源码
 */
@Tag(name = "管理后台 - 首页组件管理")
@RestController
@RequestMapping("/system/home/component")
@Validated
public class HomeComponentController {

    @Resource
    private HomeComponentService componentService;

    @PostMapping("/create")
    @Operation(summary = "创建组件")
    @PreAuthorize("@ss.hasPermission('system:home-component:create')")
    public CommonResult<Long> createComponent(@Valid @RequestBody HomeComponentSaveReqVO createReqVO) {
        return success(componentService.createComponent(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新组件")
    @PreAuthorize("@ss.hasPermission('system:home-component:update')")
    public CommonResult<Boolean> updateComponent(@Valid @RequestBody HomeComponentSaveReqVO updateReqVO) {
        componentService.updateComponent(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除组件")
    @Parameter(name = "id", description = "组件编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:home-component:delete')")
    public CommonResult<Boolean> deleteComponent(@RequestParam("id") Long id) {
        componentService.deleteComponent(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得组件")
    @Parameter(name = "id", description = "组件编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:home-component:query')")
    public CommonResult<HomeComponentRespVO> getComponent(@RequestParam("id") Long id) {
        HomeComponentDO component = componentService.getComponent(id);
        return success(BeanUtils.toBean(component, HomeComponentRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得组件分页")
    @PreAuthorize("@ss.hasPermission('system:home-component:query')")
    public CommonResult<PageResult<HomeComponentRespVO>> getComponentPage(@Valid HomeComponentPageReqVO pageReqVO) {
        PageResult<HomeComponentDO> pageResult = componentService.getComponentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, HomeComponentRespVO.class));
    }

    @GetMapping("/available-list")
    @Operation(summary = "获取可用组件列表", description = "供首页设计器使用")
    public CommonResult<List<HomeComponentRespVO>> getAvailableComponentList() {
        List<HomeComponentDO> list = componentService.getAvailableComponentList();
        return success(BeanUtils.toBean(list, HomeComponentRespVO.class));
    }

    @GetMapping("/by-category")
    @Operation(summary = "按分类获取组件")
    public CommonResult<Map<Long, List<HomeComponentRespVO>>> getComponentsByCategory() {
        Map<Long, List<HomeComponentDO>> map = componentService.getComponentsByCategory();
        // 转换为VO
        Map<Long, List<HomeComponentRespVO>> result = new HashMap<>();
        map.forEach((k, v) -> result.put(k, BeanUtils.toBean(v, HomeComponentRespVO.class)));
        return success(result);
    }

    @GetMapping("/category/list")
    @Operation(summary = "获取组件分类列表")
    public CommonResult<List<HomeComponentCategoryRespVO>> getCategoryList() {
        List<HomeComponentCategoryDO> list = componentService.getCategoryList();
        return success(BeanUtils.toBean(list, HomeComponentCategoryRespVO.class));
    }

}
