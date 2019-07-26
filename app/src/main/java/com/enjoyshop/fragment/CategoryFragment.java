package com.enjoyshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cjj.MaterialRefreshLayout;
import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.activity.GoodsDetailsActivity;
import com.enjoyshop.adapter.CategoryAdapter;
import com.enjoyshop.adapter.SecondGoodsAdapter;
import com.enjoyshop.bean.Weather;
import com.enjoyshop.contants.HttpContants;
import com.enjoyshop.helper.JsonToObject;
import com.enjoyshop.service.LocationService;
import com.enjoyshop.utils.ToastUtils;
import com.google.gson.Gson;
import com.sunfusheng.marqueeview.MarqueeView;
import com.wang.bean.Goods;
import com.wang.bean.GoodsDetail;
import com.wang.bean.Image;
import com.wang.bean.ThirdType;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import lombok.extern.java.Log;
import okhttp3.Call;

import static com.enjoyshop.EnjoyshopApplication.getApplication;
import static com.enjoyshop.contants.HttpContants.GOODS_LIST;
import static com.enjoyshop.contants.HttpContants.THIRD_TYPE;

/**
 * <pre>
 *     desc   : 分类fragment
 *     version: 1.0
 * </pre>
 */
@Log
public class CategoryFragment extends BaseFragment {

    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFREH = 1;
    private static final int STATE_MORE   = 2;
    private              int state        = STATE_NORMAL;       //正常情况

    @BindView(R.id.recyclerview_category)
    RecyclerView          mRecyclerView;
    @BindView(R.id.recyclerview_wares)
    RecyclerView          mRecyclerviewWares;
    @BindView(R.id.refresh_layout)
    MaterialRefreshLayout mRefreshLaout;
    @BindView(R.id.vf_hotmessage)
    MarqueeView           mVfHotMessage;
    @BindView(R.id.tv_city)
    TextView              mCityName;
    @BindView(R.id.tv_day_weather)
    TextView              mDayWeather;
    @BindView(R.id.tv_night_weather)
    TextView              mNightWeather;

    private Gson           mGson         = new Gson();
    private List<ThirdType> categoryFirst = new ArrayList<>();      //一级菜单
    private CategoryAdapter         mCategoryAdapter;                      //一级菜单
    private SecondGoodsAdapter      mSecondGoodsAdapter;              //二级菜单
    private List<Goods> datas;
    private List<String>            mVFMessagesList;                 //上下轮播的信息

    private String          provinceName;                                  //省份
    private String          cityName;                                      //城市名
    private String          dayWeather;
    private String          nightWeather;
    private LocationService locationService;

    private int currPage  = 1;     //当前是第几页
    private int totalPage = 1;    //一共有多少页
    private int pageSize  = 10;     //每页数目

    private Map<String,Object> map = new HashMap<>();

    private Gson gson = new Gson();


    @Override
    protected int getContentResourseId() {
        return R.layout.fragment_category;
    }


    @Override
    protected void init() {

        mVFMessagesList = new ArrayList<>();

        requestCategoryData();      // 热门商品数据
        requestMessageData();        //轮播信息数据
        getLocation();            //获取当前城市的位置

    }

    @Override
    public void onResume() {
        super.onResume();
        mVfHotMessage.startFlipping();
    }

    private void getLocation() {

        locationService = ((EnjoyshopApplication) getApplication()).locationService;
        locationService.registerListener(mListener);
        locationService.setLocationOption(locationService.getOption());
        locationService.start();// 定位SDK
    }


    private void requestCategoryData() {
        OkHttpUtils.get().url(THIRD_TYPE).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                android.util.Log.d(TAG, "error = " + e);
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    JSONArray json = (JSONArray) jb.get("obj");
                    categoryFirst = JsonToObject.jsonToList(json,ThirdType.class);
                    log.info("分类数据为："+categoryFirst);
                    showCategoryData();
                    defaultClick();
                }  catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void requestMessageData() {

        mVFMessagesList.add("开学季,凭录取通知书购手机6折起");
        mVFMessagesList.add("都世丽人内衣今晚20点最低10元开抢");
        mVFMessagesList.add("轻松购达3000元以上即送赠500元红包");
        mVFMessagesList.add("秋老虎到来,轻松购为您准备了这些必备衣物");
        mVFMessagesList.add("穿了幸福时光男装,帅呆呆,妹子马上来");

        if (!mVFMessagesList.isEmpty()) {
            mVfHotMessage.setVisibility(View.VISIBLE);
            mVfHotMessage.startWithList(mVFMessagesList);
        } else {
            mVfHotMessage.setVisibility(View.GONE);
        }

    }


