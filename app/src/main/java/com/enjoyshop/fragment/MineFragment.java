package com.enjoyshop.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.activity.AddressListActivity;
import com.enjoyshop.activity.LoginActivity;
import com.enjoyshop.activity.MyCenterActivity;
import com.enjoyshop.activity.MyFavoriteActivity;
import com.enjoyshop.activity.MyOrdersActivity;
import com.enjoyshop.utils.GlideUtils;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.CircleImageView;
import com.wang.bean.Users;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import basic.PictureSelectFragment;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.enjoyshop.contants.Contants.REQUEST_CODE;
import static com.enjoyshop.contants.HttpContants.UPDATE_USER_HEAD;
import static java.lang.String.valueOf;


/**
 * <pre>
 *     desc   : 我的 fragment
 *     version: 1.0
 * </pre>
 */
public class MineFragment extends PictureSelectFragment {

    @BindView(R.id.img_head)
    CircleImageView mImageHead;
    @BindView(R.id.txt_username)
    TextView        mTxtUserName;
    @BindView(R.id.btn_logout)
    Button          mbtnLogout;

    private Handler handler;

    private File file;
    private Map<String,Object> map = new HashMap<>();
    private Users user;
    private String imgHead = "";
    private Uri uri;


    @Override
    protected void init() {
        showUser();
        // 设置裁剪图片结果监听
        setOnPictureSelectedListener(new OnPictureSelectedListener() {
            @Override
            public void onPictureSelected(Uri fileUri, Bitmap bitmap) {
                uri = fileUri;
                sendMsg(110,bitmap);
            }
        });
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 110:
                        refrash((Bitmap)msg.obj);
                        break;
                }
            }
        };
    }

    private void refrash(Bitmap bitmap) {
        mImageHead.setImageBitmap(bitmap);

        String filePath = uri.getEncodedPath();
        String imagePath = Uri.decode(filePath);
        file = new File(imagePath);
        Toast.makeText(mContext, "图片已经保存到:" + imagePath, Toast.LENGTH_LONG).show();
        map.put("user_id",user.getUser_id()+"");
        post_file(UPDATE_USER_HEAD,map,file);
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.fragment_mine;
    }


    @OnClick({R.id.txt_my_center,R.id.txt_my_address, R.id.txt_my_favorite, R.id.txt_my_orders,  R
            .id.img_head, R.id.btn_logout})
    public void onclick(View view) {
        switch (view.getId()) {
            //个人中心
            case R.id.txt_my_center:
                Intent intent3 = new Intent(getActivity(), MyCenterActivity.class);
                startActivity(intent3,true);
                break;
            //收货地址
            case R.id.txt_my_address:
                startActivity(new Intent(getActivity(), AddressListActivity.class), true);
                break;
            //我的收藏
            case R.id.txt_my_favorite:
                startActivity(new Intent(getActivity(), MyFavoriteActivity.class), true);
                break;
            //我的订单
            case R.id.txt_my_orders:
                startActivity(new Intent(getActivity(), MyOrdersActivity.class), true);
                break;
            case R.id.img_head:
                user = EnjoyshopApplication.getInstance().getUser();
                if (user == null) {
                    Intent intent2 = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent2, REQUEST_CODE);
                } else {
                    selectPicture();
                }
                break;
            case R.id.btn_logout:
                EnjoyshopApplication.getInstance().clearUser();
                showUser();
                break;
            default:
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showUser();

    }

    private void sendMsg(int what,Object msg){
        Log.e("msg",msg.toString());
        handler.obtainMessage(what, msg).sendToTarget();
    }

    private void showUser() {
        user = EnjoyshopApplication.getInstance().getUser();
        if (user != null) {
            imgHead = "user_"+user.getUser_id()+"_head.jpeg";
            mTxtUserName.setText(user.getUser_name()+" | "+user.getUser_tel());
            mbtnLogout.setText("退出登录");
            GlideUtils.portrait(getContext(), user.getUser_head(), mImageHead);
        } else {
            mTxtUserName.setText("请登陆");
            mbtnLogout.setText("请登陆");
            GlideUtils.portrait(getContext(), null, mImageHead);
        }
    }

    protected void post_file(final String url, final Map<String, Object> map, File file) {
        OkHttpClient client = new OkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(file != null){
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("*"), file);
            String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("file",imgHead , body);
        }
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
            }
        }
        Request request = new Request.Builder().url(url).post(requestBody.build()).tag(mContext).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(10000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("lfq" ,"onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String str = response.body().string();
                    ToastUtils.showSafeToast(getContext(),"恭喜您，头像更换成功！");
                    user.setUser_head(imgHead);
                    EnjoyshopApplication.getInstance().clearUser();
                    EnjoyshopApplication.getInstance().putUser(user,"12345678asfghdssa");
                    //sendMsg(110,user);
                } else {
                    Log.i("lfq" ,response.message() + " error : body " + response.body().string());
                }
            }
        });

    }
}
