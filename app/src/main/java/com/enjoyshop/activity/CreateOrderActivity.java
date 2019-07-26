package com.enjoyshop.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.adapter.OrdersDetailsAdapter;
import com.enjoyshop.fragment.CustomDialog;
import com.enjoyshop.helper.JsonToObject;
import com.enjoyshop.utils.LogUtil;
import com.enjoyshop.utils.QRCodeUtil;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.FullyLinearLayoutManager;
import com.wang.bean.Address;
import com.wang.bean.CartDetail;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import lombok.extern.java.Log;
import okhttp3.Call;

import static com.enjoyshop.contants.Contants.CHOOSE_ADDRESS_ORDER;
import static com.enjoyshop.contants.Contants.CHOOSE_ORDER;
import static com.enjoyshop.contants.HttpContants.CHANGE_ORDER;
import static com.enjoyshop.contants.HttpContants.GET_DEFAULT;
import static com.enjoyshop.contants.HttpContants.GET_PAY_URL;
import static com.enjoyshop.contants.HttpContants.GET_STATUS;
import static com.enjoyshop.contants.HttpContants.INSERT_ORDER;
import static com.enjoyshop.contants.HttpContants.INSERT_ORDER_CART;
import static com.enjoyshop.contants.HttpContants.PAY_ORDER;


/**
 * Describe: 订单确认
 */
@Log
public class CreateOrderActivity extends BaseActivity implements View.OnClickListener {

    //模拟支付支付渠道
    private static final String CHANNEL_ALIPAY = "alipay";

    @BindView(R.id.txt_order)
    TextView       txtOrder;
    @BindView(R.id.recycler_view)
    RecyclerView   mRecyclerView;
    @BindView(R.id.rl_alipay)
    RelativeLayout mLayoutAlipay;
    @BindView(R.id.rb_alipay)
    View           mRbAlipay;
    @BindView(R.id.btn_createOrder)
    Button         mBtnCreateOrder;
    @BindView(R.id.btn_cancleOrder)
    Button         mBtnCancleOrder;
    @BindView(R.id.txt_total)
    TextView       mTxtTotal;
    @BindView(R.id.to_name)
    TextView       to_name;
    @BindView(R.id.to_address)
    TextView       to_address;
    @BindView(R.id.ll_pay)
    LinearLayout ll_pay;

    private Dialog dialog;

    private Timer mTimer;


    private OrdersDetailsAdapter mAdapter;
    private String            orderNum;
    private String payChannel = CHANNEL_ALIPAY;           //默认为支付宝支付
    private double amount;
    private boolean isAddress = true;

    private Map<String,Object> map = new HashMap<>();
    private Map<String,Object> map2 = new HashMap<>();
    private List<CartDetail> cartDetails;
    private CartDetail cartDetail;
    private Address address;
    private Integer user_id;
    private Integer status;
    private String statu = "";
    private String order_id;
    private boolean isPay = true;

    private String url = "";
    private Bitmap mBitmap;

