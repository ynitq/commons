@echo off
cd /d "%~dp0"

set xsd=xsd\dict.xsd
set packagename=com.cfido.commons.spring.dict.schema
set genPath=src\main\java

echo -----------------

xjc %xsd% -p %packagename% -d %genPath% -encoding UTF-8

echo -----------------
echo done
echo -----------------
echo xsdFile: %xsd%
echo package: %packagename%
echo outPath: %genPath%

@pause