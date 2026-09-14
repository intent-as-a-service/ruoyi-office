package cn.iocoder.yudao.module.system.controller.admin.schedule;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.schedule.vo.*;
import cn.iocoder.yudao.module.system.convert.schedule.ScheduleConvert;
import cn.iocoder.yudao.module.system.dal.dataobject.schedule.ScheduleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.schedule.ScheduleReceiverDO;
import cn.iocoder.yudao.module.system.dal.mysql.schedule.ScheduleReceiverMapper;
import cn.iocoder.yudao.module.system.service.schedule.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 日程管理
 *
 * @author 宇擎源码
 */
@Tag(name = "管理后台 - 日程管理")
@RestController
@RequestMapping("/system/schedule")
@Validated
public class ScheduleController {

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private ScheduleReceiverMapper scheduleReceiverMapper;

    @PostMapping("/create")
    @Operation(summary = "创建日程")
    @PreAuthorize("@ss.hasPermission('system:schedule:create')")
    public CommonResult<Long> createSchedule(@Valid @RequestBody ScheduleCreateReqVO createReqVO) {
        return success(scheduleService.createSchedule(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新日程")
    @PreAuthorize("@ss.hasPermission('system:schedule:update')")
    public CommonResult<Boolean> updateSchedule(@Valid @RequestBody ScheduleUpdateReqVO updateReqVO) {
        scheduleService.updateSchedule(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除日程")
    @Parameter(name = "id", description = "日程编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:schedule:delete')")
    public CommonResult<Boolean> deleteSchedule(@RequestParam("id") Long id) {
        scheduleService.deleteSchedule(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得日程")
    @Parameter(name = "id", description = "日程编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:schedule:query')")
    public CommonResult<ScheduleRespVO> getSchedule(@RequestParam("id") Long id) {
        ScheduleDO schedule = scheduleService.getSchedule(id);
        // 查询接收人列表
        List<ScheduleReceiverDO> receivers = scheduleReceiverMapper.selectListByScheduleId(id);
        ScheduleRespVO respVO = ScheduleConvert.INSTANCE.convert(schedule, receivers);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得日程分页")
    @PreAuthorize("@ss.hasPermission('system:schedule:query')")
    public CommonResult<PageResult<ScheduleRespVO>> getSchedulePage(@Valid SchedulePageReqVO pageReqVO) {
        PageResult<ScheduleDO> pageResult = scheduleService.getSchedulePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ScheduleRespVO.class));
    }

    @GetMapping("/list-by-date")
    @Operation(summary = "根据日期查询日程列表")
    @PreAuthorize("@ss.hasPermission('system:schedule:query')")
    public CommonResult<List<ScheduleRespVO>> getScheduleListByDate(@Valid ScheduleListByDateReqVO reqVO) {
        List<ScheduleDO> list = scheduleService.getScheduleListByDate(reqVO.getScheduleDate());
        return success(BeanUtils.toBean(list, ScheduleRespVO.class));
    }

    @GetMapping("/dates")
    @Operation(summary = "获取有日程的日期列表")
    @Parameter(name = "startDate", description = "开始日期", required = true, example = "2026-01-01")
    @Parameter(name = "endDate", description = "结束日期", required = true, example = "2026-01-31")
    @PreAuthorize("@ss.hasPermission('system:schedule:query')")
    public CommonResult<List<String>> getScheduleDates(@RequestParam("startDate") LocalDate startDate,
                                                         @RequestParam("endDate") LocalDate endDate) {
        List<String> dates = scheduleService.getScheduleDates(startDate, endDate);
        return success(dates);
    }

    @GetMapping("/my-page")
    @Operation(summary = "获取我的日程分页")
    @PreAuthorize("@ss.hasPermission('system:schedule:query')")
    public CommonResult<PageResult<ScheduleRespVO>> getMySchedulePage(@Valid SchedulePageReqVO pageReqVO) {
        PageResult<ScheduleDO> pageResult = scheduleService.getMySchedulePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ScheduleRespVO.class));
    }

    @PostMapping("/push")
    @Operation(summary = "推送日程给指定用户")
    @PreAuthorize("@ss.hasPermission('system:schedule:push')")
    public CommonResult<Boolean> pushSchedule(@Valid @RequestBody SchedulePushReqVO pushReqVO) {
        scheduleService.pushSchedule(pushReqVO);
        return success(true);
    }

}

