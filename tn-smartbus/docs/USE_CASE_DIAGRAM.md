# TN SmartBus — Use Case Diagram

```mermaid
flowchart LR
    Passenger((Passenger))
    Driver((Driver))
    Conductor((Conductor))
    Admin((Admin))

    subgraph Passenger Use Cases
        UC1[Track live buses on map]
        UC2[Search bus / route / stop]
        UC3[View nearby stops and buses]
        UC4[Plan a journey]
        UC5[Calculate fare]
        UC6[Save favorite routes/stops]
        UC7[File a complaint]
        UC8[Report/search lost & found items]
        UC9[Raise emergency SOS]
        UC10[Register / OTP login]
    end

    subgraph Driver Use Cases
        UC11[Start / end trip]
        UC12[Push live GPS location]
        UC13[Report break/fuel/bus-health status]
        UC14[Raise emergency alert]
    end

    subgraph Conductor Use Cases
        UC15[Log crowd/occupancy]
        UC16[Log ticket sales]
        UC17[Mark trip complete]
    end

    subgraph Admin Use Cases
        UC18[Manage routes]
        UC19[Manage fleet]
        UC20[Triage complaints]
        UC21[View analytics dashboard]
    end

    Passenger --> UC1
    Passenger --> UC2
    Passenger --> UC3
    Passenger --> UC4
    Passenger --> UC5
    Passenger --> UC6
    Passenger --> UC7
    Passenger --> UC8
    Passenger --> UC9
    Passenger --> UC10

    Driver --> UC11
    Driver --> UC12
    Driver --> UC13
    Driver --> UC14

    Conductor --> UC15
    Conductor --> UC16
    Conductor --> UC17

    Admin --> UC18
    Admin --> UC19
    Admin --> UC20
    Admin --> UC21

    UC12 -.includes.-> UC1
    UC15 -.includes.-> UC1
```

**Notes:**
- `UC12` (driver GPS push) and `UC15` (conductor crowd log) both feed into
  `UC1` (live tracking) — this is the "includes" relationship shown, since
  the passenger-facing live map is only as good as what drivers/conductors
  report.
- All driver/conductor/admin use cases require JWT authentication scoped to
  that role (`SecurityConfig`); passenger use cases 1-3 are public reads,
  4-9 require passenger authentication.
