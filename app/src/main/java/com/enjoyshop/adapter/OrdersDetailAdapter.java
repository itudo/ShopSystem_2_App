package com.enjoyshop.adapter;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.utils.GlideUtils;
import com.wang.bean.Attribute;
import com.wang.bean.OrderDetail;

import java.util.List;

/**
 * Describe: 订单 适配器
 */

public class OrdersDetailAdapter extends BaseQuickAdapter<OrderDetail, BaseViewHolder> {


    public OrdersDetailAdapter(List<OrderDetail> datas) {
        super(R.layout.template_details, datas);
    }

    @Override
    protected void convert(BaseViewHolder holder, OrderDetail item) {
        String attr = "";
        for(Attribute a:item.getGoodsDetails().getAttributes()) {
            attr += a.getAttribute_name()+" ";
        }
        Button button = holder.getView(R.id.to_review);
        if(item.getOrderdetail_status().equals("1")) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.INVISIBLE);
            button.setEnabled(false);
        }

        holder.setText(R.id.detail_name, item.getGoods_name())
                .setText(R.id.detail_count, "数量：×"+item.getGoods_count())
                .setText(R.id.attr_name,"属性:"+attr)
                .setText(R.id.detial_price, "¥："+item.getGoods_buyprice()+"")
                .addOnClickListener(R.id.to_review);

        GlideUtils.load(EnjoyshopApplication.sContext, item.getGoodsDetails().getImagess().get(0).getImage_path(), (ImageView) holder
                .getView(R.id.iv_view));
    }
}


