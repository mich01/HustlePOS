package com.mich01.hustlepos.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.mich01.hustlepos.Data.CartList;
import com.mich01.hustlepos.DB.DBManager;
import com.mich01.hustlepos.Data.SoldItems;
import com.mich01.hustlepos.UI.Sales.Transactions;

import java.util.ArrayList;

import com.mich01.hustlepos.R;


public class SalesAdapter extends ArrayAdapter<String>
{
    Context context;
    LayoutInflater inflater;
    ArrayList<SoldItems> AllItems;
    int ItemID;

    public SalesAdapter(Context context, ArrayList<SoldItems> MyItems) {
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
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View item_row  = layoutInflater.inflate(R.layout.activity_transaction_list_item, parent, false);
        TextView Product_Name =  item_row.findViewById(R.id.product_sold_name);
        TextView is_InStock = item_row.findViewById(R.id.is_in_stock);
        TextView Product_Price = item_row.findViewById(R.id.amount_sold);
        TextView Stock_Count = item_row.findViewById(R.id.qty);
        Product_Name.setText(AllItems.get(position).getItemName());
        String StockCount = "Quantity: " + AllItems.get(position).getQuantity();
        Product_Name.setText(AllItems.get(position).getItemName());
        Stock_Count.setText(StockCount);
        String Price = "Ksh: " + AllItems.get(position).getAmountSold();
        Product_Price.setText(Price);
        if(Integer.valueOf(AllItems.get(position).getQuantity()) <1)
        {
            is_InStock.setText("Out of Stock");
            Stock_Count.setBackgroundColor(Color.RED);
            Stock_Count.setTextColor(Color.WHITE);
        }
        else if(Integer.valueOf(AllItems.get(position).getQuantity())<11)
        {
            is_InStock.setText("Low Stock Numbers");
            Stock_Count.setBackgroundColor(Color.YELLOW);
            Stock_Count.setTextColor(Color.BLACK);
        }
        else
        {
            is_InStock.setText("In Stock");
        }
        //item_row.setOnClickListener(v -> AddItemToListDialog(ID,v.getRootView().getContext()));
        return item_row;
    }
    public void AddItemToListDialog(int ITEM_ID, Context context)
    {
        ItemID =ITEM_ID;
        int Limit=0;
        Button Add_Btn, Cancel_Btn;
        TextView ItemName, PurchasePrice, Items_Remaining;
        EditText SalePrice, Quantity;
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        Cursor c = new DBManager(context).getItem(ITEM_ID);
        dialogBuilder = new AlertDialog.Builder(context);
        final View addItemPopupView = LayoutInflater.from(context).inflate(R.layout.select_item_popup, null);
        ItemName =  addItemPopupView.findViewById(R.id.txt_selected_item_name);
        PurchasePrice = addItemPopupView.findViewById(R.id.txt_purchase_price);
        SalePrice = addItemPopupView.findViewById(R.id.txt_selling_at);
        Quantity =addItemPopupView.findViewById(R.id.txt_selected_quantity);
        Add_Btn = addItemPopupView.findViewById(R.id.cmd_add_to_list);
        Cancel_Btn = addItemPopupView.findViewById(R.id.cmd_ignore);
        Items_Remaining = addItemPopupView.findViewById(R.id.quantity_label);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
        {
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
        dialog.show();
        Add_Btn.setOnClickListener(v ->
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
        });

        Cancel_Btn.setOnClickListener(v -> dialog.dismiss());
    }
}

