package com.enjoyshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.enjoyshop.R;
import com.enjoyshop.helper.MyMap;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.ClearEditText;
import com.enjoyshop.widget.EnjoyshopToolBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;

import static com.enjoyshop.contants.HttpContants.USER_VALIATE;


/*
 * Describe: 注册activity
 */
public class RegActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;
    @BindView(R.id.txtCountry)
    TextView         mTxtCountry;
    @BindView(R.id.edittxt_user_tel)
    ClearEditText    mEtxt_user_tel;
    @BindView(R.id.edittxt_user_pwd)
    ClearEditText    mEtxt_user_pwd;
    @BindView(R.id.edittxt_user_name)
    ClearEditText    mEtxt_user_name;
    @BindView(R.id.edittxt_idcard)
    ClearEditText    mEtxt_idcard;
    @BindView(R.id.edittxt_email)
    ClearEditText    mEtxt_email;
    @BindView(R.id.rb_man)
    RadioButton rbman;
    @BindView(R.id.rb_woman)
    RadioButton rbwoman;

    private String user_tel;
    private String user_pwd;
    private String user_name;
    private String user_sex;
    private String user_idcard;
    private String user_email;
    private Map<String,Object> map = new HashMap<String,Object>();

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_reg;
    }


    @Override
    protected void init() {
        initToolBar();
    }


    private void initToolBar() {

        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCode();
            }
        });
    }

    /**
     * 获取手机号 密码等信息
     */
    private void getCode() {

        user_tel = mEtxt_user_tel.getText().toString().trim().replaceAll("\\s*", "");
        user_pwd = mEtxt_user_pwd.getText().toString().trim();
        user_name = mEtxt_user_name.getText().toString().trim();
        user_idcard = mEtxt_idcard.getText().toString().trim();
        user_email = mEtxt_email.getText().toString().trim();
        if(rbman.isClickable()) {
            user_sex = "男";
        } else if(rbwoman.isClickable()) {
            user_sex = "女";
        }
        map.put("user_name",user_name);
        map.put("user_pwd",user_pwd);
        map.put("user_sex",user_sex);
        map.put("user_tel",user_tel);
        map.put("user_email",user_email);
        map.put("user_idcard",user_idcard);
        checkPhoneNum();
    }

    /**
     * 对手机号进行验证
     * 是否合法  是否已经注册
     */
    private void checkPhoneNum() {

        if (TextUtils.isEmpty(map.get("user_name").toString())) {
            ToastUtils.showSafeToast(RegActivity.this, "请输入用户名");
            return;
        }
        if (TextUtils.isEmpty(map.get("user_pwd").toString())) {
            ToastUtils.showSafeToast(RegActivity.this, "请输入密码");
            return;
        }
        if (TextUtils.isEmpty(map.get("user_sex").toString())) {
            ToastUtils.showSafeToast(RegActivity.this, "请选择性别");
            return;
        }
        if (TextUtils.isEmpty(map.get("user_tel").toString())) {
            ToastUtils.showSafeToast(RegActivity.this, "请输入您的手机号");
            return;
        }
        if (TextUtils.isEmpty(map.get("user_email").toString())) {
            ToastUtils.showSafeToast(RegActivity.this, "请输入邮箱");
            return;
        }

        if (TextUtils.isEmpty(map.get("user_idcard").toString())) {
            ToastUtils.showSafeToast(RegActivity.this, "请输入身份证");
            return;
        }
        queryUserData(map);
    }

    /**
     * 查询手机号是否已经注册了
     * <p>
     */
    private void queryUserData(Map map) {
        OkHttpUtils.post().url(USER_VALIATE).params(map).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d(TAG, "error = " + e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if (response != null&&Integer.parseInt(jb.get("code").toString())==1) {
                        ToastUtils.showSafeToast(RegActivity.this, "手机号已注册！");
                    } else if (response != null&&Integer.parseInt(jb.get("code").toString())==2) {
                        ToastUtils.showSafeToast(RegActivity.this, "邮箱已注册");
                    } else {
                        jumpRegSecondUi();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 跳转到注册界面二
     */

    private void jumpRegSecondUi() {
        Intent intent = new Intent(this, RegSecondActivity.class);
        MyMap mymap = new MyMap();
        mymap.setMap((HashMap<String, Object>) map);//将hashmap数据添加到封装的myMap中  
        Bundle bundle = new Bundle();
        bundle.putSerializable("map",mymap);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}

