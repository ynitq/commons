# API SERVER 的框架项目

包结构

- adapter : 各类的接入适配器，例如Ajax适配器， WebSocket适配器
- bean : 项目用到的各类bean和exception
- utils : 顾名思义，就是工具了

## TODO
- 2016.11.28 BY 梁韦江 ：现在接口实现类的容器是一个单子实例，所以如果项目有多套api，会全部混在一起。但是本身做单子实例也是有道理的，就是为DebugController提供数据，所以要分开也麻烦。