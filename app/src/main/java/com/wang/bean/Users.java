package com.wang.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class Users implements Serializable{
	private static final long serialVersionUID = 7072805719866647848L;
	private Integer user_id;   
	private String user_name;
	private String user_pwd;
	private String user_sex;

	private String user_tel;
	private String user_email;
	private String user_addr;
	private String user_idcard;
	private String user_level;
	
	private String user_date;
	
	private List<Address> adress;
	
	private String code;
	
	private String name;
	
	private String user_head;

	public Users() {
	}

	public Users(String user_name, String user_pwd, String user_sex, String user_tel, String user_email, String user_idcard) {
		this.user_name = user_name;
		this.user_pwd = user_pwd;
		this.user_sex = user_sex;
		this.user_tel = user_tel;
		this.user_email = user_email;
		this.user_idcard = user_idcard;
	}

	@Override
	public String toString() {
		return "Users{" +
				"user_id=" + user_id +
				", user_name='" + user_name + '\'' +
				", user_sex='" + user_sex + '\'' +
				", user_tel='" + user_tel + '\'' +
				", user_email='" + user_email + '\'' +
				", user_idcard='" + user_idcard + '\'' +
				", user_head='" + user_head + '\'' +
				'}';
	}
}
