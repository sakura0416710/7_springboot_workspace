package kh.springboot.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
//외부 파일에 접근하기 위한 설정
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")				// 맵핑 URI 설정 ex.()안에 쓴 형식을 따라한 뒤, 내가 쓰고 싶은 걸 써야함.
		.addResourceLocations("file:///C:/uploadFiles/", "classPath:/static/");		//정적 리소스 위치
	}
}
