package com.enjoyshop.activity;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.bean.PickerCityAddressBean;
import com.enjoyshop.utils.GetJsonDataUtil;
import com.enjoyshop.utils.KeyBoardUtils;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.ClearEditText;
import com.enjoyshop.widget.EnjoyshopToolBar;
import com.google.gson.Gson;
import com.wang.bean.Address;
import com.wang.bean.Users;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

import static com.enjoyshop.contants.HttpContants.ADD_ADDRESS;
import static com.enjoyshop.contants.HttpContants.UPDATE_ADDRESS;


/**
 * Describe: 添加收货地址
 */
@lombok.extern.java.Log
public class AddressAddActivity extends BaseActivity {

    //三级联动 github地址   //https://github.com/saiwu-bigkoo/Android-PickerView    start:4953

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;
    @BindView(R.id.edittxt_consignee)
    ClearEditText    mEditConsignee;
    @BindView(R.id.edittxt_phone)
    ClearEditText    mEditPhone;
    @BindView(R.id.txt_address)
    TextView         mTxtAddress;
    @BindView(R.id.edittxt_add)
    ClearEditText    mEditAddr;


    private ArrayList<PickerCityAddressBean>        options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>>            options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private boolean                                 isLoaded      = false;
    private Thread thread;
    private static final int MSG_LOAD_DATA    = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED  = 0x0003;

    /**
     * 默认只有1条数据
     */
    private boolean isOnlyAddress = true;

    private Address address;
    private Users user;

    private Map<String,Object> map = new HashMap<>();

    /**
     * 数据库的操作类型.
     * 如果是0 就是增加(默认)
     * 1 就是修改
     */
    private int addressDoType = 0;


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 写子线程中的操作,解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;
                case MSG_LOAD_SUCCESS:
                    isLoaded = true;
                    break;
                case MSG_LOAD_FAILED:
                    ToastUtils.showSafeToast(AddressAddActivity.this, "数据获取失败,请重试");
                    break;

            }
        }
    };

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_address_add;
    }


    @Override
    protected void init() {

        mHandler.sendEmptyMessage(MSG_LOAD_DATA);

        address = (Address) getIntent().getSerializableExtra("address");
        user = EnjoyshopApplication.getApplication().getUser();
        map.put("user_id",user.getUser_id()+"");
        if (address != null) {
            addressDoType = 1;
            log.info("进行修改操作");
            editAddress();
        } else {
            addressDoType = 0;
            log.info("进行添加操作");
        }

        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAddress(map);
            }
        });
    }


    @OnClick({R.id.txt_address})
    public void viewClick(View view) {
        switch (view.getId()) {
            case R.id.txt_address:
                if (isLoaded) {
                    KeyBoardUtils.closeKeyboard(mEditConsignee,AddressAddActivity.this);
                    KeyBoardUtils.closeKeyboard(mEditPhone,AddressAddActivity.this);
                    KeyBoardUtils.closeKeyboard(mEditAddr,AddressAddActivity.this);
                    ShowPickerView();
                } else {
                    ToastUtils.showSafeToast(AddressAddActivity.this, "请稍等,数据获取中");
                }
                break;
        }
    }

    /**
     * 修改新的地址
     */
    private void editAddress() {
        Integer address_id = address.getAddress_id();
        map.put("address_id",address_id+"");

        String address_user = address.getAddress_user();
        String address_tel = address.getAddress_tel();
        String address_name = address.getAddress_name();
        String address_code = address.getAddress_code();

        mEditConsignee.setText(address_user);
        //mEditConsignee.setSelection(9);
        mEditPhone.setText(address_tel);
        mTxtAddress.setText(address_name);
        mEditAddr.setText(address_code);
    }

    /**
     * 创建新的地址
     */
    public void createAddress(Map map) {

        String address_user = mEditConsignee.getText().toString();    //收件人
        String address_tel = mEditPhone.getText().toString();

        String address_code = mEditAddr.getText().toString();
        String address_name = mTxtAddress.getText().toString();

        //进行非空判断
        if (TextUtils.isEmpty(address_user)) {
            ToastUtils.showSafeToast(AddressAddActivity.this, "收件人为空,请检查");
            return;
        }

        if (TextUtils.isEmpty(address_tel)) {
            ToastUtils.showSafeToast(AddressAddActivity.this, "联系电话为空,请检查");
            return;
        }

        if (TextUtils.isEmpty(address_name)) {
            ToastUtils.showSafeToast(AddressAddActivity.this, "地址不完整,请检查");
            return;
        }

        if (TextUtils.isEmpty(address_code)) {
            ToastUtils.showSafeToast(AddressAddActivity.this, "邮政编码为空,请检查");
            return;
        }
        map.put("address_user",address_user);
        map.put("address_tel",address_tel);
        map.put("address_name",address_name);
        map.put("address_code",address_code);

        if (addressDoType == 0) {
            log.info("添加地址为："+map);
            OkHttpUtils.post().url(ADD_ADDRESS).params(map)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.d(TAG, "error = " + e);
                }
                @Override
                public void onResponse(String response, int id) {
                    ToastUtils.showSafeToast(AddressAddActivity.this, "地址添加成功！");
                }
            });

        } else if (addressDoType == 1) {
            log.info("修改地址为："+map);
            OkHttpUtils.post().url(UPDATE_ADDRESS).params(map)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.d(TAG, "error = " + e);
                }
                @Override
                public void onResponse(String response, int id) {
                    try {
                        JSONObject jb = new JSONObject(response);
                        if (jb != null&&Integer.parseInt(jb.get("code").toString())==0) {
                            ToastUtils.showSafeToast(AddressAddActivity.this, "地址修改失败！");
                        }  else if (response != null&&Integer.parseInt(jb.get("code").toString())==1){
                            ToastUtils.showSafeToast(AddressAddActivity.this, "地址修改成功！");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        finish();
    }


    private void ShowPickerView() {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView
                .OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText() +
                        options2Items.get(options1).get(options2) +
                        options3Items.get(options1).get(options2).get(options3);

                mTxtAddress.setText(tx);
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();

        pvOptions.setPicker(options1Items, options2Items, options3Items);       //三级选择器
        pvOptions.show();
    }


    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = GetJsonDataUtil.getJson(this, "province.json");
        //获取assets目录下的json文件数据

        ArrayList<PickerCityAddressBean> jsonBean = parseData(JsonData);     //用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {

                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size();
                         d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            //添加城市数据
            options2Items.add(CityList);
            //添加地区数据
            options3Items.add(Province_AreaList);
        }

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }


    public ArrayList<PickerCityAddressBean> parseData(String result) {    //Gson 解析
        ArrayList<PickerCityAddressBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                PickerCityAddressBean entity = gson.fromJson(data.optJSONObject(i).toString(),
                        PickerCityAddressBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }


}
