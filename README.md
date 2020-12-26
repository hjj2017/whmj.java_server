# whmj.java_server

威海地方玩法麻将，Java 服务端代码，JDK 语言版本 OpenJDK 13+33。
想要在 IntelliJ IDEA 中成功运行代码，只需要依次启动两个服务器：

- proxyserver
- bizserver

在启动这两个服务器之前，当然还需要做一些准备工作。

## 初始化 MySQL 数据库
需要建立 mj_game、mj_log、mj_log_template 这三个数据库。

```sql
create database mj_game         default character set utf8mb4;
create database mj_log          default character set utf8mb4;
create database mj_log_template default character set utf8mb4;
```

建立数据库完成之后，需要导入相应的数据库文件：

```sql
use mj_game
source etc/sql/mj_game.sql;                -- 创建 t_user、t_club、t_club_member 等数据表
source etc/sql/mj_cost_room_card_conf.sql; -- 导入房卡消耗数量配置

use mj_log_template
source etc/sql/mj_log_temlate.sql;         -- 导入日志记录模板表
```

## 初始化 Redis
MySQL 数据库初始化完成之后，还需要初始化 Redis，主要是建立用户 Id 池和老友圈 Id 池。
这需要你的机器有 Python3 环境！
并且已经为 Python3 安装了 pymysql 和 redis 扩展。

如果你已经安装好 Python3 及其扩展，那么运行以下命令即可：

```shell
python3 etc/tool/gen_user_id.py
python3 etc/tool/gen_club_id.py
```

**注意，在运行前需要确保 .py 文件中配置的数据库地址、用户名和密码是否正确。**

## 启动 proxyserver
启动 proxyserver 时，需要在 IDEA 中添加以下参数：

```
--server_id=1001
--server_name=proxy_server_1001
-h 0.0.0.0
-p 20480
-c ../etc/proxyserver_all.conf.json
```

## 启动 bizserver
启动 proxyserver 时，需要在 IDEA 中添加以下参数：

```
--server_id=2001
--server_name=biz_server_2001
--server_job_type_set=PASSPORT,HALL,GAME,CLUB,CHAT,RECORD
-h 127.0.0.1
-p 40960
-c ../etc/bizserver_all.conf.json
```
