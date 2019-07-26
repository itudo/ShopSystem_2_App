package com.enjoyshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.activity.AddressListActivity;
import com.enjoyshop.activity.CreateOrderActivity;
import com.enjoyshop.helper.JsonToObject;
import com.enjoyshop.utils.GlideUtils;
import com.enjoyshop.utils.LogUtil;
import com.enjoyshop.utils.ToastUtils;
import com.wang.bean.Address;
import com.wang.bean.Attribute;
import com.wang.bean.CartDetail;
import com.wang.bean.Goods;
import com.wang.bean.GoodsDetail;
import com.wang.bean.Image;
import com.wang.bean.Users;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

import static com.enjoyshop.contants.Contants.CHOOSE_ADDRESS_GOODS;
import static com.enjoyshop.contants.HttpContants.ADD_CART;
import static com.enjoyshop.contants.HttpContants.COLLECT_GOODS;
import static com.enjoyshop.contants.HttpContants.GOODS_DETAIL;

@lombok.extern.java.Log
public class GoodsFragment extends BaseFragment {
    @BindView(R.id.txt_goods_name)
    TextView txt_goods_name;
    @BindView(R.id.txt_goods_sale)
    TextView txt_goods_sale;
    @BindView(R.id.txt_goods_price)
    TextView txt_goods_price;
    @BindView(R.id.group)
    RadioGroup group;
    @BindView(R.id.address_btn)
    Button btnAddress;
    @BindView(R.id.imgGoods)
    ImageView good_img;

    private Goods goodsBean = new Goods();
    private Address address;
    private GoodsDetail goodsDetail;

    private Integer goods_id;
    private Integer detail_id = 0;
    private Double price;
    private Map<String,Object> map = new HashMap<>();

    RadioButton rb;

    private Users user;

    private List<GoodsDetail> goodsDetails = new ArrayList<>();

