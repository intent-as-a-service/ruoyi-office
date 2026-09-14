package cn.iocoder.yudao.module.oa.controller.admin.seal;

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

import cn.iocoder.yudao.module.oa.controller.admin.seal.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.seal.SealDO;
import cn.iocoder.yudao.module.oa.service.seal.SealService;

@Tag(name = "OA协同办公 - 印章信息")
@RestController
@RequestMapping("/oa/seal")
@Validated
public class SealController {

    @Resource
    private SealService sealService;

    @PostMapping("/create")
    @Operation(summary = "创建印章信息")
    @PreAuthorize("@ss.hasPermission('oa:seal:create')")
    public CommonResult<Long> createSeal(@Valid @RequestBody SealSaveReqVO createReqVO) {
        return success(sealService.createSeal(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新印章信息")
    @PreAuthorize("@ss.hasPermission('oa:seal:update')")
    public CommonResult<Boolean> updateSeal(@Valid @RequestBody SealSaveReqVO updateReqVO) {
        sealService.updateSeal(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除印章信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:seal:delete')")
    public CommonResult<Boolean> deleteSeal(@RequestParam("id") Long id) {
        sealService.deleteSeal(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除印章信息")
    @PreAuthorize("@ss.hasPermission('oa:seal:delete')")
    public CommonResult<Boolean> deleteSealList(@RequestParam("ids") List<Long> ids) {
        sealService.deleteSealListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得印章信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:seal:query')")
    public CommonResult<SealRespVO> getSeal(@RequestParam("id") Long id) {
        SealDO seal = sealService.getSeal(id);
        return success(BeanUtils.toBean(seal, SealRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得印章信息分页")
    @PreAuthorize("@ss.hasPermission('oa:seal:query')")
    public CommonResult<PageResult<SealRespVO>> getSealPage(@Valid SealPageReqVO pageReqVO) {
        PageResult<SealDO> pageResult = sealService.getSealPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SealRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出印章信息 Excel")
    @PreAuthorize("@ss.hasPermission('oa:seal:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSealExcel(@Valid SealPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SealDO> list = sealService.getSealPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "印章信息.xls", "数据", SealRespVO.class,
                        BeanUtils.toBean(list, SealRespVO.class));
    }

}

