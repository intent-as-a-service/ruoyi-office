package cn.iocoder.yudao.module.oa.service.file;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.oa.controller.admin.file.vo.FileShareReqVO;
import cn.iocoder.yudao.module.oa.controller.admin.file.vo.FileShareRespVO;
import cn.iocoder.yudao.module.oa.controller.admin.file.vo.SharedFileListRespVO;
import cn.iocoder.yudao.module.oa.dal.dataobject.file.FileFavoriteDO;
import cn.iocoder.yudao.module.oa.dal.dataobject.file.FileInfoDO;
import cn.iocoder.yudao.module.oa.dal.dataobject.file.FilePermissionDO;
import cn.iocoder.yudao.module.oa.dal.mysql.file.FileFavoriteMapper;
import cn.iocoder.yudao.module.oa.dal.mysql.file.FileInfoMapper;
import cn.iocoder.yudao.module.oa.dal.mysql.file.FilePermissionMapper;
import cn.iocoder.yudao.module.oa.service.system.SystemService;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.module.oa.enums.ErrorCodeConstants.*;

/**
 * 文件分享服务实现类
 *
 * @author 宇擎源码
 */
@Service
@Slf4j
public class FileShareServiceImpl implements FileShareService {

    @Resource
    private FileInfoMapper fileInfoMapper;

    @Resource
    private FilePermissionMapper filePermissionMapper;

    @Resource
    private FileFavoriteMapper fileFavoriteMapper;

    @Resource
    private SystemService systemService;

    @Override
    @Transactional
    public Integer shareFile(FileShareReqVO shareReqVO) {
        // 1. 检查文件是否存在
        FileInfoDO fileInfo = fileInfoMapper.selectById(shareReqVO.getFileId());
        if (fileInfo == null) {
            throw ServiceExceptionUtil.exception(FILE_INFO_NOT_EXISTS);
        }

        // 2. 检查是否为文件所有者
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (!fileInfo.getOwnerId().equals(currentUserId)) {
            throw ServiceExceptionUtil.exception(FILE_SHARE_NOT_OWNER);
        }

        int shareCount = 0;
        
        // 3. 处理每个分享目标
        for (FileShareReqVO.ShareTarget target : shareReqVO.getShareTargets()) {
            // 检查是否已经分享过
            FilePermissionDO existingPermission = filePermissionMapper.selectByFileIdAndTarget(
                    shareReqVO.getFileId(), target.getShareType(), target.getTargetId());
            
            if (existingPermission != null) {
                // 更新权限
                existingPermission.setPermission(target.getPermission());
                existingPermission.setInheritPermission(shareReqVO.getInheritPermission());
                filePermissionMapper.updateById(existingPermission);
            } else {
                // 创建新的分享记录
                FilePermissionDO permission = new FilePermissionDO();
                permission.setFileId(shareReqVO.getFileId());
                permission.setShareType(target.getShareType());
                permission.setTargetId(target.getTargetId());
                permission.setTargetName(target.getTargetName());
                permission.setPermission(target.getPermission());
                permission.setInheritPermission(shareReqVO.getInheritPermission());
                permission.setAccessCount(0);
                
                // 计算分享路径和根分享ID
                calculateSharePath(permission, fileInfo);
                
                filePermissionMapper.insert(permission);
                shareCount++;
            }
        }

        // 4. 更新文件的分享状态
        if (shareCount > 0) {
            fileInfo.setIsShared(true);
            fileInfoMapper.updateById(fileInfo);
        }

        // 5. 如果开启权限继承且是文件夹，处理子文件夹
        if (shareReqVO.getInheritPermission() && fileInfo.getFileType() == 0) {
            for (FileShareReqVO.ShareTarget target : shareReqVO.getShareTargets()) {
                inheritPermissionsToChildren(shareReqVO.getFileId(), target.getShareType(), 
                        target.getTargetId(), target.getPermission());
            }
        }

        return shareCount;
    }

    @Override
    @Transactional
    public void unshareFile(Long fileId, Integer shareType, Long targetId) {
        // 1. 检查文件是否存在
        FileInfoDO fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            throw ServiceExceptionUtil.exception(FILE_INFO_NOT_EXISTS);
        }

        // 2. 检查是否为文件所有者
        Long currentUserId = SecurityFrameworkUtils.getLoginUserId();
        if (!fileInfo.getOwnerId().equals(currentUserId)) {
            throw ServiceExceptionUtil.exception(FILE_CANCEL_SHARE_NOT_OWNER);
        }

