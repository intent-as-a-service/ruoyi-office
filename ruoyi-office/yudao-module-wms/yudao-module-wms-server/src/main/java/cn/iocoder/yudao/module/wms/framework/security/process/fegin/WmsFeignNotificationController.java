package cn.iocoder.yudao.module.wms.framework.security.process.fegin;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusMessage;
import cn.iocoder.yudao.module.wms.enums.ApiConstants;
import cn.iocoder.yudao.module.wms.framework.security.enums.FlowCodeEnum;
import cn.iocoder.yudao.module.wms.framework.security.service.FlowProcessRespDTO;
import cn.iocoder.yudao.module.wms.framework.security.service.IBillBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * WMS 流程回调 Controller
 * 接收来自BPM服务的Feign调用
 *
 * @author 宇擎源码
 */
@Tag(name = "管理后台 - WMS流程回调")
@RestController
@RequestMapping(ApiConstants.PREFIX + "/process-callback")
@Validated
@Slf4j
public class WmsFeignNotificationController {

    private Map<String, IBillBizService> serviceMap;

    @Autowired
    public void businessServiceFactory(List<IBillBizService> services) {
        this.serviceMap = services.stream().collect(Collectors.toMap(service -> AopUtils.getTargetClass(service).getSimpleName(), Function.identity()));
    }
    @PostMapping("/status-change")
    @Operation(summary = "接收流程状态变化回调")
    public CommonResult<Boolean> processStatusChange(@RequestBody BpmProcessInstanceStatusMessage message) {
        log.info("[processStatusChange][Feign回调] 收到流程状态变化回调: {}", message);

        try {
            String processDefinitionKey = message.getProcessDefinitionKey();
            String businessKey = message.getBusinessKey();
            Integer status = message.getProcessInstanceInfo().getStatus();

            // 参数校验
            if (StrUtil.isBlank(processDefinitionKey) || StrUtil.isBlank(businessKey) || status == null) {
                log.warn("[processStatusChange] 参数不完整，processDefinitionKey: {}, businessKey: {}, status: {}",
                        processDefinitionKey, businessKey, status);
                return CommonResult.error(400, "参数不完整");
            }

            // 只处理WMS相关流程
            if (!processDefinitionKey.startsWith("wms_")) {
                log.debug("[processStatusChange] 非WMS流程，跳过处理: {}", processDefinitionKey);
                return success(true);
            }

            log.info("[processStatusChange] 处理WMS流程状态变化，processDefinitionKey: {}, businessKey: {}, status: {}",
                    processDefinitionKey, businessKey, status);

            // 根据流程类型分发处理

            // 根据流程编码获取对应的业务实现类
            Class<? extends IBillBizService> serviceClass = FlowCodeEnum.getServiceClassByFlowCode(processDefinitionKey);
            assert serviceClass != null;
            String simpleName = serviceClass.getSimpleName();
            IBillBizService billBizService = this.serviceMap.get(simpleName);
            FlowProcessRespDTO flowProcessRespDTO = BeanUtils.toBean(message, FlowProcessRespDTO.class);
            billBizService.updateFlowDataByKey(flowProcessRespDTO);
            return success(true);

        } catch (Exception e) {
            log.error("[processStatusChange][Feign回调] 处理流程状态变化失败", e);
            return CommonResult.error(500, "处理失败: " + e.getMessage());
        }
    }

}