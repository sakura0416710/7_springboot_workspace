package kh.springboot.member.controller;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import kh.springboot.HomeController;
import kh.springboot.member.model.exception.MemberException;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller 
@RequiredArgsConstructor //final이 붙은 상수나 @NonNull이 붙은 변수만 가지고 생성자 생성
@SessionAttributes("loginUser")
@RequestMapping("/Member/") //공용 url
public class MemberController {

    private final HomeController homeController;

	//@service불러오기 (프레임워크가 만든 객체를 '주입')
	//1. 필드 주입
//	@Autowired
//	private MemberService mService;
	
	//2.생성자 주입(값을 넣어야 final로 고정시키는데 null을 넣을 순 없으니 @RequiredArgsConstructor 추가
	//lombok으로 생성
	private final MemberService mService;
	
	//암호화 설정파일 주입받아오기 (생성자 주입)
	private final BCryptPasswordEncoder bcrypt;


	/* 
    @GetMapping("/signIn") //그냥 signIn이 맞을것 같지만 '/' 붙여도 됨.
	public String signIn() {
		return "login";
	}
	
    파라미터 전송받는 방법
	1. HttpServletRequest 이용하기(servlet방식)
	@PostMapping("member/signIn")
	public void login(HttpServletRequest request) {
		String id = request.getParameter("id");
		String pwd = request.getParameter("pwd");
		System.out.println("id1 : " + id);
		System.out.println("pwd1 : " + pwd);
	}		*/
	
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
	
	
	4. @ModelAttribute 이용 : 필드명(getter,setter명)과 내가보내는 파라미터 명이 같으면 알아서 맵핑이 된다.									
	@PostMapping("member/signIn")
	public void login(@ModelAttribute Member m) {
		System.out.println("id4 : " + m.getId());
		System.out.println("pwd4 : " + m.getPwd());
		
	}			
	
	5. @ModelAttribute를 생략
	@PostMapping("member/signIn") --->처음 로그인 버전(암호화 전)
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
	}												*/
	
//	로그아웃 : session을 무효화시킨 뒤 home(경로 제시)으로 이동
	@GetMapping("logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/home";
	}															

	
	
	//회원 등록(뷰 보여주기 viewResolver->view)
	@GetMapping("enroll")
	public String insertMember() {
		return ("enroll");
	}
	// 값 받아오기
	@PostMapping("enroll")
	public String insertMember(@ModelAttribute Member m,
								@RequestParam("emailId") String emailId,
								@RequestParam ("emailDomain")String emailDomain) {
		if (!emailId.trim().equals("")) {
			m.setEmail(emailId + "@" + emailDomain);
		}
		String encPwd = bcrypt.encode(m.getPwd());
		m.setPwd(encPwd);
	
		//회원가입 성공, 실패 경로
		int result = mService.insertMember(m);
		
		if(result > 0) {
			return "redirect:/home";
		}else {
			throw new MemberException("회원가입을 실패하였습니다.");
		}
		
		
	}
/*	암호화 후 로그인
	@PostMapping("/signIn")
	public String login(Member m, HttpSession session){
		Member loginUser = mService.login(m);
		//로그인 유저가 있다면 (아이디 존재여부로 비교) 그 로그인 유저의 비번을 꺼내오겠다. (그게 db에 암호화돼서 있을거니까 그걸 꺼내오면 댐)
		if(loginUser != null && bcrypt.matches(m.getPwd(),loginUser.getPwd())) {
			session.setAttribute("loginUser", loginUser);//인자 두개 : rawPassword, encodedPassword
			return "redirect:/home";
		} else {
			throw new MemberException("로그인을 실패하였습니다.");
		}
	} 			 */
	
	//마이페이지 이동 : list를 담아서 view에 보내는 방법 2가지
	//1.Model객체 이용 : request영역에 담기는 Map형식(key-value)의 객체
