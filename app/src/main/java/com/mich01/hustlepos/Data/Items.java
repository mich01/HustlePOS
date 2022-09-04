package com.mich01.hustlepos.Data;

public class Items
{
    int ID;
    String ItemName;
    byte [] Img;
    String PurchasePrice;
    String SellingPrice;
    String Quantity;

    public Items(int ID, byte[] Img, String itemName, String purchasePrice, String sellingPrice, String quantity) {
        this.ID = ID;
        this.Img = Img;
        this.ItemName = itemName;
        this.PurchasePrice = purchasePrice;
        this.SellingPrice = sellingPrice;
        this.Quantity = quantity;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public byte[] getImg() {
        return Img;
    }

    public void setImg(byte[] img) {
        Img = img;
    }

    public String getPurchasePrice() {
        return PurchasePrice;
    }

    public void setPurchasePrice(String purchasePrice) {
        PurchasePrice = purchasePrice;
    }

    public String getSellingPrice() {
        return SellingPrice;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }
}
