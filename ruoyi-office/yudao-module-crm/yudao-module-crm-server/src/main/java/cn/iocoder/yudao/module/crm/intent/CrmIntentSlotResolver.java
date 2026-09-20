package cn.iocoder.yudao.module.crm.intent;

import cn.iocoder.yudao.module.crm.dal.dataobject.business.CrmBusinessDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.clue.CrmClueDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.contact.CrmContactDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.contract.CrmContractDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.customer.CrmCustomerDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.product.CrmProductDO;
import cn.iocoder.yudao.module.crm.dal.dataobject.receivable.CrmReceivableDO;
import cn.iocoder.yudao.module.crm.service.business.CrmBusinessService;
import cn.iocoder.yudao.module.crm.service.clue.CrmClueService;
import cn.iocoder.yudao.module.crm.service.contact.CrmContactService;
import cn.iocoder.yudao.module.crm.service.contract.CrmContractService;
import cn.iocoder.yudao.module.crm.service.customer.CrmCustomerService;
import cn.iocoder.yudao.module.crm.service.product.CrmProductService;
import cn.iocoder.yudao.module.crm.service.receivable.CrmReceivableService;
import dev.intent.protocol.ResolvedSlot;
import dev.intent.protocol.SlotRef;
import dev.intent.sdk.host.IntentPrincipal;
import dev.intent.sdk.host.IntentSlotResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CRM 槽位解析器：把工作台上下文栈里的 {@code (类型, 编号)} 反查成"还在不在、我能看吗、它叫什么"。
 *
 * <p>它在工作台里同时支撑两件事（同一个问题）：</p>
 * <ol>
 *   <li><b>槽位失效检测</b>：上下文栈里摆着的客户可能已被删、或被划到别人的数据范围里；</li>
 *   <li><b>接力提议反查</b>：结果文本里出现"HT-2026-0400"时，只有这里查得到实体才允许加进上下文
 *       ——前端不许凭正则造槽位值。</li>
 * </ol>
 *
 * <p><b>三种结果的划分口径（前端据此决定标红还是沉默）</b>：</p>
 * <ul>
 *   <li>类型认识、编号是数字、实体取得到 → {@code accessible=true} + 名称；</li>
 *   <li>类型认识、编号是数字、实体取不到（已删 / 不在我的数据范围里）→ {@code accessible=false}。
 *       这两种情况在本系统里无法区分（数据权限过滤掉的与被删除的一样查不到），
 *       所以合并成"不可用"，由前端提示替换——<b>不整份拒绝</b>；</li>
 *   <li>类型不认识、编号不是数字 → <b>整条省略</b>。前端按"无法校验"处理（不标红、不提示），
 *       因为这不是"实体失效"，是"没人问过这类实体"。</li>
 * </ul>
 *
 * <p><b>鉴权</b>：这里不额外做权限判定——用的是宿主自己的 Service，
 * 数据权限（部门数据范围 / 租户）随当前线程的登录态生效。不要把 principal 当唯一依据。</p>
 */
@Slf4j
@Component
public class CrmIntentSlotResolver implements IntentSlotResolver {

    @Resource
    private CrmCustomerService customerService;
    @Resource
    private CrmContractService contractService;
    @Resource
    private CrmBusinessService businessService;
    @Resource
    private CrmContactService contactService;
    @Resource
    private CrmClueService clueService;
    @Resource
    private CrmProductService productService;
    @Resource
    private CrmReceivableService receivableService;

    @Override
    public Map<String, ResolvedSlot> resolve(IntentPrincipal principal, List<SlotRef> refs) {
        Map<String, ResolvedSlot> result = new LinkedHashMap<>();
        if (refs == null || refs.isEmpty()) {
            return result;
        }
        for (SlotRef ref : refs) {
            if (ref == null || ref.type() == null || ref.type().isBlank() || result.containsKey(ref.type())) {
                continue; // 同一类型只解析一次（上下文栈里一个类型只出现一个槽位）
            }
            Long id = parseId(ref.id());
            if (id == null) {
                continue;
            }
            try {
                ResolvedSlot resolved = lookup(ref.type().trim(), id);
                if (resolved != null) {
                    result.put(ref.type().trim(), resolved);
                }
            } catch (Exception e) {
                // 单条失败不拖垮整批：这个槽位当作"无法校验"，其余槽位照常返回
                log.warn("[intent] 槽位解析失败: type={} id={} - {}", ref.type(), ref.id(), e.getMessage());
            }
        }
        return result;
    }

    private ResolvedSlot lookup(String type, Long id) {
        return switch (type) {
            case "customer" -> {
                CrmCustomerDO entity = customerService.getCustomer(id);
                yield entity == null ? ResolvedSlot.inaccessible(type, String.valueOf(id))
                        : ResolvedSlot.visible(type, String.valueOf(id), entity.getName());
            }
            case "contract" -> {
                CrmContractDO entity = contractService.getContract(id);
                yield entity == null ? ResolvedSlot.inaccessible(type, String.valueOf(id))
                        : ResolvedSlot.visible(type, String.valueOf(id), display(entity.getName(), entity.getNo()));
            }
            case "opportunity" -> {
                CrmBusinessDO entity = businessService.getBusiness(id);
                yield entity == null ? ResolvedSlot.inaccessible(type, String.valueOf(id))
                        : ResolvedSlot.visible(type, String.valueOf(id), entity.getName());
            }
            case "contact" -> {
                CrmContactDO entity = contactService.getContact(id);
                yield entity == null ? ResolvedSlot.inaccessible(type, String.valueOf(id))
                        : ResolvedSlot.visible(type, String.valueOf(id), entity.getName());
            }
            case "clue" -> {
                CrmClueDO entity = clueService.getClue(id);
                yield entity == null ? ResolvedSlot.inaccessible(type, String.valueOf(id))
                        : ResolvedSlot.visible(type, String.valueOf(id), entity.getName());
            }
            case "product" -> {
                CrmProductDO entity = productService.getProduct(id);
                yield entity == null ? ResolvedSlot.inaccessible(type, String.valueOf(id))
                        : ResolvedSlot.visible(type, String.valueOf(id), display(entity.getName(), entity.getNo()));
            }
            case "payment" -> {
                CrmReceivableDO entity = receivableService.getReceivable(id);
                yield entity == null ? ResolvedSlot.inaccessible(type, String.valueOf(id))
                        : ResolvedSlot.visible(type, String.valueOf(id), entity.getNo());
            }
            default -> null; // 不是 CRM 的实体类型：省略，交给别的模块的解析器
        };
    }

    private static String display(String name, String fallback) {
        return name == null || name.isBlank() ? fallback : name;
    }

    private static Long parseId(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}