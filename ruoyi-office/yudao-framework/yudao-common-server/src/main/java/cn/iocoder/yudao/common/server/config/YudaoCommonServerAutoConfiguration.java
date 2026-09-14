package cn.iocoder.yudao.common.server.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 通用服务模块的自动配置类
 *
 * @author 宇擎源码
 */
@AutoConfiguration
@ComponentScan({
    "cn.iocoder.yudao.common.server.attachment"
})
@MapperScan({
    "cn.iocoder.yudao.common.server.attachment.dal.mysql"
})
public class YudaoCommonServerAutoConfiguration {

}
