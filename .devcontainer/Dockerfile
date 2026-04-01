# ────────────────────────────────────────────────────────────
# Oracle JDK 21
# 使用前に Oracle Container Registry へのログインが必要
#   docker login container-registry.oracle.com
#
# チーム内のプライベートレジストリができたら
# oracle のレジストリをミラーリングして、そこから取得
# ────────────────────────────────────────────────────────────
FROM container-registry.oracle.com/java/jdk:21

# ── vscode ユーザー ───────────────────────────────────────────
# WSL 側の UID と揃えてファイル権限の問題を防ぐ
# docker-compose.yml の build.args から渡す
ARG USERNAME=vscode

# デフォルトでWSL側とUIDがそろってそうなので、必要ないかもしれません
# ARG USER_UID=1000
# ARG USER_GID=1000
#
# RUN groupadd --gid $USER_GID $USERNAME \
#     && useradd --uid $USER_UID --gid $USER_GID -m $USERNAME \
#     && echo "$USERNAME ALL=(ALL) NOPASSWD:ALL" \
#        >> /etc/sudoers.d/$USERNAME \
#     && chmod 0440 /etc/sudoers.d/$USERNAME

# ── システムツール ────────────────────────────────────────────
RUN apt-get update && apt-get install -y \
    maven \
    git \
    curl \
    unzip \
    sudo \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# ── Node.js 21 ────────────────────────────────────────────────
RUN curl -fsSL https://deb.nodesource.com/setup_21.x | bash - \
    && apt-get install -y nodejs \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# pnpm 
RUN npm install -g pnpm

# ── Maven キャッシュ用ディレクトリ ────────────────────────────
# docker-compose.yml の maven-cache ボリュームのマウント先
RUN mkdir -p /home/$USERNAME/.m2 \
    && chown -R $USERNAME:$USERNAME /home/$USERNAME/.m2

# ── install global node packages ────────────────────────────
# RUN su vscode -c "source /usr/local/share/nvm/nvm.sh && npm install -g <your-package-here>" 2>&1

# ── 共通 jar ──────────────────────────────────────────────────
# 更新頻度が低いのでイメージに焼き込む
# 更新時は `docker compose up --build` で再ビルドする
#
# プロジェクトルートの lib/common/ に jar を置いておく
# groupId・artifactId・version はチームのルールに合わせて変更する

# COPY lib/common/ /opt/common-lib/

# RUN for jar in /opt/common-lib/*.jar; do \
#       name=$(basename "$jar" .jar); \
#       mvn install:install-file \
#         -Dfile="$jar" \
#         -DgroupId=com.example \
#         -DartifactId="$name" \
#         -Dversion=1.0 \
#         -Dpackaging=jar \
#         -q; \
#     done

USER $USERNAME
WORKDIR /workspace