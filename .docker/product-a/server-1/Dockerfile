# ────────────────────────────────────────────────────────────
# base image: debian bookworm slim 
#  + oracle jdk 21 + tomcat 10.1.54
# 
# チーム内のプライベートレジストリができたら、そこから取得
# ────────────────────────────────────────────────────────────
FROM debian:12-slim

ARG USER_UID=1000
ARG USER_GID=1000

# ── Commands ────────────────────────────────────────────
RUN apt-get update && apt-get install -y \
    wget \
    curl \
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

# ── Tomcat ───────────────────────────────────────────────────
# バージョンは仮置き
ARG TOMCAT_VERSION=10.1.54

RUN curl -fsSL \
    https://archive.apache.org/dist/tomcat/tomcat-10/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz \
    | tar -xz -C /opt \
    && mv /opt/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat \
    && chown -R $USER_UID:$USER_UID /opt/tomcat

ENV CATALINA_HOME=/opt/tomcat
ENV PATH=$CATALINA_HOME/bin:$PATH

# ── デフォルト起動コマンド ────────────────────────────────────
CMD ["catalina.sh", "run"]