# IGSM Chatbot Backend

Este es el backend del chatbot del IGSM, construido con Java y Spring Boot.

## Requisitos

- Java 17 o superior
- Maven

## Cómo correr la aplicación

1.  Abre una terminal en la carpeta `backend`.
2.  Ejecuta el siguiente comando:

```bash
mvn spring-boot:run
```

La aplicación iniciará en el puerto configurado (por defecto 8080).

## Endpoints

- Webhook: `POST /api/webhook/evolution`

##detalle de cada parte del documento 


-----

# 🤖 Backend de Chatbot IGSM (IGSM.2)

Este proyecto es el componente backend de un Chatbot para el sistema IGSM.2, desarrollado en **Java con Spring Boot** y utilizando **Maven** como herramienta de gestión de dependencias. Está diseñado para manejar la lógica del chatbot, la persistencia de datos y la integración con servicios externos (como la API de Evolution, presumiblemente para la mensajería).

## 🚀 Estructura del Proyecto

A continuación, se detalla la función de cada archivo y directorio clave dentro de la estructura.

### 📁 Archivos Raíz y Configuración

| Archivo/Directorio | Uso Principal |
| :--- | :--- |
| `Dockerfile` | **Contenerización.** Contiene las instrucciones para construir una imagen de Docker del backend, permitiendo que la aplicación se ejecute en un entorno consistente y aislado. |
| `pom.xml` | **Gestión de Dependencias (Maven).** Define el proyecto, sus dependencias de Java (por ejemplo, Spring Boot, la base de datos, etc.), y los *plugins* necesarios para la construcción, prueba y empaquetado. |
| `README.md` | **Documentación.** Este archivo, que proporciona una visión general del proyecto, cómo configurarlo, ejecutarlo y comprender su estructura. |
| `set_webhook.sh` | **Script de Configuración.** Es un *script* de *shell* que se utiliza probablemente para configurar el *webhook* en un servicio de mensajería (como WhatsApp, Telegram, etc.) para que las notificaciones entrantes se dirijan a la URL de este backend. |
| `src/` | **Código Fuente y Recursos.** Contiene todo el código fuente y los archivos de configuración no compilados. |
| `target/` | **Artefactos de Construcción.** Directorio generado por Maven que contiene los archivos compilados, como las clases (`.class`) y los archivos empaquetados (`.jar`, si aplica). **No debe ser incluido en el control de versiones.** |

-----

### 💻 Directorio `src/main/java/com/igsm/chatbot`

Este es el núcleo de la aplicación Spring Boot, siguiendo la convención estándar del patrón **MVC (Modelo-Vista-Controlador)** junto con capas de **Servicio** y **Repositorio**.

#### 1\. Archivos de Ejecución y Configuración

| Archivo | Uso Específico |
| :--- | :--- |
| `ChatbotApplication.java` | **Punto de Entrada.** Contiene el método `main()` que inicia la aplicación Spring Boot. Está anotado con `@SpringBootApplication`. |
| `config/DataInitializer.java` | **Inicialización de Datos.** Un componente que se ejecuta al inicio de la aplicación para cargar o configurar datos iniciales necesarios en la base de datos (por ejemplo, cargar las `Diplomatura`s disponibles). |

#### 2\. Paquete `controller` (Capas de Interfaz Web)

Manejan las solicitudes HTTP entrantes y dirigen el flujo de la aplicación.

| Archivo | Uso Específico |
| :--- | :--- |
| `DiplomaturaController.java` | **API de Diplomaturas.** Expone *endpoints* HTTP para gestionar y/o consultar información sobre las diplomaturas (listarlas, obtener detalles, etc.). |
| `StatsController.java` | **API de Estadísticas.** Expone *endpoints* HTTP para obtener métricas y estadísticas sobre el uso del chatbot (ej. número de consultas, suscripciones, etc.). |
| `WebhookController.java` | **Manejo de Mensajería.** Es el *endpoint* principal que recibe los mensajes y eventos entrantes del servicio de mensajería (a través del *webhook* configurado). Contiene la lógica inicial para procesar los mensajes del usuario. |

#### 3\. Paquete `model` (Capas de Datos/Modelo)

Clases que representan la estructura de los datos de la aplicación, generalmente mapeados a la base de datos (Entidades).

| Archivo | Uso Específico |
| :--- | :--- |
| `Diplomatura.java` | **Modelo de Contenido.** Representa una diplomatura, conteniendo campos como nombre, descripción, duración, etc. |
| `Inquiry.java` | **Modelo de Consulta.** Representa una consulta o interacción de un usuario con el chatbot, útil para el seguimiento y las estadísticas. |
| `Subscription.java` | **Modelo de Suscripción.** Representa la suscripción de un usuario a notificaciones o a una diplomatura específica. |

#### 4\. Paquete `repository` (Capas de Persistencia)

Interfaces que extienden las interfaces de Spring Data JPA para proporcionar operaciones CRUD (Crear, Leer, Actualizar, Borrar) y consultas personalizadas a la base de datos para cada modelo.

| Archivo | Uso Específico |
| :--- | :--- |
| `DiplomaturaRepository.java` | Repositorio para la entidad `Diplomatura`. |
| `InquiryRepository.java` | Repositorio para la entidad `Inquiry`. |
| `SubscriptionRepository.java` | Repositorio para la entidad `Subscription`. |

#### 5\. Paquete `service` (Capas de Lógica de Negocio)

Contienen la lógica de negocio central, orquestando las llamadas a los repositorios y a otros servicios.

| Archivo | Uso Específico |
| :--- | :--- |
| `EvolutionApiService.java` | **Servicio de Integración Externa.** Maneja la comunicación con el servicio de API de Evolution (presumiblemente para enviar mensajes al usuario) y transforma los datos según sea necesario. |
| `UserSessionService.java` | **Gestión de Sesiones.** Se encarga de mantener el estado de la conversación de cada usuario (ej. ¿en qué paso de la conversación se encuentra el usuario?), permitiendo interacciones conversacionales más complejas. |

-----

### ⚙️ Directorio `src/main/resources`

| Archivo | Uso Específico |
| :--- | :--- |
| `application.properties` | **Configuración de la Aplicación.** Contiene propiedades de configuración clave para Spring Boot, como la configuración de la base de datos (URL, usuario, contraseña), el puerto del servidor y las claves de la API de Evolution u otros servicios. |

-----

## 🛠️ Cómo Iniciar el Proyecto

1.  **Requisitos:** Tener instalado **Java JDK** (versión compatible con Spring Boot) y **Maven**.
2.  **Configuración:** Editar el archivo `src/main/resources/application.properties` con las credenciales de la base de datos y la configuración de la API externa (Evolution).
3.  **Compilación:** Abrir una terminal en el directorio raíz del proyecto y ejecutar:
    ```bash
    mvn clean install
    ```
4.  **Ejecución:**
    ```bash
    java -jar target/NOMBRE_DEL_JAR.jar # Reemplazar con el nombre del JAR generado.
    ```
5.  **Configuración del Webhook:** Ejecutar el script (ajustando la URL y el token si es necesario):
    ```bash
    ./set_webhook.sh
    ```

-----

¿Hay algún aspecto de la estructura del proyecto o un archivo en particular que te gustaría que se detallara aún más?
