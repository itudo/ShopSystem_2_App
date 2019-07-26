package com.enjoyshop.fragment;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.activity.CreateOrderActivity;
import com.enjoyshop.adapter.ShopCartAdapter;
import com.enjoyshop.bean.MessageEvent;
import com.enjoyshop.helper.JsonToObject;
import com.enjoyshop.utils.LogUtil;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.EnjoyshopToolBar;
import com.enjoyshop.widget.WrapContentLinearLayoutManager;
import com.wang.bean.Attribute;
import com.wang.bean.CartDetail;
import com.wang.bean.Goods;
import com.wang.bean.GoodsDetail;
import com.wang.bean.Image;
import com.wang.bean.Users;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import lombok.extern.java.Log;
import okhttp3.Call;

import static com.enjoyshop.contants.HttpContants.CART_LIST;
import static com.enjoyshop.contants.HttpContants.DELETE_CART;

/**
 * <pre>
 *     desc   : 购物车fragment
 *     version: 1.0
 * </pre>
 */

@Log
public class ShopCartFragment extends BaseFragment implements ShopCartAdapter.CheckItemListener {

    public static final  int    ACTION_EDIT     = 1;
    public static final  int    ACTION_CAMPLATE = 2;
    private static final String TAG             = "CartFragment";

    @BindView(R.id.recycler_view)
    RecyclerView     mRecyclerView;
    @BindView(R.id.checkbox_all)
    CheckBox         mCheckBox;
    @BindView(R.id.txt_total)
    TextView         mTextTotal;
    @BindView(R.id.btn_order)
    Button           mBtnOrder;
    @BindView(R.id.btn_del)
    Button           mBtnDel;
    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolbar;
    @BindView(R.id.rv_bottom)
    RelativeLayout   mRvBottom;
    @BindView(R.id.ll_empty)
    LinearLayout     mLlEmpty;

    private ShopCartAdapter    mAdapter;
    private boolean            isSelectAll;
    //列表数据
    private List<CartDetail> dataArray;
    //选中后的数据
    private List<CartDetail> checkedList;
    private Map<String,Object> map = new HashMap<>();
    private Map<String,Object> map2 = new HashMap<>();
    private Boolean flag = true;

    private double total = 0;
    private Users user;

    @Override
    protected int getContentResourseId() {
        return R.layout.fragment_shopcart;
    }

    @Override
    protected void init() {
        checkedList = new ArrayList<>();
        changeToolbar();
        user = EnjoyshopApplication.getInstance().getUser();
        if(user==null) {
            ToastUtils.showSafeToast(getContext(),"请您先登录！");
            showCart();
        }else {
            map.put("user_id",user.getUser_id()+"");
            initData(map);
            //showTotalPrice();
        }

    }


    /**
     * 改变标题栏
     */
    private void changeToolbar() {
        mToolbar.hideSearchView();
        mToolbar.showTitleView();
        mToolbar.setTitle(R.string.cart);
    }


    /**
     * 获取数据
     */

