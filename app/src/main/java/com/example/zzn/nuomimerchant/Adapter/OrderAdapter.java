package com.example.zzn.nuomimerchant.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zzn.nuomimerchant.R;
import com.example.zzn.nuomimerchant.common.http.NetWork;
import com.example.zzn.nuomimerchant.model.MyResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by ZZN on 2017/8/31.
 */

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<MyResult.Results> picData = new ArrayList<>();
    private List<MyResult.Results> textData = new ArrayList<>();
    private MyItemOnClickListener mMyItemOnClickListener = null;
    private OrderInfoAdapter orderInfoAdapter;
    private String statestr;
    private LinearLayout merchantphone;
    //type
    public static final int TYPE_TYPE3 = 0xff03;


    public OrderAdapter(Context context) {
        this.context = context;
    }

    public List<MyResult.Results> getPicData() {
        return picData;
    }

    public void setPicData(List<MyResult.Results> picData) {
        this.picData = picData;
    }

    public List<MyResult.Results> getTextData() {
        return textData;
    }

    public void setTextData(List<MyResult.Results> textData) {
        this.textData = textData;
    }

    public void setItemOnClickListener(MyItemOnClickListener listener){
        mMyItemOnClickListener=listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HolderType(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orderinfo, parent, false));
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindType((HolderType) holder, position);
    }

    @Override
    public int getItemCount() {
        if (picData != null)
            return picData.size();
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_TYPE3;
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return gridManager.getSpanCount();
                }
            });
        }
    }

    private void bindType(HolderType holder, int position) {
        holder.setDataAndRefreshUI(picData.get(position), textData.get(position));
    }


    public class HolderType extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView orderinfo,time,price,state,order_merchant_name;
        private ImageView order_merchant_img;
        private String id;
        private RecyclerView recyclerView;
        private Button ok_btn,cancelbtn,finish;

        public HolderType(View itemView) {
            super(itemView);
            recyclerView=itemView.findViewById(R.id.order_info_rv);
            orderInfoAdapter=new OrderInfoAdapter();

            orderinfo = (TextView) itemView.findViewById(R.id.order_info);
            time=itemView.findViewById(R.id.order_time);
            price=itemView.findViewById(R.id.order_price);
            state=itemView.findViewById(R.id.order_state);
            order_merchant_img=itemView.findViewById(R.id.order_merchant_img);
            order_merchant_name=itemView.findViewById(R.id.order_merchant_name);
            ok_btn=itemView.findViewById(R.id.order_okbtn);

            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    state.setText("已接单");
                    ok_btn.setVisibility(View.GONE);
                    finish.setVisibility(View.VISIBLE);
                    statestr="已接单";
                    changeState(statestr,id);
                }
            });
            cancelbtn=itemView.findViewById(R.id.order_cancelbtn);

            cancelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    state.setText("已取消");
                    ok_btn.setVisibility(View.GONE);
                    cancelbtn.setVisibility(View.GONE);
                    statestr="已取消";
                    changeState(statestr,id);
                }
            });
            finish=itemView.findViewById(R.id.finish);
            finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelbtn.setVisibility(View.GONE);
                    finish.setVisibility(View.GONE);
                    state.setText("已完成");
                    statestr="已完成";
                    changeState(statestr,id);
                }
            });
            merchantphone=itemView.findViewById(R.id.merchantphone);
            merchantphone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context).setTitle("系统提示")//设置对话框标题

                            .setMessage("用户电话：18311112222")//设置显示的内容

                            .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮

                                @Override

                                public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                    // TODO Auto-generated method stub
                                }

                            }).show();//在按键响应事件中显示此对话框
                }
            });
            itemView.setOnClickListener(this);
        }

        public  void changeState(String state,String id) {
            Log.e("test",state+id);
            Call<MyResult> call;
            call = (Call<MyResult>) NetWork.getMyApi().getState(state,id);
            call.enqueue(new Callback<MyResult>() {
                @Override
                public void onResponse(Call<MyResult> call, retrofit2.Response<MyResult> response) {
                    Log.e("LoginBean",response.message());
                }
                @Override
                public void onFailure(Call<MyResult> call, Throwable t) {
                    System.out.println("请求失败");
                    System.out.print(t.getMessage());
                }
            });}


        public void setDataAndRefreshUI(MyResult.Results picdata, MyResult.Results textdata) {
            id=textdata.getId();
            if (textdata.getState().equals("未接单")){
                ok_btn.setVisibility(View.VISIBLE);
                cancelbtn.setVisibility(View.VISIBLE);

            }if (textdata.getState().equals("已接单")){
                cancelbtn.setVisibility(View.GONE);
                finish.setVisibility(View.VISIBLE);
            }if (textdata.getState().equals("已取消")){
                cancelbtn.setVisibility(View.GONE);
                finish.setVisibility(View.GONE);
                ok_btn.setVisibility(View.GONE);
            }if (textdata.getState().equals("已完成")){
                cancelbtn.setVisibility(View.GONE);
                finish.setVisibility(View.GONE);
                ok_btn.setVisibility(View.GONE);
            }
//            orderinfo.setText(textdata.getFood());
            time.setText("下单时间："+textdata.getTime());
            recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 3, GridLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(orderInfoAdapter);
            Log.e("orderInfoAdapter",textdata.getFood());
            orderInfoAdapter.setOrderinfo(textdata.getFood());
            price.setText("总价："+textdata.getPrice());
            state.setText(textdata.getState());
            order_merchant_name.setText(textdata.getName());
            Picasso.with(context)
                    .load(picdata.getImg())
                    .error(R.mipmap.home)
                    .fit()
                    .centerCrop()
                    .into(order_merchant_img);
        }

        @Override
        public void onClick(View view) {
            if(mMyItemOnClickListener!=null){
                mMyItemOnClickListener.onItemOnClick(view,id);
            }
        }
    }
}