        // 3. 删除分享记录
        FilePermissionDO permission = filePermissionMapper.selectByFileIdAndTarget(fileId, shareType, targetId);
        if (permission != null) {
            filePermissionMapper.deleteById(permission.getId());
            
            // 4. 如果开启了权限继承，删除子文件的继承权限
            if (permission.getInheritPermission() && fileInfo.getFileType() == 0) {
                removeInheritedPermissions(fileId, shareType, targetId);
            }
        }

        // 5. 检查是否还有其他分享，更新文件分享状态
        List<FilePermissionDO> remainingShares = filePermissionMapper.selectListByFileId(fileId);
        if (remainingShares.isEmpty()) {
            fileInfo.setIsShared(false);
            fileInfoMapper.updateById(fileInfo);
        }
    }

    @Override
    public FileShareRespVO getFileShareInfo(Long fileId) {
        // 1. 获取文件信息
        FileInfoDO fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            throw ServiceExceptionUtil.exception(FILE_INFO_NOT_EXISTS);
        }

        // 2. 获取分享权限列表
        List<FilePermissionDO> permissions = filePermissionMapper.selectListByFileId(fileId);

        // 3. 构建响应对象
        FileShareRespVO respVO = BeanUtils.toBean(fileInfo, FileShareRespVO.class);
        respVO.setFileId(fileInfo.getId());
        
        List<FileShareRespVO.ShareTargetInfo> shareTargets = permissions.stream()
                .map(permission -> {
                    FileShareRespVO.ShareTargetInfo targetInfo = new FileShareRespVO.ShareTargetInfo();
                    targetInfo.setId(permission.getId());
                    targetInfo.setShareType(permission.getShareType());
                    targetInfo.setShareTypeName(permission.getShareType() == 0 ? "人员" : "组织");
                    targetInfo.setTargetId(permission.getTargetId());
                    targetInfo.setTargetName(permission.getTargetName());
                    targetInfo.setPermission(permission.getPermission());
                    targetInfo.setPermissionName(permission.getPermission() == 0 ? "仅查看" : "可管理");
                    targetInfo.setInheritPermission(permission.getInheritPermission());
                    targetInfo.setAccessCount(permission.getAccessCount());
                    targetInfo.setCreateTime(permission.getCreateTime());
                    return targetInfo;
                })
                .collect(Collectors.toList());
        
        respVO.setShareTargets(shareTargets);
        return respVO;
    }

    @Override
    public List<SharedFileListRespVO> getSharedFilesForUser(Long userId) {
        // 1. 获取用户信息和部门信息
        AdminUserRespDTO user = systemService.getUser(userId);
        List<Long> deptIds = new ArrayList<>();
        if (user != null && user.getDeptId() != null) {
            deptIds.add(user.getDeptId());
            // 可以扩展为获取用户所有相关部门
        }

        // 2. 获取用户直接分享的文件
        List<FilePermissionDO> directShares = filePermissionMapper.selectByUserId(userId);
        
        // 3. 获取用户所在部门的分享文件
        List<FilePermissionDO> orgShares = filePermissionMapper.selectByUserOrg(userId, deptIds);
        
        // 4. 合并并去重，取最高权限
        Map<Long, FilePermissionDO> shareMap = new HashMap<>();
        directShares.forEach(share -> shareMap.put(share.getFileId(), share));
        orgShares.forEach(share -> {
            if (!shareMap.containsKey(share.getFileId()) || 
                shareMap.get(share.getFileId()).getPermission() < share.getPermission()) {
                shareMap.put(share.getFileId(), share);
            }
        });

        // 5. 只返回根级别的分享文件（rootShareId为null或等于自身ID）
        List<FilePermissionDO> rootShares = shareMap.values().stream()
                .filter(share -> share.getRootShareId() == null || 
                        share.getRootShareId().equals(share.getFileId()))
                .collect(Collectors.toList());

        // 6. 获取文件信息并转换为响应对象
        List<SharedFileListRespVO> result = rootShares.stream()
                .map(share -> {
                    FileInfoDO fileInfo = fileInfoMapper.selectById(share.getFileId());
                    if (fileInfo == null) return null;
                    
                    SharedFileListRespVO respVO = BeanUtils.toBean(fileInfo, SharedFileListRespVO.class);
                    respVO.setFileId(fileInfo.getId());
                    respVO.setUserPermission(share.getPermission());
                    respVO.setPermissionName(share.getPermission() == 0 ? "仅查看" : "可管理");
                    respVO.setSharePath(share.getSharePath());
                    respVO.setRootShareId(share.getRootShareId());
                    respVO.setIsRootShare(true);
                    respVO.setCanEdit(share.getPermission() >= 1);
                    respVO.setCanDelete(share.getPermission() >= 1);
                    respVO.setCanShare(share.getPermission() >= 1);
                    return respVO;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 7. 补充收藏状态
        if (userId != null && !result.isEmpty()) {
            List<Long> fileIds = result.stream().map(SharedFileListRespVO::getFileId).collect(Collectors.toList());
            List<FileFavoriteDO> favorites = fileFavoriteMapper.selectList(new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<FileFavoriteDO>()
                    .eq(FileFavoriteDO::getUserId, userId)
                    .in(FileFavoriteDO::getFileId, fileIds));
            Set<Long> favoriteFileIds = favorites.stream().map(FileFavoriteDO::getFileId).collect(Collectors.toSet());
            result.forEach(item -> item.setIsFavorite(favoriteFileIds.contains(item.getFileId())));
        }

        return result;
    }

    @Override
    public List<SharedFileListRespVO> getSharedSubFiles(Long rootShareId, Long parentId, Long userId) {
        // 1. 检查用户对根分享文件夹的权限
        Integer rootPermission = checkFilePermission(rootShareId, userId);
        if (rootPermission < 0) {
            throw ServiceExceptionUtil.exception(FILE_SHARE_NO_PERMISSION);
        }

        // 2. 获取子文件列表
        List<FileInfoDO> subFiles = fileInfoMapper.selectListByParentId(parentId);

        // 3. 转换为响应对象并设置权限信息
        List<SharedFileListRespVO> result = subFiles.stream()
                .map(file -> {
                    SharedFileListRespVO respVO = BeanUtils.toBean(file, SharedFileListRespVO.class);
                    respVO.setFileId(file.getId());
                    respVO.setUserPermission(rootPermission); // 继承根权限
                    respVO.setPermissionName(rootPermission == 0 ? "仅查看" : "可管理");
                    respVO.setRootShareId(rootShareId);
                    respVO.setIsRootShare(false);
                    respVO.setCanEdit(rootPermission >= 1);
                    respVO.setCanDelete(rootPermission >= 1);
                    respVO.setCanShare(rootPermission >= 1);
                    return respVO;
                })
                .collect(Collectors.toList());

        // 4. 补充收藏状态
        if (userId != null && !result.isEmpty()) {
            List<Long> fileIds = result.stream().map(SharedFileListRespVO::getFileId).collect(Collectors.toList());
            List<FileFavoriteDO> favorites = fileFavoriteMapper.selectList(new cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX<FileFavoriteDO>()
                    .eq(FileFavoriteDO::getUserId, userId)
                    .in(FileFavoriteDO::getFileId, fileIds));
            Set<Long> favoriteFileIds = favorites.stream().map(FileFavoriteDO::getFileId).collect(Collectors.toSet());
            result.forEach(item -> item.setIsFavorite(favoriteFileIds.contains(item.getFileId())));
        }

        return result;
    }

    @Override
    public Integer checkFilePermission(Long fileId, Long userId) {
        // 1. 获取用户部门信息
        AdminUserRespDTO user = systemService.getUser(userId);
        List<Long> deptIds = new ArrayList<>();
        if (user != null && user.getDeptId() != null) {
            deptIds.add(user.getDeptId());
        }

        // 2. 检查直接权限和组织权限
        Integer maxPermission = filePermissionMapper.selectMaxPermissionByFileIdAndUser(fileId, userId, deptIds);
        if (maxPermission >= 0) {
            return maxPermission;
        }

        // 3. 检查继承权限（向上查找父文件夹权限）
        return checkInheritedPermission(fileId, userId, deptIds);
    }

    @Override
    public String getSharedFilePath(Long fileId, Long userId) {
        // 获取用户部门信息
        AdminUserRespDTO user = systemService.getUser(userId);
        List<Long> deptIds = new ArrayList<>();
        if (user != null && user.getDeptId() != null) {
            deptIds.add(user.getDeptId());
        }

        // 查找用户对该文件的权限记录
        List<FilePermissionDO> permissions = filePermissionMapper.selectByFileIdAndUser(fileId, userId, deptIds);
        if (!permissions.isEmpty()) {
            return permissions.get(0).getSharePath();
        }

        return null;
    }

    /**
     * 检查继承权限
     */
    private Integer checkInheritedPermission(Long fileId, Long userId, List<Long> deptIds) {
        FileInfoDO fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null || fileInfo.getParentId() == 0) {
            return -1; // 到达根目录，无权限
        }

        // 递归检查父文件夹权限
        Integer parentPermission = filePermissionMapper.selectMaxPermissionByFileIdAndUser(
                fileInfo.getParentId(), userId, deptIds);
        if (parentPermission >= 0) {
            // 检查父文件夹是否开启权限继承
            List<FilePermissionDO> parentShares = filePermissionMapper.selectByFileIdAndUser(
                    fileInfo.getParentId(), userId, deptIds);
            for (FilePermissionDO parentShare : parentShares) {
                if (parentShare.getInheritPermission()) {
                    return parentPermission;
                }
            }
        }

        // 继续向上查找
        return checkInheritedPermission(fileInfo.getParentId(), userId, deptIds);
    }

    /**
     * 计算分享路径
     */
    private void calculateSharePath(FilePermissionDO permission, FileInfoDO fileInfo) {
        List<String> pathParts = new ArrayList<>();
        FileInfoDO current = fileInfo;

        // 向上构建路径，直到找到根分享文件夹或到达根目录
        while (current != null && current.getParentId() != 0) {
            pathParts.add(0, current.getFileName());

            // 检查当前文件夹是否是根分享文件夹
            FilePermissionDO parentShare = filePermissionMapper.selectByFileIdAndTarget(
                    current.getId(), permission.getShareType(), permission.getTargetId());
            if (parentShare != null && parentShare.getRootShareId() == null) {
                // 找到根分享文件夹
                permission.setRootShareId(current.getId());
                break;
            }

            current = fileInfoMapper.selectById(current.getParentId());
        }

        // 如果没有找到根分享文件夹，则当前文件就是根分享
        if (permission.getRootShareId() == null) {
            permission.setRootShareId(fileInfo.getId());
            pathParts.clear();
            pathParts.add(fileInfo.getFileName());
        }

        permission.setSharePath(String.join("/", pathParts));
    }

    /**
     * 权限继承到子文件夹
     */
    private void inheritPermissionsToChildren(Long parentId, Integer shareType, Long targetId, Integer permission) {
        List<FileInfoDO> children = fileInfoMapper.selectListByParentId(parentId);

        for (FileInfoDO child : children) {
            // 检查是否已有权限记录
            FilePermissionDO existingPermission = filePermissionMapper.selectByFileIdAndTarget(
                    child.getId(), shareType, targetId);

            if (existingPermission == null) {
                // 创建继承权限
                FilePermissionDO childPermission = new FilePermissionDO();
                childPermission.setFileId(child.getId());
                childPermission.setShareType(shareType);
                childPermission.setTargetId(targetId);
                childPermission.setTargetName(getTargetName(shareType, targetId));
                childPermission.setPermission(permission);
                childPermission.setInheritPermission(true);
                childPermission.setAccessCount(0);

                // 设置根分享ID
                FilePermissionDO parentPermission = filePermissionMapper.selectByFileIdAndTarget(parentId, shareType, targetId);
                childPermission.setRootShareId(parentPermission.getRootShareId());

                filePermissionMapper.insert(childPermission);

                // 递归处理子文件夹
                if (child.getFileType() == 0) {
                    inheritPermissionsToChildren(child.getId(), shareType, targetId, permission);
                }
            }
        }
    }

    /**
     * 删除继承权限
     */
    private void removeInheritedPermissions(Long parentId, Integer shareType, Long targetId) {
        List<FileInfoDO> children = fileInfoMapper.selectListByParentId(parentId);

        for (FileInfoDO child : children) {
            FilePermissionDO childPermission = filePermissionMapper.selectByFileIdAndTarget(
                    child.getId(), shareType, targetId);
            
            if (childPermission != null && childPermission.getInheritPermission()) {
                filePermissionMapper.deleteById(childPermission.getId());
                
                // 递归删除子文件夹的继承权限
                if (child.getFileType() == 0) {
                    removeInheritedPermissions(child.getId(), shareType, targetId);
                }
            }
        }
    }

    /**
     * 获取目标名称
     */
    private String getTargetName(Integer shareType, Long targetId) {
        if (shareType == 0) {
            // 人员
            return systemService.getUserNickname(targetId);
        } else {
            // 组织
            return systemService.getDeptName(targetId);
        }
    }
}
