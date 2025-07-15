package kh.springboot.board.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kh.springboot.board.model.exception.BoardException;
import kh.springboot.board.model.service.BoardService;
import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.common.Pagination;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/attm")
@RequiredArgsConstructor
public class AttachmentController {

    private final BoardController boardController;

	private final BoardService bService;

	//첨부파일 게시판 전체조회하기 (페이지네이션:전체 게시글 개수 조회하고 한 창에 몇개씩 보이게 할지 정하기)
	@GetMapping("list")
	public String selectList(@RequestParam(value="page",defaultValue="1")int currentPage,
									Model model, HttpServletRequest request ) {
		int listCount = bService.getListCount(2);
		//현재 페이지, 전체 게시글 개수, 9개로 끊겠다(BoardLimit)
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 9); 
		
		/* 첨부파일 게시판 = 게시글 내용 + 첨부파일
		(1)게시글 제목이랑 내용: INSERT BOARD TABLE
		(2)첨부파일 부분: insert를 attachment테이블
		*첨부파일의 가장 첫번째 사진 : 썸네일로 이용하기 위해 attmLevel 컬럼 생성(ex. 0이면 썸넬, 1이면 아닌 것)
		*/
		ArrayList<Board> bList = bService.selectBoardList(pi,2); //(1)
		ArrayList<Attachment> aList = bService.selectAttmBoardList(); //(2)
		
		if(bList != null) {
			model.addAttribute("bList", bList).addAttribute("aList", aList).addAttribute("loc",request.getRequestURI()).addAttribute("pi", pi);
			return "views/attm/list";
		} else {
			throw new BoardException("첨부파일 게시글 조회 실패");
		}
		
	}
	
	//첨부파일 게시판 작성 뷰 넘기기
	@GetMapping("write")
	public String writeAttm() {
		return "views/attm/write";
	}
	
	//첨부파일 작성 데이터 저장(Board:제목, 글내용 Attachment : 파일)
	@PostMapping("insert")
	public String insertAttamBoard(@ModelAttribute Board b,
								 @RequestParam("file")ArrayList<MultipartFile> files,
								 HttpSession session) {
		String id = ((Member)session.getAttribute("loginUser")).getId();
		b.setBoardWriter(id);
		
		//ArrayList<MultipartFile>를 내가 쓸 수 있는 형태로 바꿔두기
		//파일을 안 넣어도 있는 거처럼 나오므로 파일이름을 syso해보면 띄어쓰기(빈칸)으로 나옴.
		ArrayList<Attachment> list = new ArrayList<Attachment>();
		for(int i = 0; i < files.size(); i++) {
			MultipartFile upload = files.get(i);
			/*System.out.println(upload.getOriginalFilename()); //업로드한 파일의 원본 이름을 리턴 ; 파일을 넣은 경우 파일명, 없을 경우 ""로 리턴.
			if(upload != null && !upload.isEmpty()) {
				isEmpty: 사이즈를 보고 빈 건지 아닌지 판단함(파일이 0바이트면 안 들어감..)*/
			if(!upload.getOriginalFilename().equals("")) {
				String[] returnArr = saveFile(upload); //파일을 rename 후 파일저장소에 파일을 저장하는 메소드
				if(returnArr[1] != null) {
					Attachment a = new Attachment();
					a.setOriginalName(upload.getOriginalFilename());
					a.setRenameName(returnArr[1]);
					a.setAttmPath(returnArr[0]);
					
					list.add(a);
				}
			}
		}
		
		//attamLevel삽입(list에 반복문 걸고 n번째 줄 상관없이 첫번째에 있는 파일에 level 0 부여해서 썸네일로 만들기)
		for(int i = 0; i<list.size(); i++) {
			Attachment a = list.get(i);
			if(i==0) {
				a.setAttmLevel(0);
			} else {
				a.setAttmLevel(1);
			}
		}
		//이제 insert준비
		int result1 = 0;
		int result2 = 0;
		
		//첨부파일이 존재하지 않으면 일반게시판으로 옮겨야 한다.
		if(list.isEmpty()) {
			b.setBoardType(1);
			result1 = bService.insertBoard(b);
		} else {
			//첨부파일은 attchment에 insert.
			//게시판타입2로 설정하고 제목내용 넣고 파일부분도 넣어주기
			b.setBoardType(2);
			result1 = bService.insertBoard(b);
			
			/*selectKey도입: board insert로 seq + 1하고 attachment올릴 때 사용할 seq=currVal로 쓰려는데 누가 그 사이에 board insert하면 seq+1에서 또 +1되버리므로
			attchment의 currval이 seq+1+1로 들어가게 되는 사고를 방지하게 위해서 사용 => insertBoard에 적용 */
			for(Attachment a: list) {
				a.setRefBoardId(b.getBoardId());
			}
			result2 = bService.insertAttm(list);
		}
		
		if(result1 + result2 == list.size() + 1 ) {
			//첨부파일 등록 성공했을 때 -첨부파일이 없는 경우도 있으므로
			//attm.list로 가지말고 경우를 나누어야 한다.
			if(result2 == 0) {
				return "redirect:/board/list";
			} else {
				return "redirect:/attm/list";
			}
			
		} else {
			for(Attachment a : list) {
				deleteFile(a.getRenameName());
			}
			throw new BoardException("첨부파일 게시글 작성을 실패하였습니다.");
		}
		
		
		
		
	}
	
	public String[] saveFile(MultipartFile upload) {
		//파일경로 찾기 (프로젝트 아에 외부에 저장하기 : 일반 폴더가 아니라 서버를 파서 저장한다던가, 다른 컴퓨터에 한다던가..)
		String savePath = "c:\\uploadFiles";
		//파일 만들기(java.io.file)
		File folder = new File(savePath);
		if(!folder.exists()) {
			folder.mkdirs();
		}
		
		/*파일명 rename (DB에 다른사람이 넣은 같은 파일명이 들어가고 헷갈리잖아!)
		:시간을 쓰는 게 제일 안 겹치는 방법 				*/
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		int ranNum = (int)(Math.random()*100000); //랜덤숫자까지 준비(밀리세컨까지 똑같을 경우 방지)
		String originFileName = upload.getOriginalFilename();
		//날짜 + 랜덤숫자 + 확장자 이어붙이기(substring자르기)
		String renameFileName = sdf.format(new Date()) + ranNum 
								+ originFileName.substring(originFileName.lastIndexOf("."));
	
		//이제 renamePath에 저장하고 그걸 upload에 이동시키기. 
		String renamePath = folder + "\\" + renameFileName;
		try {
			upload.transferTo(new File(renamePath));
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] returnArr = new String[2];
		returnArr[0] = savePath; //attmPath를 집어넣음
		returnArr[1] = renameFileName;
		return returnArr;
	}
	
	
	
	public void deleteFile(String renameName) {
		String savePath = "c:\\uploadFiles";
		
		File f = new File(savePath + "\\" + renameName);
		if(f.exists()) {
			f.delete();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
