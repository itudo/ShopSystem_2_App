package com.enjoyshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.VolleyLog;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.enjoyshop.R;
import com.enjoyshop.activity.GoodsDetailsActivity;
import com.enjoyshop.activity.SearchActivity;
import com.enjoyshop.adapter.HotGoodsAdapter;
import com.enjoyshop.helper.JsonToObject;
import com.enjoyshop.utils.ToastUtils;
import com.google.gson.Gson;
import com.wang.bean.Goods;
import com.wang.bean.GoodsDetail;
import com.wang.bean.Image;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

import static com.enjoyshop.contants.HttpContants.GOODS_LIST;


/**
 * <pre>
 *     desc   : 热卖商品fragment
 *     version: 2.0
 * </pre>
 */
public class HotFragment extends BaseFragment {

    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFREH = 1;
    private static final int STATE_MORE   = 2;
    private              int state        = STATE_NORMAL;       //正常情况


    @BindView(R.id.recyclerview)
    RecyclerView          mRecyclerView;
    @BindView(R.id.refresh_view)
    MaterialRefreshLayout mRefreshLaout;

    private int  currPage  = 1;     //当前是第几页
    private int  totalPage = 1;    //一共有多少页
    private int  pageSize  = 10;     //每页数目
    private Gson mGson     = new Gson();
    private List<Goods> datas;
    private HotGoodsAdapter         mAdatper;


    @Override
    protected int getContentResourseId() {
        return R.layout.fragment_hot;
    }

    @Override
    protected void init() {
        initRefreshLayout();     //控件初始化
        getData();              //获取后台数据
    }


    //跳转到搜索界面
    @OnClick({R.id.toolbar})
    public void searchView(View view) {
        startActivity(new Intent(getContext(), SearchActivity.class));
    }


    private void initRefreshLayout() {

        mRefreshLaout.setLoadMore(true);
        mRefreshLaout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                if (currPage <= totalPage) {
                    loadMoreData();
                } else {
                    ToastUtils.showSafeToast(getContext(),"没有更多数据啦");
                    mRefreshLaout.finishRefreshLoadMore();
                }
            }
        });
    }

    /**
     * 加载更多
     */
    private void loadMoreData() {
        state = STATE_MORE;
        currPage = ++currPage;
        getData();
    }

    /**
     * 刷新
     */
    private void refreshData() {
        state = STATE_REFREH;
        currPage = 1;
        getData();
    }

    private void getData() {
        OkHttpUtils.post().url(GOODS_LIST).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                android.util.Log.d(VolleyLog.TAG, "error = " + e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if(jb.get("obj").toString()!="null") {
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
                    }

                }  catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 展示数据
     */
    private void showData() {
        switch (state) {
            case STATE_NORMAL:
                mAdatper = new HotGoodsAdapter(datas, getContext());
                mRecyclerView.setAdapter(mAdatper);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                        DividerItemDecoration.HORIZONTAL));
                break;
            case STATE_REFREH:
                mAdatper.clearData();
                mAdatper.addData(datas);
                mRecyclerView.scrollToPosition(0);
                mRefreshLaout.finishRefresh();
                break;
            case STATE_MORE:
                mAdatper.clearData();
                mAdatper.addData(mAdatper.getDatas().size(), datas);
                mRecyclerView.scrollToPosition(mAdatper.getDatas().size());
                mRefreshLaout.finishRefreshLoadMore();
                break;
        }


        mAdatper.setOnItemClickListener(new HotGoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //借助currPage 和pageSize 可以实现默认情况和刷新时,都可以使用
                Goods listBean = mAdatper.getDatas().get(position);
                Intent intent = new Intent(getContext(), GoodsDetailsActivity.class);
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
}
