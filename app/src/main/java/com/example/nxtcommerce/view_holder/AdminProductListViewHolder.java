package com.example.nxtcommerce.view_holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nxtcommerce.Interface.ItemClickListener;
import com.example.nxtcommerce.R;

public class AdminProductListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView tvName, tvTotalPriceAmount, tvAddress, tvMobile;
    private ItemClickListener itemClickListener;

    public AdminProductListViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvCustomerName);
        tvTotalPriceAmount = itemView.findViewById(R.id.tvPrice);
        tvAddress = itemView.findViewById(R.id.tvAddress);
        tvMobile = itemView.findViewById(R.id.tvMobile);
    }


    @Override
    public void onClick(View view) {

    }
}