    private HashMap<String, RelativeLayout> channels = new HashMap<>();

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_create_order;
    }

    @Override
    protected void init() {
        user_id = EnjoyshopApplication.getInstance().getUser().getUser_id();
        status = getIntent().getIntExtra("status",0);
        amount = getIntent().getDoubleExtra("totalPrice",0);
        getData(map);
        showData();
        initView();

        dialog = new Dialog(CreateOrderActivity.this);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener(){

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                ToastUtils.showSafeToast(CreateOrderActivity.this,"支付取消了");
                /*isPay = false;
                if(status==98) {
                    createNoPayOrderByCart(map);
                } else {
                    createNoPayOrder(map);
                }*/
                goBack2();
            }
        });
        mTimer = new Timer();
    }

    private void getData(Map map) {
        cartDetails = (List<CartDetail>) getIntent().getSerializableExtra("orderList");
        cartDetail = cartDetails.get(0);
        order_id = cartDetail.getOrder_id()+"";
        map2.put("order_id",order_id);
        log.info("订单信息："+cartDetails);
        map.put("user.user_id",user_id+"");
        address = (Address) getIntent().getSerializableExtra("address");
        if(address!=null) {
            setAddress(address);
            return;
        }
        OkHttpUtils.post().url(GET_DEFAULT).params(map).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                android.util.Log.d(TAG, "error = " + e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if (response != null&&Integer.parseInt(jb.get("code").toString())==1) {
                        JSONObject json = (JSONObject) jb.get("obj");
                        address = (Address) JsonToObject.jsonToPOJO(json,Address.class);
                        setAddress(address);
                    } else {
                        setAddress(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createNoPayOrderByCart(Map map) {
        map.put("user_id",user_id+"");
        map.put("to_userName",address.getAddress_user());
        map.put("to_addr",address.getAddress_name());
        map.put("to_tel",address.getAddress_tel());
        map.put("order_totalmoney",amount+"");
        String idstr = "";
        for(CartDetail c:cartDetails) {
            idstr+=c.getCartdetail_id()+",";
        }
        idstr = idstr.substring(0,idstr.length()-1);
        map.put("id_str",idstr);
        LogUtil.e("购物车生成的待支付订单",map.toString(),true);
        OkHttpUtils.post().url(INSERT_ORDER_CART).params(map).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                android.util.Log.d(TAG, "error = " + e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if (response != null&&Integer.parseInt(jb.get("code").toString())==1) {
                        order_id = jb.get("obj").toString();
                        ToastUtils.showSafeToast(CreateOrderActivity.this,"待付款订单已生成！");
                        url = "http://192.168.43.53:8762/service-user/pay.action?order_id="+order_id+"&order_totalmoney="+amount;
                        LogUtil.e("生成的支付二维码",url,true);
                        mBitmap = QRCodeUtil.createQRCodeBitmap(url, 480, 480);
                        ImageView mImageView = new ImageView(CreateOrderActivity.this);
                        mImageView.setImageBitmap(mBitmap);

                        dialog.setTitle("请扫描下方二维码完成支付");
                        dialog.setContentView(mImageView);
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createNoPayOrder(Map map) {
        map.put("user_id",user_id+"");
        map.put("to_userName",address.getAddress_user());
        map.put("to_addr",address.getAddress_name());
        map.put("to_tel",address.getAddress_tel());
        map.put("order_totalmoney",amount+"");
        map.put("goods_count",cartDetail.getGoods_count()+"");
        map.put("goods_money",cartDetail.getGoods_money()+"");
        map.put("goodsDetail.goodsdetail_id",cartDetail.getGoodsDetails().getGoodsdetail_id()+"");
        LogUtil.e("商城生成的待支付订单",map.toString(),true);
        OkHttpUtils.post().url(INSERT_ORDER).params(map).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                android.util.Log.d(TAG, "error = " + e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if (response != null&&Integer.parseInt(jb.get("code").toString())==1) {
                        order_id = jb.get("obj").toString();
                        LogUtil.e("生成订单号",order_id+"",true);
                        ToastUtils.showSafeToast(CreateOrderActivity.this,"待付款订单已生成！");
                        url = "http://192.168.43.53:8762/service-user/pay.action?order_id="+order_id+"&order_totalmoney="+amount;
                        LogUtil.e("生成的支付二维码",url,true);
                        mBitmap = QRCodeUtil.createQRCodeBitmap(url, 480, 480);
                        ImageView mImageView = new ImageView(CreateOrderActivity.this);
                        mImageView.setImageBitmap(mBitmap);

                        dialog.setTitle("请扫描下方二维码完成支付");
                        dialog.setContentView(mImageView);
                        dialog.show();
                        timerTask(order_id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void timerTask(final String id) {
        TimerTask timerTask = new TimerTask(){
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Map map = new HashMap();
                        map.put("order_id",id);
                        OkHttpUtils.post().url(GET_STATUS).params(map).build().execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                android.util.Log.d(TAG, "error = " + e);
                            }
                            @Override
                            public void onResponse(String response, int id) {
                                try {
                                    JSONObject jb = new JSONObject(response);
                                    if (response != null&&Integer.parseInt(jb.get("code").toString())==1) {
                                        mTimer.cancel();
                                        ToastUtils.showSafeToast(CreateOrderActivity.this,"支付成功！");
                                        goBack2();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        };
        mTimer.schedule(timerTask,1,1000);
    }

    private void initView() {
        if(status!=0&&status!=99&&status!=98) {
            ll_pay.setVisibility(View.GONE);
            switch (status) {
                case 1:
                    mBtnCancleOrder.setVisibility(View.GONE);
                    statu = "催促发货";
                    break;
                case 2:
                    mBtnCancleOrder.setText("查看物流");
                    statu = "确认收货";
                    break;
                case 3:
                    mBtnCancleOrder.setVisibility(View.GONE);
                    statu = "立即评价";
                    break;
                case 4:
                    mBtnCancleOrder.setVisibility(View.GONE);
                    statu = "删除订单";
                    break;
            }
            mBtnCreateOrder.setText(statu);
            mTxtTotal.setText("应付款： ￥" + amount);
            return;
        } else if(status==99||status==98) {
            mBtnCreateOrder.setText("提交订单");
            mTxtTotal.setText("应付款： ￥" + amount);
            mBtnCancleOrder.setVisibility(View.GONE);
        }
        mTxtTotal.setText("付款： ￥" + amount);

        channels.put(CHANNEL_ALIPAY, mLayoutAlipay);

        mLayoutAlipay.setOnClickListener(this);
    }


    public void showData() {

        mAdapter = new OrdersDetailsAdapter(cartDetails);
        if(status==0) {
            try {
                FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(this);
                //recyclerView外面嵌套ScrollView.数据显示不全
                layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
                mRecyclerView.setLayoutManager(layoutManager);
            } catch (Exception e) {
                log.info(e.toString());
            }
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(CreateOrderActivity.this));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(CreateOrderActivity.this, DividerItemDecoration
                    .HORIZONTAL));
        }
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View v) {
        selectPayChannle(v.getTag().toString());
    }

    @OnClick(R.id.rl_addr)
    public void chooseAddress(View view) {
        if(status!=98&&status!=99) {
            ToastUtils.showSafeToast(CreateOrderActivity.this,"订单已生成不可修改！");
            return;
        }
        Intent intent = new Intent(CreateOrderActivity.this, AddressListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isChoose",true);
        bundle.putInt("ischoose",CHOOSE_ADDRESS_ORDER);
        intent.putExtras(bundle);
        startActivityForResult(intent,CHOOSE_ADDRESS_ORDER);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==CHOOSE_ADDRESS_ORDER){
            Address ad  = (Address) data.getSerializableExtra("address");
            LogUtil.e(TAG,"回调到订单页面，回调数据为："+ad,true);
            if(ad != null) {
                isAddress = true;
                setAddress(ad);
            }
        }
    }
    public void goBack() {
        Intent intent = new Intent(CreateOrderActivity.this, GoodsDetailsActivity.class);
        setResult(CHOOSE_ORDER,intent);
        finish();
    }

    public void goBack2() {
        Intent intent = new Intent(CreateOrderActivity.this, MyOrdersActivity.class);
        startActivity(intent);
    }
    public void setAddress(Address a) {
        if(a!=null) {
            LogUtil.e("送货至",a.getAddress_name(),true);
            to_name.setText(a.getAddress_user()+"("+a.getAddress_tel()+")");
            to_address.setText(a.getAddress_name());
        } else {
            isAddress = false;
            to_name.setText("对不起，您尚未添加地址");
            to_address.setText("点击前往添加地址");
        }

    }


    /**
     * 当前的支付渠道 以及三个支付渠道互斥 的功能
     */
    public void selectPayChannle(String paychannel) {

        for (Map.Entry<String, RelativeLayout> entry : channels.entrySet()) {
            payChannel = paychannel;
            RelativeLayout rb = entry.getValue();
            if (entry.getKey().equals(payChannel)) {
                int childCount = rb.getChildCount();
                LogUtil.e("测试子控件", childCount + "", true);

                View viewCheckBox = rb.getChildAt(2);      //这个是类似checkBox的控件
                viewCheckBox.setBackgroundResource(R.drawable.icon_check_true);
            } else {
                View viewCheckBox = rb.getChildAt(2);      //这个是类似checkBox的控件
                viewCheckBox.setBackgroundResource(R.drawable.icon_check_false);
            }

        }
    }



    @OnClick(R.id.btn_createOrder)
    public void createNewOrder(View view) {
        switch (status) {
            case 0:
                pay();
                break;
            case 1:
                cuiHuo();
                goBack();
                break;
            case 2:
                shouHuo(map2);
                goBack();
                break;
            case 3:
                review();
                goBack();
                break;
            case 4:
                delOrder(map2);
                goBack();
                break;
            default:
                createOrder();
                break;
        }
    }

    private void createOrder() {
        if(!isAddress) {
            ToastUtils.showSafeToast(CreateOrderActivity.this,"对不起，尚未选择地址！");
            return;
        }
        if(status==98) {
            createNoPayOrderByCart(map);
        } else {
            createNoPayOrder(map);
        }

    }

    private void pay() {
        url = "http://192.168.43.53:8762/service-user/pay.action?order_id="+order_id+"&order_totalmoney="+amount;
        LogUtil.e("生成的支付二维码",url,true);
        mBitmap = QRCodeUtil.createQRCodeBitmap(url, 480, 480);
        ImageView mImageView = new ImageView(CreateOrderActivity.this);
        mImageView.setImageBitmap(mBitmap);

        dialog.setTitle("请扫描下方二维码完成支付");
        dialog.setContentView(mImageView);
        dialog.show();
        timerTask(order_id);
    }

    private void delOrder(Map map2) {
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
                        ToastUtils.showSafeToast(CreateOrderActivity.this,"订单已删除！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void review() {

    }

    private void shouHuo(Map map2) {
        map2.put("status","收货");
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
                        ToastUtils.showSafeToast(CreateOrderActivity.this,"收货成功！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void cuiHuo() {
        ToastUtils.showSafeToast(CreateOrderActivity.this,"催货成功！");
    }

    @OnClick(R.id.btn_cancleOrder)
    public void cancleOrder(View view) {
        if(mBtnCancleOrder.getText().toString().equals("查看物流")) {
            lookMap();
        } else {
            cancle(map2);
            goBack();
        }
    }

    private void lookMap() {
        ToastUtils.showSafeToast(CreateOrderActivity.this,"功能暂未开放！");
    }

    private void cancle(Map map2) {
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
                        ToastUtils.showSafeToast(CreateOrderActivity.this,"订单已取消！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 因为接口文档要求,商品列表为json格式,所以这里需要定义一个内部类
     */
    class WareItem {

        private Long ware_id;
        private int  amount;

        public WareItem(Long ware_id, int amount) {
            this.ware_id = ware_id;
            this.amount = amount;
        }

        public Long getWare_id() {
            return ware_id;
        }

        public void setWare_id(Long ware_id) {
            this.ware_id = ware_id;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

}