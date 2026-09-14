package cn.iocoder.yudao.module.intent.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.intent.dal.dataobject.ExecutorProfileDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 执行器档案 Mapper。
 */
@Mapper
public interface ExecutorProfileMapper extends BaseMapperX<ExecutorProfileDO> {

    default ExecutorProfileDO selectByExecutorId(String executorId) {
        return selectOne(ExecutorProfileDO::getExecutorId, executorId);
    }

    /** 物理计数（含逻辑删除行）：启动种子与"删除后重建同 ID"判断使用。 */
    @Select("SELECT COUNT(1) FROM intent_executor WHERE executor_id = #{executorId}")
    int countByExecutorIdIncludeDeleted(@Param("executorId") String executorId);

    /** 恢复逻辑删除行并更新内容（删除后重建同 ID 执行器）。 */
    @Update("UPDATE intent_executor SET deleted = 0, profile_json = #{profileJson}, source = #{source}, "
            + "updater = #{updater}, update_time = NOW() WHERE executor_id = #{executorId}")
    int restoreAndUpdate(@Param("executorId") String executorId, @Param("profileJson") String profileJson,
            @Param("source") String source, @Param("updater") String updater);
}
