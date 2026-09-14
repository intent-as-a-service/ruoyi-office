package cn.iocoder.yudao.module.intent.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 意图 UI 静态资源：/intent-ui/** → classpath:/intent-ui/
 *
 * <p>与宿主系统同源部署，登录态与租户隔离直接复用宿主会话，
 * 无需为演示页面单独处理跨域与鉴权。</p>
 */
@Configuration(proxyBeanMethods = false)
public class IntentUiConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/intent-ui/**")
                .addResourceLocations("classpath:/intent-ui/");
    }
}
