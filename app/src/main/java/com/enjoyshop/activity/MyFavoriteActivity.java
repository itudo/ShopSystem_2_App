package com.enjoyshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.adapter.HotGoodsAdapter;
import com.enjoyshop.helper.JsonToObject;
import com.enjoyshop.widget.EnjoyshopToolBar;
import com.wang.bean.Goods;
import com.wang.bean.GoodsDetail;
import com.wang.bean.Image;
import com.wang.bean.Users;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

import static com.enjoyshop.contants.HttpContants.GET_COLLECT_GOODS;

/**
 * Describe: 我的收藏
 */

public class MyFavoriteActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private Users user;
    private List<Goods> datas;
    private HotGoodsAdapter mAdatper;

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_myfavorite;
    }


    @Override
    protected void init() {
        user = EnjoyshopApplication.getInstance().getUser();
        initToolBar();
        initData();
    }

    private void initData() {
        OkHttpUtils.get().url(GET_COLLECT_GOODS)
                .addParams("user_name",user.getUser_name())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG,e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    JSONArray json = (JSONArray) jb.get("obj");
                    datas = JsonToObject.jsonToList(json,Goods.class);
                    for(Goods g : datas) {
                        JSONArray j = new JSONArray(g.getGoodsDetails());
                        List<GoodsDetail> goodsDetails = JsonToObject.jsonToList(j,GoodsDetail.class);
                        for(GoodsDetail gg:goodsDetails) {
                            JSONArray jj = new JSONArray(gg.getImages());
                            List<Image> images = JsonToObject.jsonToList(jj,Image.class);
                            gg.setImagess(images);
                        }
                        g.setGoodsDetail(goodsDetails);
                    }
                    showData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showData() {
        mAdatper = new HotGoodsAdapter(datas, MyFavoriteActivity.this);
        mRecyclerView.setAdapter(mAdatper);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MyFavoriteActivity.this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(MyFavoriteActivity.this,
                DividerItemDecoration.HORIZONTAL));

        mAdatper.setOnItemClickListener(new HotGoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //借助currPage 和pageSize 可以实现默认情况和刷新时,都可以使用
                Goods listBean = mAdatper.getDatas().get(position);
                Intent intent = new Intent(MyFavoriteActivity.this, GoodsDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Bundle bundle = new Bundle();
                bundle.putInt("goods_id",listBean.getGoods_id());
                bundle.putString("goods_name",listBean.getGoods_name());
                intent.putExtras(bundle);
                startActivity(intent);
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
                MyFavoriteActivity.this.finish();
            }
        });
    }

}
