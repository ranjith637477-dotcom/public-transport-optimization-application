import React, { useEffect, useState } from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';

const NAV_ITEMS = [
  { to: '/', label: 'Overview', end: true },
  { to: '/routes', label: 'Routes' },
  { to: '/buses', label: 'Fleet' },
  { to: '/complaints', label: 'Complaints' },
  { to: '/analytics', label: 'Analytics' },
];

function LiveClock() {
  const [time, setTime] = useState(new Date());
  useEffect(() => {
    const t = setInterval(() => setTime(new Date()), 1000);
    return () => clearInterval(t);
  }, []);
  return (
    <span className="font-mono text-sm text-muted tabular-nums">
      {time.toLocaleTimeString('en-IN', { hour12: false })}
    </span>
  );
}

export default function Layout() {
  const navigate = useNavigate();

  function logout() {
    localStorage.removeItem('tnsmartbus_admin_token');
    navigate('/login');
  }

  return (
    <div className="min-h-screen flex">
      <aside className="w-60 shrink-0 border-r border-border bg-panel flex flex-col">
        <div className="px-5 py-6 border-b border-border">
          <div className="flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-signal pulse-dot" />
            <span className="font-display font-semibold tracking-tight text-ink">TN SmartBus</span>
          </div>
          <p className="text-xs text-muted mt-1 font-mono">FLEET CONTROL</p>
        </div>

        <nav className="flex-1 px-3 py-4 space-y-1">
          {NAV_ITEMS.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              className={({ isActive }) =>
                `block px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-panel2 text-ink border border-border'
                    : 'text-muted hover:text-ink hover:bg-panel2/50'
                }`
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className="px-3 py-4 border-t border-border">
          <button
            onClick={logout}
            className="w-full text-left px-3 py-2 rounded-md text-sm text-muted hover:text-alert hover:bg-panel2/50 transition-colors"
          >
            Sign out
          </button>
        </div>
      </aside>

      <div className="flex-1 flex flex-col min-w-0">
        <header className="h-14 border-b border-border bg-panel/60 backdrop-blur flex items-center justify-between px-6">
          <span className="text-xs text-muted font-mono tracking-wider">TAMIL NADU STATE TRANSPORT CORPORATION</span>
          <LiveClock />
        </header>
        <main className="flex-1 p-6 overflow-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
