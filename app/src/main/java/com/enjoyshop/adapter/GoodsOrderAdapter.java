package com.enjoyshop.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.utils.GlideUtils;
import com.wang.bean.CartDetail;

import java.util.List;

/**
 * <pre>
 *     desc   : 商品订单适配器
 *     version: 1.0
 * </pre>
 */


public class GoodsOrderAdapter extends BaseQuickAdapter<CartDetail, BaseViewHolder> {

    private List<CartDetail> mDatas;

    public GoodsOrderAdapter(List<CartDetail> datas) {
        super(R.layout.template_orders, datas);
        this.mDatas = datas;
    }

    @Override
    protected void convert(BaseViewHolder holder, CartDetail item) {
        GlideUtils.load(EnjoyshopApplication.sContext, null, (ImageView) holder
                .getView(R.id.iv_view));
    }


    public float getTotalPrice() {

        float sum = 0;
        if (!isNull())
            return sum;

        for (CartDetail cartDetail : mDatas) {
            sum += cartDetail.getGoodsDetails().getGoods_count() * cartDetail.getGoodsDetails().getGoodsdetail_price();
        }

        return sum;

    }


    private boolean isNull() {
        return (mDatas != null && mDatas.size() > 0);
    }
}
