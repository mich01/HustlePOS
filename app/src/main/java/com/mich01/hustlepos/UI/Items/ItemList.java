package com.mich01.hustlepos.UI.Items;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.mich01.hustlepos.Adapters.Items_Adapter;
import com.mich01.hustlepos.DB.DBManager;
import com.mich01.hustlepos.Data.Items;
import com.mich01.hustlepos.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ItemList extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static ListView MyItems;
    EditText SearchText;
    static ArrayList<Items> AllItems = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static Items_Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My Inventory List");
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },2);
            }
        }
        MyItems = findViewById(R.id.items_list);
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
        adapter = new Items_Adapter(getApplicationContext(),FilteredItemsList);
        MyItems.setAdapter(adapter);
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
        SearchText = findViewById(R.id.search_contact);
        searchView.setQueryHint("Search Item..");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                FilterItems(query);
                return false;
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
                    AllItems.add(new Items(cur.getInt(cur.getColumnIndex("ID")),
                            cur.getBlob(cur.getColumnIndex("img")),
                            cur.getString(cur.getColumnIndex("ItemName")),
                            cur.getString(cur.getColumnIndex("PurchasePrice")),
                            cur.getString(cur.getColumnIndex("SellingPrice")),
                            cur.getString(cur.getColumnIndex("Quantity"))));
                }
            }
            handler.post(() -> {
                adapter = new Items_Adapter(context,AllItems);
                MyItems.setAdapter(adapter);
            });
        });
    }
}