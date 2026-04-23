# Core API & Guardrails

Spring Boot 3.x backend for the assignment. It uses PostgreSQL for durable entities and Redis for all volatile guardrails, virality counters, cooldowns, and batched notifications.

## What it implements
- `POST /api/posts` to create a post
- `POST /api/posts/{postId}/comments` to add a comment
- `POST /api/posts/{postId}/like` to like a post
- Redis-backed virality scoring
- Redis atomic bot cap and cooldown checks
- Notification batching with a scheduled sweeper

## Thread safety approach
Bot comment reservations are handled with a Redis Lua script so the bot cap check, cooldown check, and counter increment happen atomically. That prevents race conditions when many requests hit the same post concurrently. PostgreSQL writes happen only after Redis allows the action, so Redis acts as the gatekeeper and the application remains stateless.

## Local run
1. Start PostgreSQL and Redis:

```bash
docker compose up -d
```

2. Run the Spring Boot app from this folder with Java 17+.

3. API base URL:

```text
http://localhost:8080
```

## Example payloads
Create post:

```json
{
  "authorType": "USER",
  "authorId": 1,
  "content": "My post"
}
```

Add comment:

```json
{
  "authorType": "BOT",
  "authorId": 10,
  "content": "Bot reply",
  "depthLevel": 1,
  "targetHumanId": 1
}
```

Like post:

```json
{
  "actorType": "USER",
  "actorId": 1
}
```
