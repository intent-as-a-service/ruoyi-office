package cn.iocoder.yudao.module.intent.dal.mysql;

import cn.iocoder.yudao.module.intent.dal.dataobject.IntentSpecDO;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 意图规范 Mapper。
 */
@Mapper
public interface IntentSpecMapper extends BaseMapperX<IntentSpecDO> {

    default IntentSpecDO selectByIntentId(String intentId) {
        return selectOne(IntentSpecDO::getIntentId, intentId);
    }

    /** 物理计数（含逻辑删除行）：启动种子与"删除后重建同 ID"判断使用。 */
    @Select("SELECT COUNT(1) FROM intent_spec WHERE intent_id = #{intentId}")
    int countByIntentIdIncludeDeleted(@Param("intentId") String intentId);

    /** 恢复逻辑删除行并更新内容（删除后重建同 ID 意图）。 */
    @Update("UPDATE intent_spec SET deleted = 0, spec_json = #{specJson}, source = #{source}, "
            + "updater = #{updater}, update_time = NOW() WHERE intent_id = #{intentId}")
    int restoreAndUpdate(@Param("intentId") String intentId, @Param("specJson") String specJson,
            @Param("source") String source, @Param("updater") String updater);
}
