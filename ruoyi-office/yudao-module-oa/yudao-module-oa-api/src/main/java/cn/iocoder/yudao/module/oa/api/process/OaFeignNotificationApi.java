package cn.iocoder.yudao.module.oa.api.process;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.bpm.api.event.BpmProcessInstanceStatusMessage;
import cn.iocoder.yudao.module.oa.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * OA 流程回调 API 接口
 *
 * @author 宇擎源码
 */
@FeignClient(name = ApiConstants.NAME, contextId = "oaProcessCallbackApi")
public interface OaFeignNotificationApi {

    String PREFIX = ApiConstants.PREFIX + "/workflow-callback";

    /**
     * 流程状态变化回调
     *
     * @param message 流程状态消息
     * @return 处理结果
     */
    @PostMapping(PREFIX + "/status-change")
    CommonResult<Boolean> processStatusChange(@RequestBody BpmProcessInstanceStatusMessage message);

} 