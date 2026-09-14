package cn.iocoder.yudao.module.oa.controller.admin.meetingroom;

import cn.hutool.core.util.StrUtil;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.oa.controller.admin.meetingroom.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.meetingroom.MeetingRoomDO;
import cn.iocoder.yudao.module.oa.service.meetingroom.MeetingRoomService;

@Tag(name = "OA协同办公 - 会议室管理")
@RestController
@RequestMapping("/oa/meeting-room")
@Validated
public class MeetingRoomController {

    @Resource
    private MeetingRoomService meetingRoomService;

    @PostMapping("/create")
    @Operation(summary = "创建会议室信息")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room:create')")
    public CommonResult<Long> createMeetingRoom(@Valid @RequestBody MeetingRoomSaveReqVO createReqVO) {
        return success(meetingRoomService.createMeetingRoom(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新会议室信息")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room:update')")
    public CommonResult<Boolean> updateMeetingRoom(@Valid @RequestBody MeetingRoomSaveReqVO updateReqVO) {
        meetingRoomService.updateMeetingRoom(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除会议室信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:meeting-room:delete')")
    public CommonResult<Boolean> deleteMeetingRoom(@RequestParam("id") Long id) {
        meetingRoomService.deleteMeetingRoom(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除会议室信息")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room:delete')")
    public CommonResult<Boolean> deleteMeetingRoomList(@RequestParam("ids") List<Long> ids) {
        meetingRoomService.deleteMeetingRoomListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得会议室信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room:query')")
    public CommonResult<MeetingRoomRespVO> getMeetingRoom(@RequestParam("id") Long id) {
        MeetingRoomDO meetingRoom = meetingRoomService.getMeetingRoom(id);
        MeetingRoomRespVO respVO = BeanUtils.toBean(meetingRoom, MeetingRoomRespVO.class);
        // 处理设备列表（逗号分隔字符串转数组）
        if (StrUtil.isNotBlank(meetingRoom.getEquipment())) {
            respVO.setEquipment(Arrays.asList(meetingRoom.getEquipment().split(",")));
        }
        // 处理预定成员列表（逗号分隔字符串转数组）
        if (StrUtil.isNotBlank(meetingRoom.getBookingMembers())) {
            respVO.setBookingMembers(
                Arrays.stream(meetingRoom.getBookingMembers().split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList())
            );
        }
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得会议室信息分页")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room:query')")
    public CommonResult<PageResult<MeetingRoomRespVO>> getMeetingRoomPage(@Valid MeetingRoomPageReqVO pageReqVO) {
        PageResult<MeetingRoomDO> pageResult = meetingRoomService.getMeetingRoomPage(pageReqVO);
        
        // 保存为final变量，以便在lambda中使用
        final PageResult<MeetingRoomDO> finalPageResult = pageResult;
        final List<MeetingRoomDO> finalRoomList = finalPageResult.getList();
        
        PageResult<MeetingRoomRespVO> respPageResult = BeanUtils.toBean(pageResult, MeetingRoomRespVO.class);
        // 处理每个记录的设备列表和预定成员列表
        respPageResult.getList().forEach(respVO -> {
            MeetingRoomDO meetingRoom = finalRoomList.stream()
                .filter(item -> item.getId().equals(respVO.getId()))
                .findFirst()
                .orElse(null);
            if (meetingRoom != null) {
                // 处理设备列表
                if (StrUtil.isNotBlank(meetingRoom.getEquipment())) {
                    respVO.setEquipment(Arrays.asList(meetingRoom.getEquipment().split(",")));
                }
                // 处理预定成员列表
                if (StrUtil.isNotBlank(meetingRoom.getBookingMembers())) {
                    respVO.setBookingMembers(
                        Arrays.stream(meetingRoom.getBookingMembers().split(","))
                            .map(Long::valueOf)
                            .collect(Collectors.toList())
                    );
                }
            }
        });
        return success(respPageResult);
    }

    @GetMapping("/bookable-page")
    @Operation(summary = "获得可预定的会议室信息分页（用于会议预定单选择会议室）")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room:query')")
    public CommonResult<PageResult<MeetingRoomRespVO>> getBookableMeetingRoomPage(@Valid MeetingRoomPageReqVO pageReqVO) {
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        
        PageResult<MeetingRoomDO> pageResult = meetingRoomService.getBookableMeetingRoomPage(pageReqVO, currentUserId);
        
        // 保存为final变量，以便在lambda中使用
        final PageResult<MeetingRoomDO> finalPageResult = pageResult;
        final List<MeetingRoomDO> finalRoomList = finalPageResult.getList();
        
        PageResult<MeetingRoomRespVO> respPageResult = BeanUtils.toBean(pageResult, MeetingRoomRespVO.class);
        // 处理每个记录的设备列表和预定成员列表
        respPageResult.getList().forEach(respVO -> {
            MeetingRoomDO meetingRoom = finalRoomList.stream()
                .filter(item -> item.getId().equals(respVO.getId()))
                .findFirst()
                .orElse(null);
            if (meetingRoom != null) {
                // 处理设备列表
                if (StrUtil.isNotBlank(meetingRoom.getEquipment())) {
                    respVO.setEquipment(Arrays.asList(meetingRoom.getEquipment().split(",")));
                }
                // 处理预定成员列表
                if (StrUtil.isNotBlank(meetingRoom.getBookingMembers())) {
                    respVO.setBookingMembers(
                        Arrays.stream(meetingRoom.getBookingMembers().split(","))
                            .map(Long::valueOf)
                            .collect(Collectors.toList())
                    );
                }
            }
        });
        return success(respPageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出会议室信息 Excel")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportMeetingRoomExcel(@Valid MeetingRoomPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MeetingRoomDO> list = meetingRoomService.getMeetingRoomPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "会议室信息.xls", "数据", MeetingRoomRespVO.class,
                        BeanUtils.toBean(list, MeetingRoomRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获取会议室精简信息列表（用于下拉选择）")
    @PreAuthorize("@ss.hasPermission('oa:meeting-room:query')")
    public CommonResult<List<MeetingRoomRespVO>> getSimpleMeetingRoomList() {
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        
        MeetingRoomPageReqVO pageReqVO = new MeetingRoomPageReqVO();
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        // 只查询允许预定的会议室
        pageReqVO.setAllowBooking(true);
        List<MeetingRoomDO> list = meetingRoomService.getMeetingRoomPage(pageReqVO).getList();
        
        // 根据可用范围过滤会议室
        if (currentUserId != null) {
            list = list.stream()
                    .filter(room -> {
                        // 如果可用范围为全部成员（0），则允许
                        if (room.getBookingScope() == null || room.getBookingScope() == 0) {
                            return true;
                        }
                        // 如果可用范围为指定成员（1），则检查当前用户是否在可预定成员列表中
                        if (room.getBookingScope() == 1) {
                            if (StrUtil.isBlank(room.getBookingMembers())) {
                                return false; // 指定成员但未设置成员列表，不允许
                            }
                            // 检查当前用户ID是否在可预定成员列表中
                            String[] memberIds = room.getBookingMembers().split(",");
                            for (String memberId : memberIds) {
                                if (String.valueOf(currentUserId).equals(memberId.trim())) {
                                    return true;
                                }
                            }
                            return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
        }
        
        List<MeetingRoomRespVO> respList = BeanUtils.toBean(list, MeetingRoomRespVO.class);
        // 处理预定成员列表
        for (int i = 0; i < respList.size(); i++) {
            MeetingRoomDO meetingRoom = list.get(i);
            if (StrUtil.isNotBlank(meetingRoom.getBookingMembers())) {
                respList.get(i).setBookingMembers(
                    Arrays.stream(meetingRoom.getBookingMembers().split(","))
                        .map(Long::valueOf)
                        .collect(Collectors.toList())
                );
            }
        }
        return success(respList);
    }

}

