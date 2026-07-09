import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import api from '../lib/api.js';
import { useTranslation } from '../lib/i18n.js';

export default function Notifications() {
  const lang = useSelector((s) => s.language.lang);
  const t = useTranslation(lang);
  const token = useSelector((s) => s.auth.token);
  const [items, setItems] = useState([]);

  function load() {
    if (!token) return;
    api.get('/api/v1/notifications').then((res) => setItems(res.data)).catch(() => {});
  }

  useEffect(load, [token]);

  async function markRead(id) {
    await api.put(`/api/v1/notifications/${id}/read`);
    load();
  }

  if (!token) {
    return (
      <div className="px-4 py-8 text-center rise-in">
        <p className="text-sm text-muted mb-3">Sign in to see your notifications.</p>
        <Link to="/login" className="text-sm font-semibold px-4 py-2 rounded-lg bg-brand text-base">{t('signIn')}</Link>
      </div>
    );
  }

  const typeColor = {
    ARRIVAL: 'text-brand', DELAY: 'text-saffron', CANCELLATION: 'text-alert',
    ALERT: 'text-alert', PROMO: 'text-muted',
  };

  return (
    <div className="px-4 py-4 space-y-3 rise-in">
      <h1 className="font-display text-xl font-semibold text-ink">Notifications</h1>
      <div className="glass rounded-2xl divide-y divide-border">
        {items.length === 0 && <p className="text-sm text-muted p-6 text-center">No notifications yet.</p>}
        {items.map((n) => (
          <button
            key={n.id}
            onClick={() => !n.isRead && markRead(n.id)}
            className={`w-full text-left p-4 ${n.isRead ? 'opacity-60' : ''}`}
          >
            <div className="flex items-center justify-between mb-1">
              <span className={`text-xs font-mono uppercase ${typeColor[n.notificationType] || 'text-muted'}`}>
                {n.notificationType}
              </span>
              {!n.isRead && <span className="w-1.5 h-1.5 rounded-full bg-brand" />}
            </div>
            <p className="text-sm text-ink font-medium">{n.title}</p>
            <p className="text-xs text-muted mt-0.5">{n.message}</p>
          </button>
        ))}
      </div>
    </div>
  );
}
