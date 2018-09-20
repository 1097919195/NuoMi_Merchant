package com.example.zzn.nuomimerchant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.example.zzn.nuomimerchant.Adapter.MyItemOnClickListener;
import com.example.zzn.nuomimerchant.Adapter.OrderAdapter;
import com.example.zzn.nuomimerchant.application.MainApplication;
import com.example.zzn.nuomimerchant.common.http.NetWork;
import com.example.zzn.nuomimerchant.model.MyResult;
import com.orhanobut.hawk.Hawk;
import com.trello.rxlifecycle.components.RxActivity;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MainActivity extends RxActivity {
    private RecyclerView recycler_view;
    private SwipeRefreshLayout swipe;
    private OrderAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView back;
    private CloudPushService cloudPushService;
    private static final String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainApplication.setMainActivity(this);
        this.setContentView(R.layout.activity_main);
        cloudPushService = PushServiceFactory.getCloudPushService();
        recycler_view = (RecyclerView) findViewById(R.id.order_recycler_view);
        swipe = (SwipeRefreshLayout)findViewById(R.id.order_swip);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cloudPushService.unbindAccount(new CommonCallback() {
                    @Override
                    public void onSuccess(String s) {
                        Log.i(TAG,"unbind account success");
                    }

                    @Override
                    public void onFailed(String s, String s1) {
                        Log.i(TAG,"bind account failed." +
                                "errorCode: " + s + ", errorMsg:" + s1);
                    }
                });
                Intent i=new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                MainActivity.this.finish();
                Hawk.clear();
            }
        });
        initData();
    }
    @Override
    public void onResume() {
        Log.e("onResume","onResume");
        super.onResume();
    }

    private void initData() {
        layoutManager = new LinearLayoutManager(this);
        mAdapter = new OrderAdapter(this);
        recycler_view.setLayoutManager(new GridLayoutManager(recycler_view.getContext(), 6, GridLayoutManager.VERTICAL, false));
        recycler_view.setAdapter(mAdapter=new OrderAdapter(this));
        recycler_view.addOnScrollListener(mScrollListener);
        mAdapter.setItemOnClickListener(new MyItemOnClickListener() {
            @Override
            public void onItemOnClick(View view, String postion) {
//                Intent intent=new Intent(getActivity(), ShoppingCartActivity.class);
//                startActivity(intent);
            }
        });

        getNetData(); //刚进入界面先刷新一次
        //刷新时执行的事件
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNetData();
            }
        });
    }

    //RecyclerView向下滑动事件
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        int lastVisibleItem;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == layoutManager.getItemCount()) {
                getNetData();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            lastVisibleItem = findLastVisibleItemPosition();
        }
    };

    //查询最后一个可见Item的下标
    public int findLastVisibleItemPosition() {
        int lastVisibleItemPosition = 0;
        lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        return lastVisibleItemPosition;
    }


    private void getNetData() {
        Observable.combineLatest(NetWork.getMyApi().getOrder(), NetWork.getMyApi().getOrder(),
                new Func2<MyResult, MyResult, Void>() {
                    @Override
                    public Void call(MyResult PicResult, MyResult TextResult) {
                        mAdapter.setPicData(PicResult.getResults());
                        mAdapter.setTextData(TextResult.getResults());
                        return null;
                    }
                })
                .compose(this.<Void>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        mAdapter.notifyDataSetChanged();
                        swipe.setRefreshing(false);
                    }
                });
    }

}
