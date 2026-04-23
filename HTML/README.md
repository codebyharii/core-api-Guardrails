# Guardrails API Console

This folder contains a single HTML page that acts as a lightweight browser client for the Core API & Guardrails backend.

Use it when you want to test the API without Postman.

## What is inside

- [guardrails-api.html](guardrails-api.html) - the main page
- [style.css](style.css) - legacy styles for other HTML samples in this folder

## What the page does

The page lets you:

- create a post
- like a post
- add a comment
- inspect the raw JSON response

## Before you start

Make sure the backend is running first.

1. Open the backend folder:

```powershell
cd "Core API & Guardrails"
```

2. Start PostgreSQL and Redis:

```powershell
docker compose up -d
```

3. Start the Spring Boot application:

```powershell
mvn spring-boot:run
```

4. Confirm the backend is reachable at:

```text
http://localhost:8080
```

## Open the page

Open [guardrails-api.html](guardrails-api.html) in any browser.

The base URL is set to `http://localhost:8080` by default. Change it at the top of the page if your backend runs somewhere else.

## Seed data used by the demo

The page is prefilled for these sample records:

- User `1` - Alice
- User `2` - Bob
- Bot `3` - TechBot
- Bot `4` - NewsBot

If the database is empty, insert these records before testing.

## API actions

The page calls these endpoints:

- `POST /api/posts` - create a post
- `POST /api/posts/{postId}/like` - like a post
- `POST /api/posts/{postId}/comments` - add a comment

## Example requests

Create post:

```json
{
  "authorType": "USER",
  "authorId": 1,
  "content": "First post from the demo page."
}
```

Like post:

```json
{
  "actorType": "USER",
  "actorId": 2
}
```

Add comment:

```json
{
  "authorType": "BOT",
  "authorId": 3,
  "content": "Spring Boot is awesome!",
  "depthLevel": 1,
  "targetHumanId": 1
}
```

## If something fails

- If you see `User not found`, verify the sample data was inserted.
- If the browser blocks requests, make sure the backend is running and CORS is enabled.
- If you changed the backend port, update the base URL field in the page.