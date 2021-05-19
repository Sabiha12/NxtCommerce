package com.example.nxtcommerce;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nxtcommerce.util.SharedPrefStore;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    public LayoutInflater inflater;
    private Dialog progressDialog;
    private InputMethodManager inputMethodManager;
    public SharedPrefStore sharedPrefStore;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        (BaseActivity.this).overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        sharedPrefStore = new SharedPrefStore(this);
        inputMethodManager = (InputMethodManager) this.getSystemService(BaseActivity.INPUT_METHOD_SERVICE);

        progressDialog();
    }

    @Override
    public void onClick(View view) {

    }

    private void progressDialog() {
        progressDialog = new Dialog(this);
        View view = View.inflate(this, R.layout.progress_dialog, null);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(view);
        assert progressDialog.getWindow() != null;
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
    }


    public void startProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            try {
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * To encrypt password
     * */
    public static String getSha256Pass(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getCurrentDate() {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //2020-04-21
        String currentDate = sdf.format(calForDate.getTime());
        if (currentDate != null || !currentDate.equals(""))
            return currentDate;
        else
            return "";

    }

    public String getCurrentTime() {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss a"); //2020-04-21
        String currentTime = sdf.format(calForDate.getTime());
        if (currentTime != null || !currentTime.equals(""))
            return currentTime;
        else
            return "";

    }

    public void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public boolean hideSoftKeyboard() {
        try {
            if (getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) getSystemService(activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}

