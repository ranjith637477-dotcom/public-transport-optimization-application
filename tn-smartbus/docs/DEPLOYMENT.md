# TN SmartBus — Deployment Guide

## 1. Prerequisites

- Java 17+, Maven (or use the included `./mvnw` wrapper)
- Node.js 18+ and npm
- Python 3.10+
- PostgreSQL 15+ with the PostGIS extension
- Redis 7+

## 2. Local development setup

### 2.1 Database

```bash
createdb tnsmartbus
psql tnsmartbus -f database/schema.sql
psql tnsmartbus -f database/seed_data.sql
```

### 2.2 Backend (Spring Boot)

```bash
cd backend
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=change-this-in-production
export AI_SERVICE_URL=http://localhost:8000
./mvnw spring-boot:run
```
Runs on `http://localhost:8080`. Swagger UI at `/swagger-ui.html`.

### 2.3 AI microservice (FastAPI)

```bash
cd ai-service
pip install -r requirements.txt
python training/train_models.py   # trains and saves models/*.joblib (only needed once)
uvicorn main:app --reload --port 8000
```
Runs on `http://localhost:8000`. Health check: `GET /health`.

### 2.4 Admin dashboard (React)

```bash
cd frontend/admin-dashboard
npm install
npm run dev
```
Runs on `http://localhost:5173`. Sign in with phone `9000000000` (OTP
printed in the backend console).

### 2.5 Passenger app (React)

```bash
cd frontend/passenger-app
npm install
npm run dev
```
Runs on `http://localhost:5174`.

### 2.6 Bring it all up in order

1. PostgreSQL + Redis running
2. Load schema + seed data
3. Start the AI service (train models first, once)
4. Start the backend
5. Start the admin dashboard and/or passenger app

The GPS simulator starts automatically with the backend
(`app.gps-simulator.enabled=true`) and animates the two demo trips from the
seed data, so the map has live data within 5 seconds of backend startup —
no need to wait for a real driver to start a trip.

## 3. Environment variables reference

| Variable | Used by | Default | Notes |
|---|---|---|---|
| `DB_USERNAME` / `DB_PASSWORD` | backend | `postgres` / `postgres` | |
| `JWT_SECRET` | backend | dev placeholder | **must** change in production |
| `AI_SERVICE_URL` | backend | `http://localhost:8000` | |
| `REDIS_HOST` / `REDIS_PORT` | backend | `localhost` / `6379` | |
| `VITE_BACKEND_URL` | both frontends | `http://localhost:8080` | set in `.env` per app |

## 4. Production deployment notes

This project is structured to deploy each piece independently:

- **Backend**: package as a jar (`./mvnw clean package`) and run behind a
  reverse proxy (nginx) or containerize. Point it at a managed PostgreSQL
  with PostGIS (e.g. AWS RDS with the PostGIS extension, or a self-hosted
  instance) and a managed Redis.
- **AI microservice**: containerize with the trained `models/*.joblib`
  baked into the image (or mounted from object storage), run behind
  gunicorn/uvicorn workers.
- **Frontends**: `npm run build` produces a static `dist/` folder for each
  app — serve via any static host (S3 + CloudFront, Netlify, Nginx).
- **Real GPS ingestion**: disable the simulator
  (`app.gps-simulator.enabled=false`) once real devices or the driver app
  are pushing locations via `/api/v1/driver/trips/{id}/location`.
- **Secrets**: `JWT_SECRET`, DB credentials, and any future SMS gateway
  keys belong in a secrets manager, not environment variables checked into
  version control.
- **SMS gateway**: `OtpService.sendSms()` currently logs the OTP; swap in a
  real provider (MSG91, Twilio) before any real users interact with the
  system.

## 5. What's NOT yet implemented (be upfront about this in a viva)

- Google OAuth login (stubbed with a clear TODO in `AuthController`)
- Offline mode / PWA service worker for the passenger app
- Real historical training data for the AI models (currently trained on a
  realistic synthetic dataset — see `ai-service/training/train_models.py`
  for how to swap in real `gps_history`/`crowd_data` once accumulated)
- Push notifications via Firebase Cloud Messaging (notifications table
  exists in the schema; delivery integration is a follow-up)
- Multi-leg (transfer) journey planning — only direct routes are currently
  matched
