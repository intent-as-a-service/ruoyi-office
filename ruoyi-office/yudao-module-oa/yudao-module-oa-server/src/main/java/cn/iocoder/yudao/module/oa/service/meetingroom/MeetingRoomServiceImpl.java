package cn.iocoder.yudao.module.oa.service.meetingroom;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;
import cn.iocoder.yudao.module.oa.controller.admin.meetingroom.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.meetingroom.MeetingRoomDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.oa.dal.mysql.meetingroom.MeetingRoomMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.oa.enums.ErrorCodeConstants.*;

/**
 * 会议室信息 Service 实现类
 *
 * @author 宇擎源码
 */
@Service
@Validated
public class MeetingRoomServiceImpl implements MeetingRoomService {

    @Resource
    private MeetingRoomMapper meetingRoomMapper;

    @Override
    public Long createMeetingRoom(MeetingRoomSaveReqVO createReqVO) {
        // 转换并插入
        MeetingRoomDO meetingRoom = BeanUtils.toBean(createReqVO, MeetingRoomDO.class);
        // 处理设备列表（数组转逗号分隔字符串）
        if (CollUtil.isNotEmpty(createReqVO.getEquipment())) {
            meetingRoom.setEquipment(String.join(",", createReqVO.getEquipment()));
        }
        // 处理预定成员列表（数组转逗号分隔字符串）
        if (CollUtil.isNotEmpty(createReqVO.getBookingMembers())) {
            meetingRoom.setBookingMembers(
                createReqVO.getBookingMembers().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","))
            );
        }
        meetingRoomMapper.insert(meetingRoom);
        // 返回
        return meetingRoom.getId();
    }

    @Override
    public void updateMeetingRoom(MeetingRoomSaveReqVO updateReqVO) {
        // 校验存在
        validateMeetingRoomExists(updateReqVO.getId());
        // 转换并更新
        MeetingRoomDO updateObj = BeanUtils.toBean(updateReqVO, MeetingRoomDO.class);
        // 处理设备列表（数组转逗号分隔字符串）
        if (CollUtil.isNotEmpty(updateReqVO.getEquipment())) {
            updateObj.setEquipment(String.join(",", updateReqVO.getEquipment()));
        } else {
            updateObj.setEquipment(null);
        }
        // 处理预定成员列表（数组转逗号分隔字符串）
        if (CollUtil.isNotEmpty(updateReqVO.getBookingMembers())) {
            updateObj.setBookingMembers(
                updateReqVO.getBookingMembers().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","))
            );
        } else {
            updateObj.setBookingMembers(null);
        }
        meetingRoomMapper.updateById(updateObj);
    }

    @Override
    public void deleteMeetingRoom(Long id) {
        // 校验存在
        validateMeetingRoomExists(id);
        // 删除
        meetingRoomMapper.deleteById(id);
    }

    @Override
    public void deleteMeetingRoomListByIds(List<Long> ids) {
        // 校验存在
        validateMeetingRoomExists(ids);
        // 删除
        meetingRoomMapper.deleteByIds(ids);
    }

    private void validateMeetingRoomExists(List<Long> ids) {
        List<MeetingRoomDO> list = meetingRoomMapper.selectByIds(ids);
        if (CollUtil.isEmpty(list) || list.size() != ids.size()) {
            throw exception(MEETING_ROOM_NOT_EXISTS);
        }
    }

    private void validateMeetingRoomExists(Long id) {
        if (meetingRoomMapper.selectById(id) == null) {
            throw exception(MEETING_ROOM_NOT_EXISTS);
        }
    }

    @Override
    public MeetingRoomDO getMeetingRoom(Long id) {
        return meetingRoomMapper.selectById(id);
    }

    @Override
    public PageResult<MeetingRoomDO> getMeetingRoomPage(MeetingRoomPageReqVO pageReqVO) {
        return meetingRoomMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<MeetingRoomDO> getBookableMeetingRoomPage(MeetingRoomPageReqVO pageReqVO, Long currentUserId) {
        // 设置过滤条件：可用状态为正常（0）、允许预定（true）
        pageReqVO.setAvailableStatus(0);
        pageReqVO.setAllowBooking(true);
        
        // 查询符合条件的会议室
        PageResult<MeetingRoomDO> pageResult = meetingRoomMapper.selectPage(pageReqVO);
        
        // 如果当前用户ID不为空，则根据可用范围进一步过滤
        if (currentUserId != null) {
            List<MeetingRoomDO> filteredList = pageResult.getList().stream()
                    .filter(room -> {
                        // 如果可用范围为全部成员（0）或未设置，则允许
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
            
            // 重新构建分页结果，总数使用过滤后的列表大小
            return new PageResult<>(filteredList, (long) filteredList.size());
        }
        
        return pageResult;
    }

}

