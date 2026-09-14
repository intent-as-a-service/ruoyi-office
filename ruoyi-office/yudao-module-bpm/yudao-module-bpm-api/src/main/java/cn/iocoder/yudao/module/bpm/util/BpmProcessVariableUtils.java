package cn.iocoder.yudao.module.bpm.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import static cn.iocoder.yudao.module.bpm.enums.BpmProcessVariableConstants.*;

/**
 * BPM 流程变量工具类
 * 提供统一的流程变量创建方法，确保待办列表能够显示关键业务信息
 * 
 * @author 宇擎源码
 */
@Slf4j
public class BpmProcessVariableUtils {

    /**
     * 标准流程变量字段映射
     * key: 字段名，value: 是否需要转换为字符串
     */
    private static final Map<String, Boolean> STANDARD_FIELD_MAP = Map.of(
            BILL_CODE, true,        // 单据编号 -> 转字符串
            CAUSE, true,            // 事由 -> 转字符串
            DEPT_NAME, true,        // 部门名称 -> 转字符串
            DEPT_ID, false,         // 部门ID -> 保持原类型
            COMPANY_NAME, true,     // 公司名称 -> 转字符串
            COMPANY_ID, false       // 公司ID -> 保持原类型
    );

    /**
     * 获取所有支持的标准字段名称
     * 
     * @return 标准字段名称集合
     */
    public static Set<String> getSupportedFieldNames() {
        return STANDARD_FIELD_MAP.keySet();
    }

    /**
     * 检查字段是否为支持的标准字段
     * 
     * @param fieldName 字段名称
     * @return true: 支持的标准字段，false: 不支持
     */
    public static boolean isSupportedField(String fieldName) {
        return STANDARD_FIELD_MAP.containsKey(fieldName);
    }

    /**
     * 构建单据流程变量
     * 
     * 从业务对象中提取属性构建流程变量，支持以下标准字段：
     * - billCode: 单据编号（转为字符串）
     * - cause: 事由/说明（转为字符串）
     * - deptName: 部门名称（转为字符串）
     * - deptId: 部门ID（保持原类型）
     * - companyName: 公司名称（转为字符串）
     * - companyId: 公司ID（保持原类型）
     * 
     * @param billObject 业务单据对象（VO、DO等包含标准字段的对象）
     * @return 流程变量Map，key为字段名，value为字段值
     */
    public static Map<String, Object> buildBillVariables(Object billObject) {
        Map<String, Object> variables = new HashMap<>();
        
        if (billObject == null) {
            return variables;
        }
        
        try {
            Class<?> clazz = billObject.getClass();
            Field[] fields = clazz.getDeclaredFields();
            
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(billObject);
                
                // 跳过null值
                if (value == null) {
                    continue;
                }
                
                String fieldName = field.getName();
                
                // 标准字段映射 - 使用Map优化if-else链
                Boolean needToString = STANDARD_FIELD_MAP.get(fieldName);
                if (needToString != null) {
                    Object processedValue = needToString ? value.toString() : value;
                    variables.put(fieldName, processedValue);
                }
            }
        } catch (Exception e) {
            // 反射异常时，降级处理，返回空Map
            log.warn("[buildBillVariables] 提取流程变量失败，对象类型: {}, 错误: {}", 
                    billObject.getClass().getSimpleName(), e.getMessage());
        }
        
        // 调试日志：记录提取的变量
        if (log.isDebugEnabled() && !variables.isEmpty()) {
            log.debug("[buildBillVariables] 成功提取流程变量，对象类型: {}, 变量: {}", 
                    billObject.getClass().getSimpleName(), variables.keySet());
        }
        
        return variables;
    }

    /**
     * 构建单据流程变量（扩展版本）
     * 
     * @param billObject 业务单据对象
     * @param additionalVariables 额外的业务变量
     * @return 流程变量Map
     */
    public static Map<String, Object> buildBillVariables(Object billObject, Map<String, Object> additionalVariables) {
        Map<String, Object> variables = buildBillVariables(billObject);
        if (additionalVariables != null) {
            variables.putAll(additionalVariables);
        }
        return variables;
    }


    /**
     * 从流程变量中获取单据编号
     * 
     * @param variables 流程变量
     * @return 单据编号
     */
    public static String getBillCode(Map<String, Object> variables) {
        if (variables == null) {
            return null;
        }
        Object billCode = variables.get(BILL_CODE);
        return billCode != null ? billCode.toString() : null;
    }

    /**
     * 从流程变量中获取事由
     * 
     * @param variables 流程变量
     * @return 事由/说明
     */
    public static String getCause(Map<String, Object> variables) {
        if (variables == null) {
            return null;
        }
        Object cause = variables.get(CAUSE);
        return cause != null ? cause.toString() : null;
    }

    /**
     * 从流程变量中获取部门名称
     * 
     * @param variables 流程变量
     * @return 部门名称
     */
    public static String getDeptName(Map<String, Object> variables) {
        if (variables == null) {
            return null;
        }
        Object deptName = variables.get(DEPT_NAME);
        return deptName != null ? deptName.toString() : null;
    }

    /**
     * 从流程变量中获取部门ID
     * 
     * @param variables 流程变量
     * @return 部门ID
     */
    public static Long getDeptId(Map<String, Object> variables) {
        if (variables == null) {
            return null;
        }
        Object deptId = variables.get(DEPT_ID);
        if (deptId instanceof Number) {
            return ((Number) deptId).longValue();
        }
        if (deptId != null) {
            try {
                return Long.parseLong(deptId.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 从流程变量中获取公司名称
     * 
     * @param variables 流程变量
     * @return 公司名称
     */
    public static String getCompanyName(Map<String, Object> variables) {
        if (variables == null) {
            return null;
        }
        Object companyName = variables.get(COMPANY_NAME);
        return companyName != null ? companyName.toString() : null;
    }

    /**
     * 从流程变量中获取公司ID
     * 
     * @param variables 流程变量
     * @return 公司ID
     */
    public static Long getCompanyId(Map<String, Object> variables) {
        if (variables == null) {
            return null;
        }
        Object companyId = variables.get(COMPANY_ID);
        if (companyId instanceof Number) {
            return ((Number) companyId).longValue();
        }
        if (companyId != null) {
            try {
                return Long.parseLong(companyId.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

}
