import React from 'react';

export default function StatCard({ label, value, accent = 'ink', suffix = '' }) {
  const accentClass = {
    ink: 'text-ink',
    signal: 'text-signal',
    amber: 'text-amber',
    alert: 'text-alert',
  }[accent];

  return (
    <div className="bg-panel border border-border rounded-lg p-5">
      <p className="text-xs uppercase tracking-wider text-muted font-mono mb-2">{label}</p>
      <p key={value} className={`flap-in text-3xl font-display font-semibold ${accentClass}`}>
        {value}
        {suffix && <span className="text-base text-muted ml-1">{suffix}</span>}
      </p>
    </div>
  );
}
