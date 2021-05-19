package com.example.nxtcommerce.view_holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nxtcommerce.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {
    public TextView tvDate, tvTotalPriceAmount, tvAddress;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        tvDate = itemView.findViewById(R.id.tvDate);
        tvTotalPriceAmount = itemView.findViewById(R.id.tvTotalPriceAmount);
        tvAddress = itemView.findViewById(R.id.tvAddress);
    }
}
