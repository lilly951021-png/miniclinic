package tw.edu.fju.miniclinic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import tw.edu.fju.miniclinic.interceptor.LoginRequiredInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginRequiredInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
            .addPathPatterns(
                "/dashboard",
                "/dashboard/**",
                "/password",        // 保護修改密碼頁面
                "/password/**",     // 保護處理修改密碼的 POST 路由
                "/api/auth/me",
                "/api/appointments/*/status"
            )
            .excludePathPatterns(
                "/login",
                "/logout"
            );
    }
}