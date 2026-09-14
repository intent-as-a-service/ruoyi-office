package cn.iocoder.yudao.module.oa.dal.mysql.file;

import java.util.*;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.oa.dal.dataobject.file.FilePermissionDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业云盘-文件权限 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface FilePermissionMapper extends BaseMapperX<FilePermissionDO> {

    default List<FilePermissionDO> selectListByFileId(Long fileId) {
        return selectList(FilePermissionDO::getFileId, fileId);
    }

    default void deleteByFileId(Long fileId) {
        delete(FilePermissionDO::getFileId, fileId);
    }

    default FilePermissionDO selectByFileIdAndTarget(Long fileId, Integer shareType, Long targetId) {
        return selectOne(new LambdaQueryWrapperX<FilePermissionDO>()
                .eq(FilePermissionDO::getFileId, fileId)
                .eq(FilePermissionDO::getShareType, shareType)
                .eq(FilePermissionDO::getTargetId, targetId));
    }

    default List<FilePermissionDO> selectByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<FilePermissionDO>()
                .eq(FilePermissionDO::getShareType, 0) // 人员分享
                .eq(FilePermissionDO::getTargetId, userId));
    }

    default List<FilePermissionDO> selectByUserOrg(Long userId, List<Long> deptIds) {
        if (deptIds == null || deptIds.isEmpty()) {
            return new ArrayList<>();
        }
        return selectList(new LambdaQueryWrapperX<FilePermissionDO>()
                .eq(FilePermissionDO::getShareType, 1) // 组织分享
                .in(FilePermissionDO::getTargetId, deptIds));
    }

    default List<FilePermissionDO> selectByFileIdAndUser(Long fileId, Long userId, List<Long> deptIds) {
        List<FilePermissionDO> result = new ArrayList<>();
        
        // 查询直接分享给用户的权限
        FilePermissionDO userPermission = selectOne(new LambdaQueryWrapperX<FilePermissionDO>()
                .eq(FilePermissionDO::getFileId, fileId)
                .eq(FilePermissionDO::getShareType, 0)
                .eq(FilePermissionDO::getTargetId, userId));
        if (userPermission != null) {
            result.add(userPermission);
        }
        
        // 查询分享给用户所在组织的权限
        if (deptIds != null && !deptIds.isEmpty()) {
            List<FilePermissionDO> orgPermissions = selectList(new LambdaQueryWrapperX<FilePermissionDO>()
                    .eq(FilePermissionDO::getFileId, fileId)
                    .eq(FilePermissionDO::getShareType, 1)
                    .in(FilePermissionDO::getTargetId, deptIds));
            result.addAll(orgPermissions);
        }
        
        return result;
    }

    default Integer selectMaxPermissionByFileIdAndUser(Long fileId, Long userId, List<Long> deptIds) {
        List<FilePermissionDO> permissions = selectByFileIdAndUser(fileId, userId, deptIds);
        return permissions.stream()
                .mapToInt(FilePermissionDO::getPermission)
                .max()
                .orElse(-1); // -1表示无权限
    }

}