    private void initData(Map map) {
        OkHttpUtils.post().url(CART_LIST).params(map).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                android.util.Log.d(TAG, "error = " + e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if (response != null&&Integer.parseInt(jb.get("code").toString())==1) {
                        JSONArray j = (JSONArray) jb.get("obj");
                        dataArray = initCart(j);
                        showCart();
                        showTotalPrice();
                    } else {
                        ToastUtils.showSafeToast(getContext(), "暂无评论！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showCart() {
        if (dataArray == null) {
            initEmptyView();           //如果数据为空,显示空的试图
            return;
        }
        /**
         * 购物车数据不为空
         */
        mAdapter = new ShopCartAdapter(getContext(), dataArray, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
    }

    private List<CartDetail> initCart(JSONArray j) throws Exception {
        List<CartDetail> list = JsonToObject.jsonToList(j,CartDetail.class);
        for(CartDetail c:list) {
            JSONObject jsons = new JSONObject(c.getGoodsDetail());
            GoodsDetail g = (GoodsDetail) JsonToObject.jsonToPOJO(jsons,GoodsDetail.class);
            JSONArray attr = new JSONArray(g.getAttribute());
            JSONArray img = new JSONArray(g.getImages());
            JSONObject json = new JSONObject(g.getGoods());
            Goods goodsBean = new Goods();
            goodsBean.setGoods_id(Integer.parseInt(json.get("goods_id").toString()));
            goodsBean.setGoods_name(json.get("goods_name").toString());
            goodsBean.setGoods_sale(Integer.parseInt(json.get("goods_sale").toString()));
            List<Attribute> attribute = JsonToObject.jsonToList(attr,Attribute.class);
            List<Image> image = JsonToObject.jsonToList(img,Image.class);
            g.setAttributes(attribute);
            g.setImagess(image);
            g.setGoodss(goodsBean);
            c.setGoodsDetails(g);
        }
        return list;
    }




    private void initEmptyView() {
        mRvBottom.setVisibility(View.GONE);
        mLlEmpty.setVisibility(View.VISIBLE);
    }


    @OnClick({R.id.btn_del, R.id.btn_order, R.id.tv_goshop, R.id.checkbox_all})
    public void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_del:
                delCart(map2);
                break;
            case R.id.btn_order:
                jumpToOrder();
                break;
            case R.id.tv_goshop:      //如果没有商品时
                mLlEmpty.setVisibility(View.GONE);

                EventBus.getDefault().post(new MessageEvent(0));

                break;
            case R.id.checkbox_all:
                isSelectAll = !isSelectAll;
                checkedList.clear();
                if (isSelectAll) {//全选处理
                    mCheckBox.setChecked(true);
                    checkedList.addAll(dataArray);
                }else {
                    mCheckBox.setChecked(false);
                }
                for (CartDetail checkBean : dataArray) {
                    checkBean.setChecked(isSelectAll);
                }
                mAdapter.notifyDataSetChanged();
                showTotalPrice();
                break;
        }
    }

    private void jumpToOrder() {
        if(total==0) {
            ToastUtils.showSafeToast(getContext(), "请选择至少一件服装！");
            return;
        }
        log.info("订单信息为："+checkedList);
        Intent intent = new Intent(getContext(), CreateOrderActivity.class);
        intent.putExtra("totalPrice",total);
        intent.putExtra("status",98);
        intent.putExtra("orderList", (Serializable) checkedList);
        startActivity(intent, true);
    }

    private void delCart(Map map2) {
        for (CartDetail cart : dataArray) {
            if (cart.isChecked()) {   //是否勾上去了
                map2.put("cartdetail_id",cart.getCartdetail_id()+"");
                OkHttpUtils.post().url(DELETE_CART).params(map2).build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        android.util.Log.d(TAG, "error = " + e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jb = new JSONObject(response);
                            if (response != null&&Integer.parseInt(jb.get("code").toString())==1) {
                                ToastUtils.showSafeToast(getContext(), "删除成功！");
                                flag = false;
                                initData(map);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }


    @Override
    public void itemChecked(CartDetail checkBean, boolean isChecked) {
        //处理Item点击选中回调事件
        if (isChecked) {
            //选中处理
            if (!checkedList.contains(checkBean)) {
                checkedList.add(checkBean);
            }
        } else {
            //未选中处理
            if (checkedList.contains(checkBean)) {
                checkedList.remove(checkBean);
            }
        }
        //判断列表数据是否全部选中
        if (checkedList.size() == dataArray.size()) {
            mCheckBox.setChecked(true);
        } else {
            mCheckBox.setChecked(false);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            refData();
        }
    }

    /**
     * 刷新数据
     * <p>
     * fragment是隐藏与显示.生命周期很多没走.
     * 先将数据全部清除,再重新添加(有可能和以前一样,有可能有新数据)
     * 清除的目的,就是为了防止添加了新数据而界面上没展示
     */
    private void refData() {
        user = EnjoyshopApplication.getInstance().getUser();
        if(user==null) {
            ToastUtils.showSafeToast(getContext(),"请您先登录！");
            initEmptyView();
            return;
        }else {
            map.put("user_id",user.getUser_id()+"");
            initData(map);
            if (dataArray != null && dataArray.size() > 0) {
                mLlEmpty.setVisibility(View.GONE);
                mRvBottom.setVisibility(View.VISIBLE);
                showCart();
                showTotalPrice();
            } else {
                initEmptyView();
            }
        }


    }

    public void showTotalPrice() {
        total = getTotalPrice();
        mTextTotal.setText(Html.fromHtml("合计:￥<span style='color:#eb4f38'>" + total + "</span>"),
                TextView.BufferType.SPANNABLE);
    }

    /**
     * 计算总和
     */

    public boolean isNull() {
        return (dataArray != null && dataArray.size() > 0);
    }

    private double getTotalPrice() {

        float sum = 0;
        if (!isNull())
            return sum;

        for (CartDetail cart : dataArray) {
            if (cart.isChecked()) {   //是否勾上去了
                sum += cart.getGoods_count() * cart.getGoodsDetails().getGoodsdetail_price();
            }
        }
        return sum;
    }

}



