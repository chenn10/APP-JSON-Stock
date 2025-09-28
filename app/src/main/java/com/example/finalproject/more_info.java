package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class more_info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            displayStockInfo(extras);
        }
    }

    private void displayStockInfo(Bundle extras) {
        // 基本資訊
        TextView tvCode = findViewById(R.id.tvMoreCode);
        TextView tvName = findViewById(R.id.tvMoreName);
        TextView tvTradeVolume = findViewById(R.id.tvMoreTradeVolume);
        TextView tvTradeValue = findViewById(R.id.tvMoreTradeValue);
        
        // 價格資訊
        TextView tvOpeningPrice = findViewById(R.id.tvOpeningPrice);
        TextView tvHighestPrice = findViewById(R.id.tvHighestPrice);
        TextView tvLowestPrice = findViewById(R.id.tvLowestPrice);
        TextView tvClosingPrice = findViewById(R.id.tvClosingPrice);
        TextView tvChange = findViewById(R.id.tvChange);
        TextView tvTransaction = findViewById(R.id.tvTransaction);
        
        // 統計資訊
        TextView tvPriceRange = findViewById(R.id.tvPriceRange);
        TextView tvAvgPrice = findViewById(R.id.tvAvgPrice);
        TextView tvMarketCap = findViewById(R.id.tvMarketCap);

        String code = extras.getString("Code", "N/A");
        String name = extras.getString("Name", "N/A");
        String tradeVolume = extras.getString("TradeVolume", "0");
        String tradeValue = extras.getString("TradeValue", "0");
        String openingPrice = extras.getString("OpeningPrice", "0");
        String highestPrice = extras.getString("HighestPrice", "0");
        String lowestPrice = extras.getString("LowestPrice", "0");
        String closingPrice = extras.getString("ClosingPrice", "0");
        String change = extras.getString("Change", "0");
        String transaction = extras.getString("Transaction", "0");

        // 設置基本資訊
        tvCode.setText("股票代碼: " + code);
        tvName.setText("股票名稱: " + name);
        tvTradeVolume.setText("成交量: " + formatNumber(tradeVolume) + " 股");
        tvTradeValue.setText("成交值: " + formatMoney(tradeValue) + " 元");
        
        // 設置價格資訊
        tvOpeningPrice.setText("開盤價: " + formatPrice(openingPrice) + " 元");
        tvHighestPrice.setText("最高價: " + formatPrice(highestPrice) + " 元");
        tvLowestPrice.setText("最低價: " + formatPrice(lowestPrice) + " 元");
        tvClosingPrice.setText("收盤價: " + formatPrice(closingPrice) + " 元");
        tvChange.setText("漲跌: " + change);
        tvTransaction.setText("成交筆數: " + formatNumber(transaction) + " 筆");
        
        // 設置漲跌顏色
        setChangeColor(tvChange, tvClosingPrice, change);
        
        // 計算並顯示統計資訊
        calculateAndDisplayStats(tvPriceRange, tvAvgPrice, tvMarketCap, 
                                openingPrice, highestPrice, lowestPrice, closingPrice, 
                                tradeVolume, tradeValue);
    }
    
    private void setChangeColor(TextView tvChange, TextView tvClosingPrice, String change) {
        try {
            double changeValue = Double.parseDouble(change);
            int color;
            if (changeValue > 0) {
                color = ContextCompat.getColor(this, R.color.green);
                tvChange.setText("▲ " + change);
            } else if (changeValue < 0) {
                color = ContextCompat.getColor(this, R.color.red);
                tvChange.setText("▼ " + change);
            } else {
                color = ContextCompat.getColor(this, R.color.white);
                tvChange.setText("— " + change);
            }
            tvChange.setTextColor(color);
            tvClosingPrice.setTextColor(color);
        } catch (NumberFormatException e) {
            tvChange.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvClosingPrice.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
    }
    
    private void calculateAndDisplayStats(TextView tvPriceRange, TextView tvAvgPrice, TextView tvMarketCap,
                                        String openingPrice, String highestPrice, String lowestPrice, 
                                        String closingPrice, String tradeVolume, String tradeValue) {
        try {
            double opening = Double.parseDouble(openingPrice);
            double highest = Double.parseDouble(highestPrice);
            double lowest = Double.parseDouble(lowestPrice);
            double closing = Double.parseDouble(closingPrice);
            long volume = Long.parseLong(tradeVolume.replace(",", ""));
            long value = Long.parseLong(tradeValue.replace(",", ""));
            
            // 價格區間
            double priceRange = highest - lowest;
            tvPriceRange.setText("價格區間: " + String.format("%.2f", priceRange) + " 元");
            
            // 平均成交價
            double avgPrice = volume > 0 ? (double) value / volume : 0;
            tvAvgPrice.setText("平均成交價: " + String.format("%.2f", avgPrice) + " 元");
            
            // 估算市值 (假設流通股數為成交量的100倍，這只是示意)
            long estimatedShares = volume * 100;
            double marketCap = closing * estimatedShares;
            tvMarketCap.setText("估算市值: " + formatMoney(String.valueOf((long)marketCap)) + " 元");
            
        } catch (NumberFormatException e) {
            tvPriceRange.setText("價格區間: 計算錯誤");
            tvAvgPrice.setText("平均成交價: 計算錯誤");
            tvMarketCap.setText("估算市值: 計算錯誤");
        }
    }

    private String formatNumber(String number) {
        if (number == null || number.isEmpty() || number.equals("N/A")) return "N/A";
        try {
            long num = Long.parseLong(number.replace(",", ""));
            if (num >= 100000000) {
                return String.format("%.1f億", num / 100000000.0);
            } else if (num >= 10000) {
                return String.format("%.1f萬", num / 10000.0);
            } else {
                return String.format("%,d", num);
            }
        } catch (NumberFormatException e) {
            return number;
        }
    }
    
    private String formatMoney(String money) {
        if (money == null || money.isEmpty() || money.equals("N/A")) return "N/A";
        try {
            long amount = Long.parseLong(money.replace(",", ""));
            if (amount >= 100000000) {
                return String.format("%.1f億", amount / 100000000.0);
            } else if (amount >= 10000) {
                return String.format("%.1f萬", amount / 10000.0);
            } else {
                return String.format("%,d", amount);
            }
        } catch (NumberFormatException e) {
            return money;
        }
    }
    
    private String formatPrice(String price) {
        if (price == null || price.isEmpty() || price.equals("N/A")) return "N/A";
        try {
            double p = Double.parseDouble(price);
            return String.format("%.2f", p);
        } catch (NumberFormatException e) {
            return price;
        }
    }

    public void goBack(View view) {
        finish();
    }
    
    public void shareStock(View view) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String code = extras.getString("Code", "");
            String name = extras.getString("Name", "");
            String closingPrice = extras.getString("ClosingPrice", "");
            String change = extras.getString("Change", "");
            
            String shareText = String.format("【股票資訊】\n%s (%s)\n收盤價: %s 元\n漲跌: %s\n\n來自股票查詢APP", 
                                           name, code, formatPrice(closingPrice), change);
            
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "分享股票資訊"));
        }
    }
}
