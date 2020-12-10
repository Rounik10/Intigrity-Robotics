package com.example.intigritirobotics.ui.MyCart;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.intigritirobotics.ProductDetailActivity;
import com.example.intigritirobotics.R;
import com.example.intigritirobotics.ViewAllModel;
import java.util.List;
import static com.example.intigritirobotics.MainHomeActivity.currentUserUId;
import static com.example.intigritirobotics.MainHomeActivity.firebaseFirestore;
import static com.example.intigritirobotics.ui.MyCart.MyCartActivity.calculatePrice;
import static com.example.intigritirobotics.ui.MyCart.MyCartActivity.cartItemRecycler;
import static com.example.intigritirobotics.ui.MyCart.MyCartActivity.deleteItem;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {

    private final List<ViewAllModel> ViewAllModelList;

    public MyCartAdapter(List<ViewAllModel> ViewAllModelList) {
        this.ViewAllModelList = ViewAllModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.cart_item_layout,viewGroup,false);

        ViewHolder viewHolder = new ViewHolder(view);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position)
    {
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

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final ImageView pic;
        private final TextView CategoryTitle;
        private final TextView ProductPrice;
        private final TextView ProductRating;
        private  LinearLayout delete_layout, qtyLayout;
        private TextView qtyText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.product_preview_pic);
            CategoryTitle = itemView.findViewById(R.id.product_preview_title);
            ProductRating = itemView.findViewById(R.id.product_preview_rating);
            ProductPrice = itemView.findViewById(R.id.product_preview_price);
            delete_layout = itemView.findViewById(R.id.delete_from_cart);
            qtyLayout =itemView.findViewById(R.id.cart_item_qty_layout);
            qtyText =itemView.findViewById(R.id.cart_item_qty_text);
        }
        private void  setData( final String index, String resource, String title, int price, float rating) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions()
                    .placeholder(R.drawable.category_icon))
                    .into(pic);
            CategoryTitle.setText(title);
            String priceText = "Rs." + price + "/-";
            String ratingText = "" + rating;
            ProductPrice.setText(priceText);
            ProductRating.setText(ratingText);

            delete_layout.setOnClickListener(view -> {
                deleteItem(itemView, index, price);
            });

            qtyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog quantityDialog = new Dialog(itemView.getContext());
                    quantityDialog.setContentView(R.layout.qty_dialog);
                    quantityDialog.getWindow().setLayout( ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    quantityDialog.setCancelable(false);
                    EditText quantityNo = quantityDialog.findViewById(R.id.qty_edit_text);
                    Button cancelBtn = quantityDialog.findViewById(R.id.qty_dialog_cancel);
                    Button okBtn = quantityDialog.findViewById(R.id.qty_dialog_ok);
                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            quantityDialog.dismiss();
                        }
                    });

                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (quantityNo.getText().length()==0 || quantityNo.getText().equals("0")) {
                                    Toast.makeText(itemView.getContext(),"Invalid quantity!",Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    int quantity = Integer.parseInt(String.valueOf(quantityNo.getText()));
                                    if (quantity>=11) {
                                        Toast.makeText(itemView.getContext(),"Max 10 !",Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        qtyText.setText(quantityNo.getText());
                                        quantityDialog.dismiss();

                                        MyCartActivity.productList.get(getAdapterPosition()).setQuantity(quantity);

                                        calculatePrice();

                                    }
                                }

                            }
                        });
                        quantityDialog.show();
                }
            });
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), ProductDetailActivity.class);
                intent.putExtra("Index", index);
                intent.putExtra("Title", title);
                intent.putExtra("ID", index);
                intent.putExtra("Category ID", "");
                intent.putExtra("Price", "" + price);
                intent.putExtra("Rating", "" + rating);
                itemView.getContext().startActivity(intent);
            });

        }

    }

}