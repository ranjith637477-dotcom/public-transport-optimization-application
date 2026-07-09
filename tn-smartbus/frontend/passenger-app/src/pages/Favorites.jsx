import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import api from '../lib/api.js';
import { useTranslation } from '../lib/i18n.js';

export default function Favorites() {
  const lang = useSelector((s) => s.language.lang);
  const t = useTranslation(lang);
  const token = useSelector((s) => s.auth.token);
  const [routes, setRoutes] = useState([]);
  const [stops, setStops] = useState([]);

  useEffect(() => {
    if (!token) return;
    api.get('/api/v1/favorites/routes').then((res) => setRoutes(res.data)).catch(() => {});
    api.get('/api/v1/favorites/stops').then((res) => setStops(res.data)).catch(() => {});
  }, [token]);

  if (!token) {
    return (
      <div className="px-4 py-8 text-center rise-in">
        <p className="text-sm text-muted mb-3">Sign in to save favorite routes and stops.</p>
        <Link to="/login" className="text-sm font-semibold px-4 py-2 rounded-lg bg-brand text-base">{t('signIn')}</Link>
      </div>
    );
  }

  return (
    <div className="px-4 py-4 space-y-4 rise-in">
      <h1 className="font-display text-xl font-semibold text-ink">{t('favorites')}</h1>

      <div className="glass rounded-2xl p-4">
        <h2 className="text-xs uppercase tracking-wider text-muted font-mono mb-2">Routes</h2>
        {routes.length === 0 && <p className="text-sm text-muted py-2">No favorite routes yet.</p>}
        {routes.map((f) => (
          <div key={f.id} className="py-2 text-sm text-ink border-b border-border last:border-0">
            {f.route?.routeNumber} — {f.route?.routeName}
          </div>
        ))}
      </div>

      <div className="glass rounded-2xl p-4">
        <h2 className="text-xs uppercase tracking-wider text-muted font-mono mb-2">Stops</h2>
        {stops.length === 0 && <p className="text-sm text-muted py-2">No favorite stops yet.</p>}
        {stops.map((f) => (
          <div key={f.id} className="py-2 text-sm text-ink border-b border-border last:border-0">
            {f.stop?.stopName}
          </div>
        ))}
      </div>
    </div>
  );
}
