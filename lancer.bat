@echo off
setlocal
cd /d "%~dp0"

set "MAVEN_CMD="
if exist ".tools\apache-maven-3.10.0\bin\mvn.cmd" set "MAVEN_CMD=%CD%\.tools\apache-maven-3.10.0\bin\mvn.cmd"
if not defined MAVEN_CMD (
    for /f "delims=" %%M in ('where mvn 2^>nul') do if not defined MAVEN_CMD set "MAVEN_CMD=%%M"
)
if not defined MAVEN_CMD (
    echo Maven introuvable. Installez Maven et ajoutez son dossier bin au PATH.
    echo Voir GUIDE_INSTALLATION.md.
    exit /b 1
)

call "%MAVEN_CMD%" "-Dmaven.repo.local=%USERPROFILE%\.m2\repository" -DskipTests package
if errorlevel 1 exit /b %ERRORLEVEL%

if not defined CATALINA_HOME if exist ".tools\apache-tomcat-10.1.61\bin\catalina.bat" set "CATALINA_HOME=%CD%\.tools\apache-tomcat-10.1.61"
if not defined CATALINA_HOME (
    echo CATALINA_HOME n'est pas defini. Tomcat 10.1+ est requis pour demarrer le WAR.
    echo Le WAR est disponible dans target\medical-supply-chain-1.0.0.war.
    echo Voir GUIDE_INSTALLATION.md.
    exit /b 1
)
if not exist "%CATALINA_HOME%\bin\catalina.bat" (
    echo Tomcat introuvable dans "%CATALINA_HOME%".
    exit /b 1
)

if not defined JAVA_HOME (
    for /f "delims=" %%J in ('where java 2^>nul') do if not defined JAVA_EXE set "JAVA_EXE=%%J"
)
if not defined JAVA_HOME if defined JAVA_EXE for %%I in ("%JAVA_EXE%") do set "JAVA_HOME=%%~dpI.."
if not defined JAVA_HOME (
    echo JAVA_HOME n'est pas defini et Java est introuvable dans PATH.
    exit /b 1
)

copy /Y "target\medical-supply-chain-1.0.0.war" "%CATALINA_HOME%\webapps\medical-supply-chain.war" >nul
if errorlevel 1 (
    echo Impossible de copier le WAR dans "%CATALINA_HOME%\webapps".
    exit /b 1
)

netstat -ano | findstr /R /C:":8080 .*LISTENING" >nul
if errorlevel 1 (
    call "%CATALINA_HOME%\bin\catalina.bat" start
    if errorlevel 1 exit /b %ERRORLEVEL%
)

echo Attente du deploiement de MediSupply...
for /L %%I in (1,1,20) do (
    curl.exe -fsS --max-time 2 "http://localhost:8080/medical-supply-chain/" >nul 2>nul
    if not errorlevel 1 goto openApp
    timeout /t 2 /nobreak >nul
)
echo Tomcat est lance, mais l'application ne repond pas encore. Consultez les journaux dans "%CATALINA_HOME%\logs".
exit /b 1

:openApp
echo MediSupply est disponible sur http://localhost:8080/medical-supply-chain/
start "" "http://localhost:8080/medical-supply-chain/"
exit /b 0
