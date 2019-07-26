package com.enjoyshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.enjoyshop.R;
import com.enjoyshop.adapter.HistorySearchAdapter;
import com.enjoyshop.adapter.HotSearchAdapter;
import com.enjoyshop.adapter.SearchAdapter;
import com.enjoyshop.helper.JsonToObject;
import com.enjoyshop.utils.LogUtil;
import com.enjoyshop.utils.PreferencesUtils;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.ClearEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wang.bean.Goods;
import com.wang.bean.GoodsDetail;
import com.wang.bean.Image;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

import static com.enjoyshop.contants.HttpContants.SEARCH_GOODS;

/**
 * Describe: 搜索界面
 */

public class SearchActivity extends BaseActivity {

    @BindView(R.id.edittxt_phone)
    ClearEditText mEditText;
    @BindView(R.id.listView)
    RecyclerView mListView;
    @BindView(R.id.hot_search_ry)
    RecyclerView  mHotSearchView;
    @BindView(R.id.history_search_ry)
    RecyclerView  mHistorySearchView;

    private List<String> hotSearchData;
    private List<String> historySearchData;

    private SearchAdapter mSearchaDapter;
    private HotSearchAdapter     mHotSearchAdapter;
    private HistorySearchAdapter mHistorySearchAdapter;
    private List<Goods> datas;


    @Override
    protected int getContentResourseId() {
        return R.layout.activity_search;
    }


    @Override
    protected void init() {
        hotSearchData = new ArrayList<>();
        historySearchData = new ArrayList<>();
        setHotSearchData();
        getHotSearchData();
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                search(editable.toString());
            }
        });
    }

    private void search(String s) {
        LogUtil.e("搜索",s,true);
        OkHttpUtils.get().url(SEARCH_GOODS)
                .addParams("search",s)
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
                    datas = JsonToObject.jsonToList(json, Goods.class);
                    for (Goods g : datas) {
                        JSONArray j = new JSONArray(g.getGoodsDetails());
                        List<GoodsDetail> goodsDetails = JsonToObject.jsonToList(j, GoodsDetail.class);
                        for (GoodsDetail gg : goodsDetails) {
                            JSONArray jj = new JSONArray(gg.getImages());
                            List<Image> images = JsonToObject.jsonToList(jj, Image.class);
                            gg.setImagess(images);
                        }
                        g.setGoodsDetail(goodsDetails);
                        showData();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showData() {
        LogUtil.e("搜索数据",datas.toString(),true);
        mSearchaDapter = new SearchAdapter(datas,SearchActivity.this);
        mListView.setAdapter(mSearchaDapter);
        mListView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.addItemDecoration(new DividerItemDecoration(SearchActivity.this,
                DividerItemDecoration.HORIZONTAL));
        mSearchaDapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //借助currPage 和pageSize 可以实现默认情况和刷新时,都可以使用
                Goods listBean = mSearchaDapter.getDatas().get(position);
                Intent intent = new Intent(SearchActivity.this, GoodsDetailsActivity.class);
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


    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        getHistorydata();
        setHistorySearchData();
    }

    /**
     * 热门搜索
     */
    private void getHotSearchData() {

        //TODO 真正开发这里的数据从后台获取

        hotSearchData.add("华为手机");
        hotSearchData.add("玫瑰花");
        hotSearchData.add("移动硬盘");
        hotSearchData.add("android高级进阶");
        hotSearchData.add("蚕丝被");
        mHotSearchAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化热门搜索相关的适配器及其信息
     */
    private void setHotSearchData() {
        mHotSearchAdapter = new HotSearchAdapter(hotSearchData);
        mHotSearchView.setAdapter(mHotSearchAdapter);
        mHotSearchView.setLayoutManager(new GridLayoutManager(this, 3));

        mHotSearchAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String content = (String) adapter.getData().get(position);
                doData(content);
            }
        });
    }

    /**
     * 初始化历史搜索相关的适配器及其信息
     */
    private void setHistorySearchData() {

        mHistorySearchAdapter = new HistorySearchAdapter(historySearchData);
        mHistorySearchView.setAdapter(mHistorySearchAdapter);
        mHistorySearchView.setLayoutManager(new GridLayoutManager(this, 3));

        mHistorySearchAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String content = (String) adapter.getData().get(position);
                doData(content);
            }
        });
    }


    @OnClick(R.id.gosearch)
    public void onViewClicked() {
        String content = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showSafeToast(SearchActivity.this,"还没输入您想搜索的宝贝呢");
            return;
        }
        doData(content);
    }


    /**
     * 历史搜索
     * 必须进行判断是不是本来就停留在这个界面进行操作,还是初始化进入
     * 要不然,会重复添加数据
     */
    private void getHistorydata() {

        String histortStr = PreferencesUtils.getString(SearchActivity.this, "histortStr");

        if (histortStr != null) {
            historySearchData = new Gson().fromJson(histortStr, new TypeToken<List<String>>() {
            }.getType());
        }

    }


    /**
     * 操作数据库数据
     */
    private int position = -1;

    private void doData(String content) {

        //有历史数据
        if (historySearchData != null && historySearchData.size() > 0) {
            for (int i = 0; i < historySearchData.size(); i++) {
                if (content.equals(historySearchData.get(i))) {
                    //有重复的
                    position = i;
                }
            }

            if (position != -1) {
                historySearchData.remove(position);
                historySearchData.add(0, content);
            } else {
                historySearchData.add(0, content);
            }

        } else {
            //没有历史数据
            historySearchData.add(content);
        }

        mHistorySearchAdapter.notifyDataSetChanged();
        String histortStr = new Gson().toJson(historySearchData);
        PreferencesUtils.putString(SearchActivity.this, "histortStr", histortStr);

        Bundle bundle = new Bundle();
        bundle.putString("search", content);
        Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);     //跳转到搜索结果界面

    }
}


