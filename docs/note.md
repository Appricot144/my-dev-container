# 検討事項 と 作業に関するメモ

## TODO

- [x] dev container は起動するか
- [x] server container は起動するか
- [x] SpringMVCをvscodeでビルドする
- [x] deploy to each server-container
  - [x] single container
- [x] Devコンテナからserverへ通信できるか
- [x] serverコンテナ間の通信ができるか
- [ ] server time をそろえる(ntpd)
- [ ] vscode tasks
  - [ ] build
    - [x] single server (choose a project)
    - [ ] build by product
  - [ ] deploy
    - [ ] single container
    - [ ] each profiles
  - [ ] docker:up
    - [ ] single container
    - [ ] profile up
  - [ ] docker:down
  - [ ] docker:restart
  - [ ] test
  - [ ] clean maven cache
- [ ] tomcat の log を開発コンテナに流す`tail -f server.log`
- [ ] server.xml を各サーバ用に用意

## チームごとの開発環境の差分

各チームで必要なソフトウェアを洗い出すところから

## サーバ実行環境の差分

- [x] JDK のバージョン
  - oracle jdk 21 で統一
- [x] Tomcat のバージョンと設定
  - `server.xml` サーバごとの差分があるか。共通設定と個別設定を分ける。
  - 対応しなければならない差分は少なそうだが、対応すべきかは要調査
- [x] 環境変数・JVM オプション
  - メモリ設定やシステムプロパティにサーバごとの違いがあるか。
  - ここもほぼ差分はなさそう
- [ ] 実行環境にインストールが必要なソフトウェア
  - Reports Ribre Office,
  - TELAND
  - Keycloak
  - etc ...
  - `docker-compose.yml`の environments に持たせるか、各サーバコンテナのDockerfileに焼きこむ
  - 最終手段は、コンテナ起動後に実行ファイルをコンテナに送って実行させる

## docker image の作成方針

docker-compose.yml の環境変数で差分を吸収
差分が大きい場合 → ベースイメージを1つ作り、各サーバ用の Dockerfile でそれを継承する構成

- [x] base image の作成
- [ ] 各コンテナのimage作成

## devcontainer.json の設定項目

- [ ] 追加がないか検討

### extentions

- prettier
- ESLint
- Japanese language Pack
- Exension Pack for Java
- tailwind css intelliSence
- Git Lens
- Git Graph

### settings

- [ ] 環境構築手順から反映

### features

- docker-outside-of-docker
