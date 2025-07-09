package kh.springboot.member.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import kh.springboot.member.model.mapper.MemberMapper;
import kh.springboot.member.model.vo.Member;
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
	
}
