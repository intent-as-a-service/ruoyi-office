package cn.iocoder.yudao.module.system.dal.mysql.schedule;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.schedule.ScheduleReceiverDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 日程接收人关系 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface ScheduleReceiverMapper extends BaseMapperX<ScheduleReceiverDO> {

    /**
     * 根据日程ID查询接收人列表
     *
     * @param scheduleId 日程ID
     * @return 接收人列表
     */
    default List<ScheduleReceiverDO> selectListByScheduleId(Long scheduleId) {
        return selectList(ScheduleReceiverDO::getScheduleId, scheduleId);
    }

    /**
     * 根据接收人ID查询日程ID列表
     *
     * @param receiverId 接收人ID
     * @return 日程ID列表
     */
    default List<Long> selectScheduleIdsByReceiverId(Long receiverId) {
        return selectList(new LambdaQueryWrapperX<ScheduleReceiverDO>()
                .eq(ScheduleReceiverDO::getReceiverId, receiverId))
                .stream()
                .map(ScheduleReceiverDO::getScheduleId)
                .toList();
    }

    /**
     * 批量插入接收人关系
     *
     * @param receivers 接收人列表
     */
    default void insertBatch(List<ScheduleReceiverDO> receivers) {
        if (receivers == null || receivers.isEmpty()) {
            return;
        }
        receivers.forEach(this::insert);
    }

    /**
     * 根据日程ID删除接收人关系
     *
     * @param scheduleId 日程ID
     */
    default void deleteByScheduleId(Long scheduleId) {
        delete(ScheduleReceiverDO::getScheduleId, scheduleId);
    }

    /**
     * 查询用户是否已读指定日程
     *
     * @param receiverId 接收人ID
     * @param scheduleId 日程ID
     * @return 是否已读
     */
    default boolean isRead(Long receiverId, Long scheduleId) {
        ScheduleReceiverDO receiver = selectOne(new LambdaQueryWrapperX<ScheduleReceiverDO>()
                .eq(ScheduleReceiverDO::getReceiverId, receiverId)
                .eq(ScheduleReceiverDO::getScheduleId, scheduleId));
        return receiver != null && receiver.getReadStatus() != null && receiver.getReadStatus() == 1;
    }

    /**
     * 标记为已读
     *
     * @param receiverId 接收人ID
     * @param scheduleId 日程ID
     */
    default void markAsRead(Long receiverId, Long scheduleId) {
        ScheduleReceiverDO receiver = selectOne(new LambdaQueryWrapperX<ScheduleReceiverDO>()
                .eq(ScheduleReceiverDO::getReceiverId, receiverId)
                .eq(ScheduleReceiverDO::getScheduleId, scheduleId));
        if (receiver != null) {
            receiver.setReadStatus(1);
            receiver.setReadTime(java.time.LocalDateTime.now());
            updateById(receiver);
        }
    }

}

