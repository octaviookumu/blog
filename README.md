# Blog

A full-stack blogging platform with a Spring Boot backend and a React (Vite) frontend. Supports authenticated post authoring with drafts, categories, and tags, plus a public-facing read experience.

**Live:**

- Frontend: https://blog-frontend-sigma-livid.vercel.app
- Backend API: https://blog-4fwc.onrender.com/api/v1

## Tech stack

**Backend**

- Java 21, Spring Boot
- Spring Security with stateless JWT authentication (`jjwt`)
- Spring Data JPA + PostgreSQL
- MapStruct for entity ↔ DTO mapping
- springdoc-openapi (Swagger UI, disabled in production)

**Frontend**

- React 18 + TypeScript, built with Vite
- React Router
- TanStack Query for data fetching/caching
- NextUI + Tailwind CSS
- Tiptap for rich text editing

**Infrastructure**

- Neon (managed PostgreSQL) in production
- Render (backend, Docker-based deploy)
- Vercel (frontend)
- Local dev: Docker Compose (PostgreSQL + Adminer)

## Features

- Public browsing of published posts, filterable by category and tag
- JWT-based login for the author/admin
- Draft posts (authenticated-only) alongside published posts
- Rich text post editor (Tiptap)
- Category and tag management

## Project structure

```
blog/
├── backend/   # Spring Boot API
└── frontend/  # React + Vite app
```

## Getting started locally

### Prerequisites

- Java 21
- Node.js (LTS)
- Docker (for local Postgres)

### 1. Backend

```bash
cd backend
cp .env.example .env   # fill in real values, see below
docker-compose up -d   # starts Postgres on :5431 and Adminer on :8888
./mvnw spring-boot:run
```

The API runs on `http://localhost:8080` by default.

**Required `.env` values:**

| Variable                                            | Purpose                                                                                                |
| --------------------------------------------------- | ------------------------------------------------------------------------------------------------------ |
| `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` | Local Postgres container credentials                                                                   |
| `JWT_SECRET`                                        | Signing key for auth tokens — at least 32 bytes                                                        |
| `ADMIN_EMAIL`, `ADMIN_NAME`, `ADMIN_PASSWORD`       | Creates your one admin/author account on first boot (no public registration endpoint exists by design) |
| `CORS_ALLOWED_ORIGINS`                              | Comma-separated list of origins allowed to call the API                                                |

The admin account is created once, automatically, the first time the app boots with those values set (see `AdminSeeder`). It's safe to leave the values in place afterward — it won't recreate or overwrite an existing user.

### 2. Frontend

```bash
cd frontend
npm install
npm run dev
```

Set `VITE_API_BASE_URL` in a `.env.local` file if you're pointing at a backend other than the default relative `/api/v1` (e.g. the deployed Render instance). Include the `/api/v1` suffix — the frontend's API layer doesn't add it for you once this variable is set.

## API overview

All endpoints are under `/api/v1`.

| Endpoint        | Method           | Auth                            |
| --------------- | ---------------- | ------------------------------- |
| `/auth/login`   | POST             | Public                          |
| `/posts`        | GET              | Public                          |
| `/posts/{id}`   | GET, PUT, DELETE | GET public, write authenticated |
| `/posts/drafts` | GET              | Authenticated                   |
| `/categories`   | GET              | Public                          |
| `/tags`         | GET              | Public                          |

Interactive API docs (Swagger UI) are available at `/swagger-ui/index.html` in non-production environments.

## Deployment

See the [Deployment Guide](https://github.com/octaviookumu/deployment-guide) for the full checklist covering Neon, Render, and Vercel.
