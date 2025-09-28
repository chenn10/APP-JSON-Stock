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
        ProgressDialog dialog = ProgressDialog.show(this, "讀取中", "請稍候", true);
        new Thread(() -> {
            try {
                URL url = new URL(catchData);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
                    String code = stock.getString("Code");
                    String name = stock.getString("Name");
                    String tradeVolume = stock.getString("TradeVolume");
                    String tradeValue = stock.getString("TradeValue");
                    // 繼續解析其他欄位...

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("Code", code);
                    hashMap.put("Name", name);
                    hashMap.put("TradeVolume", tradeVolume);
                    hashMap.put("TradeValue", tradeValue);
                    // 將其他欄位加入 hashMap...

                    arrayList.add(hashMap);
                }

                Log.d(TAG, "catchData: " + arrayList);

                runOnUiThread(() -> {
                    dialog.dismiss();
                    RecyclerView recyclerView;
                    MyAdapter myAdapter;
                    recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
                    myAdapter = new MyAdapter();
                    recyclerView.setAdapter(myAdapter);
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvCode, tvName, tvTradeVolume, tvTradeValue;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCode = itemView.findViewById(R.id.tvCode);
                tvName = itemView.findViewById(R.id.tvName);
                tvTradeVolume = itemView.findViewById(R.id.tvTradeVolume);
                tvTradeValue = itemView.findViewById(R.id.tvTradeValue);
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
            holder.tvTradeVolume.setText("交易量：" + item.get("TradeVolume"));
            holder.tvTradeValue.setText("交易價值：" + item.get("TradeValue"));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), more_info.class);
                    intent.putExtra("Code", item.get("Code"));
                    intent.putExtra("Name", item.get("Name"));
                    intent.putExtra("TradeVolume", item.get("TradeVolume"));
                    intent.putExtra("TradeValue", item.get("TradeValue"));
                    v.getContext().startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }
}


