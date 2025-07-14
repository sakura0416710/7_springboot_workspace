package kh.springboot.common;

import kh.springboot.board.model.vo.PageInfo;

public class Pagination {
	public static PageInfo getPageInfo
	(int currentPage, int listCount, int boardLimit) {
		int pageLimit = 10; //한 페이지에 보일 게시글 개수
		int maxPage = (int)Math.ceil((double)listCount / boardLimit); //전체 페이지 중 가장 마지막 페이지
		//소수점으로 나눈 뒤 나머지까지 다 챙겨서 올림 해주기. ceil의 타입도 double이므로 int로 강제형변환 해주면 끝.
		int startPage = (currentPage - 1) /pageLimit * pageLimit + 1; // 현재 페이지 기준 버튼 중 시작버튼
		int endPage = startPage + pageLimit -1; //현재 페이지 기준 버튼 중 마지막 버튼점
		if(maxPage < endPage) {
			endPage = maxPage;
		}
		
		return new PageInfo(currentPage, listCount, pageLimit, maxPage, startPage, endPage, boardLimit);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
