package com.enjoyshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.enjoyshop.EnjoyshopApplication;
import com.enjoyshop.R;
import com.enjoyshop.bean.Tab;
import com.enjoyshop.contants.HttpContants;
import com.enjoyshop.fragment.DetailFragment;
import com.enjoyshop.fragment.GoodsFragment;
import com.enjoyshop.fragment.ReviewFragment;
import com.enjoyshop.helper.SharePresenter;
import com.enjoyshop.utils.CartShopProvider;
import com.enjoyshop.utils.LogUtil;
import com.enjoyshop.utils.ToastUtils;
import com.enjoyshop.widget.EnjoyshopToolBar;
import com.enjoyshop.widget.FragmentTabHost;
import com.wang.bean.Address;
import com.wang.bean.Users;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;

/**
 * Describe: 商品详情
 */

public class GoodsDetailsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    EnjoyshopToolBar mToolBar;
    /*@BindView(R.id.webView)
    WebView      mWebView;*/

    private Integer goods_id;
    private Address address = new Address();
    //private WebAppInterface   mAppInterfce;
    private CartShopProvider  cartProvider;
    private LayoutInflater  mInflater;
    private FragmentTabHost mTabhost;

    private List<Tab> mTabs = new ArrayList<>();

    private Map<String,Object> map = new HashMap<>();

    private String goods_name;

    @Override
    protected int getContentResourseId() {
        return R.layout.activity_goods_detail;
    }

    @Override
    protected void init() {
        //接收数据
        Bundle bundle = getIntent().getExtras();
        goods_id = bundle.getInt("goods_id");
        goods_name = bundle.getString("goods_name");
        if (goods_id == null) {
            finish();
        }
        address = (Address) bundle.getSerializable("address");
        //cartProvider = new CartShopProvider(this);

        initToolBar();
        initTab();
        //initData();

    }
    private void initTab() {

        Tab tab_1 = new Tab(GoodsFragment.class, R.string.goods, R.drawable.selector_icon_home);
        Tab tab_2 = new Tab(DetailFragment.class, R.string.detail, R.drawable.selector_icon_hot);
        Tab tab_3 = new Tab(ReviewFragment.class, R.string.review, R.drawable.selector_icon_category);

        mTabs.add(tab_1);
        mTabs.add(tab_2);
        mTabs.add(tab_3);

        mInflater = LayoutInflater.from(this);
        mTabhost = (FragmentTabHost) this.findViewById(android.R.id.tabhost);
        mTabhost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        for (Tab tab : mTabs) {
            TabHost.TabSpec tabSpec = mTabhost.newTabSpec(getString(tab.getTitle()));
            tabSpec.setIndicator(buildIndicator(tab));
            Bundle bundle = new Bundle();
            bundle.putInt("goods_id",goods_id);
            if(address != null) {
                bundle.putSerializable("address",address);
            }
            mTabhost.addTab(tabSpec, tab.getFragment(), bundle);
        }


        mTabhost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        mTabhost.setCurrentTab(0);           //默认选中第0个

    }

    /**
     * 初始化标题栏
     */
    private void initToolBar() {

        mToolBar.setNavigationOnClickListener(this);
        mToolBar.setRightButtonText("分享");
        mToolBar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePresenter.getInstance().showShareDialogOnBottom
                        (0, GoodsDetailsActivity.this, goods_name,
                                goods_name, "0");
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar:
                    this.finish();
                    break;
            default:
                break;
        }
    }



    private View buildIndicator(Tab tab) {

        View view = mInflater.inflate(R.layout.tab_indicator, null);
        ImageView img = (ImageView) view.findViewById(R.id.icon_tab);
        TextView text = (TextView) view.findViewById(R.id.txt_indicator);

        img.setImageResource(tab.getIcon());
        text.setText(tab.getTitle());

        return view;
    }

    private void addToFavorite() {

        Users user = EnjoyshopApplication.getInstance().getUser();

        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class), true);
        }

        Integer userId = EnjoyshopApplication.getInstance().getUser().getUser_id();



        OkHttpUtils.post().url(HttpContants.FAVORITE_CREATE).build().execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtil.e("收藏", "收藏失败" + e, true);
            }

            @Override
            public void onResponse(String response, int id) {
                ToastUtils.showSafeToast(GoodsDetailsActivity.this,"已添加到收藏夹");
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if (mWebView != null) {
            mWebView.destroy();
        }*/
    }
}
