## サーバ差分調査後に変更

- Dockerfile 
  - `FROM eclipse-temurin:21-jdk-jammyJDK` バージョンを確定値に変更
  - `mvn install:install-file` の `-DgroupId` 等共通 jar の命名ルールに合わせる
- docker-compose.ymlimage
  - `tomcat:10-jdk21Tomcat` バージョンを確定値に変更

## サーバの差分調査

- JDK のバージョン
  - サーバ間でバージョンが揃っているか。たしかjdk-21
- Tomcat のバージョンと設定
  - `server.xml` や `context.xml` にサーバごとの差分があるか。共通設定と個別設定を分ける。
- 依存ライブラリや共有クラス
  - 複数サーバで共通の jar を使っているか。共通ライブラリがある場合、ビルド手順に反映する。
- 環境変数・JVM オプション 
  - メモリ設定やシステムプロパティにサーバごとの違いがあるか。

## docker image の作成方針

差分のある要素をリストにする

docker-compose.yml の環境変数で差分を吸収
差分が大きい場合 → ベースイメージを1つ作り、各サーバ用の Dockerfile でそれを継承する構成

## devcontainer.json

拡張機能を`extentions`に追加。

## 確認内容
- [] Serverは全て起動するか
- [] Devコンテナからserverへ通信できるか
- [] Serverコンテナ間の通信ができるか