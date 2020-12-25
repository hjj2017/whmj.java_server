#!/bin/bash

# 运行主类
java_main_clazz=org.mj.bizserver.BizServer

# /////////////////////////////////////////////

paramz=""
paramz="${paramz} --server_id=2001"
paramz="${paramz} --server_name=biz_server_2001"
paramz="${paramz} --server_job_type_set=PASSPORT,HALL,GAME,CLUB,CHAT,RECORD"
paramz="${paramz} -h 服务器局域网_IP"
paramz="${paramz} -p 40960"
paramz="${paramz} -c ./etc/bizserver_all.conf.json"

java_cmd="java -server -cp .:./lib/* -Xmx512m ${java_main_clazz} ${paramz}"
nohup $java_cmd > /dev/null &
