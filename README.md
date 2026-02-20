# 📝 Test App: Exam Simulator

## Actualmente --> **WIP**

**Test App** es una herramienta educativa desarrollada en Java y JavaFX diseñada para ayudar a estudiantes a repasar 
contenidos de exámenes de forma interactiva. 

## ✨ Características Principales

* **Carga de Archivos Personalizada:** Mediante un selector de archivos nativo, puedes cargar cualquier test en formato XML.
* **Organización por Temas:** El programa agrupa automáticamente las preguntas según el atributo `tema` definido en el XML.
* **Modo de Repaso Inteligente:** Si fallas preguntas, el programa te permite realizar un test de "Repaso de fallos" al finalizar, enfocándote solo en lo que necesitas reforzar.
* **Feedback Inmediato:** Tras cada respuesta, recibirás una alerta indicando si has acertado y cuál era la opción correcta.
* **Mezcla Aleatoria:** Las preguntas se desordenan cada vez que inicias un tema para evitar que memorices el orden de las respuestas.

---

## 📂 Formato de los Cuestionarios (XML)

El programa lee archivos `.xml`. Para que tus tests funcionen, deben seguir este esquema:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<preguntas>
    <pregunta tema="1" multiple="false">
        <enunciado>¿Cuál es la ventaja de usar JDBC?</enunciado>
        <opcion>Independencia de la base de datos.</opcion>
        <opcion>Es más lento que el acceso directo.</opcion>
        <respuesta>0</respuesta> </pregunta>
</preguntas>

```

---

## 🚀 Cómo empezar

### Para Usuarios (Ejecución rápida)

1. Descarga el archivo `TestApp.jar` desde la sección de **Releases**.
2. Asegúrate de tener instalado **Java 17** o superior.
3. Ejecuta el archivo con doble clic o desde la consola:
```bash
java -jar TestApp.jar

```


4. Carga uno de los XML de ejemplo que encontrarás en la carpeta `/xml_test_examples`.

### Para Desarrolladores (Compilación)

Este proyecto utiliza **Maven**. Para generar tu propio ejecutable:

1. Clona el repositorio.
2. Ejecuta el comando de empaquetado:
```bash
mvn clean package

```


3. Encuentra tu JAR en la carpeta `target/`.

---

## 🏗️ Estructura del Proyecto

```text
├── src/main/java      # Código fuente (JavaFX + Lógica XML)
├── tests/             # Carpeta con archivos XML de ejemplo (Temas 1, 2, 3...)
├── pom.xml            # Configuración de dependencias Maven
└── README.md          # Documentación

```

---

## 🛠️ Tecnologías utilizadas

* **Java 23** (Compatible con Java 17+)
* **JavaFX 17**: Para la interfaz gráfica de usuario.
* **Maven**: Gestión de dependencias y construcción del proyecto.
* **DOM (javax.xml)**: Para el procesamiento de los archivos de preguntas.

---

## ⚖️ Licencia

Este proyecto es de código abierto y está disponible bajo la [Licencia MIT](). ¡Siéntete libre de clonarlo y mejorarlo!

