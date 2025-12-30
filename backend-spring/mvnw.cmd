@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------

@echo off
setlocal

set MAVEN_CMD_LINE_ARGS=%*

@REM Find the project base dir
set MAVEN_PROJECTBASEDIR=%~dp0
IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties" goto error

@REM Download maven-wrapper.jar if needed
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

if exist %WRAPPER_JAR% goto runMaven
echo Downloading Maven Wrapper...
powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri %WRAPPER_URL% -OutFile %WRAPPER_JAR%}"

:runMaven
set MAVEN_OPTS=-Xmx512m

@REM Check for Maven installation
where mvn >nul 2>&1
if %ERRORLEVEL% == 0 (
    mvn %MAVEN_CMD_LINE_ARGS%
) else (
    echo Maven is not installed. Please install Maven or use the wrapper.
    echo Download from: https://maven.apache.org/download.cgi
    exit /b 1
)

goto end

:error
echo The maven wrapper was not found in this project.
exit /b 1

:end
endlocal
