README
====
 
#### 服务器消息通信规则
 
- BizServer 虽然知道所有的客户端消息, 并不直接面向客户端 
    ( 这样做的目的是出于安全性和动态扩容方面的考虑 );
- 面向客户端的是 ProxyServer;
- ProxyServer 与 BizServer 之间使用 InternalServerMsg 通信;
- BizServer 之间不能也无需互相通信;
