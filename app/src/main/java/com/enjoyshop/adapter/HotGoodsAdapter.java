package com.enjoyshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.utils.GlideUtils;
import com.wang.bean.Goods;

import java.util.List;

/**
 * Describe: 热卖商品的适配器
 */

public class HotGoodsAdapter extends RecyclerView.Adapter<HotGoodsAdapter.ViewHolder> implements
        View.OnClickListener {

    private List<Goods> mDatas;
    private LayoutInflater          mInflater;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener = null;

    public HotGoodsAdapter(List<Goods> datas, Context context) {
        this.mDatas = datas;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mInflater = LayoutInflater.from(parent.getContext());
        View view = mInflater.inflate(R.layout.template_hot_wares, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HotGoodsAdapter.ViewHolder holder, int position) {
        final Goods data = getData(position);
        GlideUtils.load(EnjoyshopApplication.sContext, data.getGoodsDetail().get(0).getImagess().get(0).getImage_path(), holder.ivView);
        holder.textTitle.setText(data.getGoods_name());
        holder.textPrice.setText("￥" + data.getGoodsDetail().get(0).getGoodsdetail_price());
        holder.textSale.setText("销量："+data.getGoods_sale());
        holder.itemView.setTag(position);
    }


    private Goods getData(int position) {
        return mDatas.get(position);
    }

    public List<Goods> getDatas() {
        return mDatas;
    }

    public void clearData() {
        mDatas.clear();
        notifyItemRangeRemoved(0, mDatas.size());
    }

    public void addData(List<Goods> datas) {
        addData(0, datas);
    }

    public void addData(int position, List<Goods> datas) {
        if (datas != null && datas.size() > 0) {
            mDatas.addAll(datas);
            notifyItemRangeChanged(position, mDatas.size());
        }

    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());

        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivView;
        TextView  textTitle;
        TextView  textPrice;
        TextView  textSale;

        public ViewHolder(View itemView) {
            super(itemView);

            ivView = (ImageView) itemView.findViewById(R.id.iv_view);
            textTitle = (TextView) itemView.findViewById(R.id.text_title);
            textPrice = (TextView) itemView.findViewById(R.id.text_price);
            textSale = (TextView) itemView.findViewById(R.id.text_sale);
        }
    }


    /**
     * item的点击事件
     */
    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * 暴露给外面,以便于调用
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


}
