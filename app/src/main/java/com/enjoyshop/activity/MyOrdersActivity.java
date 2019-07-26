package com.enjoyshop.activity;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.adapter.OrdersAdapter;
import com.enjoyshop.helper.JsonToObject;
import com.enjoyshop.utils.LogUtil;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.EnjoyshopToolBar;
import com.wang.bean.Address;
import com.wang.bean.Attribute;
import com.wang.bean.CartDetail;
import com.wang.bean.Goods;
import com.wang.bean.GoodsDetail;
import com.wang.bean.Image;
import com.wang.bean.OrderDetail;
import com.wang.bean.Orders;
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
import okhttp3.Call;

import static com.enjoyshop.contants.Contants.CHOOSE_ORDER;
import static com.enjoyshop.contants.HttpContants.CHANGE_ORDER;
import static com.enjoyshop.contants.HttpContants.GET_ORDERS;

/**
 * Describe: 我的订单
 */
@lombok.extern.java.Log
public class MyOrdersActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;
    @BindView(R.id.order_group)
    RadioGroup group;


    Map<String,Object> map = new HashMap<String,Object>();
    Map<String,Object> map2 = new HashMap<>();

    @BindView(R.id.recycler_view_order)
    RecyclerView mRecyclerview;

    private OrdersAdapter mAdapter;
    private List<Orders> ordersList;

    private Integer tag = 10;

    @Override
    protected void init() {
        initToolBar();
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = group.findViewById(group.getCheckedRadioButtonId());
                tag = Integer.parseInt(rb.getTag().toString());
                initAdd();
            }
        });
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initAdd();
    }

    private void initView() {
        if (mAdapter == null) {
            mAdapter = new OrdersAdapter(MyOrdersActivity.this,ordersList);
            mRecyclerview.setAdapter(mAdapter);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(MyOrdersActivity.this));
            mRecyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration
                    .HORIZONTAL));

            mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    Orders orders = (Orders)adapter.getData().get(position);
                    switch (view.getId()) {
                        case R.id.order_detail:
                            orderDetail(orders);
                            break;
                        case R.id.order_pay:
                            orderPay(orders,map);
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    private void orderPay(Orders orders,Map map) {
        switch (orders.getOrder_status()) {
            case 0:
                orderDetail(orders);
                break;
            case 1:
                cuiHuo();
                break;
            case 2:
                lookMap();
                break;
            case 3:
                review(orders);
                break;
            case 4:
                delOrder(orders,map2);
                break;
        }
    }

    private void orderDetail(Orders orders) {
        log.info("查看订单"+orders.getOrder_id());
        Intent intent = new Intent(MyOrdersActivity.this, CreateOrderActivity.class);
        intent.putExtra("totalPrice",orders.getOrder_totalmoney());
        intent.putExtra("status",orders.getOrder_status());
        List<CartDetail> cartDetails = new ArrayList<>();
        for(OrderDetail o:orders.getOrderDetails()) {
            CartDetail cartDetail = new CartDetail();
            cartDetail.setOrder_id(orders.getOrder_id());
            cartDetail.setCartdetail_id(o.getOrderdetail_id());
            cartDetail.setGoodsDetails(o.getGoodsDetails());
            cartDetail.setGoods_count(o.getGoods_count());
            cartDetail.setGoods_money(o.getGoods_buyprice());
            cartDetails.add(cartDetail);
        }
        intent.putExtra("orderList", (Serializable) cartDetails);
        Address address = new Address();
        address.setAddress_name(orders.getTo_addr());
        address.setAddress_user(orders.getTo_userName());
        address.setAddress_tel(orders.getTo_tel());
        intent.putExtra("address", address);
        startActivityForResult(intent,CHOOSE_ORDER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==CHOOSE_ORDER){
            initView();
        }
    }

    private void orderCancle(Orders orders,Map map2) {
        map2.put("order_id",orders.getOrder_id()+"");
        map2.put("status","取消");
        OkHttpUtils.post().url(CHANGE_ORDER).params(map2).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                android.util.Log.d(TAG, "error = " + e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if (response != null&&Integer.parseInt(jb.get("code").toString())==1) {
                        ToastUtils.showSafeToast(MyOrdersActivity.this,"订单已取消！");
                        initAdd();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void delOrder(Orders orders,Map map2) {
        map2.put("order_id",orders.getOrder_id()+"");
        map2.put("status","删除");
        OkHttpUtils.post().url(CHANGE_ORDER).params(map2).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                android.util.Log.d(TAG, "error = " + e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if (response != null&&Integer.parseInt(jb.get("code").toString())==1) {
                        ToastUtils.showSafeToast(MyOrdersActivity.this,"订单已删除！");
                        initAdd();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void review(Orders orders) {
    }

    private void cuiHuo() {
        ToastUtils.showSafeToast(MyOrdersActivity.this,"催货成功！");
    }
    private void lookMap() {
        ToastUtils.showSafeToast(MyOrdersActivity.this,"功能暂未开放！");
    }

    private void initOrders(Map map) {
        map.put("type",tag+"");
        LogUtil.e("查看订单状态",tag+"",true);
        map.put("user_id",EnjoyshopApplication.getInstance().getUser().getUser_id()+"");
        OkHttpUtils.post().url(GET_ORDERS).params(map).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d(TAG, "error = " + e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if (response != null&&Integer.parseInt(jb.get("code").toString())==1) {
                        JSONArray json = (JSONArray) jb.get("rows");
                        ordersList = getOrders(json);
                        mAdapter.setNewData(ordersList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private List<Orders> getOrders(JSONArray json) throws Exception {
        List<Orders> list = JsonToObject.jsonToList(json, Orders.class);
        for(Orders o:list) {
            JSONArray s = new JSONArray(o.getOrderDetail());
            List<OrderDetail> od = JsonToObject.jsonToList(s,OrderDetail.class);
            for(OrderDetail oo:od) {
                JSONObject j = new JSONObject(oo.getGoodsDetail());
                GoodsDetail g = (GoodsDetail) JsonToObject.jsonToPOJO(j,GoodsDetail.class);
                JSONObject jb = new JSONObject(g.getGoods());
                Goods gs = (Goods) JsonToObject.jsonToPOJO(jb,Goods.class);
                g.setGoodss(gs);
                List<Attribute> attribute = JsonToObject.jsonToList(new JSONArray(g.getAttribute()),Attribute.class);
                g.setAttributes(attribute);
                JSONArray jj = new JSONArray(g.getImages());
                List<Image> images = JsonToObject.jsonToList(jj,Image.class);
                g.setImagess(images);
                oo.setGoodsDetails(g);
            }

            o.setOrderDetails(od);
        }
        log.info("订单信息："+list);
        return list;

    }


    @Override
    protected int getContentResourseId() {
        return R.layout.activity_myorder;
    }

    private void initAdd() {
        initOrders(map);
        mAdapter.notifyDataSetChanged();
    }

    private void initToolBar() {
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyOrdersActivity.this.finish();
            }
        });
    }

}
