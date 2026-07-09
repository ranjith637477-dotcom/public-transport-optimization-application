import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import api from '../lib/api.js';
import { useTranslation } from '../lib/i18n.js';

const TYPES = ['DRIVER', 'CONDUCTOR', 'BUS', 'ROAD'];

export default function Complaints() {
  const lang = useSelector((s) => s.language.lang);
  const t = useTranslation(lang);
  const token = useSelector((s) => s.auth.token);
  const [complaintType, setComplaintType] = useState('BUS');
  const [description, setDescription] = useState('');
  const [mine, setMine] = useState([]);
  const [submitted, setSubmitted] = useState(false);

  function load() {
    if (!token) return;
    api.get('/api/v1/complaints/mine').then((res) => setMine(res.data)).catch(() => {});
  }

  useEffect(load, [token]);

  async function submit(e) {
    e.preventDefault();
    await api.post('/api/v1/complaints', { complaintType, description });
    setDescription('');
    setSubmitted(true);
    load();
    setTimeout(() => setSubmitted(false), 2500);
  }

  if (!token) {
    return (
      <div className="px-4 py-8 text-center rise-in">
        <p className="text-sm text-muted mb-3">Sign in to file or track complaints.</p>
        <Link to="/login" className="text-sm font-semibold px-4 py-2 rounded-lg bg-brand text-base">{t('signIn')}</Link>
      </div>
    );
  }

  return (
    <div className="px-4 py-4 space-y-4 rise-in">
      <h1 className="font-display text-xl font-semibold text-ink">{t('fileComplaint')}</h1>

      <form onSubmit={submit} className="glass rounded-2xl p-4 space-y-3">
        <div>
          <label className="text-xs uppercase tracking-wider text-muted font-mono">{t('complaintType')}</label>
          <select
            value={complaintType}
            onChange={(e) => setComplaintType(e.target.value)}
            className="mt-1 w-full bg-surface2 border border-border rounded-lg px-3 py-2 text-ink focus:outline-none focus:ring-2 focus:ring-brand/40"
          >
            {TYPES.map((ty) => <option key={ty} value={ty}>{ty}</option>)}
          </select>
        </div>
        <div>
          <label className="text-xs uppercase tracking-wider text-muted font-mono">{t('description')}</label>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={3}
            required
            className="mt-1 w-full bg-surface2 border border-border rounded-lg px-3 py-2 text-ink focus:outline-none focus:ring-2 focus:ring-brand/40"
          />
        </div>
        <button className="w-full bg-brand text-base font-semibold py-2.5 rounded-lg hover:opacity-90 transition-opacity">
          {t('submit')}
        </button>
        {submitted && <p className="text-xs text-brand text-center">Complaint submitted.</p>}
      </form>

      <div className="glass rounded-2xl p-4">
        <h2 className="text-xs uppercase tracking-wider text-muted font-mono mb-2">Your complaints</h2>
        {mine.length === 0 && <p className="text-sm text-muted py-2">No complaints filed yet.</p>}
        {mine.map((c) => (
          <div key={c.id} className="py-2 border-b border-border last:border-0">
            <div className="flex items-center justify-between">
              <span className="text-xs font-mono text-muted uppercase">{c.complaintType}</span>
              <span className="text-xs font-mono text-saffron">{c.status}</span>
            </div>
            <p className="text-sm text-ink mt-1">{c.description}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
