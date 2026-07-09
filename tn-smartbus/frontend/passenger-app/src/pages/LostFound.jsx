import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import api from '../lib/api.js';
import { useTranslation } from '../lib/i18n.js';

export default function LostFound() {
  const lang = useSelector((s) => s.language.lang);
  const t = useTranslation(lang);
  const token = useSelector((s) => s.auth.token);
  const [itemDescription, setItemDescription] = useState('');
  const [itemType, setItemType] = useState('LOST');
  const [contactInfo, setContactInfo] = useState('');
  const [items, setItems] = useState([]);
  const [view, setView] = useState('LOST');

  function load(type) {
    api.get('/api/v1/lost-and-found', { params: { itemType: type } }).then((res) => setItems(res.data)).catch(() => {});
  }

  useEffect(() => load(view), [view]);

  async function submit(e) {
    e.preventDefault();
    await api.post('/api/v1/lost-and-found', { itemDescription, itemType, contactInfo });
    setItemDescription('');
    setContactInfo('');
    load(view);
  }

  return (
    <div className="px-4 py-4 space-y-4 rise-in">
      <h1 className="font-display text-xl font-semibold text-ink">{t('lostAndFound')}</h1>

      {token ? (
        <form onSubmit={submit} className="glass rounded-2xl p-4 space-y-3">
          <div className="flex gap-2">
            {['LOST', 'FOUND'].map((ty) => (
              <button
                type="button"
                key={ty}
                onClick={() => setItemType(ty)}
                className={`flex-1 text-xs font-mono py-2 rounded-lg border ${
                  itemType === ty ? 'bg-surface2 border-brand/40 text-ink' : 'border-border text-muted'
                }`}
              >
                {ty}
              </button>
            ))}
          </div>
          <div>
            <label className="text-xs uppercase tracking-wider text-muted font-mono">{t('itemDescription')}</label>
            <textarea
              value={itemDescription}
              onChange={(e) => setItemDescription(e.target.value)}
              rows={2}
              required
              className="mt-1 w-full bg-surface2 border border-border rounded-lg px-3 py-2 text-ink focus:outline-none focus:ring-2 focus:ring-brand/40"
            />
          </div>
          <div>
            <label className="text-xs uppercase tracking-wider text-muted font-mono">{t('contactInfo')}</label>
            <input
              value={contactInfo}
              onChange={(e) => setContactInfo(e.target.value)}
              className="mt-1 w-full bg-surface2 border border-border rounded-lg px-3 py-2 text-ink focus:outline-none focus:ring-2 focus:ring-brand/40"
            />
          </div>
          <button className="w-full bg-brand text-base font-semibold py-2.5 rounded-lg hover:opacity-90 transition-opacity">
            {t('reportItem')}
          </button>
        </form>
      ) : (
        <div className="glass rounded-2xl p-4 text-center">
          <p className="text-sm text-muted mb-2">Sign in to report a lost or found item.</p>
          <Link to="/login" className="text-sm font-semibold px-4 py-2 rounded-lg bg-brand text-base inline-block">{t('signIn')}</Link>
        </div>
      )}

      <div className="flex gap-2">
        {['LOST', 'FOUND'].map((ty) => (
          <button
            key={ty}
            onClick={() => setView(ty)}
            className={`flex-1 text-xs font-mono py-2 rounded-lg border ${
              view === ty ? 'bg-surface2 border-brand/40 text-ink' : 'border-border text-muted'
            }`}
          >
            {ty} items
          </button>
        ))}
      </div>

      <div className="glass rounded-2xl divide-y divide-border">
        {items.length === 0 && <p className="text-sm text-muted p-4 text-center">No {view.toLowerCase()} items reported.</p>}
        {items.map((item) => (
          <div key={item.id} className="p-4">
            <p className="text-sm text-ink">{item.itemDescription}</p>
            {item.contactInfo && <p className="text-xs text-muted mt-1">Contact: {item.contactInfo}</p>}
          </div>
        ))}
      </div>
    </div>
  );
}
