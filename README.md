Estado actual: no esta desplegado en la vps

# IGSM Chatbot Project

Este proyecto implementa un chatbot de WhatsApp para el Instituto IGSM utilizando **Evolution API** y un backend en **Java (Spring Boot)**.

## 🚀 Estructura del Proyecto

- `shell-evolution-api/`: Contiene la configuración de Docker para la API de WhatsApp (Evolution API, Postgres, Redis).
- `backend/`: Código fuente del bot en Java Spring Boot.

##falta dockerizar la parte del backend
## 🛠️ Requisitos

- Docker y Docker Compose
- Java 17 o superior
- Maven #MAven es una tecnologia que permite copmpilar y ejecutar el codigo funete de java

## ⚙️ Configuración y Puesta en Marcha

### 1. Iniciar todo el stack
Desde la carpeta raíz `IGSM`:

```bash
docker compose up -d --build
```

Esto levantará:
- Evolution API (Puerto 8080)
- Postgres
- Redis
- IGSM Chatbot Backend (Puerto 8081)

### 3. Configurar el Webhook (¡IMPORTANTE!)
Para que Evolution API le envíe los mensajes al Bot, deben estar conectados.
Como corren en entornos distintos (Docker vs Host), usamos la **IP Local** de tu máquina.

**⚠️ Si cambias de red (WiFi/Cable) o reinicias el router, tu IP puede cambiar.**

Si el bot deja de responder:
1.  Verifica tu nueva IP:
    ```bash
    hostname -I
    ```
2.  Edita el archivo `backend/set_webhook.sh` y actualiza la línea `WEBHOOK_URL` con tu nueva IP.
3.  Ejecuta el script:
    ```bash
    cd backend
    ./set_webhook.sh
    ```

## 🔐 Información Sensible y Configuración

- **API Key**: Se define en `shell-evolution-api/.env` (`AUTHENTICATION_API_KEY`) y debe coincidir en `backend/src/main/resources/application.properties`.
- **Instancia**: El nombre de la instancia (ej: `diplos`) también debe coincidir en ambos lados.

## 📝 Comandos Útiles

- **Ver logs del bot**: Mirar la terminal donde corre `mvn spring-boot:run`.
- **Ver logs de la API**: `docker logs -f evolution_api`
- **Reiniciar API**: `docker restart evolution_api`
