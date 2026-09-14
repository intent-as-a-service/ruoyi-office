package cn.iocoder.yudao.module.oa;

import cn.iocoder.yudao.framework.common.util.bill.BillCodeUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * 项目的启动类
 *
 * @author 宇擎源码
 */
@SpringBootApplication
public class OaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OaServerApplication.class, args);
    }

    /**
     * 注册单据编号生成工具Bean
     */
    @Bean
    public BillCodeUtils billCodeUtils() {
        return new BillCodeUtils();
    }
}