    @Override
    protected void init() {
        user = EnjoyshopApplication.getInstance().getUser();
        Bundle bundle = getArguments();
        goods_id = bundle.getInt("goods_id");
        map.put("goods_id",goods_id+"");

        initData(map);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rb = group.findViewById(group.getCheckedRadioButtonId());
                LogUtil.e("选择属性",rb.getText().toString(),true);
                goodsDetail = (GoodsDetail) rb.getTag();
                showGoodsDetails(goodsDetail);
            }
        });

    }

    private void initData(Map map) {
        OkHttpUtils.post().url(GOODS_DETAIL).params(map).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d(TAG, "error = " + e);
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if (response != null&&Integer.parseInt(jb.get("code").toString())==1) {
                        JSONArray j = (JSONArray) jb.get("obj");
                        goodsDetails = getGoodsDetail(j);

                        goodsDetail = goodsDetails.get(0);
                        showGoodsDetails(goodsDetail);
                        log.info("当前属性为："+detail_id);
                        initRadioGroup(goodsDetails);
                    } else {
                        ToastUtils.showSafeToast(getContext(), "暂无商品！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initRadioGroup(List<GoodsDetail> goodsDetails) {
        for(GoodsDetail g : goodsDetails) {
            RadioButton radioButton = new RadioButton(getContext());
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
            radioButton.setLayoutParams(layoutParams);
            radioButton.setId(g.getGoodsdetail_id());
            radioButton.setText(g.getAttr_name()+"\r\r\r\r\r库存"+g.getGoods_count());
            radioButton.setTextSize(25);
            radioButton.setTag(g);
            radioButton.setTextColor(getResources().getColorStateList(R.color.base_blue_selector));//设置选中/未选中的文字颜色
            radioButton.setBackground(getResources().getDrawable(R.drawable.bg_btn_style_white));//设置按钮选中/未选中的背景
            group.addView(radioButton);
        }
        group.check(goodsDetails.get(0).getGoodsdetail_id());
    }

    private void showGoodsDetails(GoodsDetail g) {
        txt_goods_price.setText("  ¥:"+g.getGoodsdetail_price()+"");
        detail_id = g.getGoodsdetail_id();
        price = g.getGoodsdetail_price();
        GlideUtils.load(EnjoyshopApplication.sContext, g.getImagess().get(0).getImage_path(),good_img );
    }

    @OnClick({R.id.address_btn,R.id.goods_collect,R.id.goods_cart,R.id.now_buy})
    public void viewclick(View view) {
        switch (view.getId()) {
            case R.id.address_btn:
                toChooseAddress();
                break;
            case R.id.goods_collect:
                collectGoods();
                break;
            case R.id.goods_cart:
                addCart(map);
                break;
            case R.id.now_buy:
                nowbuy();
                break;
        }
    }

    private void collectGoods() {
        if(user==null) {
            ToastUtils.showSafeToast(getContext(), "您尚未登录");
            return;
        }
        OkHttpUtils.get().url(COLLECT_GOODS)
                .addParams("user_name",user.getUser_name())
                .addParams("goods_id",goods_id+"")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG,e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                ToastUtils.showSafeToast(getContext(), "收藏成功！");
            }
        });
    }

    private void nowbuy() {
        if(address==null) {
            ToastUtils.showSafeToast(getContext(), "请选择收货地址！");
            return;
        }
        Intent intent = new Intent(getContext(), CreateOrderActivity.class);
        intent.putExtra("totalPrice",price);
        intent.putExtra("status",99);
        List<CartDetail> cartDetails = new ArrayList<>();
        CartDetail cartDetail = new CartDetail();
        cartDetail.setGoodsDetails(goodsDetail);
        cartDetail.setGoods_count(1);
        cartDetail.setGoods_money(price);
        cartDetails.add(cartDetail);
        intent.putExtra("orderList", (Serializable) cartDetails);
        intent.putExtra("address", address);
        startActivity(intent, true);
    }

    private void addCart(Map map) {
        if(user==null) {
            ToastUtils.showSafeToast(getContext(), "您尚未登录");
            return;
        }
        map.put("user_id",user.getUser_id()+"");
        map.put("goodsdetail_id",detail_id+"");
        map.put("goodsdetail_price",price+"");
        map.put("goods_count",1+"");
        OkHttpUtils.post().url(ADD_CART).params(map).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d(TAG, "error = " + e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if (response != null&&Integer.parseInt(jb.get("code").toString())==1) {
                        ToastUtils.showSafeToast(getContext(), "添加购物车成功！");
                    } else {
                        ToastUtils.showSafeToast(getContext(), "添加购物车失败！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void toChooseAddress() {
        if(user==null) {
            ToastUtils.showSafeToast(getContext(), "您尚未登录");
            return;
        }
        Intent intent = new Intent(getActivity(), AddressListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isChoose",true);
        bundle.putInt("ischoose",CHOOSE_ADDRESS_GOODS);
        intent.putExtras(bundle);
        startActivityForResult(intent,CHOOSE_ADDRESS_GOODS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==CHOOSE_ADDRESS_GOODS){
            LogUtil.e(TAG,"回调到商品页面",true);
            address  = (Address) data.getSerializableExtra("address");
            if(address != null) {
                LogUtil.e("送货至",address.getAddress_name(),true);
                btnAddress.setText("送货至："+address.getAddress_user()+"  "+address.getAddress_name());
            }
        }
    }

    private List<GoodsDetail> getGoodsDetail(JSONArray j) throws Exception {
        List<GoodsDetail> goodsDetails = JsonToObject.jsonToList(j,GoodsDetail.class);
        for(GoodsDetail g:goodsDetails) {
            JSONArray attr = new JSONArray(g.getAttribute());
            JSONArray img = new JSONArray(g.getImages());
            JSONObject json = new JSONObject(g.getGoods());
            goodsBean.setGoods_id(goods_id);
            goodsBean.setGoods_name(json.get("goods_name").toString());
            goodsBean.setGoods_sale(Integer.parseInt(json.get("goods_sale").toString()));
            List<Attribute> attribute = JsonToObject.jsonToList(attr,Attribute.class);
            List<Image> image = JsonToObject.jsonToList(img,Image.class);
            g.setAttributes(attribute);
            g.setImagess(image);
            g.setGoodss(goodsBean);
        }
        txt_goods_name.setText(goodsBean.getGoods_name());
        txt_goods_sale.setText("销量："+goodsBean.getGoods_sale());
        log.info("服装信息为："+goodsDetails);
        return goodsDetails;
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.fragment_goods;
    }

}
