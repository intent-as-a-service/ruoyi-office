package cn.iocoder.yudao.module.oa.dal.dataobject.car;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 还车申请单 DO
 *
 * @author 宇擎源码
 */
@TableName("oa_car_return_bill")
@KeySequence("oa_car_return_bill_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarReturnBillDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 单据编号
     */
    private String billCode;
    /**
     * 流程实例编号
     */
    private String processInstanceId;
    /**
     * 单据状态
     */
    private Integer processStatus;
    /**
     * 用车申请单
     */
    private String applyBill;
    /**
     * 车辆
     */
    private Long carId;
    /**
     * 车牌号
     */
    private String carNo;
    /**
     * 出车时间
     */
    private LocalDateTime goTime;
    /**
     * 回车时间
     */
    private LocalDateTime returnTime;
    /**
     * 出车地点
     */
    private String goArea;
    /**
     * 回车地点
     */
    private String returnArea;
    /**
     * 用车事由
     */
    private String cause;
    /**
     * 申请人
     */
    private String applyer;
    /**
     * 随行人
     */
    private String passenger;
    /**
     * 还车说明
     */
    private String remark;
    /**
     * 创建者姓名
     */
    private String creatorName;
    /**
     * 父级ID
     */
    private Long parentId;
    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 部门名称
     */
    private String deptName;
    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 公司名称
     */
    private String companyName;


}
