# 所有的共用项目

这个项目不是父项目-子模块的结构，各个子项目都是独立的项目，无继承关系

## 更新信息
- 2016.07.02 
将 spring-boot-parent 的内容直接放到父pom文件中

## 合并的
- 有些工具包在开发过程中经常更新，一个个项目更新起来太麻烦了。
- 合并后所有项目可以一起build，一起deploy。简单方便啊
- 我们主要合并的是经常会变化的，linziMQ之类不是经常变化的可以先不拷贝过来

- [api-server](https://git.lin-zi.com/commons/linzi-commons/tree/dev/api-server) : api server 模板，我们将api server所需要的各类共用类都放到这个项目了
- [annotation](https://git.lin-zi.com/commons/linzi-commons/tree/dev/annotation) : 将各个工具包用到的annotation都集中放这里了，可用于安卓
- [utils](https://git.lin-zi.com/commons/linzi-commons/tree/dev/utils): 各类常用工具
- [linzi-spring-support](https://git.lin-zi.com/commons/linzi-commons/tree/dev/spring-support) : 自动化配置，一行注解就可以使用相关服务
- [linzi-beans](https://git.lin-zi.com/commons/linzi-commons/tree/dev/beans): 通用的beans,可给安卓使用的代码
- [sms-gw-253](https://git.lin-zi.com/commons/linzi-commons/tree/dev/sms-gw-253): 253.com短信网关接入 
- [code-gen](https://git.lin-zi.com/commons/linzi-commons/tree/dev/code-gen): 代码生成工具
- [dict](https://git.lin-zi.com/commons/linzi-commons/tree/dev/dict) : 字典项目，用于管理前端页面的文字

## 已经作废的
- [spring-boot-parent](https://git.lin-zi.com/commons/linzi-commons/tree/dev/spring-boot-parent) : maven项目pom文件基础模板，各个项目继承这个文件就好了。
