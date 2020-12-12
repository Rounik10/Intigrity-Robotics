package com.example.intigritirobotics.ui.MyOrders;



import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.intigritirobotics.MyOrderDetailActivity;
import com.example.intigritirobotics.R;

import java.util.List;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {

    private List<MyOrderModel> myOrderModelList;

    public MyOrderAdapter(List<MyOrderModel> myOrderModelList) {
        this.myOrderModelList = myOrderModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_order_item_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String orderId = myOrderModelList.get(position).getOrderID();
        String orderDate = myOrderModelList.get(position).getOrderDate();
        String productTitle = myOrderModelList.get(position).getProductTitle();
        String productPic = myOrderModelList.get(position).getProductPic();
        String productStatus = myOrderModelList.get(position).getProductStatus();

        viewHolder.setData(orderId,orderDate,productTitle, productPic, productStatus);

    }

    @Override
    public int getItemCount() {
        return myOrderModelList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView pic;
        private TextView ProductTitle,OrderId, OrderDate,OrderStatus;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.my_order_item_layout_pic);
            ProductTitle = itemView.findViewById(R.id.my_order_item_layout_title);
            OrderDate = itemView.findViewById(R.id.my_order_item_layout_order_date);
            OrderId = itemView.findViewById(R.id.my_order_item_layout_order_id);
            OrderStatus = itemView.findViewById(R.id.my_order_item_layout_status);


        }

        private void  setData( String orderId, String orderDate, String productTitle,String  productPic, String productStatus)
        {
            Glide.with(itemView.getContext()).load(productPic).apply(new RequestOptions().placeholder(R.drawable.category_icon)).into(pic);
            ProductTitle.setText(productTitle);
            OrderDate.setText(orderDate);
            OrderId.setText(orderId);
            OrderStatus.setText(productStatus);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( itemView.getContext(), MyOrderDetailActivity.class);

                    itemView.getContext().startActivity(intent);
                }
            });

        }


    }

}
