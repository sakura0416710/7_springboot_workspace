package kh.springboot.member.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import kh.springboot.member.model.exception.MemberException;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller 
@RequiredArgsConstructor //final이 붙은 상수나 @NonNull이 붙은 변수만 가지고 생성자 생성
public class MemberController {

	//@service불러오기 (프레임워크가 만든 객체를 '주입')
	//1. 필드 주입
//	@Autowired
//	private MemberService mService;
	
	//2.생성자 주입(값을 넣어야 final로 고정시키는데 null을 넣을 순 없으니 @RequiredArgsConstructor 추가
	//lombok으로 생성
	private final MemberService mService;
	
	
 
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
//	@PostMapping("member/signIn")
//	public void login(@ModelAttribute Member m) {
//		System.out.println("id4 : " + m.getId());
//		System.out.println("pwd4 : " + m.getPwd());
//		
//	}
	
//	5. @ModelAttribute를 생략
	@PostMapping("member/signIn")
	public String login(Member m, HttpSession session){
		
	//	System.out.println(mService); //mService의 주소값이 새로고침할때마다 바뀜(=결합도가 높다). 결합도가 낮게, 분리시키는 것이 관건.
		Member loginUser = mService.login(m);
		if(loginUser != null ) {
			session.setAttribute("loginUser", loginUser);
			return "redirect:/home"; //view/home은 forward 방식. sendRedirect로 url을 home으로 재요청.
			//어차피 데이터를 session에 담아서 redirect해도 됨(request에 담았으면 redirect시 데이터 사라짐)
		} else {
			//사용자정의 에러 가능 ->throw: 에러 강제발생->사용자정의 에러이므로 클래스 만들어주기
			//안잡아도 되는 uncheckedError(최상위 클래스인 RuntimeException을 상속받는 걸로 바꿔주기
			throw new MemberException("로그인을 실패하였습니다.");
		}
	}
	
	//로그아웃 : session을 무효화시킨 뒤 home(경로 제시)으로 이동
	@GetMapping("/member/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/home";
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
}
