package com.enjoyshop.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.helper.JsonToObject;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.ClearEditText;
import com.enjoyshop.widget.EnjoyshopToolBar;
import com.wang.bean.Users;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

import static com.enjoyshop.contants.Contants.REQUEST_CODE;
import static com.enjoyshop.contants.HttpContants.USER_LOGIN;

/**
 * Describe: 登录界面
 */
@lombok.extern.java.Log
public class LoginActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;
    @BindView(R.id.etxt_phone)
    ClearEditText    mEtxtPhone;
    @BindView(R.id.etxt_pwd)
    ClearEditText    mEtxtPwd;
    @BindView(R.id.txt_toReg)
    TextView         mTxtToReg;
    @BindView(R.id.txt_forget)
    TextView         mForget;


    @Override
    protected void init() {
        initToolBar();
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_login;
    }

    private void initToolBar() {

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
            }
        });
    }


    @OnClick({R.id.btn_login, R.id.txt_toReg,R.id.txt_forget})
    public void viewclick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();   //登录
                break;
            case R.id.txt_toReg:
                Intent intent = new Intent(this, RegActivity.class);
                startActivity(intent);
                break;
            case R.id.txt_forget:
                Intent intent2 = new Intent(this, Forget1Activity.class);
                startActivity(intent2);
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {

        String name = mEtxtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showSafeToast(LoginActivity.this, "请输入手机号或用户名");
            return;
        }

        String pwd = mEtxtPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showSafeToast(LoginActivity.this, "请输入密码");
            return;
        }
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("name",name);
        map.put("user_pwd",pwd);
        loginlogic(map);

    }

    private void loginlogic(Map map) {
        log.info("用户登录："+map);
        OkHttpUtils.post().url(USER_LOGIN).params(map).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d(TAG, "error = " + e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if (response != null&&Integer.parseInt(jb.get("code").toString())==0) {
                        ToastUtils.showSafeToast(LoginActivity.this, "手机号或用户名不存在！");
                    } else if (response != null&&Integer.parseInt(jb.get("code").toString())==2) {
                        ToastUtils.showSafeToast(LoginActivity.this, "密码错误！请重新输入！");
                    } else if (response != null&&Integer.parseInt(jb.get("code").toString())==1){
                        ToastUtils.showSafeToast(LoginActivity.this, "登录成功");
                        EnjoyshopApplication application = EnjoyshopApplication.getInstance();
                        JSONObject json = (JSONObject) jb.get("obj");
                        Users user = (Users) JsonToObject.jsonToPOJO(json,Users.class);
                        log.info("登录成功："+user);
                        application.putUser(user, "12345678asfghdssa");
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        setResult(REQUEST_CODE,intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
