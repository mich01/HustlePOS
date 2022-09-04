package com.mich01.hustlepos.UI.Analytics;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mich01.hustlepos.DB.DBManager;
import com.mich01.hustlepos.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Analytics extends AppCompatActivity {
    int ProductCount=0;
    BarChart Sales_Chart, Products_Profits;
    PieChart Cumulative, Cumulative_Product_Profits;
    BarDataSet barDataSet;
    BarData data;
    PieData cumulative_sales;
    PieData cumulative_Profits;
    ArrayList<String> Products = new ArrayList<>();
    ArrayList ProductProfits = new ArrayList();
    ArrayList ProductSales = new ArrayList();
    ArrayList CumulativeProductProfits = new ArrayList();
    ArrayList CumulativeProductSales = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("My Analytics");
        actionBar.setDisplayHomeAsUpEnabled(true);
        getXAxisProducts();
        Sales_Chart=  findViewById(R.id.yearly_sales);
         Cumulative =  findViewById(R.id.cumulative_product_sales);
         Products_Profits =  findViewById(R.id.product_profits);
         Cumulative_Product_Profits = findViewById(R.id.cumulative_product_profits);
         PopulateSales();
         PopulateProfits();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private ArrayList<String> getXAxisValuesa() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("JAN");
        xAxis.add("FEB");
        xAxis.add("MAR");
        xAxis.add("APR");
        xAxis.add("MAY");
        xAxis.add("JUN");
        xAxis.add("JUL");
        xAxis.add("AUG");
        xAxis.add("SEPT");
        xAxis.add("OCT");
        xAxis.add("NOV");
        xAxis.add("DEC");
        return xAxis;
    }
    private List<String> getXAxisProducts() {
        Cursor productsCursor= new DBManager(Analytics.this).getUniqueSales();
        Products.clear();
        if (productsCursor.getCount()>0)
        {
            while (productsCursor.moveToNext())
            {
                Products.add(productsCursor.getString(productsCursor.getColumnIndex("ItemName")));
            }
        }
        ProductCount = Products.size();
        return Products;
    }

    private void PopulateSales()
    {
        //Pie Data
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            for(int i=0;i<Products.size();i++)
            {
                int TotalSales=10;
                float xI =i;
                ProductSales.add(new BarEntry(xI,TotalSales));
                CumulativeProductSales.add(new PieEntry((float) TotalSales, "Item " + xI));
                Cursor SalesCursor = new DBManager(Analytics.this).getSales();
                if (SalesCursor.getCount() > 0) {
                    while (SalesCursor.moveToNext())
                    {
                        if(SalesCursor.getString(SalesCursor.getColumnIndex("ItemName")).equals(Products.get(i)))
                        {
                            TotalSales =TotalSales+SalesCursor.getInt(SalesCursor.getColumnIndex("Price"));
                            ProductSales.set(i,new BarEntry(xI,TotalSales));
                            CumulativeProductSales.set(i, new PieEntry((float) TotalSales, Products.get(i)));
                        }
                    }

                }
            }
            handler.post(() -> {
                //Bar Data
                barDataSet=new BarDataSet(ProductSales,"Products Sales");
                XAxis BarLabel =Sales_Chart.getXAxis();
                BarLabel.setLabelCount(Products.size());
                BarLabel.setPosition(XAxis.XAxisPosition.BOTTOM);
                BarLabel.setDrawGridLines(false);
                List<String> BarLabels = getXAxisProducts();
                barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                data = new BarData(barDataSet);
                final Description Title = new Description();
                Title.setText("Products Sales");
                Sales_Chart.setData(data);
                Sales_Chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter());
                Sales_Chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(BarLabels));
                Sales_Chart.setDescription(Title);
                Sales_Chart.animateXY(2000, 2000);
                //PieChart Content
                PieDataSet dataSet = new PieDataSet(CumulativeProductSales, "Overall Sales");
                dataSet.setSliceSpace(1f);
                dataSet.setSelectionShift(5f);
                dataSet = new PieDataSet(CumulativeProductSales, "Overall Sales");
                cumulative_sales = new PieData(dataSet);
                Cumulative.setDescription(Title);
                Cumulative.setData(cumulative_sales);
                Cumulative.setCenterText("Overall Sales");
                Cumulative.setDrawHoleEnabled(false);
                dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                dataSet.setValueTextColor(Color.BLACK);
                dataSet.setValueTextSize(10f);
                Cumulative.invalidate();
                Sales_Chart.invalidate();
            });
        });
    }
    private void PopulateProfits()
    {
        //Pie Data
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            for(int i=0;i<Products.size();i++)
            {
                int TotalSales=10;
                float xI =i;
                ProductProfits.add(new BarEntry(xI,TotalSales));
                CumulativeProductProfits.add(new PieEntry((float) TotalSales, "Item " + xI));
                Cursor SalesCursor = new DBManager(Analytics.this).getSales();
                if (SalesCursor.getCount() > 0) {
                    while (SalesCursor.moveToNext())
                    {
                        if(SalesCursor.getString(SalesCursor.getColumnIndex("ItemName")).equals(Products.get(i)))
                        {
                           TotalSales =(TotalSales+SalesCursor.getInt(SalesCursor.getColumnIndex("Price")))-((SalesCursor.getInt(SalesCursor.getColumnIndex("PurchasePrice"))*(SalesCursor.getInt(SalesCursor.getColumnIndex("Quantity")))));
                            ProductProfits.set(i,new BarEntry(xI,TotalSales));
                            CumulativeProductProfits.set(i,new PieEntry((float) TotalSales, Products.get(i)));
                        }
                    }

                }
            }
            handler.post(() -> {
                //Bar Data
                barDataSet=new BarDataSet(ProductProfits,"Product Profits");
                XAxis BarLabel =Sales_Chart.getXAxis();
                BarLabel.setLabelCount(Products.size());
                BarLabel.setPosition(XAxis.XAxisPosition.BOTTOM);
                BarLabel.setDrawGridLines(false);
                List<String> BarLabels = getXAxisProducts();
                barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                data = new BarData(barDataSet);
                final Description Title = new Description();
                Title.setText("Product Profits");
                Products_Profits.setData(data);
                Products_Profits.getXAxis().setValueFormatter(new IndexAxisValueFormatter());
                Products_Profits.getXAxis().setValueFormatter(new IndexAxisValueFormatter(BarLabels));
                Products_Profits.setDescription(Title);
                Products_Profits.animateXY(2000, 2000);
                //PieChart Content
                PieDataSet dataSet = new PieDataSet(CumulativeProductProfits, "Overall Profits");
                dataSet.setSliceSpace(1f);
                dataSet.setSelectionShift(5f);
                dataSet = new PieDataSet(CumulativeProductProfits, "Overall Profits");
                cumulative_Profits = new PieData(dataSet);
                Cumulative_Product_Profits.setDescription(Title);
                Cumulative_Product_Profits.setData(cumulative_Profits);
                Cumulative_Product_Profits.setCenterText("Overall Profits");
                dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                dataSet.setValueTextColor(Color.BLACK);
                dataSet.setValueTextSize(10f);
                Cumulative_Product_Profits.invalidate();
                Products_Profits.invalidate();
            });
        });
    }
}


