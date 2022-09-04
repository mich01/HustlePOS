package com.mich01.hustlepos.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
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

import com.google.android.material.textfield.TextInputLayout;
import com.mich01.hustlepos.DB.DBManager;
import com.mich01.hustlepos.Data.Items;
import com.mich01.hustlepos.UI.Items.ItemList;

import org.json.JSONObject;

import java.util.ArrayList;

import com.mich01.hustlepos.R;

public class Items_Adapter extends ArrayAdapter<String>
{
    Context context;
    LayoutInflater inflater;
    ArrayList<Items> AllItems;

    public Items_Adapter(Context context, ArrayList<Items> MyItems) {
        super(context, R.layout.activity_list_items,R.id.product_name);
        this.AllItems = MyItems;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return AllItems.size();
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int ID = AllItems.get(position).getID();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View item_row  = layoutInflater.inflate(R.layout.activity_list_items, parent, false);
        ImageView images = item_row.findViewById(R.id.product_img);
        TextView Product_Name =  item_row.findViewById(R.id.product_name);
        TextView is_InStock = item_row.findViewById(R.id.is_in_stock);
        TextView Product_Price = item_row.findViewById(R.id.product_price);
        TextView Stock_Count = item_row.findViewById(R.id.stock_numbers);
        String StockCount = "Remaining Stock: " + AllItems.get(position).getQuantity();
        Product_Name.setText(AllItems.get(position).getItemName());
        Stock_Count.setText(StockCount);
        if(Integer.parseInt(AllItems.get(position).getQuantity()) <1)
        {
            is_InStock.setText(R.string.out_of_stock);
            Stock_Count.setBackgroundColor(Color.RED);
            Stock_Count.setTextColor(Color.WHITE);
        }
        else if(Integer.parseInt(AllItems.get(position).getQuantity())<11)
        {
            is_InStock.setText(R.string.low_stock_numbers);
            Stock_Count.setBackgroundColor(Color.YELLOW);
            Stock_Count.setTextColor(Color.BLACK);
        }
        else
        {
            is_InStock.setText("In Stock");
        }
        String Price = "Ksh: " + AllItems.get(position).getSellingPrice();
        Product_Price.setText(Price);
        byte[] blob=AllItems.get(position).getImg();
        if (AllItems.get(position).getImg()!=null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            images.setImageBitmap(bmp);
            item_row.setOnClickListener(v -> ViewEditItemDialog(ID, v.getRootView().getContext()));
        }
        else
        {
            images.setImageResource(R.drawable.add_item);
            item_row.setOnClickListener(v -> ViewEditItemDialog(ID, v.getRootView().getContext()));
        }
        return item_row;
    }
    public void ViewEditItemDialog(int ITEM_ID, Context context)
    {
        ImageView ProductImage;
        TextInputLayout Name, BuyingPrice, SellingPrice, Quty;
        EditText ItemName, PurchasePrice, SalePrice, Quantity;
        Button Save_Btn, Cancel_Btn, Delete_Btn;
        TextView Status;
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        Cursor c = new DBManager(context).getItem(ITEM_ID);
        dialogBuilder = new AlertDialog.Builder(context);
        final View addItemPopupView = LayoutInflater.from(context).inflate(R.layout.add_item_popup, null);
        ProductImage = addItemPopupView.findViewById(R.id.add_item_banner);
        ItemName =  addItemPopupView.findViewById(R.id.txt_item_name);
        PurchasePrice = addItemPopupView.findViewById(R.id.txt_purchase_price);
        SalePrice = addItemPopupView.findViewById(R.id.txt_selling_price);
        Quantity =addItemPopupView.findViewById(R.id.txt_quantity);
        Save_Btn = addItemPopupView.findViewById(R.id.cmd_save_record);
        Cancel_Btn = addItemPopupView.findViewById(R.id.cmd_cancel);
        Delete_Btn = addItemPopupView.findViewById(R.id.cmd_delete_item);
        Status = addItemPopupView.findViewById(R.id.lbl_status);
        Name = addItemPopupView.findViewById(R.id.itn);
        BuyingPrice = addItemPopupView.findViewById(R.id.ptcp);
        SellingPrice = addItemPopupView.findViewById(R.id.sp);
        Quty = addItemPopupView.findViewById(R.id.qty);
        Name.setHint(R.string.edit_item_name);
        BuyingPrice.setHint(R.string.edit_buying_price_hint);
        SellingPrice.setHint(R.string.edit_selling_price_text);
        Quty.setHint(R.string.edit_qty_text);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
        {
            byte[] blob=c.getBlob(c.getColumnIndex("img") );
            if (c.getBlob(c.getColumnIndex("img"))!=null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                ProductImage.setImageBitmap(bmp);
            }
            else
            {
                ProductImage.setImageResource(R.drawable.add_item);
            }
            ItemName.setText(c.getString( c.getColumnIndex("ItemName") ));
            PurchasePrice.setText(c.getString( c.getColumnIndex("PurchasePrice") ));
            SalePrice.setText(c.getString( c.getColumnIndex("SellingPrice") ));
            Quantity.setText(c.getString( c.getColumnIndex("Quantity") ));
        }
        c.close();
        Save_Btn.setText(R.string.btn_update_item);
        dialogBuilder.setView(addItemPopupView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
         ProductImage.setOnClickListener(v->{
        });
        Delete_Btn.setOnClickListener(v->{
            if(new DBManager(v.getRootView().getContext()).DeleteItem(ITEM_ID)==1)
            {
                Toast.makeText(context,"Item Deleted: ", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(context,"Can't delete item already in the sales recorded", Toast.LENGTH_LONG).show();
            }
            ItemList.PopulateList(context);
            notifyDataSetChanged();
            ItemList.adapter.notifyDataSetChanged();
            dialog.dismiss();
        });
        Save_Btn.setOnClickListener(v -> {
            try {
                String ImageFileName = "HustlePOS-"+(System.currentTimeMillis()/1000)+".png";
               // new BackendFunctions(context).SaveImage(ProductImage.getDrawingCache(),ImageFileName);
                JSONObject ItemJson = new JSONObject();
                ItemJson.put("ItemName", ItemName.getText().toString());
                ItemJson.put("img", ImageFileName);
                ItemJson.put("PurchasePrice", Integer.valueOf(PurchasePrice.getText().toString()));
                ItemJson.put("SellingPrice", Integer.valueOf(SalePrice.getText().toString()));
                ItemJson.put("Quantity", Integer.valueOf(Quantity.getText().toString()));
                if(new DBManager(context).UpdateItem(ItemJson, ITEM_ID))
                {
                    ItemList.PopulateList(v.getRootView().getContext());
                    Status.setBackgroundResource(R.color.success);
                    Status.setText(R.string.item_update_status);
                    Handler h = new Handler();
                    h.postDelayed(() -> {
                        ItemList.adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    },10);
                }
            } catch (Exception ignored) {
                Status.setBackgroundResource(R.color.warning);
                Status.setText(R.string.error_update_issue);
            }});
        Cancel_Btn.setOnClickListener(v -> dialog.dismiss());
    }
}
