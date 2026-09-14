package cn.iocoder.yudao.module.oa.dal.mysql.file;

import java.util.*;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.oa.dal.dataobject.file.FileFavoriteDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 企业云盘-收藏文件 Mapper
 *
 * @author 宇擎源码
 */
@Mapper
public interface FileFavoriteMapper extends BaseMapperX<FileFavoriteDO> {

    default List<FileFavoriteDO> selectListByUserId(Long userId) {
        return selectList(FileFavoriteDO::getUserId, userId);
    }

    default FileFavoriteDO selectByFileIdAndUserId(Long fileId, Long userId) {
        return selectOne(new LambdaQueryWrapperX<FileFavoriteDO>()
                .eq(FileFavoriteDO::getFileId, fileId)
                .eq(FileFavoriteDO::getUserId, userId));
    }

    default void deleteByFileIdAndUserId(Long fileId, Long userId) {
        delete(new LambdaQueryWrapperX<FileFavoriteDO>()
                .eq(FileFavoriteDO::getFileId, fileId)
                .eq(FileFavoriteDO::getUserId, userId));
    }

    /**
     * 物理删除收藏记录，避免唯一索引冲突
     * 使用原生SQL绕过MyBatis-Plus的逻辑删除机制
     */
    @Delete("DELETE FROM oa_file_favorite WHERE file_id = #{fileId} AND user_id = #{userId} AND tenant_id = #{tenantId}")
    void physicalDeleteByFileIdAndUserId(@Param("fileId") Long fileId, 
                                       @Param("userId") Long userId,
                                       @Param("tenantId") Long tenantId);

}

