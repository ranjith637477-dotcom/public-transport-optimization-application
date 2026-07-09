import React, { useEffect, useState } from 'react';
import api from '../lib/api.js';

const STATUS_FLOW = ['OPEN', 'IN_PROGRESS', 'RESOLVED', 'REJECTED'];

export default function ComplaintsPage() {
  const [complaints, setComplaints] = useState([]);
  const [filter, setFilter] = useState('OPEN');

  async function load() {
    const res = await api.get('/api/v1/admin/complaints', { params: filter ? { status: filter } : {} });
    setComplaints(res.data);
  }

  useEffect(() => { load(); }, [filter]);

  async function setStatus(id, status) {
    await api.put(`/api/v1/admin/complaints/${id}/status`, { status });
    load();
  }

  const statusColor = {
    OPEN: 'text-alert', IN_PROGRESS: 'text-amber', RESOLVED: 'text-signal', REJECTED: 'text-muted',
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="font-display text-2xl font-semibold text-ink">Complaints</h1>
          <p className="text-sm text-muted mt-1">{complaints.length} in this view.</p>
        </div>
        <div className="flex gap-2">
          {['', ...STATUS_FLOW].map((s) => (
            <button
              key={s || 'ALL'}
              onClick={() => setFilter(s)}
              className={`text-xs font-mono px-3 py-1.5 rounded-md border ${
                filter === s ? 'bg-panel2 border-signal/40 text-ink' : 'border-border text-muted hover:text-ink'
              }`}
            >
              {s || 'ALL'}
            </button>
          ))}
        </div>
      </div>

      <div className="bg-panel border border-border rounded-lg divide-y divide-border">
        {complaints.map((c) => (
          <div key={c.id} className="px-5 py-4 flex items-start justify-between gap-4">
            <div className="min-w-0">
              <div className="flex items-center gap-2 mb-1">
                <span className="text-xs font-mono uppercase text-muted">{c.complaintType}</span>
                <span className={`text-xs font-mono ${statusColor[c.status]}`}>{c.status}</span>
              </div>
              <p className="text-sm text-ink">{c.description}</p>
              <p className="text-xs text-muted mt-1">{new Date(c.createdAt).toLocaleString()}</p>
            </div>
            <select
              value={c.status}
              onChange={(e) => setStatus(c.id, e.target.value)}
              className="bg-panel2 border border-border rounded-md px-2 py-1 text-xs font-mono text-ink shrink-0"
            >
              {STATUS_FLOW.map((s) => <option key={s} value={s}>{s}</option>)}
            </select>
          </div>
        ))}
        {complaints.length === 0 && (
          <p className="px-5 py-8 text-center text-muted text-sm">No complaints in this view.</p>
        )}
      </div>
    </div>
  );
}
