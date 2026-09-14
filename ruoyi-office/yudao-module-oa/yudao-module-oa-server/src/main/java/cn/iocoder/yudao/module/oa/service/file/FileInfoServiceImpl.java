package cn.iocoder.yudao.module.oa.service.file;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.oa.service.system.SystemService;
import cn.iocoder.yudao.module.oa.util.FileCategoryUtils;
import cn.iocoder.yudao.module.oa.controller.admin.file.vo.FileInfoPageReqVO;
import cn.iocoder.yudao.module.oa.controller.admin.file.vo.FileInfoRespVO;
import cn.iocoder.yudao.module.oa.controller.admin.file.vo.FileInfoSaveReqVO;
import cn.iocoder.yudao.module.oa.controller.admin.file.vo.FileStorageStatsRespVO;
import cn.iocoder.yudao.module.oa.dal.dataobject.file.FileFavoriteDO;
import cn.iocoder.yudao.module.oa.dal.dataobject.file.FileInfoDO;
import cn.iocoder.yudao.module.oa.dal.mysql.file.FileFavoriteMapper;
import cn.iocoder.yudao.module.oa.dal.mysql.file.FileInfoMapper;
import cn.iocoder.yudao.module.oa.dal.mysql.file.FilePermissionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.oa.enums.ErrorCodeConstants.FILE_INFO_NOT_EXISTS;

/**
 * 企业云盘-文件信息 Service 实现类
 *
 * @author 宇擎源码
 */
@Service
@Validated
public class FileInfoServiceImpl implements FileInfoService {

    @Resource
    private FileInfoMapper fileInfoMapper;

    @Resource
    private FilePermissionMapper filePermissionMapper;

    @Resource
    private FileFavoriteMapper fileFavoriteMapper;

    @Resource
    private FileApi fileApi;

    @Resource
    private SystemService systemService;

    @Resource
    private FileCategoryUtils fileCategoryUtils;

    @Override
    public Long uploadFile(MultipartFile file, Long parentId) throws Exception {
        // 0. 验证文件大小（100MB限制）
        long maxSize = 100 * 1024 * 1024; // 100MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("上传文件不能超过100M，请压缩后上传");
        }
        
        // 0.1. 获取当前用户信息
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        
        // 0.2. 校验用户存储空间限制（5GB）
        long maxStorageSize = 5L * 1024 * 1024 * 1024; // 5GB
        Long tenantId = TenantContextHolder.getTenantId();
        Long usedSize = fileInfoMapper.selectTotalFileSizeByOwnerId(userId, tenantId);
        if (usedSize == null) {
            usedSize = 0L;
        }
        if (usedSize + file.getSize() > maxStorageSize) {
            throw new IllegalArgumentException("存储空间不足，您的存储空间限制为5G，当前已使用" + 
                formatFileSize(usedSize) + "，无法上传该文件");
        }
        
        // 1. 上传文件到文件存储服务
        byte[] content = IoUtil.readBytes(file.getInputStream());
        String fileUrl = fileApi.createFile(content, file.getOriginalFilename(), "oa/cloud", file.getContentType());
        
        // 2. 获取文件信息
        String originalFilename = file.getOriginalFilename();
        String fileName = originalFilename;
        String fileExtension = "";
        String fileSuffix = "";
        if (StrUtil.isNotBlank(originalFilename) && originalFilename.contains(".")) {
            int lastDotIndex = originalFilename.lastIndexOf(".");
            fileName = originalFilename.substring(0, lastDotIndex);
            fileExtension = originalFilename.substring(lastDotIndex + 1);
            fileSuffix = fileExtension.toLowerCase(); // 文件后缀统一小写
        }
        
        // 3. 用户信息已在前面获取，这里不再重复获取
        
        // 4. 根据文件后缀获取文件分类
        String fileCategory = fileCategoryUtils.getFileCategoryBySuffix(fileSuffix);
        
        // 5. 创建文件信息记录
        FileInfoDO fileInfo = new FileInfoDO();
        fileInfo.setParentId(parentId);
        fileInfo.setFileType(1); // 1表示文件（0表示文件夹）
        fileInfo.setFileName(originalFilename);
        fileInfo.setFileExtension(fileExtension);
        fileInfo.setFileSuffix(fileSuffix);
        fileInfo.setFileCategory(fileCategory);
        fileInfo.setFileSize(file.getSize());
        fileInfo.setFileUrl(fileUrl);
        fileInfo.setOwnerId(userId);
        fileInfo.setOwnerName(systemService.getUserNickname(userId));
        
        // 获取用户部门信息
        var userInfo = systemService.getUser(userId);
        if (userInfo != null) {
            fileInfo.setDeptId(userInfo.getDeptId());
            fileInfo.setDeptName(systemService.getDeptName(userInfo.getDeptId()));
        }
        
