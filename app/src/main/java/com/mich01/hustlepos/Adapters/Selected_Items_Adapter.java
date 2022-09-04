package com.mich01.hustlepos.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.mich01.hustlepos.Data.CartList;
import com.mich01.hustlepos.DB.DBManager;
import com.mich01.hustlepos.Data.Items;
import com.mich01.hustlepos.UI.Sales.Transactions;

import java.util.ArrayList;

import com.mich01.hustlepos.R;

public class Selected_Items_Adapter extends ArrayAdapter<String>
{
    Context context;
    LayoutInflater inflater;
    ArrayList<Items> AllItems;
    int ItemID;

    public Selected_Items_Adapter(Context context, ArrayList<Items> MyItems) {
        super(context, R.layout.activity_list_items,R.id.product_name);
        this.AllItems = MyItems;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return AllItems.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int ID = AllItems.get(position).getID();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View item_row  = layoutInflater.inflate(R.layout.activity_list_items, parent, false);
        ImageView images = item_row.findViewById(R.id.product_img);
        TextView Product_Name =  item_row.findViewById(R.id.product_name);
        TextView Product_Price = item_row.findViewById(R.id.product_price);
        TextView Stock_Count = item_row.findViewById(R.id.stock_numbers);
        Product_Name.setText(AllItems.get(position).getItemName());
        String StockCount = "Remaining Stock: " + AllItems.get(position).getQuantity();
        Product_Name.setText(AllItems.get(position).getItemName());
        Stock_Count.setText(StockCount);
        String Price = "Ksh: " + AllItems.get(position).getSellingPrice();
        Product_Price.setText(Price);
        byte[] blob= AllItems.get(position).getImg();
        if (AllItems.get(position).getImg()!=null) {
            Bitmap bmp= BitmapFactory.decodeByteArray(blob,0,blob.length);
            images.setImageBitmap(bmp);
        }
        else
        {
            images.setImageResource(R.drawable.add_item);
        }
        item_row.setOnClickListener(v -> AddItemToListDialog(ID,v.getRootView().getContext()));
        return item_row;
    }
    public void AddItemToListDialog(int ITEM_ID, Context context)
    {
        ItemID =ITEM_ID;
        int Limit=0;
        ImageView ItemImage;
        Button Add_Btn, Cancel_Btn;
        TextView ItemName, PurchasePrice, Items_Remaining;
        EditText SalePrice, Quantity;
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        Cursor c = new DBManager(context).getItem(ITEM_ID);
        dialogBuilder = new AlertDialog.Builder(context);
        final View addItemPopupView = LayoutInflater.from(context).inflate(R.layout.select_item_popup, null);
        ItemImage =  addItemPopupView.findViewById(R.id.add_item_banner);
        ItemName =  addItemPopupView.findViewById(R.id.txt_selected_item_name);
        PurchasePrice = addItemPopupView.findViewById(R.id.txt_purchase_price);
        SalePrice = addItemPopupView.findViewById(R.id.txt_selling_at);
        Quantity =addItemPopupView.findViewById(R.id.txt_selected_quantity);
        Add_Btn = addItemPopupView.findViewById(R.id.cmd_add_to_list);
        Cancel_Btn = addItemPopupView.findViewById(R.id.cmd_ignore);
        Items_Remaining = addItemPopupView.findViewById(R.id.quantity_label);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
        {
            byte[] blob=c.getBlob( c.getColumnIndex("img") );
            if (c.getBlob( c.getColumnIndex("img") )!=null) {
                Bitmap bmp= BitmapFactory.decodeByteArray(blob,0,blob.length);
                ItemImage.setImageBitmap(bmp);
            }
            else
            {
                ItemImage.setImageResource(R.drawable.add_item);
            }
            ItemName.setText(c.getString( c.getColumnIndex("ItemName") ));
            String PurchaseP_rice = "Bought At Ksh: " + c.getString(c.getColumnIndex("PurchasePrice"));
            PurchasePrice.setText(PurchaseP_rice);
            SalePrice.setText(c.getString( c.getColumnIndex("SellingPrice") ));
            Quantity.setText("0");
            Limit =Integer.parseInt(c.getString( c.getColumnIndex("Quantity")));
            String RemainingStatus ="Items Remaining in stock: "+(Limit);
            Items_Remaining.setText(RemainingStatus);
        }
        int finalLimit = Limit;
        Quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count>0)
                {
                    //Quantity.getText();
                    String RemainingStatus ="Items Remaining in stock: "+(finalLimit -Integer.parseInt(Quantity.getText().toString()));
                    Items_Remaining.setText(RemainingStatus);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        c.close();
        dialogBuilder.setView(addItemPopupView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        Add_Btn.setOnClickListener(v ->
        {
            try
            {
                if((finalLimit -Integer.parseInt(Quantity.getText().toString()))>=0)
                {
                    Transactions.MyCartItems.add(new CartList(ITEM_ID,
                            ItemName.getText().toString(),
                            Integer.parseInt(SalePrice.getText().toString()),
                            Integer.parseInt(Quantity.getText().toString())));
                    dialog.dismiss();
                    Transactions.CartItemsCount=Transactions.CartItemsCount+Integer.parseInt(Quantity.getText().toString());
                    String TotalItems ="Total Items: "+Transactions.CartItemsCount;
                    Transactions.Total_Items.setText(TotalItems);
                    Transactions.TotalAmount = Transactions.TotalAmount+(Integer.parseInt(SalePrice.getText().toString())*Integer.parseInt(Quantity.getText().toString()));
                    String TotalAmount = "Total Amount: "+Transactions.TotalAmount;
                    Transactions.Total_Amount.setText(TotalAmount);
                    Snackbar.make(v, ItemName.getText()+" Added to list", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
                else
                {
                    Snackbar.make(v, ItemName.getText()+" You cannot add more items than your current in stock", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }catch (Exception ignored)
            {
                Toast.makeText(context,
                        R.string.not_added_to_list,
                        Toast.LENGTH_LONG).show();
            }});

        Cancel_Btn.setOnClickListener(v -> dialog.dismiss());
    }
}
