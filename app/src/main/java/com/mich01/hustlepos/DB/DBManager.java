package com.mich01.hustlepos.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.mich01.hustlepos.UI.Setup.BackupRestore;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DBManager
{
    Context context;
    private final SQLiteDatabase DB;
    public DBManager(Context context)
    {
        this.context =context;
        final DBHelper helper= DBHelper.getInstance(context);
        DB = helper.getWritableDatabase();
    }


    public boolean AddItem(JSONObject ItemAdded, byte[] imgData)
    {
        boolean status = false;
        try
        {
            ContentValues itemsValues = new ContentValues();
            itemsValues.put("img", imgData);
            itemsValues.put("ItemName", ItemAdded.getString("ItemName"));
            itemsValues.put("PurchasePrice", ItemAdded.getString("PurchasePrice"));
            itemsValues.put("SellingPrice", ItemAdded.getString("SellingPrice"));
            itemsValues.put("Quantity", ItemAdded.getString("Quantity"));
            long result = DB.insert("Items", null, itemsValues);
            status= result != -1;
            DB.close();
        }
        catch (Exception ignored){}

        return status;
    }
    public boolean AddTransaction(JSONObject ItemAdded)
    {
        boolean status = false;
        try
        {
            int Total = ItemAdded.getInt("Price")*ItemAdded.getInt("Quantity");
            ContentValues itemsValues = new ContentValues();
            itemsValues.put("ItemID", ItemAdded.getString("ItemID"));
            itemsValues.put("TransactionID", ItemAdded.getString("TransactionID"));
            itemsValues.put("Price", Total);
            itemsValues.put("Quantity", ItemAdded.getString("Quantity"));
            DB.insert("Sales", null, itemsValues);
            return true;
        }
        catch (Exception ignored)
        {
            return false;
        }
    }
    public boolean UpdateItem(JSONObject ItemAdded, int ID)
    {
        boolean status = false;
        try
        {
            ContentValues itemsValues = new ContentValues();
            itemsValues.put("ItemName", ItemAdded.getString("ItemName"));
            itemsValues.put("PurchasePrice", ItemAdded.getString("PurchasePrice"));
            itemsValues.put("SellingPrice", ItemAdded.getString("SellingPrice"));
            itemsValues.put("Quantity", ItemAdded.getString("Quantity"));
            long result = DB.update("Items", itemsValues, "ID=?", new String[]{String.valueOf(ID)});
            status= result != -1;
        }
        catch (Exception ignored){}
        return status;
    }
    public boolean UpdateStock(int StockRemaining, int ID)
    {
        int PreviousQuantity=0;
        Cursor CurrentItem = getItem(ID);
        if (CurrentItem.getCount() > 0)
        {
            while (CurrentItem.moveToNext()) {
                PreviousQuantity=  CurrentItem.getInt(CurrentItem.getColumnIndex("Quantity"));
            }
        }
        boolean status = false;
        ContentValues itemsValues = new ContentValues();
        itemsValues.put("Quantity", PreviousQuantity-StockRemaining);
        long result = DB.update("Items", itemsValues, "ID=?", new String[]{String.valueOf(ID)});
        status= result != -1;
        return status;
    }
    public Cursor getItem(int ItemID)
    {
        Cursor cursor = DB.rawQuery("select * from Items where ID=? LIMIT 1", new String[] {String.valueOf(ItemID)});
        return cursor;
    }
    public Cursor getTransaction(int TransactionID)
    {
        Cursor cursor = DB.rawQuery("select * from Sales where TransactionID=? LIMIT 1", new String[] {String.valueOf(TransactionID)});
        return cursor;
    }
    public Cursor getItems()
    {
        Cursor cursor = DB.rawQuery("select * from Items", null);
        return cursor;
    }
    public Cursor searchItems(String ItemName)
    {
        Cursor cursor = DB.rawQuery("select * from Item where TransactionID=? LIMIT 1", new String[] {ItemName});
        return cursor;
    }
    public Cursor getSales()
    {
        Cursor cursor = DB.rawQuery("select Sales.ID, Sales.ItemID, Items.ItemName, Items.img, Sales.TransactionID,Items.PurchasePrice, Sales.Price, Sales.Quantity, Sales.Timestamp from Sales INNER JOIN Items on Items.ID = Sales.ItemID", null);
        return cursor;
    }
    public Cursor getUniqueSales()
    {
        Cursor cursor = DB.rawQuery("SELECT DISTINCT Sales.ItemID,Items.ItemName FROM Sales INNER JOIN Items on Items.ID = Sales.ItemID ;", null);
        return cursor;
    }
    public Cursor searchTransactions(long StartDate, long EndDate)
    {
        Cursor cursor = DB.rawQuery("select * from Sales where Timestamp>=? AND Timestamp<=? LIMIT 1", new String[] {String.valueOf(StartDate), String.valueOf(EndDate)});
        return cursor;
    }
    public Cursor getTransactions()
    {
        Cursor cursor = DB.rawQuery("SELECT DISTINCT TransactionID FROM Sales;", null);
        return cursor;
    }

    public void clearSales()
    {
        DB.delete("Sales", null, null);
        DB.close();
    }
    public int DeleteItem(int ItemID)
    {
        int result=0;
        Cursor cursor = DB.rawQuery("select * from Sales where ItemID=?", new String[] {String.valueOf(ItemID)});
        if(cursor.getCount()<1) {
            result = DB.delete("Items", "ID=?", new String[]{String.valueOf(ItemID)});
            result =1;
        }
        else
        {
            result =0;
            //Toast.makeText(context,"This Item has already Existing Transactions",Toast.LENGTH_LONG).show();
            //Snackbar.make()
        }
        return result;
    }
    public void DeleteSale(String ItemID)
    {
        Cursor cursor = DB.rawQuery("select * from Sales where ItemID=?", new String[] {ItemID});
        if(cursor.getCount()<1) {
            int result = DB.delete("Items", "ID=?", new String[]{ItemID});
        }
        else
        {
            Toast.makeText(context,"This Item has already Existing Transactions",Toast.LENGTH_LONG).show();
            //Snackbar.make()
        }
    }
    public int DeleteTransaction(String TransactionID)
    {
        int result = DB.delete("Sales", "TransactionID=?", new String[]{TransactionID});
        return result;
    }
    public int DeleteSaleItem(String ItemID)
    {
        int result = DB.delete("Sales", "TransactionID=?", new String[]{ItemID});
        return result;
    }
    public void backup(String outFileName) throws IOException, NullPointerException {
        String Path= BackupRestore.inFileName;
        //database path
        File dbFile = new File(Path+".db");
        FileInputStream fis = new FileInputStream(dbFile);
        // Open the empty db as the output stream
        OutputStream output = new FileOutputStream(outFileName);
        // Transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        // Close the streams
        output.flush();
        output.close();
        fis.close();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String backupOnline() throws IOException, NullPointerException {
        String Path = BackupRestore.inFileName;
        String fileString = "";
        //database path
        File dbFile = new File(Path + ".db");
        byte[] buffer = new byte[(int) dbFile.length()];
        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(dbFile));
        buf.read(buffer, 0, buffer.length);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fileString = android.util.Base64.encodeToString(buffer, android.util.Base64.DEFAULT);
        } else {
            fileString = android.util.Base64.encodeToString(buffer, Base64.DEFAULT);
        }
        // Open the empty db as the output stream
        buf.close();
        return fileString;
    }

}
