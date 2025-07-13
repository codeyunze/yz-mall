#!/bin/bash

app_name="yz-unqid-startup"
app_version="0.0.1-SNAPSHOT"
app_port="30008"
app_active=test

java_opts="-Xms1536m -Xmx1536m -Xmn512m -Xss256K"

nohup java -server -Duser.timezone=Asia/Shanghai -jar $java_opts ${app_name}-${app_version}.jar --spring.profiles.active=$app_active --server.port=$app_port --spring.application.name=$app_name >> console.log 2>&1 &

echo "${app_name}-${app_version}服务启动成功"