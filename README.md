# How to Use

## コンテナの起動方法

dev コンテナのみ起動する場合（VS Code アタッチ用）

`docker compose up dev -d`

製品ごとにサーバを手動起動する場合

```
docker compose --profile product-a up -d   # 製品Aのサーバコンテナ全台
docker compose --profile product-b up -d   # 製品Bのサーバコンテナ全台
docker compose --profile product-c up -d   # 製品Cのサーバコンテナ全台
```

コンテナを個別に起動する場合

`docker compose up product-a-1 -d`

## サーバのデプロイ方法

## サーバへのアクセス方法

## <a id="directory-conf">ディレクトリ構成</a>
```
project-root/
 ├─ .devcontainer/
 │   ├─ devcontainer.json
 │   └─ Dockerfile          ← 開発用（Oracle JDK 21 + 開発ツール）
 ├─ .docker/
 │   ├─ apache/
 │   │   └─ Dockerfile      ← リバースプロキシのベース（Apache）
 │   └─ product-a/server-1/
 │       └─ Dockerfile      ← 製品ごとのサーバ共通ベース（Oracle JDK 21 + Tomcat）
 │
 ├─ lib/
 │   └─ common/             ← 共通 jar（dev&各server Dockerfile が参照）
 │
 ├─ product-a/
 │   ├──.git/
 │   ├── server1/
 │   │    ├── src/          ← Java ソースコード
 │   │    ├── pom.xml
 │   │    └── conf/
 │   │         └── server.xml           ← Tomcat 設定
 │   └── server2/
 │        ├── src/
 │        ├── pom.xml
 │        └── conf/
 │             └── server.xml
 ├─ 各プロジェクト
 │   └─ ...
 │
 ├── docker-compose.yml
 └── deploy.sh
```

## 環境構築

## wslのインストール
1. Microsoft Store からインストール
2. wslのデフォルトユーザの登録

`/etc/wsl.conf` に以下を書く。
```
[user]
default=vscode
```

## DevContainerのインストール
- vscode 拡張機能 からインストール

## DevContainerの起動

