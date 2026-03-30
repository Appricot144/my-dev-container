# ────────────────────────────────────────────────────────────
# JDK バージョンはサーバ差分調査の結果で確定する
# 現状はプレースホルダー（例：21）
# ────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-jammy

# ── システムツール ────────────────────────────────────────────
RUN apt-get update && apt-get install -y \
    maven \
    git \
    curl \
    unzip \
    sudo \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# ── vscode ユーザー ───────────────────────────────────────────
# WSL 側の UID と揃えてファイル権限の問題を防ぐ
# docker-compose.yml の build.args から渡す
ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=1000

RUN groupadd --gid $USER_GID $USERNAME \
    && useradd --uid $USER_UID --gid $USER_GID -m $USERNAME \
    && echo "$USERNAME ALL=(ALL) NOPASSWD:ALL" \
       >> /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME

# ── Volta（Node.js バージョン管理）────────────────────────────
# 将来バージョン管理ツールが変わる場合はこのブロックを差し替える
ENV VOLTA_HOME=/home/$USERNAME/.volta
ENV PATH=$VOLTA_HOME/bin:$PATH

RUN curl https://get.volta.sh | bash -s -- --skip-setup

# Node.js（LTS）と pnpm を Volta 経由でインストール
# バージョンはチームで統一して変更する
RUN volta install node@20 \
    && volta install pnpm

# ── 共通 jar ──────────────────────────────────────────────────
# 更新頻度が低いのでイメージに焼き込む
# 更新時は `docker compose up --build` で再ビルドする
#
# プロジェクトルートの lib/common/ に jar を置いておく
# groupId・artifactId・version はチームのルールに合わせて変更する
COPY lib/common/ /opt/common-lib/

RUN for jar in /opt/common-lib/*.jar; do \
      name=$(basename "$jar" .jar); \
      mvn install:install-file \
        -Dfile="$jar" \
        -DgroupId=com.example \
        -DartifactId="$name" \
        -Dversion=1.0 \
        -Dpackaging=jar \
        -q; \
    done

# ── Maven キャッシュ用ディレクトリ ────────────────────────────
# docker-compose.yml の maven-cache ボリュームをマウントする先
RUN mkdir -p /home/$USERNAME/.m2 \
    && chown -R $USERNAME:$USERNAME /home/$USERNAME/.m2

USER $USERNAME
WORKDIR /workspace