package com.mich01.hustlepos.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mich01.hustlepos.Data.SoldItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import com.mich01.hustlepos.R;

public class TransactionsAdapter extends BaseExpandableListAdapter {
    ArrayList<String> listGroup;
    HashMap<String, ArrayList<SoldItems>> listChild;

    public TransactionsAdapter(ArrayList<String> listGroup, HashMap<String, ArrayList<SoldItems>> listChild) {
        this.listGroup = listGroup;
        this.listChild = listChild;
    }
    @Override
    public int getGroupCount() {
        return listGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(listChild.get(listGroup.get(groupPosition))).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(listChild.get(listGroup.get(groupPosition))).get(childPosition);
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expandable_list_item, parent, false);
        TextView textView = convertView.findViewById(R.id.text_title);
        String sGroup = String.valueOf(getGroup(groupPosition));
        textView.setText(sGroup);
        textView.setTypeface(null, Typeface.BOLD);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_list_items, parent, false);
        ImageView Product_Img = convertView.findViewById(R.id.product_img);
        TextView Product_Name =  convertView.findViewById(R.id.product_name);
        TextView Product_Price = convertView.findViewById(R.id.product_price);
        TextView Stock_Count = convertView.findViewById(R.id.stock_numbers);
        String ItemNameChild;
        String Quantity;
        String Amount;
        byte[] blob=Objects.requireNonNull(listChild.get(listGroup.get(groupPosition))).get(childPosition).getItemImage();
        if (Objects.requireNonNull(listChild.get(listGroup.get(groupPosition))).get(childPosition).getItemImage()!=null) {
            Bitmap bmp= BitmapFactory.decodeByteArray(blob,0,blob.length);
            Product_Img.setImageBitmap(bmp);
        }
        else
        {
            Product_Img.setImageResource(R.drawable.add_item);
        }
            ItemNameChild= Objects.requireNonNull(listChild.get(listGroup.get(groupPosition))).get(childPosition).getItemName();
            Amount= String.valueOf(Objects.requireNonNull(listChild.get(listGroup.get(groupPosition))).get(childPosition).getAmountSold());
            Quantity= "Items Sold "+ Objects.requireNonNull(listChild.get(listGroup.get(groupPosition))).get(childPosition).getQuantity();

            Product_Name.setText(ItemNameChild);
            Product_Price.setText(Amount);
            Stock_Count.setText(Quantity);
            Product_Name.setTypeface(null, Typeface.BOLD);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
