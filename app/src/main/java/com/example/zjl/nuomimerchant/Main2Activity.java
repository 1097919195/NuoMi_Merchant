package com.example.zjl.nuomimerchant;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.example.zjl.nuomimerchant.Adapter.MyItemOnClickListener;
import com.example.zjl.nuomimerchant.Adapter.OrderAdapter;
import com.example.zjl.nuomimerchant.common.http.NetWork;
import com.example.zjl.nuomimerchant.bean.MyResult;
import com.orhanobut.hawk.Hawk;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class Main2Activity extends RxAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView recycler_view;
    private SwipeRefreshLayout swipe;
    private OrderAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private CloudPushService cloudPushService;
    private static final String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "功能暂未完善", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        cloudPushService = PushServiceFactory.getCloudPushService();
        recycler_view = (RecyclerView) findViewById(R.id.order_recycler_view);
        swipe = (SwipeRefreshLayout)findViewById(R.id.order_swip);
        initData();
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Intent intent=new Intent(Main2Activity.this, AccountSettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Toast.makeText(this,"敬请期待",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_slideshow) {
            Intent intent=new Intent(Main2Activity.this, MessagePromptActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            Toast.makeText(this,"当前为最新版本",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
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
            Intent i=new Intent(Main2Activity.this, LoginActivity.class);
            startActivity(i);
            Main2Activity.this.finish();
            Hawk.clear();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        getNetData();
        super.onResume();
    }
}
