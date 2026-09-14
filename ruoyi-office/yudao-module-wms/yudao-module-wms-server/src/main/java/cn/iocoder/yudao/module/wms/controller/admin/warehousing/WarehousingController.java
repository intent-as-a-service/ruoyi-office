package cn.iocoder.yudao.module.wms.controller.admin.warehousing;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
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

import cn.iocoder.yudao.module.wms.controller.admin.warehousing.vo.*;
import cn.iocoder.yudao.module.wms.dal.dataobject.warehousing.WarehousingDO;
import cn.iocoder.yudao.module.wms.service.warehousing.WarehousingService;

@Slf4j
@Tag(name = "仓库管理 - 仓库信息")
@RestController
@RequestMapping("/wms/warehousing")
@Validated
public class WarehousingController {

    @Resource
    private WarehousingService warehousingService;

    @PostMapping("/create")
    @Operation(summary = "创建仓库信息")
    @PreAuthorize("@ss.hasPermission('wms:warehousing:create')")
    public CommonResult<Long> createWarehousing(@Valid @RequestBody WarehousingSaveReqVO createReqVO) {
        return success(warehousingService.createWarehousing(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新仓库信息")
    @PreAuthorize("@ss.hasPermission('wms:warehousing:update')")
    public CommonResult<Boolean> updateWarehousing(@Valid @RequestBody WarehousingSaveReqVO updateReqVO) {
        warehousingService.updateWarehousing(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除仓库信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:warehousing:delete')")
    public CommonResult<Boolean> deleteWarehousing(@RequestParam("id") Long id) {
        warehousingService.deleteWarehousing(id);
        return success(true);
    }


    @GetMapping("/get")
    @Operation(summary = "获得仓库信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:warehousing:query')")
    public CommonResult<WarehousingRespVO> getWarehousing(@RequestParam("id") Long id) {
        WarehousingDO warehousing = warehousingService.getWarehousing(id);
        return success(BeanUtils.toBean(warehousing, WarehousingRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得仓库信息列表")
    @PreAuthorize("@ss.hasPermission('wms:warehousing:query')")
    public CommonResult<List<WarehousingRespVO>> getWarehousingList(@Valid WarehousingListReqVO listReqVO) {
        List<WarehousingDO> list = warehousingService.getWarehousingList(listReqVO);
        log.info("[getWarehousingList][请求参数：{}]", listReqVO);
        log.info("[getWarehousingList][返回结果：{}]", JSON.toJSONString( list));
        return success(BeanUtils.toBean(list, WarehousingRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出仓库信息 Excel")
    @PreAuthorize("@ss.hasPermission('wms:warehousing:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportWarehousingExcel(@Valid WarehousingListReqVO listReqVO,
              HttpServletResponse response) throws IOException {
        List<WarehousingDO> list = warehousingService.getWarehousingList(listReqVO);
        // 导出 Excel
        ExcelUtils.write(response, "仓库信息.xls", "数据", WarehousingRespVO.class,
                        BeanUtils.toBean(list, WarehousingRespVO.class));
    }

}