package kh.springboot.board.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
		HashMap<String, String>map = new HashMap<String, String>();
		map.put("i", "2");
		int listCount = bService.getListCount(map);
		//현재 페이지, 전체 게시글 개수, 9개로 끊겠다(BoardLimit)
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 9); 
		
		/* 첨부파일 게시판 = 게시글 내용 + 첨부파일
		(1)게시글 제목이랑 내용: INSERT BOARD TABLE
		(2)첨부파일 부분: insert를 attachment테이블
		*첨부파일의 가장 첫번째 사진 : 썸네일로 이용하기 위해 attmLevel 컬럼 생성(ex. 0이면 썸넬, 1이면 아닌 것)
		*/
		ArrayList<Board> bList = bService.selectBoardList(pi,map); //(1)
		ArrayList<Attachment> aList = bService.selectAttmBoardList(null); //(2) -> 썸네일만 가져오는 쿼리랑 상세보기(이미지까지전부)쿼리 경우를 나눠주기(밑에 attmDetail 메소드)
		
		if(bList != null) {
			model.addAttribute("bList", bList).addAttribute("aList", aList)
				 .addAttribute("loc",request.getRequestURI()).addAttribute("pi", pi);
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
	
	//첨부파일 게시글 상세보기 (loginUser - 조히수 count하려고 필요)
	@GetMapping("{id}/{page}")
	public String selectAttm (@PathVariable("id") int bId,
							  @PathVariable("page") int page,
							  HttpSession session, Model model) {
		Member loginUser = ((Member)session.getAttribute("loginUser"));
		Board b = bService.selectBoard(bId, loginUser);
		ArrayList<Attachment>list = bService.selectAttmBoardList(bId);
		
		if(b!=null) {
			model.addAttribute("b", b)
			.addAttribute("page",page)
			.addAttribute("list",list);
			
			return "views/attm/detail";
		} else {
			throw new BoardException("첨부파일 게시글 상세보기 실패");
		}
		
	}
	//첨부파일 게시글 상세보기를 하기위한 첨부파일 select query
	/*
	 *   select * from attachment 
				  where attm_status='Y'and ref_board_id=?
				  order by attm_id
	 * 
	 * 해당 쿼리는 첨부파일 게시글 전체조회 쿼리랑 상당히 유사하므로 
	 * 
	 */
	
	//첨부파일 게시판 수정(내용을 물고 view에 뿌려주기)
	@PostMapping("/updForm")
	public String selectAttmBoard(@RequestParam("boardId") int bId,
								  @RequestParam("page")int page, Model model) {
		Board b = bService.selectBoard(bId, null);
		ArrayList<Attachment> list = bService.selectAttmBoardList(bId);
		model.addAttribute("b",b).addAttribute("page",page);
		model.addAttribute("list", list);
		return "views/attm/edit";
	}
	
	
	
	//첨부파일 게시판 수정 > deleteAttm배열로 받을 수 있다.
	@PostMapping("update")
	public String updateBoard(@ModelAttribute Board b,
						    @RequestParam("file")ArrayList<MultipartFile> files,
						    @RequestParam("deleteAttm")String[] deleteAttm,
						    @RequestParam("page")int page) {
		for(MultipartFile mf : files) {
			
			
			
			
			
		}
		
		/*
		 * 첨부파일이 여러 개인 상황
		 * 1)board만 바꿨을 때 : 3개, [,,] =>파일이름은 뜨지 않는다.
		 * 2)첨부파일을 삭제만 할때 : 삭제하는 파일명이 출력된다.
		 * 3)첨부파일 삭제, 추가 모두 할 때 : 변경된 사항 다 뜸
		 * 
		 * 첨부파일이 한 개 일 때
		 * 1)board만 바꿨을 때 : 0개, []라고 뜸 = length 0이 된다는 것 !!!!!!!!ㄴ
		 * 2)첨부파일 삭제만 할 때 : 1개,[파일명]
		 * 3) 첨부파일 삭제 + 추가할 때 : 1개[파일명]
		 * 4)첨부파일 추가만 할때 : 0개 ,[]
		 * */
		
		
		/*
		 * 1. 새 파일 o
		 * 	(1)기존파일 모두 삭제 : 기존파일 모두 삭제 & 새 파일 저장하기
		 * 	-> 새 파일 중에서 level 0, 1지정할 것.
		 * 	
		 * 	(2) 기존 파일 일부 삭제 : 기존 파일 일부 삭제 & 새 파일 저장하기
		 * ->기존 중에 level0 (썸네일) 검사해서 있으면 나머지를 모두 level 1로 지정,
		 * 	 level 0이 삭제된 거면 기존 파일 중 맨 앞의 파일을 level 0으로 지정
		 *
		 *	(3) 기존 파일 모두 유지 : 새 파일 저장만 하면 됨. 이 때 level 1로 지정
		 * 
		 * 
		 * 
		 * 2. 새 파일 x
		 * 	(1)기존 파일 모두 삭제 : 파일list모두 삭제 후, 일반게시판으로 이동
		 * 	->update board set board_type = '1'로 해주기
		 * 	->update board메소드를 .. 수정해야됨 ㅅㅂ
		 * 
		 * 	(2)기존 파일 일부 삭제 : 삭제 된 파일 중 level 0 의 유무 검사 
		 * 	->level 0 사라지면 기존 중 맨 앞의 파일을 level 0 새 지정, 그대로면 놔두기
		 * 
		 * 	(3)기존 파일 모두 유지 : board만 수정
		 * 
		 * 
		 */
		//1. 새 파일 o 
		b.setBoardType(2); 
		//새로 넣는 파일이 있다면 ArrayList<Attachment> list에 옮겨담기
		ArrayList<Attachment> list = new ArrayList<Attachment>();
		for(int i = 0; i < files.size(); i++) {
			MultipartFile upload = files.get(i);
			if(!upload.getOriginalFilename().equals("")) {
				String[] returnArr = saveFile(upload); //파일을 rename 후 파일저장소에 파일을 저장하는 메소드
				if(returnArr[1] != null) {
					Attachment a = new Attachment();
					a.setOriginalName(upload.getOriginalFilename());
					a.setRenameName(returnArr[1]);
					a.setAttmPath(returnArr[0]);
					a.setRefBoardId(b.getBoardId());
					list.add(a);
				}
			}
		}
		//삭제한다는 파일이 있으면 삭제할 파일의 이름과 레벨을 각각 delRename과 delLevel에 옮겨담기 : deleteAttm(버튼 눌러서파일 삭제한거)
		ArrayList<String> delRename = new ArrayList<String>();
		ArrayList<Integer> delLevel = new ArrayList<Integer>();
		for(String rename : deleteAttm) {
			if(!rename.equals("")) {
				String[] split = rename.split("/");
				delRename.add(split[0]);
				delLevel.add(Integer.parseInt(split[1]));
			}
		}
		//1.삭제하는 파일이 존재할 경우
		int deleteAttmResult = 0; /* = 삭제 완료한 resultMap의 행 개수*/
		boolean existBeforeAttm = true; //이전에 넣은 첨부파일이 존재하는지에 대한 boolean 메소드
		
		if(!delRename.isEmpty()) { //삭제 진행
			deleteAttmResult = bService.deleteAttm(delRename);
			if(deleteAttmResult > 0) {
				for(String rename : delRename) {
					deleteFile(rename);
				}
			}
			/*삭제했으니까 이제 다시 파일 레벨 지정하기
			1-1.기존파일 모두 삭제했을 때 조건 -> level 0, 1을 새로 지정해야함		*/
			if(deleteAttm.length == deleteAttmResult) {
				existBeforeAttm = false;
				if(list.isEmpty()) {
					//이제 남은 파일리스트가 비었을 때 -> 새 파일 추가가 없다는 거지->그럼 일반게시판으로 가야겠제
					b.setBoardType(1);
				} 
			//1-2.기존파일 중 일부 삭제 (level만 갖고있는 변수 : delLevel)
			} else { 
				for(int level : delLevel) {
					if(level == 0) {
						bService.updateAttmLevel(b.getBoardId()); 
						break;
					}
				}
			}
		}
		
		/*2.새 파일이 있을 때 새 파일들에 대한 level설정하기 ->list에 반복문 걸어서 level부여하기
		2-1. 이전에 넣은 첨부파일이 없어-> 새로운 파일들한테 level설정하기		*/
			existBeforeAttm = false; 
			for(int i = 0; i < list.size(); i++) {
				Attachment a = list.get(i);
				if(existBeforeAttm = true) {
					a.setAttmLevel(1);
				} else {
					if(i == 0) {
						a.setAttmLevel(0);
					} else {
						a.setAttmLevel(1);
					}
				}
			}
			
			//level새로 다시 지정 다 해줫으면 이제 service에 업데이트 하가ㅣ
			int updateBoardResult = bService.updateBoard(b);
			
			int updateAttmResult = 0;
			if(!list.isEmpty()) { //list 전송
				updateAttmResult = bService.insertAttm(list);
			}
		
			//게시글 수정 성공여부(성공 시 1) + 썸넬 level이 조정된 행의 개수가 리턴됨.
			//list.size() : 새로 추가 된 첨부파일의 개수
			if(updateBoardResult + updateAttmResult == list.size() + 1) {
				if(deleteAttm.length != 0 && delRename.size()== deleteAttm.length &&
						updateAttmResult == 0) {
					//즉, 새로 추가 된 게 없으면
					return "redirect:/board/list";
				} else {
					return String.format("redirect:/attm/%d/%d", b.getBoardId(), page);
				}
			} else {
				throw new BoardException ("첨부파일 게시글 수정을 실패하였습니다.");
			}
		
		}
	
//		@PostMapping("delete")
//		public String deleteAttm (@RequestParam("boardId")int bId) {
//			int result1 = bService.deleteBoard(bId); //보드 부분 삭제
//			int result2 = bService.statusNAttm(bId); // 첨부파일 부분 삭제(bId가 같은 것들 매칭시켜서)
//			
//			if(result1 > 0 && result2 > 0) {
//				return "redirect:/attm/list";
//			} else {
//				throw new BoardException("첨부파일 게시판 삭제 실패");
//			}
//		}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
