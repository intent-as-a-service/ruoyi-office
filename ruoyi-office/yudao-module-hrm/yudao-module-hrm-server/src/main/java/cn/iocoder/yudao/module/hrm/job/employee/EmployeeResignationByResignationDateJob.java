package cn.iocoder.yudao.module.hrm.job.employee;

import cn.iocoder.yudao.framework.tenant.core.job.TenantJob;
import cn.iocoder.yudao.module.hrm.dal.dataobject.employee.EmployeeResignationBillDO;
import cn.iocoder.yudao.module.hrm.dal.mysql.employee.EmployeeResignationBillMapper;
import cn.iocoder.yudao.module.hrm.service.employee.EmployeeResignationBillService;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.yudao.module.bpm.enums.task.BpmTaskStatusEnum.APPROVE;

/**
 * 员工离职申请单定时任务：处理已审批通过且到达离职日期的离职申请
 * 每天 01:00 执行一次
 *
 * @author 宇擎源码
 */
@Component
@Slf4j
public class EmployeeResignationByResignationDateJob {

    @Resource
    private EmployeeResignationBillMapper employeeResignationBillMapper;

    @Resource
    private EmployeeResignationBillService employeeResignationBillService;

    @XxlJob("employeeResignationByResignationDateJob")
    @TenantJob // 多租户
    public String execute() {
        LocalDate today = LocalDate.now();
        List<EmployeeResignationBillDO> bills = employeeResignationBillMapper.selectList(
                new LambdaQueryWrapperX<EmployeeResignationBillDO>()
                        .eq(EmployeeResignationBillDO::getProcessStatus, APPROVE.getStatus())
                        .eq(EmployeeResignationBillDO::getResignationDate, today)
        );
        
        if (bills.isEmpty()) {
            log.info("[execute][今天没有需要处理的员工离职申请单]");
            return "今天没有需要处理的员工离职申请单";
        }

        log.info("[execute][开始处理员工离职申请单，数量：{}]", bills.size());
        int successCount = 0;
        int failCount = 0;
        
        for (EmployeeResignationBillDO bill : bills) {
            try {
                // 调用 Service 方法更新员工档案
                employeeResignationBillService.updateEmployeeFromResignationBill(bill.getId());
                successCount++;
                log.info("[execute][处理员工离职申请单成功，billId={}]", bill.getId());
            } catch (Exception ex) {
                failCount++;
                log.error("[execute][处理员工离职申请单失败，billId={}]", bill.getId(), ex);
            }
        }
        
        String result = String.format("处理完成：成功 %d 个，失败 %d 个", successCount, failCount);
        log.info("[execute][{}]", result);
        return result;
    }

}

