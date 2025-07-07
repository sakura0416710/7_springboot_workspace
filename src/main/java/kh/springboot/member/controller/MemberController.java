package kh.springboot.member.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import kh.springboot.HomeController;
import kh.springboot.member.model.vo.Member;

@Controller
public class MemberController {

    private final HomeController homeController;

    MemberController(HomeController homeController) {
        this.homeController = homeController;
    }
	
	@GetMapping("/member/signIn")
	public String signIn() {
		return "views/member/login";
	}
	
	//파라미터 전송받는 방법
	//1. HttpServletRequest 이용하기(servlet방식)
//	@PostMapping("member/signIn")
//	public void login(HttpServletRequest request) {
//		String id = request.getParameter("id");
//		String pwd = request.getParameter("pwd");
//		System.out.println("id1 : " + id);
//		System.out.println("pwd1 : " + pwd);
//		
//	}
	
	/*2.@RequestParam 이용하기
	value : view에서 받아오는 파라미터의 이름(@RequestParam에 들어갈 속성이 value하나 뿐이라면 생략 가능)
	defaultValue : 값이 없거나 null일때 기본적으로 들어갈 데이터를 지정하는 속성
					값이 들어가 있을 때는 defaultValue에 설정된 데이터가 들어가지 않음
	required   : 지정 파라미터가 필수 파라미터인지 설정하는 속성 (기본값 true)
				   만일 해당 파라미터가 없어도 된다 혹은 없을 수도 있다면 false값을 주어 해당 자료형의 기본값을 받음.	@PostMapping("member/signIn")
	public void login(@RequestParam(value = "id", defaultValue="hello") String id,
					  @RequestParam(value = "pwd") String pwd,
					  @RequestParam(value = "test", required=false)String test){
		System.out.println("id2 : " + id);
		System.out.println("pwd2 : " + pwd);
		
	} 
	
	
	3. @RequestParam 생략 : 파라미터 명과 변수 명을 일치시켜 자동 매핑되게 함
	public void login(String id, String pwd){이렇게 가능. 근데 인텔리제이에서만 되는 기능임..sts에선 막아둠 ㅎ}
	
	
	4. @ModelAttribute 이용 : 필드명(getter,setter명)과 내가보내는 파라미터 명이 같으면 알아서 맵핑이 된다.									*/
	@PostMapping("member/signIn")
	public void login(@ModelAttribute Member m) {
		System.out.println("id4 : " + m.getId());
		System.out.println("pwd4 : " + m.getPwd());
		
	}
	
//	5. @ModelAttribute를 생략
//	@PostMapping("member/signIn")
//	public void login(Member m){
//		System.out.println("id4 : " + m.getId());
//		System.out.println("pwd4 : " + m.getPwd());
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
