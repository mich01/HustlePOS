package com.mich01.hustlepos.UI.Setup;

import static com.mich01.hustlepos.Prefs.PrefsMgr.MyPrefs;
import static com.mich01.hustlepos.Prefs.PrefsMgr.MyPrefsEditor;
import static com.mich01.hustlepos.Prefs.PrefsMgr.getPrefs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.mich01.hustlepos.DB.DBManager;
import com.mich01.hustlepos.Modules.Alerts;
import com.mich01.hustlepos.R;
import com.mich01.hustlepos.UI.HomeActivity;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    Button RegisterUser;
    EditText EmailAddress, PhoneNumber, NewPassWord, ConfirmedPassword;

    private static void onErrorResponse(VolleyError error) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_register);
        RegisterUser = findViewById(R.id.cmd_register);
        EmailAddress = findViewById(R.id.txt_email);
        PhoneNumber = findViewById(R.id.txt_phone_no);
        NewPassWord = findViewById(R.id.txt_password);
        ConfirmedPassword = findViewById(R.id.txt_confirm_password);
        RegisterUser.setOnClickListener(v -> {
            if( TextUtils.isEmpty(EmailAddress.getText()))
            {
                EmailAddress.setError( "Email is required!" );
            }
            else if(TextUtils.isEmpty(PhoneNumber.getText()))
            {
                PhoneNumber.setError( "Phone Number is required!" );
            }
            else if(TextUtils.isEmpty(NewPassWord.getText()))
            {
                NewPassWord.setError( "Please enter a password!" );
            }
            else if(TextUtils.isEmpty(ConfirmedPassword.getText()))
            {
                ConfirmedPassword.setError( "Please confirm your password!" );
            }
            else if(!NewPassWord.getText().toString().equals(ConfirmedPassword.getText().toString()))
            {
                new Alerts().AlertUser(RegisterActivity.this, "Error!!","Your password entered an Confirmed Password do not match");
            }
            else
                {
                    RegisterUser(EmailAddress.getText().toString(),PhoneNumber.getText().toString(),NewPassWord.getText().toString());
                }
        });
    }

    @SuppressLint("HardwareIds")
    private void RegisterUser(String Email, String PhoneNumber, String PassWord) {
        completeSetup(PassWord,Email,PhoneNumber);
    }

    private void completeSetup(String PassWord, String Email, String PhoneNumber)
    {
        try {
            SharedPreferences preferences = MyPrefs = getPrefs(this);
            MyPrefsEditor = preferences.edit();
            MyPrefsEditor.putString("Pin",PassWord);
            MyPrefsEditor.putInt("SetupComplete",1);
            MyPrefsEditor.putString("PhoneNumber",PhoneNumber);
            MyPrefsEditor.putString("Email",Email);
            MyPrefsEditor.putLong("InstalledTimestamp", System.currentTimeMillis());
            MyPrefsEditor.putBoolean("Licensed", false);
            MyPrefsEditor.apply();
            MyPrefsEditor.commit();
            Intent i = new Intent(RegisterActivity.this, HomeActivity.class);
            DBManager DB = new DBManager(RegisterActivity.this);
            DB.getItem(0);
            startActivity(i);
            finish();
        } catch (Exception ignored) {}
    }
}