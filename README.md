# 🎟️ Tu Tiket

> Sistema local para la **gestión y venta de boletos para eventos**.
> Proyecto realizado para la materia **Bases de Datos Avanzadas (BDA)**.

---

## 📌 Sobre el proyecto

**Tu Tiket** permite administrar eventos, usuarios, compras y boletos desde una aplicación local desarrollada en Java.

El sistema cuenta con tres tipos de usuarios:

| 👤 Usuario            | Funciones principales                                                                   |
| --------------------- | --------------------------------------------------------------------------------------- |
| 🛡️ **Administrador** | Administra usuarios, promotoras y eventos. Puede cancelar eventos y generar reembolsos. |
| 🎫 **Promotora**      | Crea y administra sus eventos.                                                          |
| 🛒 **Cliente**        | Consulta eventos, compra y cancela boletos, administra cuentas y tarjetas.              |

---

## ✨ Funciones

* 🔐 Registro e inicio de sesión.
* 🎪 Creación y administración de eventos.
* 🎟️ Compra y cancelación de boletos.
* ⏱️ Cancelación de compras dentro de 24 horas.
* 💳 Administración de cuentas y tarjetas.
* 💰 Recarga de saldo.
* 🔄 Reembolsos automáticos al cancelar eventos.
* 📄 Generación, descarga e impresión de tickets.
* 📊 Consulta del estado de los boletos.

---

## 🧪 Cuentas de prueba

|        Tipo       | Usuario          | Contraseña |
| :---------------: | ---------------- | :--------: |
| 🛡️ Administrador | `admin`          |    `123`   |
|    🎫 Promotora   | `promotora1`     |    `123`   |
|    🎫 Promotora   | `promotora2`     |    `123`   |
|     🛒 Cliente    | `cliente_prueba` |    `123`   |

---

## 🛠️ Tecnologías

```text
Java
JDBC
MySQL
Mindrot
iTextPDF
```

* **JDBC** → conexión con la base de datos.
* **Mindrot** → conexión mediante SSH.
* **iTextPDF** → generación de tickets en PDF.

---

## 📂 Estructura principal

```text
src/
├── domain/
├── dto/
├── repository/
├── service/
└── view/
```

| Paquete      | Descripción                           |
| ------------ | ------------------------------------- |
| `domain`     | Entidades principales del sistema.    |
| `dto`        | Objetos para transportar información. |
| `repository` | Operaciones con la base de datos.     |
| `service`    | Lógica y reglas del sistema.          |
| `view`       | Pantallas de la aplicación.           |

---

## 🚀 Ejecución

1. Tener **Java** y **MySQL** instalados.
2. Crear la base de datos utilizando el script SQL del proyecto.
3. Configurar la conexión a MySQL.
4. Abrir el proyecto en el IDE.
5. Ejecutar la aplicación.
6. Iniciar sesión con una de las cuentas de prueba.

---

<div align="center">

### 🎟️ TU TIKET

**Bases de Datos Avanzadas · Ingeniería en Software · ITSON**

</div>
