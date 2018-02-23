# 所有的共用项目

这个项目不是父项目-子模块的结构，各个子项目都是独立的项目，无继承关系

## 更新信息
- 2016.07.02 
将 spring-boot-parent 的内容直接放到父pom文件中

## 合并的
- 有些工具包在开发过程中经常更新，一个个项目更新起来太麻烦了。
- 合并后所有项目可以一起build，一起deploy。简单方便啊
- 我们主要合并的是经常会变化的，linziMQ之类不是经常变化的可以先不拷贝过来

- [api-server](https://github.com/ynitq/commons/tree/dev/api-server) : api server 模板
- [annotation](https://github.com/ynitq/commons/tree/dev/annotation) : 将各个工具包用到的annotation都集中放这里了，可用于安卓
- [utils](https://github.com/ynitq/commons/tree/dev/utils): 各类常用工具
- [spring-support](https://github.com/ynitq/commons/tree/dev/spring-support) : 自动化配置，一行注解就可以使用相关服务，我们将各类共用类都放到这个项目了
- [beans](https://github.com/ynitq/commons/tree/dev/beans): 通用的beans,可给安卓使用的代码
- [code-gen](https://github.com/ynitq/commons/tree/dev/code-gen): 代码生成工具，用于从数据库生成orm文件，无外键的
