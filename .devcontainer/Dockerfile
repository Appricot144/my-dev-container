# ────────────────────────────────────────────────────────────
# base image: debian bookworm slim 
#  + oracle jdk 21 + tomcat 10.1.54 + etc ...
# 
# チーム内のプライベートレジストリができたら、そこから取得
# ────────────────────────────────────────────────────────────

FROM debian:12-slim

# ── User ───────────────────────────────────────────
# docker-compose.yml の build.args から渡す 
# container user (default: uid=1000, gid=1000, username=vscode)
ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=1000
ARG DOCKER_GID=997
RUN groupadd --gid $USER_GID $USERNAME \
    && useradd --uid $USER_UID --gid $USER_GID -m $USERNAME \
    && groupadd --gid $DOCKER_GID docker \
    && usermod -aG docker $USERNAME

# ── locale ────────────────────────────────────────────────────
RUN apt-get update && apt-get install -y --no-install-recommends \
    locales \
    tzdata \
    && apt-get clean && rm -rf /var/lib/apt/lists/* \
    && sed -i -e 's/# \(ja_JP.UTF-8\)/\1/' /etc/locale.gen \
    && locale-gen ja_JP.UTF-8 \
    && update-locale LANG=ja_JP.UTF-8 \
    && ln -sf /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

ENV LANG=ja_JP.UTF-8 \
    LC_ALL=ja_JP.UTF-8 \
    TZ=Asia/Tokyo

# ── Commands ────────────────────────────────────────────
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    maven \
    git \
    zsh \
    sudo \
    && echo "$USERNAME ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers \
    && apt-get clean && rm -rf /var/lib/apt/lists/* \
    && chsh -s /bin/zsh $USERNAME

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

# ── Volume Directory ────────────────────────────
# deploy volume を追加する場合は、ここに追加
RUN mkdir -p /home/$USERNAME/.m2 \
    && mkdir -p /deploy/product-a/server1/ \
    && chown -R $USERNAME:$USERNAME /deploy \
    && chown -R $USERNAME:$USERNAME /home/$USERNAME/.m2

# ── 共通 jar ──────────────────────────────────────────────────
# 更新頻度が低いのでイメージに焼き込む
# 更新時は `docker compose up --build` で再ビルドする
#
# プロジェクトルートの lib/common/ に jar を配置

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

COPY entry.sh /usr/local/bin/entry.sh
RUN chmod a+x /usr/local/bin/entry.sh \
    && chown $USERNAME /usr/local/bin/entry.sh

USER $USERNAME
WORKDIR /workspace