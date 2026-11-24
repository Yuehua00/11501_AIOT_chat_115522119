package com.example.hw_chatroom;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. 找到第一頁的元件
        EditText editName = findViewById(R.id.editName);
        EditText editIp = findViewById(R.id.editIp);
        EditText editPort = findViewById(R.id.editPort);
        Button btnConnect = findViewById(R.id.btnConnect);

        // 2. 監聽 CONNECT 按鈕
        btnConnect.setOnClickListener(view -> {

            // 3. 取得使用者輸入的資料
            String name =
                    editName.getText().toString().trim();

            String ip =
                    editIp.getText().toString().trim();

            String portText =
                    editPort.getText().toString().trim();

            // 4. 檢查是否有空白欄位
            if (name.isEmpty() || ip.isEmpty()
                    || portText.isEmpty()) {

                Toast.makeText(this,
                        "請填寫所有欄位",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // 名稱不可含分隔符號或換行
            if (name.contains("|") ||
                    name.contains("\n")) {
                Toast.makeText(this,
                        "名稱不能包含 | 或換行",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // 5. 將 Port 轉換成整數
            int port;
            try {
                port = Integer.parseInt(portText);

                if (port < 1 || port > 65535) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this,
                        "Port 必須介於 1～65535",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // 6. 切換到第二頁
            Intent intent = new Intent(
                    MainActivity.this,
                    MainActivity2.class
            );

            // 7. 傳送名稱、IP 和 Port
            intent.putExtra("name", name);
            intent.putExtra("ip", ip);
            intent.putExtra("port", port);

            startActivity(intent);
        });
    }
}
