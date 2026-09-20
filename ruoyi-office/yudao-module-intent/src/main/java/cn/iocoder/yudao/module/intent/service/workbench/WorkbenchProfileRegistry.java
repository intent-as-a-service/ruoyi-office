package cn.iocoder.yudao.module.intent.service.workbench;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 工作台装配档案注册中心。
 *
 * <p>三条规则，按顺序执行，错一条就会串数据：</p>
 * <ol>
 *   <li><b>选档案</b>：按 YAML 声明顺序取第一个与本人角色有交集的档案；都不命中用兜底档案
 *       （{@code roles} 为空的那份）。顺序即优先级，不要靠"哪个更具体"去猜；</li>
 *   <li><b>算视角</b>：视角只能在本人已有角色里<b>挑</b>一个子集，交集为空就退化为"不改变角色"。
 *       少了这一步，前端传 {@code view=manager} 就能拿到主管才有的意图——这是提权，
 *       不是个性化；</li>
 *   <li><b>可选项是"显示"不是"授权"</b>：档案里的 pinnedIntents 决定排序，不决定可见性。</li>
 * </ol>
 */
@Slf4j
public class WorkbenchProfileRegistry {

    private static final ObjectMapper YAML = new ObjectMapper(new YAMLFactory());

    private final List<WorkbenchProfile> profiles;

    private WorkbenchProfileRegistry(List<WorkbenchProfile> profiles) {
        this.profiles = List.copyOf(profiles);
    }

    /** 解析一份 YAML：顶层 {@code profiles: [...]}。 */
    public static List<WorkbenchProfile> parse(String yaml) {
        try {
            Root root = YAML.readValue(yaml, Root.class);
            return root == null || root.profiles() == null ? List.of() : root.profiles();
        } catch (Exception e) {
            throw new IllegalStateException("工作台档案解析失败: " + e.getMessage(), e);
        }
    }

    public static WorkbenchProfileRegistry of(List<WorkbenchProfile> profiles) {
        return new WorkbenchProfileRegistry(profiles);
    }

    public List<WorkbenchProfile> all() {
        return profiles;
    }

    /** 按角色命中档案（YAML 顺序即优先级），都不命中时用兜底档案。 */
    public Optional<WorkbenchProfile> forRoles(Collection<String> roles) {
        Set<String> mine = normalize(roles);
        for (WorkbenchProfile profile : profiles) {
            if (profile.roles().isEmpty()) {
                continue; // 兜底档案最后再说
            }
            for (String role : profile.roles()) {
                if (mine.contains(role)) {
                    return Optional.of(profile);
                }
            }
        }
        return profiles.stream().filter(profile -> profile.roles().isEmpty()).findFirst();
    }

    /**
     * 视角 → 生效角色：**取交集，不是取并集**。
     *
     * @param profile   命中档案（可空 = 没有档案，按不改变角色处理）
     * @param viewId    视角 id（{@code null} / {@code self} / 未知 id 都退化为"本人全部角色"）
     * @param userRoles 本人真实角色（来自宿主，永不被请求覆盖）
     * @return 生效角色；交集为空时返回本人全部角色
     */
    public Set<String> effectiveRoles(WorkbenchProfile profile, String viewId, Collection<String> userRoles) {
        Set<String> mine = normalize(userRoles);
        if (profile == null || viewId == null || viewId.isBlank() || "self".equals(viewId)) {
            return mine;
        }
        Optional<WorkbenchProfile.View> view = profile.views().stream()
                .filter(item -> viewId.equals(item.id())).findFirst();
        if (view.isEmpty() || view.get().roles().isEmpty()) {
            return mine;
        }
        Set<String> narrowed = normalize(view.get().roles());
        narrowed.retainAll(mine);
        if (narrowed.isEmpty()) {
            log.warn("[intent] 视角 {} 声明的角色 {} 与本人角色 {} 无交集，已退化为本人全部角色（视角只能收窄，不能放大）",
                    viewId, view.get().roles(), mine);
            return mine;
        }
        return narrowed;
    }

    /** 可供该用户选择的视角：本人角色能撑起来的那些（self 永远可用）。 */
    public List<WorkbenchProfile.View> availableViews(WorkbenchProfile profile, Collection<String> userRoles) {
        List<WorkbenchProfile.View> result = new ArrayList<>();
        result.add(new WorkbenchProfile.View("self", "我自己", List.of()));
        Set<String> mine = normalize(userRoles);
        if (profile == null) {
            return result;
        }
        for (WorkbenchProfile.View view : profile.views()) {
            if (view.roles().isEmpty() || "self".equals(view.id())) {
                continue;
            }
            Set<String> narrowed = normalize(view.roles());
            narrowed.retainAll(mine);
            if (!narrowed.isEmpty()) {
                result.add(view);
            }
        }
        return result;
    }

    /** 找档案里声明的某个预设。 */
    public Optional<WorkbenchProfile.Preset> preset(WorkbenchProfile profile, String presetId) {
        if (profile == null || presetId == null) {
            return Optional.empty();
        }
        return profile.presets().stream().filter(item -> presetId.equals(item.id())).findFirst();
    }

    private static Set<String> normalize(Collection<String> roles) {
        Set<String> result = new LinkedHashSet<>();
        if (roles == null) {
            return result;
        }
        for (String role : roles) {
            if (role != null && !role.isBlank()) {
                result.add(role.trim());
            }
        }
        return result;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Root(List<WorkbenchProfile> profiles) {
    }
}