#!/bin/bash
# 服务名称
serviceName=$1
# 版本号
version=$2
# 运行jar包地址
address=$3
if [ -z "$address" ]; then
    address=.
fi

docker build -t $serviceName:$version $address