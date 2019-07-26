package com.enjoyshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.enjoyshop.R;
import com.enjoyshop.activity.ReviewActivity;
import com.enjoyshop.utils.LogUtil;
import com.wang.bean.OrderDetail;
import com.wang.bean.Orders;

import java.util.List;

/**
 * Describe: 订单 适配器
 */

public class OrdersAdapter extends BaseQuickAdapter<Orders, BaseViewHolder> {
    private Context context;

    TextView txt2;
    TextView sta;
    private String order_id;


    public OrdersAdapter(Context context,List<Orders> datas) {
        super(R.layout.template_orders, datas);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder holder, Orders item) {
        RecyclerView  mRecyclerview = holder.getView(R.id.recycler_view_detail);
        txt2 = holder.getView(R.id.order_pay);
        sta = holder.getView(R.id.order_status);
        order_id = item.getOrder_id();
        LogUtil.e(item.getOrder_id()+"号订单信息",item.getOrderDetails().toString(),true);
        OrdersDetailAdapter   mAdapter = new OrdersDetailAdapter(item.getOrderDetails());
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this.context));
        mRecyclerview.addItemDecoration(new DividerItemDecoration(this.context, DividerItemDecoration
                .HORIZONTAL));

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                OrderDetail orders = (OrderDetail)adapter.getData().get(position);
                orders.setOrder_id(order_id);
                switch (view.getId()) {
                    case R.id.to_review:
                        jumpToReview(orders);
                        break;
                    default:
                        break;
                }
            }
        });

        String status = "";
        String button2 = "";
        switch (item.getOrder_status()) {
            case 0:
                status = "等待买家付款";
                button2 = "立即支付";
                txt2.setBackgroundColor(context.getResources().getColor(R.color.red_btn_color_normal));
                sta.setBackgroundColor(context.getResources().getColor(R.color.red_btn_color_normal));
                break;
            case 1:
                status = "待发货";
                button2 = "催促发货";
                txt2.setBackgroundColor(context.getResources().getColor(R.color.mediumturquoise));
                sta.setBackgroundColor(context.getResources().getColor(R.color.mediumturquoise));
                break;
            case 2:
                status = "待收货";
                button2 = "查看物流";
                txt2.setBackgroundColor(context.getResources().getColor(R.color.olive));
                sta.setBackgroundColor(context.getResources().getColor(R.color.olive));
                break;
            case 3:
                status = "待评价";
                button2 = "立即评价";
                txt2.setBackgroundColor(context.getResources().getColor(R.color.limegreen));
                sta.setBackgroundColor(context.getResources().getColor(R.color.limegreen));
                break;
            case 4:
                status = "交易成功";
                button2 = "删除订单";
                sta.setBackgroundColor(context.getResources().getColor(R.color.base_title_bg_color));
                txt2.setBackgroundColor(context.getResources().getColor(R.color.base_title_bg_color));
                break;

        }
        holder.setText(R.id.order_time, item.getOrder_time())
                .setText(R.id.order_id, item.getOrder_id())
                .setText(R.id.order_status, status)
                .setText(R.id.order_total, "共"+item.getOrderDetails().size()+"件商品   合计：¥" + item.getOrder_totalmoney() + "元")
                .setText(R.id.order_pay,button2)
                .addOnClickListener(R.id.order_detail)
                .addOnClickListener(R.id.order_pay);
    }

    private void jumpToReview(OrderDetail orderDetail) {
        LogUtil.e("评价商品id",orderDetail.getGoodsDetails().getGoodsdetail_id()+"",true);
        Intent intent = new Intent(context,ReviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("orderDetail",orderDetail);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}