1. 全てのプロジェクトをvscode で開く
2. プロジェクトルート に [ディレクトリ構成](#directory-conf) に従ってディレクトリを配置
3. コマンドパレットから `コンテナとして再度開く` を選択

## 開発環境セットアップガイド

## 目次

1. [前提・全体構成](#1-前提全体構成)
2. [WSL のインストール](#2-wsl-のインストール)
3. [Docker のインストール](#3-docker-のインストール)
4. [リポジトリのクローン](#4-リポジトリのクローン)
5. [環境変数ファイルの確認](#5-環境変数ファイルの確認)
6. [コンテナの起動](#6-コンテナの起動)
7. [VS Code Dev Container への接続](#7-vs-code-dev-container-への接続)
8. [動作確認](#8-動作確認)
9. [日常的な操作](#9-日常的な操作)
10. [トラブルシューティング](#10-トラブルシューティング)

---

## 1. 前提・全体構成

```
Windows
└── WSL2 (Debian)
    └── Docker Engine
        ├── dev         # 開発コンテナ（Java/Node/Maven/zsh）
        ├── proxy       # Apache リバースプロキシ（port 8080）
        └── product-a-1 # Tomcat サーバ（profile: product-a）
```

| コンテナ | 役割 | 公開ポート |
|---|---|---|
| `dev` | VS Code がアタッチする開発環境 | なし |
| `proxy` | Apache。外部からのリクエストを各 Tomcat へ振り分ける | `8080` |
| `product-a-1` | Tomcat 10 + WAR の実行環境 | なし（proxy 経由のみ） |

---

## 2. WSL のインストール

> **対象:** Windows 10 (21H2 以降) / Windows 11

### 2-1. WSL 本体のインストール

PowerShell を **管理者として実行** し、以下を入力します。

```powershell
wsl --install -d Debian
```

インストール完了後、PC を再起動します。

### 2-2. 再起動後の初期設定

再起動すると Debian のセットアップ画面が開きます。  
ユーザー名とパスワードを設定してください（Windows のものと別でも構いません）。

### 2-3. WSL バージョンの確認

PowerShell で以下を実行し、`VERSION` が `2` であることを確認します。

```powershell
wsl -l -v
```

`1` と表示された場合は以下で変換します。

```powershell
wsl --set-version Debian 2
```

---

## 3. Docker のインストール

WSL の Debian ターミナルを開き、以下を順に実行します。

### 3-1. 必要パッケージの取得

```bash
sudo apt-get update
sudo apt-get install -y ca-certificates curl gnupg
```

### 3-2. Docker の公式 GPG キー追加

```bash
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/debian/gpg \
  | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
```

### 3-3. リポジトリ登録

```bash
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/debian \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" \
  | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

### 3-4. Docker Engine のインストール

```bash
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io \
  docker-buildx-plugin docker-compose-plugin
```

### 3-5. 現在のユーザーを docker グループへ追加

```bash
sudo usermod -aG docker $USER
```

**ターミナルを一度閉じて開き直す**か、以下で即時反映します。

```bash
newgrp docker
```

### 3-6. インストール確認

```bash
docker --version
docker compose version
```

以下のように表示されれば OK です（バージョン番号は異なっても構いません）。

```
Docker version 26.x.x, build xxxxxxx
Docker Compose version v2.x.x
```

---

## 4. リポジトリのクローン

WSL の Debian ターミナルで、作業ディレクトリへ移動してクローンします。  
（例: ホームディレクトリ直下に `projects` フォルダを作る場合）

```bash
mkdir -p ~/projects && cd ~/projects
git clone <リポジトリの URL>
cd <リポジトリ名>
```

> **注意:** Windows 側（`/mnt/c/...`）ではなく、**WSL 側のファイルシステム**（`~/` など）に  
> クローンしてください。Windows 側だとファイル I/O が遅くなりビルド時間が大幅に伸びます。

---

## 5. 環境変数ファイルの確認

プロジェクトルートの `.env` を確認します。通常は変更不要です。

```bash
cat .env
```

```
UID=1000
GID=1000
USERNAME=vscode
DB_SCHEMA=TEST_SCHEMA
DOCKER_GID=997
```

`UID` / `GID` が自分のユーザーと異なる場合は合わせて変更します。

```bash
# 自分の UID/GID を確認する
id
```

---

## 6. コンテナの起動

### 6-1. 基本セット（dev + proxy）の起動

VS Code の Dev Container を使う場合は [7 章](#7-vs-code-dev-container-への接続) へ進んでくだい。  
ターミナルから起動したい場合は以下を実行します。

```bash
docker compose up -d
```

### 6-2. 製品サーバ（Tomcat）を追加で起動する

製品ごとに Docker Compose の **profile** で管理しています。  
必要な製品のみ起動できます。

```bash
# 製品A（Tomcat × 1）を起動
docker compose --profile product-a up -d
```

### 6-3. 起動しているコンテナの確認

```bash
docker compose ps
```

`State` が `running` または `healthy` であれば起動成功です。

```
NAME          IMAGE        STATUS                    PORTS
dev           dev-dev      Up 3 minutes
proxy         httpd:2.4    Up 3 minutes              0.0.0.0:8080->8080/tcp
product-a-1   ...          Up 2 minutes (healthy)
```

---

## 7. VS Code Dev Container への接続

### 7-1. 必要な VS Code 拡張のインストール

VS Code の拡張機能タブで以下を検索してインストールします。

- `ms-vscode-remote.remote-containers` （Dev Containers）
- `ms-vscode-remote.remote-wsl` （WSL）

### 7-2. WSL 上のプロジェクトを開く

1. VS Code 左下の `><` アイコンをクリック
2. **「WSL への接続」** を選択
3. WSL 内でプロジェクトフォルダを開く（`ファイル > フォルダを開く`）

### 7-3. Dev Container を開く

1. VS Code 左下の `><` アイコンをクリック
2. **「コンテナで再度開く」** を選択
3. 初回はイメージのビルドが走ります（10〜20 分程度かかります）

ビルドが完了すると、`dev` コンテナと `proxy` コンテナが自動で起動し、  
VS Code がコンテナ内にアタッチされた状態になります。

---

## 8. 動作確認

### 8-1. proxy コンテナが応答しているか確認

ブラウザまたは WSL ターミナルから確認します。

```bash
curl -I http://localhost:8080/
```

`HTTP/1.1 200 OK` または `403 Forbidden` が返れば proxy は起動しています。  
（デフォルトは DocumentRoot 未設定のため 403 になります）

### 8-2. WAR をビルドしてデプロイする

VS Code のコマンドパレット（`Ctrl + Shift + P`）を開き、  
`Tasks: Run Task` を選択します。

| タスク名 | 内容 |
|---|---|
| `build` | Maven で WAR を生成（`Ctrl+Shift+B`） |
| `deploy` | WAR を Tomcat の webapps へコピー |
| `build & deploy` | ビルドとデプロイを連続実行 |

または VS Code の統合ターミナルから直接実行もできます。

```bash
# プロジェクトルートで実行
mvn clean package -DskipTests -f echo-server/pom.xml
cp echo-server/target/echo-server.war /deploy/product-a/server1/
```

### 8-3. アプリケーションへのアクセス確認

```bash
curl http://localhost:8080/echo-server/
```

### 8-4. Tomcat のログを確認する

VS Code のコマンドパレットから `Tasks: Run Task` > `tomcat:log` を選択するか、  
ターミナルから直接実行します。

```bash
docker compose logs -f product-a-1
```

---

## 9. 日常的な操作

### コンテナの停止

```bash
# 全コンテナ停止（ボリュームは保持）
docker compose --profile product-a down

# 製品A だけ停止
docker compose stop product-a-1
```

### コンテナの再ビルド

Dockerfile を変更した場合は再ビルドが必要です。

```bash
docker compose --profile product-a up -d --build
```

### Maven キャッシュのクリア

```bash
docker volume rm <プロジェクト名>_maven-cache
```

---

## 10. トラブルシューティング

### `docker: permission denied` と表示される

docker グループへの追加が反映されていません。ターミナルを開き直すか以下を実行してください。

```bash
newgrp docker
```

### コンテナが `unhealthy` のまま起動しない

Tomcat の起動に時間がかかっている可能性があります。  
`docker-compose.yml` の `start_period` を延ばすか、ログを確認します。

```bash
docker compose logs product-a-1
```

### ポート 8080 が使用中と言われる

Windows 側でポートが使われています。以下で確認します。

```powershell
# PowerShell
netstat -ano | findstr :8080
```

使用中のプロセスを終了するか、`docker-compose.yml` のポート番号を変更してください。

### WSL 側のファイルが Windows エクスプローラーから見当たらない

エクスプローラーのアドレスバーに `\\wsl$\Debian` と入力すると WSL のファイルシステムにアクセスできます。