package com.example.nxtcommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.nxtcommerce.databinding.ActivityCartBinding;
import com.example.nxtcommerce.firebase_model.Cart;
import com.example.nxtcommerce.util.Constant;
import com.example.nxtcommerce.view_holder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CartActivity extends BaseActivity {
    ActivityCartBinding activityCartBinding;
    private RecyclerView.LayoutManager layoutManager;
    DatabaseReference databaseReference;

    private int overTotalPrice = 0;
    private String productID = "", productQuantity = "", pName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCartBinding = DataBindingUtil.setContentView(this, R.layout.activity_cart);
        init();
    }

    private void init() {
        setSupportActionBar(activityCartBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activityCartBinding.rvCartList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        activityCartBinding.rvCartList.setLayoutManager(layoutManager);
        activityCartBinding.btnNext.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNext:
                gotoConfirmOrderActivity();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        getDataFromDatabase();
    }

    private void getDataFromDatabase() {
        //overTotalPrice = 0;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("cart");

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(databaseReference.child(sharedPrefStore.getString(Constant.UID)).child("products"), Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                holder.txtProductPrice.setText(model.getPrice() + "TK");
                holder.txtProductName.setText(model.getPname());
                holder.txtProductQuantity.setText(model.getQuantity());

                int totalPriceOfTotalPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                overTotalPrice = overTotalPrice + totalPriceOfTotalPrice;
                activityCartBinding.tvMessage.setVisibility(View.GONE);
                activityCartBinding.tvTotalPrice.setVisibility(View.VISIBLE);
                activityCartBinding.btnNext.setVisibility(View.VISIBLE);
                activityCartBinding.tvTotalPrice.setText("Total Price : " + String.valueOf(overTotalPrice) + "TK");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String options[] = getResources().getStringArray(R.array.array_cart_options);
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                if (i == 0) {//edit
                                    Intent intent = new Intent(CartActivity.this, ProductDetailActivity.class);
                                    intent.putExtra(Constant.PID, model.getPid());
                                    startActivity(intent);
                                }
                                if (i == 1) {//remove
                                    databaseReference.child(sharedPrefStore.getString(Constant.UID))
                                            .child("products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        showToast("Item removed successfully");
                                                        Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                        startActivity(intent);
                                                        dialogInterface.dismiss();
                                                    }

                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        activityCartBinding.rvCartList.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        overTotalPrice = 0; //To re-set 0 before re-calculating product price
    }

    private void gotoConfirmOrderActivity() {
        Intent intent = new Intent(CartActivity.this, ConfirmOrderActivity.class);
        intent.putExtra(Constant.TOTAL_PRICE, String.valueOf(overTotalPrice));
        intent.putExtra(Constant.PID, productID);
        intent.putExtra(Constant.PQUANTITY, productQuantity);
        intent.putExtra(Constant.PNAME, pName);
        intent.putExtra(Constant.ACTIVITY, Constant.CART_ACTIVITY);
        startActivity(intent);
    }

}