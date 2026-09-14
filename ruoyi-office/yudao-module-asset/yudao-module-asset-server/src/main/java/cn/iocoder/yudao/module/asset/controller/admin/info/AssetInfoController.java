package cn.iocoder.yudao.module.asset.controller.admin.info;

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

import cn.iocoder.yudao.module.asset.controller.admin.info.vo.*;
import cn.iocoder.yudao.module.asset.dal.dataobject.info.AssetInfoDO;
import cn.iocoder.yudao.module.asset.service.info.AssetInfoService;

@Tag(name = "管理后台 - 资产信息")
@RestController
@RequestMapping("/asset/info")
@Validated
public class AssetInfoController {

    @Resource
    private AssetInfoService infoService;

    @PostMapping("/create")
    @Operation(summary = "创建资产信息")
    @PreAuthorize("@ss.hasPermission('asset:info:create')")
    public CommonResult<Long> createInfo(@Valid @RequestBody AssetInfoSaveReqVO createReqVO) {
        return success(infoService.createInfo(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新资产信息")
    @PreAuthorize("@ss.hasPermission('asset:info:update')")
    public CommonResult<Boolean> updateInfo(@Valid @RequestBody AssetInfoSaveReqVO updateReqVO) {
        infoService.updateInfo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除资产信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('asset:info:delete')")
    public CommonResult<Boolean> deleteInfo(@RequestParam("id") Long id) {
        infoService.deleteInfo(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除资产信息")
                @PreAuthorize("@ss.hasPermission('asset:info:delete')")
    public CommonResult<Boolean> deleteInfoList(@RequestParam("ids") List<Long> ids) {
        infoService.deleteInfoListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得资产信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('asset:info:query')")
    public CommonResult<AssetInfoRespVO> getInfo(@RequestParam("id") Long id) {
        AssetInfoDO info = infoService.getInfo(id);
        return success(BeanUtils.toBean(info, AssetInfoRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得资产信息分页")
    @PreAuthorize("@ss.hasPermission('asset:info:query')")
    public CommonResult<PageResult<AssetInfoRespVO>> getInfoPage(@Valid AssetInfoPageReqVO pageReqVO) {
        PageResult<AssetInfoDO> pageResult = infoService.getInfoPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AssetInfoRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出资产信息 Excel")
    @PreAuthorize("@ss.hasPermission('asset:info:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportInfoExcel(@Valid AssetInfoPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AssetInfoDO> list = infoService.getInfoPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "资产信息.xls", "数据", AssetInfoRespVO.class,
                        BeanUtils.toBean(list, AssetInfoRespVO.class));
    }

}