package com.enjoyshop.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.enjoyshop.R;

import java.util.List;

/**
 * <pre>
 *   desc   : 历史搜索的适配器
 * </pre>
 */

public class HistorySearchAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public HistorySearchAdapter(List<String> datas) {
        super(R.layout.item_search, datas);
    }

    @Override
    protected void convert(BaseViewHolder holder, String item) {
        holder.setText(R.id.tv_content, item);
    }
}
