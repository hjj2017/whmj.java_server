#!/bin/bash

# 运行主类
java_main_clazz=org.mj.proxyserver.ProxyServer

# /////////////////////////////////////////////

paramz=""
paramz="${paramz} --server_id=1001"
paramz="${paramz} --server_name=proxy_server_1001"
paramz="${paramz} -h 服务器局域网_IP"
paramz="${paramz} -p 20480"
paramz="${paramz} -c ./etc/proxyserver_all.conf.json"

java_cmd="java -server -cp .:./lib/* -Xmx512m ${java_main_clazz} ${paramz}"
nohup $java_cmd > /dev/null &
