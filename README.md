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
