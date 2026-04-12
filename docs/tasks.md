# VS Code タスク 使い方ガイド

## 目次

- [VS Code タスク 使い方ガイド](#vs-code-タスク-使い方ガイド)
  - [目次](#目次)
  - [1. はじめて環境を立ち上げる](#1-はじめて環境を立ち上げる)
    - [手順](#手順)
  - [2. コードを修正して Tomcat に反映する](#2-コードを修正して-tomcat-に反映する)
    - [手順](#手順-1)
  - [3. ログを確認する](#3-ログを確認する)
    - [Tomcat のログをリアルタイムで見る](#tomcat-のログをリアルタイムで見る)
  - [4. コンテナを停止する](#4-コンテナを停止する)
    - [製品単位（profile）でまとめて停止する](#製品単位profileでまとめて停止する)
    - [コンテナを1つだけ停止する](#コンテナを1つだけ停止する)
  - [5. Docker イメージを再ビルドする](#5-docker-イメージを再ビルドする)
    - [製品単位（profile）で再ビルドして起動する](#製品単位profileで再ビルドして起動する)
    - [コンテナを1つだけ再ビルドして起動する](#コンテナを1つだけ再ビルドして起動する)
  - [6. テストを実行する](#6-テストを実行する)
  - [タスク実行方法まとめ](#タスク実行方法まとめ)
    - [ショートカット](#ショートカット)
    - [タスク一覧](#タスク一覧)

---

## 1. はじめて環境を立ち上げる

Dev Container 起動後、Tomcat コンテナをまだ起動していない状態から始める場合。

### 手順

**① Tomcat コンテナを起動する**

`Ctrl+Shift+P` → `Tasks: Run Task` → `docker:up:profile`

- 選択肢が表示されるので `product-a` を選ぶ
- `product-a-1`（と将来的には `product-a-2`）が起動する

**② アプリをビルドして Tomcat にデプロイする**

`Ctrl+Shift+P` → `Tasks: Run Task` → `build & deploy`

- 選択肢が表示されるので対象プロジェクト（例: `echo-server`）を選ぶ
- WAR ファイルの生成 → `/deploy/product-a` へのコピーが順番に実行される
- Tomcat の `autoDeploy=true` により、コピー後に自動でデプロイされる

**③ ブラウザで確認する**

`http://localhost:8080/echo-server/`

---

## 2. コードを修正して Tomcat に反映する

開発中に最も頻繁に使うフロー。

### 手順

**① ビルドしてデプロイする**

`Ctrl+Shift+B`（デフォルトビルドタスクのショートカット）

> ※ `Ctrl+Shift+B` は `build` タスク（WAR 生成のみ）に割り当てられています。  
> デプロイまで一括で行いたい場合は、`Ctrl+Shift+P` → `Tasks: Run Task` → `build & deploy` を使ってください。

| やりたいこと | 使うタスク | 実行方法 |
|---|---|---|
| WAR を生成するだけ | `build` | `Ctrl+Shift+B` |
| WAR を生成して Tomcat に反映 | `build & deploy` | コマンドパレットから選択 |

**② Tomcat が自動で再デプロイするのを待つ**

`server.xml` の `reloadable="true"` により、WAR の置き換えを検知して自動でリロードされます。

---

## 3. ログを確認する

### Tomcat のログをリアルタイムで見る

`Ctrl+Shift+P` → `Tasks: Run Task` → `tomcat:log`

- `product-a-1` のログが専用ターミナルパネルに流れ続ける
- 起動確認やエラー調査のときに使う
- 止めるには `Ctrl+C`

---

## 4. コンテナを停止する

### 製品単位（profile）でまとめて停止する

`Ctrl+Shift+P` → `Tasks: Run Task` → `docker:down:profile`

- 停止したい profile（`product-a` / `product-b` / `product-c`）を選ぶ
- その profile に属するコンテナがすべて停止する

### コンテナを1つだけ停止する

`Ctrl+Shift+P` → `Tasks: Run Task` → `docker:down:container`

- 停止したいコンテナ（例: `product-a-1`）を選ぶ

---

## 5. Docker イメージを再ビルドする

Dockerfile を変更したときや、依存関係を更新したときに使う。

### 製品単位（profile）で再ビルドして起動する

`Ctrl+Shift+P` → `Tasks: Run Task` → `docker:rebuild:profile`

- 対象 profile を選ぶ
- イメージを再ビルドしてコンテナを起動し直す

### コンテナを1つだけ再ビルドして起動する

`Ctrl+Shift+P` → `Tasks: Run Task` → `docker:rebuild:container`

- 対象コンテナを選ぶ

> **いつ使うか**
> - Dockerfile にパッケージ追加・変更をした
> - `server.xml` 等の設定ファイルを変更した（ボリュームマウントなので再ビルド不要な場合もある）

---

## 6. テストを実行する

`Ctrl+Shift+P` → `Tasks: Run Task` → `test`

- プロジェクトルートで `mvn test` を実行する
- 結果はターミナルパネルに表示される

---

## タスク実行方法まとめ

### ショートカット

| ショートカット | 動作 |
|---|---|
| `Ctrl+Shift+B` | `build`（WAR 生成）をすぐ実行 |
| `Ctrl+Shift+P` → `Tasks: Run Task` | タスク一覧から選んで実行 |

### タスク一覧

| タスク名 | やること | 入力選択 |
|---|---|---|
| `build` | WAR を生成する | プロジェクト選択 |
| `deploy` | WAR を Tomcat にコピーする | プロジェクト選択 |
| `build & deploy` | WAR 生成 → Tomcat コピーを一括実行 | プロジェクト選択 |
| `test` | `mvn test` を実行する | なし |
| `tomcat:log` | `product-a-1` のログを流す | なし |
| `docker:up:profile` | profile 単位でコンテナを起動 | profile 選択 |
| `docker:up:container` | コンテナ単体を起動 | コンテナ選択 |
| `docker:down:profile` | profile 単位でコンテナを停止 | profile 選択 |
| `docker:down:container` | コンテナ単体を停止 | コンテナ選択 |
| `docker:rebuild:profile` | イメージ再ビルドして起動（profile） | profile 選択 |
| `docker:rebuild:container` | イメージ再ビルドして起動（単体） | コンテナ選択 |