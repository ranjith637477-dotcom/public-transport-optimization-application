import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import api from '../lib/api.js';
import { useTranslation } from '../lib/i18n.js';

export default function Sos() {
  const lang = useSelector((s) => s.language.lang);
  const t = useTranslation(lang);
  const token = useSelector((s) => s.auth.token);
  const [status, setStatus] = useState('idle'); // idle -> sending -> sent -> error

  async function raiseSos() {
    if (!navigator.geolocation) {
      setStatus('error');
      return;
    }
    setStatus('sending');
    navigator.geolocation.getCurrentPosition(
      async (pos) => {
        try {
          await api.post('/api/v1/sos', {
            latitude: pos.coords.latitude,
            longitude: pos.coords.longitude,
          });
          setStatus('sent');
        } catch {
          setStatus('error');
        }
      },
      () => setStatus('error')
    );
  }

  if (!token) {
    return (
      <div className="px-4 py-8 text-center rise-in">
        <p className="text-sm text-muted mb-3">Sign in to use the emergency SOS feature.</p>
        <Link to="/login" className="text-sm font-semibold px-4 py-2 rounded-lg bg-brand text-base">{t('signIn')}</Link>
      </div>
    );
  }

  return (
    <div className="px-4 py-10 text-center rise-in space-y-6">
      <div>
        <p className="font-display text-xl font-semibold text-alert">{t('emergency')}</p>
        <p className="text-sm text-muted mt-2 max-w-xs mx-auto">{t('emergencyConfirm')}</p>
      </div>

      <button
        onClick={raiseSos}
        disabled={status === 'sending' || status === 'sent'}
        className={`w-40 h-40 rounded-full mx-auto flex items-center justify-center text-lg font-display font-semibold transition-all
          ${status === 'sent' ? 'bg-brand text-base' : 'bg-alert text-white hover:opacity-90 active:scale-95'}
          disabled:opacity-80`}
      >
        {status === 'idle' && 'SOS'}
        {status === 'sending' && '...'}
        {status === 'sent' && 'Sent ✓'}
        {status === 'error' && 'Retry'}
      </button>

      {status === 'sent' && (
        <p className="text-sm text-brand">Your location has been shared. Help is on the way.</p>
      )}
      {status === 'error' && (
        <p className="text-sm text-alert">Could not share location. Check location permissions and try again.</p>
      )}
    </div>
  );
}
