# core-api-Guardrails

This repository contains two parts:

- a Spring Boot backend called **Core API & Guardrails**
- a standalone browser UI called **Guardrails API Console**

The backend stores posts, comments, and users in PostgreSQL and uses Redis for guardrails, cooldowns, and virality tracking. The HTML console is a simple page you can open in a browser to test the API without Postman.

## Main folders

- [Core API & Guardrails](Core%20API%20%26%20Guardrails) - Spring Boot service
- [HTML](HTML) - standalone HTML demo page and documentation

## Quick start

1. Start the required services from the backend folder:

```powershell
cd "Core API & Guardrails"
docker compose up -d
```

2. Run the Spring Boot app:

```powershell
mvn spring-boot:run
```

3. Open [HTML/guardrails-api.html](HTML/guardrails-api.html) in a browser.

## What you can test

- create a post
- like a post
- add a comment
- see the raw API response on the page

## Default local URL

The HTML page uses this backend URL by default:

```text
http://localhost:8080
```

## Sample data

The demo page is prefilled for these ids:

- User `1` - Alice
- User `2` - Bob
- Bot `3` - TechBot
- Bot `4` - NewsBot

If the database is empty, seed these records before testing the page.