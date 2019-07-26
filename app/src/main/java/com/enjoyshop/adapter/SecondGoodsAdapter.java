package com.enjoyshop.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.utils.GlideUtils;
import com.wang.bean.Goods;

import java.util.List;

/**
 * Dscribe: 分类 二级菜单 适配器
 */

public class SecondGoodsAdapter extends BaseQuickAdapter<Goods, BaseViewHolder> {

    public SecondGoodsAdapter(List<Goods> datas) {
        super(R.layout.template_category_wares, datas);
    }

    @Override
    protected void convert(BaseViewHolder holder, Goods bean) {
        holder.setText(R.id.text_title, bean.getGoods_name())
                .setText(R.id.text_price, "￥" + bean.getGoodsDetail().get(0).getGoodsdetail_price());
        GlideUtils.load(EnjoyshopApplication.sContext, bean.getGoodsDetail().get(0).getImagess().get(0).getImage_path(), (ImageView) holder
                .getView(R.id.iv_view));
    }
}
