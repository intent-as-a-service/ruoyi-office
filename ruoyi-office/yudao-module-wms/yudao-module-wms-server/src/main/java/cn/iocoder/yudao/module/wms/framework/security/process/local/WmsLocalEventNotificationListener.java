package cn.iocoder.yudao.module.wms.framework.security.process.local;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceInfo;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusEvent;

import cn.iocoder.yudao.module.wms.framework.security.enums.FlowCodeEnum;
import cn.iocoder.yudao.module.wms.framework.security.service.FlowProcessRespDTO;
import cn.iocoder.yudao.module.wms.framework.security.service.IBillBizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * WMS 请假单的结果的监听器实现类
 *
 * @author 宇擎源码
 */
@Component
@Slf4j
public class WmsLocalEventNotificationListener implements ApplicationListener<BpmProcessInstanceStatusEvent> {

    private Map<String, IBillBizService> serviceMap;

    @Autowired
    public void businessServiceFactory(List<IBillBizService> services) {
        this.serviceMap = services.stream().collect(Collectors.toMap(service -> AopUtils.getTargetClass(service).getSimpleName(), Function.identity()));
    }

    @Override
    public void onApplicationEvent(BpmProcessInstanceStatusEvent message) {
        log.info("[processStatusChange][Feign回调] 收到流程状态变化回调: {}", message);

        try {
            String processDefinitionKey = message.getProcessDefinitionKey();
            String businessKey = message.getBusinessKey();
            BpmProcessInstanceInfo processInstanceInfo = message.getProcessInstanceInfo();
            Integer status = processInstanceInfo.getStatus();

            // 参数校验
            if (StrUtil.isBlank(processDefinitionKey) || StrUtil.isBlank(businessKey) || status == null) {
                log.warn("[processStatusChange] 参数不完整，processDefinitionKey: {}, businessKey: {}, status: {}",
                        processDefinitionKey, businessKey, status);

            }

            // 只处理WMS相关流程
            if (!processDefinitionKey.startsWith("wms_")) {
                log.debug("[processStatusChange] 非WMS流程，跳过处理: {}", processDefinitionKey);
                return;
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

        } catch (Exception e) {
            log.error("[processStatusChange][Feign回调] 处理流程状态变化失败", e);
        }
    }

}
