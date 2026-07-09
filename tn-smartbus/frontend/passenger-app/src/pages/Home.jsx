import React, { useEffect, useRef, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet';
import L from 'leaflet';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import api, { BACKEND_URL } from '../lib/api.js';
import { useTranslation } from '../lib/i18n.js';

// Default view centered on the Tambaram-Kanchipuram rural corridor (matches seed data)
const DEFAULT_CENTER = [12.90, 79.95];

function busDivIcon(heading) {
  return L.divIcon({
    className: '',
    html: `<div style="transform: rotate(${heading || 0}deg); font-size: 22px; filter: drop-shadow(0 2px 3px rgba(0,0,0,0.5));">🚌</div>`,
    iconSize: [24, 24],
    iconAnchor: [12, 12],
  });
}

function stopDivIcon() {
  return L.divIcon({
    className: '',
    html: `<div style="width:10px;height:10px;border-radius:50%;background:#F2A65A;border:2px solid #0E1420;"></div>`,
    iconSize: [10, 10],
    iconAnchor: [5, 5],
  });
}

function RecenterOnLocate({ position }) {
  const map = useMap();
  useEffect(() => {
    if (position) map.setView(position, 13);
  }, [position]);
  return null;
}

export default function Home() {
  const lang = useSelector((s) => s.language.lang);
  const t = useTranslation(lang);
  const [buses, setBuses] = useState([]);
  const [nearbyStops, setNearbyStops] = useState([]);
  const [userPosition, setUserPosition] = useState(null);
  const stompRef = useRef(null);

  // Initial REST fetch, then live updates over WebSocket - same pattern as the
  // Phase 1 Leaflet demo, now inside the real React app.
  useEffect(() => {
    api.get('/api/v1/tracking/live').then((res) => setBuses(res.data)).catch(() => {});

    const socket = new SockJS(`${BACKEND_URL}/ws`);
    const client = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        client.subscribe('/topic/buses/live', (message) => {
          setBuses(JSON.parse(message.body));
        });
      },
      reconnectDelay: 4000,
    });
    client.activate();
    stompRef.current = client;
    return () => client.deactivate();
  }, []);

  useEffect(() => {
    if (!navigator.geolocation) return;
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        const coords = [pos.coords.latitude, pos.coords.longitude];
        setUserPosition(coords);
        api.get('/api/v1/stops/nearby', { params: { lat: coords[0], lng: coords[1], radiusMeters: 2000 } })
          .then((res) => setNearbyStops(res.data))
          .catch(() => {});
      },
      () => {
        // Location denied/unavailable - fall back to nearby search near the default corridor
        api.get('/api/v1/stops/nearby', { params: { lat: DEFAULT_CENTER[0], lng: DEFAULT_CENTER[1], radiusMeters: 5000 } })
          .then((res) => setNearbyStops(res.data))
          .catch(() => {});
      }
    );
  }, []);

  return (
    <div className="rise-in">
      <div className="h-[45vh] relative">
        <MapContainer center={DEFAULT_CENTER} zoom={11} scrollWheelZoom={true} className="h-full w-full">
          <TileLayer
            attribution='&copy; OpenStreetMap contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          <RecenterOnLocate position={userPosition} />
          {buses.map((bus) => (
            <Marker key={bus.tripId} position={[bus.latitude, bus.longitude]} icon={busDivIcon(bus.headingDegrees)}>
              <Popup>
                <div className="text-sm">
                  <b>{bus.busRegistrationNumber}</b> ({bus.busType})<br />
                  Route {bus.routeNumber} - {bus.routeName}<br />
                  {bus.speedKmph.toFixed(0)} km/h · {bus.crowdLevel}<br />
                  {bus.etaMinutesToNextStop ? `ETA ${bus.etaMinutesToNextStop.toFixed(1)} min` : ''}
                </div>
              </Popup>
            </Marker>
          ))}
          {nearbyStops.map((stop) => (
            <Marker key={stop.id} position={[stop.latitude, stop.longitude]} icon={stopDivIcon()}>
              <Popup>{stop.stopName}</Popup>
            </Marker>
          ))}
        </MapContainer>

        <div className="absolute top-3 left-3 right-3">
          <Link to="/search" className="glass rounded-full px-4 py-3 flex items-center gap-2 text-sm text-muted shadow-lg">
            🔍 {t('searchPlaceholder')}
          </Link>
        </div>
      </div>

      <div className="px-4 -mt-4 relative z-10 space-y-4">
        <motion.div
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          className="glass rounded-2xl p-4"
        >
          <div className="flex items-center justify-between mb-3">
            <h2 className="font-display font-semibold text-ink">{t('liveBuses')}</h2>
            <span className="text-xs font-mono text-muted">{buses.length} on road</span>
          </div>
          <div className="space-y-2 max-h-48 overflow-auto">
            {buses.length === 0 && (
              <p className="text-sm text-muted py-4 text-center">No buses running right now.</p>
            )}
            {buses.map((bus) => (
              <div key={bus.tripId} className="flex items-center justify-between text-sm py-1.5">
                <span className="font-mono text-ink">{bus.busRegistrationNumber}</span>
                <span className="text-muted text-xs truncate mx-2 flex-1">{bus.routeNumber}</span>
                <CrowdDot level={bus.crowdLevel} />
              </div>
            ))}
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.08 }}
          className="glass rounded-2xl p-4"
        >
          <h2 className="font-display font-semibold text-ink mb-3">{t('nearbyStops')}</h2>
          <div className="space-y-2">
            {nearbyStops.length === 0 && (
              <p className="text-sm text-muted py-4 text-center">Enable location to see nearby stops.</p>
            )}
            {nearbyStops.slice(0, 5).map((stop) => (
              <div key={stop.id} className="flex items-center gap-2 text-sm py-1">
                <span className="w-1.5 h-1.5 rounded-full bg-saffron shrink-0" />
                <span className="text-ink">{stop.stopName}</span>
              </div>
            ))}
          </div>
        </motion.div>
      </div>
    </div>
  );
}

function CrowdDot({ level }) {
  const color = level === 'HIGH' ? 'bg-alert' : level === 'MEDIUM' ? 'bg-saffron' : 'bg-brand';
  return <span className={`w-2 h-2 rounded-full ${color} shrink-0`} />;
}
