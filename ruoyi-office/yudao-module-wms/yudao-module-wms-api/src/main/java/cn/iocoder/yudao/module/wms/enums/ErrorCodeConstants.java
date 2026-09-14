package cn.iocoder.yudao.module.wms.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * oa 错误码枚举类
 *
 * oa 系统，使用 1-101-000-000 段
 */
public interface ErrorCodeConstants {



    // ========== 仓库信息 ==========
    ErrorCode WAREHOUSING_NOT_EXISTS = new ErrorCode(2_101_001_000, "仓库信息不存在");
    ErrorCode WAREHOUSING_EXITS_CHILDREN = new ErrorCode(2_101_002_000, "存在存在子仓库信息，无法删除");
    ErrorCode WAREHOUSING_PARENT_NOT_EXITS = new ErrorCode(2_101_003_000,"父级仓库信息不存在");
    ErrorCode WAREHOUSING_PARENT_ERROR = new ErrorCode(2_101_004_000, "不能设置自己为父仓库信息");
    ErrorCode WAREHOUSING_WAREHOUSING_NAME_DUPLICATE = new ErrorCode(2_101_005_000, "仓库名称已经存在");
    ErrorCode WAREHOUSING_WAREHOUSING_CODE_DUPLICATE = new ErrorCode(2_101_006_000, "仓库编码已经存在");
    ErrorCode WAREHOUSING_PARENT_IS_CHILD = new ErrorCode(2_101_007_000, "不能设置自己的子类为父类");


    // ========== 采购订单  ==========
    ErrorCode PURCHASE_ORDER_NOT_EXISTS = new ErrorCode(2_102_001_000, "采购订单不存在");


    // ========== 采购订单明细  ==========
    ErrorCode PURCHASE_ORDER_DETAIL_NOT_EXISTS = new ErrorCode(2_103_001_000, "采购订单明细不存在");


    // ========== 采购入库  ==========
    ErrorCode PURCHASE_IN_WAREHOUSING_NOT_EXISTS = new ErrorCode(2_104_001_000, "采购入库不存在");
    // ========== 采购入库、领用、退库、归还、借用、调拨明细 ==========
    ErrorCode GOODS_WAREHOUSING_DETAIL_NOT_EXISTS = new ErrorCode(2_105_001_000, "采购入库、领用、退库、归还、借用、调拨明细不存在");

    // ========== 领用、退库、归还、借用、调拨主 =========
    ErrorCode GOODS_COMMON_OPERATION_ORDER_NOT_EXISTS = new ErrorCode(2_106_001_000, "领用、退库、归还、借用、调拨主不存在");

}
