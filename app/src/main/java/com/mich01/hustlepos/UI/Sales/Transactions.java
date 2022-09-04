package com.mich01.hustlepos.UI.Sales;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mich01.hustlepos.Adapters.Selected_Items_Adapter;
import com.mich01.hustlepos.Data.CartList;
import com.mich01.hustlepos.DB.DBManager;
import com.mich01.hustlepos.Data.Items;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mich01.hustlepos.R;

public class Transactions extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    static ListView MyItems;
    public static int CartItemsCount, TotalAmount=0;
    @SuppressLint("StaticFieldLeak")
    public static TextView Total_Items, Total_Amount;
    public static ArrayList<CartList> MyCartItems = new ArrayList<>();
    public static ArrayList<Items> AllItems = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static Selected_Items_Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyCartItems.clear();
        setContentView(R.layout.activity_transactions);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Select Items");
        actionBar.setDisplayHomeAsUpEnabled(true);
        MyItems = findViewById(R.id.bought_items_list);
        Total_Items = findViewById(R.id.total_items);
        Total_Amount = findViewById(R.id.total_cost);
        FloatingActionButton fab = findViewById(R.id.check_cart);
        fab.setImageResource(R.drawable.view_my_cart);
        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Total Items = "+MyCartItems.size(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            Intent i = new Intent(Transactions.this, Transaction_Checkout.class);
            startActivity(i);
        });
       PopulateList(this);
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items_menu, menu);
        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return false;
            }
        };
        menu.findItem(R.id.search_contact).setOnActionExpandListener(onActionExpandListener);
        SearchView searchView= (SearchView) menu.findItem(R.id.search_contact).getActionView();
        searchView.setQueryHint("Search Item..");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                FilterItems(query);
                return false;
            }
            public void FilterItems(String SearchString)
            {
                ArrayList<Items> FilteredItemsList = new ArrayList<>();
                for(int i=0;i<AllItems.size();i++)
                {
                    if(AllItems.get(i).getItemName().toLowerCase().contains(SearchString.toLowerCase()))
                    {
                        FilteredItemsList.add(AllItems.get(i));
                    }
                }
                adapter = new Selected_Items_Adapter(getApplicationContext(),FilteredItemsList);
                MyItems.setAdapter(adapter);
            }
            @Override
            public boolean onQueryTextChange(String query) {
                FilterItems(query);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    public static void PopulateList(Context context)
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            AllItems.clear();
            Cursor cur = new DBManager(context).getItems();
            if(cur.getCount()>0)
            {
                while (cur.moveToNext())
                {
                    if(Integer.parseInt(cur.getString(cur.getColumnIndex("Quantity")))>0)
                    {
                        AllItems.add(new Items(cur.getInt(cur.getColumnIndex("ID")),
                                cur.getBlob(cur.getColumnIndex("img")),
                                cur.getString(cur.getColumnIndex("ItemName")),
                                cur.getString(cur.getColumnIndex("PurchasePrice")),
                                cur.getString(cur.getColumnIndex("SellingPrice")),
                                cur.getString(cur.getColumnIndex("Quantity"))));
                    }
                }
            }
            handler.post(() -> {
                adapter = new Selected_Items_Adapter(context,AllItems);
                MyItems.setAdapter(adapter);
            });
        });
    }
}