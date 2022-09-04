package com.mich01.hustlepos.Support;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mich01.hustlepos.R;

public class SupportActivity extends AppCompatActivity {
    WebView AppBrowser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        String Url = getIntent().getStringExtra("Url");
        String SupportActivity = getIntent().getStringExtra("Support");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(SupportActivity);
        actionBar.setDisplayHomeAsUpEnabled(true);
        AppBrowser = findViewById(R.id.support_view);
        AppBrowser.setWebViewClient(new WebViewClient());

        AppBrowser.loadUrl(Url);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if(AppBrowser.canGoBack())
        {
            AppBrowser.goBack();
        }
        else {
            super.onBackPressed();
        }
        finish();
    }
}