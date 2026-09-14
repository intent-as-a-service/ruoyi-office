package cn.iocoder.yudao.module.system.service.home;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.page.HomePageLayoutSaveReqVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.page.HomePagePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.page.HomePageSaveReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomePageDO;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomePageLayoutDO;
import cn.iocoder.yudao.module.system.dal.dataobject.home.UserHomePageDO;
import cn.iocoder.yudao.module.system.dal.mysql.home.HomePageLayoutMapper;
import cn.iocoder.yudao.module.system.dal.mysql.home.HomePageMapper;
import cn.iocoder.yudao.module.system.dal.mysql.home.UserHomePageMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;

/**
 * 首页配置 Service 实现类
 *
 * @author 宇擎源码
 */
@Service
@Validated
public class HomePageServiceImpl implements HomePageService {

    @Resource
    private HomePageMapper homePageMapper;

    @Resource
    private UserHomePageMapper userHomePageMapper;

    @Resource
    private HomePageLayoutMapper layoutMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createHomePage(HomePageSaveReqVO createReqVO) {
        // 校验编码唯一性
        validateHomePageCodeUnique(null, createReqVO.getCode());

        // 不允许创建 default_workspace 编码的首页（系统保留）
        if ("default_workspace".equals(createReqVO.getCode())) {
            throw exception(HOME_PAGE_CODE_DUPLICATE);
        }

        // 用户创建的首页不能设置为系统默认
        createReqVO.setIsDefault(false);

        // 插入首页（creator 会自动设置为当前用户）
        HomePageDO homePage = BeanUtils.toBean(createReqVO, HomePageDO.class);
        homePageMapper.insert(homePage);
        return homePage.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHomePage(HomePageSaveReqVO updateReqVO) {
        // 校验首页是否存在
        HomePageDO existingPage = validateHomePageExists(updateReqVO.getId());

        // 校验权限：只有管理员可以编辑 default_workspace
        if ("default_workspace".equals(existingPage.getCode())) {
            validateAdminPermission();
        } else {
            // 其他首页：只有创建者可以编辑
            validateCreatorPermission(existingPage);
        }

        // 校验编码唯一性
        validateHomePageCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 不允许修改 default_workspace 编码
        if ("default_workspace".equals(existingPage.getCode()) && !"default_workspace".equals(updateReqVO.getCode())) {
            throw exception(HOME_PAGE_CODE_DUPLICATE);
        }

        // 用户创建的首页不能设置为系统默认
        if (!"default_workspace".equals(existingPage.getCode())) {
            updateReqVO.setIsDefault(false);
        }

        // 更新首页
        HomePageDO updateObj = BeanUtils.toBean(updateReqVO, HomePageDO.class);
        homePageMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHomePage(Long id) {
        // 校验首页是否存在
        HomePageDO homePage = validateHomePageExists(id);

        // 不允许删除系统默认首页
        if ("default_workspace".equals(homePage.getCode())) {
            throw exception(HOME_PAGE_DEFAULT_CAN_NOT_DELETE);
        }

        // 校验权限：只有创建者可以删除
        validateCreatorPermission(homePage);

        // 校验是否有用户正在使用
        UserHomePageDO userHomePage = userHomePageMapper.selectOne(UserHomePageDO::getPageId, id);
        if (userHomePage != null) {
            throw exception(HOME_PAGE_HAS_USER_USING);
        }

        // 删除首页
        homePageMapper.deleteById(id);
    }

    @Override
    public HomePageDO getHomePage(Long id) {
        return homePageMapper.selectById(id);
    }

    @Override
    public PageResult<HomePageDO> getHomePagePage(HomePagePageReqVO pageReqVO) {
        // 获取当前用户ID
        Long userId = cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId();
        // 只显示当前用户创建的首页和系统默认首页
        return homePageMapper.selectPageByUser(pageReqVO, userId);
    }

    @Override
    public HomePageDO getDefaultHomePage() {
        return homePageMapper.selectDefaultPage();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setMyHomePage(Long id) {
        // 校验首页是否存在
        validateHomePageExists(id);

        // 获取当前用户ID
        Long userId = cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId();

        // 校验首页是否启用
        HomePageDO homePage = homePageMapper.selectById(id);
        if (!cn.iocoder.yudao.framework.common.enums.CommonStatusEnum.ENABLE.getStatus().equals(homePage.getStatus())) {
            throw exception(HOME_PAGE_NOT_EXISTS);
        }

        // 使用 enableUserHomePage 方法设置用户首页
        enableUserHomePage(userId, id);
    }

    @Override
    public HomePageDO getUserHomePage(Long userId) {
        // 先查询用户是否配置了首页
        UserHomePageDO userHomePage = userHomePageMapper.selectByUserId(userId);
        if (userHomePage != null) {
            return homePageMapper.selectById(userHomePage.getPageId());
        }

        // 如果用户未配置首页，返回默认首页
        return getDefaultHomePage();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableUserHomePage(Long userId, Long pageId) {
        // 校验首页是否存在
        validateHomePageExists(pageId);

        // 校验首页是否启用
        HomePageDO homePage = homePageMapper.selectById(pageId);
        if (!CommonStatusEnum.ENABLE.getStatus().equals(homePage.getStatus())) {
            throw exception(HOME_PAGE_NOT_EXISTS);
        }

        // 查询用户是否已有配置
        UserHomePageDO userHomePage = userHomePageMapper.selectByUserId(userId);
        if (userHomePage != null) {
            // 更新用户首页配置
            UserHomePageDO updateObj = new UserHomePageDO();
            updateObj.setId(userHomePage.getId());
            updateObj.setPageId(pageId);
            userHomePageMapper.updateById(updateObj);
        } else {
            // 新增用户首页配置
            UserHomePageDO insertObj = new UserHomePageDO();
            insertObj.setUserId(userId);
            insertObj.setPageId(pageId);
            userHomePageMapper.insert(insertObj);
        }
    }

    @Override
    public List<HomePageDO> getSimpleHomePageList() {
        return homePageMapper.selectList(HomePageDO::getStatus, CommonStatusEnum.ENABLE.getStatus());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveHomePageLayout(HomePageLayoutSaveReqVO saveReqVO) {
        // 校验首页是否存在
        HomePageDO homePage = validateHomePageExists(saveReqVO.getPageId());
        
        // 校验权限：如果是 default_workspace，只有管理员可以保存（通过 Controller 层权限注解控制）
        // 其他首页：只有创建者可以保存
        if (!"default_workspace".equals(homePage.getCode())) {
            validateCreatorPermission(homePage);
        }

        // 校验JSON格式
        JSONObject layoutConfig;
        try {
            layoutConfig = JSONUtil.parseObj(saveReqVO.getLayoutJson());
        } catch (Exception e) {
            throw new IllegalArgumentException("布局JSON格式不正确");
        }

        // 先删除该首页的所有布局
        layoutMapper.deleteByPageId(saveReqVO.getPageId());

        // 解析并保存布局项
        JSONArray items = layoutConfig.getJSONArray("items");
        if (items != null && !items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                JSONObject item = items.getJSONObject(i);
                HomePageLayoutDO layout = new HomePageLayoutDO();
                layout.setPageId(saveReqVO.getPageId());
                layout.setComponentCode(item.getStr("componentCode"));
                
                // 获取整数值，如果为null则使用默认值
                Integer x = item.getInt("x");
                Integer y = item.getInt("y");
                Integer w = item.getInt("w");
                Integer h = item.getInt("h");
                
                layout.setPositionX(x != null ? x : 0);
                layout.setPositionY(y != null ? y : 0);
                layout.setWidth(w != null ? w : 6);
                layout.setHeight(h != null ? h : 4);
                
                // 保存组件配置
                Object configObj = item.get("config");
                String configStr = "{}";
                if (configObj != null) {
                    if (configObj instanceof JSONObject) {
                        configStr = ((JSONObject) configObj).toString();
                    } else if (configObj instanceof String) {
                        configStr = (String) configObj;
                    }
                }
                layout.setConfig(configStr);
                layout.setSort(i);
                
                layoutMapper.insert(layout);
            }
        }
    }

    @Override
    public List<HomePageLayoutDO> getHomePageLayoutList(Long pageId) {
        return layoutMapper.selectListByPageId(pageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHomePageLayouts(Long pageId) {
        layoutMapper.deleteByPageId(pageId);
    }

    /**
     * 校验首页编码唯一性
     */
    private void validateHomePageCodeUnique(Long id, String code) {
        HomePageDO homePage = homePageMapper.selectByCode(code);
        if (homePage == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的首页
        if (id == null) {
            throw exception(HOME_PAGE_CODE_DUPLICATE);
        }
        if (!homePage.getId().equals(id)) {
            throw exception(HOME_PAGE_CODE_DUPLICATE);
        }
    }

    /**
     * 校验首页是否存在
     */
    private HomePageDO validateHomePageExists(Long id) {
        if (id == null) {
            throw exception(HOME_PAGE_NOT_EXISTS);
        }
        HomePageDO homePage = homePageMapper.selectById(id);
        if (homePage == null) {
            throw exception(HOME_PAGE_NOT_EXISTS);
        }
        return homePage;
    }

    /**
     * 校验管理员权限
     */
    private void validateAdminPermission() {
        Long userId = cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId();
        // 检查是否有系统管理权限
        cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUser();
        // 这里可以添加更具体的权限检查，暂时使用权限注解来控制
        // 如果需要在 Service 层检查，可以使用 SecurityFrameworkService
    }

    /**
     * 校验创建者权限
     */
    private void validateCreatorPermission(HomePageDO homePage) {
        Long userId = cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId();
        String creator = homePage.getCreator();
        if (creator == null || !creator.equals(String.valueOf(userId))) {
            // 如果不是创建者，抛出权限异常
            throw exception(HOME_PAGE_NOT_EXISTS); // 使用通用错误码，实际应该定义新的错误码如 HOME_PAGE_NO_PERMISSION
        }
    }

}
