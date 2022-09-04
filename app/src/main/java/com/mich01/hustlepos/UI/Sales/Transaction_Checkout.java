package com.mich01.hustlepos.UI.Sales;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mich01.hustlepos.Adapters.Shop_List_Adapter;
import com.mich01.hustlepos.DB.DBManager;
import com.mich01.hustlepos.Data.Items;
import com.mich01.hustlepos.Modules.BackendFunctions;
import com.mich01.hustlepos.UI.HomeActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mich01.hustlepos.R;

public class Transaction_Checkout extends AppCompatActivity
{
    public int ItemCount = 0;
    public int TotalCount=0;
    public static ArrayList<Items> AllItems = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static Shop_List_Adapter adapter;
    @SuppressLint("StaticFieldLeak")
    public static ListView MyItems;
    @SuppressLint("StaticFieldLeak")
    public static TextView Total_Items, Total_Amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_checkout);
        MyItems = findViewById(R.id.transaction_items_list);
        Total_Items = findViewById(R.id.total_items_selected);
        Total_Amount = findViewById(R.id.total_cost_of_items);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Select Items");
        actionBar.setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = findViewById(R.id.complete_transaction);
        fab.setOnClickListener(view ->
        {
            String TransactionID = new BackendFunctions(Transaction_Checkout.this).GenerateTransactionID();
            for(int i=0;i<Transactions.MyCartItems.size();i++)
            {
                        try {
                            JSONObject ItemJSON = new JSONObject();
                            ItemJSON.put("ItemID",Transactions.MyCartItems.get(i).getItemID());
                            ItemJSON.put("TransactionID",TransactionID);
                            ItemJSON.put("Price",Transactions.MyCartItems.get(i).getPrice());
                            ItemJSON.put("Quantity",Transactions.MyCartItems.get(i).getQuantity());
                            new DBManager(Transaction_Checkout.this).AddTransaction(ItemJSON);
                            new DBManager(Transaction_Checkout.this).UpdateStock(Integer.parseInt(AllItems.get(i).getQuantity()),AllItems.get(i).getID());
                        } catch (Exception ignored) {}
            }
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.complete);
            mp.start();
            Toast.makeText(Transaction_Checkout.this, "Transaction Completed ", Toast.LENGTH_LONG).show();
            AllItems.clear();
            Transactions.PopulateList(view.getRootView().getContext());
            Transactions.adapter.notifyDataSetChanged();
            Transactions.MyCartItems.clear();
            adapter.notifyDataSetChanged();
            HomeActivity.PopulateFigures(Transaction_Checkout.this);
            finish();
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
        Objects.requireNonNull(getSupportActionBar()).setTitle("Selected Items");
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
        return super.onCreateOptionsMenu(menu);
    }
    public void PopulateList(Context context)
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            AllItems.clear();
            for(int i=0;i<Transactions.MyCartItems.size();i++)
            {
                Cursor cur = new DBManager(context).getItem(Transactions.MyCartItems.get(i).getItemID());
                if (cur.getCount() > 0)
                {
                    while (cur.moveToNext()) {
                        AllItems.add(new Items(cur.getInt(cur.getColumnIndex("ID")),
                                cur.getBlob(cur.getColumnIndex("img")),
                                cur.getString(cur.getColumnIndex("ItemName")),
                                cur.getString(cur.getColumnIndex("PurchasePrice")),
                                String.valueOf(Transactions.MyCartItems.get(i).getPrice()),
                                String.valueOf(Transactions.MyCartItems.get(i).getQuantity())));
                        ItemCount=ItemCount+Transactions.MyCartItems.get(i).getQuantity();
                        TotalCount=TotalCount+(Transactions.MyCartItems.get(i).getPrice()*Transactions.MyCartItems.get(i).getQuantity());
                    }
                }
            }
            handler.post(() -> {
                Toast.makeText(context, "You have added "+Transactions.MyCartItems.size()+" Items",Toast.LENGTH_LONG).show();
                String TotalItems ="Total Items: "+ItemCount;
                String TotalAmount ="Total Amount: "+TotalCount;
                Total_Items.setText(TotalItems);
                Total_Amount.setText(TotalAmount);
                adapter = new Shop_List_Adapter(context,AllItems);
                MyItems.setAdapter(adapter);
            });
        });
    }
}