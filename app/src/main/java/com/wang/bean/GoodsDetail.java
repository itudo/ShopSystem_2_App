package com.wang.bean;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class GoodsDetail implements Serializable {
	private static final long serialVersionUID = -2225704600669923145L;
	private Integer goodsdetail_id;
	private double goodsdetail_price;
	private Integer goods_count;
	private Goods goodss;
	private String goods;
	private List<Image> imagess;
	private String images;
	
	private List<Attribute> attributes;
	private String attribute;
	
	private String attr_name;
	
	private String contactStr;

	public String goods_name;

	public String type_name;
	private String goods_desc;
	private String goods_id;
	private String goods_price;
}
