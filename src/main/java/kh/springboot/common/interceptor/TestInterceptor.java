package kh.springboot.common.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TestInterceptor implements HandlerInterceptor {

//		@Override
//		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//				throws Exception {
//			// Dispatcher Servlet이 Controller를 호출하기 전에 수행 = Controller로 요청이 들어가기 전
//			
//			System.out.println("-----------------------------START-----------------------------");
//			System.out.println(request.getRequestURI() + "\n");
//			
//			
//			
//			return HandlerInterceptor.super.preHandle(request, response, handler);
//		}
//		
//		
//		@Override
//		public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//			ModelAndView modelAndView) throws Exception {
//		//Controller에서 DisptacherServlet으로 리턴되는 순간에 수행
//			
//			System.out.println("----------view------------");
//			System.out.println(request.getRequestURI());
//			if(modelAndView != null) {
//				System.out.println(modelAndView.getModel());
//				System.out.println(modelAndView.getViewName() + "\n");
//			}
//		
//		
//		
//		
//		
//		
//		}
//	
//		@Override
//		public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
//			throws Exception {
//		// view까지 모든 작업이 완료된 후 수행 = 최종결과를 생성하는 일까지 포함
//		System.out.println("~~~~~~~~~~~END~~~~~~~~~~~~~~~");
//		System.out.println(request.getRequestURI() + "\n");
//			
//		}
//	
//	
//	
	
}
