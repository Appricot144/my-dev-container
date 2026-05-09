#!/bin/bash
# ============================================================
# 初回デプロイ: WAR を展開して webapps に配置する
# ============================================================
set -e

PROJECT=${1:-Echo-RestAPI}
DEPLOY_DIR=${2:-/deploy/echo/rest-api}
WAR_PATH="$HOME/Echo/${PROJECT}/target/echo-restapi.war"

echo ">>> ビルド中: ${PROJECT}"
mvn clean package -DskipTests -f "${PROJECT}/pom.xml"

echo ">>> 展開中: ${WAR_PATH} → ${DEPLOY_DIR}"
rm -rf "${DEPLOY_DIR:?}"/*
cd "${DEPLOY_DIR}"
jar -xf "${WAR_PATH}"

echo ">>> デプロイ完了"
echo "    以降の変更は以下のタスクで反映できます:"
echo "    - Javaの変更  : Tasks > compile"
echo "    - 静的リソース : Tasks > deploy:static"