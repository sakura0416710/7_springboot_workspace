package kh.springboot.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class TemplateResolverConfig {

	@Bean //어노테이션 추가해야 부트가 찾아서 실행해줌(IoC)
	public ClassLoaderTemplateResolver memberResolver() {
		ClassLoaderTemplateResolver mResolver = new ClassLoaderTemplateResolver();
		//application properties안에 있는 설정 (prefix)를 자바 코드로 바꾸기
		//member까지 포함된 viewResolver를 하나더 만든 것.
		//어플리케이션 properties에 잇는 VR에선 template/까지 잇는 거 (기본)도 존재 !
		//기본설정으로 먼저 찾고 없으면 이 경로로 찾음. 필요할 때마다 쓰려고.
		mResolver.setPrefix("templates/views/member/"); //여기선 /추가해야 controller에서 앞에 /안붙일 수있음.
		mResolver.setSuffix(".html");
		mResolver.setTemplateMode(TemplateMode.HTML);
		mResolver.setCharacterEncoding("UTF-8");
		mResolver.setCacheable(false); //서버 껐다 켜야 새로고침 된느거 꺼두기
		mResolver.setCheckExistence(true); //url맵핑을 연쇄적으로 자동적으로 있는거 다 찾ㄱ 해줌. false로 하면 최 상단에 있는거 하나 찾았으니까 그거만 실행하고 멈춤
		return mResolver;
	}

	@Bean 
	public ClassLoaderTemplateResolver boardResolver() {
		ClassLoaderTemplateResolver bResolver = new ClassLoaderTemplateResolver();
		
		bResolver.setPrefix("templates/views/board/"); 
		bResolver.setSuffix(".html");
		bResolver.setTemplateMode(TemplateMode.HTML);
		bResolver.setCharacterEncoding("UTF-8");
		bResolver.setCacheable(false); 
		bResolver.setCheckExistence(true); 
		return bResolver;
	}
	@Bean 
	public ClassLoaderTemplateResolver adminResolver() {
		ClassLoaderTemplateResolver adminResolver = new ClassLoaderTemplateResolver();
		
		adminResolver.setPrefix("templates/views/admin/"); 
		adminResolver.setSuffix(".html");
		adminResolver.setTemplateMode(TemplateMode.HTML);
		adminResolver.setCharacterEncoding("UTF-8");
		adminResolver.setCacheable(false); 
		adminResolver.setCheckExistence(true); 
		return adminResolver;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
