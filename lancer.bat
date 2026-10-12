@echo off
setlocal
cd /d "%~dp0"

if not exist target\classes mkdir target\classes
if errorlevel 1 exit /b 1

javac -encoding UTF-8 -d target\classes src\main\java\com\medical\supplychain\*.java
if errorlevel 1 exit /b %ERRORLEVEL%

java -cp target\classes com.medical.supplychain.Main %*
exit /b %ERRORLEVEL%
