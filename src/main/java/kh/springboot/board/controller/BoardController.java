package kh.springboot.board.controller;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kh.springboot.board.model.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/board") //공용url만들기(/board/=/board 그게그거, 여러 개의 공용url은 ({"",""})해도 되는데 메소드마다 if로 경우를 나눠주어야 함.
@RequiredArgsConstructor
public class BoardController {

	private final BoardService bService;
	
	@GetMapping("/list")
	public ModelAndView selectList(@RequestParam(value="page", defaultValue="1")int currentPage,
							 ModelAndView mv, HttpServletRequest request) {
		int listCount = bService.getListCount(1); //보드 안에 게시판종류가 모두 들어가있고, 1이 일반게시판, 2가 첨부파일 게시판을 뜻함. 일반게시판에 대한 리스트카운트를 알기 위해 인자로 1을 넣은 것.
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5); //PageInfo에 기본설정해놓고 값 받아오기
		ArrayList<Board> list = bService.selectBoardList(pi, 1); //일반게시판의 데이터를 배열로 받아오기
		//받아온 데이터 물고 view로 보내기 (ModelAndView사용)
		mv.addObject("list", list).addObject("pi", pi).addObject("loc",request.getRequestURI()).setViewName("list");
		
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
		Board b = bService.selectBoard(bId, loginUser);
		if(b != null) {
			model.addAttribute("b",b).addAttribute("page",page);
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
	@PostMapping("delete")
	public String deleteBoard(@RequestParam("boardId")int bId) {
		int result = bService.deleteBoard(bId);
		if(result > 0) {
			return "redirect:/board/list";
			
		} else {
			throw new BoardException("게시글 삭제 실패");
		}
	}
	
	
	
	
	
	
	
	
	
}
	
	
	
	
	

