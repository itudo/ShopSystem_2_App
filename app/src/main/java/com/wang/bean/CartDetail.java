package com.wang.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class CartDetail implements Serializable{
	private static final long serialVersionUID = -1014373879619533921L;
	private Integer cartdetail_id;
	private double goods_money;
	private Integer goods_count;
	private boolean isChecked = false;
	private Integer cartdetail_status;
    private GoodsDetail goodsDetails;
    private String goods_name;
    private String goodsDetail;
    private Cart cart;

    private String order_id = "";
}