        fileInfo.setSortOrder(0);
        
        // 6. 插入数据库
        fileInfoMapper.insert(fileInfo);
        
        return fileInfo.getId();
    }

    @Override
    public Long createFileInfo(FileInfoSaveReqVO createReqVO) {
        // 插入
        FileInfoDO fileInfo = BeanUtils.toBean(createReqVO, FileInfoDO.class);
        
        // 如果是文件且有后缀，但没有设置分类，则根据后缀自动设置分类
        if (fileInfo.getFileType() != null && fileInfo.getFileType() == 1 
                && StrUtil.isNotBlank(fileInfo.getFileSuffix()) 
                && StrUtil.isBlank(fileInfo.getFileCategory())) {
            fileInfo.setFileCategory(fileCategoryUtils.getFileCategoryBySuffix(fileInfo.getFileSuffix()));
        }
        
        // 如果没有设置所有者信息，则使用当前登录用户
        if (fileInfo.getOwnerId() == null) {
            Long userId = SecurityFrameworkUtils.getLoginUserId();
            fileInfo.setOwnerId(userId);
            fileInfo.setOwnerName(systemService.getUserNickname(userId));
            
            // 获取用户部门信息
            var userInfo = systemService.getUser(userId);
            if (userInfo != null) {
                fileInfo.setDeptId(userInfo.getDeptId());
                fileInfo.setDeptName(systemService.getDeptName(userInfo.getDeptId()));
            }
        }
        
        fileInfoMapper.insert(fileInfo);
        // 返回
        return fileInfo.getId();
    }

    @Override
    public void updateFileInfo(FileInfoSaveReqVO updateReqVO) {
        // 校验存在
        validateFileInfoExists(updateReqVO.getId());
        // 更新
        FileInfoDO updateObj = BeanUtils.toBean(updateReqVO, FileInfoDO.class);
        fileInfoMapper.updateById(updateObj);
    }

    @Override
    public void deleteFileInfo(Long id) {
        // 校验存在
        validateFileInfoExists(id);
        // 删除
        fileInfoMapper.deleteById(id);
        // 删除相关权限
        filePermissionMapper.deleteByFileId(id);
        // 删除相关收藏
        fileFavoriteMapper.delete(FileFavoriteDO::getFileId, id);
    }

    @Override
    public void deleteFileInfoListByIds(List<Long> ids) {
        // 校验存在
        validateFileInfoExists(ids);
        // 删除
        fileInfoMapper.deleteByIds(ids);
        // 删除相关权限和收藏
        ids.forEach(id -> {
            filePermissionMapper.deleteByFileId(id);
            fileFavoriteMapper.delete(FileFavoriteDO::getFileId, id);
        });
    }

    private void validateFileInfoExists(List<Long> ids) {
        List<FileInfoDO> list = fileInfoMapper.selectByIds(ids);
        if (CollUtil.isEmpty(list) || list.size() != ids.size()) {
            throw exception(FILE_INFO_NOT_EXISTS);
        }
    }

    private void validateFileInfoExists(Long id) {
        if (fileInfoMapper.selectById(id) == null) {
            throw exception(FILE_INFO_NOT_EXISTS);
        }
    }

    @Override
    public FileInfoDO getFileInfo(Long id) {
        return fileInfoMapper.selectById(id);
    }

    @Override
    public PageResult<FileInfoRespVO> getFileInfoPage(FileInfoPageReqVO pageReqVO) {
        // 自动添加所有者过滤条件（当前登录用户）
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (currentUserId != null) {
            pageReqVO.setOwnerId(currentUserId);
        }
        
        PageResult<FileInfoDO> pageResult = fileInfoMapper.selectPage(pageReqVO);
        PageResult<FileInfoRespVO> result = BeanUtils.toBean(pageResult, FileInfoRespVO.class);
        
        // 补充收藏状态
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId != null && CollUtil.isNotEmpty(result.getList())) {
            List<Long> fileIds = result.getList().stream().map(FileInfoRespVO::getId).collect(Collectors.toList());
            List<FileFavoriteDO> favorites = fileFavoriteMapper.selectList(new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<FileFavoriteDO>()
                    .eq(FileFavoriteDO::getUserId, userId)
                    .in(FileFavoriteDO::getFileId, fileIds));
            Set<Long> favoriteFileIds = favorites.stream().map(FileFavoriteDO::getFileId).collect(Collectors.toSet());
            result.getList().forEach(item -> item.setIsFavorite(favoriteFileIds.contains(item.getId())));
        }
        
        return result;
    }

    @Override
    public List<FileInfoRespVO> getFileInfoListByParentId(Long parentId, Long userId) {
        List<FileInfoDO> list = fileInfoMapper.selectListByParentId(parentId, userId);
        List<FileInfoRespVO> result = BeanUtils.toBean(list, FileInfoRespVO.class);
        
        // 补充收藏状态
        if (userId != null && CollUtil.isNotEmpty(result)) {
            List<Long> fileIds = result.stream().map(FileInfoRespVO::getId).collect(Collectors.toList());
            List<FileFavoriteDO> favorites = fileFavoriteMapper.selectList(new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<FileFavoriteDO>()
                    .eq(FileFavoriteDO::getUserId, userId)
                    .in(FileFavoriteDO::getFileId, fileIds));
            Set<Long> favoriteFileIds = favorites.stream().map(FileFavoriteDO::getFileId).collect(Collectors.toSet());
            result.forEach(item -> item.setIsFavorite(favoriteFileIds.contains(item.getId())));
        }
        
        return result;
    }

    @Override
    public void moveFileInfo(Long id, Long targetParentId) {
        // 校验存在
        validateFileInfoExists(id);
        validateFileInfoExists(targetParentId);
        
        // 更新父文件夹ID
        FileInfoDO updateObj = new FileInfoDO();
        updateObj.setId(id);
        updateObj.setParentId(targetParentId);
        fileInfoMapper.updateById(updateObj);
    }

    @Override
    public void renameFileInfo(Long id, String newName) {
        // 校验存在
        validateFileInfoExists(id);
        
        // 更新文件名
        FileInfoDO updateObj = new FileInfoDO();
        updateObj.setId(id);
        updateObj.setFileName(newName);
        fileInfoMapper.updateById(updateObj);
    }

    @Override
    public void favoriteFile(Long fileId, Long userId) {
        // 校验文件存在
        validateFileInfoExists(fileId);
        
        // 检查是否已收藏
        FileFavoriteDO existing = fileFavoriteMapper.selectByFileIdAndUserId(fileId, userId);
        if (existing != null) {
            return; // 已收藏，不重复添加
        }
        
        // 添加收藏
        FileFavoriteDO favorite = new FileFavoriteDO();
        favorite.setFileId(fileId);
        favorite.setUserId(userId);
        fileFavoriteMapper.insert(favorite);
    }

    @Override
    public void unfavoriteFile(Long fileId, Long userId) {
        // 物理删除收藏记录，避免唯一索引冲突
        Long tenantId = TenantContextHolder.getTenantId();
        fileFavoriteMapper.physicalDeleteByFileIdAndUserId(fileId, userId, tenantId);
    }

    @Override
    public List<FileInfoRespVO> getFavoriteFileList(Long userId) {
        // 获取收藏记录
        List<FileFavoriteDO> favorites = fileFavoriteMapper.selectListByUserId(userId);
        if (CollUtil.isEmpty(favorites)) {
            return Collections.emptyList();
        }
        
        // 获取文件信息
        List<Long> fileIds = favorites.stream().map(FileFavoriteDO::getFileId).collect(Collectors.toList());
        List<FileInfoDO> files = fileInfoMapper.selectByIds(fileIds);
        List<FileInfoRespVO> result = BeanUtils.toBean(files, FileInfoRespVO.class);
        
        // 标记为已收藏
        result.forEach(item -> item.setIsFavorite(true));
        
        return result;
    }

    @Override
    public FileStorageStatsRespVO getFileStorageStats(Long userId) {
        Long tenantId = TenantContextHolder.getTenantId();
        
        // 查询已用空间（仅文件，不包括文件夹）
        Long usedSize = fileInfoMapper.selectTotalFileSizeByOwnerId(userId, tenantId);
        if (usedSize == null) {
            usedSize = 0L;
        }
        
        // 查询文件数量（仅文件，不包括文件夹）
        Long fileCount = fileInfoMapper.selectFileCountByOwnerId(userId, tenantId);
        if (fileCount == null) {
            fileCount = 0L;
        }
        
        // 查询共享文件数量
        Long sharedFileCount = fileInfoMapper.selectSharedFileCountByOwnerId(userId, tenantId);
        if (sharedFileCount == null) {
            sharedFileCount = 0L;
        }
        
        // 总空间限制：5GB
        long totalSize = 5L * 1024 * 1024 * 1024;
        
        FileStorageStatsRespVO stats = new FileStorageStatsRespVO();
        stats.setUsedSize(usedSize);
        stats.setTotalSize(totalSize);
        stats.setFileCount(fileCount);
        stats.setSharedFileCount(sharedFileCount);
        
        return stats;
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + "B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2fKB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2fMB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2fGB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

}

