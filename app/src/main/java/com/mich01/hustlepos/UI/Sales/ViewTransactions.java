package com.mich01.hustlepos.UI.Sales;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mich01.hustlepos.Adapters.TransactionsAdapter;
import com.mich01.hustlepos.DB.DBManager;
import com.mich01.hustlepos.Data.SoldItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mich01.hustlepos.R;

public class ViewTransactions extends AppCompatActivity {
    ExpandableListView TransactionListView;
    ArrayList<String> TransactionIDs = new ArrayList<>();
    ArrayList<SoldItems> arrayList = new ArrayList<>();
    HashMap<String, ArrayList<SoldItems>> soldItems = new HashMap<>();
    TransactionsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transactions);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("My Sales");
        actionBar.setDisplayHomeAsUpEnabled(true);
        TransactionListView = findViewById(R.id.transaction_lists);
        TransactionIDs.clear();
        soldItems.clear();
        if(!(getIntent().getLongExtra("StartDate", 0) ==0) || !(getIntent().getLongExtra("EndDate", 0) ==0))
        {
            PopulateFilteredList(this,getIntent().getLongExtra("StartDate", 0),getIntent().getLongExtra("EndDate", 0));
        }
        else
        {
            PopulateList(this);
        }
        //PopulateList(this);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void PopulateList(Context context)
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Cursor TransactionCursor = new DBManager(this).getTransactions();
            if (TransactionCursor.getCount() > 0) {
                while (TransactionCursor.moveToNext()) {
                    TransactionIDs.add(TransactionCursor.getString(TransactionCursor.getColumnIndex("TransactionID")));
                }
            }

            for (int i=0;i<TransactionIDs.size();i++)
            {
                ArrayList<SoldItems> MyList = new ArrayList<>();
                arrayList.clear();
                Cursor cur = new DBManager(context).getSales();
                    if (cur.getCount() > 0)
                    {
                        while (cur.moveToNext())
                        {
                            if(TransactionIDs.get(i).trim().equals(cur.getString(cur.getColumnIndex("TransactionID")).trim()))
                            {
                                MyList.add(new SoldItems(cur.getInt(cur.getColumnIndex("ID")),
                                            cur.getBlob(cur.getColumnIndex("img")),
                                            cur.getString(cur.getColumnIndex("ItemName")),
                                            cur.getInt(cur.getColumnIndex("Quantity")),
                                            cur.getInt(cur.getColumnIndex("Price")),
                                            cur.getString(cur.getColumnIndex("TransactionID")),
                                            cur.getLong(cur.getColumnIndex("Timestamp"))));
                                }
                            }
                        }
                    soldItems.put(TransactionIDs.get(i), MyList);
            }
            handler.post(() -> {
                adapter = new TransactionsAdapter(TransactionIDs,soldItems);
                TransactionListView.setAdapter(adapter);
            });
        });
    }
    public void PopulateFilteredList(Context context, long StartDate, long EndDate)
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Cursor TransactionCursor = new DBManager(this).getTransactions();
            if (TransactionCursor.getCount() > 0) {
                while (TransactionCursor.moveToNext()) {
                    TransactionIDs.add(TransactionCursor.getString(TransactionCursor.getColumnIndex("TransactionID")));
                }
            }

            for (int i=0;i<TransactionIDs.size();i++)
            {
                ArrayList<SoldItems> MyList = new ArrayList<>();
                arrayList.clear();
                Cursor cur = new DBManager(context).getSales();
                if (cur.getCount() > 0)
                {
                    while (cur.moveToNext())
                    {
                        if(TransactionIDs.get(i).trim().equals(cur.getString(cur.getColumnIndex("TransactionID")).trim()))
                        {
                            MyList.add(new SoldItems(cur.getInt(cur.getColumnIndex("ID")),
                                    cur.getBlob(cur.getColumnIndex("img")),
                                    cur.getString(cur.getColumnIndex("ItemName")),
                                    cur.getInt(cur.getColumnIndex("Quantity")),
                                    cur.getInt(cur.getColumnIndex("Price")),
                                    cur.getString(cur.getColumnIndex("TransactionID")),
                                    cur.getLong(cur.getColumnIndex("Timestamp"))));
                        }
                    }
                }
                soldItems.put(TransactionIDs.get(i), MyList);
            }
            handler.post(() -> {
                adapter = new TransactionsAdapter(TransactionIDs,soldItems);
                TransactionListView.setAdapter(adapter);
            });
        });
    }
}