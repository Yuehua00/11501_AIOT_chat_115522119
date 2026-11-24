package com.example.hw_chatroom;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MainActivity2 extends AppCompatActivity {

    private TextView tvMessages;
    private EditText editMessage;
    private Button btnSend;
    private Button btnLeave;
    private ScrollView scrollView;

    private volatile Socket socket;
    private volatile PrintWriter out;
    private volatile boolean leaving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // 1. 連接第二頁的元件
        tvMessages = findViewById(R.id.tvMessages);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);
        btnLeave = findViewById(R.id.btnLeave);
        scrollView = findViewById(R.id.scrollView2);

        // 2. 取得第一頁輸入的資料
        String name = getIntent().getStringExtra("name");
        String ip = getIntent().getStringExtra("ip");
        int port = getIntent().getIntExtra("port", -1);

        if (name == null || ip == null || port == -1) {
            finish();
            return;
        }

        // 尚未連線時，不能發送訊息
        btnSend.setEnabled(false);

        // 3. 監聽 SEND 按鈕
        btnSend.setOnClickListener(v -> sendMessage());

        // 4. 監聽 LEAVE 按鈕
        btnLeave.setOnClickListener(v -> leaveChat());

        // 5. 開始連線
        appendMessage("正在連線至 " + ip + ":" + port);
        connectToServer(name, ip, port);
    }

    // 6. 在背景執行緒連線 Server
    private void connectToServer(
            String name, String ip, int port) {

        new Thread(() -> {
            try {
                Socket connected = new Socket();
                socket = connected;

                connected.connect(
                        new InetSocketAddress(ip, port),
                        5000
                );

                if (leaving) {
                    connected.close();
                    return;
                }

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                connected.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                );

                PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(
                                connected.getOutputStream(),
                                StandardCharsets.UTF_8
                        ), true
                );

                out = writer;

                // 告訴 Server 自己的名稱
                writer.println("JOIN|" + name);

                // 回到 UI Thread 更新畫面
                runOnUiThread(() -> {
                    btnSend.setEnabled(true);
                    appendMessage("已連線至 Server");
                });

                // 持續接收 Server 訊息
                String line;

                while ((line = in.readLine()) != null) {
                    String message = line;

                    runOnUiThread(() -> {
                        appendMessage(message);
                    });
                }

            } catch (IOException e) {

                runOnUiThread(() -> {
                    if (!leaving) {
                        appendMessage(
                                "連線失敗或中斷：" +
                                        e.getMessage()
                        );
                    }
                });

            } finally {
                closeSocket();

                runOnUiThread(() -> {
                    btnSend.setEnabled(false);
                    if (!leaving) {
                        appendMessage("與 Server 已斷線");
                    }
                });
            }
        }).start();
    }

    // 7. 傳送聊天訊息
    private void sendMessage() {

        String message = editMessage.getText()
                .toString().trim()
                .replace("\r", " ")
                .replace("\n", " ");

        if (message.isEmpty()) {
            return;
        }

        editMessage.setText("");

        new Thread(() -> {
            PrintWriter writer = out;

            if (writer != null && !leaving) {
                synchronized (writer) {
                    writer.println("MSG|" + message);

                    if (writer.checkError()) {
                        runOnUiThread(() ->
                                appendMessage("訊息傳送失敗")
                        );
                    }
                }
            }
        }).start();
    }

    // 8. 正常離開聊天室
    private void leaveChat() {
        leaving = true;
        btnSend.setEnabled(false);
        btnLeave.setEnabled(false);

        new Thread(() -> {
            PrintWriter writer = out;

            if (writer != null) {
                synchronized (writer) {
                    writer.println("LEAVE");
                }
            }

            closeSocket();
            runOnUiThread(() -> finish());
        }).start();
    }

    // 9. 更新聊天紀錄
    private void appendMessage(String message) {
        tvMessages.append(message + "\n");

        scrollView.post(() ->
                scrollView.fullScroll(View.FOCUS_DOWN)
        );
    }

    // 10. 關閉 Socket
    private void closeSocket() {
        Socket current = socket;
        socket = null;

        if (current != null) {
            try {
                current.close();
            } catch (IOException ignored) {
            }
        }

        out = null;
    }

    @Override
    protected void onDestroy() {
        leaving = true;
        closeSocket();
        super.onDestroy();
    }
}
