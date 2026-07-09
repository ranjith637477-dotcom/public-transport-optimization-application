import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import api from '../lib/api.js';
import { useTranslation } from '../lib/i18n.js';

const BUS_TYPES = ['ORDINARY', 'EXPRESS', 'DELUXE', 'ULTRA_DELUXE'];

export default function FareCalculator() {
  const lang = useSelector((s) => s.language.lang);
  const t = useTranslation(lang);
  const [distanceKm, setDistanceKm] = useState(10);
  const [busType, setBusType] = useState('ORDINARY');
  const [concessionType, setConcessionType] = useState('NONE');
  const [result, setResult] = useState(null);

  async function calculate(e) {
    e.preventDefault();
    const res = await api.post('/api/v1/fare/calculate', {
      distanceKm: Number(distanceKm),
      busType,
      concessionType,
    });
    setResult(res.data);
  }

  return (
    <div className="px-4 py-4 space-y-4 rise-in">
      <h1 className="font-display text-xl font-semibold text-ink">{t('fareCalculator')}</h1>

      <form onSubmit={calculate} className="glass rounded-2xl p-4 space-y-3">
        <div>
          <label className="text-xs uppercase tracking-wider text-muted font-mono">{t('distance')} (km)</label>
          <input
            type="number"
            value={distanceKm}
            onChange={(e) => setDistanceKm(e.target.value)}
            className="mt-1 w-full bg-surface2 border border-border rounded-lg px-3 py-2 text-ink focus:outline-none focus:ring-2 focus:ring-brand/40"
          />
        </div>
        <div>
          <label className="text-xs uppercase tracking-wider text-muted font-mono">{t('busType')}</label>
          <select
            value={busType}
            onChange={(e) => setBusType(e.target.value)}
            className="mt-1 w-full bg-surface2 border border-border rounded-lg px-3 py-2 text-ink focus:outline-none focus:ring-2 focus:ring-brand/40"
          >
            {BUS_TYPES.map((b) => <option key={b} value={b}>{b}</option>)}
          </select>
        </div>
        <div>
          <label className="text-xs uppercase tracking-wider text-muted font-mono">{t('concession')}</label>
          <select
            value={concessionType}
            onChange={(e) => setConcessionType(e.target.value)}
            className="mt-1 w-full bg-surface2 border border-border rounded-lg px-3 py-2 text-ink focus:outline-none focus:ring-2 focus:ring-brand/40"
          >
            <option value="NONE">{t('none')}</option>
            <option value="STUDENT">{t('student')}</option>
            <option value="SENIOR_CITIZEN">{t('seniorCitizen')}</option>
          </select>
        </div>
        <button className="w-full bg-brand text-base font-semibold py-2.5 rounded-lg hover:opacity-90 transition-opacity">
          {t('calculate')}
        </button>
      </form>

      {result && (
        <div className="glass rounded-2xl p-5 text-center">
          <p className="text-xs uppercase tracking-wider text-muted font-mono mb-1">{t('fare')}</p>
          <p className="text-3xl font-display font-semibold text-brand">₹{result.finalFare.toFixed(2)}</p>
          {result.concessionDiscount > 0 && (
            <p className="text-xs text-muted mt-1">
              Base ₹{result.baseFare.toFixed(2)} − ₹{result.concessionDiscount.toFixed(2)} concession
            </p>
          )}
        </div>
      )}
    </div>
  );
}
