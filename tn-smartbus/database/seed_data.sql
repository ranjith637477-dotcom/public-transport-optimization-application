-- =========================================================
-- Sample seed data: rural TNSTC routes around Chennai region
-- Run after schema.sql
-- =========================================================

INSERT INTO depots (name, district, location, contact_number) VALUES
('Tambaram Depot', 'Chengalpattu', ST_GeogFromText('POINT(80.1000 12.9250)'), '9840000001'),
('Kanchipuram Depot', 'Kanchipuram', ST_GeogFromText('POINT(79.7036 12.8342)'), '9840000002')
ON CONFLICT DO NOTHING;

INSERT INTO buses (registration_number, bus_type, total_seats, ladies_seats, senior_citizen_seats, depot_id, status)
VALUES
('TN01AB1234', 'ORDINARY', 52, 6, 4, 1, 'ACTIVE'),
('TN01AB5678', 'EXPRESS', 45, 4, 4, 1, 'ACTIVE'),
('TN09CD4321', 'ORDINARY', 52, 6, 4, 2, 'ACTIVE')
ON CONFLICT DO NOTHING;

INSERT INTO routes (route_number, route_name, source_name, destination_name, total_distance_km, estimated_duration_min, is_rural, geom)
VALUES
('TN-101', 'Tambaram - Kanchipuram via Walajabad', 'Tambaram', 'Kanchipuram', 62.5, 105, true,
    ST_GeogFromText('LINESTRING(80.1000 12.9250, 79.9500 12.8900, 79.8200 12.8600, 79.7036 12.8342)')),
('TN-102', 'Tambaram - Chengalpattu Rural Loop', 'Tambaram', 'Chengalpattu', 38.0, 70, true,
    ST_GeogFromText('LINESTRING(80.1000 12.9250, 80.0300 12.8000, 79.9800 12.6900)'))
ON CONFLICT DO NOTHING;

INSERT INTO bus_stops (stop_name, location, district, is_rural) VALUES
('Tambaram Bus Stand', ST_GeogFromText('POINT(80.1000 12.9250)'), 'Chengalpattu', false),
('Walajabad', ST_GeogFromText('POINT(79.8200 12.8600)'), 'Kanchipuram', true),
('Kanchipuram Bus Stand', ST_GeogFromText('POINT(79.7036 12.8342)'), 'Kanchipuram', false),
('Chengalpattu Bus Stand', ST_GeogFromText('POINT(79.9800 12.6900)'), 'Chengalpattu', false)
ON CONFLICT DO NOTHING;

-- Link stops to route TN-101 in sequence
INSERT INTO route_stops (route_id, stop_id, sequence_order, distance_from_source_km, avg_travel_time_min)
SELECT r.id, s.id, seq, dist, mins FROM
(VALUES
  ('TN-101', 'Tambaram Bus Stand', 1, 0, 0),
  ('TN-101', 'Walajabad', 2, 35.0, 55),
  ('TN-101', 'Kanchipuram Bus Stand', 3, 62.5, 105)
) AS t(route_number, stop_name, seq, dist, mins)
JOIN routes r ON r.route_number = t.route_number
JOIN bus_stops s ON s.stop_name = t.stop_name;

-- A schedule and a currently RUNNING trip so the tracking module has live data on first boot
INSERT INTO schedules (route_id, bus_id, departure_time, arrival_time, frequency_minutes, schedule_type)
SELECT r.id, b.id, '07:00:00', '08:45:00', 60, 'REGULAR'
FROM routes r, buses b
WHERE r.route_number = 'TN-101' AND b.registration_number = 'TN01AB1234';

INSERT INTO trips (bus_id, route_id, trip_date, status, actual_start_time, current_location, current_speed_kmph, heading_degrees, delay_minutes)
SELECT b.id, r.id, CURRENT_DATE, 'RUNNING', now(),
       ST_GeogFromText('POINT(80.0000 12.9000)'), 42.0, 210.0, 3
FROM routes r, buses b
WHERE r.route_number = 'TN-101' AND b.registration_number = 'TN01AB1234';

INSERT INTO trips (bus_id, route_id, trip_date, status, actual_start_time, current_location, current_speed_kmph, heading_degrees, delay_minutes)
SELECT b.id, r.id, CURRENT_DATE, 'RUNNING', now(),
       ST_GeogFromText('POINT(80.0300 12.7800)'), 35.0, 190.0, 0
FROM routes r, buses b
WHERE r.route_number = 'TN-102' AND b.registration_number = 'TN01AB5678';
-- =========================================================
-- Phase 3 seed data: sample driver + conductor assigned to the
-- demo trips created in seed_data.sql, so the driver/conductor
-- endpoints are testable immediately.
-- Run after schema.sql, seed_data.sql, and migration_phase3.sql
-- =========================================================

-- Driver user + driver profile
INSERT INTO users (full_name, phone_number, role_id)
SELECT 'Murugan S', '9800000001', r.id FROM roles r WHERE r.name = 'DRIVER'
ON CONFLICT DO NOTHING;

INSERT INTO drivers (user_id, license_number, depot_id, experience_years)
SELECT u.id, 'TN-DL-000123', d.id, 8
FROM users u, depots d
WHERE u.phone_number = '9800000001' AND d.name = 'Tambaram Depot'
ON CONFLICT DO NOTHING;

-- Conductor user + conductor profile
INSERT INTO users (full_name, phone_number, role_id)
SELECT 'Kannan R', '9800000002', r.id FROM roles r WHERE r.name = 'CONDUCTOR'
ON CONFLICT DO NOTHING;

INSERT INTO conductors (user_id, employee_code, depot_id)
SELECT u.id, 'TNSTC-C-4521', d.id
FROM users u, depots d
WHERE u.phone_number = '9800000002' AND d.name = 'Tambaram Depot'
ON CONFLICT DO NOTHING;

-- Assign this driver + conductor to the TN-101 demo trip
UPDATE trips t
SET driver_id = (SELECT id FROM drivers WHERE license_number = 'TN-DL-000123'),
    conductor_id = (SELECT id FROM conductors WHERE employee_code = 'TNSTC-C-4521')
FROM routes r
WHERE t.route_id = r.id AND r.route_number = 'TN-101';

-- =========================================================
-- Admin login seed (for the admin dashboard)
-- =========================================================
INSERT INTO users (full_name, phone_number, role_id)
SELECT 'TNSTC Admin', '9000000000', r.id FROM roles r WHERE r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- Admin login uses the OTP flow (see README) - no password hash needed.
-- POST /api/v1/auth/otp/request {"phoneNumber":"9000000000"}, then
-- /api/v1/auth/otp/verify with the OTP printed in the backend console.
