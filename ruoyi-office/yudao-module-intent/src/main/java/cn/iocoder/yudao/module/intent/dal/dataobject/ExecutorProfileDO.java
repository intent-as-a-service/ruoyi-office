package cn.iocoder.yudao.module.intent.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 执行器档案 DO：ExecutorProfile 的数据库存储。
 *
 * <p>启动时 classpath YAML 作为种子写入；后台新增/修改/删除即时生效（热更新到运行时）。</p>
 */
@TableName("intent_executor")
@KeySequence("intent_executor_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExecutorProfileDO extends BaseDO {

    /** 编号 */
    @TableId
    private Long id;
    /** 执行器标识（IntentSpec.executor 引用） */
    private String executorId;
    /** ExecutorProfile 完整定义（JSON） */
    private String profileJson;
    /** 来源：builtin / custom */
    private String source;
    /** 备注 */
    private String remark;
}
