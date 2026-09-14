package cn.iocoder.yudao.module.system.service.home;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.app.HomeAppUserCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.app.HomeAppUserRespVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.app.HomeAppUserUpdateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.home.vo.app.HomeAppUserUpdateSortReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomeAppConfigDO;
import cn.iocoder.yudao.module.system.dal.dataobject.home.HomeAppUserDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.MenuDO;
import cn.iocoder.yudao.module.system.dal.mysql.home.HomeAppConfigMapper;
import cn.iocoder.yudao.module.system.dal.mysql.home.HomeAppUserMapper;
import cn.iocoder.yudao.module.system.service.permission.MenuService;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.HOME_APP_USER_EXISTS;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.HOME_APP_USER_NOT_EXISTS;

/**
 * 用户应用配置 Service 实现类
 *
 * @author 宇擎源码
 */
@Service
@Validated
public class HomeAppUserServiceImpl implements HomeAppUserService {

    @Resource
    private HomeAppUserMapper appUserMapper;

    @Resource
    private HomeAppConfigMapper appConfigMapper;

    @Resource
    private MenuService menuService;

    @Resource
    private PermissionService permissionService;

    @Override
    public List<HomeAppUserRespVO> getMyAppList() {
        // 获取当前用户ID
        Long userId = cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId();

        // 查询用户的应用配置列表（仅显示状态）
        List<HomeAppUserDO> appUserList = appUserMapper.selectListByUserIdAndStatus(userId, CommonStatusEnum.ENABLE.getStatus());

        // 如果用户没有配置应用，自动初始化
        if (appUserList.isEmpty()) {
            initUserApp();
            appUserList = appUserMapper.selectListByUserIdAndStatus(userId, CommonStatusEnum.ENABLE.getStatus());
        }

        // 获取用户有权限的菜单ID列表
        Set<Long> userMenuIds = getUserMenuIds(userId);

        // 过滤出用户有权限的应用
        appUserList = appUserList.stream()
                .filter(app -> userMenuIds.contains(app.getMenuId()))
                .collect(Collectors.toList());

        // 获取菜单信息 Map（包含所有菜单，用于递归查找父菜单）
        List<MenuDO> allMenuList = menuService.getMenuList();
        Map<Long, MenuDO> allMenuMap = allMenuList.stream()
                .collect(Collectors.toMap(MenuDO::getId, menu -> menu));

        // 转换为 VO
        return appUserList.stream()
                .map(app -> {
                    HomeAppUserRespVO vo = BeanUtils.toBean(app, HomeAppUserRespVO.class);
                    MenuDO menu = allMenuMap.get(app.getMenuId());
                    if (menu != null) {
                        vo.setMenuName(menu.getName());
                        // 获取完整路径（递归拼接父级路径）
                        vo.setMenuPath(getFullMenuPath(menu, allMenuMap));
                        vo.setMenuIcon(menu.getIcon());
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUserApp(HomeAppUserCreateReqVO createReqVO) {
        // 获取当前用户ID
        Long userId = cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId();

        // 校验菜单是否存在
        MenuDO menu = menuService.getMenu(createReqVO.getMenuId());
        if (menu == null) {
            throw exception(HOME_APP_USER_NOT_EXISTS);
        }

        // 校验用户是否有该菜单的权限
        Set<Long> userMenuIds = getUserMenuIds(userId);
        if (!userMenuIds.contains(createReqVO.getMenuId())) {
            throw exception(HOME_APP_USER_NOT_EXISTS);
        }

        // 校验该用户是否已添加该应用
        HomeAppUserDO existingApp = appUserMapper.selectByUserIdAndMenuId(userId, createReqVO.getMenuId());
        if (existingApp != null) {
            throw exception(HOME_APP_USER_EXISTS);
        }

        // 获取当前用户应用的最大排序值
        List<HomeAppUserDO> userApps = appUserMapper.selectListByUserId(userId);
        int maxSort = userApps.stream()
                .mapToInt(HomeAppUserDO::getSort)
                .max()
                .orElse(0);

        // 创建应用
        HomeAppUserDO appUser = BeanUtils.toBean(createReqVO, HomeAppUserDO.class);
        appUser.setUserId(userId);
        appUser.setSort(maxSort + 1);
        appUser.setStatus(CommonStatusEnum.ENABLE.getStatus());
        appUserMapper.insert(appUser);

        return appUser.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserApp(HomeAppUserUpdateReqVO updateReqVO) {
        // 校验应用是否存在
        validateUserAppExists(updateReqVO.getId());

        // 更新应用
        HomeAppUserDO updateObj = BeanUtils.toBean(updateReqVO, HomeAppUserDO.class);
        appUserMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserApp(Long id) {
        // 校验应用是否存在
        validateUserAppExists(id);

        // 删除应用
        appUserMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserAppSort(List<HomeAppUserUpdateSortReqVO> sortList) {
        // 批量更新排序
        sortList.forEach(sort -> {
            HomeAppUserDO updateObj = new HomeAppUserDO();
            updateObj.setId(sort.getId());
            updateObj.setSort(sort.getSort());
            appUserMapper.updateById(updateObj);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initUserApp() {
        // 获取当前用户ID
        Long userId = cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId();

        // 查询系统级已启用的应用配置
        List<HomeAppConfigDO> configList = appConfigMapper.selectListByStatus(CommonStatusEnum.ENABLE.getStatus());

        // 获取用户有权限的菜单ID列表
        Set<Long> userMenuIds = getUserMenuIds(userId);

        // 过滤出用户有权限的应用配置
        configList = configList.stream()
                .filter(config -> userMenuIds.contains(config.getMenuId()))
                .collect(Collectors.toList());

        // 批量插入用户应用配置
        for (int i = 0; i < configList.size(); i++) {
            HomeAppConfigDO config = configList.get(i);
            HomeAppUserDO appUser = new HomeAppUserDO();
            appUser.setUserId(userId);
            appUser.setMenuId(config.getMenuId());
            appUser.setName(config.getName());
            appUser.setIcon(config.getIcon());
            appUser.setColor(config.getColor());
            appUser.setSort(i);
            appUser.setStatus(CommonStatusEnum.ENABLE.getStatus());
            appUserMapper.insert(appUser);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetUserApp() {
        // 获取当前用户ID
        Long userId = cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId();

        // 删除用户的所有应用配置
        appUserMapper.deleteByUserId(userId);

        // 重新初始化
        initUserApp();
    }

    /**
     * 校验用户应用是否存在
     */
    private void validateUserAppExists(Long id) {
        if (id == null) {
            throw exception(HOME_APP_USER_NOT_EXISTS);
        }
        HomeAppUserDO appUser = appUserMapper.selectById(id);
        if (appUser == null) {
            throw exception(HOME_APP_USER_NOT_EXISTS);
        }

        // 校验是否是当前用户的应用
        Long userId = cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId();
        if (!appUser.getUserId().equals(userId)) {
            throw exception(HOME_APP_USER_NOT_EXISTS);
        }
    }

    /**
     * 获取用户有权限的菜单ID列表
     */
    private Set<Long> getUserMenuIds(Long userId) {
        // 获取用户的角色列表
        Set<Long> roleIds = permissionService.getUserRoleIdListByUserId(userId);

        // 获取角色拥有的菜单ID列表
        Set<Long> menuIds = permissionService.getRoleMenuListByRoleId(roleIds);

        // 获取菜单列表，过滤出类型为菜单（type=2）的
        List<MenuDO> menuList = menuService.getMenuList();
        return menuList.stream()
                .filter(menu -> menuIds.contains(menu.getId()))
                .filter(menu -> menu.getType().equals(2)) // 只选择菜单类型
                .filter(menu -> menu.getStatus().equals(CommonStatusEnum.ENABLE.getStatus())) // 只选择启用的
                .map(MenuDO::getId)
                .collect(Collectors.toSet());
    }

    /**
     * 获取菜单信息 Map
     */
    private Map<Long, MenuDO> getMenuMap(Set<Long> menuIds) {
        if (menuIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<MenuDO> menuList = menuService.getMenuList();
        return menuList.stream()
                .filter(menu -> menuIds.contains(menu.getId()))
                .collect(Collectors.toMap(MenuDO::getId, menu -> menu));
    }

    /**
     * 递归获取菜单的完整路径（拼接所有父级路径）
     * 
     * @param menu 当前菜单
     * @param allMenuMap 所有菜单的 Map（用于查找父菜单）
     * @return 完整路径，例如：/bpm/start-process
     */
    private String getFullMenuPath(MenuDO menu, Map<Long, MenuDO> allMenuMap) {
        if (menu == null || menu.getPath() == null) {
            return null;
        }

        // 如果是根节点或父节点是根节点，直接返回当前路径（确保以 / 开头）
        if (menu.getParentId() == null || menu.getParentId().equals(MenuDO.ID_ROOT)) {
            String path = menu.getPath();
            return path.startsWith("/") ? path : "/" + path;
        }

        // 递归获取父菜单路径
        MenuDO parentMenu = allMenuMap.get(menu.getParentId());
        if (parentMenu == null) {
            // 如果找不到父菜单，直接返回当前路径
            String path = menu.getPath();
            return path.startsWith("/") ? path : "/" + path;
        }

        // 拼接父路径和当前路径
        String parentPath = getFullMenuPath(parentMenu, allMenuMap);
        if (parentPath == null || parentPath.isEmpty()) {
            String path = menu.getPath();
            return path.startsWith("/") ? path : "/" + path;
        }

        // 确保路径格式正确：父路径/子路径
        String currentPath = menu.getPath();
        if (currentPath.startsWith("/")) {
            currentPath = currentPath.substring(1); // 移除开头的 /
        }
        
        // 如果父路径以 / 结尾，移除它
        if (parentPath.endsWith("/")) {
            parentPath = parentPath.substring(0, parentPath.length() - 1);
        }

        return parentPath + "/" + currentPath;
    }

}

