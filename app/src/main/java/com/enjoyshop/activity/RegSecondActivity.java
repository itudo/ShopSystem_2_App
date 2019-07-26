package com.enjoyshop.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.enjoyshop.R;
import com.enjoyshop.helper.MyMap;
import com.enjoyshop.utils.CountTimerView;
import com.enjoyshop.utils.LogUtil;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.ClearEditText;
import com.enjoyshop.widget.EnjoyshopToolBar;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import okhttp3.Call;

import static com.enjoyshop.contants.HttpContants.SEND_MAIL;
import static com.enjoyshop.contants.HttpContants.USER_REG;

/**
 * Describe: 接收验证码的注册界面,即注册界面二
 */
@lombok.extern.java.Log
public class RegSecondActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;

    @BindView(R.id.txtTip)
    TextView mTxtTip;

    @BindView(R.id.btn_reSend)
    Button mBtnResend;

    @BindView(R.id.edittxt_code)
    ClearEditText mEtCode;

    private String phone;
    private String pwd;
    private String code;

    private SpotsDialog dialog;
    private Gson mGson = new Gson();

    private Map<String,Object> map;

    @Override
    protected void init() {

        initToolBar();
        dialog = new SpotsDialog(this);

        map = ((MyMap) getIntent().getSerializableExtra("map")).getMap();
        System.out.println(map);
        phone = map.get("user_tel").toString();
        mTxtTip.setText(phone);
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_reg_second;
    }

    /**
     * 标题栏 完成
     */
    private void initToolBar() {
        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCode();
            }
        });
    }

    /**
     * 提交验证码
     */
    private void submitCode() {
        String vCode = mEtCode.getText().toString().trim();

        if (TextUtils.isEmpty(vCode)) {
            ToastUtils.showSafeToast(RegSecondActivity.this, "请填写验证码");
        }

        if (!code.equals(vCode)) {
            ToastUtils.showSafeToast(RegSecondActivity.this, "验证码不准确,请重新获取");
        } else {
            userReg(map);
        }

    }

    @OnClick(R.id.btn_reSend)
    public void getVcode(View view) {

        //倒计时
        CountTimerView timerView = new CountTimerView(mBtnResend);
        timerView.start();

        code = (int)((Math.random()*9+1)*1000)+"";
        LogUtil.e(TAG,"发送的验证码为："+code,true);
        OkHttpUtils.post().url(SEND_MAIL)
                .addParams("sendTo",phone)
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
                        LogUtil.e(TAG,"验证码发送成功！",true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void userReg(Map map) {
        log.info("用户注册：" + map);
        OkHttpUtils.post().url(USER_REG).params(map).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d(TAG, "error = " + e);
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if (response != null && Integer.parseInt(jb.get("code").toString()) == 0) {
                        ToastUtils.showSafeToast(RegSecondActivity.this, "注册失败！");
                    } else if (response != null && Integer.parseInt(jb.get("code").toString()) == 1) {
                        ToastUtils.showSafeToast(RegSecondActivity.this, "注册成功");
                        jumpLoginUi();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void jumpLoginUi() {
        log.info("注册成功："+map);
        startActivity(new Intent(RegSecondActivity.this, LoginActivity.class));
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
