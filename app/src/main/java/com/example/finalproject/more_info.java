package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

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
            String code = extras.getString("Code");
            String name = extras.getString("Name");
            String tradeVolume = extras.getString("TradeVolume");
            String tradeValue = extras.getString("TradeValue");

            TextView tvCode = findViewById(R.id.tvMoreCode);
            TextView tvName = findViewById(R.id.tvMoreName);
            TextView tvTradeVolume = findViewById(R.id.tvMoreTradeVolume);
            TextView tvTradeValue = findViewById(R.id.tvMoreTradeValue);

            tvCode.setText("股票代碼: " + code);
            tvName.setText("股票名稱: " + name);
            tvTradeVolume.setText("交易量: " + tradeVolume);
            tvTradeValue.setText("交易價值: " + tradeValue);
        }
    }

    public void goBack(View view) {
        finish();
    }
}