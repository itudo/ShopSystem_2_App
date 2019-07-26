package com.enjoyshop.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.utils.GlideUtils;
import com.wang.bean.Review;

import java.util.List;

/**
 * Dscribe: 评论 适配器
 */

public class ReviewAdapter extends BaseQuickAdapter<Review, BaseViewHolder> {

    public ReviewAdapter(List<Review> datas) {
        super(R.layout.template_review_wares, datas);
    }

    @Override
    protected void convert(BaseViewHolder holder, Review bean) {
        float k = bean.getReview_level();
        int j = (int) k;
        String[] str = new String[]{"☆","☆","☆","☆","☆"};
        for(int i=0;i<j;i++) {
            str[i] = "★";
        }
        holder.setText(R.id.review_goods, bean.getReview_goods())
                .setText(R.id.review_user, bean.getReview_user()+"\r\r\r\r"+bean.getReview_date())
                .setText(R.id.review_level,str[0]+str[1]+str[2]+str[3]+str[4])
                .setText(R.id.review_context,bean.getReview_content());
        GlideUtils.review(EnjoyshopApplication.sContext,bean.getReview_image() , (ImageView) holder
                .getView(R.id.review_img));
    }
}
