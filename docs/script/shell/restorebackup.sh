#!/bin/bash

gunzip < your_backup.sql.gz | mysql -u用户名 -p密码 数据库名