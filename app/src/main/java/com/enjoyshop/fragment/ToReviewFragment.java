package com.enjoyshop.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.activity.MyOrdersActivity;
import com.enjoyshop.contants.HttpContants;
import com.enjoyshop.utils.GlideUtils;
import com.enjoyshop.utils.LogUtil;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.ClearEditText;
import com.wang.bean.Attribute;
import com.wang.bean.OrderDetail;
import com.wang.bean.Users;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import basic.PictureSelectFragment;
import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.String.valueOf;

@lombok.extern.java.Log
public class ToReviewFragment extends PictureSelectFragment {

    private Users user;
    private OrderDetail orderDetail;
    private File file;
    private Map<String,Object> map = new HashMap<>();

    @BindView(R.id.review_goods)
    ImageView    review_goods;
    @BindView(R.id.review_name)
    TextView review_name;
    @BindView(R.id.review_attr)
    TextView review_attr;
    @BindView(R.id.review_price)
    TextView    review_price;
    @BindView(R.id.review_level)
    RatingBar review_level;
    @BindView(R.id.review_level2)
    TextView    review_level2;
    @BindView(R.id.review_context)
    ClearEditText review_context;
    @BindView(R.id.main_frag_picture_iv)
    ImageView mPictureIv;
    @BindView(R.id.go_review)
    Button toReview;

    private Float reviewlevel = 5f;
    private String imgReview = "";

    @Override
    protected void init() {
        user = EnjoyshopApplication.getInstance().getUser();
        orderDetail = (OrderDetail) getActivity().getIntent().getSerializableExtra("orderDetail");
        imgReview = "order_"+orderDetail.getOrderdetail_id()+"_review.jpeg";
        log.info("要评价的商品信息："+orderDetail);
        initEvents();
        initData();
    }

    private void review() {
        map.put("user_id", user.getUser_id()+"");
        map.put("level",reviewlevel+"");
        map.put("context",review_context.getText()+"");
        map.put("orderdetail_id",orderDetail.getOrderdetail_id()+"");
        map.put("order_id",orderDetail.getOrder_id());
        LogUtil.e("数据：",map.toString(),true);
        post_file(HttpContants.ADD_REVIEW,map,file);
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
            requestBody.addFormDataPart("file", file.getName(), body);
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
                    ToastUtils.showSafeToast(getContext(),"恭喜您，评论成功！");
                    back();
                } else {
                    Log.i("lfq" ,response.message() + " error : body " + response.body().string());
                }
            }
        });

    }

    private void back() {
        startActivity(new Intent(getActivity(), MyOrdersActivity.class), true);
    }

    private void initData() {
        GlideUtils.load(EnjoyshopApplication.sContext, orderDetail.getGoodsDetails().getImagess().get(0).getImage_path(),review_goods);
        review_name.setText(orderDetail.getGoods_name());
        String attr = "";
        for(Attribute a:orderDetail.getGoodsDetails().getAttributes()) {
            attr += a.getAttribute_name()+" ";
        }
        review_attr.setText(attr);
        review_price.setText("数量："+orderDetail.getGoods_count()+"  "+"价格："+orderDetail.getGoods_buyprice());
    }

    public void initEvents() {
        review_level.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                LogUtil.e("选择评价等级",rating+"",true);
                reviewlevel = rating;
                if(rating<=1.0) {
                    review_level2.setText("非常差");
                } else if(rating>1.0&&rating<=2.0) {
                    review_level2.setText("差");
                } else if(rating>2.0&&rating<=3.0) {
                    review_level2.setText("一般");
                } else if(rating>3.0&&rating<=4.0) {
                    review_level2.setText("好");
                } else if(rating>4.0&&rating<=5.0) {
                    review_level2.setText("非常好");
                }
            }
        });
        // 设置图片点击监听
        mPictureIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture();
            }
        });
        // 设置裁剪图片结果监听
        setOnPictureSelectedListener(new OnPictureSelectedListener() {
            @Override
            public void onPictureSelected(Uri fileUri, Bitmap bitmap) {
                mPictureIv.setImageBitmap(bitmap);

                String filePath = fileUri.getEncodedPath();
                String imagePath = Uri.decode(filePath);
                file = new File(imagePath);
                Toast.makeText(mContext, "图片已经保存到:" + imagePath, Toast.LENGTH_LONG).show();
            }
        });
        toReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                review();
            }
        });
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.fragment_toreview;
    }
}
