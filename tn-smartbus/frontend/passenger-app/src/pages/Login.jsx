import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import api from '../lib/api.js';
import { setCredentials } from '../store/authSlice.js';
import { useTranslation } from '../lib/i18n.js';

export default function Login() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const lang = useSelector((s) => s.language.lang);
  const t = useTranslation(lang);

  const [phone, setPhone] = useState('');
  const [otp, setOtp] = useState('');
  const [stage, setStage] = useState('phone');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function requestOtp(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await api.post('/api/v1/auth/otp/request', { phoneNumber: phone });
      setStage('otp');
    } catch {
      setError('Could not send OTP.');
    } finally {
      setLoading(false);
    }
  }

  async function verifyOtp(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await api.post('/api/v1/auth/otp/verify', { phoneNumber: phone, otpCode: otp });
      dispatch(setCredentials({
        token: res.data.token, userId: res.data.userId,
        fullName: res.data.fullName, role: res.data.role,
      }));
      navigate('/more');
    } catch {
      setError('Invalid or expired OTP.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="px-4 py-8 rise-in">
      <div className="text-center mb-6">
        <p className="font-display text-xl font-semibold text-ink">{t('signIn')}</p>
        <p className="text-sm text-muted mt-1">{t('tagline')}</p>
      </div>

      <div className="glass rounded-2xl p-5">
        {stage === 'phone' ? (
          <form onSubmit={requestOtp} className="space-y-4">
            <div>
              <label className="text-xs uppercase tracking-wider text-muted font-mono">{t('phoneNumber')}</label>
              <input
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                placeholder="98XXXXXXXX"
                className="mt-1 w-full bg-surface2 border border-border rounded-lg px-3 py-2 text-ink font-mono focus:outline-none focus:ring-2 focus:ring-brand/40"
              />
            </div>
            {error && <p className="text-sm text-alert">{error}</p>}
            <button disabled={loading} className="w-full bg-brand text-base font-semibold py-2.5 rounded-lg hover:opacity-90 disabled:opacity-50">
              {loading ? '...' : t('sendOtp')}
            </button>
          </form>
        ) : (
          <form onSubmit={verifyOtp} className="space-y-4">
            <div>
              <label className="text-xs uppercase tracking-wider text-muted font-mono">{t('enterOtp')}</label>
              <input
                autoFocus
                value={otp}
                onChange={(e) => setOtp(e.target.value)}
                maxLength={6}
                className="mt-1 w-full bg-surface2 border border-border rounded-lg px-3 py-2 text-ink font-mono tracking-widest text-lg focus:outline-none focus:ring-2 focus:ring-brand/40"
              />
            </div>
            {error && <p className="text-sm text-alert">{error}</p>}
            <button disabled={loading} className="w-full bg-brand text-base font-semibold py-2.5 rounded-lg hover:opacity-90 disabled:opacity-50">
              {loading ? '...' : t('verify')}
            </button>
          </form>
        )}
      </div>
    </div>
  );
}
