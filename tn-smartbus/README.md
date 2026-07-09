# TN SmartBus — AI Powered Rural Public Transport Management System

A production-structured platform for Tamil Nadu State Transport Corporation
(TNSTC) focused on rural bus routes, where live tracking currently doesn't
exist. Built as a final year engineering capstone project.

## What's in this project

| Component | Tech | Location |
|---|---|---|
| Backend API | Spring Boot 3, PostgreSQL + PostGIS, Redis, WebSocket (STOMP), JWT | `backend/` |
| AI microservice | FastAPI, scikit-learn (trained models) | `ai-service/` |
| Admin dashboard | React, Redux Toolkit, Tailwind, Recharts | `frontend/admin-dashboard/` |
| Passenger app | React, Redux Toolkit, Tailwind, Leaflet, Framer Motion | `frontend/passenger-app/` |
| Database | PostgreSQL + PostGIS schema and seed data | `database/` |
| Diagrams & docs | ER diagram, sequence diagrams, use-case diagram, DFD, deployment guide | `docs/` |

## Features implemented

**Passenger**
- Live bus tracking on a map, updating every 5 seconds (WebSocket + REST fallback)
- Nearby stops and nearby buses (PostGIS radius search)
- Unified search across bus number, route number/name, source/destination, intermediate stops
- Full route view: stop-by-stop breakdown with distance/timing, schedules, and active route alerts
- Journey planner (direct routes between two stops, ranked by duration, with fare pre-computed)
- Fare calculator (slab-based by bus type, with student/senior-citizen concession)
- Crowd level indicator (LOW/MEDIUM/HIGH) — backed by real conductor-reported occupancy when available, an AI model otherwise, and a time-of-day heuristic as a last resort
- Favorite routes and stops
- Complaints (driver/conductor/bus/road) and trip feedback/ratings
- Lost & found (report and browse)
- In-app notifications (arrival/delay/cancellation/alert)
- Emergency SOS (shares live location, optionally tied to a trip)
- Phone OTP login, Tamil/English language toggle
- Dark, glassmorphism, mobile-first UI with Leaflet map and live-updating cards

**Driver**
- Start/end trip, push real GPS location
- Report break/fuel/bus-health status
- Raise an emergency alert

**Conductor**
- Log real occupied-seat/standing-passenger counts (feeds the crowd indicator)
- Log ticket sales
- Mark trip complete

**Admin**
- Manage routes and fleet (full CRUD)
- Complaints triage queue with status transitions
- Live dashboard (total/running/delayed buses, average delay, open complaints)
- Analytics (most crowded routes, from real occupancy data)

**AI**
- Crowd level prediction (RandomForestClassifier, ~87% accuracy on held-out synthetic data)
- ETA prediction (RandomForestRegressor)
- Delay prediction (RandomForestRegressor)
- Route stop-ordering optimization (nearest-neighbour heuristic)

All three prediction endpoints are trained on a realistic synthetic dataset
(no real TNSTC historical data exists yet) — see
`ai-service/training/train_models.py` for the generation logic and the note
on swapping in real data once `gps_history`/`crowd_data` accumulate.

## Quick start

See **`docs/DEPLOYMENT.md`** for full setup instructions. Short version:

```bash
# 1. Database
createdb tnsmartbus
psql tnsmartbus -f database/schema.sql
psql tnsmartbus -f database/seed_data.sql

# 2. AI service
cd ai-service && pip install -r requirements.txt
python training/train_models.py
uvicorn main:app --reload --port 8000 &

# 3. Backend
cd ../backend
export DB_USERNAME=postgres DB_PASSWORD=postgres
./mvnw spring-boot:run &

# 4. Frontends
cd ../frontend/admin-dashboard && npm install && npm run dev &
cd ../passenger-app && npm install && npm run dev
```

- Passenger app: `http://localhost:5174`
- Admin dashboard: `http://localhost:5173` (sign in with phone `9000000000`, OTP printed in the backend console)
- Backend Swagger docs: `http://localhost:8080/swagger-ui.html`
- AI service health check: `http://localhost:8000/health`

The GPS simulator starts automatically with the backend and animates two
demo buses on a real Tambaram–Kanchipuram rural route, so there's live data
on the map immediately — no need to wait for a real driver trip.

Seeded test accounts (all via OTP login — check the backend console log for the code):
- Admin: `9000000000`
- Driver: `9800000001` (assigned to the TN-101 demo trip)
- Conductor: `9800000002` (assigned to the TN-101 demo trip)

## Architecture

See the system architecture diagram shared earlier in this conversation,
and `docs/DFD.md` / `docs/SEQUENCE_DIAGRAMS.md` for how data flows between
components. In short: both React frontends and any driver/conductor client
talk to one Spring Boot backend over REST + WebSocket; the backend persists
to PostgreSQL/PostGIS and calls the FastAPI AI service for predictions,
falling back to rule-based logic if the AI service is unreachable.

## Honest limitations (worth knowing for a viva)

- **Google OAuth login** is stubbed with a clear TODO (`AuthController`) —
  needs real OAuth client credentials to finish.
- **AI models are trained on synthetic data**, not real TNSTC history,
  because no historical dataset exists yet. The training script is written
  so swapping in real `gps_history`/`crowd_data` later requires no change
  to `main.py`.
- **Multi-leg (transfer) journey planning** isn't implemented — only direct
  routes between two stops are matched.
- **Offline mode / PWA support** isn't implemented.
- **SMS delivery** is stubbed to a console log (`OtpService.sendSms()`) —
  swap in a real gateway (MSG91/Twilio) before any real users use this.
- **Push notifications** (Firebase Cloud Messaging) — the `notifications`
  table exists but delivery isn't wired up.

## Project structure

```
tn-smartbus/
├── backend/                  Spring Boot API (tracking, auth, driver/conductor, admin, analytics)
├── ai-service/                FastAPI ML service (crowd, ETA, delay, route optimization)
├── frontend/
│   ├── admin-dashboard/       React admin/ops dashboard
│   └── passenger-app/         React passenger mobile-first app
├── database/
│   ├── schema.sql             Full PostgreSQL + PostGIS schema
│   └── seed_data.sql          Sample rural routes, buses, admin/driver/conductor accounts
└── docs/
    ├── ER_DIAGRAM.md
    ├── SEQUENCE_DIAGRAMS.md
    ├── USE_CASE_DIAGRAM.md
    ├── DFD.md
    └── DEPLOYMENT.md
```
