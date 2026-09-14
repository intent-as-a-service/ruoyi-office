package cn.iocoder.yudao.module.system.dal.mysql.schedule;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.schedule.vo.SchedulePageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.schedule.ScheduleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 日程管理 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface ScheduleMapper extends BaseMapperX<ScheduleDO> {

    default PageResult<ScheduleDO> selectPage(SchedulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ScheduleDO>()
                .likeIfPresent(ScheduleDO::getTitle, reqVO.getTitle())
                .eqIfPresent(ScheduleDO::getScheduleDate, reqVO.getScheduleDate())
                .betweenIfPresent(ScheduleDO::getScheduleDate, reqVO.getScheduleDateStart(), reqVO.getScheduleDateEnd())
                .eqIfPresent(ScheduleDO::getScheduleType, reqVO.getScheduleType())
                .eqIfPresent(ScheduleDO::getScheduleCategory, reqVO.getScheduleCategory())
                .eqIfPresent(ScheduleDO::getCreatorId, reqVO.getCreatorId())
                .eqIfPresent(ScheduleDO::getStatus, reqVO.getStatus())
                .orderByDesc(ScheduleDO::getScheduleDate)
                .orderByAsc(ScheduleDO::getStartTime)
                .orderByDesc(ScheduleDO::getId));
    }

    /**
     * 根据日期查询日程列表
     *
     * @param scheduleDate 日程日期
     * @param userId 用户ID（用于查询自己创建的和接收的）
     * @return 日程列表
     */
    default List<ScheduleDO> selectListByDate(LocalDate scheduleDate, Long userId) {
        LambdaQueryWrapperX<ScheduleDO> wrapper = (LambdaQueryWrapperX<ScheduleDO>) new LambdaQueryWrapperX<ScheduleDO>()
                .eq(ScheduleDO::getScheduleDate, scheduleDate)
                .eq(ScheduleDO::getStatus, CommonStatusEnum.ENABLE.getStatus()) // 只查询启用的
                // 自己创建的，或者作为接收人收到的
                .apply("(" +
                        "creator_id = {0} OR EXISTS (" +
                        "  SELECT 1 FROM system_schedule_receiver sr " +
                        "  WHERE sr.schedule_id = system_schedule.id " +
                        "    AND sr.receiver_id = {0} " +
                        "    AND sr.deleted = 0" +
                        ")" +
                        ")", userId);
        return selectList(wrapper);
    }

    /**
     * 获取有日程的日期列表（用于日历显示点）
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param userId 用户ID
     * @return 日期列表
     */
    @Select("SELECT DISTINCT schedule_date " +
            "FROM system_schedule s " +
            "WHERE s.schedule_date BETWEEN #{startDate} AND #{endDate} " +
            "AND s.status = 0 " +
            "AND s.deleted = 0 " +
            "AND s.tenant_id = #{tenantId} " +
            "AND (s.creator_id = #{userId} " +
            "     OR EXISTS (SELECT 1 FROM system_schedule_receiver sr " +
            "                WHERE sr.schedule_id = s.id " +
            "                AND sr.receiver_id = #{userId} " +
            "                AND sr.deleted = 0)) " +
            "ORDER BY schedule_date")
    List<LocalDate> selectScheduleDates(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate,
                                       @Param("userId") Long userId,
                                       @Param("tenantId") Long tenantId);

    /**
     * 查询当前用户的日程（包括自己创建的和接收的）
     *
     * @param userId 用户ID
     * @param reqVO 查询条件
     * @return 日程分页列表
     */
    default PageResult<ScheduleDO> selectMySchedulePage(Long userId, SchedulePageReqVO reqVO) {
        LambdaQueryWrapperX<ScheduleDO> wrapper = (LambdaQueryWrapperX<ScheduleDO>) new LambdaQueryWrapperX<ScheduleDO>()
                .likeIfPresent(ScheduleDO::getTitle, reqVO.getTitle())
                .eqIfPresent(ScheduleDO::getScheduleDate, reqVO.getScheduleDate())
                .betweenIfPresent(ScheduleDO::getScheduleDate, reqVO.getScheduleDateStart(), reqVO.getScheduleDateEnd())
                .eqIfPresent(ScheduleDO::getScheduleType, reqVO.getScheduleType())
                .eqIfPresent(ScheduleDO::getScheduleCategory, reqVO.getScheduleCategory())
                .eqIfPresent(ScheduleDO::getStatus, reqVO.getStatus())
                // 自己创建的，或者作为接收人收到的
                .apply("(" +
                        "creator_id = {0} OR EXISTS (" +
                        "  SELECT 1 FROM system_schedule_receiver sr " +
                        "  WHERE sr.schedule_id = system_schedule.id " +
                        "    AND sr.receiver_id = {0} " +
                        "    AND sr.deleted = 0" +
                        ")" +
                        ")", userId) // 或者接收的
                .orderByDesc(ScheduleDO::getScheduleDate)
                .orderByAsc(ScheduleDO::getStartTime)
                .orderByDesc(ScheduleDO::getId);
        return selectPage(reqVO, wrapper);
    }

}

