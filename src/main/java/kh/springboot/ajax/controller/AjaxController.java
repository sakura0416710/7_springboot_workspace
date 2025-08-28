package kh.springboot.ajax.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.Reply;
import kh.springboot.member.model.service.MemberService;
import kh.springboot.member.model.vo.Member;
import kh.springboot.member.model.vo.TodoList;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/member","/board"})
@SessionAttributes("loginUser")

public class AjaxController {
	/*
	 * 비동기 통신 ajax
	 * Controller -> view데이터를 보내는 방법2가지
	 * 1.ResponseBody (return o) - 비동기통신 2.PrintWriter
	 * 
	 * 
	 * 
	 * RestController : @Controller + @ResponseBody 합쳐진 형태
	 *								  반환형태 : JSON 형태로 객체데이터를 많이 보냄 {key, value형식의 map 형태}
	 * ->REST API 개발에 쓰임
	 *
	 * REST API (Representational State Transfer API) 
	 * : 자원을 이름으로 구분하여 해당 자원의 상태를 주고 받는 모든 것
	 * (상태값에 따라서 데이터 통신을하겟다)
	 * 1. HTTP URI를 통해 자원 명시 (ex. lupdate (x) list(o)
	 * 2. HTTP Method (Get. Post, PUT, DELETE, PATCH 등)를 통해 해당 자원(URI)에 대한 CRUD를 정함
	 * 3. Create Read Update Delete = CRUD
	 * create : 데이터 생성(POST)
	 * read : 데이터 조회 (get)
	 * update : 데이터 수정 (put, patch)
	 * delete : 데이터 삭제(delete)
	 * REST FULL API : 모든 것이 rest 형식으로 진행된 것  
	 * */
	
	private final MemberService mService;
	private final BoardService bService;
	//이메일 샌더 설정
	private final JavaMailSender mailSender;
	
	//아이디 중복 확인 
	@GetMapping("checkValue")
	//메서드 리턴값을 view가 아니라 HTTP응답 바디에 직접 쓰겠다. =>ajax요청을 처리해서 json, int등 반환(문자열, 숫자 등 데이터 등을 그대로 응답한다API처럼)
	public int checkValue (@RequestParam("value")String value,
							@RequestParam("column")String column) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("col", column);
		map.put("val", value);

