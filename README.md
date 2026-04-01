# How to Use

## DevContainerの起動

1. Project を vscode で開く
2. project root に `.devcontainer`, `server-a`を置く
3. コマンドパレットから`コンテナとして再度開く`を選択

## コンテナの起動方法

dev コンテナのみ起動（VS Code アタッチ用）

`docker compose up dev -d`

製品ごとにサーバを手動起動する

```
docker compose --profile product-a up -d   # 製品Aのサーバコンテナ全台
docker compose --profile product-b up -d   # 製品Bのサーバコンテナ全台
docker compose --profile product-c up -d   # 製品Cのサーバコンテナ全台
```

個別に起動する場合

`docker compose up product-a-1 -d`

## ディレクトリ構成
```
project-root/
 ├─ .devcontainer/
 │   ├─ devcontainer.json
 │   └─ Dockerfile          ← 開発用（Oracle JDK 21 + 開発ツール）
 ├─ .docker/
 │   └─ server/
 │       └─ Dockerfile      ← サーバ共通ベース（Oracle JDK 21 + Tomcat）
 ├─ docker-compose.yml
 └─ lib/
     └─ common/             ← 共通 jar（両方の Dockerfile が参照）
```