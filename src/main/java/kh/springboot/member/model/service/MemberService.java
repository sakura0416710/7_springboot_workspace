package kh.springboot.member.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import kh.springboot.member.model.mapper.MemberMapper;
import kh.springboot.member.model.vo.Member;
import kh.springboot.member.model.vo.TodoList;
import lombok.RequiredArgsConstructor;

@Service //service역할을 하는 빈을 생성하게 된 것.결합도도 낮추고 프레임워크의 IoC특성도 지켜줌.
@RequiredArgsConstructor
public class MemberService {
	private final MemberMapper mapper;
	//1.로그인 
	public Member login(Member m) {
		
		return mapper.login(m);
		
	}
	//2. 회원가입
	public int insertMember(Member m) {
		return mapper.insertMember(m);
	}
	
	//3.게시판에 글이랑 댓글 띄우기
	public ArrayList<HashMap<String, Object>> selectMyList(String id) {
		return mapper.selectMyList(id);
	}
	
	//4.회원정보 수정
	public int updateMember(Member m) {
		return mapper.updateMember(m);
	}
	
	//5.비밀번호 수정
	public int updatePassword(Member m) {
		return mapper.updatePassword(m);
	}
	//6. 회원탈퇴 
	public int deleteMember(String id) {
		return mapper.deleteMember(id);
	}
	
	//7.아이디 중복확인
	public int checkValue(HashMap<String, String> map) {
		return mapper.checkValue(map);
	}
	/*8.아이디 찾기
	public String findId(Member m) {
		return mapper.findId(m);
	}
	//9. 비번 찾기
	public Member findPw(Member m) {
		return mapper.findPw(m);
	}		*/
	
	//8+9번
	public Member findInfo(Member m) {
		return mapper.findInfo(m);
	}
	
	//todoList가져오기
	public ArrayList<TodoList> selectTodoList(String id) {
		return mapper.selectTodoList(id);
	}
	
	//todoList 추가하기
	public int insertTodoList(TodoList todoList) {
		return mapper.insertTodoList(todoList);
	}
	
	//todoList 삭제하기
	public int deleteTodoList(int num) {
		return mapper.deleteTodoList(num);
	}
	//todoList수정
	public int updateTodoList(TodoList todoList) {
		return mapper.updateTodoList(todoList);
	}
	
	//프로필 사진 등록
	public int updateProfile(Member m) {
		return mapper.updateProfile(m);
	}
	
	//멤버 리스트 불러오기
	public ArrayList<Member> selectMembers(String id) {
		return mapper.selectMembers(id);
	}
	
	//멤버 정보 수정
	public int updateMemberItem(HashMap<String, String> map) {
		return mapper.updateMemberItem(map);
	}
}
