package com.example.nxtcommerce.fragment.order_history;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nxtcommerce.ProductDetailActivity;
import com.example.nxtcommerce.R;
import com.example.nxtcommerce.firebase_model.Cart;
import com.example.nxtcommerce.firebase_model.Orders;
import com.example.nxtcommerce.firebase_model.Products;
import com.example.nxtcommerce.fragment.BaseFragment;
import com.example.nxtcommerce.util.Constant;
import com.example.nxtcommerce.view_holder.OrderViewHolder;
import com.example.nxtcommerce.view_holder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class OrderHistoryFragment extends BaseFragment {
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    TextView tvMsg;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("orders");
        //databaseReference = FirebaseDatabase.getInstance().getReference().child("orders").child(baseActivity.sharedPrefStore.getString(Constant.UID));

        tvMsg = view.findViewById(R.id.tvMessage);
        recyclerView = view.findViewById(R.id.rvHistory);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

       /* FirebaseRecyclerOptions<Orders> options =
                new FirebaseRecyclerOptions.Builder<Orders>()
                        .setQuery(databaseReference, Orders.class)
                        .build();*/
        FirebaseRecyclerOptions<Orders> options =
                new FirebaseRecyclerOptions.Builder<Orders>()
                        .setQuery(databaseReference.child(baseActivity.sharedPrefStore.getString(Constant.UID)), Orders.class).build();

        FirebaseRecyclerAdapter<Orders, OrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<Orders, OrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull final Orders model) {
                        holder.tvDate.setText(model.getDate() + " at " + model.getTime());
                        holder.tvAddress.setText(model.getAddress());
                        holder.tvTotalPriceAmount.setText(model.getTotal() + "TK");
                        //holder.tvTotalPriceAmount.setText(model.getStatus());
                        // System.out.println("Data", model.getDate() + model.getAddress() + model.getTotalAmount());
                        Log.d("Tag", "model" + model.getDate() + model.getAddress() + model.getTotal() + model.getStatus());
                        tvMsg.setVisibility(View.GONE);
                    }

                    @NonNull
                    @Override
                    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item_layout, parent, false);
                        OrderViewHolder holder = new OrderViewHolder(view);
                        return holder;

                    }
                };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        return view;
    }
}