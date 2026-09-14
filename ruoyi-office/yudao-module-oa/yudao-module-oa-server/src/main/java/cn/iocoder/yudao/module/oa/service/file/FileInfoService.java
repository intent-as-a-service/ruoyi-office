package cn.iocoder.yudao.module.oa.service.file;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.oa.controller.admin.file.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.file.FileInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * 企业云盘-文件信息 Service 接口
 *
 * @author 宇擎源码
 */
public interface FileInfoService {

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @param parentId 父文件夹ID
     * @return 文件ID
     */
    Long uploadFile(MultipartFile file, Long parentId) throws Exception;

    /**
     * 创建文件/文件夹
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createFileInfo(@Valid FileInfoSaveReqVO createReqVO);

    /**
     * 更新文件/文件夹
     *
     * @param updateReqVO 更新信息
     */
    void updateFileInfo(@Valid FileInfoSaveReqVO updateReqVO);

    /**
     * 删除文件/文件夹
     *
     * @param id 编号
     */
    void deleteFileInfo(Long id);

    /**
     * 批量删除文件/文件夹
     *
     * @param ids 编号
     */
    void deleteFileInfoListByIds(List<Long> ids);

    /**
     * 获得文件/文件夹
     *
     * @param id 编号
     * @return 文件/文件夹
     */
    FileInfoDO getFileInfo(Long id);

    /**
     * 获得文件/文件夹分页
     *
     * @param pageReqVO 分页查询
     * @return 文件/文件夹分页
     */
    PageResult<FileInfoRespVO> getFileInfoPage(FileInfoPageReqVO pageReqVO);

    /**
     * 获得指定文件夹下的文件列表
     *
     * @param parentId 父文件夹ID
     * @param userId 用户ID
     * @return 文件列表
     */
    List<FileInfoRespVO> getFileInfoListByParentId(Long parentId, Long userId);

    /**
     * 移动文件/文件夹
     *
     * @param id 文件ID
     * @param targetParentId 目标文件夹ID
     */
    void moveFileInfo(Long id, Long targetParentId);

    /**
     * 重命名文件/文件夹
     *
     * @param id 文件ID
     * @param newName 新名称
     */
    void renameFileInfo(Long id, String newName);

    /**
     * 收藏文件
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     */
    void favoriteFile(Long fileId, Long userId);

    /**
     * 取消收藏文件
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     */
    void unfavoriteFile(Long fileId, Long userId);

    /**
     * 获取用户收藏的文件列表
     *
     * @param userId 用户ID
     * @return 文件列表
     */
    List<FileInfoRespVO> getFavoriteFileList(Long userId);

    /**
     * 获取用户文件存储统计信息
     *
     * @param userId 用户ID
     * @return 存储统计信息
     */
    FileStorageStatsRespVO getFileStorageStats(Long userId);

}

