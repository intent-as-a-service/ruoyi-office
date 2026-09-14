package cn.iocoder.yudao.module.oa.dal.mysql.file;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.oa.dal.dataobject.file.FileInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import cn.iocoder.yudao.module.oa.controller.admin.file.vo.*;

/**
 * 企业云盘-文件信息 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface FileInfoMapper extends BaseMapperX<FileInfoDO> {

    default PageResult<FileInfoDO> selectPage(FileInfoPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FileInfoDO>()
                .eqIfPresent(FileInfoDO::getParentId, reqVO.getParentId())
                .eqIfPresent(FileInfoDO::getFileType, reqVO.getFileType())
                .likeIfPresent(FileInfoDO::getFileName, reqVO.getFileName())
                .eqIfPresent(FileInfoDO::getOwnerId, reqVO.getOwnerId())
                .likeIfPresent(FileInfoDO::getOwnerName, reqVO.getOwnerName())
                .eqIfPresent(FileInfoDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(FileInfoDO::getIsShared, reqVO.getIsShared())
                .eqIfPresent(FileInfoDO::getShareType, reqVO.getShareType())
                .eqIfPresent(FileInfoDO::getFileCategory, reqVO.getFileCategoryFilter())
                .betweenIfPresent(FileInfoDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(FileInfoDO::getFileType) // 文件夹排前面（0=文件夹，1=文件）
                .orderByDesc(FileInfoDO::getUpdateTime) // 按更新时间倒序
                .orderByAsc(FileInfoDO::getSortOrder));
    }

    default List<FileInfoDO> selectListByParentId(Long parentId) {
        return selectList(new LambdaQueryWrapperX<FileInfoDO>()
                .eq(FileInfoDO::getParentId, parentId)
                .orderByAsc(FileInfoDO::getFileType) // 文件夹排前面（0=文件夹，1=文件）
                .orderByDesc(FileInfoDO::getUpdateTime) // 按更新时间倒序
                .orderByAsc(FileInfoDO::getSortOrder));
    }

    default List<FileInfoDO> selectListByParentId(Long parentId, Long ownerId) {
        return selectList(new LambdaQueryWrapperX<FileInfoDO>()
                .eq(FileInfoDO::getParentId, parentId)
                .eq(FileInfoDO::getOwnerId, ownerId)
                .orderByAsc(FileInfoDO::getFileType) // 文件夹排前面（0=文件夹，1=文件）
                .orderByDesc(FileInfoDO::getUpdateTime) // 按更新时间倒序
                .orderByAsc(FileInfoDO::getSortOrder));
    }

    /**
     * 查询用户上传的文件总大小（仅文件，不包括文件夹）
     *
     * @param ownerId 用户ID
     * @return 文件总大小（字节）
     */
    @Select("SELECT COALESCE(SUM(file_size), 0) FROM oa_file_info " +
            "WHERE owner_id = #{ownerId} AND file_type = 1 AND deleted = 0 AND tenant_id = #{tenantId}")
    Long selectTotalFileSizeByOwnerId(@Param("ownerId") Long ownerId, @Param("tenantId") Long tenantId);

    /**
     * 查询用户上传的文件数量（仅文件，不包括文件夹）
     *
     * @param ownerId 用户ID
     * @return 文件数量
     */
    @Select("SELECT COUNT(*) FROM oa_file_info " +
            "WHERE owner_id = #{ownerId} AND file_type = 1 AND deleted = 0 AND tenant_id = #{tenantId}")
    Long selectFileCountByOwnerId(@Param("ownerId") Long ownerId, @Param("tenantId") Long tenantId);

    /**
     * 查询用户共享的文件数量
     *
     * @param ownerId 用户ID
     * @return 共享文件数量
     */
    @Select("SELECT COUNT(DISTINCT fp.file_id) FROM oa_file_permission fp " +
            "INNER JOIN oa_file_info fi ON fp.file_id = fi.id " +
            "WHERE fi.owner_id = #{ownerId} AND fp.deleted = 0 AND fi.deleted = 0 AND fp.tenant_id = #{tenantId}")
    Long selectSharedFileCountByOwnerId(@Param("ownerId") Long ownerId, @Param("tenantId") Long tenantId);

}

