package cn.iocoder.yudao.module.system.service.schedule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.system.controller.admin.schedule.vo.*;
import cn.iocoder.yudao.module.system.dal.dataobject.schedule.ScheduleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.schedule.ScheduleReceiverDO;
import cn.iocoder.yudao.module.system.dal.mysql.schedule.ScheduleMapper;
import cn.iocoder.yudao.module.system.dal.mysql.schedule.ScheduleReceiverMapper;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;

/**
 * 日程管理 Service 实现类
 *
 * @author 宇擎源码
 */
@Service
@Validated
public class ScheduleServiceImpl implements ScheduleService {

    @Resource
    private ScheduleMapper scheduleMapper;

    @Resource
    private ScheduleReceiverMapper scheduleReceiverMapper;

    @Resource
    private AdminUserService adminUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSchedule(ScheduleCreateReqVO createReqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        String userName = adminUserService.getUser(userId).getNickname();

        // 插入日程
        ScheduleDO schedule = BeanUtils.toBean(createReqVO, ScheduleDO.class);
        schedule.setCreatorId(userId);
        schedule.setCreatorName(userName);
        schedule.setIsPushed(false); // 创建时默认为非推送，不自动推送
        schedule.setTenantId(TenantContextHolder.getTenantId());
        scheduleMapper.insert(schedule);

        // 注意：创建时不自动推送，需要用户点击"确认并立即推送"按钮才会推送

        return schedule.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSchedule(ScheduleUpdateReqVO updateReqVO) {
        // 校验日程是否存在
        ScheduleDO schedule = validateScheduleExists(updateReqVO.getId());

        // 校验权限：只有创建人且非推送的才能编辑
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (!schedule.getCreatorId().equals(userId)) {
            throw exception(SCHEDULE_NOT_EDITABLE);
        }

        // 更新日程
        ScheduleDO updateObj = BeanUtils.toBean(updateReqVO, ScheduleDO.class);
        scheduleMapper.updateById(updateObj);

        // 注意：更新时不自动推送，需要用户点击"确认并立即推送"按钮才会推送
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSchedule(Long id) {
        // 校验日程是否存在
        ScheduleDO schedule = validateScheduleExists(id);

        // 校验权限：只有创建人且非推送的才能删除
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (!schedule.getCreatorId().equals(userId) || Boolean.TRUE.equals(schedule.getIsPushed())) {
            throw exception(SCHEDULE_NOT_DELETABLE);
        }

        // 删除接收人关系
        scheduleReceiverMapper.deleteByScheduleId(id);

        // 删除日程
        scheduleMapper.deleteById(id);
    }

    @Override
    public ScheduleDO getSchedule(Long id) {
        return scheduleMapper.selectById(id);
    }

    @Override
    public PageResult<ScheduleDO> getSchedulePage(SchedulePageReqVO pageReqVO) {
        return scheduleMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ScheduleDO> getScheduleListByDate(LocalDate scheduleDate) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return scheduleMapper.selectListByDate(scheduleDate, userId);
    }

    @Override
    public List<String> getScheduleDates(LocalDate startDate, LocalDate endDate) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        Long tenantId = TenantContextHolder.getTenantId();
        List<LocalDate> dates = scheduleMapper.selectScheduleDates(startDate, endDate, userId, tenantId);
        // 转换为 yyyy-MM-dd 格式的字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FORMAT_YEAR_MONTH_DAY);
        return dates.stream()
                .map(date -> date.format(formatter))
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<ScheduleDO> getMySchedulePage(SchedulePageReqVO pageReqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        return scheduleMapper.selectMySchedulePage(userId, pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushSchedule(SchedulePushReqVO pushReqVO) {
        // 校验日程是否存在
        ScheduleDO schedule = validateScheduleExists(pushReqVO.getScheduleId());

        // 校验权限：只有创建人才能推送
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (!schedule.getCreatorId().equals(userId)) {
            throw exception(SCHEDULE_NOT_EDITABLE);
        }

        // 推送日程给接收人
        pushScheduleToReceivers(pushReqVO.getScheduleId(), pushReqVO.getReceiverIds());

        // 更新日程为已推送
        schedule.setIsPushed(true);
        scheduleMapper.updateById(schedule);
    }

    /**
     * 推送日程给接收人
     *
     * @param scheduleId 日程ID
     * @param receiverIds 接收人ID列表
     */
    private void pushScheduleToReceivers(Long scheduleId, List<Long> receiverIds) {
        // 删除旧的接收人关系
        scheduleReceiverMapper.deleteByScheduleId(scheduleId);

        // 创建新的接收人关系
        Long tenantId = TenantContextHolder.getTenantId();
        List<ScheduleReceiverDO> receivers = receiverIds.stream()
                .map(receiverId -> {
                    ScheduleReceiverDO receiver = new ScheduleReceiverDO();
                    receiver.setScheduleId(scheduleId);
                    receiver.setReceiverId(receiverId);
                    receiver.setReceiverName(adminUserService.getUser(receiverId).getNickname());
                    receiver.setReadStatus(0); // 未读
                    receiver.setTenantId(tenantId);
                    return receiver;
                })
                .collect(Collectors.toList());

        scheduleReceiverMapper.insertBatch(receivers);
    }

    @VisibleForTesting
    public ScheduleDO validateScheduleExists(Long id) {
        if (id == null) {
            throw exception(SCHEDULE_NOT_FOUND);
        }
        ScheduleDO schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw exception(SCHEDULE_NOT_FOUND);
        }
        return schedule;
    }

}

