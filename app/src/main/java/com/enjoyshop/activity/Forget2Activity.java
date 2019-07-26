package com.enjoyshop.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.contants.HttpContants;
import com.enjoyshop.utils.LogUtil;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import okhttp3.Call;

public class Forget2Activity extends BaseActivity {
    @BindView(R.id.new_pwd)
    ClearEditText txt_new_pwd;
    @BindView(R.id.new_pwd2)
    ClearEditText txt_new_pwd2;
    @BindView(R.id.btn_change)
    Button btn_change;

    private String newPwd="";
    private String newPwd2="";
    private int user_id;

    @Override
    protected void init() {
        user_id = getIntent().getIntExtra("user_id",0);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validata();
            }
        });
    }

    private void validata() {
        newPwd = txt_new_pwd.getText().toString().trim();
        newPwd2 = txt_new_pwd2.getText().toString().trim();
        LogUtil.e("输入的信息","新密码："+newPwd+"+"+newPwd2,true);

        if (newPwd.equals("")) {
            ToastUtils.showSafeToast(Forget2Activity.this, "请输入新密码");
            return;
        }

        if (newPwd2.equals("")) {
            ToastUtils.showSafeToast(Forget2Activity.this, "请再次输入新密码");
            return;
        }
        if(!newPwd.equals(newPwd2)) {
            ToastUtils.showSafeToast(Forget2Activity.this, "两次密码输入不一致!,请重新输入！");
            return;
        }

        changePwd();
    }

    private void changePwd() {
        OkHttpUtils.get().url(HttpContants.CHANGE_PWD)
                .addParams("user_id", user_id+"")
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
        ToastUtils.showSafeToast(Forget2Activity.this,"密码修改成功！请重新登录！");
        Intent intent = new Intent(Forget2Activity.this,LoginActivity.class);
        //intent.putExtra("isLogin",true);
        startActivity(intent);
        finish();
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_forget2;
    }
}
