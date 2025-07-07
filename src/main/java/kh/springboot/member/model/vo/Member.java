package kh.springboot.member.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//기본생성자
@NoArgsConstructor

//매개변수 있는 생성자
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Member {
	private String id;
	private String pwd;
	private String name;
	private String nickName;
	private String email;
	private String gender;
	private int age;
	private String phone;
	private String address;
	private Date enrollDate;
	private Date updateDate;
	private String memberStatus;
	private String isAdmin;
}

//lombok이용
