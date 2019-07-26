package com.enjoyshop.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.enjoyshop.R;
import com.enjoyshop.helper.JsonToObject;
import com.enjoyshop.utils.CountTimerView;
import com.enjoyshop.utils.LogUtil;
import com.enjoyshop.utils.ToastUtils;
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

import static com.enjoyshop.contants.HttpContants.SEND_EMAIL;
import static com.enjoyshop.contants.HttpContants.YANZHENG;


/*
 * Describe: 忘记密码activity
 */
public class Forget1Activity extends BaseActivity {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;
    @BindView(R.id.txt_user_tel)
    TextView userTel;
    @BindView(R.id.txt_email)
    TextView txt_email;
    @BindView(R.id.user_code)
    TextView user_code;
    @BindView(R.id.btn_sendCode)
    Button mBtnResend;

    private Map<String,Object> map = new HashMap<String,Object>();
    private String tel;
    private Users u;
    private String code;
    private Handler handler;
    private boolean isYanZheng = false;

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_forget1;
    }


    @Override
    protected void init() {
        initToolBar();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        txt_email.setText(((Users)msg.obj).getUser_email());
                        break;
                }
            }
        };
    }


    private void initToolBar() {

        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isYanZheng) {
                    submitCode();
                } else {
                    ToastUtils.showSafeToast(Forget1Activity.this,"对不起，尚未通过验证！");
                }
            }
        });
    }

    @OnClick({R.id.btn_yanzheng,R.id.btn_sendCode})
    public void viewclick(View view) {
        switch (view.getId()) {
            case R.id.btn_yanzheng:
                yanzheng();
                break;
            case R.id.btn_sendCode:
                getVcode();
                break;
        }
    }

    private void getVcode() {
        //倒计时
        CountTimerView timerView = new CountTimerView(mBtnResend);
        timerView.start();
        code = (int)((Math.random()*9+1)*100000)+"";
        LogUtil.e(TAG,"发送的验证码为："+code,true);
        OkHttpUtils.post().url(SEND_EMAIL)
                .addParams("email",txt_email.getText().toString())
                .addParams("code",code)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG,"error"+e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if(Integer.parseInt(jb.get("code").toString())==1) {
                        LogUtil.e(TAG,"验证码已收到：",true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void yanzheng() {
        LogUtil.e(TAG,"开始验证",true);
        tel = userTel.getText().toString().trim();
        if(TextUtils.isEmpty(tel)) {
            ToastUtils.showSafeToast(Forget1Activity.this, "请输入手机号或用户名");
            return;
        }
        OkHttpUtils.post().url(YANZHENG).addParams("name",tel).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG,"error:"+e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    LogUtil.e(TAG,"code:"+jb.get("code"),true);
                    if(Integer.parseInt(jb.get("code").toString())==1) {
                        JSONObject j = (JSONObject) jb.get("obj");
                        u = (Users) JsonToObject.jsonToPOJO(j,Users.class);
                        ToastUtils.showSafeToast(Forget1Activity.this,"验证通过！");
                        isYanZheng = true;
                        sendMsg(1,u);
                    } else {
                        ToastUtils.showSafeToast(Forget1Activity.this,"验证不通过！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 提交验证码
     */
    private void submitCode() {
        String vCode = user_code.getText().toString().trim();

        if (TextUtils.isEmpty(vCode)) {
            ToastUtils.showSafeToast(Forget1Activity.this, "请填写验证码");
        }

        if (!code.equals(vCode)) {
            ToastUtils.showSafeToast(Forget1Activity.this, "验证码不准确,请重新获取");
        } else {
            jumpSecondUi();
        }

    }

    private void sendMsg(int what,Object msg){
        Log.e("msg",msg.toString());
        handler.obtainMessage(what, msg).sendToTarget();
    }

    /**

    /**
     * 跳转到注册界面二
     */

    private void jumpSecondUi() {
        Intent intent = new Intent(this, Forget2Activity.class);
        intent.putExtra("user_id",u.getUser_id());
        startActivity(intent);
        finish();
    }
}