    /**
     * 展示一级菜单数据
     */
    private boolean isclick = false;

    private void showCategoryData() {

        mCategoryAdapter = new CategoryAdapter(categoryFirst);

        mCategoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ThirdType thirdType = (ThirdType) adapter.getData().get(position);
                int id = thirdType.getThirdtype_id();
                String name = thirdType.getThirdtype_name();
                isclick = true;
                defaultClick();
                map.put("thirdtype_id",id+"");
                requestWares(map);
            }
        });


        mRecyclerView.setAdapter(mCategoryAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

    }


    private void defaultClick() {

        //默认选中第0个
        if (!isclick) {
            ThirdType category = categoryFirst.get(0);
            int id = category.getThirdtype_id();
            map.put("thirdtype_id",id+"");
            requestWares(map);
        }
    }


    /**
     * 服装数据
     */
    private void requestWares(Map map) {

                OkHttpUtils.post().url(GOODS_LIST).params(map).build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        android.util.Log.d(TAG, "error = " + e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jb = new JSONObject(response);
                            if (jb.get("obj").toString() != "null") {
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
                                }
                            } else {
                                datas = null;
                            }
                            showData(datas);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 展示二级菜单的数据
     * @param datas
     */
    private void    showData(List<Goods> datas) {
        log.info("服装信息:"+datas);
        switch (state) {
            case STATE_NORMAL:

                mSecondGoodsAdapter = new SecondGoodsAdapter(datas);
                mSecondGoodsAdapter.setOnItemClickListener(new BaseQuickAdapter
                        .OnItemClickListener() {

                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        Goods listBean = (Goods) adapter.getData().get(position);

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


                mRecyclerviewWares.setAdapter(mSecondGoodsAdapter);
                mRecyclerviewWares.setLayoutManager(new GridLayoutManager(getContext(), 2));
                mRecyclerviewWares.setItemAnimator(new DefaultItemAnimator());
                mRecyclerviewWares.addItemDecoration(new DividerItemDecoration(getContext(),
                        DividerItemDecoration.HORIZONTAL));
                break;
        }
    }


    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {

            if (null != location && location.getLocType() != BDLocation.TypeServerError) {

                cityName = location.getCity();
                provinceName = location.getProvince();
                if (cityName != null) {
                    mCityName.setText(cityName.substring(0, cityName.length() - 1));
                } else {
                    mCityName.setText("衡阳");
                }
                getCityWeather();
            } else {
                getCityWeather();
            }
        }

    };


    /**
     * 查询天气数据
     */
    private void getCityWeather() {

        String city;          //有可能查询不到,或者网络异常,所以默认查询城市为 湖南衡阳
        String province;

        if (cityName != null && provinceName != null) {
            city = cityName.substring(0, cityName.length() - 1);
            province = provinceName.substring(0, provinceName.length() - 1);
        } else {
            city = "衡阳";
            province = "湖南";
        }

        String url = HttpContants.requestWeather + "?key=201f8a7a91c30&city=" + city +
                "&province=" + province;

        OkHttpUtils.get().url(url).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Weather weather = mGson.fromJson(response, Weather.class);
                    List<Weather.ResultBean> result = weather.getResult();
                    //只有一个城市,所以只有一个数据
                    List<Weather.ResultBean.FutureBean> future = result.get(0).getFuture();
                    dayWeather = future.get(0).getDayTime();
                    nightWeather = future.get(0).getNight();
                    showWeather();
                } catch (Exception e) {
                    ToastUtils.showSafeToast(getContext(), e.getMessage());
                }
            }
        });
    }

    /**
     * 展示天气数据
     */
    private void showWeather() {
        mDayWeather.setText("白天: " + dayWeather);
        mNightWeather.setText("晚间: " + nightWeather);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
    }

    @Override
    public void onPause() {
        super.onPause();
        mVfHotMessage.stopFlipping();
    }
}



