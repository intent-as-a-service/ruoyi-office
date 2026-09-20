package cn.iocoder.yudao.module.intent.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
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

    /**
     * 目录入口：{@code /intent-ui/} 与 {@code /intent-ui} 都送到 index.html。
     *
     * <p>不做这一步，{@code /intent-ui/} 会被当成"目录资源"解析失败，
     * 然后落到全局异常处理器上返回一段 404 JSON——用户看到的是 JSON，不是页面。
     * 工作台现在是一等入口，不能要求每个人都记得带 index.html。</p>
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/intent-ui", "/intent-ui/index.html");
        registry.addRedirectViewController("/intent-ui/", "/intent-ui/index.html");
    }
}
