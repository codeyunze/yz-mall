FROM registry.cn-guangzhou.aliyuncs.com/devyunze/openjdk:25-ea-17-jdk-slim
MAINTAINER yunze
ADD ./target/*.jar /tmp/
RUN cd /tmp && mv *.jar app.jar
RUN mv /tmp/app.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]