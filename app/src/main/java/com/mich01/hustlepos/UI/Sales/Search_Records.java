package com.mich01.hustlepos.UI.Sales;


import static com.mich01.hustlepos.Prefs.PrefsMgr.MyPrefs;
import static com.mich01.hustlepos.Prefs.PrefsMgr.getPrefs;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mich01.hustlepos.R;

import java.util.Calendar;

public class Search_Records extends AppCompatActivity {
    CalendarView StartDate,EndDate;
    Button SearchRecords;
    Long dateStart, dateEnd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_records);
        Calendar calendar = Calendar.getInstance();//get the current day
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Search Sales by Start and End dates");
        actionBar.setDisplayHomeAsUpEnabled(true);
        StartDate = findViewById(R.id.start_date);
        EndDate = findViewById(R.id.end_date);
        MyPrefs = getPrefs(this);
        StartDate.setMinDate(MyPrefs.getLong("InstalledTimestamp", 0));
        StartDate.setMaxDate(calendar.getTimeInMillis());
        EndDate.setMinDate(MyPrefs.getLong("InstalledTimestamp", 0));
        EndDate.setMaxDate(calendar.getTimeInMillis());
        SearchRecords = findViewById(R.id.cmd_advanced_search);
        dateStart = StartDate.getDate();
        dateEnd = EndDate.getDate();
        SearchRecords.setOnClickListener(v->searchRecords(dateStart,dateEnd));
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
    void searchRecords(Long StartDateStamp,Long EndDateStamp)
    {
        Intent i = new Intent(Search_Records.this, ViewTransactions.class);
        i.putExtra("StartDate",StartDateStamp);
        i.putExtra("EndDate",EndDateStamp);
        startActivity(i);
        finish();
    }
}