package com.example.nxtcommerce;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.nxtcommerce.databinding.ActivityConfirmOrderBinding;
import com.example.nxtcommerce.firebase_model.Cart;
import com.example.nxtcommerce.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfirmOrderActivity extends BaseActivity {
    ActivityConfirmOrderBinding activityConfirmOrderBinding;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser currentUser;
    private DatabaseReference userRef;
    private String totalAmount = "", orderNum = "0";
    String mobile, address, mobilePattern, productID, productQuantity, pName, currentDate, currentDateRemove, currentTime, currentTimeRemove, currentTimeRemoveFinal, uid, uidRemove, orderId;
    int activity = 0;
    List<Cart> orderedProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityConfirmOrderBinding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_order);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        setupFirebaseAuth();
        init();
    }

    private void init() {
        setSupportActionBar(activityConfirmOrderBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activityConfirmOrderBinding.btnConfirmOrder.setOnClickListener(this);

        totalAmount = getIntent().getStringExtra(Constant.TOTAL_PRICE);
        productID = getIntent().getStringExtra(Constant.PID);
        pName = getIntent().getStringExtra(Constant.PNAME);
        productQuantity = getIntent().getStringExtra(Constant.PQUANTITY);
        activity = getIntent().getIntExtra(Constant.ACTIVITY, 0); //0 = ProductDetailsActivity, 1 = CartActivity

        activityConfirmOrderBinding.tvTotalPriceAmount.setText(totalAmount + "TK");
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            activityConfirmOrderBinding.etName.setText(currentUser.getDisplayName());
            activityConfirmOrderBinding.etName.setEnabled(false);
            activityConfirmOrderBinding.etName.setTextColor(getResources().getColor(R.color.black));
        }
        activityConfirmOrderBinding.etMobileNumber.addTextChangedListener(textWatcher);
        activityConfirmOrderBinding.etAddress.addTextChangedListener(textWatcher);

        //showKeyboard(this);
        activityConfirmOrderBinding.etMobileNumber.requestFocus();

        currentDate = getCurrentDate();
        currentDateRemove = currentDate.replace("-","");
        currentTime = getCurrentTime();
        currentTimeRemove = currentTime.substring(0,currentTime.length()-3);
        currentTimeRemoveFinal = currentTimeRemove.replace(":","");
        uid = sharedPrefStore.getString(Constant.UID);
        uidRemove = uid.substring(0,4);

        orderId = currentDateRemove + "at" + currentTimeRemoveFinal + "Id"+ uidRemove;
        System.out.println("currentDatetime" + currentDateRemove + " " + currentTimeRemoveFinal + " " + uidRemove +" "+orderId);
    }

    private void setupFirebaseAuth() {
        Log.d("Tag", "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d("Tag", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("Tag", "onAuthStateChanged:signed_out");
                    Log.d("Tag", "onAuthStateChanged: Navigating back to Main screen");
                    Intent intent = new Intent(ConfirmOrderActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
    }

    /***
     * To remove error msg on text change in edit text
     * */
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mobile = (activityConfirmOrderBinding.etMobileNumber.getText().toString().trim());
            address = (activityConfirmOrderBinding.etAddress.getText().toString().trim());

            if (mobile.length() > 0) {
                activityConfirmOrderBinding.etMobileNumberInputLayout.setError(null);
            }
            if (address.length() > 0) {
                activityConfirmOrderBinding.etAddressInputLayout.setError(null);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnConfirmOrder:
                if (formValidation()) {
                    //showToast("checked");
                    confirmOrder();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
            hideSoftKeyboard();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void confirmOrder() {
        startProgressDialog();
/** (begin)
 * get the list of product in cart
 * */
        orderedProductList = new ArrayList<>();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference productsRef = rootRef.child("cart").child(sharedPrefStore.getString(Constant.UID)).child("products");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                               /* String product = ds.getKey();
                                Log.d("TAGProduct", product);*/
                    Cart cart = ds.getValue(Cart.class);
                    orderedProductList.add(cart);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        productsRef.addListenerForSingleValueEvent(eventListener);

        /**
         * get the list of product in cart
         * (end)
         * */

        /**
         * Insert/Update data into "orders"
         * */
      /*  final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("orders")
                .child("orderlist")
                .child(sharedPrefStore.getString(Constant.UID))
                .child("list")
                .child(getCurrentDate() + "at" + getCurrentTime());*/

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("orders")
                .child(sharedPrefStore.getString(Constant.UID))
                .child(currentDate + "at" + currentTime);
        final HashMap<String, Object> orderMap = new HashMap<>();

        orderMap.put("total", totalAmount);
        orderMap.put("name", activityConfirmOrderBinding.etName.getText().toString());
        orderMap.put("mobile", activityConfirmOrderBinding.etMobileNumber.getText().toString());
        orderMap.put("address", activityConfirmOrderBinding.etAddress.getText().toString());
        orderMap.put("date", currentDate);
        orderMap.put("time", currentTime);
        orderMap.put("status", "Not shipped");

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (activity == 1) {//for CartActivity

                    /**
                     *Insert/update data into "orderedproduct"
                     * */
              /*      final DatabaseReference orderedProductDref = FirebaseDatabase.getInstance().getReference()
                            .child("orderedproduct")
                            .child(sharedPrefStore.getString(Constant.UID))
                            .child(getCurrentDate() + "at" + getCurrentTime());*/

                 /*   final DatabaseReference orderedProductDref = FirebaseDatabase.getInstance().getReference()
                            .child("orderedproduct")
                            .child(sharedPrefStore.getString(Constant.UID))
                            .child(currentDateRemove + "at" + currentTimeRemoveFinal + "Id"+ uidRemove);*/

                    final DatabaseReference orderedProductDref = FirebaseDatabase.getInstance().getReference()
                            .child("orderedproduct")
                            .child(sharedPrefStore.getString(Constant.UID))
                            .child(orderId);

                    final HashMap<String, Object> orderProduct = new HashMap<>();

                    orderProduct.put("orderlist", orderedProductList);

                    orderedProductDref.updateChildren(orderProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            /*final DatabaseReference adminViewProductDref = FirebaseDatabase.getInstance().getReference()
                                    .child("adminview")
                                    .child(currentDateRemove + "at" + currentTimeRemoveFinal + "Id"+ uidRemove);*/

                            final DatabaseReference adminViewProductDref = FirebaseDatabase.getInstance().getReference()
                                    .child("adminview")
                                    .child(orderId);

                            final HashMap<String, Object> adminViewProduct = new HashMap<>();

                            adminViewProduct.put("total", totalAmount);
                            adminViewProduct.put("name", activityConfirmOrderBinding.etName.getText().toString());
                            adminViewProduct.put("mobile", activityConfirmOrderBinding.etMobileNumber.getText().toString());
                            adminViewProduct.put("address", activityConfirmOrderBinding.etAddress.getText().toString());
                            adminViewProduct.put("date", currentDate);
                            adminViewProduct.put("time", currentTime);
                            adminViewProduct.put("status", "Not shipped");
                            adminViewProduct.put("userid", sharedPrefStore.getString(Constant.UID));
                            adminViewProduct.put("orderid", orderId);

                            adminViewProductDref.updateChildren(adminViewProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    /**
                                     *Remove data from "cart"
                                     * */
                                    FirebaseDatabase.getInstance().getReference().child("cart")
                                            .child(sharedPrefStore.getString(Constant.UID))
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        stopProgressDialog();
                                                        showToast("Order placed successfully!!");
                                                        Intent intent = new Intent(ConfirmOrderActivity.this, HomeActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    });

                 /*   FirebaseDatabase.getInstance().getReference().child("cart")
                            .child(sharedPrefStore.getString(Constant.UID))
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        stopProgressDialog();
                                        showToast("Order placed successfully!!");
                                        Intent intent = new Intent(ConfirmOrderActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });*/
                } else {//for ProductDetailsActivity
                    /**
                     *Insert/update data into "adminview"
                     * */
               /*     final DatabaseReference adminViewProductDref = FirebaseDatabase.getInstance().getReference()
                            .child("adminview")
                            .child(currentDateRemove + "at" + currentTimeRemoveFinal + "Id"+ uidRemove);*/
            //  .child(currentDateRemove + "at" + currentTimeRemoveFinal + "Id"+ uidRemove);

                    final DatabaseReference adminViewProductDref = FirebaseDatabase.getInstance().getReference()
                            .child("adminview")
                            .child(orderId);
                    final HashMap<String, Object> adminViewProduct = new HashMap<>();

                    adminViewProduct.put("total", totalAmount);
                    adminViewProduct.put("name", activityConfirmOrderBinding.etName.getText().toString());
                    adminViewProduct.put("mobile", activityConfirmOrderBinding.etMobileNumber.getText().toString());
                    adminViewProduct.put("address", activityConfirmOrderBinding.etAddress.getText().toString());
                    adminViewProduct.put("date", currentDate);
                    adminViewProduct.put("time", currentTime);
                    adminViewProduct.put("status", "Not shipped");
                    adminViewProduct.put("userid", sharedPrefStore.getString(Constant.UID));
                    adminViewProduct.put("orderid", orderId);

                    adminViewProductDref.updateChildren(adminViewProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            /**
                             *Insert/update data into "orderedproduct"
                             * */
                         /*   final DatabaseReference orderedProductDref = FirebaseDatabase.getInstance().getReference()
                                    .child("orderedproduct")
                                    .child(sharedPrefStore.getString(Constant.UID))
                                    .child(getCurrentDate() + "at" + getCurrentTime())
                                    .child(orderNum);*/

                         /*   final DatabaseReference orderedProductDref = FirebaseDatabase.getInstance().getReference()
                                    .child("orderedproduct")
                                    .child(sharedPrefStore.getString(Constant.UID))
                                    .child(currentDateRemove + "at" + currentTimeRemoveFinal + "Id"+ uidRemove)
                                    .child("orderlist")
                                    .child(orderNum);*/

                            final DatabaseReference orderedProductDref = FirebaseDatabase.getInstance().getReference()
                                    .child("orderedproduct")
                                    .child(sharedPrefStore.getString(Constant.UID))
                                    .child(orderId)
                                    .child("orderlist")
                                    .child(orderNum);

                            final HashMap<String, Object> orderProduct = new HashMap<>();

                            orderProduct.put("date", getCurrentDate());
                            orderProduct.put("pid", productID);
                            orderProduct.put("pname", pName);
                            orderProduct.put("price", totalAmount);
                            orderProduct.put("quantity", productQuantity);
                            orderProduct.put("time", currentTime);

                            orderedProductDref.updateChildren(orderProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    stopProgressDialog();
                                    showToast("Order placed successfully!!");
                                    Intent intent = new Intent(ConfirmOrderActivity.this, HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    });


                }

            }
        });

    }

    private boolean formValidation() {
        mobile = activityConfirmOrderBinding.etMobileNumber.getText().toString();
        mobilePattern = "^(?:\\+?88)?01[13-9]\\d{8}$"; //accept +880********* or 0********* @[13-9] means 1 or 3 or 4 or 5 or 6 or 7 or 8 or 9

        if (activityConfirmOrderBinding.etMobileNumber.getText().toString().equals("")) {
            activityConfirmOrderBinding.etMobileNumberInputLayout.setError(getString(R.string.mobile_number_required));
            activityConfirmOrderBinding.etMobileNumber.requestFocus();
            return false;
        }
        if (!mobile.matches(mobilePattern) && mobile.length() > 0) {
            activityConfirmOrderBinding.etMobileNumberInputLayout.setError(getString(R.string.mobile_invalid));
            activityConfirmOrderBinding.etMobileNumber.requestFocus();
            return false;
        }
        if (activityConfirmOrderBinding.etAddress.getText().toString().equals("")) {
            activityConfirmOrderBinding.etAddressInputLayout.setError(getString(R.string.address_required));
            activityConfirmOrderBinding.etAddress.requestFocus();
            return false;
        }
        return true;
    }

}