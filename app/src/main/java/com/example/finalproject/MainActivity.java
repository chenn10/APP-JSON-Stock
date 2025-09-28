package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    String TAG = MainActivity.class.getSimpleName() + "My";
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        catchData();
    }

    private void catchData() {
        String catchData = "https://api.jsonserve.com/WfdxPD"; 
        ProgressDialog dialog = ProgressDialog.show(this, "正在載入股票資料", "請稍候...", true);
        dialog.setCancelable(false);
        
        new Thread(() -> {
            try {
                URL url = new URL(catchData);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                InputStream is = connection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    json.append(line);
                }

                JSONArray stocksArray = new JSONArray(json.toString());

                for (int i = 0; i < stocksArray.length(); i++) {
                    JSONObject stock = stocksArray.getJSONObject(i);
                    
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("Code", stock.optString("Code", "N/A"));
                    hashMap.put("Name", stock.optString("Name", "N/A"));
                    hashMap.put("TradeVolume", stock.optString("TradeVolume", "0"));
                    hashMap.put("TradeValue", stock.optString("TradeValue", "0"));
                    hashMap.put("OpeningPrice", stock.optString("OpeningPrice", "0"));
                    hashMap.put("HighestPrice", stock.optString("HighestPrice", "0"));
                    hashMap.put("LowestPrice", stock.optString("LowestPrice", "0"));
                    hashMap.put("ClosingPrice", stock.optString("ClosingPrice", "0"));
                    hashMap.put("Change", stock.optString("Change", "0"));
                    hashMap.put("Transaction", stock.optString("Transaction", "0"));

                    arrayList.add(hashMap);
                }

                Log.d(TAG, "載入成功，股票數量: " + arrayList.size());

                runOnUiThread(() -> {
                    dialog.dismiss();
                    RecyclerView recyclerView;
                    MyAdapter myAdapter;
                    recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
                    myAdapter = new MyAdapter();
                    recyclerView.setAdapter(myAdapter);
                    
                    Toast.makeText(this, "載入 " + arrayList.size() + " 筆股票資料", Toast.LENGTH_SHORT).show();
                });
            } catch (MalformedURLException e) {
                handleError(dialog, "網址錯誤");
                e.printStackTrace();
            } catch (IOException e) {
                handleError(dialog, "網路連線失敗，請檢查網路連線");
                e.printStackTrace();
            } catch (JSONException e) {
                handleError(dialog, "資料格式錯誤");
                e.printStackTrace();
            } catch (Exception e) {
                handleError(dialog, "載入失敗，請稍後再試");
                e.printStackTrace();
            }
        }).start();
    }
    
    private void handleError(ProgressDialog dialog, String message) {
        runOnUiThread(() -> {
            dialog.dismiss();
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvCode, tvName, tvTradeVolume, tvTradeValue, tvClosingPrice, tvChange;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCode = itemView.findViewById(R.id.tvCode);
                tvName = itemView.findViewById(R.id.tvName);
                tvTradeVolume = itemView.findViewById(R.id.tvTradeVolume);
                tvTradeValue = itemView.findViewById(R.id.tvTradeValue);
                tvClosingPrice = itemView.findViewById(R.id.tvClosingPrice);
                tvChange = itemView.findViewById(R.id.tvChange);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_data_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HashMap<String, String> item = arrayList.get(position);
            
            holder.tvCode.setText(item.get("Code"));
            holder.tvName.setText(item.get("Name"));
            holder.tvTradeVolume.setText("成交量：" + formatNumber(item.get("TradeVolume")));
            holder.tvTradeValue.setText("成交值：" + formatMoney(item.get("TradeValue")));
            holder.tvClosingPrice.setText("收盤價：" + formatPrice(item.get("ClosingPrice")));
            
            // 設置漲跌顯示
            String change = item.get("Change");
            holder.tvChange.setText("漲跌：" + change);
            try {
                double changeValue = Double.parseDouble(change);
                if (changeValue > 0) {
                    holder.tvChange.setTextColor(getResources().getColor(R.color.green));
                    holder.tvClosingPrice.setTextColor(getResources().getColor(R.color.green));
                } else if (changeValue < 0) {
                    holder.tvChange.setTextColor(getResources().getColor(R.color.red));
                    holder.tvClosingPrice.setTextColor(getResources().getColor(R.color.red));
                } else {
                    holder.tvChange.setTextColor(getResources().getColor(R.color.white));
                    holder.tvClosingPrice.setTextColor(getResources().getColor(R.color.white));
                }
            } catch (NumberFormatException e) {
                holder.tvChange.setTextColor(getResources().getColor(R.color.white));
                holder.tvClosingPrice.setTextColor(getResources().getColor(R.color.white));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), more_info.class);
                    // 傳遞所有資料
                    for (String key : item.keySet()) {
                        intent.putExtra(key, item.get(key));
                    }
                    v.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
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
    }
}
