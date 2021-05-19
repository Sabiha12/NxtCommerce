package com.example.nxtcommerce.view_holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nxtcommerce.Interface.ItemClickListener;
import com.example.nxtcommerce.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductName, txtProductQuantity, txtProductPrice;
    private ItemClickListener itemClickListener;
    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txtProductName = itemView.findViewById(R.id.tvProductName);
        txtProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
        txtProductPrice = itemView.findViewById(R.id.tvProductPrice);
    }

    @Override
    public void onClick(View view) {

    }
}
