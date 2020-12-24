
package com.example.intigritirobotics.ui.MyOrders;



import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.intigritirobotics.MainHomeActivity;
import com.example.intigritirobotics.MyOrderDetailActivity;
import com.example.intigritirobotics.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static com.example.intigritirobotics.MainHomeActivity.firebaseFirestore;

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
        String productID = myOrderModelList.get(position).getProductID();
        String productStatus = myOrderModelList.get(position).getProductStatus();

        viewHolder.setData(orderId,orderDate,productID, productStatus);

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

        private void  setData( String orderId, String orderDate, String productId, String productStatus)
        {

            MainHomeActivity.firebaseFirestore.document("PRODUCTS/"+productId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful())
                            {
                                DocumentSnapshot queryDocumentSnapshots = task.getResult();
                                Glide.with(itemView.getContext()).load(queryDocumentSnapshots.get("product_pic").toString().split(", ")[0]).apply(new RequestOptions().placeholder(R.drawable.category_icon)).into(pic);
                                  ProductTitle.setText(queryDocumentSnapshots.get("product title").toString());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                }
            });
            OrderDate.setText(orderDate);
            OrderId.setText(orderId);
            OrderStatus.setText(productStatus);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( itemView.getContext(), MyOrderDetailActivity.class);

                    MyOrderModel clickedOrder = myOrderModelList.get(getAdapterPosition());

                    intent.putExtra("date", clickedOrder.getOrderDate());
                    intent.putExtra("order id", clickedOrder.getOrderID());
                    intent.putExtra("productId", clickedOrder.getProductID());
                    intent.putExtra("status", clickedOrder.getProductStatus());

                    itemView.getContext().startActivity(intent);
                }
            });

        }


    }

}
