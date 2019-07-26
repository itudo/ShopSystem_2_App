package com.enjoyshop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.fragment.ShopCartFragment;
import com.enjoyshop.utils.GlideUtils;
import com.enjoyshop.utils.LogUtil;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.NumberAddSubView;
import com.wang.bean.Attribute;
import com.wang.bean.CartDetail;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.android.volley.VolleyLog.TAG;
import static com.enjoyshop.contants.HttpContants.UPDATE_CART;

/**
 * Describe: 购物车的适配器
 */

public class ShopCartAdapter extends RecyclerView.Adapter<ShopCartAdapter.ViewHolder> {

    private Context            mContext;
    private List<CartDetail> mDatas;
    private CheckItemListener  mCheckListener;
    private Map<String,Object> map = new HashMap<>();

    public ShopCartAdapter(Context mContext, List<CartDetail> mDatas, CheckItemListener
            mCheckListener) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.mCheckListener = mCheckListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.template_cart, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CartDetail cart = mDatas.get(position);
        String attr = "";
        for(Attribute a:cart.getGoodsDetails().getAttributes()) {
            attr += a.getAttribute_name()+" ";
        }

        holder.mTvTitle.setText(cart.getGoodsDetails().getGoodss().getGoods_name()+"("+attr+")");
        holder.mTvPrice.setText("￥" + (cart.getGoodsDetails().getGoodsdetail_price()*cart.getGoods_count()));
        holder.mCheckBox.setChecked(cart.isChecked());
        holder.mNumberAddSubView.setValue(cart.getGoods_count());
        GlideUtils.load(EnjoyshopApplication.sContext, cart.getGoodsDetails().getImagess().get(0).getImage_path(), holder.mIvLogo);


        //点击实现选择功能，当然可以把点击事件放在item_cb对应的CheckBox上，只是焦点范围较小
        holder.mLlContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cart.setChecked(!cart.isChecked());
                holder.mCheckBox.setChecked(cart.isChecked());
                if (null != mCheckListener) {
                    mCheckListener.itemChecked(cart, holder.mCheckBox.isChecked());
                }
                notifyDataSetChanged();
                ((ShopCartFragment)mCheckListener).showTotalPrice();
            }
        });


        holder.mNumberAddSubView.setOnButtonClickListener(new NumberAddSubView.OnButtonClickListener() {
            @Override
            public void onButtonAddClick(View view, int value) {
                cart.setGoods_count(value);
                updateCount(cart,map);
//                mCartShopProvider.updata(cart);
                ((ShopCartFragment)mCheckListener).showTotalPrice();
            }

            @Override
            public void onButtonSubClick(View view, int value) {
                cart.setGoods_count(value);
                updateCount(cart,map);
//                mCartShopProvider.updata(cart);
                ((ShopCartFragment)mCheckListener).showTotalPrice();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout     mLlContent;
        private TextView         mTvTitle;
        private TextView         mTvPrice;
        private CheckBox         mCheckBox;
        private ImageView        mIvLogo;
        private NumberAddSubView mNumberAddSubView;

        public ViewHolder(View itemView) {
            super(itemView);
            mLlContent = itemView.findViewById(R.id.ll_item);
            mCheckBox = itemView.findViewById(R.id.checkbox);
            mIvLogo = itemView.findViewById(R.id.iv_view);
            mTvTitle = itemView.findViewById(R.id.text_title);
            mTvPrice = itemView.findViewById(R.id.text_price);
            mNumberAddSubView = itemView.findViewById(R.id.num_control);
        }
    }

    public interface CheckItemListener {

        void itemChecked(CartDetail checkBean, boolean isChecked);
    }

    private void updateCount(CartDetail cart,Map map) {
        map.put("cartdetail_id",cart.getCartdetail_id()+"");
        map.put("goods_count",cart.getGoods_count()+"");

        OkHttpUtils.post().url(UPDATE_CART).params(map).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                android.util.Log.d(TAG, "error = " + e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if (response != null&&Integer.parseInt(jb.get("code").toString())==0) {
                        ToastUtils.showSafeToast(mContext, "服装数量修改失败！");
                    } else {
                        LogUtil.e("服装数量修改成功","",true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
