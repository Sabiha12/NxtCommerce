package com.example.nxtcommerce;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.nxtcommerce.databinding.ActivityAdminProductDetailsBinding;
import com.example.nxtcommerce.firebase_model.Cart;
import com.example.nxtcommerce.util.Constant;
import com.example.nxtcommerce.view_holder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminProductDetailsActivity extends BaseActivity {
    public ActivityAdminProductDetailsBinding activityAdminProductDetailsBinding;
    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    String userId, orderId, uidRemove;
    private int overTotalPrice = 0;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAdminProductDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_admin_product_details);
        mAuth = FirebaseAuth.getInstance();
        userId = getIntent().getStringExtra(Constant.UID);
        orderId = getIntent().getStringExtra(Constant.ORDERID);
        System.out.println("intent" + userId + orderId);
        // getProductDetails(userId, dateAndTime);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getProductDetails(userId, orderId);
    }

    private void getProductDetails(String userId, String orderId) {
        System.out.println("gotcha");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("orderedproduct");
      /*  String node = dateAndTime +"Id" + uidRemove;
        System.out.println("node"+node);*/

    /*    String node = dateAndTime +"Id" + uidRemove;
        System.out.println("node"+node);
        System.out.println("date" + dateAndTime);*/

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(databaseReference.child(userId).child(orderId).child("orderlist"), Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                holder.txtProductPrice.setText(model.getPrice() + "TK");
                holder.txtProductName.setText(model.getPname());
                holder.txtProductQuantity.setText(model.getQuantity());

                int totalPriceOfTotalPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                overTotalPrice = overTotalPrice + totalPriceOfTotalPrice;
                activityAdminProductDetailsBinding.tvTotalPrice.setVisibility(View.VISIBLE);
                activityAdminProductDetailsBinding.tvTotalPrice.setText("Total Price : " + String.valueOf(overTotalPrice) + "TK");
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        activityAdminProductDetailsBinding.rvAdminProductDetails.setAdapter(adapter);
        adapter.startListening();
    }

    private void init() {
        setSupportActionBar(activityAdminProductDetailsBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activityAdminProductDetailsBinding.rvAdminProductDetails.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        activityAdminProductDetailsBinding.rvAdminProductDetails.setLayoutManager(layoutManager);

        uidRemove = userId.substring(0, 4);
        System.out.println("customerId" + uidRemove);
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
}