package com.example.nxtcommerce;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.nxtcommerce.databinding.ActivityLogInBinding;
import com.example.nxtcommerce.util.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LogInActivity extends BaseActivity {
    public ActivityLogInBinding activityLogInBinding;
    String name, email, password, emailPattern, hashPassword, userKey;
    int typesOfLogin;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLogInBinding = DataBindingUtil.setContentView(this, R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();
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
                }
            }
        };
    }

    private void init() {
        setSupportActionBar(activityLogInBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activityLogInBinding.btnLogin.setOnClickListener(this);
        activityLogInBinding.rlLogIn.setOnClickListener(this);
        activityLogInBinding.etEmail.addTextChangedListener(textWatcher);
        activityLogInBinding.etPassword.addTextChangedListener(textWatcher);

        typesOfLogin = getIntent().getIntExtra(Constant.LOGIN_TYPE, 0); // User = 2, Admin = 3

        if (typesOfLogin==2){
            activityLogInBinding.tvAppName.setText(getResources().getString(R.string.login));
        } else {
            activityLogInBinding.tvAppName.setText(getResources().getString(R.string.admin_login));
        }
        // showKeyboard(this);
        // activityLogInBinding.etEmail.requestFocus();
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
            email = (activityLogInBinding.etEmail.getText().toString().trim());
            password = (activityLogInBinding.etPassword.getText().toString().trim());

            if (email.length() > 0) {
                activityLogInBinding.etEmailInputLayout.setError(null);
            }
            if (password.length() >= 6) {
                activityLogInBinding.etPasswordInputLayout.setError(null);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            hideSoftKeyboard();
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                if (formValidation()) {
                    logIntoAccount();
                    //showToast("checked");
                }
                break;
        }
    }

    private void messageAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
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

    private void logIntoAccount() {
        startProgressDialog();
        email = activityLogInBinding.etEmail.getText().toString();
        password = activityLogInBinding.etPassword.getText().toString();
        hashPassword = getSha256Pass(password);

        // mref = new Firebase("https://tango-3a561.firebaseio.com/");

        if (typesOfLogin==2){
            logIn(email, hashPassword);
        } else {
            logIn(email, password);
        }

      /*  mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userKey = user.getUid();

        databaseReference.child("users").child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userID = dataSnapshot.child("user_id").getValue(String.class);
                sharedPrefStore.saveString(Constant.UID, userID);
                // Log.d(TAG, "Name: " + userID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/
    }

    private void logIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                if (mAuth.getCurrentUser() != null) {
              /*      userKey = mAuth.getCurrentUser().getUid();
                    databaseReference.child("users").child(userKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String userID = dataSnapshot.child("user_id").getValue(String.class);
                            sharedPrefStore.saveString(Constant.UID, userID);
                            // Log.d(TAG, "Name: " + userID);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });*/

                    sharedPrefStore.saveString(Constant.EMAIL, email);
                    sharedPrefStore.setInt(Constant.LOGIN_TYPE,typesOfLogin);
                    //sharedPrefStore.saveString(Constant.PASSWORD,password);
                    if (typesOfLogin == 2) {
                        gotoHomeActivity();

                    } else {
                        gotoAdminHomeActivity();
                    }
                    finish();


                } else {
                    messageAlert("Something went wrong");
                    mAuth.signOut();
                }
                stopProgressDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                stopProgressDialog();
                messageAlert("Error occurred : " + e.getMessage());
            }
        });
    }

    private void gotoAdminHomeActivity() {
        Intent intent = new Intent(LogInActivity.this, AdminHomeActivity.class);
        startActivity(intent);
    }

    private void gotoHomeActivity() {
        Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private boolean formValidation() {
        email = activityLogInBinding.etEmail.getText().toString();
        password = activityLogInBinding.etPassword.getText().toString();
        emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; //accept ex: abc@gmail.com

        if (activityLogInBinding.etEmail.getText().toString().equals("")) {
            activityLogInBinding.etEmailInputLayout.setError(getString(R.string.email_required));
            activityLogInBinding.etEmail.requestFocus();
            return false;
        }
        if (!email.matches(emailPattern) && email.length() > 0) {
            activityLogInBinding.etEmailInputLayout.setError(getString(R.string.email_invalid));
            activityLogInBinding.etEmail.requestFocus();
            return false;
        }
        if (activityLogInBinding.etPassword.getText().toString().equals("")) {
            activityLogInBinding.etPasswordInputLayout.setError(getString(R.string.pass_required));
            activityLogInBinding.etPassword.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            activityLogInBinding.etPasswordInputLayout.setError(getString(R.string.pass_length));
            activityLogInBinding.etPassword.requestFocus();
            return false;
        }
        return true;
    }

}