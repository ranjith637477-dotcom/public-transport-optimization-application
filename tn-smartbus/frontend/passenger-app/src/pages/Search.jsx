import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import api from '../lib/api.js';
import { useTranslation } from '../lib/i18n.js';

export default function Search() {
  const lang = useSelector((s) => s.language.lang);
  const t = useTranslation(lang);
  const [query, setQuery] = useState('');
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);

  async function runSearch(e) {
    e.preventDefault();
    if (!query.trim()) return;
    setLoading(true);
    try {
      const res = await api.get('/api/v1/search', { params: { query } });
      setResults(res.data);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="px-4 py-4 space-y-4 rise-in">
      <form onSubmit={runSearch}>
        <input
          autoFocus
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder={t('searchPlaceholder')}
          className="w-full glass rounded-full px-4 py-3 text-sm text-ink placeholder:text-muted focus:outline-none"
        />
      </form>

      {loading && <p className="text-sm text-muted text-center py-6">Searching...</p>}

      {results && (
        <div className="space-y-4">
          {results.routes?.length > 0 && (
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="glass rounded-2xl p-4">
              <h3 className="text-xs uppercase tracking-wider text-muted font-mono mb-2">Routes</h3>
              <div className="divide-y divide-border">
                {results.routes.map((r) => (
                  <Link key={r.routeNumber} to={`/route/${r.routeId}`} className="block py-2">
                    <p className="text-ink text-sm font-medium">
                      <span className="font-mono text-brand">{r.routeNumber}</span> — {r.routeName}
                    </p>
                    <p className="text-xs text-muted">{r.sourceName} → {r.destinationName}</p>
                  </Link>
                ))}
              </div>
            </motion.div>
          )}

          {results.buses?.length > 0 && (
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.05 }} className="glass rounded-2xl p-4">
              <h3 className="text-xs uppercase tracking-wider text-muted font-mono mb-2">Buses</h3>
              <div className="divide-y divide-border">
                {results.buses.map((b) => (
                  <div key={b.id} className="py-2 flex items-center justify-between">
                    <span className="font-mono text-ink text-sm">{b.registrationNumber}</span>
                    <span className="text-xs text-muted">{b.busType}</span>
                  </div>
                ))}
              </div>
            </motion.div>
          )}

          {(!results.routes?.length && !results.buses?.length) && (
            <p className="text-sm text-muted text-center py-6">No matches found for "{query}".</p>
          )}
        </div>
      )}
    </div>
  );
}
