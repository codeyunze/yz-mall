FROM registry.cn-guangzhou.aliyuncs.com/devyunze/linux_amd64_openjdk:8
ADD *.jar /tmp/
RUN cd /tmp && mv *.jar app.jar
RUN mv /tmp/app.jar /app.jar
EXPOSE 30000
ENTRYPOINT java ${JAVA_OPTS} -jar /app.jar