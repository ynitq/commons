# 代码生成工具

本项目是准备替换原来的代码生成工具。

## 第一步，准备做专门的table 到 entity的生成工具
要解决的问题如下：
- 我们现在开始大量使用cache，所以对象不能带外键否则没法“序列化-反序列化”，该工具必须能支持不生成外键
- eclipse自带的jpa工具可以生成不带外键的对象，entity可以带接口，但所有数据库中int类型的字段都被生成为int型，而不是Integer型，问题多多。
- hibernate tools的功能太强大，模板反而很难写，而且不支持不生成外键。


### 功能要点
- entity对象可以加接口
- 代码符合jpa 2.1规范
- 可以使用web界面控制


# 使用说明
本生成工具只用于Spring boot项目，正常情况下，在项目文件中已经有了数据库配置，所以我们直接用原来的数据库配置就可以了。

为了不影响原来的代码，所以生成工具的运行程序是放在test目录下。

使用方法很简单

- 修改pom.xml文件，加入依赖

```
		<dependency>
			<!-- 代码生成工具 -->
			<groupId>com.linzi.commons</groupId>
			<artifactId>code-gen</artifactId>
			<scope>test</scope>
		</dependency>

```
注意，```scope = test```


- 配置一下```codeGen.properties``` 

```
codeGen.basePackage=com.linzi.app.appserver

codeGen.entity.auto=true
codeGen.entity.entityPackage=entity.server

codeGen.entity.implement[0]=java.io.Serializable
codeGen.entity.implement[1]=com.linzi.app.appserver.entity.server.IBasePo

codeGen.entity.tableNameMap.newses = News

```

主要是要配置 basePackage 以及implement，其实大多数情况下，我们都有java.io.Serializable这个接口

- 在test目录下，随便建一个java程序，包名最后是随意，不要和项目本身的包名一致，例如：

```
package codeGen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.linzi.commons.codeGen.anno.CodeGenEntity;
import com.linzi.commons.spring.anno.EnableJmxInWeb;

@SpringBootApplication
@EnableJmxInWeb
@CodeGenEntity
public class StartCodeGen {

	public static void main(String[] args) {
		SpringApplication.run(StartCodeGen.class, args);
	}

}

``` 

然后运行这个程序就行了。如果需要调整代码，例如修改数据库，然后再重新生成什么的，可通过jmx操作

# 自定义模板
我们从数据库中读取了表信息后，主要是构建了TableBean和ColumnBean两个bean，模板中可以用于这些bean的属性

### TableBean
- name:String 表名
- comment:String 表的备注
- javaClassName:String 对应的java类名
- implementsStr:String 接口字符串，如果有指定的接口，内容就是 implements xxxx 
- embeddedId:boolean 是否是复合主键，目前我们其实不打算用复合主键
- id:SimpleIdBean 主键
- columns:List<ColumnBean> 列

### ColumnBean
- name:String 数据库字段名
- hasComment:boolean 是否有备注
- comment:String 数据库中的字段备注
- nullable:boolean 是否可以为空
- javaClassName:String 字段对应的java类型
- nullableStr:String 如果可以为空时，有字符串nullable=true字样
- lengthStr:String 如果类型是字符串，并且有长度，就是 length=xxx字样
- propName:String java中的属性名，例如 name
- propNameU:String 将属性名的第一个字母变成大写，例如 Name
- getter:String getter名字，如果是boolean型的，就是 is开头，其他的都是get开头
 