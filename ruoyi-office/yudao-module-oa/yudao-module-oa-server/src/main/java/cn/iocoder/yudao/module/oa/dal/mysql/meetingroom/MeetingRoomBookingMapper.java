package cn.iocoder.yudao.module.oa.dal.mysql.meetingroom;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.oa.dal.dataobject.meetingroom.MeetingRoomBookingDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.oa.controller.admin.meetingroom.vo.*;

/**
 * 会议室预定申请单 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface MeetingRoomBookingMapper extends BaseMapperX<MeetingRoomBookingDO> {

    default PageResult<MeetingRoomBookingDO> selectPage(MeetingRoomBookingPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MeetingRoomBookingDO>()
                .eqIfPresent(MeetingRoomBookingDO::getCreator, reqVO.getCreator())
                .likeIfPresent(MeetingRoomBookingDO::getBillCode, reqVO.getBillCode())
                .likeIfPresent(MeetingRoomBookingDO::getRoomName, reqVO.getRoomName())
                .likeIfPresent(MeetingRoomBookingDO::getMeetingTitle, reqVO.getMeetingTitle())
                .likeIfPresent(MeetingRoomBookingDO::getModeratorName, reqVO.getModeratorName())
                .eqIfPresent(MeetingRoomBookingDO::getProcessStatus, reqVO.getProcessStatus())
                .eqIfPresent(MeetingRoomBookingDO::getUseStatus, reqVO.getUseStatus())
                .betweenIfPresent(MeetingRoomBookingDO::getMeetingStartTime, reqVO.getMeetingStartTime())
                .betweenIfPresent(MeetingRoomBookingDO::getMeetingEndTime, reqVO.getMeetingEndTime())
                .betweenIfPresent(MeetingRoomBookingDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MeetingRoomBookingDO::getId));
    }

    default MeetingRoomBookingDO selectByBillCode(String billCode) {
        return selectOne(MeetingRoomBookingDO::getBillCode, billCode);
    }

}

