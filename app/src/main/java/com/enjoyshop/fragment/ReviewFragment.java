package com.enjoyshop.fragment;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.enjoyshop.R;
import com.enjoyshop.adapter.ReviewAdapter;
import com.enjoyshop.helper.JsonToObject;
import com.enjoyshop.utils.ToastUtils;
import com.wang.bean.Review;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import lombok.extern.java.Log;
import okhttp3.Call;

import static com.enjoyshop.contants.HttpContants.REVIEW_LIST;

@Log
public class ReviewFragment extends BaseFragment {

    private ReviewAdapter    reviewAdapter;
    private List<Review> reviewList;
    private Integer goods_id;
    private Map<String,Object> map = new HashMap<>();

    @BindView(R.id.recycler_view_review)
    RecyclerView mRecyclerview;

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        goods_id = bundle.getInt("goods_id");
        map.put("goods_id",goods_id+"");
        initReview(map);
    }

    private void initReview(Map map) {
        OkHttpUtils.post().url(REVIEW_LIST).params(map).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                android.util.Log.d(TAG, "error = " + e);
            }
            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jb = new JSONObject(response);
                    if (response != null&&Integer.parseInt(jb.get("code").toString())==1) {
                        JSONArray j = (JSONArray) jb.get("obj");
                        reviewList = JsonToObject.jsonToList(j,Review.class);
                        log.info("评论信息："+reviewList);
                        showReview();
                    } else {
                        ToastUtils.showSafeToast(getContext(), "暂无评论！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showReview() {
        if (reviewAdapter == null) {
            reviewAdapter = new ReviewAdapter(reviewList);
            mRecyclerview.setAdapter(reviewAdapter);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration
                    .HORIZONTAL));
        }


    }
    @Override
    protected int getContentResourseId () {
        return R.layout.fragment_review;
    }
}
