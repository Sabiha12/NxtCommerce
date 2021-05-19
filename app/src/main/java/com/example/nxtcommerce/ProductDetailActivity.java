package com.example.nxtcommerce;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.nxtcommerce.databinding.ActivityProductDetailBinding;
import com.example.nxtcommerce.firebase_model.Products;
import com.example.nxtcommerce.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProductDetailActivity extends BaseActivity {
    ActivityProductDetailBinding activityProductDetailBinding;
    //firebase
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String productID, user_id, productPrice, productQuantity, pName;
    private int totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProductDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail);

        productID = getIntent().getStringExtra(Constant.PID);
        user_id = sharedPrefStore.getString(Constant.UID);
        getProductDetails(productID);
        mAuth = FirebaseAuth.getInstance();

        init();
    }

    private void init() {
        setSupportActionBar(activityProductDetailBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activityProductDetailBinding.btnAddToCart.setOnClickListener(this);
        activityProductDetailBinding.btnConfirmOrder.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddToCart:
                addToCartList();
                break;
            case R.id.btnConfirmOrder:
                gotoConfirmOrderActivity();
                break;
        }
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

    private void getProductDetails(String productID) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("products");

        databaseReference.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Products products = snapshot.getValue(Products.class);
                    productPrice = String.valueOf(products.getPrice());
                    pName = products.getName();
                    activityProductDetailBinding.tvProductName.setText(pName);
                    activityProductDetailBinding.tvProductDesc.setText(getResources().getString(R.string.product_description_dot) + " " + products.getDescription());
                    activityProductDetailBinding.tvProductPrice.setText(productPrice + "TK");
                    Picasso.get().load(products.getImage()).into(activityProductDetailBinding.productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void gotoConfirmOrderActivity() {
        productQuantity = activityProductDetailBinding.btnCounter.getNumber();
        totalPrice = (Integer.parseInt(productPrice) * Integer.parseInt(productQuantity));
        Intent intent = new Intent(ProductDetailActivity.this, ConfirmOrderActivity.class);
        intent.putExtra(Constant.TOTAL_PRICE, String.valueOf(totalPrice));
        intent.putExtra(Constant.PID, productID);
        intent.putExtra(Constant.PQUANTITY, productQuantity);
        intent.putExtra(Constant.PNAME,pName);
        intent.putExtra(Constant.ACTIVITY, Constant.PRODUCT_DETAILS_ACTIVITY);
        startActivity(intent);
    }

    private void addToCartList() {
        startProgressDialog();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("cart");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", activityProductDetailBinding.tvProductName.getText().toString());
        cartMap.put("price", productPrice);
        cartMap.put("date", getCurrentDate());
        cartMap.put("time", getCurrentTime());
        cartMap.put("quantity", activityProductDetailBinding.btnCounter.getNumber());

        databaseReference.child(user_id).child("products")
                .child(String.valueOf(productID))
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showToast("Added to the cart list");
                            stopProgressDialog();
                            Intent intent = new Intent(ProductDetailActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            stopProgressDialog();
                            messageAlert("Error occurred : " + task.getException());
                        }
                    }
                });
    }

    private void messageAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailActivity.this);
        builder.setMessage(message);
        builder.setTitle("Message: ");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}