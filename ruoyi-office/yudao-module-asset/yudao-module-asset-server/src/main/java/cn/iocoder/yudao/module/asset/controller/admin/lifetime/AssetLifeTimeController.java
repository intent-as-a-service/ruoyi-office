package cn.iocoder.yudao.module.asset.controller.admin.lifetime;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.asset.controller.admin.lifetime.vo.*;
import cn.iocoder.yudao.module.asset.dal.dataobject.lifetime.AssetLifeTimeDO;
import cn.iocoder.yudao.module.asset.service.lifetime.AssetLifeTimeService;

@Tag(name = "管理后台 - 资产历史记录")
@RestController
@RequestMapping("/asset/life-time")
@Validated
public class AssetLifeTimeController {

    @Resource
    private AssetLifeTimeService lifeTimeService;

    @PostMapping("/create")
    @Operation(summary = "创建资产历史记录")
    @PreAuthorize("@ss.hasPermission('asset:life-time:create')")
    public CommonResult<Long> createLifeTime(@Valid @RequestBody AssetLifeTimeSaveReqVO createReqVO) {
        return success(lifeTimeService.createLifeTime(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新资产历史记录")
    @PreAuthorize("@ss.hasPermission('asset:life-time:update')")
    public CommonResult<Boolean> updateLifeTime(@Valid @RequestBody AssetLifeTimeSaveReqVO updateReqVO) {
        lifeTimeService.updateLifeTime(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除资产历史记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('asset:life-time:delete')")
    public CommonResult<Boolean> deleteLifeTime(@RequestParam("id") Long id) {
        lifeTimeService.deleteLifeTime(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除资产历史记录")
                @PreAuthorize("@ss.hasPermission('asset:life-time:delete')")
    public CommonResult<Boolean> deleteLifeTimeList(@RequestParam("ids") List<Long> ids) {
        lifeTimeService.deleteLifeTimeListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得资产历史记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('asset:life-time:query')")
    public CommonResult<AssetLifeTimeRespVO> getLifeTime(@RequestParam("id") Long id) {
        AssetLifeTimeDO lifeTime = lifeTimeService.getLifeTime(id);
        return success(BeanUtils.toBean(lifeTime, AssetLifeTimeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得资产历史记录分页")
    @PreAuthorize("@ss.hasPermission('asset:life-time:query')")
    public CommonResult<PageResult<AssetLifeTimeRespVO>> getLifeTimePage(@Valid AssetLifeTimePageReqVO pageReqVO) {
        PageResult<AssetLifeTimeDO> pageResult = lifeTimeService.getLifeTimePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AssetLifeTimeRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出资产历史记录 Excel")
    @PreAuthorize("@ss.hasPermission('asset:life-time:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportLifeTimeExcel(@Valid AssetLifeTimePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AssetLifeTimeDO> list = lifeTimeService.getLifeTimePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "资产历史记录.xls", "数据", AssetLifeTimeRespVO.class,
                        BeanUtils.toBean(list, AssetLifeTimeRespVO.class));
    }

}