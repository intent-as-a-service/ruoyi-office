package cn.iocoder.yudao.module.oa.dal.dataobject.car;

import lombok.*;

import java.time.LocalDate;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 车辆信息 DO
 *
 * @author 宇擎源码
 */
@TableName("oa_car")
@KeySequence("oa_car_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    
    /**
     * 公司ID
     */
    private Long companyId;
    
    /**
     * 公司名称
     */
    private String companyName;
    
    /**
     * 车牌号
     */
    private String carNo;
    /**
     * 车辆名称
     */
    private String carName;
    /**
     * 车型
     *
     * 枚举 {@link TODO oa_car_type 对应的类}
     */
    private Long carType;
    /**
     * 分类
     *
     * 枚举 {@link TODO oa_car_cls 对应的类}
     */
    private Long carCls;
    /**
     * 品牌型号
     */
    private String brand;
    /**
     * 车座
     */
    private String seatNum;
    /**
     * 裸车价
     */
    private BigDecimal barePrice;
    /**
     * 交强险到期日期
     */
    private LocalDate forceInsuranceDate;
    /**
     * 商业险到期日期
     */
    private LocalDate businessInsuranceDate;
    /**
     * 年检日期
     */
    private LocalDate yearCheckDate;
    /**
     * 上传照片
     */
    private String picUrl;
    /**
     * 显示顺序
     */
    private Integer sort;
    /**
     * 状态（0正常 1停用）
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;


}