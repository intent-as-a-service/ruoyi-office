package cn.iocoder.yudao.module.system.service.schedule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.schedule.vo.*;
import cn.iocoder.yudao.module.system.dal.dataobject.schedule.ScheduleDO;

import java.time.LocalDate;
import java.util.List;

/**
 * 日程管理 Service 接口
 *
 * @author 宇擎源码
 */
public interface ScheduleService {

    /**
     * 创建日程
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSchedule(ScheduleCreateReqVO createReqVO);

    /**
     * 更新日程
     *
     * @param updateReqVO 更新信息
     */
    void updateSchedule(ScheduleUpdateReqVO updateReqVO);

    /**
     * 删除日程
     *
     * @param id 编号
     */
    void deleteSchedule(Long id);

    /**
     * 获得日程
     *
     * @param id 编号
     * @return 日程
     */
    ScheduleDO getSchedule(Long id);

    /**
     * 获得日程分页
     *
     * @param pageReqVO 分页查询
     * @return 日程分页
     */
    PageResult<ScheduleDO> getSchedulePage(SchedulePageReqVO pageReqVO);

    /**
     * 根据日期查询日程列表
     *
     * @param scheduleDate 日程日期
     * @return 日程列表
     */
    List<ScheduleDO> getScheduleListByDate(LocalDate scheduleDate);

    /**
     * 获取有日程的日期列表（用于日历显示点）
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期列表（格式：yyyy-MM-dd）
     */
    List<String> getScheduleDates(LocalDate startDate, LocalDate endDate);

    /**
     * 获取我的日程分页（包括自己创建的和接收的）
     *
     * @param pageReqVO 分页查询
     * @return 日程分页
     */
    PageResult<ScheduleDO> getMySchedulePage(SchedulePageReqVO pageReqVO);

    /**
     * 推送日程给指定用户
     *
     * @param pushReqVO 推送信息
     */
    void pushSchedule(SchedulePushReqVO pushReqVO);

}

