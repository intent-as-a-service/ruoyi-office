# BPM MQ通知功能使用指南

## 问题背景

在使用RedisMQTemplate发送消息时，发现`redisMQTemplate.send(topic, jsonString)`方法不支持直接传入topic和字符串消息。

经过分析发现，`RedisMQTemplate.send()`方法需要的参数是继承自`AbstractRedisStreamMessage`的消息对象，而不是简单的字符串参数。

## 解决方案

我们封装了满足需求的Redis Stream消息类和相应的处理逻辑。

### 1. 消息类封装

#### BPM服务端（发送方）

```java
// BpmProcessInstanceStatusRedisMessage.java
@Data
@EqualsAndHashCode(callSuper = true)
public class BpmProcessInstanceStatusRedisMessage extends AbstractRedisStreamMessage {
    private String processInstanceId;
    private String processDefinitionKey;
    private Integer status;
    private String businessKey;
    private LocalDateTime eventTime;
    private String tenantId;
    private String startUserId;
    private String extInfo;

    @Override
    public String getStreamKey() {
        return "bpm.workflow.instance.status.changed";
    }
}
```

#### OA服务端（接收方）

```java
// OaBpmProcessInstanceStatusMessage.java
@Data
@EqualsAndHashCode(callSuper = true)
public class OaBpmProcessInstanceStatusMessage extends AbstractRedisStreamMessage {
    // 字段与发送方相同
    
    @Override
    public String getStreamKey() {
        return "bpm.workflow.instance.status.changed";
    }
}
```

### 2. MQ通知处理器

```java
@Component
public class BpmMqNotificationHandler implements BpmNotificationHandler {

    @Resource
    private RedisMQTemplate redisMQTemplate;

    @Override
    public void handleNotification(BpmProcessInstanceStatusMessage message) {
        // 转换为Redis Stream消息对象
        BpmProcessInstanceStatusRedisMessage redisMessage = 
            BeanUtil.copyProperties(message, BpmProcessInstanceStatusRedisMessage.class);
        
        // 发送MQ消息
        redisMQTemplate.send(redisMessage);
    }
}
```

### 3. MQ消息消费者

```java
@Component
public class OaProcessNotificationConsumer 
    extends AbstractRedisStreamMessageListener<OaBpmProcessInstanceStatusMessage> {

    @Override
    public void onMessage(OaBpmProcessInstanceStatusMessage message) {
        // 处理消息
        if (message.getProcessDefinitionKey().startsWith("oa_")) {
            handleOaProcessNotification(message);
        }
    }
}
```

## 配置方式

### 1. 启用MQ通知

```yaml
yudao:
  bpm:
    notification:
      default-type: mq
      mq:
        enabled: true
```

### 2. Redis配置

确保Redis配置正确：

```yaml
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    database: 0
```

## 使用效果

### 发送消息

```java
// BPM服务中
BpmProcessInstanceStatusMessage message = BpmProcessInstanceStatusMessage.builder()
    .processInstanceId("process_123")
    .processDefinitionKey("oa_car_apply_bill")
    .status(1)
    .businessKey("123")
    .build();

notificationManager.sendProcessStatusNotification(processInstance, status);
```

### 接收消息

```java
// OA服务中自动接收并处理
[onMessage][MQ消费] 收到流程状态变化消息: processInstanceId=process_123, processDefinitionKey=oa_car_apply_bill, status=1
[handleOaProcessNotification] 处理OA流程状态变化，processDefinitionKey: oa_car_apply_bill, businessKey: 123, status: 1
[updateProcessStatus] 更新用车申请单流程状态，id: 123, status: 1
[updateProcessStatus] 用车申请单流程状态更新成功，id: 123, status: 1
```

## 关键点总结

1. **消息类必须继承AbstractRedisStreamMessage**：这是框架要求
2. **StreamKey必须一致**：发送方和接收方的getStreamKey()返回值必须相同
3. **消费者继承AbstractRedisStreamMessageListener**：框架会自动处理序列化和消息确认
4. **类型安全**：通过泛型确保消息类型安全

## 故障排查

### 常见问题

1. **消息发送失败**
   - 检查Redis连接
   - 确认消息类继承关系正确
   - 查看日志中的异常信息

2. **消息未被消费**
   - 检查StreamKey是否一致
   - 确认消费者组配置
   - 查看Redis中的Stream数据

3. **序列化问题**
   - 确保发送方和接收方的消息类字段一致
   - 检查字段类型匹配

### 调试命令

```bash
# 查看Redis Stream信息
redis-cli XINFO STREAM bpm.workflow.instance.status.changed

# 查看消费者组信息
redis-cli XINFO GROUPS bpm.workflow.instance.status.changed

# 查看未处理消息
redis-cli XPENDING bpm.workflow.instance.status.changed oa-server
```

## 性能优化

1. **批量处理**：如果消息量大，可以考虑批量消费
2. **异步处理**：消息处理逻辑使用异步方式
3. **监控告警**：监控消息堆积情况

这个封装很好地解决了RedisMQTemplate的使用问题，提供了类型安全和易用的MQ通知功能。 