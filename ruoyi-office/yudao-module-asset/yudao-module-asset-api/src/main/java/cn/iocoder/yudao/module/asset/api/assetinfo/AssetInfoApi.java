package cn.iocoder.yudao.module.asset.api.assetinfo;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.asset.api.assetinfo.dto.AssetInfoReqDTO;
import cn.iocoder.yudao.module.asset.enums.ApiConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 资产服务 - 资产信息")
public interface AssetInfoApi {


    String PREFIX = ApiConstants.PREFIX + "/info";

    @GetMapping(PREFIX + "/batchSaveInfo")
    @Operation(summary = "批量保存资产信息")
    CommonResult<Boolean> batchSaveInfo(@RequestBody List<AssetInfoReqDTO> reqDTOList);
}