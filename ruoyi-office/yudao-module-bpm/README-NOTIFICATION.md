# BPM 跨服务通知系统使用说明

## 概述

本系统提供了三种跨服务通知方式，支持BPM服务向其他业务服务发送流程状态变化通知：

1. **本地事件通知（ApplicationEvent）**：适用于单体架构或同一JVM内的服务通信
2. **消息队列通知（MQ）**：适用于异步、高吞吐量的跨服务通信场景
3. **Feign远程调用通知**：适用于实时性要求高的同步调用场景

## 快速开始

### 1. 配置文件设置

在 `application.yaml` 中添加配置：

```yaml
yudao:
  bpm:
    notification:
      # 默认通知方式
      default-type: local_event  # local_event, mq, feign
      # 是否异步处理
      async: true
      # MQ 通知配置
      mq:
        enabled: true
      # Feign 通知配置  
      feign:
        enabled: true
      # 流程特定配置
      process-config:
        oa_car_apply_bill: mq     # 用车申请单使用MQ
        oa_leave: feign           # 请假流程使用Feign
```

### 2. 切换通知方式

#### 方式一：全局切换
修改 `default-type` 配置：
```yaml
yudao:
  bpm:
    notification:
      default-type: mq  # 全部使用MQ通知
```

#### 方式二：按流程类型切换
在 `process-config` 中配置：
```yaml
yudao:
  bpm:
    notification:
      process-config:
        oa_car_apply_bill: mq     # 用车申请单使用MQ
        oa_leave: feign           # 请假使用Feign
        oa_expense: local_event   # 报销使用本地事件
```

## 各种通知方式详解

### 1. 本地事件通知（local_event）

**适用场景：**
- 单体应用架构
- 同一JVM内的模块通信
- 开发测试环境

**配置：**
```yaml
yudao:
  bpm:
    notification:
      default-type: local_event
```

**特点：**
- ✅ 零依赖，无需额外组件
- ✅ 响应最快
- ❌ 仅限单体架构
- ❌ 无法跨JVM通信

### 2. 消息队列通知（mq）

**适用场景：**
- 微服务架构
- 异步处理要求
- 高吞吐量场景
- 需要消息持久化

**配置：**
```yaml
yudao:
  bpm:
    notification:
      default-type: mq
      mq:
        enabled: true
```

**特点：**
- ✅ 解耦性强
- ✅ 支持异步处理
- ✅ 消息持久化
- ✅ 支持重试机制
- ❌ 依赖MQ组件
- ❌ 有一定延迟

### 3. Feign远程调用（feign）

**适用场景：**
- 微服务架构
- 实时性要求高
- 需要同步返回结果
- 强一致性要求

**配置：**
```yaml
yudao:
  bpm:
    notification:
      default-type: feign
      feign:
        enabled: true
```

**特点：**
- ✅ 实时同步调用
- ✅ 可获取返回结果
- ✅ 强一致性
- ❌ 服务间耦合较强
- ❌ 网络异常影响较大

## 业务服务集成

### 接收MQ消息

在业务服务中创建消费者：

```java
@Component
public class YourProcessNotificationConsumer extends AbstractStreamMessageListener<String> {
    
    @Override
    public String getTopic() {
        return "bpm.workflow.instance.status.changed";
    }
    
    @Override
    public void onMessage(String message) {
        // 处理消息
    }
}
```

### 实现Feign接口

在业务服务中实现回调接口：

```java
@RestController
@RequestMapping("/your-service/workflow-callback")
public class YourProcessCallbackController {
    
    @PostMapping("/status-change")
    public CommonResult<Boolean> processStatusChange(@RequestBody Map<String, Object> message) {
        // 处理回调
        return CommonResult.success(true);
    }
}
```

### 监听本地事件

在业务服务中创建监听器：

```java
@Component
public class YourProcessStatusListener extends BpmProcessInstanceStatusEventListener {
    
    @Override
    protected String getProcessDefinitionKey() {
        return "your_process_key";
    }
    
    @Override
    protected void onEvent(BpmProcessInstanceStatusEvent event) {
        // 处理事件
    }
}
```

## 消息格式

所有通知方式传递的消息格式相同：

```json
{
  "processInstanceId": "流程实例ID",
  "processDefinitionKey": "流程定义Key",
  "status": "流程状态",
  "businessKey": "业务标识",
  "startUserId": "发起人ID",
  "tenantId": "租户ID",
  "eventTime": "事件时间"
}
```

## 监控和调试

### 日志配置

```yaml
logging:
  level:
    cn.iocoder.yudao.module.bpm.service.notification: DEBUG
```

### 健康检查

通过管理接口查看通知处理器状态：

```java
@Autowired
private BpmNotificationManager notificationManager;

public Map<String, String> getNotificationStatus() {
    return notificationManager.getAvailableHandlers()
        .entrySet().stream()
        .collect(Collectors.toMap(
            entry -> entry.getKey().getCode(),
            entry -> entry.getValue().isEnabled() ? "enabled" : "disabled"
        ));
}
```

## 最佳实践

1. **开发环境**：使用 `local_event`，简单快速
2. **测试环境**：使用 `mq`，验证异步处理
3. **生产环境**：根据业务需求选择：
   - 实时性要求高：`feign`
   - 高并发场景：`mq`
   - 混合场景：按流程类型配置

4. **错误处理**：
   - MQ：配置重试和死信队列
   - Feign：配置熔断和降级
   - 本地事件：注意异常传播

5. **性能优化**：
   - 启用异步处理：`async: true`
   - 合理配置线程池
   - 监控处理耗时

## 故障排查

### 常见问题

1. **MQ消息未消费**
   - 检查Topic名称是否正确
   - 确认消费者组配置
   - 查看MQ连接状态

2. **Feign调用失败**
   - 检查服务注册发现
   - 确认接口路径正确
   - 查看网络连通性

3. **本地事件未触发**
   - 确认监听器注册
   - 检查processDefinitionKey匹配
   - 查看事务提交状态

### 调试技巧

1. 开启DEBUG日志查看详细信息
2. 使用Actuator监控端点
3. 通过JMX查看MQ状态
4. 使用链路追踪工具分析调用链 