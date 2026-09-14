package cn.iocoder.yudao.module.system.service.notice;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.notice.NoticeReadDO;
import cn.iocoder.yudao.module.system.dal.mysql.notice.NoticeReadMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户公告已读 Service 实现类
 *
 * @author ruoyi
 */
@Service
public class NoticeReadServiceImpl implements NoticeReadService {

    @Resource
    private NoticeReadMapper noticeReadMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long noticeId, Long userId) {
        // 检查是否已存在
        if (noticeReadMapper.isRead(userId, noticeId)) {
            return;
        }
        // 创建已读记录
        NoticeReadDO readDO = new NoticeReadDO();
        readDO.setNoticeId(noticeId);
        readDO.setUserId(userId);
        readDO.setReadTime(LocalDateTime.now());
        noticeReadMapper.insert(readDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsReadBatch(List<Long> noticeIds, Long userId) {
        if (noticeIds == null || noticeIds.isEmpty()) {
            return;
        }
        // 查询已读的公告ID
        List<Long> readNoticeIds = noticeReadMapper.selectReadNoticeIds(userId, noticeIds);
        // 过滤出未读的公告ID
        List<Long> unreadNoticeIds = noticeIds.stream()
                .filter(id -> !readNoticeIds.contains(id))
                .toList();
        // 批量插入
        if (!unreadNoticeIds.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            List<NoticeReadDO> readList = unreadNoticeIds.stream()
                    .map(noticeId -> {
                        NoticeReadDO readDO = new NoticeReadDO();
                        readDO.setNoticeId(noticeId);
                        readDO.setUserId(userId);
                        readDO.setReadTime(now);
                        return readDO;
                    })
                    .toList();
            readList.forEach(noticeReadMapper::insert);
        }
    }

    @Override
    public List<Long> getReadNoticeIds(Long userId, List<Long> noticeIds) {
        if (noticeIds == null || noticeIds.isEmpty()) {
            return List.of();
        }
        return noticeReadMapper.selectReadNoticeIds(userId, noticeIds);
    }

    @Override
    public boolean isRead(Long userId, Long noticeId) {
        return noticeReadMapper.isRead(userId, noticeId);
    }

}
