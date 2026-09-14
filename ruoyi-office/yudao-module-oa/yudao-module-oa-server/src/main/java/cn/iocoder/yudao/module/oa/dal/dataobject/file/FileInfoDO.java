package cn.iocoder.yudao.module.oa.dal.dataobject.file;

import lombok.*;

import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 企业云盘-文件信息 DO
 *
 * @author 宇擎源码
 */
@TableName("oa_file_info")
@KeySequence("oa_file_info_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库,可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoDO extends BaseDO {

    /**
     * 文件ID
     */
    @TableId
    private Long id;
    
    /**
     * 父文件夹ID
     */
    private Long parentId;
    
    /**
     * 文件类型(0文件 1文件夹)
     */
    private Integer fileType;
    
    /**
     * 文件名称
     */
    private String fileName;
    
    /**
     * 文件扩展名
     */
    private String fileExtension;
    
    /**
     * 文件后缀名（不含点号）
     */
    private String fileSuffix;
    
    /**
     * 文件分类（all全部 image图片 document文档 video视频 audio音频 archive压缩包 other其他）
     */
    private String fileCategory;
    
    /**
     * 文件大小(字节)
     */
    private Long fileSize;
    
    /**
     * 文件存储路径
     */
    private String filePath;
    
    /**
     * 文件访问URL
     */
    private String fileUrl;
    
    /**
     * 文件MD5值
     */
    private String fileMd5;
    
    /**
     * 所有者ID
     */
    private Long ownerId;
    
    /**
     * 所有者名称
     */
    private String ownerName;
    
    /**
     * 部门ID
     */
    private Long deptId;
    
    /**
     * 部门名称
     */
    private String deptName;
    
    /**
     * 是否共享文件夹(0否 1是)
     */
    private Boolean isShared;
    
    /**
     * 文件夹分享类型(0人员 1组织)
     */
    private Integer shareType;
    
    /**
     * 分享权限(0仅查看 1可管理)
     */
    private Integer sharePermission;
    
    /**
     * 排序
     */
    private Integer sortOrder;

}

