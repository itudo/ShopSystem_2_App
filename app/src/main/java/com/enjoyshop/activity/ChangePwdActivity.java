package com.enjoyshop.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.contants.HttpContants;
import com.enjoyshop.utils.LogUtil;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.ClearEditText;
import com.wang.bean.Users;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import okhttp3.Call;

public class ChangePwdActivity extends BaseActivity {
    @BindView(R.id.old_pwd)
    ClearEditText txt_old_pwd;
    @BindView(R.id.new_pwd)
    ClearEditText txt_new_pwd;
    @BindView(R.id.new_pwd2)
    ClearEditText txt_new_pwd2;
    @BindView(R.id.btn_change)
    Button btn_change;

    private Users user;
    private String oldPwd="";
    private String newPwd="";
    private String newPwd2="";

    @Override
    protected void init() {
        user = EnjoyshopApplication.getInstance().getUser();
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validata();
            }
        });
    }

    private void validata() {
        oldPwd = txt_old_pwd.getText().toString().trim();
        newPwd = txt_new_pwd.getText().toString().trim();
        newPwd2 = txt_new_pwd2.getText().toString().trim();
        LogUtil.e("输入的信息","旧密码："+oldPwd+",新密码："+newPwd+"+"+newPwd2,true);
        if (oldPwd.equals("")) {
            ToastUtils.showSafeToast(ChangePwdActivity.this, "请输入旧密码");
            return;
        }

        if (newPwd.equals("")) {
            ToastUtils.showSafeToast(ChangePwdActivity.this, "请输入新密码");
            return;
        }

        if (newPwd2.equals("")) {
            ToastUtils.showSafeToast(ChangePwdActivity.this, "请再次输入新密码");
            return;
        }
        if(!newPwd.equals(newPwd2)) {
            ToastUtils.showSafeToast(ChangePwdActivity.this, "两次密码输入不一致!,请重新输入！");
            return;
        }
        if(!oldPwd.equals(user.getUser_pwd())) {
            ToastUtils.showSafeToast(ChangePwdActivity.this, "对不起，旧密码输入错误！"+user.getUser_pwd());
            return;
        }

        changePwd();
    }

    private void changePwd() {
        OkHttpUtils.get().url(HttpContants.CHANGE_PWD)
                .addParams("user_id", user.getUser_id()+"")
                .addParams("user_pwd",newPwd)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject json = new JSONObject(response);
                    if((Integer)json.get("code")==1) {
                        jumpToCenter();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void jumpToCenter() {
        EnjoyshopApplication.getInstance().clearUser();
        LogUtil.e("用户已退出","",true);
        ToastUtils.showSafeToast(ChangePwdActivity.this,"密码修改成功！请重新登录！");
        Intent intent = new Intent(ChangePwdActivity.this,LoginActivity.class);
        //intent.putExtra("isLogin",true);
        startActivity(intent);
        finish();
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_change_pwd;
    }
}
