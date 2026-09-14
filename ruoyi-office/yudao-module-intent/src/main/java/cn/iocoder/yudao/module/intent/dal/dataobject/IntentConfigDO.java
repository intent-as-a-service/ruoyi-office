package cn.iocoder.yudao.module.intent.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 意图配置 DO：上架状态 + 可见角色。
 *
 * <p>意图定义（IntentSpec YAML）声明默认策略；本表存储运营态配置，
 * 两者合并生效：未配置的意图默认上架且不限角色。</p>
 */
@TableName("intent_config")
@KeySequence("intent_config_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class IntentConfigDO extends BaseDO {

    /** 编号 */
    @TableId
    private Long id;
    /** 意图编号（crm.customer.analyze） */
    private String intentId;
    /** 是否上架（下架后目录不返回、执行被拒绝） */
    private Boolean enabled;
    /** 可见角色编码 JSON 数组（["*"]=不限制；空数组=不限制） */
    private String roles;
    /** 备注 */
    private String remark;
}
