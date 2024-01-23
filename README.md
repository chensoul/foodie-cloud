# foodie-cloud

food-social-contact-parent 项目的重构版本。

## 开发环境

软件版本：

| 组件          | 用途  	  |              版本号              | 
|:------------|:------:|:-----------------------------:| 
| Java        | 编译运行项目 | 1.8以上（推荐8u161以后的版本，否则要装JCE插件） |
| Maven       |  依赖管理  |            3.0.4以上            |
| MySQL       |  数据库   |              8.x              | 
| Redis     	 | 缓存组件 	 |              7.x              | 
| RabbitMQ    | 消息中间件  |            3.7.15             | 
| Kafka       | 消息中间件  |             2.2.0             
| Lua         |  限流脚本  |             5.3.5             | 

### 技术选型

Spring Cloud每个业务领域都有多个可供选择的组件，这里也列出了微服务章节中将要用到的组件+中间件的技术选型，这也是当前主流的选型。

| 内容          |            技术选型  	            | 
|:------------|:-----------------------------:| 
| 服务网关     	  |            Gateway            |
| 服务治理  	     |           Eureka 	            |
| 负载均衡     	  |           Ribbon 	            |
| 服务间调用     	 |            Feign 	            |
| 服务容错     	  | Hystrix + Turbine + Dashboard |
| 消息总线     	  |        Bus + RabbitMQ	        |
| 调用链追踪     	 |     Sleuth + Zipkin + ELK     |
| 消息驱动     	  |      Stream + RabbitMQ	       |
| 流控     	    |          Sentinel 	           |

### 依赖版本

| 依赖                                                                         | 本项目版本         | 新版                                                                                                                                                                                                                                     | 说明                 |
|----------------------------------------------------------------------------|---------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------|
| [spring-boot](https://github.com/spring-projects/spring-boot)              | 2.7.18        | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&versionPrefix=2.&metadataUrl=https://s01.oss.sonatype.org/content/repositories/releases/org/springframework/boot/spring-boot-dependencies/maven-metadata.xml">     | 限制 Spring Boot 2.x |
| [spring-cloud](https://github.com/spring-cloud)                            | 2021.0.9      | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&versionPrefix=2021&metadataUrl=https://s01.oss.sonatype.org/content/repositories/releases/org/springframework/cloud/spring-cloud-dependencies/maven-metadata.xml"> | 限制 Spring Boot 2.x |
| [spring-boot-admin](https://github.com/codecentric/spring-boot-admin)      | 2.7.15        | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&versionPrefix=2.&metadataUrl=https://oss.sonatype.org/content/repositories/releases/de/codecentric/spring-boot-admin-dependencies/maven-metadata.xml">             | 限制 Spring Boot 2.x |
| [spring-cloud-security](https://spring.io/projects/spring-cloud-security/) | 2.2.5.RELEASE | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&versionPrefix=1.&metadataUrl=https://s01.oss.sonatype.org/content/repositories/releases/org/springframework/cloud/spring-cloud-security/maven-metadata.xml">       |                    |
| [mybatis-plus](https://github.com/baomidou/mybatis-plus)                   | 3.5.5         | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&metadataUrl=https://oss.sonatype.org/content/repositories/releases/com/baomidou/mybatis-plus-boot-starter/maven-metadata.xml">                                     |                    |
| [springdoc](https://github.com/springdoc)                                  | 1.7.0         | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&metadataUrl=https://oss.sonatype.org/content/repositories/releases/org/springdoc/springdoc-openapi-ui/maven-metadata.xml">                                         |                    |

## 默认端口

| 内容                         | 端口  	  | 
|:---------------------------|:------:| 
| mysql（单机模式）     	          | 3306 	 |
| redis（单机模式）     	          | 6379 	 |
| rabbitmq（单机模式）     	       | 5672 	 |
| Gateway     	              |   80   |
| Eureka  	                  | 8080 	 |
| foodie-diners     	        |  8081  |
| foodie-oauth2-server     	 |  8082  |
| foodie-seckill     	       |  8083  |
| foodie-follow     	        |  8084  |
| foodie-feeds     	         |  8085  |
| foodie-points     	        |  8086  |
| foodie-restaruants     	   |  8087  |



