# ────────────────────────────────────────────────────────────
# base image: debian bookworm slim 
#  + oracle jdk 21 + tomcat 10.1.54 + etc ...
# 
# チーム内のプライベートレジストリができたら、そこから取得
# ────────────────────────────────────────────────────────────

FROM debian:12-slim

# ── User ───────────────────────────────────────────
# docker-compose.yml の build.args から渡す (default: uid=1000, gid=1000, username=vscode)
ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=1000
RUN groupadd --gid $USER_GID $USERNAME \
    && useradd --uid $USER_UID --gid $USER_GID -m $USERNAME \
    && mkdir -p /etc/sudoers.d \
    && echo "$USERNAME ALL=(ALL) NOPASSWD:ALL" \
       >> /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME

# ── locale ────────────────────────────────────────────────────
# ロケールを合わせたければアンコメントして使用
# RUN apt-get update && apt-get install -y --no-install-recommends \
#     locales \
#     language-pack-ja \
#     tzdata \
#     && apt-get clean && rm -rf /var/lib/apt/lists/* \
#     && locale-gen ja_JP.UTF-8 \
#     && update-locale LANG=ja_JP.UTF-8 \
#     && ln -sf /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

# ENV LANG=ja_JP.UTF-8 \
#     LC_ALL=ja_JP.UTF-8 \
#     TZ=Asia/Tokyo

# ── Commands ────────────────────────────────────────────
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    maven \
    git \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# ── oracle jdk ────────────────────────────────────────────
RUN wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.deb \
    && dpkg -i jdk-21_linux-x64_bin.deb \
    && rm jdk-21_linux-x64_bin.deb \
    && ln -s /usr/lib/jvm/jdk-21*-oracle-x64 /usr/lib/jvm/jdk-21-oracle-x64 \
    && apt-get autoremove -y \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/jdk-21-oracle-x64 \
    PATH=$JAVA_HOME/bin:$PATH

# ── Node.js 21 ────────────────────────────────────────────────
RUN curl -fsSL https://deb.nodesource.com/setup_21.x | bash - \
    && apt-get install -y nodejs \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# pnpm 
RUN npm install -g pnpm

# ── Maven Cache Directory ────────────────────────────
# maven-cache ボリュームのマウント先
RUN mkdir -p /home/$USERNAME/.m2 \
    && chown -R $USERNAME:$USERNAME /home/$USERNAME/.m2

# ── install global node packages ────────────────────────────
# RUN su vscode -c "source /usr/local/share/nvm/nvm.sh && npm install -g <your-package-here>" 2>&1

# ── 共通 jar ──────────────────────────────────────────────────
# 更新頻度が低いのでイメージに焼き込む
# 更新時は `docker compose up --build` で再ビルドする
#
# プロジェクトルートの lib/common/ に jar を配置

# 更新するときは`docker cp path/to/file.jar`

# COPY lib/common/ /opt/common-lib/

# RUN if [ "$(ls /opt/common-lib/*.jar 2>/dev/null)" ]; then \
#       for jar in /opt/common-lib/*.jar; do \
#         name=$(basename "$jar" .jar); \
#         mvn install:install-file \
#           -Dfile="$jar" \
#           -DgroupId=com.example \
#           -DartifactId="$name" \
#           -Dversion=1.0 \
#           -Dpackaging=jar \
#           -q; \
#       done; \
#     else \
#       echo "service common jar not found. skiped."; \
#     fi

USER $USERNAME
WORKDIR /workspace