package cn.iocoder.yudao.module.crm.intent;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.crm.controller.admin.business.vo.business.CrmBusinessPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.clue.vo.CrmCluePageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.contact.vo.CrmContactPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.contract.vo.contract.CrmContractPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.customer.vo.customer.CrmCustomerPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.product.vo.product.CrmProductPageReqVO;
import cn.iocoder.yudao.module.crm.controller.admin.receivable.vo.receivable.CrmReceivablePageReqVO;
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
import dev.intent.sdk.host.IntentSlotCandidateProvider;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * CRM 槽位候选：给工作台「＋ 加实体」提供可挑的客户 / 合同 / 商机 / 联系人 / 线索 / 产品 / 回款单。
 *
 * <p>取数用的是宿主自己的分页 Service，所以<b>数据权限自动生效</b>
 * （部门数据范围 + 租户 + 本人场景，全部随当前登录态）；这里不做第二套过滤，
 * 否则工作台会与业务列表页给出不一致的候选集——那种不一致极难排查。</p>
 *
 * <p>候选集刻意只取一页（limit 条）：这是输入框的联想，不是列表页。
 * 关键字为空时给的是"该 Service 默认排序下的前几条"，对本人负责的数据就是最近在跟的几条。</p>
 */
@Component
public class CrmIntentSlotCandidateProvider implements IntentSlotCandidateProvider {

    private static final List<String> TYPES = List.of("customer", "contract", "opportunity",
            "contact", "clue", "product", "payment");

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
    public Collection<String> types() {
        return TYPES;
    }

    @Override
    public List<Candidate> search(String type, String keyword, int limit) {
        int size = Math.min(Math.max(limit, 1), 50);
        String key = keyword == null || keyword.isBlank() ? null : keyword.trim();
        return switch (type) {
            case "customer" -> {
                CrmCustomerPageReqVO reqVO = new CrmCustomerPageReqVO();
                reqVO.setName(key);
                reqVO.setPageSize(size);
                PageResult<CrmCustomerDO> page = customerService.getCustomerPage(reqVO, null);
                yield map(page.getList(), entity -> new Candidate(id(entity.getId()), entity.getName(), null));
            }
            case "contract" -> {
                CrmContractPageReqVO reqVO = new CrmContractPageReqVO();
                reqVO.setName(key);
                reqVO.setPageSize(size);
                PageResult<CrmContractDO> page = contractService.getContractPage(reqVO, null);
                yield map(page.getList(), entity -> new Candidate(id(entity.getId()), entity.getName(), entity.getNo()));
            }
            case "opportunity" -> {
                CrmBusinessPageReqVO reqVO = new CrmBusinessPageReqVO();
                reqVO.setName(key);
                reqVO.setPageSize(size);
                PageResult<CrmBusinessDO> page = businessService.getBusinessPage(reqVO, null);
                yield map(page.getList(), entity -> new Candidate(id(entity.getId()), entity.getName(), null));
            }
            case "contact" -> {
                CrmContactPageReqVO reqVO = new CrmContactPageReqVO();
                reqVO.setName(key);
                reqVO.setPageSize(size);
                PageResult<CrmContactDO> page = contactService.getContactPage(reqVO, null);
                yield map(page.getList(), entity -> new Candidate(id(entity.getId()), entity.getName(), entity.getMobile()));
            }
            case "clue" -> {
                CrmCluePageReqVO reqVO = new CrmCluePageReqVO();
                reqVO.setName(key);
                reqVO.setPageSize(size);
                PageResult<CrmClueDO> page = clueService.getCluePage(reqVO, null);
                yield map(page.getList(), entity -> new Candidate(id(entity.getId()), entity.getName(), entity.getMobile()));
            }
            case "product" -> {
                CrmProductPageReqVO reqVO = new CrmProductPageReqVO();
                reqVO.setName(key);
                reqVO.setPageSize(size);
                PageResult<CrmProductDO> page = productService.getProductPage(reqVO);
                yield map(page.getList(), entity -> new Candidate(id(entity.getId()), entity.getName(), entity.getNo()));
            }
            case "payment" -> {
                CrmReceivablePageReqVO reqVO = new CrmReceivablePageReqVO();
                reqVO.setNo(key);
                reqVO.setPageSize(size);
                PageResult<CrmReceivableDO> page = receivableService.getReceivablePage(reqVO, null);
                yield map(page.getList(), entity -> new Candidate(id(entity.getId()), entity.getNo(), null));
            }
            default -> List.of();
        };
    }

    private static <T> List<Candidate> map(List<T> rows, java.util.function.Function<T, Candidate> mapper) {
        if (rows == null || rows.isEmpty()) {
            return List.of();
        }
        List<Candidate> result = new ArrayList<>(rows.size());
        for (T row : rows) {
            if (row != null) {
                result.add(mapper.apply(row));
            }
        }
        return result;
    }

    private static String id(Long value) {
        return value == null ? null : String.valueOf(value);
    }
}
