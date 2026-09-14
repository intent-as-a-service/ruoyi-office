package cn.iocoder.yudao.module.oa.service.file;

import cn.iocoder.yudao.module.oa.controller.admin.file.vo.FileShareReqVO;
import cn.iocoder.yudao.module.oa.controller.admin.file.vo.FileShareRespVO;
import cn.iocoder.yudao.module.oa.controller.admin.file.vo.SharedFileListRespVO;

import java.util.List;

/**
 * 文件分享服务接口
 *
 * @author 宇擎源码
 */
public interface FileShareService {

    /**
     * 分享文件/文件夹
     *
     * @param shareReqVO 分享请求参数
     * @return 分享成功的记录数
     */
    Integer shareFile(FileShareReqVO shareReqVO);

    /**
     * 取消分享
     *
     * @param fileId 文件ID
     * @param shareType 分享类型
     * @param targetId 目标ID
     */
    void unshareFile(Long fileId, Integer shareType, Long targetId);

    /**
     * 获取文件的分享信息
     *
     * @param fileId 文件ID
     * @return 分享信息
     */
    FileShareRespVO getFileShareInfo(Long fileId);

    /**
     * 获取用户可访问的共享文件列表(根级别)
     *
     * @param userId 用户ID
     * @return 共享文件列表
     */
    List<SharedFileListRespVO> getSharedFilesForUser(Long userId);

    /**
     * 获取指定共享文件夹下的子文件列表
     *
     * @param rootShareId 根分享ID
     * @param parentId 父文件夹ID
     * @param userId 用户ID
     * @return 子文件列表
     */
    List<SharedFileListRespVO> getSharedSubFiles(Long rootShareId, Long parentId, Long userId);

    /**
     * 检查用户对文件的访问权限
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 权限级别(-1表示无权限, 0仅查看, 1可管理)
     */
    Integer checkFilePermission(Long fileId, Long userId);

    /**
     * 获取文件的完整共享路径
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 共享路径
     */
    String getSharedFilePath(Long fileId, Long userId);
}
