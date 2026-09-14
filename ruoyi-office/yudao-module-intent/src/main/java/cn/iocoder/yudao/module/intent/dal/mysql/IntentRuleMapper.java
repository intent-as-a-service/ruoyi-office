package cn.iocoder.yudao.module.intent.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.intent.dal.dataobject.IntentRuleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 自动跟进规则 Mapper。
 */
@Mapper
public interface IntentRuleMapper extends BaseMapperX<IntentRuleDO> {

    default List<IntentRuleDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<IntentRuleDO>()
                .eq(IntentRuleDO::getStatus, status)
                .orderByAsc(IntentRuleDO::getId));
    }

    default IntentRuleDO selectByRuleKey(String ruleKey) {
        return selectOne(IntentRuleDO::getRuleKey, ruleKey);
    }
}
