package com.mich01.hustlepos.UI;

import static com.mich01.hustlepos.Prefs.PrefsMgr.MyPrefs;
import static com.mich01.hustlepos.Prefs.PrefsMgr.MyPrefsEditor;
import static com.mich01.hustlepos.Prefs.PrefsMgr.getPrefs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.mich01.hustlepos.DB.DBManager;
import com.mich01.hustlepos.R;
import com.mich01.hustlepos.Support.SupportActivity;
import com.mich01.hustlepos.UI.Analytics.Analytics;
import com.mich01.hustlepos.UI.Items.ItemList;
import com.mich01.hustlepos.UI.Sales.Search_Records;
import com.mich01.hustlepos.UI.Sales.Transactions;
import com.mich01.hustlepos.UI.Sales.ViewTransactions;
import com.mich01.hustlepos.UI.Setup.BackupRestore;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HomeActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    CardView View_Items_Btn, Add_Items_Btn, Sell_Items_Btn, View_Sales_Btn, Advance_Search, Analytics_Btn;
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    TextView Status;
    static TextView Total_sales;
    static int Sales;
    static int Item_cost = 0;
    ImageView ProductImage,person_img;
    private EditText ItemName, PurchasePrice, SalePrice, Quantity;
    public static final int PICK_IMAGE = 1;
    public byte[] imageInByte;
    Uri filePath;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK && data != null)
            {
                filePath = data.getData();
                Picasso.get().load(filePath).into(ProductImage);
            }
        }
    }
   /* public ActivityResultLauncher<String> mGetFile = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        Picasso.get().load(result).into(ProductImage);
                    }
                }
            });*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Objects.requireNonNull(getSupportActionBar()).hide();
        View_Items_Btn = findViewById(R.id.view_items);
        Add_Items_Btn = findViewById(R.id.add_items);
        Sell_Items_Btn = findViewById(R.id.sell_items);
        View_Sales_Btn = findViewById(R.id.view_sales);
        Total_sales = findViewById(R.id.global_total_sales);
        person_img = findViewById(R.id.support_img);
        Advance_Search = findViewById(R.id.advance_search_sales);
        Analytics_Btn = findViewById(R.id.analytics);
        MyPrefs = getPrefs(this);
        MyPrefsEditor = MyPrefs.edit();
        MyPrefsEditor.putBoolean("Licensed",false);
        person_img.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(HomeActivity.this, v);
            popup.setOnMenuItemClickListener(HomeActivity.this);
            popup.inflate(R.menu.support_menu);
            popup.show();
        });
        View_Items_Btn.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, ItemList.class);
            startActivity(i);
            //ViewEditItemDialog(1);
        });
        Add_Items_Btn.setOnClickListener(v -> ViewAddItemDialog());
        Sell_Items_Btn.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, Transactions.class);
            startActivity(i);
        });
        MyPrefs = getPrefs(this);
        String Days = (String) DateUtils.getRelativeTimeSpanString(MyPrefs.getLong("InstalledTimestamp", 0), System.currentTimeMillis(), 1);
        System.out.println("Days" + Days);
        if ((System.currentTimeMillis() - MyPrefs.getLong("InstalledTimestamp", 0)) < 1296000000  || MyPrefs.getBoolean("Licensed", false))
        {
            System.out.println("Days: " + ((System.currentTimeMillis() - MyPrefs.getLong("InstalledTimestamp", 0)) / (1000*60*60*24)));
            View_Sales_Btn.setOnClickListener(v -> {
                Intent i = new Intent(HomeActivity.this, ViewTransactions.class);
                //Intent i = new Intent(HomeActivity.this, ViewSales.class);
                startActivity(i);
            });
            Advance_Search.setOnClickListener(v -> {
                // ViewAdvancedSearchDialog();
                Intent i = new Intent(HomeActivity.this, Search_Records.class);
                startActivity(i);
            });
            Analytics_Btn.setOnClickListener(v -> {
                // ViewAdvancedSearchDialog();
                Intent i = new Intent(HomeActivity.this, Analytics.class);
                startActivity(i);
            });
        }
        else
        {
            Advance_Search.setEnabled(false);
            Advance_Search.setBackgroundColor(Color.GRAY);
            View_Sales_Btn.setEnabled(false);
            View_Sales_Btn.setBackgroundColor(Color.GRAY);
            Analytics_Btn.setEnabled(false);
            Analytics_Btn.setBackgroundColor(Color.GRAY);
            Advance_Search.setEnabled(false);
            Advance_Search.setBackgroundColor(Color.GRAY);
            AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setIcon(R.drawable.ic_stop_no_action);
            alertDialog.setMessage("Sorry your Trial period for this app has expired Please purchase the lifetime license "+Days);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "EXIT",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.show();
        }
        PopulateFigures(this);
        }

    public void ViewAddItemDialog() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }
        } else {
            dialogBuilder = new AlertDialog.Builder(this);
            final View addItemPopupView = getLayoutInflater().inflate(R.layout.add_item_popup, null);
            ItemName = addItemPopupView.findViewById(R.id.txt_item_name);
            ProductImage = addItemPopupView.findViewById(R.id.add_item_banner);
            PurchasePrice = addItemPopupView.findViewById(R.id.txt_purchase_price);
            SalePrice = addItemPopupView.findViewById(R.id.txt_selling_price);
            Quantity = addItemPopupView.findViewById(R.id.txt_quantity);
            Button save_Btn = addItemPopupView.findViewById(R.id.cmd_save_record);
            Button cancel_Btn = addItemPopupView.findViewById(R.id.cmd_cancel);
            Button delete_Btn = addItemPopupView.findViewById(R.id.cmd_delete_item);
            Status = addItemPopupView.findViewById(R.id.lbl_status);
            delete_Btn.setVisibility(View.GONE);
            dialogBuilder.setView(addItemPopupView);
            ProductImage.setDrawingCacheEnabled(true);
            dialog = dialogBuilder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            ProductImage.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            });
            save_Btn.setOnClickListener(v -> ProcessImage(filePath));
            cancel_Btn.setOnClickListener(v -> dialog.dismiss());
        }
    }
    public static void PopulateFigures(Context context)
    {
        Sales=0;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            Cursor cur = new DBManager(context).getSales();
            if (cur.getCount() > 0)
            {
                while (cur.moveToNext())
                {
                    Sales=Sales+cur.getInt(cur.getColumnIndex("Price"));
                    Item_cost=Item_cost+(cur.getInt(cur.getColumnIndex("PurchasePrice"))*cur.getInt(cur.getColumnIndex("Quantity")));
                }
            }
            handler.post(() -> {
                String T_Sales = "Ksh: "+Sales;
                Total_sales.setText(T_Sales);
            });
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        Intent i = new Intent(HomeActivity.this, SupportActivity.class);
        switch (item.getItemId())
        {
            case R.id.mnu_about:
                i.putExtra("Url","https://hustlepos.co.ke/about.html");
                i.putExtra("Support","About MyHustle");
                startActivity(i);
                return true;
            case R.id.mnu_terms:
                i.putExtra("Url","https://hustlepos.co.ke/terms.html");
                i.putExtra("Support","Terms and Conditions");
                startActivity(i);
                return true;
            case R.id.mnu_privacy:
                i.putExtra("Url","https://hustlepos.co.ke/privacy.html");
                i.putExtra("Support","Privacy Notice");
                startActivity(i);
                return true;
            case R.id.mnu_backup_restore:
                Intent BR = new Intent(HomeActivity.this, BackupRestore.class);
                startActivity(BR);
                return true;
            default:
                return false;
        }
    }
    public void ProcessImage( Uri imagePath)
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Bitmap bitmap;
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap = MediaStore.Images.Media.getBitmap(HomeActivity.this.getContentResolver(),
                        imagePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                imageInByte = bos.toByteArray();
            } catch (Exception ignored) {}
            handler.post(() -> {
               if(!PurchasePrice.getText().toString().isEmpty()||!ItemName.getText().toString().isEmpty()||(!SalePrice.getText().toString().isEmpty()
                       ||!Quantity.getText().toString().isEmpty()||!ItemName.getText().toString().isEmpty()))
               {
                   try {
                       JSONObject ItemJson = new JSONObject();
                       ItemJson.put("ItemName", ItemName.getText().toString());
                       ItemJson.put("PurchasePrice", Integer.valueOf(PurchasePrice.getText().toString()));
                       ItemJson.put("SellingPrice", Integer.valueOf(SalePrice.getText().toString()));
                       ItemJson.put("Quantity", Integer.valueOf(Quantity.getText().toString()));
                       ItemJson.put("img", imageInByte);
                       if (new DBManager(HomeActivity.this).AddItem(ItemJson, imageInByte)) {
                           //dialog.dismiss();
                           Status.setBackgroundResource(R.color.success);
                           Status.setText(R.string.add_db_record_status);
                           ProductImage.setImageResource(R.drawable.add_image);
                           ItemName.setText(null);
                           PurchasePrice.setText(null);
                           SalePrice.setText(null);
                           Quantity.setText(null);
                           Handler h = new Handler();
                           h.postDelayed(() -> {
                               Status.setBackgroundResource(R.color.white);
                               Status.setText("");
                           }, 2000);
                       }
                   } catch (Exception ignored) {
                       Status.setBackgroundResource(R.color.warning);
                       Status.setText(R.string.error_input_issue);
                   }
               }
            });
        });
    }
}