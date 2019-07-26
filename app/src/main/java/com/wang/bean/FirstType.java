package com.wang.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
@Data
public class FirstType implements Serializable {
	private static final long serialVersionUID = 2166401252104783841L;
	private Integer firsttype_id;
	private String firsttype_name;
	private List<SecondType> secondType;
	
	private List<FirstType> firstType;
	private List<FirstType> children;

	private Integer id;
	private String text;
	private Integer secondtype_id;
	
	private Integer thirdtype_id;
}
