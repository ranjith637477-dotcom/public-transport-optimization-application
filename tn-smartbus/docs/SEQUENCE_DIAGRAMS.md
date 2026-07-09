# TN SmartBus — Sequence Diagrams

## 1. GPS ingestion → live tracking broadcast

Covers both sources: the dummy GPS simulator (Phase 1) and a real driver
app / GPS device (Phase 3) — both converge on the same
`LiveTrackingService`, so this diagram applies to either.

```mermaid
sequenceDiagram
    participant Sim as GPS Simulator / Driver App
    participant TC as TrackingController / DriverController
    participant LTS as LiveTrackingService
    participant DB as PostgreSQL + PostGIS
    participant CPS as CrowdPredictionService
    participant AI as FastAPI AI Service
    participant WS as WebSocket (STOMP /topic/buses/live)
    participant App as Passenger App (React)

    Sim->>TC: POST location (lat, lng, speed, heading)
    TC->>LTS: updateTripLocation(tripId, lat, lng, speed, heading)
    LTS->>DB: UPDATE trips SET current_location, speed, heading
    TC->>LTS: broadcastFleetSnapshot()
    LTS->>DB: findAllRunningTrips()
    loop for each running trip
        LTS->>CPS: predictCrowdLevel(trip)
        CPS->>DB: check recent crowd_data report
        alt recent conductor report exists
            CPS-->>LTS: crowdLevel (ground truth)
        else no recent report
            CPS->>AI: POST /predict/crowd
            AI-->>CPS: crowdLevel (ML prediction)
        end
    end
    LTS->>WS: send(List<LiveBusDto>)
    WS-->>App: live fleet snapshot (every 5s)
    App->>App: update Leaflet markers on map
```

## 2. Trip lifecycle (driver + conductor)

```mermaid
sequenceDiagram
    participant Driver as Driver (mobile/API client)
    participant Conductor as Conductor (mobile/API client)
    participant BE as Spring Boot Backend
    participant DB as PostgreSQL

    Driver->>BE: POST /api/v1/auth/otp/request {phone}
    BE-->>Driver: OTP sent (logged to console in dev)
    Driver->>BE: POST /api/v1/auth/otp/verify {phone, otp}
    BE-->>Driver: JWT (role=DRIVER)

    Driver->>BE: POST /driver/trips/{id}/start
    BE->>DB: UPDATE trips SET status='RUNNING', actual_start_time=now()
    BE-->>Driver: 200 OK

    loop every 5s during the trip
        Driver->>BE: POST /driver/trips/{id}/location
        BE->>DB: persist location (see diagram 1)
    end

    Conductor->>BE: POST /conductor/trips/{id}/crowd {occupiedSeats, standingCount}
    BE->>DB: INSERT crowd_data (crowd_level computed from occupancy ratio)

    Conductor->>BE: POST /conductor/trips/{id}/tickets {ticketsIssued, revenue}
    BE->>DB: UPSERT ticket_statistics

    Driver->>BE: POST /driver/trips/{id}/end
    BE->>DB: UPDATE trips SET status='COMPLETED', actual_end_time=now()

    Conductor->>BE: POST /conductor/trips/{id}/complete
    BE->>DB: UPDATE trips SET status='COMPLETED'
```

## 3. Passenger journey planning

```mermaid
sequenceDiagram
    participant App as Passenger App
    participant BE as Backend
    participant DB as PostgreSQL

    App->>BE: POST /api/v1/journey/plan {sourceStopName, destinationStopName}
    BE->>DB: query route_stops self-join for matching routes
    DB-->>BE: candidate routes with distance/duration
    BE->>BE: FareCalculatorService.calculate() per route
    BE-->>App: ranked JourneyPlanOption[] with fare pre-computed
    App->>App: render options, sorted by duration
```