		return mService.checkValue(map); //0, 아님 1을 반환해서 ajax로 보냄
		
	}
	//이메일 인증
		@GetMapping("echeck")
		public String checkEmail(@RequestParam("email")String email) {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			
			//수신자, 제목, 본문 설정
			String subject = "[SpringBoot] 이메일 확인";
			String body = "<h1 align = 'center'>SpringBoot 이메일 확인</h1><br>";
			body += "<div style='border : 5px solid yellowgreen; text-align: center; font-size:25px;'>";
			body += "본 메일은 이메일 확인하기 위해 발송되었습니다.<br>";
			body += "아래 숫자를 인증번호 확인란에 작성하여 확인해주시기 바랍니다.<br><br>";
		
			//랜덤 숫자 5개뽑기 : 이 방법은 안됨Math.random()* 100000 + 1; //0 <= N < 100000 : 0 ~ 99999.99999999999999
			String random = ""; //null로 초기화 불가. random이라는 변수에 한 숫자씩 이어붙여서 5자리를 만들 것임. += 연산자로 이어붙일건데
								//null로 해버리면 null7604이런 식으로 붙여짐.
			for(int i = 0; i<5; i++) {
				random += (int)(Math.random()*10);
			}
			body += "<span stlye='font-size:30px; text-decoration:underline;'><b>" + random + "</b><span><br></div>";
			
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
			try {
				mimeMessageHelper.setTo(email);
				mimeMessageHelper.setSubject(subject);
				mimeMessageHelper.setText(body, true);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			mailSender.send(mimeMessage); //메일 발송
			return random;				  //보낸 랜덤값 
		
		}
		@PostMapping("list")
		public int insertTodoList(
				@ModelAttribute TodoList todoList) {
			int result = mService.insertTodoList(todoList);
			return result > 0? todoList.getTodoNum() : result;
		}
		
		@PutMapping("list")
		public int updateTodoList(@ModelAttribute TodoList todoList) {
			return mService.updateTodoList(todoList);
		}
		@DeleteMapping("list")
		public int deleteTodoList(
				@RequestParam("num") int num) {
			return mService.deleteTodoList(num);
		}
	
	//BoardController
		@GetMapping(value = "top",produces="application/json; charset=UTF-8")//response의 contentType을 제어함
		public String selectTop(HttpServletResponse response){
			ArrayList<Board> topList = bService.selectTop();
			
			//json 버전
			//Board = >JSONObject, arrayList => JSONArray
			JSONArray array = new JSONArray();
			for (Board b : topList) {
				JSONObject json = new JSONObject();
				json.put("boardId", b.getBoardId());
				json.put("boardTitle", b.getBoardTitle());
				json.put("nickName", b.getNickName());
				json.put("boardModifyDate", b.getBoardModifyDate());
				json.put("boardCount",b.getBoardCount());
				
				array.put(json); //지원하는 라이브러리 종류에 따라 메소드, Date지원도 됨 (예전에는 add, date지원 안됏음. 그건json simple)
				
			}
			//response.setContentType("application/json; charset=UTF-8");
			return array.toString();	
			
			
			/*GSON버전
			Gson gson = new Gson();
			response.setContentType("application/json; charset=UTF-8");
			gson = new GsonBuilder() .setDateFormat("yyyy-MM-dd").create();
			try {
				gson.toJson(topList, response.getWriter());
			} catch (JsonIOException | IOException e) {
				e.printStackTrace();
			}		*/
			
		}
		//댓글등록 jackson버전 : springboot에서 자동으로 라이브러리 제공
		@PostMapping("reply")
		public String insertReply(@ModelAttribute Reply r/*, HttpServletResponse response*/) {
			int result = bService.insertReply(r);
			ArrayList<Reply> list = bService.selectReplyList(r.getRefBoardId());
			
			ObjectMapper om = new ObjectMapper(); //내가 String으로 값을 쓰겠다.
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			om.setDateFormat(sdf);
			String str = null;
			try {
				str = om.writeValueAsString(list);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} 
			//response.setContentType("application/json; charset=UTF-8"); 둘다됨 !
			return str;
			
		}
		
		//댓글 삭제
		@DeleteMapping("reply")
		public int deleteReply(@RequestParam("rId")int rId) {
			return bService.deleteReply(rId);
			
			
		}
		//댓글 수정(rId랑 replyContent를 reply r에 (객체만든거)담아서 보내기)
		@PutMapping("reply")
		public int updateReply(@ModelAttribute Reply r) {
			return bService.updateReply(r);
		}
	
		
		@PutMapping("profile")
		public int updateProfile(@RequestParam("profile")MultipartFile profile, Model model) {
			Member m = (Member)model.getAttribute("loginUser");
			String savePath = "C:\\profiles";
			File folder = new File(savePath);
			if(!folder.exists()) {
				folder.mkdirs();
			}
			if(m.getProfile() != null) {
				File f = new File(savePath + "\\" + m.getProfile());
				f.delete();
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			int ranNum = (int)(Math.random() * 100000);
			String originalFileName = profile.getOriginalFilename();
			String renameFileName = sdf.format(new Date()) + ranNum + 
								originalFileName.substring(originalFileName.lastIndexOf("."));
			
			try {
				profile.transferTo(new File(folder + "\\" + renameFileName));
			} catch (IllegalStateException| IOException e) {
				e.printStackTrace();
			}
			m.setProfile(renameFileName);
			
			int result = mService.updateProfile(m);
			if(result > 0 ) {
				model.addAttribute("loginUser", m);
			}
			return result;
		}
		
		//날씨 API
		
		@GetMapping("weather")
		public String getWeather() {
			StringBuilder sb = new StringBuilder();
			try {
			StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /*URL*/
	        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=6e6e555fe67d33cdf8fffb0c90e6e303ff3b6b62654acb51f1716157e1ae044f"); /*Service Key*/
	        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
	        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
	        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
	        
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmm");
	        String now = sdf.format(new Date());
	        String[] dayTime = now.split(" "); //공백으로 잘라주기
	        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(dayTime[0], "UTF-8")); /*‘21년 6월 28일발표*/
	        
	        int[] baseTime = {200, 500, 800, 1100, 1400, 2000, 2300};
	        int index = -1;
	        for(int i = 0; i < baseTime.length; i++) {
	        	if(Integer.parseInt(dayTime[1]) <= baseTime[i]){
	        		index = i-1; //현재의 시간 중 가장 가까운 과거 시간 찾기
	        		
	        		if(i == 0) {
	        			index = i;
	        		}
	        		
	        		dayTime[1] = ("0" + baseTime[index]).substring(("0" + baseTime[index]).length()-4);
	        		break;
	        	}
	        
	    
			}
	        if(index == -1) {
	        	dayTime[1] = "2300";
	        }
	        
	        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(dayTime[1], "UTF-8")); /*05시 발표*/
	        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode("60", "UTF-8")); /*예보지점의 X 좌표값*/
	        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode("127", "UTF-8")); /*예보지점의 Y 좌표값*/
	       
	        //URL url = new URL(urlBuilder.toString());
	        URL url = (new URI(urlBuilder.toString())).toURL();
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Content-type", "application/json");
	        //System.out.println("Response code: " + conn.getResponseCode());
	        BufferedReader rd;
	        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
	            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        } else {
	            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
	        }
	        String line;
	        while ((line = rd.readLine()) != null) {
	            sb.append(line);
	        }
	        rd.close();
	        conn.disconnect();
	        
	    
		}catch(Exception e) {
			e.printStackTrace();
		}
			return sb.toString();
	
		}
	
}
