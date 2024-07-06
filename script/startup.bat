@echo off

chcp 65001

set app_name=yz-unqid-startup
set app_version=0.0.1-SNAPSHOT
set app_port=30008
set app_active=prod

set java_opts=-Xms1536m -Xmx1536m -Xmn1024m -Xss256K -XX:SurvivorRatio=4 -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=92 -XX:+UseCMSInitiatingOccupancyOnly

title %app_name%-%app_version%-%app_port%

java -server -Duser.timezone=Asia/Shanghai -Dfile.encoding=UTF-8 -jar %java_opts% %app_name%-%app_version%.jar --server.port=%app_port% --spring.application.name=%app_name% --spring.profiles.active=%app_active%

cmd


