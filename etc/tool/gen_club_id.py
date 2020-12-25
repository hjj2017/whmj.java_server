#!/usr/bin/env python
# -*- coding:utf-8 -*-
import pymysql
import random
import redis

# 
# 因为用到了 pymysql 和 redis, 所以先要确保已经安装了这两个库!
# 可以使用以下命令进行安装: 
# sudo pip install -i https://mirrors.aliyun.com/pypi/simple pymysql
# sudo pip install -i https://mirrors.aliyun.com/pypi/simple redis
# 

# Redis 关键字
REDIS_KEY = "list#club_id_pump"

#
# 从 MySQL 数据库中获取已经存在的亲友圈 Id 字典
# /////////////////////////////////////////////////////////////////////
# 
def get_club_id_dict_from_mysql():
    # 输出日志信息
    print(">>> 从 MySQL 中读取亲友圈 Id 列表 <<<")

    # 创建连接和游标
    conn = pymysql.connect(host = "127.0.0.1", port = 3306, user = "root", passwd = "root", db = "mj_game", charset = "utf8mb4")
    cursor = conn.cursor()
    
    # 执行 SQL, 并返回收影响行数
    cursor.execute("select club_id from t_club")

    # 拿到所有用户 Id 并创建用户 Id 字典
    all_row = cursor.fetchall()
    club_id_dict = {}

    # 关闭数据库和游标
    cursor.close()
    conn.close()

    for row in all_row:
        club_id_dict[str(row[0])] = True

    # 输出日志信息
    print("共加载 %d 条亲友圈 Id" % len(club_id_dict))

    return club_id_dict


#
# 创建亲友圈 Id 并写入 Redis
# /////////////////////////////////////////////////////////////////////
#
def gen_club_id_and_write_to_redis(club_id_dict):
    # 确保用户 Id 字典不为空
    if club_id_dict is None:
        club_id_dict = {}

    # 输出日志信息
    print(">>> 写入亲友圈 Id 到 Redis <<<")

    # 开启 Redis
    redis_cache = redis.Redis(host = "127.0.0.1", port = 6379, password = "root")
    redis_cache.delete(REDIS_KEY)

    # 获取 Redis PipeLine
    redis_pl = redis_cache.pipeline()

    # 亲友圈 Id 数组
    club_id_array = random.sample(range(100000, 999999), 899999)
    # 写入计数器
    w_counter = 0

    for club_id in club_id_array:
        # 获取关键字
        str_key = str(club_id)
        # 计数器 +1
        w_counter = w_counter + 1
        
        if not club_id_dict.get(str_key, False):
            # 将用户 Id 插入 Redis
            redis_pl.lpush(REDIS_KEY, str_key)

        if w_counter >= 1024:
            w_counter = 0
            redis_pl.execute()

    # 将剩余的部分写入 Redis
    if w_counter > 0:
        redis_pl.execute()

    redis_cache.close()


# 
# 应用主函数函数
# /////////////////////////////////////////////////////////////////////
# 
def app_main():
    # 从 MySQL 中获取已有的用户 Id 字典
    club_id_dict = get_club_id_dict_from_mysql()

    # 
    # 生成用户 Id 并写入 Redis,
    # XXX 注意: 这个过程不会重复写入 MySQL 中已有的用户 Id...
    # 也就是 user_id_dect 中的用户 Id 不会写入 Redis!
    # 以此来避免用户 Id 重复的问题
    gen_club_id_and_write_to_redis(club_id_dict)

    print("+++ 全部完成 +++")


# 执行应用主函数
app_main()

