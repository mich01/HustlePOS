package com.mich01.hustlepos;


import static com.mich01.hustlepos.Prefs.PrefsMgr.MyPrefs;
import static com.mich01.hustlepos.Prefs.PrefsMgr.getPrefs;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.mich01.hustlepos.Modules.BackendFunctions;
import com.mich01.hustlepos.UI.LoginActivity;
import com.mich01.hustlepos.UI.Setup.RegisterActivity;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity
{
    Handler h = new Handler();
    int First_Run = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Objects.requireNonNull(getSupportActionBar()).hide();
            if (!BackendFunctions.CheckRoot())
            {
                MyPrefs = getPrefs(this);
                if (MyPrefs.getLong("InstalledTimestamp", 0) == 0)
                {
                    SharedPreferences.Editor PrefEditor;
                    PrefEditor = MyPrefs.edit();
                    PrefEditor.putLong("InstalledTimestamp", System.currentTimeMillis());
                    PrefEditor.apply();
                }
                h.postDelayed(() -> {
                    First_Run = MyPrefs.getInt("SetupComplete", 0);
                    Intent i;
                    if (First_Run == 0)
                    {
                        i = new Intent(SplashActivity.this, RegisterActivity.class);

                    } else {
                        i = new Intent(SplashActivity.this, LoginActivity.class);
                    }
                    startActivity(i);
                    finish();
                }, 1000);
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setIcon(R.drawable.ic_stop_no_action);
                alertDialog.setMessage("App Wont run on rooted Devices");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "EXIT",
                        (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        });
                alertDialog.show();
            }
        }
}