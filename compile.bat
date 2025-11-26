@echo off
REM Script de compilación para el backend OJAG API

setlocal enabledelayedexpansion

set SRC_DIR=src
set BIN_DIR=bin
set LIB_DIR=lib

REM Crear directorio de salida si no existe
if not exist %BIN_DIR% mkdir %BIN_DIR%

REM Compilar todos los archivos Java
echo.
echo Compilando archivos Java...
echo.

javac -cp ".;lib/json-20250517.jar;lib/gson-2.13.2.jar;lib/mysql-connector-j-9.5.0.jar" -d %BIN_DIR% ^
  %SRC_DIR%\Main.java ^
  %SRC_DIR%\config\DatabaseConnection.java ^
  %SRC_DIR%\model\*.java ^
  %SRC_DIR%\dao\*.java ^
  %SRC_DIR%\util\*.java ^
  %SRC_DIR%\handler\*.java 2>&1

if !ERRORLEVEL! EQU 0 (
    echo.
    echo ✓ Compilacion exitosa. Los archivos .class se encuentran en: %BIN_DIR%
    echo.
    echo Para ejecutar el servidor, usa:
    echo   java -cp "%BIN_DIR%;%LIB_DIR%/*" Main
    echo.
) else (
    echo.
    echo ❌ ERROR: Compilacion fallida. Verifica los errores arriba.
    echo.
    pause
)
