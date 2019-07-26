package com.enjoyshop.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.contants.Contants;
import com.enjoyshop.contants.HttpContants;
import com.enjoyshop.utils.GlideUtils;
import com.enjoyshop.utils.LogUtil;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.ClearEditText;
import com.enjoyshop.widget.EnjoyshopToolBar;
import com.wang.bean.Users;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

import static com.enjoyshop.contants.Contants.REQUEST_CODE2;

/**
 * Describe:个人中心
 */

public class MyCenterActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;
    @BindView(R.id.user_head)
    ImageView user_head;
    @BindView(R.id.user_sex)
    ClearEditText user_sex;
    @BindView(R.id.user_name)
    ClearEditText user_name;
    @BindView(R.id.user_tel)
    ClearEditText user_tel;
    @BindView(R.id.user_email)
    ClearEditText user_email;
    @BindView(R.id.user_idcard)
    ClearEditText user_idcard;
    @BindView(R.id.change_info)
    Button change;

    private Users user;
    private Map<String,Object> map = new HashMap<>();

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_mycenter;
    }


    @Override
    protected void init() {
        user = EnjoyshopApplication.getInstance().getUser();
        initToolBar();
        initUserData();
    }

    private void initUserData() {
        GlideUtils.portrait(MyCenterActivity.this, user.getUser_head(), user_head);
        user_sex.setText(user.getUser_sex());
        user_name.setText(user.getUser_name());
        user_tel.setText(user.getUser_tel()+"");
        user_email.setText(user.getUser_email());
        user_idcard.setText(user.getUser_idcard());
    }

    @OnClick({R.id.change_info,R.id.change_pwd})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.change_info:
                changeInfo();
                break;
            case R.id.change_pwd:
                Intent intent2 = new Intent(MyCenterActivity.this, ChangePwdActivity.class);
                startActivity(intent2);
                finish();
                break;
        }
    }

    private void changeInfo() {
        if(change.getText().equals("修改资料")) {
            user_sex.setEnabled(true);
            user_name.setEnabled(true);
            user_tel.setEnabled(true);
            user_email.setEnabled(true);
            user_idcard.setEnabled(true);
            change.setText("保存修改");
        } else {
            user.setUser_idcard(user_idcard.getText().toString());
            user.setUser_email(user_email.getText().toString());
            user.setUser_tel(user_tel.getText().toString());
            user.setUser_sex(user_sex.getText().toString());
            user.setUser_name(user_name.getText().toString());
            updateUserInfo();
            LogUtil.e("修改后的用户信息",user.toString(),true);
            user_sex.setEnabled(false);
            user_name.setEnabled(false);
            user_tel.setEnabled(false);
            user_email.setEnabled(false);
            user_idcard.setEnabled(false);
            change.setText("修改资料");
        }

    }

    private void updateUserInfo() {
        OkHttpUtils.get().url(HttpContants.UPDATE_USER)
                .addParams("user_id", user.getUser_id()+"")
                .addParams("user_name",user.getUser_name())
                .addParams("user_tel",user.getUser_tel())
                .addParams("user_email",user.getUser_email())
                .addParams("user_idcard",user.getUser_idcard())
                .addParams("user_sex",user.getUser_sex())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject json = new JSONObject(response);
                    if((Integer)json.get("code")==1) {
                        EnjoyshopApplication application = EnjoyshopApplication.getInstance();
                        application.putUser(user,"12345678asfghdssa");
                        ToastUtils.showSafeToast(MyCenterActivity.this,"信息修改成功！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 关于标题栏的操作
     */
    private void initToolBar() {
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyCenterActivity.this.finish();
            }
        });
    }

}
