package com.example.nxtcommerce;

import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.nxtcommerce.databinding.ActivityMainBinding;
import com.example.nxtcommerce.util.Constant;

public class MainActivity extends BaseActivity {
    ActivityMainBinding activityMainBinding;
    String email;
    int typesOfLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        init();
        email = sharedPrefStore.getString(Constant.EMAIL);
        typesOfLogin = sharedPrefStore.getInt(Constant.LOGIN_TYPE);

        if (email != null && typesOfLogin == Constant.FOR_USER_LOGIN) {//user = 2
            gotoHomeActivity();
        } else if(email != null && typesOfLogin == Constant.FOR_ADMIN_LOGIN){//admin = 3
            gotoAdminHomeActivity();
        }

        // sharedPrefStore.getString(Constant.PASSWORD);
        // hideSoftKeyboard();
    }

    private void init() {
        activityMainBinding.btnLogin.setOnClickListener(this);
        activityMainBinding.btnSignup.setOnClickListener(this);
        activityMainBinding.tvAdminLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                goToLogInActivity();
                break;
            case R.id.btnSignup:
                goToSignUpActivity();
                break;
            case R.id.tvAdminLogin:
                gotoAdminLoginActivity();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //hideSoftKeyboard();
        //hideSoftwareKeyboard(MainActivity.this);
    }

    private void gotoHomeActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void gotoAdminHomeActivity() {
        Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
        startActivity(intent);
    }

    private void goToLogInActivity() {
        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
        intent.putExtra(Constant.LOGIN_TYPE,Constant.FOR_USER_LOGIN); //2
        startActivity(intent);
    }

    private void gotoAdminLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
        intent.putExtra(Constant.LOGIN_TYPE,Constant.FOR_ADMIN_LOGIN); //3
        startActivity(intent);
    }

    private void goToSignUpActivity() {
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

}