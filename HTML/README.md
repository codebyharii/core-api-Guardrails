# Guardrails API Console

This folder contains a single standalone HTML page that lets you test the Core API & Guardrails backend from the browser.

## Files

- `guardrails-api.html` - the main page
- `style.css` - existing HTML folder styles, if you want to reuse them

## What it does

The page can:

- create a post
- like a post
- add a comment
- show the raw API response on the page

It talks to the backend at `http://localhost:8080` by default.

## Prerequisites

Start the backend first:

1. Run PostgreSQL and Redis from the `Core API & Guardrails` folder:

```powershell
docker compose up -d
```

2. Start the Spring Boot app:

```powershell
mvn spring-boot:run
```

3. Make sure the app is reachable at:

```text
http://localhost:8080
```

## Seed data

The page is prefilled for these sample records:

- User `1` - Alice
- User `2` - Bob
- Bot `3` - TechBot
- Bot `4` - NewsBot

If your database is empty, insert the same sample data before testing.

## How to use the page

1. Open `guardrails-api.html` in a browser.
2. Leave the base URL as `http://localhost:8080` unless your backend runs elsewhere.
3. Use **Create Post** first to get a fresh post id.
4. Use **Like Post** or **Add Comment** with that post id.
5. The API response appears in the response panel at the bottom.

## API endpoints used

- `POST /api/posts`
- `POST /api/posts/{postId}/like`
- `POST /api/posts/{postId}/comments`

## Example payloads

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

## Notes

- The backend has CORS enabled for `/api/**`, so the page can call the API directly.
- If the API response says `Not Found` or `User not found`, check that the seed data was inserted correctly.
- If you change the backend port, update the base URL at the top of the page.