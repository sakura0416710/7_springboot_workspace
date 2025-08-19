package kh.springboot.common.interceptor;

import java.util.regex.Pattern;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kh.springboot.member.model.vo.Member;

public class checkLoginInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		HttpSession session = request.getSession();
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		if(loginUser == null) {
			String url = request.getRequestURI();
			//메시지 띄워주고 url이동하기
			//로그인 후 이용하세요
			//로그인 세션이 만료되어 로그인 화면으로 넘어갑니다
			
			String msg = null;
			String regExp = "/(board|attm)/\\d+/\\d+"; //정규표현식 (\\ :역슬래쉬 한 개로 인식)
			if(Pattern.matches(regExp, url)) {
				msg = "로그인 후 이용하세요";
			} else {
				msg = "로그인 세션이 만료되어 로그인 화면으로 넘어갑니다.";
			}
			
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().write("<script>alert('" + msg + "'); location.href='/member/signIn';</script>");
			return false; //preHandle은 return true를 반환함. (컨트롤러 요청 전이기 때문에 true일 때 컨트롤러에 요청을 반환함)
		}
		
		
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
}
