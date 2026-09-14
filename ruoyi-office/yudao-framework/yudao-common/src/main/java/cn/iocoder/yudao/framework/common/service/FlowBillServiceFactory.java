package cn.iocoder.yudao.framework.common.service;

import cn.iocoder.yudao.framework.common.enums.BillTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程表单服务工厂类
 * 根据单据类型获取对应的流程表单服务实现
 *
 * @author 宇擎源码
 */
@Slf4j
public abstract class FlowBillServiceFactory<T extends BillTypeEnum> implements InitializingBean {

    @Resource
    private List<FlowBillService<T>> flowBillServices;

    /**
     * 服务映射表：单据类型 -> 服务实现
     */
    private final Map<T, FlowBillService<T>> serviceMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() {
        // 初始化服务映射表
        for (FlowBillService<T> service : flowBillServices) {
            T billType = service.getSupportedBillType();
            serviceMap.put(billType, service);
            log.info("注册流程表单服务: {} -> {}", billType.getTypeName(), service.getClass().getSimpleName());
        }
    }

    /**
     * 根据单据类型获取对应的服务实现
     *
     * @param billType 单据类型
     * @return 服务实现
     */
    public FlowBillService<T> getService(T billType) {
        FlowBillService<T> service = serviceMap.get(billType);
        if (service == null) {
            throw new IllegalArgumentException("不支持的单据类型: " + billType);
        }
        return service;
    }

    /**
     * 根据流程定义Key获取对应的服务实现
     *
     * @param processDefinitionKey 流程定义Key
     * @return 服务实现
     */
    public FlowBillService<T> getServiceByProcessKey(String processDefinitionKey) {
        T[] billTypes = getBillTypeValues();
        for (T billType : billTypes) {
            if (billType.getProcessDefinitionKey().equals(processDefinitionKey)) {
                return getService(billType);
            }
        }
        throw new IllegalArgumentException("未找到对应的单据类型，流程定义Key: " + processDefinitionKey);
    }

    /**
     * 获取所有单据类型枚举值
     * 子类需要实现此方法返回具体的枚举类型数组
     *
     * @return 单据类型枚举值数组
     */
    protected abstract T[] getBillTypeValues();
}
