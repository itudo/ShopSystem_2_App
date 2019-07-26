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
import com.wang.bean.CartDetail;

import java.util.List;

/**
 * Describe: 订单 适配器
 */

public class OrdersDetailsAdapter extends BaseQuickAdapter<CartDetail, BaseViewHolder> {


    public OrdersDetailsAdapter(List<CartDetail> datas) {
        super(R.layout.template_details, datas);
    }
    @Override
    protected void convert(BaseViewHolder holder, CartDetail item) {
        String attr = "";
        for(Attribute a:item.getGoodsDetails().getAttributes()) {
            attr += a.getAttribute_name()+" ";
        }

        Button button = holder.getView(R.id.to_review);
        button.setVisibility(View.INVISIBLE);
        button.setEnabled(false);

        holder.setText(R.id.detail_name, item.getGoodsDetails().getGoodss().getGoods_name()+"("+attr+")")
                .setText(R.id.detail_count, "数量：×"+item.getGoods_count())
                .setText(R.id.detial_price, "¥："+item.getGoods_money()+"");
        GlideUtils.load(EnjoyshopApplication.sContext, item.getGoodsDetails().getImagess().get(0).getImage_path(), (ImageView) holder
                .getView(R.id.iv_view));
    }
}


