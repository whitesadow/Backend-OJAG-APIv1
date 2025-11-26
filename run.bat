@echo off
REM Script para ejecutar el servidor backend OJAG API

setlocal
set BIN_DIR=bin
set LIB_DIR=lib

if not exist %BIN_DIR% (
    echo.
    echo ❌ Directorio %BIN_DIR% no existe. Ejecuta compile.bat primero.
    echo.
    pause
    exit /b 1
)

echo.
echo Iniciando servidor OJAG API...
echo.

java -cp "%BIN_DIR%;lib/json-20250517.jar;lib/gson-2.13.2.jar;lib/mysql-connector-j-9.5.0.jar" Main

pause
