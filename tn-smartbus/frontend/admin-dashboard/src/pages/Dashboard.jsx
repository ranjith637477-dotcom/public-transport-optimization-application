import React, { useEffect, useState } from 'react';
import api, { BACKEND_URL } from '../lib/api.js';
import StatCard from '../components/StatCard.jsx';

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  const [liveBuses, setLiveBuses] = useState([]);
  const [error, setError] = useState('');

  async function loadStats() {
    try {
      const res = await api.get('/api/v1/admin/analytics/dashboard');
      setStats(res.data);
    } catch (err) {
      setError('Could not reach the backend at ' + BACKEND_URL);
    }
  }

  async function loadLiveBuses() {
    try {
      const res = await api.get('/api/v1/tracking/live');
      setLiveBuses(res.data);
    } catch (err) {
      // tracking is public; a failure here means the backend itself is down
    }
  }

  useEffect(() => {
    loadStats();
    loadLiveBuses();
    const t = setInterval(() => {
      loadStats();
      loadLiveBuses();
    }, 5000);
    return () => clearInterval(t);
  }, []);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-display text-2xl font-semibold text-ink">Overview</h1>
        <p className="text-sm text-muted mt-1">Fleet status, refreshed every 5 seconds.</p>
      </div>

      {error && (
        <div className="bg-alert/10 border border-alert/30 text-alert text-sm rounded-md px-4 py-3">
          {error}
        </div>
      )}

      {stats && (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
          <StatCard label="Total buses" value={stats.totalBuses} />
          <StatCard label="Running" value={stats.runningBuses} accent="signal" />
          <StatCard label="Delayed" value={stats.delayedBuses} accent="amber" />
          <StatCard label="Completed today" value={stats.completedTripsToday} />
          <StatCard label="Open complaints" value={stats.openComplaints} accent={stats.openComplaints > 0 ? 'alert' : 'ink'} />
          <StatCard label="Avg delay" value={stats.averageDelayMinutes.toFixed(1)} suffix="min" />
        </div>
      )}

      <div className="bg-panel border border-border rounded-lg">
        <div className="px-5 py-4 border-b border-border flex items-center justify-between">
          <h2 className="font-display font-semibold text-ink">Live fleet</h2>
          <span className="text-xs text-muted font-mono">{liveBuses.length} buses on road</span>
        </div>
        <div className="divide-y divide-border">
          {liveBuses.length === 0 && (
            <p className="px-5 py-8 text-sm text-muted text-center">
              No buses currently running. Start the GPS simulator or a driver trip to see live data here.
            </p>
          )}
          {liveBuses.map((bus) => (
            <div key={bus.tripId} className="px-5 py-3 flex items-center justify-between text-sm">
              <div className="flex items-center gap-4 min-w-0">
                <span className="font-mono text-ink w-24 shrink-0">{bus.busRegistrationNumber}</span>
                <span className="text-muted truncate">Route {bus.routeNumber} · {bus.routeName}</span>
              </div>
              <div className="flex items-center gap-4 shrink-0">
                <span className="font-mono text-muted">{bus.speedKmph.toFixed(0)} km/h</span>
                <CrowdBadge level={bus.crowdLevel} />
                {bus.delayMinutes > 5 ? (
                  <span className="text-amber text-xs font-mono">+{bus.delayMinutes}m</span>
                ) : (
                  <span className="text-signal text-xs font-mono">on time</span>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

function CrowdBadge({ level }) {
  const map = {
    LOW: { color: 'text-signal', label: 'Low' },
    MEDIUM: { color: 'text-amber', label: 'Medium' },
    HIGH: { color: 'text-alert', label: 'High' },
  };
  const cfg = map[level] || map.LOW;
  return <span className={`text-xs font-mono ${cfg.color}`}>{cfg.label}</span>;
}
