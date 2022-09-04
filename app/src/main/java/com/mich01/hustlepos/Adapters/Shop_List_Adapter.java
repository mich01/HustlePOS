package com.mich01.hustlepos.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.mich01.hustlepos.DB.DBManager;
import com.mich01.hustlepos.Data.Items;
import com.mich01.hustlepos.UI.Sales.Transaction_Checkout;
import com.mich01.hustlepos.UI.Sales.Transactions;

import java.util.ArrayList;

import com.mich01.hustlepos.R;

public class Shop_List_Adapter extends ArrayAdapter<String>
{
    Context context;
    LayoutInflater inflater;
    ArrayList<Items> AllItems;
    int ItemID;

    public Shop_List_Adapter(Context context, ArrayList<Items> MyItems) {
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
        String StockCount = "Bought Qty: " + AllItems.get(position).getQuantity();
        int TotalPrice =Integer.parseInt(AllItems.get(position).getQuantity())*Integer.parseInt(AllItems.get(position).getSellingPrice());
        Product_Name.setText(AllItems.get(position).getItemName());
        Stock_Count.setText(StockCount);
        String Price = "Ksh: " + TotalPrice;
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
        item_row.setOnClickListener(v -> DeleteFromListDialog(ID,v.getRootView().getContext(),position));
        return item_row;
    }
    public void DeleteFromListDialog(int ITEM_ID, Context context ,int index)
    {
        ItemID =ITEM_ID;
        ImageView ProductImage;
        Button Remove_Btn, Cancel_Btn;
        TextView ItemName, PurchasePrice;
        EditText SalePrice, Quantity;
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        Cursor c = new DBManager(context).getItem(ITEM_ID);
        dialogBuilder = new AlertDialog.Builder(context);
        final View addItemPopupView = LayoutInflater.from(context).inflate(R.layout.select_item_popup, null);
        ProductImage =  addItemPopupView.findViewById(R.id.add_item_banner);
        ItemName =  addItemPopupView.findViewById(R.id.txt_selected_item_name);
        PurchasePrice = addItemPopupView.findViewById(R.id.txt_purchase_price);
        SalePrice = addItemPopupView.findViewById(R.id.txt_selling_at);
        Quantity =addItemPopupView.findViewById(R.id.txt_selected_quantity);
        Remove_Btn = addItemPopupView.findViewById(R.id.cmd_add_to_list);
        Remove_Btn.setText(R.string.remove_btn);
        Cancel_Btn = addItemPopupView.findViewById(R.id.cmd_ignore);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
        {
            byte[] blob=c.getBlob(c.getColumnIndex("img") );
            if (c.getBlob(c.getColumnIndex("img") )!=null) {
                Bitmap bmp= BitmapFactory.decodeByteArray(blob,0,blob.length);
                ProductImage.setImageBitmap(bmp);
            }
            else
            {
                ProductImage.setImageResource(R.drawable.add_item);
            }
            ItemName.setText(c.getString( c.getColumnIndex("ItemName") ));
            String BuyingPrice = "Bought At Ksh: " +c.getString(c.getColumnIndex("PurchasePrice") );
            PurchasePrice.setText(BuyingPrice);
            SalePrice.setText(c.getString( c.getColumnIndex("SellingPrice") ));
            Quantity.setText(c.getString( c.getColumnIndex("Quantity")));
        }
        c.close();
        dialogBuilder.setView(addItemPopupView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Remove_Btn.setOnClickListener(v -> {
            Transaction_Checkout.AllItems.remove(index);
            Transactions.MyCartItems.remove(index);
            dialog.dismiss();
            Transactions.CartItemsCount=Transactions.CartItemsCount-Integer.parseInt(Quantity.getText().toString());
            String TotalItems ="Total Items: "+Transactions.CartItemsCount;
            Transactions.Total_Items.setText(TotalItems);
            Transaction_Checkout.Total_Items.setText(TotalItems);
            Transactions.TotalAmount = Transactions.TotalAmount-(Integer.parseInt(SalePrice.getText().toString())*Integer.parseInt(Quantity.getText().toString()));
            String TotalAmount ="Total Amount: "+Transactions.TotalAmount;
            Transactions.Total_Amount.setText(TotalAmount);
            Transaction_Checkout.Total_Amount.setText(TotalAmount);
            Transaction_Checkout.adapter.notifyDataSetChanged();
            Transactions.adapter.notifyDataSetChanged();
        });
        Cancel_Btn.setOnClickListener(v -> dialog.dismiss());
    }
}
