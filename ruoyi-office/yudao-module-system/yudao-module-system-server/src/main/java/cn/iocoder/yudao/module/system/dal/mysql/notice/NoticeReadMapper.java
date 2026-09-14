package cn.iocoder.yudao.module.system.dal.mysql.notice;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.notice.NoticeReadDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeReadMapper extends BaseMapperX<NoticeReadDO> {

    /**
     * 查询用户已读的公告ID列表
     *
     * @param userId 用户ID
     * @param noticeIds 公告ID列表
     * @return 已读的公告ID列表
     */
    default List<Long> selectReadNoticeIds(Long userId, List<Long> noticeIds) {
        if (noticeIds == null || noticeIds.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapperX<NoticeReadDO>()
                .eq(NoticeReadDO::getUserId, userId)
                .in(NoticeReadDO::getNoticeId, noticeIds))
                .stream()
                .map(NoticeReadDO::getNoticeId)
                .toList();
    }

    /**
     * 查询用户是否已读指定公告
     *
     * @param userId 用户ID
     * @param noticeId 公告ID
     * @return 是否已读
     */
    default boolean isRead(Long userId, Long noticeId) {
        return selectOne(NoticeReadDO::getUserId, userId,
                NoticeReadDO::getNoticeId, noticeId) != null;
    }

}
