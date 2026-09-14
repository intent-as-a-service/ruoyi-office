package cn.iocoder.yudao.module.oa.controller.admin.file;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.oa.controller.admin.file.vo.*;
import cn.iocoder.yudao.module.oa.dal.dataobject.file.FileInfoDO;
import cn.iocoder.yudao.module.oa.service.file.FileInfoService;
import cn.iocoder.yudao.module.oa.service.file.FileShareService;

@Tag(name = "OA协同办公 - 企业云盘")
@RestController
@RequestMapping("/oa/file")
@Validated
public class FileInfoController {

    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private FileShareService fileShareService;

    @Resource
    private FileApi fileApi;

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    @PreAuthorize("@ss.hasPermission('oa:file:create')")
    public CommonResult<Long> uploadFile(@RequestParam("file") MultipartFile file,
                                       @RequestParam(value = "parentId", defaultValue = "0") Long parentId) throws Exception {
        return success(fileInfoService.uploadFile(file, parentId));
    }

    @PostMapping("/create")
    @Operation(summary = "创建文件/文件夹")
    @PreAuthorize("@ss.hasPermission('oa:file:create')")
    public CommonResult<Long> createFileInfo(@Valid @RequestBody FileInfoSaveReqVO createReqVO) {
        return success(fileInfoService.createFileInfo(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新文件/文件夹")
    @PreAuthorize("@ss.hasPermission('oa:file:update')")
    public CommonResult<Boolean> updateFileInfo(@Valid @RequestBody FileInfoSaveReqVO updateReqVO) {
        fileInfoService.updateFileInfo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文件/文件夹")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('oa:file:delete')")
    public CommonResult<Boolean> deleteFileInfo(@RequestParam("id") Long id) {
        fileInfoService.deleteFileInfo(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除文件/文件夹")
    @PreAuthorize("@ss.hasPermission('oa:file:delete')")
    public CommonResult<Boolean> deleteFileInfoList(@RequestParam("ids") List<Long> ids) {
        fileInfoService.deleteFileInfoListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得文件/文件夹")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('oa:file:query')")
    public CommonResult<FileInfoRespVO> getFileInfo(@RequestParam("id") Long id) {
        FileInfoDO fileInfo = fileInfoService.getFileInfo(id);
        return success(BeanUtils.toBean(fileInfo, FileInfoRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得文件/文件夹分页")
    @PreAuthorize("@ss.hasPermission('oa:file:query')")
    public CommonResult<PageResult<FileInfoRespVO>> getFileInfoPage(@Valid FileInfoPageReqVO pageReqVO) {
        PageResult<FileInfoRespVO> pageResult = fileInfoService.getFileInfoPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/list")
    @Operation(summary = "获得指定文件夹下的文件列表")
    @Parameter(name = "parentId", description = "父文件夹ID", required = true, example = "0")
    @PreAuthorize("@ss.hasPermission('oa:file:query')")
    public CommonResult<List<FileInfoRespVO>> getFileInfoListByParentId(@RequestParam("parentId") Long parentId) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        List<FileInfoRespVO> list = fileInfoService.getFileInfoListByParentId(parentId, userId);
        return success(list);
    }

    @PutMapping("/move")
    @Operation(summary = "移动文件/文件夹")
    @PreAuthorize("@ss.hasPermission('oa:file:update')")
    public CommonResult<Boolean> moveFileInfo(@RequestParam("id") Long id, @RequestParam("targetParentId") Long targetParentId) {
        fileInfoService.moveFileInfo(id, targetParentId);
        return success(true);
    }

    @PutMapping("/rename")
    @Operation(summary = "重命名文件/文件夹")
    @PreAuthorize("@ss.hasPermission('oa:file:update')")
    public CommonResult<Boolean> renameFileInfo(@RequestParam("id") Long id, @RequestParam("newName") String newName) {
        fileInfoService.renameFileInfo(id, newName);
        return success(true);
    }

    @PostMapping("/favorite")
    @Operation(summary = "收藏文件")
    @PreAuthorize("@ss.hasPermission('oa:file:favorite')")
    public CommonResult<Boolean> favoriteFile(@RequestParam("fileId") Long fileId) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        fileInfoService.favoriteFile(fileId, userId);
        return success(true);
    }

    @DeleteMapping("/unfavorite")
    @Operation(summary = "取消收藏文件")
    @PreAuthorize("@ss.hasPermission('oa:file:favorite')")
    public CommonResult<Boolean> unfavoriteFile(@RequestParam("fileId") Long fileId) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        fileInfoService.unfavoriteFile(fileId, userId);
        return success(true);
    }

    @GetMapping("/favorite-list")
    @Operation(summary = "获取收藏的文件列表")
    @PreAuthorize("@ss.hasPermission('oa:file:query')")
    public CommonResult<List<FileInfoRespVO>> getFavoriteFileList() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        List<FileInfoRespVO> list = fileInfoService.getFavoriteFileList(userId);
        return success(list);
    }

    @GetMapping("/storage-stats")
    @Operation(summary = "获取用户文件存储统计信息")
    @PreAuthorize("@ss.hasPermission('oa:file:query')")
    public CommonResult<FileStorageStatsRespVO> getFileStorageStats() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        FileStorageStatsRespVO stats = fileInfoService.getFileStorageStats(userId);
        return success(stats);
    }

    // ==================== 文件分享相关接口 ====================

    @PostMapping("/share")
    @Operation(summary = "分享文件/文件夹")
    @PreAuthorize("@ss.hasPermission('oa:file:share')")
    public CommonResult<Integer> shareFile(@Valid @RequestBody FileShareReqVO shareReqVO) {
        Integer shareCount = fileShareService.shareFile(shareReqVO);
        return success(shareCount);
    }

    @DeleteMapping("/unshare")
    @Operation(summary = "取消分享")
    @PreAuthorize("@ss.hasPermission('oa:file:share')")
    public CommonResult<Boolean> unshareFile(@RequestParam("fileId") Long fileId,
                                           @RequestParam("shareType") Integer shareType,
                                           @RequestParam("targetId") Long targetId) {
        fileShareService.unshareFile(fileId, shareType, targetId);
        return success(true);
    }

    @GetMapping("/share-info")
    @Operation(summary = "获取文件分享信息")
    @Parameter(name = "fileId", description = "文件ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('oa:file:query')")
    public CommonResult<FileShareRespVO> getFileShareInfo(@RequestParam("fileId") Long fileId) {
        FileShareRespVO shareInfo = fileShareService.getFileShareInfo(fileId);
        return success(shareInfo);
    }

    @GetMapping("/shared-list")
    @Operation(summary = "获取共享文件列表(根级别)")
    @PreAuthorize("@ss.hasPermission('oa:file:query')")
    public CommonResult<List<SharedFileListRespVO>> getSharedFileList() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        List<SharedFileListRespVO> list = fileShareService.getSharedFilesForUser(userId);
        return success(list);
    }

    @GetMapping("/shared-sub-files")
    @Operation(summary = "获取共享文件夹下的子文件列表")
    @PreAuthorize("@ss.hasPermission('oa:file:query')")
    public CommonResult<List<SharedFileListRespVO>> getSharedSubFiles(
            @RequestParam("rootShareId") Long rootShareId,
            @RequestParam("parentId") Long parentId) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        List<SharedFileListRespVO> list = fileShareService.getSharedSubFiles(rootShareId, parentId, userId);
        return success(list);
    }

    @GetMapping("/check-permission")
    @Operation(summary = "检查文件权限")
    @Parameter(name = "fileId", description = "文件ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('oa:file:query')")
    public CommonResult<Integer> checkFilePermission(@RequestParam("fileId") Long fileId) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        Integer permission = fileShareService.checkFilePermission(fileId, userId);
        return success(permission);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出文件信息 Excel")
    @PreAuthorize("@ss.hasPermission('oa:file:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportFileInfoExcel(@Valid FileInfoPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        PageResult<FileInfoRespVO> pageResult = fileInfoService.getFileInfoPage(pageReqVO);
        List<FileInfoRespVO> list = pageResult.getList();
        // 导出 Excel
        ExcelUtils.write(response, "企业云盘.xls", "数据", FileInfoRespVO.class, list);
    }

}

