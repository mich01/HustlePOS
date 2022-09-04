package com.mich01.hustlepos.UI;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.mich01.hustlepos.R;

import java.util.Objects;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {
    EditText PassText;
    ImageView LockStatus;
    String EnteredPassword="";
    String AppPassword = "";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Executor executor;
        BiometricPrompt biometricPrompt;
        Objects.requireNonNull(getSupportActionBar()).hide();
        SharedPreferences preferences = getSharedPreferences("global", Context.MODE_PRIVATE);
        AppPassword = preferences.getString("Pin","0");
        setContentView(R.layout.activity_login);
        PassText = findViewById(R.id.user_pass);
        LockStatus = findViewById(R.id.lock_status);
        LockStatus.setImageResource(R.drawable.shield_locked);
        //Create the Biometric scanner here

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                LockStatus.setImageResource(R.drawable.shield_unlocked);
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Log in")
                .setSubtitle("You can also Unlock using your Fingerprint")
                .setNegativeButtonText("Use Key code")
                .build();
        biometricPrompt.authenticate(promptInfo);

        PassText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EnteredPassword = PassText.getText().toString();
                if(EnteredPassword.equals(AppPassword))
                {
                    LockStatus.setImageResource(R.drawable.shield_unlocked);
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}