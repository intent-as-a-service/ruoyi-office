package cn.iocoder.yudao.common.server.attachment.service;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.common.server.attachment.controller.vo.AttachmentSaveReqVO;
import cn.iocoder.yudao.common.server.attachment.dal.dataobject.AttachmentDO;
import cn.iocoder.yudao.common.server.attachment.dal.mysql.AttachmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 通用附件信息 Service 实现类
 *
 * @author 宇擎源码
 */
@Service
@Validated
public class AttachmentServiceImpl implements AttachmentService {

    @Resource
    private AttachmentMapper attachmentMapper;

    @Override
    public Long createAttachment(@Valid AttachmentSaveReqVO createReqVO) {
        // 插入
        AttachmentDO attachment = BeanUtils.toBean(createReqVO, AttachmentDO.class);
        if (attachment.getUploadTime() == null) {
            attachment.setUploadTime(LocalDateTime.now());
        }
        if (attachment.getSortOrder() == null) {
            attachment.setSortOrder(0);
        }
        attachmentMapper.insert(attachment);
        // 返回
        return attachment.getId();
    }

    @Override
    public void updateAttachment(@Valid AttachmentSaveReqVO updateReqVO) {
        // 校验存在
        validateAttachmentExists(updateReqVO.getId());
        // 更新
        AttachmentDO updateObj = BeanUtils.toBean(updateReqVO, AttachmentDO.class);
        attachmentMapper.updateById(updateObj);
    }

    @Override
    public void deleteAttachment(Long id) {
        // 校验存在
        validateAttachmentExists(id);
        // 删除
        attachmentMapper.deleteById(id);
    }

    private void validateAttachmentExists(Long id) {
        if (attachmentMapper.selectById(id) == null) {
            throw new RuntimeException("附件不存在");
        }
    }

    @Override
    public AttachmentDO getAttachment(Long id) {
        return attachmentMapper.selectById(id);
    }

    @Override
    public List<AttachmentDO> getAttachmentListByBusiness(String businessType, Long businessId) {
        return attachmentMapper.selectListByBusiness(businessType, businessId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttachmentList(String businessType, Long businessId, List<AttachmentSaveReqVO> attachments) {
        // 1. 获取现有附件ID集合
        Set<Long> existingIds = getAttachmentListByBusiness(businessType, businessId)
                .stream()
                .map(AttachmentDO::getId)
                .collect(Collectors.toSet());
        
        Set<Long> processedIds = new HashSet<>();
        
        // 2. 批量处理附件列表
        if (attachments != null && !attachments.isEmpty()) {
            // 转换并设置业务字段
            List<AttachmentDO> attachmentDOList = attachments.stream()
                    .map(reqVO -> {
                        AttachmentDO attachmentDO = BeanUtils.toBean(reqVO, AttachmentDO.class);
                        attachmentDO.setBusinessType(businessType);
                        attachmentDO.setBusinessId(businessId);
                        // 新增时设置上传时间
                        if (attachmentDO.getId() == null && attachmentDO.getUploadTime() == null) {
                            attachmentDO.setUploadTime(LocalDateTime.now());
                        }
                        return attachmentDO;
                    })
                    .collect(Collectors.toList());
            
            // 重新设置排序顺序
            for (int i = 0; i < attachmentDOList.size(); i++) {
                attachmentDOList.get(i).setSortOrder(i + 1);
            }
            
            // 批量 insertOrUpdate
            attachmentMapper.insertOrUpdate(attachmentDOList);
            
            // 收集所有处理过的ID
            processedIds = attachmentDOList.stream()
                    .map(AttachmentDO::getId)
                    .collect(Collectors.toSet());
        }
        
        // 3. 删除不再需要的附件
        existingIds.removeAll(processedIds);
        if (!existingIds.isEmpty()) {
            attachmentMapper.deleteBatchIds(existingIds);
        }
    }

    @Override
    public void deleteAttachmentByBusiness(String businessType, Long businessId) {
        attachmentMapper.deleteByBusiness(businessType, businessId);
    }

    @Override
    public void deleteAttachmentByBusinessIds(String businessType, List<Long> businessIds) {
        if (businessIds != null && !businessIds.isEmpty()) {
            attachmentMapper.deleteByBusinessIds(businessType, businessIds);
        }
    }

}
