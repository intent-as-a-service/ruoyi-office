package cn.iocoder.yudao.module.intent.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 自动跟进规则 DO：把"什么时候提醒、提醒什么、点了执行哪个意图"存成一条声明式规则。
 *
 * <p>与 classpath {@code intent-rules/*.yaml} 的种子规则同构（同为 IntentSuggestionRule），
 * 区别只是来源：种子规则随代码发版，这里的是运营用自然语言生成、预览后启用的。</p>
 */
@TableName("intent_rule")
@KeySequence("intent_rule_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class IntentRuleDO extends BaseDO {

    /** 启用状态：草稿（仅预览，不参与目录增强） */
    public static final int STATUS_DRAFT = 0;
    /** 启用状态：已启用 */
    public static final int STATUS_ENABLED = 1;

    /** 编号 */
    @TableId
    private Long id;
    /** 规则标识（YAML 里的 id，形如 crm.rule.xxx） */
    private String ruleKey;
    /** 规则名称（人看的标题） */
    private String title;
    /** 状态：0=草稿 1=已启用 */
    private Integer status;
    /** 规则定义（YAML 原文，一条规则 = 查询 + 条件 + 输出模板 + 参数映射） */
    private String yaml;
    /** 生成规则时用户的原话（留痕：出了问题能追到"当时想要什么"） */
    private String sourceText;
    /** 备注 */
    private String remark;
}
