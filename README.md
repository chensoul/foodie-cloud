# foodie-cloud

food-social-contact-parent 项目的重构版本。

## 快速开始

### 环境说明

| 组件     |     用途     |                     版本号                      |
| :------- | :----------: | :---------------------------------------------: |
| Java     | 编译运行项目 | 1.8以上（推荐8u161以后的版本，否则要装JCE插件） |
| Maven    |   依赖管理   |                    3.8.0以上                    |
| MySQL    |    数据库    |                       8.x                       |
| Redis    |   缓存组件   |                       7.x                       |
| RabbitMQ |  消息中间件  |                     3.7.15                      |
| Kafka    |  消息中间件  |                      2.2.0                      |
| Lua      |   限流脚本   |                      5.3.5                      |

### 技术选型

Spring Cloud每个业务领域都有多个可供选择的组件，这里也列出了微服务章节中将要用到的组件+中间件的技术选型，这也是当前主流的选型。

| 内容       |           技术选型            |
| :--------- | :---------------------------: |
| 服务网关   |            Gateway            |
| 服务治理   |            Eureka             |
| 负载均衡   |            Ribbon             |
| 服务调用   |             Feign             |
| 服务容错   | Hystrix + Turbine + Dashboard |
| 消息总线   |        Bus + RabbitMQ         |
| 调用链追踪 |     Sleuth + Zipkin + ELK     |
| 消息驱动   |       Stream + RabbitMQ       |
| 流控       |           Sentinel            |

### 依赖版本

| 依赖                                                                         | 本项目版本         | 新版                                                                                                                                                                                                                                 | 说明      |
|----------------------------------------------------------------------------|---------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|
| [spring-boot](https://github.com/spring-projects/spring-boot)              | 2.7.18        | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&versionPrefix=2.&metadataUrl=https://oss.sonatype.org/content/repositories/releases/org/springframework/boot/spring-boot-dependencies/maven-metadata.xml">     | 限制 JDK8 |
| [spring-cloud](https://github.com/spring-cloud)                            | 2021.0.9      | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&versionPrefix=2021&metadataUrl=https://oss.sonatype.org/content/repositories/releases/org/springframework/cloud/spring-cloud-dependencies/maven-metadata.xml"> | 限制 JDK8 |
| [spring-boot-admin](https://github.com/codecentric/spring-boot-admin)      | 2.7.15        | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&versionPrefix=2.&metadataUrl=https://oss.sonatype.org/content/repositories/releases/de/codecentric/spring-boot-admin-dependencies/maven-metadata.xml">         | 限制 JDK8 |
| [spring-cloud-security](https://spring.io/projects/spring-cloud-security/) | 2.2.5.RELEASE | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&metadataUrl=https://repo1.maven.org/maven2/org/springframework/cloud/spring-cloud-starter-security/maven-metadata.xml">                                        |         |
| [mybatis-plus](https://github.com/baomidou/mybatis-plus)                   | 3.5.5         | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&metadataUrl=https://oss.sonatype.org/content/repositories/releases/com/baomidou/mybatis-plus-boot-starter/maven-metadata.xml">                                 |         |
| [springdoc](https://github.com/springdoc)                                  | 1.7.0         | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&metadataUrl=https://oss.sonatype.org/content/repositories/releases/org/springdoc/springdoc-openapi-ui/maven-metadata.xml">                                     | 限制 JDK8 |

### 默认端口

| 内容                      | 端口  	  | 
|:------------------------|:------:| 
| mysql     	             | 3306 	 |
| redis   	               | 6379 	 |
| rabbitmq    	           | 5672 	 |
| eureka  	               | 8761 	 |
| gateway     	           |  8443  |
| config-file     	       |  8888  |
| auth-service     	      |  6666  |
| foodie-diner     	      |  8081  |
| foodie-point     	      |  8082  |
| foodie-order     	      |  8083  |
| foodie-follow     	     |  8084  |
| foodie-feed     	       |  8085  |
| foodie-restaurant     	 |  8086  |

### 环境搭建

- Git: https://git-scm.com/downloads
- OrbStack: https://orbstack.dev/
- Java: https://adoptium.net/installation
- curl: https://curl.haxx.se/download.html
- jq: https://stedolan.github.io/jq/download/
- Spring Boot CLI: https://docs.spring.io/spring-boot/docs/3.0.4/reference/html/getting-started.html#getting-started.installing.cli
- Siege: https://github.com/JoeDog/siege#where-is-it
- Helm: https://helm.sh/docs/intro/install/
- kubectl: https://kubernetes.io/docs/tasks/tools/install-kubectl-macos/
- Minikube: https://minikube.sigs.k8s.io/docs/start/
- Istioctl: https://istio.io/latest/docs/setup/getting-started/#download


安装软件：
```bash  
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

brew tap spring-io/tap && \
brew tap homebrew/cask-versions && \
brew install --cask temurin17 && \
brew install jq && \
brew install spring-boot && \
brew install helm && \
brew install siege && \
brew install orbstack

echo 'export JAVA_HOME=$(/usr/libexec/java_home -v8)' >> ~/.bash_profile
source ~/.bash_profile
```

验证版本：

```bash
git version && \
docker version -f json | jq -r .Client.Version && \
java -version 2>&1 | grep "openjdk version" && \
curl --version | grep "curl" | sed 's/(.*//' && \
jq --version && \
spring --version && \
siege --version 2>&1 | grep SIEGE && \
helm version --short && \
kubectl version --client -o json | jq -r .clientVersion.gitVersion && \
minikube version | grep "minikube" && \
istioctl version --remote=false
```

#### Docker 运行

```bash
mvn clean package -DskipTests=true && docker-compose build && docker-compose up -d

docker ps --format {{.Names}}
```



## 参考资料

- [Microservices with Spring Boot 3 and Spring Cloud](https://github.com/PacktPublishing/Microservices-with-Spring-Boot-and-Spring-Cloud-Third-Edition)