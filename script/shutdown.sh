#!/bin/bash

app_name="yz-unqid-startup"
app_port="30008"

pid=$(ps -ef | grep $app_port | grep -v grep | awk '{print $2}')
if [ -n "$pid" ]; then
  echo "stop ${app_name}.jar, port:$app_port, pid:$pid"
  kill -9 "$pid"
fi