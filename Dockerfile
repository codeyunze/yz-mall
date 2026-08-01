FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 通用 JVM 与 Spring Boot 参数：
# - JVM_XMS / JVM_XMX / JVM_XMN 可通过运行容器时的环境变量覆盖
# - JAVA_OPTS 用于附加 GC、调试等参数
ENV JVM_XMS=512m \
    JVM_XMX=1024m \
    JVM_XMN=256m \
    JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=prod \
    # Nacos 相关参数（mall-gateway 会用到，其他服务可忽略或照样通过 env 覆盖）
    SPRING_CLOUD_NACOS_SERVER_ADDR=yunze.com:8848 \
    SPRING_CLOUD_NACOS_USERNAME=nacos \
    SPRING_CLOUD_NACOS_PASSWORD="" \
    SPRING_CLOUD_NACOS_CONFIG_NAMESPACE=ff6944dd-b11f-4403-8245-6c621b2f54c8 \
    SPRING_CLOUD_NACOS_DISCOVERY_NAMESPACE=ff6944dd-b11f-4403-8245-6c621b2f54c8

# APP_MODULE 为 Maven 模块目录名，同时也假定为打包后的 jar 前缀
# 示例：
#   APP_MODULE=mall-gateway  =>  mall-gateway/target/mall-gateway-*.jar
ARG APP_MODULE=mall-gateway

# 将对应模块的 fat jar 拷贝到镜像中
COPY ${APP_MODULE}/target/${APP_MODULE}-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Xms${JVM_XMS} -Xmx${JVM_XMX} -Xmn${JVM_XMN} ${JAVA_OPTS} -jar /app/app.jar"]

