import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import api from '../lib/api.js';

export default function RouteDetail() {
  const { routeId } = useParams();
  const [detail, setDetail] = useState(null);

  useEffect(() => {
    api.get(`/api/v1/routes/${routeId}`).then((res) => setDetail(res.data)).catch(() => {});
  }, [routeId]);

  if (!detail) return <p className="text-sm text-muted text-center py-8">Loading route...</p>;

  const { route, stops, schedules, alerts } = detail;

  return (
    <div className="px-4 py-4 space-y-4 rise-in">
      <Link to="/search" className="text-xs text-muted">‹ Back to search</Link>

      <div className="glass rounded-2xl p-4">
        <span className="font-mono text-brand font-semibold">{route.routeNumber}</span>
        <h1 className="font-display text-lg font-semibold text-ink">{route.routeName}</h1>
        <p className="text-sm text-muted mt-1">{route.sourceName} → {route.destinationName}</p>
        <div className="flex gap-4 mt-2 text-xs text-muted font-mono">
          <span>{route.totalDistanceKm} km</span>
          <span>{route.estimatedDurationMin} min</span>
        </div>
      </div>

      {alerts?.length > 0 && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="glass rounded-2xl p-4 border-alert/30">
          <h2 className="text-xs uppercase tracking-wider text-alert font-mono mb-2">Active alerts</h2>
          {alerts.map((a) => (
            <div key={a.id} className="text-sm text-ink py-1">
              <span className="text-alert font-mono text-xs mr-2">{a.alertType}</span>
              {a.description}
            </div>
          ))}
        </motion.div>
      )}

      <div className="glass rounded-2xl p-4">
        <h2 className="text-xs uppercase tracking-wider text-muted font-mono mb-3">Stops</h2>
        <div className="relative pl-4 space-y-4 border-l border-border">
          {stops?.map((s) => (
            <div key={s.sequenceOrder} className="relative">
              <span className="absolute -left-[21px] top-1 w-2.5 h-2.5 rounded-full bg-brand" />
              <p className="text-sm text-ink">{s.stopName}</p>
              <p className="text-xs text-muted font-mono">
                {s.distanceFromSourceKm} km · {s.avgTravelTimeMin} min from source
              </p>
            </div>
          ))}
        </div>
      </div>

      {schedules?.length > 0 && (
        <div className="glass rounded-2xl p-4">
          <h2 className="text-xs uppercase tracking-wider text-muted font-mono mb-2">Schedule</h2>
          {schedules.map((sch) => (
            <div key={sch.id} className="flex items-center justify-between text-sm py-1.5 border-b border-border last:border-0">
              <span className="font-mono text-ink">{sch.departureTime} → {sch.arrivalTime}</span>
              <span className="text-xs text-muted">{sch.scheduleType}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
