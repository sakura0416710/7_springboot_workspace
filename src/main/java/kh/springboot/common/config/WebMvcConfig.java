package kh.springboot.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kh.springboot.common.interceptor.LogInterceptor;
import kh.springboot.common.interceptor.TestInterceptor;
import kh.springboot.common.interceptor.adminLoginInterceptor;
import kh.springboot.common.interceptor.checkLoginInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
//외부 파일에 접근하기 위한 설정
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")				// 맵핑 URI 설정 ex.()안에 쓴 형식을 따라한 뒤, 내가 쓰고 싶은 걸 써야함.
		.addResourceLocations("file:///C:/uploadFiles/", "classPath:/static/");		//정적 리소스 위치
	}
	
	
	@Override
		public void addInterceptors(InterceptorRegistry registry) {
			//인터셉터 등록하기
		registry.addInterceptor(new TestInterceptor())
					.addPathPatterns("/**"); //인터셉터가 가로챌  url등록
		
		registry.addInterceptor(new checkLoginInterceptor())
		
		/* <로그인 유무 확인하는 인터셉터 등록하기>
		 *	/member/에서 myInfo, edit, edit, updatePassword, delete
		 * /board/ 에서 write, insert, {id}/{page}, updForm, update, delete, rinsert, rdelete, rupdate
		 * /attm/ {id}/{page}, update, ... 
		 */
					.addPathPatterns("/member/myInfo","/member/delete", "/member/updatPassword","/member/delete")
					.addPathPatterns("/board/**", "/attm/**")
					.excludePathPatterns("/board/list","/attm/list", "/board/search", "/board/top");
					
	
		
		//관리자 계정인지 확인하는 인터셉터 등록
		registry.addInterceptor(new adminLoginInterceptor())
					.addPathPatterns("/admin/**");
		
		//로그인 , 로그인 후 계정 인터셉터나누기
		registry.addInterceptor(new LogInterceptor())
				.addPathPatterns("/member/signIn");
		
	}
	
	
	
	
	
	
	
}
