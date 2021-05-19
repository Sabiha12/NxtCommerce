package com.example.nxtcommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.nxtcommerce.databinding.ActivityAdminHomeBinding;
import com.example.nxtcommerce.firebase_model.AdminViewOrderList;
import com.example.nxtcommerce.firebase_model.Cart;
import com.example.nxtcommerce.util.Constant;
import com.example.nxtcommerce.view_holder.AdminProductListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminHomeActivity extends BaseActivity {
    public ActivityAdminHomeBinding activityAdminHomeBinding;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    List<Cart> orderedProductList;
    private RecyclerView.LayoutManager layoutManager;
    String userId, dateAndTime, orderId, currentDate, currentDateRemove, currentTime, currentTimeRemove, currentTimeRemoveFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAdminHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_admin_home);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("admin");
        setupFirebaseAuth();
        init();
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
                    Log.d("Tag", "onAuthStateChanged: Navigating back to login screen");
                    Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
    }

    private void init() {
        setSupportActionBar(activityAdminHomeBinding.toolbar);
        activityAdminHomeBinding.rvAdminOrder.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        activityAdminHomeBinding.rvAdminOrder.setLayoutManager(layoutManager);
        activityAdminHomeBinding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_logout) {
                    // do something
                    logOut();
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        getDataFromDatabase();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin_logout, menu);
        return true;
    }

    private void getDataFromDatabase() {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("adminview");

        FirebaseRecyclerOptions<AdminViewOrderList> options =
                new FirebaseRecyclerOptions.Builder<AdminViewOrderList>()
                        .setQuery(databaseReference, AdminViewOrderList.class).build();

        FirebaseRecyclerAdapter<AdminViewOrderList, AdminProductListViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminViewOrderList, AdminProductListViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminProductListViewHolder holder, int position, @NonNull final AdminViewOrderList model) {
                        holder.tvName.setText(model.getName());
                        holder.tvMobile.setText(model.getMobile());
                        holder.tvAddress.setText(model.getAddress());
                        holder.tvTotalPriceAmount.setText(model.getTotal() + "TK");
                        activityAdminHomeBinding.tvMessage.setVisibility(View.GONE);
                        //  dateAndTime = model.getDate() + "at" + model.getTime();
                        Log.d("Tag", "model" + model.getName() + model.getAddress() + model.getTotal() + model.getStatus());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                userId = model.getUserid();
                                orderId = model.getOrderid();

                             /*   currentDate = model.getDate();
                                currentDateRemove = currentDate.replace("-", "");
                                currentTime = model.getTime();
                                currentTimeRemove = currentTime.substring(0, currentTime.length() - 3);
                                currentTimeRemoveFinal = currentTimeRemove.replace(":", "");
                                System.out.println("currentDatetime" + currentDateRemove + " " + currentTimeRemoveFinal);
                                dateAndTime = currentDateRemove + "at" + currentTimeRemoveFinal;*/

                                Intent intent = new Intent(AdminHomeActivity.this, AdminProductDetailsActivity.class);
                                intent.putExtra(Constant.UID, userId);
                                intent.putExtra(Constant.ORDERID, orderId);
                                startActivity(intent);
                                // showToast("clicked");
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminProductListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_product_view_layout, parent, false);
                        AdminProductListViewHolder holder = new AdminProductListViewHolder(view);
                        return holder;

                    }
                };
        adapter.startListening();
        activityAdminHomeBinding.rvAdminOrder.setAdapter(adapter);

     /*   FirebaseRecyclerAdapter<Orders, OrderViewHolder> adapter =
                new FirebaseRecyclerAdapter<Orders, OrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull final Orders model) {
                        holder.tvDate.setText(model.getDate() + " at " + model.getTime());
                        holder.tvAddress.setText(model.getAddress());
                        holder.tvTotalPriceAmount.setText(model.getTotal() + "TK");
                        Log.d("Tag", "model" + model.getDate() + model.getAddress() + model.getTotal() + model.getStatus());
                    }

                    @NonNull
                    @Override
                    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_product_view, parent, false);
                        OrderViewHolder holder = new OrderViewHolder(view);
                        return holder;

                    }
                };
        adapter.startListening();
        activityAdminHomeBinding.rvAdminOrder.setAdapter(adapter);*/
    }

    private void logOut() {
        final BottomSheetDialog dialog = new BottomSheetDialog(AdminHomeActivity.this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_confirmation_dialog, null);
        dialog.setContentView(view);
        dialog.show();
        TextView tvHeader = view.findViewById(R.id.tvHeader);
        tvHeader.setText("LogOut!!");
        TextView tvDesc = view.findViewById(R.id.tvDesc);
        tvDesc.setText("Do you wanna log out from application?");
        Button btnYes = (Button) view.findViewById(R.id.btnPositive);
        Button btnNo = (Button) view.findViewById(R.id.btnNegative);
        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Yes");
                finishAffinity();
                String email = sharedPrefStore.getString(Constant.EMAIL);
                if (email != null) {
                    sharedPrefStore.saveString(Constant.EMAIL, null);
                    mAuth.signOut();
                    gotoMainActivity();
                    dialog.dismiss();
                } else {
                    gotoMainActivity();
                    dialog.dismiss();
                }
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("No");
                dialog.dismiss();
            }
        });
    }

    private void gotoMainActivity() {
        Intent intentLogin = new Intent(AdminHomeActivity.this, MainActivity.class);
        startActivity(intentLogin);
    }
}