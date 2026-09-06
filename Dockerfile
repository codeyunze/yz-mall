# 通用 Java 服务运行时镜像（yz-mall 各 startup / gateway 共用）
# 构建上下文只需包含 app.jar（由 CI 从模块 target 拷贝），勿把整仓作为 context。
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="yunze"

WORKDIR /app

# JVM / Spring 基础参数可通过容器环境变量覆盖；
# Nacos、数据源等业务配置请在部署时注入，不要写入镜像。
# GC 日志与 heap dump 落在 /app/logs，部署时可挂载该目录便于排查。
ENV TZ=Asia/Shanghai \
    JVM_XMS=512m \
    JVM_XMX=1024m \
    JVM_GC_OPTS="-XX:+UseG1GC -Xlog:gc*:file=/app/logs/gc.log:time,uptime,level,tags:filecount=10,filesize=5M -XX:HeapDumpPath=/app/logs -XX:+HeapDumpOnOutOfMemoryError -XX:+ExitOnOutOfMemoryError -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai" \
    JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=prod

COPY app.jar /app/app.jar

RUN apk add --no-cache tzdata curl busybox-extras \
    && addgroup -S app \
    && adduser -S -G app app \
    && mkdir -p /app/logs \
    && chown -R app:app /app

USER app

VOLUME ["/app/logs"]

# 实际监听端口由各服务配置决定（如 25001+），此处仅作文档占位
EXPOSE 8080

# exec 保证容器收到 SIGTERM 时能转发给 Java 进程，便于优雅停机
ENTRYPOINT ["sh", "-c", "exec java -Xms${JVM_XMS} -Xmx${JVM_XMX} ${JVM_GC_OPTS} ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar"]
