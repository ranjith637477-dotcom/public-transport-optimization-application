import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { motion } from 'framer-motion';
import api from '../lib/api.js';
import { useTranslation } from '../lib/i18n.js';

export default function JourneyPlanner() {
  const lang = useSelector((s) => s.language.lang);
  const t = useTranslation(lang);
  const [source, setSource] = useState('');
  const [destination, setDestination] = useState('');
  const [options, setOptions] = useState(null);
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);

  async function plan(e) {
    e.preventDefault();
    setLoading(true);
    setSearched(true);
    try {
      const res = await api.post('/api/v1/journey/plan', {
        sourceStopName: source,
        destinationStopName: destination,
      });
      setOptions(res.data);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="px-4 py-4 space-y-4 rise-in">
      <h1 className="font-display text-xl font-semibold text-ink">{t('journeyPlanner')}</h1>

      <form onSubmit={plan} className="glass rounded-2xl p-4 space-y-3">
        <div>
          <label className="text-xs uppercase tracking-wider text-muted font-mono">{t('from')}</label>
          <input
            value={source}
            onChange={(e) => setSource(e.target.value)}
            placeholder="e.g. Tambaram Bus Stand"
            className="mt-1 w-full bg-surface2 border border-border rounded-lg px-3 py-2 text-ink focus:outline-none focus:ring-2 focus:ring-brand/40"
          />
        </div>
        <div>
          <label className="text-xs uppercase tracking-wider text-muted font-mono">{t('to')}</label>
          <input
            value={destination}
            onChange={(e) => setDestination(e.target.value)}
            placeholder="e.g. Kanchipuram Bus Stand"
            className="mt-1 w-full bg-surface2 border border-border rounded-lg px-3 py-2 text-ink focus:outline-none focus:ring-2 focus:ring-brand/40"
          />
        </div>
        <button className="w-full bg-brand text-base font-semibold py-2.5 rounded-lg hover:opacity-90 transition-opacity">
          {t('findRoutes')}
        </button>
      </form>

      {loading && <p className="text-sm text-muted text-center py-4">Finding routes...</p>}

      {searched && !loading && (
        <div className="space-y-3">
          {options?.length === 0 && (
            <p className="text-sm text-muted text-center py-6">No direct route found between these stops.</p>
          )}
          {options?.map((opt, i) => (
            <motion.div
              key={opt.routeNumber}
              initial={{ opacity: 0, y: 8 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.05 }}
              className="glass rounded-2xl p-4"
            >
              <div className="flex items-center justify-between mb-1">
                <span className="font-mono text-brand font-semibold">{opt.routeNumber}</span>
                <span className="text-xs text-muted">{opt.directRoute ? 'Direct' : 'Transfer'}</span>
              </div>
              <p className="text-sm text-ink">{opt.routeName}</p>
              <div className="flex gap-4 mt-2 text-xs text-muted font-mono">
                <span>{opt.distanceKm.toFixed(1)} km</span>
                <span>{opt.estimatedDurationMin} min</span>
                <span>₹{opt.estimatedFare.toFixed(2)}</span>
              </div>
            </motion.div>
          ))}
        </div>
      )}
    </div>
  );
}
