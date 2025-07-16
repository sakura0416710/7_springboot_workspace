package kh.springboot.board.model.service;

import java.util.ArrayList;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import kh.springboot.board.model.mapper.BoardMapper;
import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;
import kh.springboot.member.model.vo.Member;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardMapper mapper;
	
	
	public int getListCount(int i) {
		return mapper.getListCount(i);
	}
	
	
	public ArrayList<Board> selectBoardList(PageInfo pi, int i) {
		int offset = (pi.getCurrentPage()-1)*pi.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset, pi.getBoardLimit());
		return mapper.selectBoardList(i, rowBounds);
	}
	
	//게시글 등록
	public int insertBoard(Board b) {
		return mapper.insertBoard(b);
	}
	
	//게시글 상세조회 (조회수 증가로직까지 : 작성자와 같을 때만 증가하는거.)
	public Board selectBoard(int bId, Member loginUser) {
		Board b = mapper.selectBoard(bId);
		if(b != null) {
			if(loginUser != null && b.getBoardWriter().equals(loginUser.getId())) {
				int result = mapper.updateCount(bId);
				if(result > 0 ) {
					b.setBoardCount(b.getBoardCount()+1); //조회수 증가 로직
				}
			}
		}
		return b;
	}

	//게시글 수정
	public int updateBoard(Board b) {
		return mapper.updateBoard(b);
	}

	//게시글 삭제 
	public int deleteBoard(int bId) {
		return mapper.deleteBoard(bId);
	}


	//첨부파일 게시글 전체조회하기
	//(인자 : null을 받아야 해서 int가 아니라 인티져로 바꿔주기)
	public ArrayList<Attachment> selectAttmBoardList(Integer bId) {
		return mapper.selectAttmBoardList(bId);
	}


	public int insertAttm(ArrayList<Attachment> list) {
		/*방법 1.
		 * 이걸 list의 길이만큼 넣어야 하므로 반복문이 필요함. 근데 쿼리를 그렇게 쓸 순 없으니까
		 *for문으로 DB에 반복 요청을 하는 것.
		 * insert into attachment
		 * values (seq_aid.nextVal, 
		 * #{attmId}, #{originalName}, #{renameName}, #{attmPath}, #{attmLevel}, default)
		 
		
		int result = 0;
		for(int i = 0; i <list.size(); i++) {
			Attachment a = list.get(i);
			result = mapper.insertAttm(a);
		}
		return result; */
		
		//방법2. 쿼리에 foreach 써서 넣어보기
		return mapper.insertAttm(list);
		
	}


	public int deleteAttm(ArrayList<String> delRename) {
		return mapper.deleteAttm(delRename);
	}


	public void updateAttmLevel(int boardId) {
		mapper.updateAttmLevel(boardId);
		
	}




	

	

}
