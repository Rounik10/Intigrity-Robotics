package com.example.intigritirobotics.ui.MyCart;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.intigritirobotics.MainHomeActivity;
import com.example.intigritirobotics.ProductDetailActivity;
import com.example.intigritirobotics.R;
import com.example.intigritirobotics.ViewAllModel;

import java.util.List;

import static com.example.intigritirobotics.MainHomeActivity.currentUserUId;
import static com.example.intigritirobotics.MainHomeActivity.firebaseFirestore;
import static com.example.intigritirobotics.MainHomeActivity.loadingDialog;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {

    private List<ViewAllModel> ViewAllModelList;

    public MyCartAdapter(List<ViewAllModel> ViewAllModelList) {
        this.ViewAllModelList = ViewAllModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_item_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String index = ViewAllModelList.get(position).getId();
        String resource = ViewAllModelList.get(position).getImage();
        String title = ViewAllModelList.get(position).getTitle();
        int price = ViewAllModelList.get(position).getFinalPrice();
        float rating = ViewAllModelList.get(position).getTotalRating();
        viewHolder.setData(index,resource,title, price, rating);
    }

    @Override
    public int getItemCount() {
        return ViewAllModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView pic;
        private TextView CategoryTitle;
        private TextView ProductPrice;
        private TextView ProductRating;
        private LinearLayout delete_layout,qty_layout;
        private  TextView tv_qty;
        private Dialog qtyDialog;
        private TextView qtyEditText, qtyDialogOk,qtyDialogCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.product_preview_pic);
            CategoryTitle = itemView.findViewById(R.id.product_preview_title);
            ProductRating = itemView.findViewById(R.id.product_preview_rating);
            ProductPrice = itemView.findViewById(R.id.product_preview_price);
            delete_layout = itemView.findViewById(R.id.delete_from_cart);
            tv_qty = itemView.findViewById(R.id.cart_item_qty_text);
            qty_layout = itemView.findViewById(R.id.cart_item_qty_layout);

            qtyDialog = new Dialog(itemView.getContext());
            qtyDialog.setContentView(R.layout.qty_dialog);
            qtyDialog.setCancelable(true);
            qtyDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            qtyEditText =qtyDialog.findViewById(R.id.qty_edit_text);
            qtyDialogOk =qtyDialog.findViewById(R.id.qty_dialog_ok);
            qtyDialogCancel =qtyDialog.findViewById(R.id.qty_dialog_cancel);
            tv_qty.setText("2");

        }
        private void  setData( final String index, String resource, String title, int price, float rating)
        {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions()
                    .placeholder(R.drawable.category_icon))
                    .into(pic);
            CategoryTitle.setText(title);
            String priceText = "Rs." + price +"/-";
            String ratingText = ""+ rating;
            ProductPrice.setText(priceText);
            ProductRating.setText(ratingText);

            delete_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firebaseFirestore.document("USERS/"+currentUserUId+"/My Cart/"+index).delete().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            itemView.setVisibility(View.GONE);
                            Toast.makeText(itemView.getContext(),"Item Deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(itemView.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            qty_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qtyDialog.show();
                }
            });
            qtyDialogOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int qty = Integer.parseInt(qtyEditText.getText().toString());
                    if (qty<=10)

                    {
                        tv_qty.setText(qty);
                        qtyDialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(itemView.getContext(),"Max number is 10 !",Toast.LENGTH_LONG).show();
                    }

                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( itemView.getContext(), ProductDetailActivity.class);
                    intent.putExtra("Index",index);
                    intent.putExtra("Title",title);
                    intent.putExtra("ID", index);
                    intent.putExtra("Category ID","");
                    intent.putExtra("Price",""+price);
                    intent.putExtra("Rating",""+rating);
                    itemView.getContext().startActivity(intent);
                }
            });

        }


    }

}
