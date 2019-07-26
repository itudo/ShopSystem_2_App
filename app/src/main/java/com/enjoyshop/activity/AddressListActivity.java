package com.enjoyshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.adapter.AddressListAdapter;
import com.enjoyshop.helper.JsonToObject;
import com.enjoyshop.utils.LogUtil;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.EnjoyshopToolBar;
import com.wang.bean.Address;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;

import static com.enjoyshop.contants.Contants.CHOOSE_ADDRESS_GOODS;
import static com.enjoyshop.contants.Contants.CHOOSE_ADDRESS_ORDER;
import static com.enjoyshop.contants.HttpContants.DEFAULT_ADDRESS;
import static com.enjoyshop.contants.HttpContants.DEL_ADDRESS;
import static com.enjoyshop.contants.HttpContants.GET_ADDRESS;

/**
 * Describe: 收货地址
 */
@lombok.extern.java.Log
public class AddressListActivity extends BaseActivity {
    Map<String,Object> map = new HashMap<String,Object>();
    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerview;

    private AddressListAdapter mAdapter;
    private List<Address> mAddressDataList;
    private int user_id;
    private boolean isChoose;
    private Integer ischoose;

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_address_list;
    }

    @Override
    protected void init() {
        initToolbar();
        user_id = EnjoyshopApplication.getInstance().getUser().getUser_id();
        isChoose = getIntent().getBooleanExtra("isChoose",false);
        ischoose = getIntent().getIntExtra("ischoose",0);
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        map.put("user_id",EnjoyshopApplication.getInstance().getUser().getUser_id()+"");
        initAdd();
    }


    private void initView() {
        if (mAdapter == null) {
            mAdapter = new AddressListAdapter(mAddressDataList);
            mRecyclerview.setAdapter(mAdapter);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(AddressListActivity.this));
            mRecyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration
                    .HORIZONTAL));

            mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    Address address = (Address)adapter.getData().get(position);
                    if(view.getId()==R.id.address||view.getId()==R.id.addresss) {
                        if(isChoose) {
                            choose(address);
                        }
                    }
                    switch (view.getId()) {
                        case R.id.txt_edit:
                            updateAddress(address);
                            break;
                        case R.id.txt_del:
                            delAddress(address,map);
                            break;
                        case R.id.cb_is_defualt:
                            chooseDefult(address,map);
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    /**
     * 初始化地址信息
     */
    private void initAddress(Map map) {
        OkHttpUtils.post().url(GET_ADDRESS).params(map)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d(TAG, "error = " + e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if (response != null&&Integer.parseInt(jb.get("code").toString())==0) {
                        ToastUtils.showSafeToast(AddressListActivity.this, "当前用户没有地址！");
                    }  else if (response != null&&Integer.parseInt(jb.get("code").toString())==1){
                        //EnjoyshopApplication application = EnjoyshopApplication.getInstance();
                        JSONArray json = (JSONArray) jb.get("obj");
                        mAddressDataList= JsonToObject.jsonToList(json, Address.class);
                        mAdapter.setNewData(mAddressDataList);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void updateAddress(Address address) {
        jumpAddressAdd(address);
    }

    private void choose(Address address) {
        LogUtil.e("选择了",address.getAddress_name()+ischoose,true);
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("address",address);
        Intent intent = null;
        if(ischoose==CHOOSE_ADDRESS_GOODS) {
            intent = new Intent(AddressListActivity.this, GoodsDetailsActivity.class);
            intent.putExtras(bundle1);
            setResult(CHOOSE_ADDRESS_GOODS,intent);
        } else if(ischoose==CHOOSE_ADDRESS_ORDER) {
            intent = new Intent(AddressListActivity.this, CreateOrderActivity.class);
            intent.putExtras(bundle1);
            setResult(CHOOSE_ADDRESS_ORDER,intent);
        }
        finish();

    }

    private void delAddress(Address address,Map map) {
        map.put("address_id",address.getAddress_id()+"");
        OkHttpUtils.post().url(DEL_ADDRESS).params(map)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d(TAG, "error = " + e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if (response != null&&Integer.parseInt(jb.get("code").toString())==0) {
                        ToastUtils.showSafeToast(AddressListActivity.this, "地址删除失败！");
                    }  else if (response != null&&Integer.parseInt(jb.get("code").toString())==1){
                        ToastUtils.showSafeToast(AddressListActivity.this, "地址删除成功！");
                        initAdd();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 需要改变2个对象的值.一个是 之前默认的.一个是当前设置默认的
     * @param address
     */
    private void chooseDefult(Address address,Map map) {
        map.put("address_id",address.getAddress_id()+"");
        map.put("user.user_id",user_id+"");
        OkHttpUtils.post().url(DEFAULT_ADDRESS).params(map)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d(TAG, "error = " + e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if (response != null&&Integer.parseInt(jb.get("code").toString())==0) {
                        ToastUtils.showSafeToast(AddressListActivity.this, "修改默认地址失败！");
                    }  else if (response != null&&Integer.parseInt(jb.get("code").toString())==1){
                        ToastUtils.showSafeToast(AddressListActivity.this, "默认地址修改成功！");
                        initAdd();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    private void initAdd() {
        initAddress(map);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 标题的初始化
     */
    private void initToolbar() {
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到添加地址界面
                jumpAddressAdd(null);
            }
        });
    }

    private void jumpAddressAdd(Address address) {
        Intent intent = new Intent(AddressListActivity.this, AddressAddActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("address",address);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
