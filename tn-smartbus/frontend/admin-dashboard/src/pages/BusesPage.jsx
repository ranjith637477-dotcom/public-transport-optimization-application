import React, { useEffect, useState } from 'react';
import api from '../lib/api.js';

const EMPTY_FORM = {
  registrationNumber: '', busType: 'ORDINARY', totalSeats: 52,
  ladiesSeats: 6, seniorCitizenSeats: 4, status: 'ACTIVE',
};

const BUS_TYPES = ['ORDINARY', 'EXPRESS', 'DELUXE', 'ULTRA_DELUXE'];
const STATUSES = ['ACTIVE', 'MAINTENANCE', 'RETIRED'];

export default function BusesPage() {
  const [buses, setBuses] = useState([]);
  const [form, setForm] = useState(EMPTY_FORM);
  const [editingId, setEditingId] = useState(null);
  const [showForm, setShowForm] = useState(false);

  async function load() {
    const res = await api.get('/api/v1/admin/buses');
    setBuses(res.data);
  }

  useEffect(() => { load(); }, []);

  function startEdit(bus) {
    setEditingId(bus.id);
    setForm({
      registrationNumber: bus.registrationNumber, busType: bus.busType,
      totalSeats: bus.totalSeats, ladiesSeats: bus.ladiesSeats,
      seniorCitizenSeats: bus.seniorCitizenSeats, status: bus.status,
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
      await api.put(`/api/v1/admin/buses/${editingId}`, form);
    } else {
      await api.post('/api/v1/admin/buses', form);
    }
    setShowForm(false);
    load();
  }

  async function remove(id) {
    if (!confirm('Delete this bus?')) return;
    await api.delete(`/api/v1/admin/buses/${id}`);
    load();
  }

  const statusColor = { ACTIVE: 'text-signal', MAINTENANCE: 'text-amber', RETIRED: 'text-muted' };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="font-display text-2xl font-semibold text-ink">Fleet</h1>
          <p className="text-sm text-muted mt-1">{buses.length} buses registered.</p>
        </div>
        <button onClick={startCreate} className="bg-signal text-base font-semibold px-4 py-2 rounded-md text-sm hover:opacity-90">
          + Add bus
        </button>
      </div>

      {showForm && (
        <form onSubmit={submit} className="bg-panel border border-border rounded-lg p-5 grid grid-cols-2 gap-4">
          <Field label="Registration number" value={form.registrationNumber} onChange={(v) => setForm({ ...form, registrationNumber: v })} />
          <SelectField label="Bus type" value={form.busType} options={BUS_TYPES} onChange={(v) => setForm({ ...form, busType: v })} />
          <Field label="Total seats" value={form.totalSeats} onChange={(v) => setForm({ ...form, totalSeats: v })} type="number" />
          <Field label="Ladies seats" value={form.ladiesSeats} onChange={(v) => setForm({ ...form, ladiesSeats: v })} type="number" />
          <Field label="Senior citizen seats" value={form.seniorCitizenSeats} onChange={(v) => setForm({ ...form, seniorCitizenSeats: v })} type="number" />
          <SelectField label="Status" value={form.status} options={STATUSES} onChange={(v) => setForm({ ...form, status: v })} />
          <div className="col-span-2 flex gap-3 pt-2">
            <button className="bg-signal text-base font-semibold px-4 py-2 rounded-md text-sm hover:opacity-90">
              {editingId ? 'Save changes' : 'Add bus'}
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
              <th className="text-left px-5 py-3">Registration</th>
              <th className="text-left px-5 py-3">Type</th>
              <th className="text-left px-5 py-3">Seats</th>
              <th className="text-left px-5 py-3">Status</th>
              <th className="text-right px-5 py-3">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-border">
            {buses.map((b) => (
              <tr key={b.id}>
                <td className="px-5 py-3 font-mono text-ink">{b.registrationNumber}</td>
                <td className="px-5 py-3 text-muted">{b.busType}</td>
                <td className="px-5 py-3 font-mono text-muted">
                  {b.totalSeats} <span className="text-xs">({b.ladiesSeats} ladies, {b.seniorCitizenSeats} senior)</span>
                </td>
                <td className={`px-5 py-3 font-mono ${statusColor[b.status] || 'text-muted'}`}>{b.status}</td>
                <td className="px-5 py-3 text-right space-x-3">
                  <button onClick={() => startEdit(b)} className="text-signal hover:underline">Edit</button>
                  <button onClick={() => remove(b.id)} className="text-alert hover:underline">Delete</button>
                </td>
              </tr>
            ))}
            {buses.length === 0 && (
              <tr><td colSpan={5} className="px-5 py-8 text-center text-muted">No buses yet.</td></tr>
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

function SelectField({ label, value, options, onChange }) {
  return (
    <div>
      <label className="text-xs uppercase tracking-wider text-muted font-mono">{label}</label>
      <select
        className="mt-1 w-full bg-panel2 border border-border rounded-md px-3 py-2 text-ink focus:outline-none focus:ring-2 focus:ring-signal/40"
        value={value}
        onChange={(e) => onChange(e.target.value)}
      >
        {options.map((o) => <option key={o} value={o}>{o}</option>)}
      </select>
    </div>
  );
}
