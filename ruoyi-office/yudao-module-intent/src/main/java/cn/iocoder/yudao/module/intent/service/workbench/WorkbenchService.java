package cn.iocoder.yudao.module.intent.service.workbench;

import cn.iocoder.yudao.framework.common.biz.system.permission.PermissionCommonApi;
import cn.iocoder.yudao.module.intent.framework.config.IntentProperties;
import cn.iocoder.yudao.module.intent.service.slot.IntentSlotRegistry;
import cn.iocoder.yudao.module.intent.service.slot.IntentSlotType;
import dev.intent.protocol.GatewayStatus;
import dev.intent.sdk.host.IntentSlotCandidateProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工作台装配服务：把"当前是谁"翻译成"工作台该长什么样"。
 *
 * <p>它只做三件事，边界很清楚：</p>
 * <ol>
 *   <li><b>取角色</b>：从宿主权限体系拿本人真实角色（带 60 秒缓存）。
 *       这是"不同角色看到不同内容"的<b>唯一</b>输入，永远不从请求里取；</li>
 *   <li><b>算作用域</b>：角色 → 命中档案 → 视角收窄后的生效角色。
 *       收窄只能取交集（见 {@link WorkbenchProfileRegistry#effectiveRoles}）；</li>
 *   <li><b>描述自己</b>：把档案、槽位登记表、候选提供者能力汇成 {@code /intent/me} 的响应，
 *       让前端一次请求就知道该渲染哪几块、能摆哪些槽位、有没有候选下拉。</li>
 * </ol>
 *
 * <p><b>它不决定意图的可见性</b>：那仍然是 {@code IntentService} 用 intent_config（上架 + 角色）
 * 与宿主权限判定的事。这里给出的 {@code effectiveRoles} 只是"以什么身份看"，
 * 作用是把可见集合<b>收窄</b>到当前视角。</p>
 */
@Slf4j
@Service
public class WorkbenchService {

    /** 角色缓存秒数：角色变更不频繁，但目录与 /me 每次请求都要用，别每请求打一次权限库。 */
    private static final long ROLE_CACHE_MILLIS = 60_000L;

    /** 候选最多翻到第几页：兜底实现是"多取再切"，不设上限等于让前端把整表拉下来。 */
    private static final int MAX_CANDIDATE_PAGE = 5;

    @Resource
    private PermissionCommonApi permissionCommonApi;
    @Resource
    private WorkbenchProfileRegistry profileRegistry;
    @Resource
    private IntentSlotRegistry slotRegistry;
    @Resource
    private IntentSlotCandidateProvider.Registry candidateRegistry;
    @Resource
    private IntentProperties properties;

    private final Map<Long, CachedRoles> roleCache = new ConcurrentHashMap<>();

    /** 当前用户在某个视角下的作用域。 */
    public record UserScope(Set<String> userRoles, WorkbenchProfile profile,
            Set<String> effectiveRoles, boolean narrowed) {
    }

    /** 本人真实角色（宿主权限体系为准）。 */
    public Set<String> rolesOf(Long userId) {
        if (userId == null) {
            return Set.of();
        }
        CachedRoles cached = roleCache.get(userId);
        long now = System.currentTimeMillis();
        if (cached != null && now - cached.at() < ROLE_CACHE_MILLIS) {
            return cached.roles();
        }
        Set<String> roles = fetchRoles(userId);
        roleCache.put(userId, new CachedRoles(roles, now));
        return roles;
    }

    private Set<String> fetchRoles(Long userId) {
        try {
            Set<String> codes = permissionCommonApi.getUserRoleCodeList(userId).getCheckedData();
            return codes == null ? Set.of() : new LinkedHashSet<>(codes);
        } catch (Exception e) {
            // 取不到角色时按"没有角色"处理：目录少而不是多，属于安全方向的降级
            log.warn("[intent] 读取用户 {} 角色失败，按无角色处理: {}", userId, e.getMessage());
            return Set.of();
        }
    }

    /** 角色 + 视角 → 生效作用域。 */
    public UserScope scope(Long userId, String view) {
        Set<String> userRoles = rolesOf(userId);
        WorkbenchProfile profile = profileRegistry.forRoles(userRoles).orElse(null);
        Set<String> effectiveRoles = profileRegistry.effectiveRoles(profile, view, userRoles);
        boolean narrowed = !effectiveRoles.equals(userRoles);
        return new UserScope(userRoles, profile, effectiveRoles, narrowed);
    }

    /** {@code /intent/me} 的响应体：前端渲染工作台骨架所需的全部信息。 */
    public Map<String, Object> describe(Long userId, String nickname) {
        Map<String, Object> payload = new LinkedHashMap<>();
        Set<String> userRoles = rolesOf(userId);
        WorkbenchProfile profile = profileRegistry.forRoles(userRoles).orElse(null);
        payload.put("userId", userId);
        payload.put("name", nickname);
        payload.put("roles", List.copyOf(userRoles));
        payload.put("systemName", properties.getSystemName());
        payload.put("gatewayStatus", (properties.getGateway().isEnabled()
                ? GatewayStatus.ENABLED : GatewayStatus.UNAVAILABLE).name());

        Map<String, Object> profileView = new LinkedHashMap<>();
        profileView.put("id", profile == null ? "default" : profile.id());
        profileView.put("label", profile == null ? "通用" : profile.label());
        profileView.put("title", profile == null ? "意图工作台" : profile.title());
        profileView.put("blocks", profile == null ? defaultBlocks() : profile.blocks());
        profileView.put("pinnedIntents", profile == null ? List.of() : profile.pinnedIntents());
        profileView.put("initialSlots", profile == null ? List.of() : profile.initialSlots());
        profileView.put("presets", profile == null ? List.of() : profile.presets());
        profileView.put("explore", profile == null ? null : profile.explore());
        profileView.put("emptyHint", profile == null ? null : profile.emptyHint());
        payload.put("profile", profileView);

        List<Map<String, Object>> views = new ArrayList<>();
        for (WorkbenchProfile.View view : profileRegistry.availableViews(profile, userRoles)) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", view.id());
            item.put("label", view.label());
            item.put("roles", view.roles());
            views.add(item);
        }
        payload.put("views", views);
        payload.put("defaultView", profile == null || profile.defaultView() == null
                ? "self" : profile.defaultView());

        List<Map<String, Object>> slotTypes = new ArrayList<>();
        for (IntentSlotType type : slotRegistry.all()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("type", type.type());
            item.put("key", type.key());
            item.put("title", type.title());
            item.put("multi", type.multi());
            item.put("entity", type.entity());
            item.put("values", type.values());
            item.put("hint", type.hint());
            // picker：前端按它决定"打开哪一种选择器"（remote / literal / custom / manual）。
            // 换宿主时只改 YAML 就能换掉选择方式，工作台前端零改动。
            item.put("picker", type.picker());
            item.put("pickerHint", type.pickerHint());
            // hasCandidates 保留：老前端只认这个字段，去掉会让它在升级窗口里静默退化
            item.put("hasCandidates", candidateRegistry.supports(type.type()));
            slotTypes.add(item);
        }
        payload.put("slotTypes", slotTypes);
        // 挂不上实体槽位的意图归到哪一组：组名从登记表来，前端不硬编码（见 IntentSlotRegistry#parseGroupFallbackTitle）
        payload.put("groupFallbackTitle", slotRegistry.groupFallbackTitle());
        return payload;
    }

    /** 候选实体检索（工作台「＋ 加实体」输入框）。 */
    public List<IntentSlotCandidateProvider.Candidate> candidates(String type, String keyword, Integer limit) {
        int size = limit == null ? 10 : Math.min(Math.max(limit, 1), 50);
        return candidateRegistry.search(type, keyword, size);
    }

    /**
     * 候选实体检索（分页形状，供工作台"加载更多"）。
     *
     * <p>{@code cursor} 是**不透明游标**（就是一个从 1 开始的页码字符串）。这里刻意不用
     * "offset/总页数"那套：契约里游标对前端不可解释，将来换成真正的 keyset 分页
     * （{@code where id < ?}）时前端一行都不用改。</p>
     *
     * <p><b>当前实现是"多取一页再切"</b>：候选 SPI（{@code IntentSlotCandidateProvider}）
     * 只有 {@code search(type, keyword, limit)}，没有游标参数。要在 SDK 上加
     * {@code searchPage} 默认方法才能真正下推分页——在那之前，这里用"取 page*limit 条
     * 再切最后一段"保证**行为正确**（只是多取了一些），并在页数与条数上设了硬上限。
     * 数据量很大的宿主应当优先实现真正的下推分页，而不是靠这里兜着。</p>
     */
    public Map<String, Object> candidatesPage(String type, String keyword, String cursor, Integer limit) {
        int size = limit == null ? 10 : Math.min(Math.max(limit, 1), 50);
        int pageNo = parseCursor(cursor);
        if (pageNo > MAX_CANDIDATE_PAGE) {
            // 到顶了就明确说"没有更多"，而不是继续深翻给数据库压力
            return pageView(List.of(), null, null);
        }
        int take = size * pageNo;
        List<IntentSlotCandidateProvider.Candidate> found = candidateRegistry.search(type, keyword, take);
        int from = Math.min((pageNo - 1) * size, found.size());
        int to = Math.min(from + size, found.size());
        List<IntentSlotCandidateProvider.Candidate> slice = found.subList(from, to);
        // 只有"取满了这一页"才给下一页游标：没取满说明后面没有了
        String next = slice.size() == size ? String.valueOf(pageNo + 1) : null;
        return pageView(slice, next, null);
    }

    private static Map<String, Object> pageView(List<IntentSlotCandidateProvider.Candidate> items,
            String nextCursor, Integer total) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("items", items);
        view.put("nextCursor", nextCursor);
        view.put("total", total);
        return view;
    }

    /** 游标 = 页码字符串；非法/为空都当第 1 页（不报错，用户不该因为游标格式而用不了下拉）。 */
    private static int parseCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return 1;
        }
        try {
            return Math.max(Integer.parseInt(cursor.trim()), 1);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /** 清空角色缓存（宿主给用户改了角色后调用；当前由启动期与 /me 的自然过期兜底）。 */
    public void evictRoles() {
        roleCache.clear();
    }

    private static List<String> defaultBlocks() {
        return List.of("context", "today", "todos", "frequent", "explore", "history");
    }

    private record CachedRoles(Set<String> roles, long at) {
    }
}
