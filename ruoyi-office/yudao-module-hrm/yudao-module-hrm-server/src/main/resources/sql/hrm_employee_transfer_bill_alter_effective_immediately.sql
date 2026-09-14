ALTER TABLE `hrm_employee_transfer_bill`
    ADD COLUMN `effective_immediately` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否立即生效（1是 0否）'
        AFTER `new_dept_name`;






