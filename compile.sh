#!/bin/bash
# Script de compilación para el backend OJAG API

SRC_DIR="src"
BIN_DIR="bin"
LIB_DIR="lib"

# Crear directorio de salida si no existe
mkdir -p "$BIN_DIR"

# Compilar todos los archivos Java
echo ""
echo "Compilando archivos Java..."
echo ""

javac -cp ".:lib/json-20250517.jar:lib/gson-2.13.2.jar:lib/mysql-connector-j-9.5.0.jar" -d "$BIN_DIR" \
  "$SRC_DIR"/Main.java \
  "$SRC_DIR"/config/DatabaseConnection.java \
  "$SRC_DIR"/model/*.java \
  "$SRC_DIR"/dao/*.java \
  "$SRC_DIR"/util/*.java \
  "$SRC_DIR"/handler/*.java

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Compilacion exitosa. Los archivos .class se encuentran en: $BIN_DIR"
    echo ""
    echo "Para ejecutar el servidor, usa:"
    echo "  java -cp \"$BIN_DIR:$LIB_DIR/*\" Main"
    echo ""
else
    echo ""
    echo "❌ ERROR: Compilacion fallida. Verifica los errores arriba."
    echo ""
fi
