package cn.iocoder.yudao.module.system.controller.admin.notice;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.infra.api.websocket.WebSocketSenderApi;
import cn.iocoder.yudao.module.system.controller.admin.notice.vo.NoticePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.notice.vo.NoticeRespVO;
import cn.iocoder.yudao.module.system.controller.admin.notice.vo.NoticeSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.notice.NoticeDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.notice.NoticeReadService;
import cn.iocoder.yudao.module.system.service.notice.NoticeService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 通知公告")
@RestController
@RequestMapping("/system/notice")
@Validated
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    @Resource
    private NoticeReadService noticeReadService;

    @Resource
    private AdminUserService adminUserService;

    @Resource
    private WebSocketSenderApi webSocketSenderApi;

    @PostMapping("/create")
    @Operation(summary = "创建通知公告")
    @PreAuthorize("@ss.hasPermission('system:notice:create')")
    public CommonResult<Long> createNotice(@Valid @RequestBody NoticeSaveReqVO createReqVO) {
        Long noticeId = noticeService.createNotice(createReqVO);
        return success(noticeId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改通知公告")
    @PreAuthorize("@ss.hasPermission('system:notice:update')")
    public CommonResult<Boolean> updateNotice(@Valid @RequestBody NoticeSaveReqVO updateReqVO) {
        noticeService.updateNotice(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除通知公告")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:notice:delete')")
    public CommonResult<Boolean> deleteNotice(@RequestParam("id") Long id) {
        noticeService.deleteNotice(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除通知公告")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('system:notice:delete')")
    public CommonResult<Boolean> deleteNoticeList(@RequestParam("ids") List<Long> ids) {
        noticeService.deleteNoticeList(ids);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获取通知公告列表")
    @PreAuthorize("@ss.hasPermission('system:notice:query')")
    public CommonResult<PageResult<NoticeRespVO>> getNoticePage(@Validated NoticePageReqVO pageReqVO) {
        PageResult<NoticeDO> pageResult = noticeService.getNoticePage(pageReqVO);
        PageResult<NoticeRespVO> voResult = BeanUtils.toBean(pageResult, NoticeRespVO.class);
        
        // 设置已读状态和创建者名称
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (voResult.getList() != null && !voResult.getList().isEmpty()) {
            // 设置已读状态
            if (userId != null) {
                List<Long> noticeIds = voResult.getList().stream()
                        .map(NoticeRespVO::getId)
                        .toList();
                List<Long> readNoticeIds = noticeReadService.getReadNoticeIds(userId, noticeIds);
                Set<Long> readSet = readNoticeIds.stream().collect(Collectors.toSet());
                voResult.getList().forEach(vo -> vo.setReadStatus(readSet.contains(vo.getId()) ? 1 : 0));
            }
            
            // 设置创建者名称
            List<Long> creatorIds = voResult.getList().stream()
                    .map(NoticeRespVO::getCreator)
                    .filter(creator -> creator != null && !creator.isEmpty())
                    .map(creator -> {
                        try {
                            return Long.parseLong(creator);
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    })
                    .filter(id -> id != null)
                    .distinct()
                    .toList();
            
            if (!creatorIds.isEmpty()) {
                Map<Long, AdminUserDO> userMap = adminUserService.getUserMap(creatorIds);
                voResult.getList().forEach(vo -> {
                    if (vo.getCreator() != null && !vo.getCreator().isEmpty()) {
                        try {
                            Long creatorId = Long.parseLong(vo.getCreator());
                            AdminUserDO user = userMap.get(creatorId);
                            if (user != null) {
                                vo.setCreatorName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                            }
                        } catch (NumberFormatException e) {
                            // 忽略非数字的creator
                        }
                    }
                });
            }
        }
        
        return success(voResult);
    }

    @GetMapping("/get")
    @Operation(summary = "获得通知公告")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:notice:query')")
    public CommonResult<NoticeRespVO> getNotice(@RequestParam("id") Long id) {
        NoticeDO notice = noticeService.getNotice(id);
        NoticeRespVO vo = BeanUtils.toBean(notice, NoticeRespVO.class);
        
        // 设置已读状态
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId != null) {
            vo.setReadStatus(noticeReadService.isRead(userId, id) ? 1 : 0);
        }
        
        // 设置创建者名称
        if (vo.getCreator() != null && !vo.getCreator().isEmpty()) {
            try {
                Long creatorId = Long.parseLong(vo.getCreator());
                AdminUserDO user = adminUserService.getUser(creatorId);
                if (user != null) {
                    vo.setCreatorName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                }
            } catch (NumberFormatException e) {
                // 忽略非数字的creator
            }
        }
        
        return success(vo);
    }

    @PostMapping("/mark-read")
    @Operation(summary = "标记公告为已读")
    @Parameter(name = "id", description = "公告编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:notice:query')")
    public CommonResult<Boolean> markAsRead(@RequestParam("id") Long id) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId != null) {
            noticeReadService.markAsRead(id, userId);
        }
        return success(true);
    }

    @PostMapping("/push")
    @Operation(summary = "推送通知公告", description = "只发送给 websocket 连接在线的用户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:notice:update')")
    public CommonResult<Boolean> push(@RequestParam("id") Long id) {
        NoticeDO notice = noticeService.getNotice(id);
        Assert.notNull(notice, "公告不能为空");
        // 通过 websocket 推送给在线的用户
        webSocketSenderApi.sendObject(UserTypeEnum.ADMIN.getValue(), "notice-push", notice);
        return success(true);
    }

}
