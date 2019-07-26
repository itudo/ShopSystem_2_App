package com.enjoyshop.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.activity.LoginActivity;
import com.wang.bean.Users;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.enjoyshop.contants.Contants.REQUEST_CODE;

/**
 * Describe: fragment的基类
 */

public abstract class BaseFragment extends Fragment {

    private   View           mView;
    protected Bundle         savedInstanceState;
    public    Context        mContext = null;
    protected LayoutInflater mInflater;
    Unbinder unbinder;

    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;

    /** 依附的Activity */
    protected Activity mActivity= null;
    private AlertDialog mAlertDialog;
    /** 类标签 */
    protected static String TAG = "";

    public String getFragmentName(){
        return TAG;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        TAG = this.getClass().getSimpleName();
        mContext  = activity;
        mActivity = activity;
//		LogUtils.i(getFragmentName() + " onAttach()");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mInflater = inflater;
        this.savedInstanceState=savedInstanceState;
        mView=mInflater.inflate(getContentResourseId(), null);
        unbinder= ButterKnife.bind(this,mView);
        init();
        return mView;
    }

    protected abstract void init();

    protected abstract int getContentResourseId();

    public void startActivity(Intent intent, boolean isNeedLogin){

        if (isNeedLogin) {
            Users user = EnjoyshopApplication.getInstance().getUser();
            if (user != null) {
                super.startActivity(intent);    //需要登录,切已经登录.直接跳到目标activity中
            } else {
                EnjoyshopApplication.getInstance().putIntent(intent);

                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(loginIntent, REQUEST_CODE);
            }
        } else {
            super.startActivity(intent);
        }
    }
    public void startActivityForResult(Intent intent,int requestCode, boolean isNeedLogin){

        if (isNeedLogin) {
            Users user = EnjoyshopApplication.getInstance().getUser();
            if (user != null) {
                super.startActivity(intent);    //需要登录,切已经登录.直接跳到目标activity中
            } else {
                EnjoyshopApplication.getInstance().putIntent(intent);
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                super.startActivity(loginIntent);
            }
        } else {
            super.startActivityForResult(intent,requestCode);
        }
    }

    /**
     * 请求权限
     *
     * 如果权限被拒绝过，则提示用户需要权限
     */
    protected void requestPermission(final String permission, String rationale, final int requestCode) {
        if (shouldShowRequestPermissionRationale(permission)) {
            showAlertDialog(getString(R.string.permission_title_rationale), rationale,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{permission}, requestCode);
                        }
                    }, getString(R.string.label_ok), null, getString(R.string.label_cancel));
        } else {
            requestPermissions(new String[]{permission}, requestCode);
        }
    }

    /**
     * 显示指定标题和信息的对话框
     *
     * @param title                         - 标题
     * @param message                       - 信息
     * @param onPositiveButtonClickListener - 肯定按钮监听
     * @param positiveText                  - 肯定按钮信息
     * @param onNegativeButtonClickListener - 否定按钮监听
     * @param negativeText                  - 否定按钮信息
     */
    protected void showAlertDialog(@Nullable String title, @Nullable String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText,
                                   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   @NonNull String negativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        mAlertDialog = builder.show();
    }


}
