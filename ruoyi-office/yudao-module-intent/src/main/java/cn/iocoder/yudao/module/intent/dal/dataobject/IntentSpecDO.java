package cn.iocoder.yudao.module.intent.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 意图规范 DO：IntentSpec 的数据库存储。
 *
 * <p>启动时 classpath YAML 作为种子写入；后台新增/修改/删除即时生效。</p>
 */
@TableName("intent_spec")
@KeySequence("intent_spec_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class IntentSpecDO extends BaseDO {

    /** 编号 */
    @TableId
    private Long id;
    /** 意图编号（系统.域.动作） */
    private String intentId;
    /** IntentSpec 完整定义（JSON） */
    private String specJson;
    /** 来源：builtin / custom */
    private String source;
    /** 备注 */
    private String remark;
}
