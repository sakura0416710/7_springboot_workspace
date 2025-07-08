package kh.springboot.member.model.mapper;

import kh.springboot.member.model.vo.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper //인터페이스 구현을 xml로 하겠다는 의미
public interface MemberMapper {

	Member login(Member m);

}
