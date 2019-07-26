package com.enjoyshop.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.activity.GoodsListActivity;
import com.enjoyshop.activity.SearchActivity;
import com.enjoyshop.adapter.HomeCatgoryAdapter;
import com.enjoyshop.bean.BannerBean;
import com.enjoyshop.bean.HomeCampaignBean;
import com.enjoyshop.contants.Contants;
import com.enjoyshop.contants.HttpContants;
import com.enjoyshop.helper.DividerItemDecortion;
import com.enjoyshop.widget.EnjoyshopToolBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;


/**
 * <pre>
 *     desc   : 首页fragment
 *     version: 1.1
 * </pre>
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;
    @BindView(R.id.recyclerview)
    RecyclerView     mRecyclerView;

    private Banner           mBanner;
    private HomeCatgoryAdapter mAdatper;
    private List<String>           images = new ArrayList<>();
    private List<String>           titles = new ArrayList<>();
    private List<HomeCampaignBean> datas  = new ArrayList<>();
    private Gson                   gson   = new Gson();
    View viewHeader;

    @Override
    protected void init() {

        viewHeader = LayoutInflater.from(getActivity()).inflate(R.layout
                .header_fragment_home, (ViewGroup) mRecyclerView.getParent(), false);
        mBanner = viewHeader.findViewById(R.id.banner);

        initView();
        requestBannerData();     //请求轮播图数据
        requestCampaignData();     //请求商品详情数据
    }

    @Override
    protected int getContentResourseId() {
        return R.layout.fragment_home;
    }


    @Override
    public void onStart() {
        super.onStart();
        //mBanner.startAutoPlay();
    }


    private void initView() {
        mToolBar.setOnClickListener(this);
        //设置banner样式
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());
    }


    /**
     * 轮播图数据
     */
    private void setBannerData() {
        //设置图片集合
        mBanner.setImages(images);
        //设置标题集合（当banner样式有显示title时）
        mBanner.setBannerTitles(titles);

        //设置指示器位置（当banner模式中有指示器时）
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.start();
    }


    /**
     * 首页商品数据
     */

    private Long defaultId = 0L;

    private void setRecyclerViewData() {

        for (int i = 0; i < datas.size(); i++) {
            if (i % 2 == 0) {
                //左边样式的item
                datas.get(i).setItemType(HomeCampaignBean.ITEM_TYPE_LEFT);
            } else {
                //右边样式的item
                datas.get(i).setItemType(HomeCampaignBean.ITEM_TYPE_RIGHT);
            }
        }

        mAdatper = new HomeCatgoryAdapter(datas);
        mRecyclerView.setAdapter(mAdatper);
        mRecyclerView.addItemDecoration(new DividerItemDecortion());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdatper.addHeaderView(viewHeader);

        mAdatper.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                HomeCampaignBean campaign = (HomeCampaignBean) adapter.getData().get(position);
                Intent intent = new Intent(getContext(), GoodsListActivity.class);
                intent.putExtra(Contants.COMPAINGAIN_ID, campaign.getId());
                startActivity(intent);
            }
        });

    }


    /**
     * 请求轮播图的数据
     */
    private void requestBannerData() {

        OkHttpUtils.get().url(HttpContants.HOME_BANNER_URL)
                .addParams("type", "1")
                .build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                Type collectionType = new TypeToken<Collection<BannerBean>>() {
                }.getType();
                Collection<BannerBean> enums = gson.fromJson(response, collectionType);
                Iterator<BannerBean> iterator = enums.iterator();
                while (iterator.hasNext()) {
                    BannerBean bean = iterator.next();
                    titles.add(bean.getName());
                    images.add(bean.getImgUrl());
                }

                setBannerData();
            }
        });
    }


    /**
     * 商品分类数据
     */
    private void requestCampaignData() {

        OkHttpUtils.get().url(HttpContants.HOME_CAMPAIGN_URL)
                .addParams("type", "1")
                .build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

                Type collectionType = new TypeToken<Collection<HomeCampaignBean>>() {
                }.getType();
                Collection<HomeCampaignBean> enums = gson.fromJson(response,
                        collectionType);
                Iterator<HomeCampaignBean> iterator = enums.iterator();
                while (iterator.hasNext()) {
                    HomeCampaignBean bean = iterator.next();
                    datas.add(bean);
                }

                setRecyclerViewData();
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        mBanner.stopAutoPlay();
    }

    //跳转到搜索界面
    @Override
    public void onClick(View v) {
        startActivity(new Intent(getContext(), SearchActivity.class));
    }

    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(EnjoyshopApplication.sContext).load(path).into(imageView);
        }
    }
}
