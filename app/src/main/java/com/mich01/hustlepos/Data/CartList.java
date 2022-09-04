package com.mich01.hustlepos.Data;

public class CartList
{
    int ItemID;
    String ItemName;
    int Price;
    int Quantity;

    public CartList(int itemID,String ItemName, int price, int quantity) {
        ItemID = itemID;
        Price = price;
        Quantity = quantity;
        this.ItemName = ItemName;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public int getItemID() {
        return ItemID;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }
}
