# Spring Boot QueryDSL Security
### Spring Boot、QueryDSL 和 Security 以及常用框架集成的完整可用的项目模板。

## 集成的功能：
Spring Framework：Spring基础框架，提供Context、Task Schedule等功能支持。  
Spring Security：Spring安全框架，提供了常规功能和诸多高级特性以满足复杂场景下的安全需求。  
Spring MVC：Web访问接口与控制器，默认页面，拦截器，全局异常，Json消息转换，自动转换时间，静态资源访问，动态国际化支持等。  
Thymeleaf：页面视图的模板引擎，很适合作为Web应用的视图的业务逻辑层。  
QueryDSL：通用高效的查询框架，专注于通过Java API构建类型安全的SQL查询，支持SQL标准且可跨数据库。  
Spring Cache + EhCache：使用声明式缓存，对方法返回值进行缓存，使用EhCache作为本地缓存，后续扩展Redis做共享缓存。  
Protostuff 序列化：Protostuff 序列化可以大幅提高时间及空间性能，适合传输对象，比如存储到Redis等。  
Spring AOP：使用AOP对方法日志进行统一处理，也可用做收集信息、事务处理、权限校验等。  
RSA 和 AES：RSA 非对称可逆加密可用于登陆加密等， AES 对称可逆加密可用于内部存储数据。  
MySql数据库：默认使用MySql，驱动和配置参考 pom.xml 和 application.yml 中的数据库连接信息。  
Swagger2：扫描Controller及标记注解，生成接口文档，访问路径：/swagger-ui.html。  
JWT：Json Web Token，使用JWT可以把认证信息存储到用户端，并对用户提交的认证信息进行验证。  
OkHttp3：OkHttp3比其他Http类具有更好的性能和更方便的API，项目中的OkHttpUtils类是对OkHttp3的简单封装。  
Logback：使用Logback记录日志，控制台日志可点击定位，方便调试。  
Spring Boot DevTools：动态加载类等，实时生效，方便编辑调试。  
Spring Test：使用MockMvc进行测试用例编写。