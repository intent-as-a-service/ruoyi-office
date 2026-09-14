package cn.iocoder.yudao.common.server.attachment.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 通用附件信息 DO
 *
 * @author 宇擎源码
 */
@TableName("common_attachment")
@KeySequence("common_attachment_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDO extends BaseDO {

    /**
     * 附件ID
     */
    @TableId
    private Long id;
    
    /**
     * 业务类型（如：seal_apply_bill、car_apply_bill等）
     */
    private String businessType;
    
    /**
     * 业务单据ID
     */
    private Long businessId;
    
    /**
     * 文件名称
     */
    private String fileName;
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 文件访问URL
     */
    private String fileUrl;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件类型（MIME类型）
     */
    private String fileType;
    
    /**
     * 文件扩展名
     */
    private String fileExtension;
    
    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 备注
     */
    private String remark;

}
