import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { toggleLanguage } from '../store/languageSlice.js';
import { useTranslation } from '../lib/i18n.js';

export default function TopBar() {
  const dispatch = useDispatch();
  const lang = useSelector((s) => s.language.lang);
  const t = useTranslation(lang);

  return (
    <header className="sticky top-0 z-20 glass border-b border-border">
      <div className="max-w-md mx-auto flex items-center justify-between px-4 py-3">
        <div className="flex items-center gap-2">
          <span className="w-2 h-2 rounded-full bg-brand pulse-dot" />
          <span className="font-display font-semibold text-ink">{t('appName')}</span>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={() => dispatch(toggleLanguage())}
            className="text-xs font-mono px-2 py-1 rounded-md border border-border text-muted hover:text-ink"
          >
            {lang === 'en' ? 'தமிழ்' : 'EN'}
          </button>
          <Link
            to="/sos"
            className="text-xs font-semibold px-3 py-1.5 rounded-md bg-alert/15 text-alert border border-alert/30"
          >
            SOS
          </Link>
        </div>
      </div>
    </header>
  );
}
