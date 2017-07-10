# 各类支持Spring的工具


## 更新日志：
- 2017-07-10 字典项目增加了MarkDown支持


该项目的目录是为了省去各种配置的麻烦，只需要一行注解，就可以用相关服务


- @EnableLinziSmsService 激活短信服务，配置后，可使用`SendSmsCodeService`，需要以下的依赖
```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-redis</artifactId>
			<scope>provided</scope>
		</dependency>
```

- @EnableJmxInWeb 激活 JmxInWeb 服务，需要以下配置
```
	jmx.port=8081
```

- @EnableJmxInWeb 激活微信接入服务。配置后可使用 `WeChatOAuthClient`，需要以下配置
```
	wechat.appId=
	wechat.appSecret=
```

- @EnableRedis 自动配置 Redis。配置后，可使用RedisTemplate

- @EnableRedisCache 自动配 Redis，同时配置好Cache, 配置后，可使用 @Cacheable @CacheEvict @CachePut等注解


# 字典项目：用于在后台管理前段的文字内容

其实就是将社会化会中前端文字管理模块变成了一个公用的项目，方便所有的系统使用。

使用例子：
```
<div> ${dict["yes"]} </div>
```

字典模块就会用yes作为key去查找对应的值，如果这个值不存在，就自动增加并保存到文件中。

## TODO
- 2016.11.28 BY 梁韦江 ：需要将app-server中关于mqtt server 安全认证的代码提取成为公用模块.