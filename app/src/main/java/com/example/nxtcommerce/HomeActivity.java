package com.example.nxtcommerce;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nxtcommerce.util.Constant;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser currentUser;
    private DatabaseReference userRef;
    NavigationView navigationView;
    TextView tvNavName, tvNavEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
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
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        tvNavName = (TextView) headerView.findViewById(R.id.tvUserName);
        tvNavEmail = (TextView) headerView.findViewById(R.id.tvUserEmail);
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String userID = dataSnapshot.child("user_id").getValue(String.class);
                    sharedPrefStore.saveString(Constant.UID, userID);
                    //Log.d(TAG, "Name: " + userID);
                    // showToast(userID);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            tvNavEmail.setText(currentUser.getEmail());
            tvNavName.setText(currentUser.getDisplayName());
            //FirebaseMessaging.getInstance().subscribeToTopic(currentUser.getUid().toString());
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_order_history)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
    }

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
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        switch (item.getItemId()) {
            case R.id.nav_logout:
                // showToast("clicked");
                logOut();
                break;
            default:
                // Trigger the default action of replacing the current
                // screen with the one matching the MenuItem's ID
                NavigationUI.onNavDestinationSelected(item, navController);
        }

        //Close navigation drawer after menu item click
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut() {
        final BottomSheetDialog dialog = new BottomSheetDialog(HomeActivity.this);
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
        Intent intentLogin = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intentLogin);
    }
}