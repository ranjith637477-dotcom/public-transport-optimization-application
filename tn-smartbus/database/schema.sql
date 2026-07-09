-- =========================================================
-- TN SmartBus - PostgreSQL + PostGIS Schema
-- =========================================================

CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================================================
-- USERS & ROLES
-- =========================================================

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) UNIQUE NOT NULL -- PASSENGER, DRIVER, CONDUCTOR, ADMIN, DEPOT_MANAGER, GOV_ADMIN
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) UNIQUE NOT NULL,
    email VARCHAR(120) UNIQUE,
    password_hash VARCHAR(255),
    google_id VARCHAR(120),
    role_id INT REFERENCES roles(id) NOT NULL,
    preferred_language VARCHAR(10) DEFAULT 'en',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE otp_verifications (
    id SERIAL PRIMARY KEY,
    phone_number VARCHAR(15) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT now()
);

-- =========================================================
-- DEPOT / DRIVER / CONDUCTOR
-- =========================================================

CREATE TABLE depots (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    district VARCHAR(60) NOT NULL,
    location GEOGRAPHY(POINT, 4326),
    contact_number VARCHAR(15)
);

CREATE TABLE drivers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) UNIQUE NOT NULL,
    license_number VARCHAR(30) UNIQUE NOT NULL,
    depot_id INT REFERENCES depots(id),
    experience_years INT DEFAULT 0,
    rating NUMERIC(2,1) DEFAULT 5.0,
    is_on_duty BOOLEAN DEFAULT FALSE
);

CREATE TABLE conductors (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) UNIQUE NOT NULL,
    employee_code VARCHAR(30) UNIQUE NOT NULL,
    depot_id INT REFERENCES depots(id)
);

-- =========================================================
-- BUS / ROUTE / STOP
-- =========================================================

CREATE TABLE buses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    registration_number VARCHAR(20) UNIQUE NOT NULL,
    bus_type VARCHAR(20) NOT NULL, -- ORDINARY, EXPRESS, DELUXE, ULTRA_DELUXE
    total_seats INT NOT NULL,
    ladies_seats INT DEFAULT 0,
    senior_citizen_seats INT DEFAULT 0,
    depot_id INT REFERENCES depots(id),
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, MAINTENANCE, RETIRED
    last_service_date DATE,
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE routes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    route_number VARCHAR(20) UNIQUE NOT NULL,
    route_name VARCHAR(150) NOT NULL,
    source_name VARCHAR(100) NOT NULL,
    destination_name VARCHAR(100) NOT NULL,
    total_distance_km NUMERIC(6,2),
    estimated_duration_min INT,
    is_rural BOOLEAN DEFAULT TRUE,
    geom GEOGRAPHY(LINESTRING, 4326), -- route polyline
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE bus_stops (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    stop_name VARCHAR(120) NOT NULL,
    location GEOGRAPHY(POINT, 4326) NOT NULL,
    district VARCHAR(60),
    is_rural BOOLEAN DEFAULT TRUE
);

CREATE TABLE route_stops (
    id SERIAL PRIMARY KEY,
    route_id UUID REFERENCES routes(id) ON DELETE CASCADE,
    stop_id UUID REFERENCES bus_stops(id),
    sequence_order INT NOT NULL,
    distance_from_source_km NUMERIC(6,2),
    avg_travel_time_min INT, -- from source
    UNIQUE(route_id, sequence_order)
);

-- =========================================================
-- SCHEDULE / TRIP
-- =========================================================

