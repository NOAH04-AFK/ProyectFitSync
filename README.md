# 🏋️ FitSync Pro - Sistema de Gestión Inteligente para Gimnasios

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Swing](https://img.shields.io/badge/GUI-Java_Swing-blue.svg)
![Status](https://img.shields.io/badge/Estado-Completado-brightgreen.svg)
![UDLA](https://img.shields.io/badge/Universidad-UDLA-red.svg)

FitSync Pro es una aplicación de escritorio desarrollada en **Java Swing** centrada en la eficiencia algorítmica y el manejo de estructuras de datos en memoria principal (RAM). Está diseñada para resolver la problemática operativa de alta concurrencia en centros de acondicionamiento físico, eliminando cuellos de botella mediante la automatización de rutinas, seguimiento antropométrico y un sistema de gamificación auto-balanceado.

---

## 🎯 Contexto y Problemática
El sistema nace para resolver la saturación operativa de "TOP Gym", donde en horas pico coinciden hasta 80 usuarios con apenas 5 entrenadores (relación 16:1). La dependencia exclusiva de procesos manuales y fichas de papel generaba tiempos de espera superiores a los 40 minutos para la asignación de rutinas, además de una pérdida de trazabilidad en el rendimiento de los socios, causando desmotivación y abandono. 

FitSync Pro aborda este problema garantizando una respuesta inmediata mediante una arquitectura de software enfocada en el rendimiento en memoria.

---

## 🧠 Arquitectura y Estructuras de Datos
En lugar de depender de consultas I/O a motores de bases de datos relacionales en tiempo real, el sistema implementa una **Arquitectura Híbrida** en memoria utilizando las siguientes estructuras de datos para garantizar tiempos de respuesta óptimos (Notación Big-O):

1. **HashMap (Tabla de Dispersión):** Utilizado en el núcleo transaccional (Gestión de Socios) para garantizar accesos, búsquedas y recuperaciones de perfiles en tiempo constante promedio **O(1)**.
2. **LinkedList (Listas Enlazadas):** Implementado en el Módulo de Rutinas para permitir inserciones y eliminaciones dinámicas de ejercicios sobre la marcha en **O(1)** (una vez referenciado el nodo), evitando desplazamientos de memoria característicos de los arreglos estáticos.
3. **ArrayList (Arreglos Dinámicos):** Utilizado en el historial clínico (Condición Física) para garantizar accesibilidad secuencial por índice, maximizando la tasa de aciertos en la memoria caché al renderizar tablas.
4. **Árbol AVL (Auto-balanceado):** Estructura central del Módulo de Gamificación. Mantiene el ranking de constancia de los usuarios, insertando y reubicando socios basándose en su puntaje en tiempo logarítmico **O(log n)**, e imprimiendo el ranking mediante un recorrido in-order descendente.

---

## 🧩 Módulos del Sistema

El sistema está dividido en 5 módulos integrados mediante el patrón Singleton y divididos por control de acceso basado en roles (Administrador, Entrenador, Socio):

* **Módulo 1 - Socios y Entrenadores:** CRUD completo de perfiles. Controla la relación límite de usuarios por entrenador.
* **Módulo 2 - Rutinas y Equipos:** Asignación de días de entrenamiento ("Mi Plan"), visualización de videotutoriales y modificación dinámica de ejercicios ante averías en la infraestructura.
* **Módulo 3 - Evolución Física:** Registro antropométrico, cálculo automatizado de IMC y validación de seguridad para evitar la asignación de ejercicios contraindicados por lesiones.
* **Módulo 4 - Gamificación y Performance:** Asignación de puntos por constancia y sobrecarga progresiva. Incluye un sistema transaccional de **Canje de Recompensas** acoplado al rebalanceo del Árbol AVL.
* **Módulo 5 - Reportes de Gestión:** Matrices e indicadores gerenciales para la toma de decisiones del negocio (Retención, aforo y membresías).

---

## ⚙️ Requisitos Previos (Prerequisites)

Para compilar y ejecutar este proyecto de forma local, necesitas:

* **Java Development Kit (JDK):** Versión 21 o superior.
* **IDE Recomendado:** IntelliJ IDEA, Apache NetBeans o Eclipse (Soporte nativo para proyectos Java con GUI).
* **Git:** Para la clonación del repositorio.

---

## 🚀 Instalación y Ejecución

### 1. Abrir el proyecto
* Abre tu IDE (ej. IntelliJ IDEA).
* Selecciona `File > Open` y busca la carpeta del proyecto recién clonado.
* Espera a que el IDE indexe los archivos y reconozca la estructura de paquetes `ec.edu.udla.fitsyncpro`.

### 2. Configurar el JDK
* Asegúrate de que tu IDE está utilizando el **JDK 21** en la configuración del proyecto (`Project Structure`).

### 3. ⚠️ IMPORTANTE: Agregar la librería de diseño GUI (FlatLaf)
El proyecto utiliza un archivo `.jar` externo (`flatlaf-3.4.1.jar`) para renderizar el diseño moderno de la interfaz gráfica. **Si omites este paso, el proyecto dará error y no compilará.**

* Ve al menú superior y selecciona `File > Project Structure...` (Estructura del Proyecto).
* En el menú lateral izquierdo, selecciona `Modules` (o *Dependencies* si usas otro IDE).
* Selecciona la pestaña `Dependencies`.
* Haz clic en el ícono del **`+`** (Añadir) y selecciona `JARs or directories...`
* Busca en la carpeta del proyecto el archivo llamado `flatlaf-3.4.1.jar`, selecciónalo y presiona **OK**.
* Haz clic en **Apply** y luego en **OK**.

### 4. Ejecutar la aplicación
* Navega hasta el paquete principal que contiene la vista de inicio.
* Haz clic derecho sobre el archivo y selecciona **Run**.

### 5. Credenciales de prueba
El sistema precarga automáticamente datos de prueba en la RAM para evaluar las estructuras. Puedes ingresar seleccionando el rol de **Entrenador** o **Socio** directamente en la pantalla de inicio.
