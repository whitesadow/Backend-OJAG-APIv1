#!/bin/bash
# Script para ejecutar el servidor backend OJAG API

BIN_DIR="bin"
LIB_DIR="lib"

if [ ! -d "$BIN_DIR" ]; then
    echo ""
    echo "❌ Directorio $BIN_DIR no existe. Ejecuta compile.sh primero."
    echo ""
    exit 1
fi

echo ""
echo "Iniciando servidor OJAG API..."
echo ""

java -cp "$BIN_DIR:lib/json-20250517.jar:lib/gson-2.13.2.jar:lib/mysql-connector-j-9.5.0.jar" Main
