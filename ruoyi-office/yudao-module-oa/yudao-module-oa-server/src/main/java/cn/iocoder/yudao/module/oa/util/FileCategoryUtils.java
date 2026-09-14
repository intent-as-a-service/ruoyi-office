package cn.iocoder.yudao.module.oa.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.biz.system.dict.dto.DictDataRespDTO;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.system.api.dict.DictDataApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件分类工具类
 *
 * @author 宇擎源码
 */
@Slf4j
@Component
public class FileCategoryUtils {

    @Resource
    private DictDataApi dictDataApi;

    /**
     * 文件分类与后缀映射缓存
     */
    private Map<String, String> suffixToCategoryMap = null;

    /**
     * 根据文件后缀获取文件分类
     *
     * @param fileSuffix 文件后缀（不含点号，如：pdf、jpg）
     * @return 文件分类（image、document、video、audio、archive、other）
     */
    public String getFileCategoryBySuffix(String fileSuffix) {
        if (StrUtil.isBlank(fileSuffix)) {
            return "other";
        }

        // 加载映射关系（懒加载）
        if (suffixToCategoryMap == null) {
            loadSuffixCategoryMapping();
        }

        // 转换为小写查找
        String suffix = fileSuffix.toLowerCase().trim();
        String category = suffixToCategoryMap.get(suffix);
        
        return category != null ? category : "other";
    }

    /**
     * 从字典加载文件分类与后缀的映射关系
     */
    private synchronized void loadSuffixCategoryMapping() {
        if (suffixToCategoryMap != null) {
            return; // 已经加载过了
        }

        suffixToCategoryMap = new HashMap<>();
        
        try {
            // 从字典获取文件分类后缀映射
            CommonResult<List<DictDataRespDTO>> result = dictDataApi.getDictDataList("oa_file_category_suffix");
            List<DictDataRespDTO> dictDataList = result != null && result.isSuccess() ? result.getData() : null;
            
            if (CollUtil.isEmpty(dictDataList)) {
                log.warn("文件分类后缀映射字典数据为空，使用默认映射");
                initDefaultMapping();
                return;
            }

            // 解析字典数据：label存储后缀列表（逗号分隔），value存储分类值
            for (DictDataRespDTO dictData : dictDataList) {
                String category = dictData.getValue();
                String suffixes = dictData.getLabel();
                
                if (StrUtil.isBlank(category) || StrUtil.isBlank(suffixes)) {
                    continue;
                }

                // 分割后缀列表
                String[] suffixArray = suffixes.split(",");
                for (String suffix : suffixArray) {
                    suffix = suffix.trim().toLowerCase();
                    if (StrUtil.isNotBlank(suffix)) {
                        suffixToCategoryMap.put(suffix, category);
                    }
                }
            }

            log.info("文件分类后缀映射加载完成，共{}条映射关系", suffixToCategoryMap.size());
        } catch (Exception e) {
            log.error("加载文件分类后缀映射失败，使用默认映射", e);
            initDefaultMapping();
        }
    }

    /**
     * 初始化默认映射关系（当字典数据不可用时使用）
     */
    private void initDefaultMapping() {
        // 图片
        String[] imageSuffixes = {"bmp", "gif", "jpeg", "jpg", "png", "svg", "webp", "ico", "heic", "heif", "raw", "psd", "ai", "eps"};
        for (String suffix : imageSuffixes) {
            suffixToCategoryMap.put(suffix, "image");
        }

        // 文档
        String[] documentSuffixes = {"doc", "docx", "pdf", "txt", "rtf", "odt", "ppt", "pptx", "xls", "xlsx", "csv", "md", "html", "htm", "xml", "json", "yaml", "yml", "log"};
        for (String suffix : documentSuffixes) {
            suffixToCategoryMap.put(suffix, "document");
        }

        // 视频
        String[] videoSuffixes = {"mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "m4v", "3gp", "rm", "rmvb", "mpg", "mpeg", "ts", "m2ts"};
        for (String suffix : videoSuffixes) {
            suffixToCategoryMap.put(suffix, "video");
        }

        // 音频
        String[] audioSuffixes = {"mp3", "wav", "flac", "aac", "ogg", "wma", "m4a", "ape", "amr", "mid", "midi"};
        for (String suffix : audioSuffixes) {
            suffixToCategoryMap.put(suffix, "audio");
        }

        // 压缩包
        String[] archiveSuffixes = {"zip", "rar", "7z", "tar", "gz", "bz2", "xz", "iso", "dmg", "cab", "arj", "lzh"};
        for (String suffix : archiveSuffixes) {
            suffixToCategoryMap.put(suffix, "archive");
        }
    }

    /**
     * 清除缓存（当字典数据更新时调用）
     */
    public void clearCache() {
        suffixToCategoryMap = null;
    }
}