CREATE TABLE schedules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    route_id UUID REFERENCES routes(id),
    bus_id UUID REFERENCES buses(id),
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    frequency_minutes INT, -- for high frequency routes; null if single trip
    days_of_week VARCHAR(20) DEFAULT 'MON,TUE,WED,THU,FRI,SAT,SUN',
    schedule_type VARCHAR(20) DEFAULT 'REGULAR', -- REGULAR, HOLIDAY, FESTIVAL
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE trips (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    schedule_id UUID REFERENCES schedules(id),
    bus_id UUID REFERENCES buses(id) NOT NULL,
    route_id UUID REFERENCES routes(id) NOT NULL,
    driver_id UUID REFERENCES drivers(id),
    conductor_id UUID REFERENCES conductors(id),
    trip_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'SCHEDULED', -- SCHEDULED, RUNNING, COMPLETED, CANCELLED, DELAYED
    actual_start_time TIMESTAMP,
    actual_end_time TIMESTAMP,
    current_location GEOGRAPHY(POINT, 4326),
    current_speed_kmph NUMERIC(5,2),
    heading_degrees NUMERIC(5,2),
    delay_minutes INT DEFAULT 0,
    break_status VARCHAR(20) DEFAULT 'NONE',       -- NONE, ON_BREAK
    fuel_status VARCHAR(20),                        -- FULL, HALF, LOW, CRITICAL
    bus_health_status VARCHAR(20) DEFAULT 'OK',     -- OK, MINOR_ISSUE, NEEDS_MAINTENANCE
    emergency_alert_active BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_trips_status ON trips(status);
CREATE INDEX idx_trips_current_location ON trips USING GIST(current_location);

-- =========================================================
-- GPS HISTORY (time-series, used for AI training)
-- =========================================================

CREATE TABLE gps_history (
    id BIGSERIAL PRIMARY KEY,
    trip_id UUID REFERENCES trips(id) ON DELETE CASCADE,
    location GEOGRAPHY(POINT, 4326) NOT NULL,
    speed_kmph NUMERIC(5,2),
    heading_degrees NUMERIC(5,2),
    recorded_at TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_gps_history_trip ON gps_history(trip_id);
CREATE INDEX idx_gps_history_location ON gps_history USING GIST(location);

-- =========================================================
-- SEATS / CROWD
-- =========================================================

CREATE TABLE seats (
    id SERIAL PRIMARY KEY,
    bus_id UUID REFERENCES buses(id) ON DELETE CASCADE,
    seat_number VARCHAR(10) NOT NULL,
    seat_type VARCHAR(20) DEFAULT 'GENERAL', -- GENERAL, LADIES, SENIOR_CITIZEN, RESERVED
    UNIQUE(bus_id, seat_number)
);

CREATE TABLE crowd_data (
    id BIGSERIAL PRIMARY KEY,
    trip_id UUID REFERENCES trips(id) ON DELETE CASCADE,
    occupied_seats INT DEFAULT 0,
    standing_count INT DEFAULT 0,
    crowd_level VARCHAR(10), -- LOW, MEDIUM, HIGH
    recorded_at TIMESTAMP DEFAULT now()
);

-- =========================================================
-- NOTIFICATIONS / ALERTS
-- =========================================================

CREATE TABLE route_alerts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    route_id UUID REFERENCES routes(id),
    alert_type VARCHAR(30) NOT NULL, -- ROAD_BLOCK, DIVERSION, CANCELLED, ACCIDENT, TRAFFIC
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(30), -- ARRIVAL, DELAY, CANCELLATION, ALERT, PROMO
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT now()
);

-- =========================================================
-- PASSENGER FEATURES
-- =========================================================

CREATE TABLE favorite_routes (
    id SERIAL PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    route_id UUID REFERENCES routes(id) ON DELETE CASCADE,
    UNIQUE(user_id, route_id)
);

CREATE TABLE favorite_stops (
    id SERIAL PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    stop_id UUID REFERENCES bus_stops(id) ON DELETE CASCADE,
    UNIQUE(user_id, stop_id)
);

CREATE TABLE complaints (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id),
    trip_id UUID REFERENCES trips(id),
    complaint_type VARCHAR(30) NOT NULL, -- DRIVER, CONDUCTOR, BUS, ROAD
    description TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'OPEN', -- OPEN, IN_PROGRESS, RESOLVED, REJECTED
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE lost_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id),
    trip_id UUID REFERENCES trips(id),
    item_description TEXT NOT NULL,
    item_type VARCHAR(20) DEFAULT 'LOST', -- LOST, FOUND
    contact_info VARCHAR(100),
    status VARCHAR(20) DEFAULT 'OPEN', -- OPEN, CLAIMED, CLOSED
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE feedback (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    trip_id UUID REFERENCES trips(id),
    bus_rating NUMERIC(2,1),
    driver_rating NUMERIC(2,1),
    comments TEXT,
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE sos_alerts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id),
    trip_id UUID REFERENCES trips(id),
    location GEOGRAPHY(POINT, 4326) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, RESOLVED
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE ticket_statistics (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    trip_id UUID REFERENCES trips(id) ON DELETE CASCADE UNIQUE,
    tickets_issued INT DEFAULT 0,
    total_revenue NUMERIC(10,2) DEFAULT 0,
    updated_at TIMESTAMP DEFAULT now()
);

-- =========================================================
-- SEED ROLES
-- =========================================================
INSERT INTO roles (name) VALUES
('PASSENGER'), ('DRIVER'), ('CONDUCTOR'), ('ADMIN'), ('DEPOT_MANAGER'), ('GOV_ADMIN')
ON CONFLICT DO NOTHING;
