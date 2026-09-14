package cn.iocoder.yudao.framework.common.enums;

/**
 * 单据类型枚举
 *
 * @author 宇擎源码
 */
public interface BillTypeEnum {

    /**
     * 单据类型
     *
     * @return 单据类型
     */
    String getTypeCode();

    /**
     * 单据名称
     *
     * @return 单据名称
     */
    String getTypeName();

    /**
     * 流程定义
     *
     * @return 流程定义key
     */
    String getProcessDefinitionKey();

}
