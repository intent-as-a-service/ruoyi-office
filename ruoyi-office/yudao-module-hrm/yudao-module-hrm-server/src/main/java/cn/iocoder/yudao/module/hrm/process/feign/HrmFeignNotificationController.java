package cn.iocoder.yudao.module.hrm.process.feign;

import cn.iocoder.yudao.common.server.process.controller.AbstractFlowNotificationController;
import cn.iocoder.yudao.framework.common.enums.SystemEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.service.FlowBillServiceFactory;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusMessage;
import cn.iocoder.yudao.module.hrm.enums.ApiConstants;
import cn.iocoder.yudao.module.hrm.enums.HrmBillTypeEnum;
import cn.iocoder.yudao.module.hrm.service.HrmFlowBillServiceFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HRM 模块 BPM 回调（Feign）
 *
 * @author 宇擎
 */
@Tag(name = "管理后台 - HRM流程回调")
@RestController
@RequestMapping(ApiConstants.PREFIX + "/process-callback")
@Validated
@Slf4j
public class HrmFeignNotificationController extends AbstractFlowNotificationController<HrmBillTypeEnum> {

    @Resource
    private HrmFlowBillServiceFactory flowBillServiceFactory;

    @Override
    protected SystemEnum getSystem() {
        return SystemEnum.HRM;
    }

    @Override
    protected FlowBillServiceFactory<HrmBillTypeEnum> getFlowBillServiceFactory() {
        return flowBillServiceFactory;
    }

    @PostMapping("/bpm-event")
    @Operation(summary = "接收BPM事件回调（支持流程实例和任务事件）")
    public CommonResult<Boolean> handleBpmEvent(@RequestBody BpmProcessInstanceStatusMessage message) {
        return doHandleBpmEvent(message);
    }

    @PostMapping("/status-change")
    @Deprecated
    @Operation(summary = "接收流程状态变化回调（兼容旧版本）")
    public CommonResult<Boolean> processStatusChange(@RequestBody BpmProcessInstanceStatusMessage message) {
        return doHandleProcessStatusChange(message);
    }
}

