package com.enjoyshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.wang.bean.Users;

import butterknife.ButterKnife;

/**
 * Created by 高磊华
 * Time  2017/8/9
 * Describe:activity的基类
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentResourseId());
        setStatusBar();
        ButterKnife.bind(this);
        init();
    }


    /**
     * android 5.0 及以下沉浸式状态栏
     */
    protected void setStatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//设置透明状态栏
        }

        initSystemBar(this);

    }


    /**
     * 沉浸式状态栏.
     */
    public void initSystemBar(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        // 使用颜色资源
        tintManager.setStatusBarTintResource(R.color.colorPrimary);

    }

    private void setTranslucentStatus(Activity activity, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);

    }


    protected abstract void init();

    protected abstract int getContentResourseId();

    public void startActivity(Intent intent, boolean isNeedLogin) {

        if (isNeedLogin) {
            Users user = EnjoyshopApplication.getInstance().getUser();
            if (user != null) {
                super.startActivity(intent);
            } else {
                EnjoyshopApplication.getInstance().putIntent(intent);
                Intent loginIntent = new Intent(this, LoginActivity.class);
                super.startActivity(intent);
            }

        } else {
            super.startActivity(intent);
        }
    }

}
