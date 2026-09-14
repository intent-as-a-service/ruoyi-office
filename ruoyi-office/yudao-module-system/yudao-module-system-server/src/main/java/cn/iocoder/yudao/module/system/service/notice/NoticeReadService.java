package cn.iocoder.yudao.module.system.service.notice;

import java.util.List;

/**
 * 用户公告已读 Service 接口
 *
 * @author ruoyi
 */
public interface NoticeReadService {

    /**
     * 标记公告为已读
     *
     * @param noticeId 公告ID
     * @param userId 用户ID
     */
    void markAsRead(Long noticeId, Long userId);

    /**
     * 批量标记公告为已读
     *
     * @param noticeIds 公告ID列表
     * @param userId 用户ID
     */
    void markAsReadBatch(List<Long> noticeIds, Long userId);

    /**
     * 查询用户已读的公告ID列表
     *
     * @param userId 用户ID
     * @param noticeIds 公告ID列表
     * @return 已读的公告ID列表
     */
    List<Long> getReadNoticeIds(Long userId, List<Long> noticeIds);

    /**
     * 查询用户是否已读指定公告
     *
     * @param userId 用户ID
     * @param noticeId 公告ID
     * @return 是否已读
     */
    boolean isRead(Long userId, Long noticeId);

}
