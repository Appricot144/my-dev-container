# ────────────────────────────────────────────────────────────
# サーバコンテナ共通ベースイメージ
# Oracle JDK 21 + Tomcat
#
# 使用前に Oracle Container Registry へのログインが必要
#   docker login container-registry.oracle.com
#
# チーム内のプライベートレジストリができたら
# oracle のレジストリをミラーリングして、そこから取得
# ────────────────────────────────────────────────────────────
FROM container-registry.oracle.com/java/jdk:21

# ── システムツール ────────────────────────────────────────────
RUN apt-get update && apt-get install -y \
    curl \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# ── Tomcat ───────────────────────────────────────────────────
# バージョンは仮置き
ARG TOMCAT_VERSION=10.1.18

RUN curl -fsSL \
    https://downloads.apache.org/tomcat/tomcat-10/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz \
    | tar -xz -C /opt \
    && mv /opt/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat

ENV CATALINA_HOME=/opt/tomcat
ENV PATH=$CATALINA_HOME/bin:$PATH

# ── 共通 jar ──────────────────────────────────────────────────
# 全サーバで共通して使う jar をイメージに焼き込む
# 更新時は `docker compose up --build` で再ビルドする
COPY lib/common/ /opt/tomcat/lib/

# ── デフォルト起動コマンド ────────────────────────────────────
CMD ["catalina.sh", "run"]