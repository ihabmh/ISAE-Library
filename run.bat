@echo off
set "OUT_DIR=out\production\ISAE Library"

if exist "%OUT_DIR%" rd /s /q "%OUT_DIR%"
mkdir "%OUT_DIR%"

echo Compiling...
javac -d "%OUT_DIR%" src\models\*.java src\factory\*.java src\filters\*.java src\observers\*.java src\statistics\*.java src\exporters\*.java src\persistence\*.java src\manager\*.java src\ui\*.java src\Main.java
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

xcopy /s /e /y "src\resources\*" "%OUT_DIR%\resources\" > nul

echo Application started.

java -cp "%OUT_DIR%" Main
if %ERRORLEVEL% NEQ 0 (
    echo Application exited with error code %ERRORLEVEL%
)
pause