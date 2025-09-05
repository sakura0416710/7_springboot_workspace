package kh.springboot.member.model.mapper;

import kh.springboot.member.model.vo.Member;
import kh.springboot.member.model.vo.TodoList;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;

@Mapper //인터페이스 구현을 xml로 하겠다는 의미
public interface MemberMapper {

	Member login(Member m);

	int insertMember(Member m);

	ArrayList<HashMap<String, Object>> selectMyList(String id);

	int updateMember(Member m);

	int updatePassword(Member m);

	int deleteMember(String id);

	int checkValue(HashMap<String, String> map);

/*	String findId(Member m);

	Member findPw(Member m);		*/

	Member findInfo(Member m);

	//todoList가져오기
	ArrayList<TodoList> selectTodoList(String id);

	//todoList추가하기
	int insertTodoList(TodoList todoList);

	//todoList삭제하기
	int deleteTodoList(int num);

	int updateTodoList(TodoList todoList);

	//프사 등록
	int updateProfile(Member m);

	//멤버리스트 가져오기 - mapper.xml에 써야댐
	ArrayList<Member> selectMembers(String id);

	//멤버 수정하기
	int updateMemberItem(HashMap<String, String> map);

}
