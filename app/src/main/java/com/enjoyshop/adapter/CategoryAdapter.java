package com.enjoyshop.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.enjoyshop.R;
import com.wang.bean.ThirdType;

import java.util.List;

/**
 * <pre>
 *     desc   : 分类一级菜单.
 *     version: 1.0
 * </pre>
 */


public class CategoryAdapter extends BaseQuickAdapter<ThirdType, BaseViewHolder> {

    public CategoryAdapter(List<ThirdType> datas) {
        super(R.layout.template_single_text, datas);
    }

    @Override
    protected void convert(BaseViewHolder holder, ThirdType item) {
        holder.setText(R.id.textView, item.getThirdtype_name());
    }
}
