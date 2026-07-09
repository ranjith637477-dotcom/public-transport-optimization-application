import React, { useEffect, useState } from 'react';
import api from '../lib/api.js';

const EMPTY_FORM = {
  routeNumber: '', routeName: '', sourceName: '', destinationName: '',
  totalDistanceKm: '', estimatedDurationMin: '', isRural: true,
};

export default function RoutesPage() {
  const [routes, setRoutes] = useState([]);
  const [form, setForm] = useState(EMPTY_FORM);
  const [editingId, setEditingId] = useState(null);
  const [showForm, setShowForm] = useState(false);

  async function load() {
    const res = await api.get('/api/v1/admin/routes');
    setRoutes(res.data);
  }

  useEffect(() => { load(); }, []);

  function startEdit(route) {
    setEditingId(route.id);
    setForm({
      routeNumber: route.routeNumber, routeName: route.routeName,
      sourceName: route.sourceName, destinationName: route.destinationName,
      totalDistanceKm: route.totalDistanceKm ?? '', estimatedDurationMin: route.estimatedDurationMin ?? '',
      isRural: route.isRural,
    });
    setShowForm(true);
  }

  function startCreate() {
    setEditingId(null);
    setForm(EMPTY_FORM);
    setShowForm(true);
  }

  async function submit(e) {
    e.preventDefault();
    if (editingId) {
      await api.put(`/api/v1/admin/routes/${editingId}`, form);
    } else {
      await api.post('/api/v1/admin/routes', form);
    }
    setShowForm(false);
    load();
  }

  async function remove(id) {
    if (!confirm('Delete this route?')) return;
    await api.delete(`/api/v1/admin/routes/${id}`);
    load();
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="font-display text-2xl font-semibold text-ink">Routes</h1>
          <p className="text-sm text-muted mt-1">{routes.length} routes configured.</p>
        </div>
        <button
          onClick={startCreate}
          className="bg-signal text-base font-semibold px-4 py-2 rounded-md text-sm hover:opacity-90"
        >
          + Add route
        </button>
      </div>

      {showForm && (
        <form onSubmit={submit} className="bg-panel border border-border rounded-lg p-5 grid grid-cols-2 gap-4">
          <Field label="Route number" value={form.routeNumber} onChange={(v) => setForm({ ...form, routeNumber: v })} />
          <Field label="Route name" value={form.routeName} onChange={(v) => setForm({ ...form, routeName: v })} />
          <Field label="Source" value={form.sourceName} onChange={(v) => setForm({ ...form, sourceName: v })} />
          <Field label="Destination" value={form.destinationName} onChange={(v) => setForm({ ...form, destinationName: v })} />
          <Field label="Distance (km)" value={form.totalDistanceKm} onChange={(v) => setForm({ ...form, totalDistanceKm: v })} type="number" />
          <Field label="Duration (min)" value={form.estimatedDurationMin} onChange={(v) => setForm({ ...form, estimatedDurationMin: v })} type="number" />
          <div className="col-span-2 flex gap-3 pt-2">
            <button className="bg-signal text-base font-semibold px-4 py-2 rounded-md text-sm hover:opacity-90">
              {editingId ? 'Save changes' : 'Create route'}
            </button>
            <button type="button" onClick={() => setShowForm(false)} className="text-muted text-sm hover:text-ink">
              Cancel
            </button>
          </div>
        </form>
      )}

      <div className="bg-panel border border-border rounded-lg overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-panel2 text-muted text-xs uppercase tracking-wider font-mono">
            <tr>
              <th className="text-left px-5 py-3">Route</th>
              <th className="text-left px-5 py-3">Source → Destination</th>
              <th className="text-left px-5 py-3">Distance</th>
              <th className="text-left px-5 py-3">Duration</th>
              <th className="text-right px-5 py-3">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-border">
            {routes.map((r) => (
              <tr key={r.id}>
                <td className="px-5 py-3">
                  <span className="font-mono text-ink">{r.routeNumber}</span>
                  <p className="text-muted text-xs">{r.routeName}</p>
                </td>
                <td className="px-5 py-3 text-muted">{r.sourceName} → {r.destinationName}</td>
                <td className="px-5 py-3 font-mono text-muted">{r.totalDistanceKm ?? '—'} km</td>
                <td className="px-5 py-3 font-mono text-muted">{r.estimatedDurationMin ?? '—'} min</td>
                <td className="px-5 py-3 text-right space-x-3">
                  <button onClick={() => startEdit(r)} className="text-signal hover:underline">Edit</button>
                  <button onClick={() => remove(r.id)} className="text-alert hover:underline">Delete</button>
                </td>
              </tr>
            ))}
            {routes.length === 0 && (
              <tr><td colSpan={5} className="px-5 py-8 text-center text-muted">No routes yet.</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function Field({ label, value, onChange, type = 'text' }) {
  return (
    <div>
      <label className="text-xs uppercase tracking-wider text-muted font-mono">{label}</label>
      <input
        type={type}
        className="mt-1 w-full bg-panel2 border border-border rounded-md px-3 py-2 text-ink focus:outline-none focus:ring-2 focus:ring-signal/40"
        value={value}
        onChange={(e) => onChange(e.target.value)}
      />
    </div>
  );
}
