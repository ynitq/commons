:: ------------------------
:: eclipse 中运行mvn的批处理，
:: ------------------------

:: 需要先进入项目所在目录，否则找不到pom文件
@echo off
cd /d "%~dp0"

call mvn deploy -Dmaven.test.skip=true
pause