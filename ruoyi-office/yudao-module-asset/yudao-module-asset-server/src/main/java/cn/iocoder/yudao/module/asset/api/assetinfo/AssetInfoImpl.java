package cn.iocoder.yudao.module.asset.api.assetinfo;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.asset.api.assetinfo.dto.AssetInfoReqDTO;
import cn.iocoder.yudao.module.asset.controller.admin.info.vo.AssetInfoSaveReqVO;
import cn.iocoder.yudao.module.asset.service.info.AssetInfoService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;


@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class AssetInfoImpl implements AssetInfoApi {

    @Resource
    private AssetInfoService assetInfoService;

    /**
     * 批量保存资产信息
     *
     * @param reqDTOList 资产信息
     * @return 批量保存结果
     */
    @Override
    public CommonResult<Boolean> batchSaveInfo(List<AssetInfoReqDTO> reqDTOList) {
        List<AssetInfoSaveReqVO> createReqVOList = BeanUtils.toBean(reqDTOList, AssetInfoSaveReqVO.class);
        return CommonResult.success(assetInfoService.batchSave(createReqVOList));
    }
}
