package com.mich01.hustlepos.Data;

public class SoldItems {
    int ItemID;
    byte [] ItemImage;
    String ItemName;
    int Quantity;
    int AmountSold;
    String TransactionID;
    long TimeStamp;

    public SoldItems(int itemID, byte[] ItemImage, String itemName, int quantity, int amountSold, String transactionID, long TimeStamp) {
        ItemID = itemID;
        this.ItemImage=ItemImage;
        ItemName = itemName;
        Quantity = quantity;
        AmountSold = amountSold;
        TransactionID = transactionID;
        this.TimeStamp =TimeStamp;
    }


    public byte[] getItemImage() {
        return ItemImage;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public int getAmountSold() {
        return AmountSold;
    }
}
