## 更新说明

+ 2016.06.06
Version: HB4-1.0.23-SNAPSHOT
操作:梁韦江
已spring boot为基础pom，统一依赖库的版本
修改了依赖的scope，将绝大数的变成非complite类型，这样其他项目在用的时候，不会需要下载如此多的jar包

+ 2015-10-27
Version: 1.0.5.RELEASE  
操作:梁韦江
用JmxInWeb替换了原mx4j
更新了pom.xml文件，加了发布的设置，用以下命令可自动发版
增加了一堆的test case需要的依赖包

```sh
mvn deploy
```  

更新了eclipse的项目文件，不在依赖于javalib目录，全部依赖包交由maven管理

---

+ 2015-10-21
Version: 1.0.3.RELEASE  
操作:廖望舒  
将原有commons-lang包替换为commons-lang3包，因前者已不再维护  
(另外commons-httpClient包也已不再维护，由于覆盖面大，故暂时不做替换)  
将项目转为maven或者gradle进行管理

---

+ 2015-09-01
Version: 1.0.2.RELEASE  
操作:梁韦江  
增加了JaxbUtil，用于操作基于jaxb的xml类  
增加了JaxbUtilTest，作为xml操作的例子  
增加了ant的buildXsdTest.xml脚本，作为用jaxb通过xsd文件生成java代码的例子  

---

+ 2015-5-15  
Version: 1.0.1.RELEASE  
操作:梁韦江  
增加了很多注释，同时在ant中增加了javadoc  

---

