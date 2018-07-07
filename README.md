# Spring Boot Faster
### Spring Boot 整合常用框架组成完整可用的快速开发模板。
更快的项目搭建、更高开发和运行效率、更灵活的应对需求变化、更好的可重构和维护性。  
  
该项目模板来自实际生产项目，适用于新项目快速迭代开发，提供了最基础的功能、工具类、相对完善的全局配置，方便快速上手。  
  
响应式管理界面改自zhengAdmin，基于Bootstrap、JQuery、Layui等，前后端是独立的，可分开部署和开发、自行更换前端框架。  
  
全局唯一的十进制趋势递增ID，使用DECIMAL(31)存储占用14字节，比BIGINT的8字节稍大，好处是可应用到任意表或分表分库。  
  
##### 首次启动前请修改pom.xml和application.yml中的数据库信息并执行faster-data.sql。  

## 集成的功能：
Spring Boot：Spring Boot和Spring基础框架，提供容器、定时任务、异步调用和其他常用功能支持。  
  
Spring Security：Spring安全框架，可满足复杂场景下的安全需求，已实现基于注解的基本权限体系。  
  
Spring MVC：Web访问接口与控制器，Restful，全局异常，自动转换时间，静态资源访问，动态国际化支持等。  
  
Querydsl：通用高效的类型安全的查询框架，使用API覆盖标准SQL且可跨数据库，具有很高的开发和执行效率。  
  
Querydsl 案例：Maven生成插件配置、独立的参考案例（位于test目录），覆盖绝大多数应用场景，可快速上手。  
  
Spring Jdbc：Spring Jdbc事务及异常支持、JdbcTemplate作为特殊情况下的后备支持，确保无后顾之忧。  
  
GlobalIdWorker：全局ID生成器，支持多机多实例运行，趋势递增且尾数均匀，对分表分库非常友好，也可用于唯一命名。  
  
Spring Cache：基于注解的缓存，默认使用EhCache作为本地缓存，resources目录中提供了Redis参考。  
  
Protostuff 序列化：Protostuff 序列化可以大幅提高时间及空间性能，适合传输对象，比如存储到Redis等。  
  
Spring AOP：使用AOP对方法日志进行统一处理，也可用做收集信息、事务处理、权限校验等。  
  
RSA 和 AES：RSA 非对称可逆加密可用于登录加密等， AES 对称可逆加密可用于内部存储数据。  
  
Swagger2：扫描Controller及标记注解，生成接口文档，访问路径：/swagger-ui.html。  
  
Spring Test：Mock测试用例参考，测试驱动开发有助于编写简洁可用和高质量的代码，并加速开发过程。 
   
MySql数据库：默认使用MySql，驱动和配置参考 pom.xml 和 application.yml 中的数据库连接信息。  
  
其他支持：OkHttpUtils、RSA和AES加密、JWT、Spring Boot DevTools、Logback配置。  

### 文件目录与包的划分规范
尽量把同一个功能模块的文件放一起，可减少目录或包的交叉访问以提高开发效率、增加隔离性以降低耦合有利于水平扩展。公共资源可以放一起。

### Spring Security说明：
Spring Security中的Role和Authority是同一个概念，但hasRole默认带前缀，建议使用hasAuthority。  
  
项目中已实现基于注解的功能权限控制（hasAuthority）和资源许可授权控制（hasPermission，若不需要删除Permission相关代码即可）。  

### Querydsl-SQL使用体验：
使用该框架的主要目的在于快速迭代开发（快速出产品，项目有价值才会进一步开发），随着需求的增加需要不断完善和重构，所以项目要能灵活应对各种需求变更。  
  
DSL使用自动生成的表模型映射对象代替SQL中的表对象，具有很好的可维护性，利用编译时检查和集成开发工具的重构功能，再也不用担心修改表模型而代码中不能完全同步修改了。  
  
接近原生SQL的纯JavaAPI构建查询，API动态拼装比原生SQL更灵活，会写SQL即可快速上手，DSL还提供了自动对象封装、类型转换、分页等其他便利的特性，配合开发工具自动完成，开发效率非常高。  
  
我们最近的一个项目的后台管理也用DSL，对复杂报表查询也都没有问题。因为是跨数据库的，所以不支持特定数据库独有的SQL语法（支持数据库函数和HINTS），暂时没有碰到非用数据库独有的SQL不可的场景（万一有则启用JdbcTemplate）。  

## 管理界面预览：
![image](https://github.com/ewingtsai/spring-boot-faster/raw/master/screens/home.gif)  
