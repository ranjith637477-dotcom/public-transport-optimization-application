import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../lib/api.js';

export default function Login() {
  const navigate = useNavigate();
  const [phone, setPhone] = useState('9000000000');
  const [otp, setOtp] = useState('');
  const [stage, setStage] = useState('phone'); // phone -> otp
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function requestOtp(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await api.post('/api/v1/auth/otp/request', { phoneNumber: phone });
      setStage('otp');
    } catch (err) {
      setError('Could not send OTP. Check the backend is running.');
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
      if (res.data.role !== 'ADMIN') {
        setError('This account is not an admin. Use the seeded admin phone number.');
        return;
      }
      localStorage.setItem('tnsmartbus_admin_token', res.data.token);
      navigate('/');
    } catch (err) {
      setError('Invalid or expired OTP.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-base">
      <div className="w-full max-w-sm">
        <div className="text-center mb-8">
          <div className="inline-flex items-center gap-2 mb-2">
            <span className="w-2 h-2 rounded-full bg-signal pulse-dot" />
            <span className="font-display font-semibold text-xl text-ink">TN SmartBus</span>
          </div>
          <p className="text-xs text-muted font-mono tracking-wider">FLEET CONTROL — ADMIN SIGN IN</p>
        </div>

        <div className="bg-panel border border-border rounded-lg p-6">
          {stage === 'phone' && (
            <form onSubmit={requestOtp} className="space-y-4">
              <div>
                <label className="text-xs uppercase tracking-wider text-muted font-mono">Phone number</label>
                <input
                  className="mt-1 w-full bg-panel2 border border-border rounded-md px-3 py-2 text-ink font-mono focus:outline-none focus:ring-2 focus:ring-signal/40"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                />
              </div>
              {error && <p className="text-sm text-alert">{error}</p>}
              <button
                disabled={loading}
                className="w-full bg-signal text-base font-semibold py-2 rounded-md hover:opacity-90 transition-opacity disabled:opacity-50"
              >
                {loading ? 'Sending...' : 'Send OTP'}
              </button>
              <p className="text-xs text-muted">
                Demo admin: 9000000000. OTP is printed in the backend console log.
              </p>
            </form>
          )}

          {stage === 'otp' && (
            <form onSubmit={verifyOtp} className="space-y-4">
              <div>
                <label className="text-xs uppercase tracking-wider text-muted font-mono">Enter OTP</label>
                <input
                  className="mt-1 w-full bg-panel2 border border-border rounded-md px-3 py-2 text-ink font-mono tracking-widest text-lg focus:outline-none focus:ring-2 focus:ring-signal/40"
                  value={otp}
                  onChange={(e) => setOtp(e.target.value)}
                  maxLength={6}
                  autoFocus
                />
              </div>
              {error && <p className="text-sm text-alert">{error}</p>}
              <button
                disabled={loading}
                className="w-full bg-signal text-base font-semibold py-2 rounded-md hover:opacity-90 transition-opacity disabled:opacity-50"
              >
                {loading ? 'Verifying...' : 'Verify & sign in'}
              </button>
              <button
                type="button"
                onClick={() => setStage('phone')}
                className="w-full text-xs text-muted hover:text-ink"
              >
                Use a different number
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}
