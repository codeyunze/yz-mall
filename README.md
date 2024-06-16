# 服务器分配

## 159 (10.0.12.16)
4核8G（剩余7G）

| 服务            | 主/从      | 端口    | 占用内存 | 是否部署 |
| --------------- |----------|-------| -------- | -------- |
| Redis-6.2.9    | master-3 | 8001  |          |          |
| Redis-6.2.9    | slave-2  | 8002  |          |          |
| Nacos-2.2.0     | master   | 8848  |          | 是       |
| Seata-1.6.1     | master   | 7091  |          |          |
| RabbitMQ-3.10.0 | master   | 15672 |          | 是       |

## 119 (10.0.20.12)
4核4G（剩余2G）

| 服务            | 主/从    | 端口  | 占用内存 | 是否部署     |
| --------------- | -------- | ----- | -------- | ------------ |
| Redis-6.2.9    | master-2 | 6379 |          |              |
| Redis-6.2.9    | slave-1  | 6378 |          |              |
| MySQL-8         | master   | 13306 | 1.5g     | 是           |
| RabbitMQ-3.10.0 | slave    | 15672 |          | 是（未启动） |

## 43 (10.0.20.10)
2核2G（剩余2G）

| 服务            | 主/从      | 端口    | 占用内存 | 是否部署 |
| --------------- |----------|-------| -------- | -------- |
| Redis-6.2.9    | master-1 | 6379 |          |          |
| Redis-6.2.9    | slave-3  | 6378 |          |          |
| MySQL-8         | slave    | 13306 |      |           |

## 8
2核2G（剩余2G）

| 服务            | 主/从    | 端口    | 占用内存 | 是否部署 |
| --------------- | -------- |-------| -------- | -------- |
| MySQL-8         | slave    | 13306 | 500M     | 是       |
| Elasticsearch-7 | master   | 9200  |          |          |

## 京东云

2核4G（剩余4G）

| 服务            | 主/从 | 端口  | 占用内存 | 是否部署 |
| --------------- | ----- | ----- | -------- | -------- |
| Elasticsearch-7 | slave | 9200  |          |          |
| RabbitMQ-3.10.0 | slave | 15672 |          |          |
|                 |       |       |          |          |

## 移动云

2核2G（剩余2G）

| 服务            | 主/从 | 端口  | 占用内存 | 是否部署 |
| --------------- | ----- | ----- | -------- | -------- |
| Elasticsearch-7 | slave | 9200  |          |          |
| RabbitMQ-3.10.0 | slave | 15672 |          |          |
|                 |       |       |          |          |






## 中间件信息

### redis集群

```shell
[root@VM-20-10-centos redis-6.2.9]# src/redis-cli -a foobared --cluster create --cluster-replicas 1 10.0.20.10:6379 10.0.20.10:6378 10.0.20.12:6379 10.0.20.12:6378 10.0.12.16:6379 10.0.12.16:6378
Warning: Using a password with '-a' or '-u' option on the command line interface may not be safe.
>>> Performing hash slots allocation on 6 nodes...
Master[0] -> Slots 0 - 5460
Master[1] -> Slots 5461 - 10922
Master[2] -> Slots 10923 - 16383
Adding replica 10.0.20.12:6378 to 10.0.20.10:6379
Adding replica 10.0.12.16:6378 to 10.0.20.12:6379
Adding replica 10.0.20.10:6378 to 10.0.12.16:6379
M: 0c64870e7c802bf7814b0daf884785d473a10767 10.0.20.10:6379
   slots:[0-5460] (5461 slots) master
S: 91d4eb697e2e4b26c18b8f69a2c481fe9299df88 10.0.20.10:6378
   replicates d7711c2a1f22b275c595ec6f4a9d9141cb6bbf3b
M: ef31e32281a9e67fa5118eb619cdf469b02d3934 10.0.20.12:6379
   slots:[5461-10922] (5462 slots) master
S: 5dcc21a67e6e2d792d9018c5df10274816d744a3 10.0.20.12:6378
   replicates 0c64870e7c802bf7814b0daf884785d473a10767
M: d7711c2a1f22b275c595ec6f4a9d9141cb6bbf3b 10.0.12.16:6379
   slots:[10923-16383] (5461 slots) master
S: 12bb6bd6e4b63e0ed8cafa224fcc79ca1570426c 10.0.12.16:6378
   replicates ef31e32281a9e67fa5118eb619cdf469b02d3934
Can I set the above configuration? (type 'yes' to accept): yes
>>> Nodes configuration updated
>>> Assign a different config epoch to each node
>>> Sending CLUSTER MEET messages to join the cluster
Waiting for the cluster to join
.
>>> Performing Cluster Check (using node 10.0.20.10:6379)
M: 0c64870e7c802bf7814b0daf884785d473a10767 10.0.20.10:6379
   slots:[0-5460] (5461 slots) master
   1 additional replica(s)
S: 5dcc21a67e6e2d792d9018c5df10274816d744a3 10.0.20.12:6378
   slots: (0 slots) slave
   replicates 0c64870e7c802bf7814b0daf884785d473a10767
S: 12bb6bd6e4b63e0ed8cafa224fcc79ca1570426c 10.0.12.16:6378
   slots: (0 slots) slave
   replicates ef31e32281a9e67fa5118eb619cdf469b02d3934
M: d7711c2a1f22b275c595ec6f4a9d9141cb6bbf3b 10.0.12.16:6379
   slots:[10923-16383] (5461 slots) master
   1 additional replica(s)
M: ef31e32281a9e67fa5118eb619cdf469b02d3934 10.0.20.12:6379
   slots:[5461-10922] (5462 slots) master
   1 additional replica(s)
S: 91d4eb697e2e4b26c18b8f69a2c481fe9299df88 10.0.20.10:6378
   slots: (0 slots) slave
   replicates d7711c2a1f22b275c595ec6f4a9d9141cb6bbf3b
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.

```
