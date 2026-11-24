# Android Socket Chatroom 

### 專案介紹｜Project Overview

本專案使用 **Android Studio 與 Java** 開發多人即時聊天室，透過 TCP Socket 建立 Server 與 Client 之間的網路連線，實現多位使用者同時聊天的功能。

系統採用 Client–Server 架構，由 Server 負責管理連線、接收訊息並廣播給所有參與者。

### 主要功能｜Features

**Server**
- 自訂 Server 名稱並啟動聊天室
- 支援兩位以上 Client 同時連線
- 即時接收與廣播聊天訊息
- 顯示使用者加入、離開通知
- 關閉 Server 時通知所有 Client

**Client**
- 輸入使用者名稱、Server IP 與 Port
- 連線至指定聊天室
- 即時發送與接收文字訊息
- 顯示聊天室成員的進出通知
- 可主動離開聊天室並正常斷線

### 使用技術｜Technologies

- **Language：** Java
- **IDE：** Android Studio
- **UI：** XML Layout
- **Network：** TCP Socket
- **Multithreading：** UI Thread / Worker Thread
- **Data Transfer：** BufferedReader / PrintWriter

### 專案架構｜Project Structure

- `app/`：Android Client
- `server/`：Android Server
- `MainActivity.java`：登入與連線設定
- `MainActivity2.java`：聊天室功能與 Socket 通訊

### 操作方式｜How to Run

1. 使用 Android Studio 開啟專案。
2. 選擇 `server` Module，啟動 Server App。
3. 輸入 Server 名稱並按下 START。
4. 選擇 `app` Module，啟動 Client App。
5. 輸入 Client 名稱、IP 與 Port。
6. 按 CONNECT 加入聊天室。
7. 開始多人即時聊天。

**連線設定**

- Server Port：`6100`
- Client IP：`127.0.0.1`（搭配 ADB Reverse）
- Client Port：`6100`

多台 Android 模擬器測試時，需依照實際裝置編號設定 ADB Forward 與 Reverse。
