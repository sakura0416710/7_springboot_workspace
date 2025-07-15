package kh.springboot.board.model.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import kh.springboot.board.model.vo.Attachment;
import kh.springboot.board.model.vo.Board;
import kh.springboot.board.model.vo.PageInfo;

@Mapper
public interface BoardMapper {
	//1.게시글 전체 조회
	int getListCount(int i);

	//내가 보낼 데이터와 그걸 처리할 객체 순서대로!! 순서 중요.
	ArrayList<Board> selectBoardList(int i, RowBounds rowBounds);

	//3.게시글 작성
	int insertBoard(Board b);

	//4.게시글 상세조회 ( 조회수 증가)
	Board selectBoard(int bId);
	int updateCount(int bId);

	
	//5.게시글 수정
	int updateBoard(Board b);

	//6. 게시글 삭제
	int deleteBoard(int bId);
	
	
	//1.첨부파일 게시글전체조회하기
	ArrayList<Attachment> selectAttmBoardList();

	//2.첨부파일 게시판(첨부파일 존재하는 게시글) 등록하기
	int insertAttm(ArrayList<Attachment> list);

}
