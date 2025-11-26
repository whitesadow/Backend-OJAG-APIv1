# 🔧 Backend OJAG API - Guía de Inicio

## 📋 Requisitos Previos

1. **Java JDK 8+** instalado
2. **MySQL** corriendo en `localhost:3306`
3. **Base de datos** `ojag_ecommerce_db` creada
4. **Librerías externas** en carpeta `lib/`

## 📦 Descargar Dependencias

Crea una carpeta `lib/` en la raíz del proyecto y descarga estas librerías:

### 1. MySQL Connector
- Descarga: [mysql-connector-java-8.0.33.jar](https://dev.mysql.com/downloads/connector/j/)
- Destino: `lib/mysql-connector-java-8.0.33.jar`

### 2. JSON Library
- Descarga: [json-20230227.jar](https://search.maven.org/artifact/org.json/json/20230227/jar)
- Destino: `lib/json-20230227.jar`

### 3. Google Gson
- Descarga: [gson-2.8.9.jar](https://search.maven.org/artifact/com.google.code.gson/gson/2.8.9/jar)
- Destino: `lib/gson-2.8.9.jar`

**Estructura final esperada:**
```
backend-ojag-apiv2/
├── lib/
│   ├── mysql-connector-java-8.0.33.jar
│   ├── json-20230227.jar
│   └── gson-2.8.9.jar
├── src/
├── compile.bat
├── compile.sh
└── run.bat / run.sh
```

## 🔨 Compilar el Proyecto

### En Windows (PowerShell):
```powershell
.\compile.bat
```

### En Linux/Mac:
```bash
chmod +x compile.sh
./compile.sh
```

## ▶️ Ejecutar el Servidor

### En Windows:
```powershell
.\run.bat
```

### En Linux/Mac:
```bash
chmod +x run.sh
./run.sh
```

**Salida esperada:**
```
╔═══════════════════════════════════════════════════════╗
║     🚀 API Ecommerce iniciada correctamente          ║
║     Puerto: 8095                                      ║
║     URL: http://localhost:8095                       ║
╚═══════════════════════════════════════════════════════╝
```

## 🧪 Probar la API

Una vez ejecutado el servidor, prueba en el navegador o con `curl`:

```bash
# Health check
curl http://localhost:8095/

# Listar productos
curl http://localhost:8095/productos

# Listar órdenes
curl http://localhost:8095/ordenes
```

## 🗄️ Configuración de Base de Datos

Si necesitas cambiar los datos de conexión, edita `src/config/DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/ojag_ecommerce_db";
private static final String USER = "root";
private static final String PASSWORD = "";
```

## 📚 Rutas Disponibles

- `GET /` - Health check
- `GET /productos` - Listar productos
- `POST /usuario/registro` - Registrar usuario
- `POST /usuario/login` - Login
- `GET /carrito` - Ver carrito
- `POST /carrito/agregar` - Agregar al carrito
- `DELETE /carrito/eliminar` - Eliminar del carrito
- `POST /checkout` - Procesar compra
- `GET /ordenes` - Listar órdenes
- `GET /orden/{id}` - Ver orden específica

## 🆘 Solución de Problemas

**Error: "Cannot find symbol"**
- Verifica que todas las librerías estén en `lib/`
- Ejecuta `compile.bat` (o `compile.sh`) nuevamente

**Error: "Connection refused"**
- Asegúrate de que MySQL esté corriendo
- Verifica que la base de datos `ojag_ecommerce_db` exista

**Error 404 al acceder a rutas**
- Espera a que aparezca el mensaje "🚀 API Ecommerce iniciada correctamente"
- Usa exactamente: `http://localhost:8095/productos` (con slash final si aplica)

## 📝 Estructura del Proyecto

```
src/
├── Main.java              # Servidor HTTP principal
├── config/
│   └── DatabaseConnection.java
├── handler/               # Manejadores de rutas
│   ├── ProductoHandler.java
│   ├── UsuarioHandler.java
│   ├── CarritoHandler.java
│   ├── CheckoutHandler.java
│   └── OrdenesHandler.java
├── dao/                   # Acceso a datos
│   ├── ProductoDAO.java
│   ├── UsuarioDAO.java
│   ├── CarritoDAO.java
│   └── OrdenDAO.java
├── model/                 # Modelos de datos
│   ├── Producto.java
│   ├── Usuario.java
│   ├── Orden.java
│   ├── DetalleOrden.java
│   └── CarritoItem.java
└── utils/                 # Utilidades
    └── JsonUtil.java
```
