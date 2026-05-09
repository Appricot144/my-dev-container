#!/bin/bash
# ============================================================
# deploy.sh
#
# 使い方:
#   ./deploy.sh --auto              # （初回のみ）
#   ./deploy.sh --full   <project>  # フルビルド＆デプロイ
#   ./deploy.sh --java   <project>  # Javaクラスのみ再コンパイル
#   ./deploy.sh --static <project>  # 静的リソースのみコピー
#   ./deploy.sh --vite   <project>  # Viteビルド＋静的リソースコピー
#
#   project: Echo-RestAPI | Echo-Web
# ============================================================
set -e

PROJECT="Echo"
WORKSPACE="${HOME}/${PROJECT}"

# ── プロジェクトごとの設定 ───────────────────────────────────
project_config() {
  local project=$1
  case $project in
    Echo-RestAPI)
      DEPLOY_DIR="/deploy/echo/rest-api"
      HAS_VITE=false
      ;;
    Echo-Web)
      DEPLOY_DIR="/deploy/echo/web"
      HAS_VITE=false
      VITE_DIR="${WORKSPACE}/Echo-Web/src/main/vite"
      ;;
    *)
      echo "ERROR: 未知のプロジェクト: ${project}"
      exit 1
      ;;
  esac
  PROJECT_DIR="${WORKSPACE}/${project}"
}

# ── フルビルド ───────────────────────────────────────────────
full_deploy() {
  local project=$1
  project_config "$project"

  echo ">>> [full] ビルド中: ${project}"
  mvn clean package -DskipTests -f "${PROJECT_DIR}/pom.xml"

  echo ">>> [full] webapps をクリーン"
  rm -rf "${DEPLOY_DIR:?}"/*

  echo ">>> [full] WAR を展開 → ${DEPLOY_DIR}"
  cd "${DEPLOY_DIR}"
  jar -xf "${PROJECT_DIR}/target/${project}.war"

  echo ">>> [full] 完了: ${project}"
}

# ── Javaクラスのみ ───────────────────────────────────────────
java_deploy() {
  local project=$1
  project_config "$project"

  if [ ! -f "${DEPLOY_DIR}/WEB-INF/web.xml" ]; then
    echo "ERROR: 未デプロイです。先に --full を実行してください"
    exit 1
  fi

  echo ">>> [java] コンパイル中: ${project}"
  mvn compile -f "${PROJECT_DIR}/pom.xml"

  echo ">>> [java] classファイルをデプロイ → ${DEPLOY_DIR}/WEB-INF/classes/"
  rsync -av --delete "${PROJECT_DIR}/target/classes/." "${DEPLOY_DIR}/WEB-INF/classes/"

  echo ">>> [java] 完了: ${project}"
}

# ── 静的リソースのみ ─────────────────────────────────────────
# src/main/webapp/WEB-INF/static/ → デプロイ先にコピー
static_deploy() {
  local project=$1
  project_config "$project"

  if [ ! -f "${DEPLOY_DIR}/WEB-INF/web.xml" ]; then
    echo "ERROR: 未デプロイです。先に --full を実行してください"
    exit 1
  fi

  local src="${PROJECT_DIR}/src/main/webapp/WEB-INF/static/"

  if [ ! -d "$src" ]; then
    echo "ERROR: 静的リソースディレクトリが見つかりません: ${src}"
    exit 1
  fi

  echo ">>> [static] コピー: ${src} → ${DEPLOY_DIR}/WEB-INF/static/"
  rsync -av --delete "${src}" "${DEPLOY_DIR}/WEB-INF/static/"

  echo ">>> [static] 完了: ${project}"
}

# ── Viteビルド＋静的リソース ─────────────────────────────────
# pnpm build → build.outDir（src/main/webapp/WEB-INF/static/）に出力
# → static_deploy でデプロイ先にコピー
vite_deploy() {
  local project=$1
  project_config "$project"

  if [ "$HAS_VITE" = false ]; then
    echo ">>> [vite] Viteプロジェクトがありません。スキップ: ${project}"
    exit 0
  fi

  if [ ! -f "${DEPLOY_DIR}/WEB-INF/web.xml" ]; then
    echo "ERROR: 未デプロイです。先に --full を実行してください"
    exit 1
  fi

  echo ">>> [vite] ビルド中: ${VITE_DIR}"
  cd "${VITE_DIR}"
  pnpm build
  # build.outDir = src/main/webapp/WEB-INF/static/ 

  static_deploy "$project"

  echo ">>> [vite] 完了: ${project}"
}

# ── 自動（postStartCommand用・初回のみ）─────────────────────
# WEB-INF/web.xml が存在しない場合だけフルビルドを実行する
auto_deploy() {
  for project in Echo-RestAPI Echo-Web; do
    project_config "$project"

    if [ -f "${DEPLOY_DIR}/WEB-INF/web.xml" ]; then
      echo ">>> [auto] デプロイ済みのためスキップ: ${project}"
    else
      echo ">>> [auto] 未デプロイのためフルビルドを開始: ${project}"
      full_deploy "$project"
    fi
  done
}

# ── エントリポイント ─────────────────────────────────────────
MODE=${1:-"--help"}
PROJECT=${2:-""}

case $MODE in
  --auto)   auto_deploy ;;
  --full)   full_deploy   "$PROJECT" ;;
  --java)   java_deploy   "$PROJECT" ;;
  --static) static_deploy "$PROJECT" ;;
  --vite)   vite_deploy   "$PROJECT" ;;
  *)
    echo "使い方:"
    echo "  ./deploy.sh --auto              # 初回のみフルデプロイ（postStartCommand用）"
    echo "  ./deploy.sh --full   <project>  # フルビルド＆デプロイ"
    echo "  ./deploy.sh --java   <project>  # Javaクラスのみ"
    echo "  ./deploy.sh --static <project>  # 静的リソースのみ"
    echo "  ./deploy.sh --vite   <project>  # Viteビルド＋静的リソース"
    echo ""
    echo "  project: Echo-RestAPI | Echo-Web"
    ;;
esac