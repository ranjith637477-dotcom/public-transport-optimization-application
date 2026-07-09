import React from 'react';
import { Link } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { useTranslation } from '../lib/i18n.js';

export default function More() {
  const lang = useSelector((s) => s.language.lang);
  const t = useTranslation(lang);
  const token = useSelector((s) => s.auth.token);
  const fullName = useSelector((s) => s.auth.fullName);

  const items = [
    { to: '/fare', label: t('fareCalculator'), icon: '₹' },
    { to: '/favorites', label: t('favorites'), icon: '★' },
    { to: '/notifications', label: 'Notifications', icon: '🔔' },
    { to: '/complaints', label: t('complaints'), icon: '⚠' },
    { to: '/lost-and-found', label: t('lostAndFound'), icon: '🎒' },
  ];

  return (
    <div className="px-4 py-4 space-y-4 rise-in">
      <div className="glass rounded-2xl p-4 flex items-center justify-between">
        <div>
          <p className="text-xs text-muted font-mono uppercase tracking-wider">{t('profile')}</p>
          <p className="text-ink font-display font-semibold">{token ? fullName || 'Passenger' : 'Not signed in'}</p>
        </div>
        {!token && (
          <Link to="/login" className="text-xs font-semibold px-3 py-1.5 rounded-md bg-brand text-base">
            {t('signIn')}
          </Link>
        )}
      </div>

      <div className="glass rounded-2xl divide-y divide-border overflow-hidden">
        {items.map((item) => (
          <Link key={item.to} to={item.to} className="flex items-center gap-3 px-4 py-3.5 hover:bg-surface2/60 transition-colors">
            <span className="text-lg w-6 text-center">{item.icon}</span>
            <span className="text-sm text-ink flex-1">{item.label}</span>
            <span className="text-muted">›</span>
          </Link>
        ))}
      </div>
    </div>
  );
}
