package com.mich01.hustlepos.UI.Setup;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mich01.hustlepos.DB.DBManager;
import com.mich01.hustlepos.Modules.BackendFunctions;
import com.mich01.hustlepos.Modules.MySingleton;
import com.mich01.hustlepos.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class BackupRestore extends AppCompatActivity
{
    Button LocalBackup, RemoteBackup;
    TextView BackupStatus;
    ProgressBar BackupProgress;
    public static String inFileName;
    private static void onErrorResponse(VolleyError error) {
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);
        BackupStatus = findViewById(R.id.text_info_status);
        BackupProgress = findViewById(R.id.upload_progress);
        RemoteBackup = findViewById(R.id.cmd_remote_backup);
        LocalBackup = findViewById(R.id.cmd_local_backup);
        BackendFunctions.verifyStoragePermissions(BackupRestore.this);
        inFileName = BackupRestore.this.getDatabasePath("mystore").toString();
        LocalBackup.setOnClickListener(v -> new BackendFunctions(BackupRestore.this).LocalBackup(new DBManager(BackupRestore.this),inFileName,this));
        RemoteBackup.setOnClickListener(v -> {
            try {
                OnlineBackup();
            } catch (Exception ignored) {}
        });
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Backup/Restore Data");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void OnlineBackup() throws NoSuchAlgorithmException, JSONException, IOException, NullPointerException {
        String url = "https://hustlepos.co.ke/hustlepos-otp/api/v1/otp/";
        String DeviceID =Settings.Secure.ANDROID_ID;
        JSONObject RequestJSON = new JSONObject();
        RequestJSON.put("PhoneNumber",new DBManager(this).backupOnline());
        RequestJSON.put("DeviceID",DeviceID );
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, RequestJSON, response -> {
                    try {
                        JSONObject Data = response.getJSONObject("data");
                    } catch (Exception ignored) {}
                }, BackupRestore::onErrorResponse);

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}