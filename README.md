# whmj.java_server

威海地方玩法麻将，Java 服务器端。
如果是在 IntelliJ IDEA 中运行服务器代码，需要分别启动两个服务器：
- proxyserver
- bizserver

# 初始化数据库
需要建立 mj_game、mj_log、mj_log_template 这三个数据库。
```
create database mj_game default character set utf8mb4;
create database mj_log default character set utf8mb4;
create database mj_log_template default character set utf8mb4;
```
建立数据库完成之后，需要导入相应的数据库文件：
```
use mj_game
source etc/sql/mj_game.sql;                -- 创建 t_user、t_club、t_club_member 等数据表
source etc/sql/mj_cost_room_card_conf.sql; -- 导入房卡消耗数量配置

use mj_log_template
source etc/sql/mj_log_temlate.sql;         -- 导入日志记录模板表
```

# 启动 proxyserver
启动 proxyserver 时，需要在 IDEA 中添加以下参数：
```
--server_id=1001
--server_name=proxy_server_1001
-h 0.0.0.0
-p 20480
-c ../etc/proxyserver_all.conf.json
```

# 启动 bizserver
启动 proxyserver 时，需要在 IDEA 中添加以下参数：
```
--server_id=2001
--server_name=biz_server_2001
--server_job_type_set=PASSPORT,HALL,GAME,CLUB,CHAT,RECORD
-h 127.0.0.1
-p 40960
-c ../etc/bizserver_all.conf.json
```
