@echo off
cd /d "%~dp0"

set xsd=src\test\resources\xsdTest\linZiShh.xsd
set packagename=com.linzi.test.xsd
set genPath=target\xsd

echo 这是个通过xsd生成代码的例子:
echo -----------------

md %genPath%
xjc %xsd% -p %packagename% -d %genPath%

echo -----------------
echo 生成完成
echo -----------------
echo xsd文件:%xsd%
echo 生成java文件的包名: %packagename%
echo 输出目录 %genPath%
