@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup batch script, version 3.3.4
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
if "%MAVEN_PROJECTBASEDIR%"=="" set MAVEN_PROJECTBASEDIR=.
set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

if not exist %WRAPPER_JAR% (
  echo [ERROR] Missing Maven Wrapper JAR: %WRAPPER_JAR%
  echo         I will attempt to download it now...
)

set JAVA_EXE=java
if not "%JAVA_HOME%"=="" set JAVA_EXE="%JAVA_HOME%\bin\java.exe"

%JAVA_EXE% -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" -classpath %WRAPPER_JAR% %WRAPPER_LAUNCHER% %*
if ERRORLEVEL 1 goto error
goto end

:error
endlocal
exit /b 1

:end
endlocal