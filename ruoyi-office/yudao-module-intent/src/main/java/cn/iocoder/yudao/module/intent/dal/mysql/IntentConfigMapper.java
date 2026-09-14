package cn.iocoder.yudao.module.intent.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.intent.dal.dataobject.IntentConfigDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 意图配置 Mapper。
 */
@Mapper
public interface IntentConfigMapper extends BaseMapperX<IntentConfigDO> {

    default IntentConfigDO selectByIntentId(String intentId) {
        return selectOne(IntentConfigDO::getIntentId, intentId);
    }

    default List<IntentConfigDO> selectAll() {
        return selectList(new LambdaQueryWrapperX<>());
    }

    default void deleteByIntentId(String intentId) {
        delete(new LambdaQueryWrapperX<IntentConfigDO>().eq(IntentConfigDO::getIntentId, intentId));
    }
}