//	@GetMapping("/member/myInfo")
//	public String myInfo(HttpSession session, Model model) {
//		Member loginUser = (Member)session.getAttribute("loginUser");
//		if(loginUser != null) {
//			String id = loginUser.getId();
//			//아이디 전달 (arrayList<map>형식으로 db에서 쿼리적은 걸 담아서 보낼것
//			ArrayList<HashMap <String, Object>> list = mService.selectMyList(id); 
//			model.addAttribute("list", list);
//		}
//		return "views/member/myInfo";
//	}
	
	//2.ModelAndView객체 이용하기: Model + View
	//model에 데이터를 저장하고 view에 forward할 뷰 정보를담음.
	@GetMapping("myInfo")
	public ModelAndView myInfo(HttpSession session, ModelAndView mv) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		if(loginUser != null) {
			String id = loginUser.getId();
			ArrayList<HashMap <String, Object>> list = mService.selectMyList(id); 
			mv.addObject("list", list);
			mv.setViewName("myInfo");
		}
		return mv;
	}
	
	
/*	 	암호화 후 로그인 + @SessionAttribute : 스프링부트가 제공하는 기능
		model에 attribute가 추가될 때 자동으로 키 값을 찾아서 일치하는 키가 있으면 세션에 등록하는 기능
		세션 영역에 저장은 하지만 로그아웃할 때 session.invalidate으로 무효화가 안돼서 로그아웃이 안됨.
		이거에 맞는 로그아웃을 만들어야 함. */
	@PostMapping("signIn")
	public String login(Member m, Model model){
		Member loginUser = mService.login(m);
		if(loginUser != null && bcrypt.matches(m.getPwd(),loginUser.getPwd())) {
			model.addAttribute("loginUser", loginUser);
			return "redirect:/home";
		} else {
			throw new MemberException("로그인을 실패하였습니다.");
		}
	} 			
	
//	로그아웃 session.Attribute버전 		
	@GetMapping("logout")
	public String logout(SessionStatus status) {
		status.setComplete();
		return "redirect:/home";
	}	
	
	@GetMapping("/edit")
	public String edit() {
		return "edit";
	}
	
	@PostMapping("edit")
	public String edit (@ModelAttribute Member m, @RequestParam("emailId") String emailId,
						@RequestParam("emailDomain")String emailDomain, Model model) {
		if(!emailId.trim().equals("")) {
			m.setEmail(emailId + "@" + emailDomain);
		}
		int result = mService.updateMember(m);
		if(result > 0) {
			model.addAttribute("loginUser", mService.login(m));
			return "reirect:/member/myInfo";
		} else {
			throw new MemberException ("회원정보 수정을 실패했습니다.");
		}
	}
	
	@PostMapping("updatePassword")
	public String updatePassword(@RequestParam("currentPwd")String pwd, 
								 @RequestParam("newPwd") String newPwd,
								  Model model) {
		//Member m = (Member)session.getAttribute("loignUser");
		Member m = (Member)model.getAttribute("loginUser"); //세션말고 모델안의 SessionAttribute로 데이터 조회가능
		//지금 친 비번이랑 암호화된 비번이랑 같은 지 (현재비번 비교)
		if(bcrypt.matches(pwd, m.getPwd())) {
			//현재 비번이 일치한다면 새로 친 비번을 암호화하고 암호화된 새 비번을 여기다 담아. 
			m.setPwd( bcrypt.encode(newPwd)); 
			int result = mService.updatePassword(m);
			if(result > 0) {
				model.addAttribute("loginUser", m);
				return "redirect:/home";
			}else {
				throw new MemberException("비밀번호 수정을 실패하였습니다.");
			}
			
		}else {
			throw new MemberException("비밀번호 수정을 실패하였습니다.");
		}
		
	}
	//회원 탈퇴 : delete가 아니라 update status를 'N'으로 해야함
	
	@GetMapping("delete")
	public String deleteMember(Model model) {
		int result = mService.deleteMember(((Member)model.getAttribute("loginUser")).getId());
		
		if(result > 0 ) {
			return "redirect:/home";
			
		}
		return "redirect:/home";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
