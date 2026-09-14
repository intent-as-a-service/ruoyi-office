package cn.iocoder.yudao.module.system.enums.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知类型
 * 
 * 注意：此枚举已废弃，公告类型现在使用字典 system_notice_type 管理
 * 字典包含：通知公告、公司动态、行业咨询、规章制度等
 *
 * @author 宇擎源码
 * @deprecated 使用字典 system_notice_type 替代
 */
@Getter
@AllArgsConstructor
@Deprecated
public enum NoticeTypeEnum {

    NOTICE(1),
    ANNOUNCEMENT(2);

    /**
     * 类型
     */
    private final Integer type;

}
