import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout.jsx';
import Login from './pages/Login.jsx';
import Dashboard from './pages/Dashboard.jsx';
import RoutesPage from './pages/RoutesPage.jsx';
import BusesPage from './pages/BusesPage.jsx';
import ComplaintsPage from './pages/ComplaintsPage.jsx';
import AnalyticsPage from './pages/AnalyticsPage.jsx';

function RequireAuth({ children }) {
  const token = localStorage.getItem('tnsmartbus_admin_token');
  if (!token) return <Navigate to="/login" replace />;
  return children;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/"
        element={
          <RequireAuth>
            <Layout />
          </RequireAuth>
        }
      >
        <Route index element={<Dashboard />} />
        <Route path="routes" element={<RoutesPage />} />
        <Route path="buses" element={<BusesPage />} />
        <Route path="complaints" element={<ComplaintsPage />} />
        <Route path="analytics" element={<AnalyticsPage />} />
      </Route>
    </Routes>
  );
}
