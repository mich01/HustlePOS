package com.mich01.hustlepos.UI.Sales;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mich01.hustlepos.Adapters.SalesAdapter;
import com.mich01.hustlepos.DB.DBManager;
import com.mich01.hustlepos.Data.SoldItems;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mich01.hustlepos.R;

public class ViewSales extends AppCompatActivity {
    ArrayList<SoldItems> AllTransactions = new ArrayList<>();
    TextView TotalSales, TotalProfits;
    SalesAdapter RecordedTransactions;
    ListView TransactionList;
    int Profit_Margin, Sales, Item_cost =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sales);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle("View Sales");
        actionBar.setDisplayHomeAsUpEnabled(true);
        TransactionList = findViewById(R.id.all_sales);
        TotalSales = findViewById(R.id.total_transactions);
        TotalProfits = findViewById(R.id.total_profits);
        FloatingActionButton fab = findViewById(R.id.close_button);
        fab.setOnClickListener(view -> finish());
        PopulateList(this);
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

            AllTransactions.clear();
            Cursor cur = new DBManager(context).getSales();
            if (cur.getCount() > 0)
            {
                while (cur.moveToNext())
                {
                    Sales=Sales+cur.getInt(cur.getColumnIndex("Price"));
                    Item_cost=Item_cost+(cur.getInt(cur.getColumnIndex("PurchasePrice"))*cur.getInt(cur.getColumnIndex("Quantity")));
                    AllTransactions.add(new SoldItems(cur.getInt(cur.getColumnIndex("ID")),
                            cur.getBlob(cur.getColumnIndex("img")),
                            cur.getString(cur.getColumnIndex("ItemName")),
                            cur.getInt(cur.getColumnIndex("Quantity")),
                            cur.getInt(cur.getColumnIndex("Price")),
                            cur.getString(cur.getColumnIndex("TransactionID")),
                            cur.getLong(cur.getColumnIndex("Timestamp"))));
                }
            }
            handler.post(() -> {
                Profit_Margin = Sales-Item_cost;
                String T_Sales = "Sales: "+Sales;
                String Profits = "Profits: "+Profit_Margin;
                TotalSales.setText(T_Sales);
                TotalProfits.setText(Profits);
                RecordedTransactions = new SalesAdapter(context,AllTransactions);
                TransactionList.setAdapter(RecordedTransactions);
            });
        });
    }
}