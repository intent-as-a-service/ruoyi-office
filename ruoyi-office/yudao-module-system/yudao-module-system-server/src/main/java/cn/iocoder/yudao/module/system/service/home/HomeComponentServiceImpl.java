package cn.iocoder.yudao.module.system.service.home;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.component.HomeComponentPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.component.HomeComponentSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomeComponentCategoryDO;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomeComponentDO;
import cn.iocoder.yudao.module.system.dal.mysql.home.HomeComponentCategoryMapper;
import cn.iocoder.yudao.module.system.dal.mysql.home.HomeComponentMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;

/**
 * 首页组件 Service 实现类
 *
 * @author 宇擎源码
 */
@Service
@Validated
public class HomeComponentServiceImpl implements HomeComponentService {

    @Resource
    private HomeComponentMapper componentMapper;

    @Resource
    private HomeComponentCategoryMapper homeComponentCategoryMapper;

    @Override
    public Long createComponent(HomeComponentSaveReqVO createReqVO) {
        // 校验组件编码唯一性
        validateComponentCodeUnique(null, createReqVO.getCode());

        // 校验分类是否存在
        validateCategoryExists(createReqVO.getCategoryId());

        // 校验配置Schema格式
        validateConfigSchema(createReqVO.getConfigSchema());

        // 插入组件
        HomeComponentDO component = BeanUtils.toBean(createReqVO, HomeComponentDO.class);
        componentMapper.insert(component);
        return component.getId();
    }

    @Override
    public void updateComponent(HomeComponentSaveReqVO updateReqVO) {
        // 校验组件是否存在
        validateComponentExists(updateReqVO.getId());

        // 校验组件编码唯一性
        validateComponentCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 校验分类是否存在
        validateCategoryExists(updateReqVO.getCategoryId());

        // 校验配置Schema格式
        validateConfigSchema(updateReqVO.getConfigSchema());

        // 更新组件
        HomeComponentDO updateObj = BeanUtils.toBean(updateReqVO, HomeComponentDO.class);
        componentMapper.updateById(updateObj);
    }

    @Override
    public void deleteComponent(Long id) {
        // 校验组件是否存在
        validateComponentExists(id);

        // TODO: Phase 3 需要校验组件是否被首页使用（查询 system_home_page_layout）
        // 暂时不实现此校验，等 Phase 3 实现首页设计器时再添加

        // 删除组件
        componentMapper.deleteById(id);
    }

    @Override
    public HomeComponentDO getComponent(Long id) {
        return componentMapper.selectById(id);
    }

    @Override
    public PageResult<HomeComponentDO> getComponentPage(HomeComponentPageReqVO pageReqVO) {
        return componentMapper.selectPage(pageReqVO);
    }

    @Override
    public List<HomeComponentDO> getAvailableComponentList() {
        return componentMapper.selectList(HomeComponentDO::getStatus, CommonStatusEnum.ENABLE.getStatus());
    }

    @Override
    public Map<Long, List<HomeComponentDO>> getComponentsByCategory() {
        List<HomeComponentDO> components = componentMapper.selectList(
                HomeComponentDO::getStatus, CommonStatusEnum.ENABLE.getStatus());
        return components.stream()
                .collect(Collectors.groupingBy(HomeComponentDO::getCategoryId));
    }

    @Override
    public List<HomeComponentCategoryDO> getCategoryList() {
        return homeComponentCategoryMapper.selectAllOrdered();
    }

    @Override
    public HomeComponentCategoryDO getCategory(Long id) {
        return homeComponentCategoryMapper.selectById(id);
    }

    /**
     * 校验组件编码唯一性
     */
    private void validateComponentCodeUnique(Long id, String code) {
        HomeComponentDO component = componentMapper.selectByCode(code);
        if (component == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的组件
        if (id == null) {
            throw exception(HOME_COMPONENT_CODE_DUPLICATE);
        }
        if (!component.getId().equals(id)) {
            throw exception(HOME_COMPONENT_CODE_DUPLICATE);
        }
    }

    /**
     * 校验组件是否存在
     */
    private void validateComponentExists(Long id) {
        if (id == null) {
            return;
        }
        if (componentMapper.selectById(id) == null) {
            throw exception(HOME_COMPONENT_NOT_EXISTS);
        }
    }

    /**
     * 校验分类是否存在
     */
    private void validateCategoryExists(Long categoryId) {
        if (categoryId == null) {
            return;
        }
        if (homeComponentCategoryMapper.selectById(categoryId) == null) {
            throw exception(HOME_COMPONENT_CATEGORY_NOT_EXISTS);
        }
    }

    /**
     * 校验配置Schema格式
     */
    private void validateConfigSchema(String configSchema) {
        if (configSchema == null || configSchema.isEmpty()) {
            return;
        }
        try {
            // 尝试解析JSON，验证格式是否正确
            JSONUtil.parseObj(configSchema);
        } catch (Exception e) {
            throw exception(HOME_COMPONENT_CONFIG_SCHEMA_INVALID);
        }
    }

}
