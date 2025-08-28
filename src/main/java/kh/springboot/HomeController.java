package kh.springboot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller //bean(컨트롤러 역할을 하는 bean(=어노테이션) 생성. 객체생성하라고 프레임워크에 지시하는 역할 
public class HomeController {

	@GetMapping("home")
	public String home() {
		return "views/home";
	}
	
	@GetMapping("/data")
	@ResponseBody
	public int test() {
		return 410;
	}
}
