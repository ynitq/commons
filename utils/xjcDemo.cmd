@echo off
cd /d "%~dp0"

set xsd=src\test\resources\xsdTest\linZiShh.xsd
set packagename=com.linzi.test.xsd
set genPath=target\xsd

echo ���Ǹ�ͨ��xsd���ɴ��������:
echo -----------------

md %genPath%
xjc %xsd% -p %packagename% -d %genPath%

echo -----------------
echo �������
echo -----------------
echo xsd�ļ�:%xsd%
echo ����java�ļ��İ���: %packagename%
echo ���Ŀ¼ %genPath%
