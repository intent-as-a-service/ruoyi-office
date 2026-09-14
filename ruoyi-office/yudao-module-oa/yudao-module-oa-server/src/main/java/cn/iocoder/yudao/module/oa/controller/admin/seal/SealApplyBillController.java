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
import cn.iocoder.yudao.module.oa.dal.dataobject.seal.SealApplyBillDO;
import cn.iocoder.yudao.module.oa.service.seal.SealApplyBillService;

@Tag(name = "管理后台 - 用印申请单")
@RestController
@RequestMapping("/oa/seal-apply-bill")
@Validated
public class SealApplyBillController {

    @Resource
    private SealApplyBillService sealApplyBillService;

    @PostMapping("/create")
    @Operation(summary = "创建用印申请单")
    @PreAuthorize("@ss.hasPermission('oa:seal-apply-bill:create')")
    public CommonResult<Long> createSealApplyBill(@Valid @RequestBody SealApplyBillSaveReqVO createReqVO) {
        return success(sealApplyBillService.createSealApplyBill(createReqVO));
    }

    @PostMapping("/save")
    @Operation(summary = "保存用印申请单")
    @PreAuthorize("@ss.hasPermission('oa:seal-apply-bill:create')")
    public CommonResult<Long> saveSealApplyBill(@Valid @RequestBody SealApplyBillSaveReqVO saveReqVO) {
        return success(sealApplyBillService.saveSealApplyBill(saveReqVO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交用印申请单")
    @PreAuthorize("@ss.hasPermission('oa:seal-apply-bill:create')")
    public CommonResult<Long> submitSealApplyBill(@Valid @RequestBody SealApplyBillSaveReqVO submitReqVO) {
        return success(sealApplyBillService.submitSealApplyBill(submitReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用印申请单")
    @PreAuthorize("@ss.hasPermission('oa:seal-apply-bill:update')")
    public CommonResult<Boolean> updateSealApplyBill(@Valid @RequestBody SealApplyBillSaveReqVO updateReqVO) {
        sealApplyBillService.updateSealApplyBill(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用印申请单")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:seal-apply-bill:delete')")
    public CommonResult<Boolean> deleteSealApplyBill(@RequestParam("id") Long id) {
        sealApplyBillService.deleteSealApplyBill(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除用印申请单")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('oa:seal-apply-bill:delete')")
    public CommonResult<Boolean> deleteSealApplyBillList(@RequestParam("ids") List<Long> ids) {
        sealApplyBillService.deleteSealApplyBillListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用印申请单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:seal-apply-bill:query')")
    public CommonResult<SealApplyBillRespVO> getSealApplyBill(@RequestParam("id") Long id) {
        SealApplyBillRespVO respVO = sealApplyBillService.getSealApplyBillInfo(id);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得用印申请单分页")
    @PreAuthorize("@ss.hasPermission('oa:seal-apply-bill:query')")
    public CommonResult<PageResult<SealApplyBillRespVO>> getSealApplyBillPage(@Valid SealApplyBillPageReqVO pageReqVO) {
        PageResult<SealApplyBillDO> pageResult = sealApplyBillService.getSealApplyBillPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SealApplyBillRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用印申请单 Excel")
    @PreAuthorize("@ss.hasPermission('oa:seal-apply-bill:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSealApplyBillExcel(@Valid SealApplyBillPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SealApplyBillDO> list = sealApplyBillService.getSealApplyBillPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用印申请单.xls", "数据", SealApplyBillRespVO.class,
                        BeanUtils.toBean(list, SealApplyBillRespVO.class));
    }

}
