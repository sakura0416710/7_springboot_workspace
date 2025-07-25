package kh.springboot.board.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kh.springboot.board.model.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.board.model.vo.Reply;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/board") //공용url만들기(/board/=/board 그게그거, 여러 개의 공용url은 ({"",""})해도 되는데 메소드마다 if로 경우를 나눠주어야 함.
@RequiredArgsConstructor
public class BoardController {

	private final BoardService bService;
	
	@GetMapping({"/list", "search"})
	public ModelAndView selectList(@RequestParam(value="page", defaultValue="1")int currentPage,
							 ModelAndView mv, HttpServletRequest request, @RequestParam HashMap<String, String> map) {
		
		map.put("i", "1");
		int listCount = bService.getListCount(map); //보드 안에 게시판종류가 모두 들어가있고, 1이 일반게시판, 2가 첨부파일 게시판을 뜻함. 일반게시판에 대한 리스트카운트를 알기 위해 인자로 1을 넣은 것.
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5); //PageInfo에 기본설정해놓고 값 받아오기
		ArrayList<Board> list = bService.selectBoardList(pi, map); //일반게시판의 데이터를 배열로 받아오기
		//받아온 데이터 물고 view로 보내기 (ModelAndView사용)
		mv.addObject("list", list).addObject("pi", pi).addObject("loc",request.getRequestURI()).setViewName("list");
		mv.addObject("map", map); //검색결과 유지하기
		
		return mv;
	}
	
	@GetMapping("write")
	public String write() {
		return "write";
	}
	
	@PostMapping("/insert")
	public String write(@ModelAttribute Board b, HttpSession session){
		String boardWriter = ((Member)session.getAttribute("loginUser")).getId();
		b.setBoardType(1);
		b.setBoardWriter(boardWriter);
		
		int result = bService.insertBoard(b); 
		
		if(result > 0) {
			//게시글 저장 성공 시 리스트 쪽으로 이동
			return "redirect:/board/list";
		} else {
			throw new BoardException("게시글 작성을 실패하였습니다.");
		}
	}
	/*
	 * url을 통해 전달된 값을 파라미터로 받아오기
	 * 1)http://localhost:8080/board?id=10&page=1 ->쿼리스트링 이용하여 여러 개 값을 전달 	: @RequestParam
	 * 2)http://localhost:8080/board/10/1		  ->우리가 쓴 방법(데이터 전달 방법 중 하나) : @PathVariable
	 * 
	 */
	//게시판 상세조회
	@GetMapping("/{id}/{page}")
	public String selectBoard(@PathVariable("id")int bId, @PathVariable("page") int page, HttpSession session, Model model) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		Board b = bService.selectBoard(bId, loginUser);;
		ArrayList<Reply> list = bService.selectReplyList(bId);
		if(b != null) {
			model.addAttribute("b",b).addAttribute("page",page);
			model.addAttribute("list", list);
			return "detail";
		} else {
			throw new BoardException("게시글 상세보기를 실패하였습니다.");
		}
	}


	
	//게시글 수정페이지(detail.html -> 내용을 물고 edit.html)
	@PostMapping("updForm")
	public String updateForm(@RequestParam("boardId")int bId,
							 @RequestParam("page")int page, Model model) {
		Board b = bService.selectBoard(bId, null);
		model.addAttribute("b",b).addAttribute("page",page);
		return "views/board/edit";
	}
	
	@PostMapping("update")
	public String updateBoard(@ModelAttribute Board b,@RequestParam("page")int page) {
		b.setBoardType(1);
		int result = bService.updateBoard(b);
		if(result > 0) { 
			//게시글 상세조회로 넘기기("/{id}/{page}")
			//return "redirect:/board/" + b.getBoardId() + "/" + page;
			return String.format("redirect:/board/%d/%d", b.getBoardId(),page); //printf같이 쓸 수도 있다.
		} else {
			throw new BoardException("게시글 수정을 실패하였습니다.");
		}
		
	}
	@PostMapping("delete") //보드에서 status=N되면 attm, reply도 트리거로 자동status=N으로 업데이트.
	public String deleteBoard(@RequestParam("boardId")int bId, HttpServletRequest request) {
		int result = bService.deleteBoard(bId);
		if(result > 0) {//redirect + board list혹은 attm list으로 경로 나누기 -> 최종경로 board/delete로 가서 삭제하는 것.
			return "redirect:/" + (request.getHeader("referer").contains("board")? "board" : "attm") + "/list";
			
		} else {
			throw new BoardException("게시글 삭제 실패");
		}
	}

	
	
	@GetMapping(value = "top",produces="application/json; charset=UTF-8")//response의 contentType을 제어함
	@ResponseBody
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
	
	
	
	/*댓글 등록(JSON버전)
	@PostMapping(value = "rinsert", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String insertReply(@ModelAttribute Reply r) {
		//arrayList에 담아야된느거 아니냐
		int result = bService.insertReply(r);
		ArrayList<Reply> list = bService.selectReplyList(r.getRefBoardId());
		
		JSONArray array = new JSONArray();
		for(Reply reply : list) {
			JSONObject json = new JSONObject();
			json.put("replyContent", reply.getReplyContent());
			json.put("nickName", reply.getNickName());
			json.put("replyModifyDate", reply.getReplyModifyDate());
			array.put(json);
		}
		return array.toString();				
		
	}		
	
	//댓글 등록(GSON버전)
	@PostMapping("rinsert")
	@ResponseBody
	public void insertReply(@ModelAttribute Reply r, HttpServletResponse response) {
		int result = bService.insertReply(r);
		ArrayList<Reply> list = bService.selectReplyList(r.getRefBoardId());
		
		response.setContentType("application/json; charset=UTF-8");
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		try {
			gson.toJson(list, response.getWriter());
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
		
		
	}				*/
	
	//댓글등록 jackson버전 : springboot에서 자동으로 라이브러리 제공
	//
	@GetMapping(value = "rinsert", produces="application/json; charset=UTF-8")
	@ResponseBody
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
	@GetMapping(value="rdelete", produces="application/json; charset=UTF-8")
	@ResponseBody
	public int deleteReply(@RequestParam("rId")int rId) {
		return bService.deleteReply(rId);
		
		
	}
	//댓글 수정(rId랑 replyContent를 reply r에 (객체만든거)담아서 보내기)
	@GetMapping("rupdate")
	public int updateReply(@ModelAttribute Reply r) {
		return bService.updateReply(r);
	}
	/*게시글 검색
	@GetMapping("search")
	public String searchBoard(@RequestParam(value="page", defaultValue="1")int currentPage, 
							  @RequestParam HashMap<String, String> map, Model model, 
							  HttpServletRequest request) {
		map.put("i", "1");
		int listCount = bService.getListCount(map); //원래 있는 보드리스트 뽑아오는 메소드 +인자변경해서 사용하기 (or메소드 새로만들기)
		
		//pagination처리
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5);
		ArrayList<Board> list = bService.selectBoardList(pi, map);
		model.addAttribute("list", list).addAttribute("pi", pi).addAttribute("loc", request.getRequestURI());
		
		
		return "list";
	}	=> list랑 search랑 한꺼번에 처리하도록 함.			*/
}
	
	
	
	
	

