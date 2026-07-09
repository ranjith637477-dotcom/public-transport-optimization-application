import React from 'react';
import { Routes, Route } from 'react-router-dom';
import TopBar from './components/TopBar.jsx';
import BottomNav from './components/BottomNav.jsx';
import Home from './pages/Home.jsx';
import Search from './pages/Search.jsx';
import JourneyPlanner from './pages/JourneyPlanner.jsx';
import FareCalculator from './pages/FareCalculator.jsx';
import More from './pages/More.jsx';
import Favorites from './pages/Favorites.jsx';
import Complaints from './pages/Complaints.jsx';
import LostFound from './pages/LostFound.jsx';
import Login from './pages/Login.jsx';
import Sos from './pages/Sos.jsx';
import RouteDetail from './pages/RouteDetail.jsx';
import Notifications from './pages/Notifications.jsx';

export default function App() {
  return (
    <div className="min-h-screen flex flex-col">
      <TopBar />
      <main className="flex-1 max-w-md mx-auto w-full pb-24">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/search" element={<Search />} />
          <Route path="/plan" element={<JourneyPlanner />} />
          <Route path="/fare" element={<FareCalculator />} />
          <Route path="/more" element={<More />} />
          <Route path="/favorites" element={<Favorites />} />
          <Route path="/complaints" element={<Complaints />} />
          <Route path="/lost-and-found" element={<LostFound />} />
          <Route path="/login" element={<Login />} />
          <Route path="/sos" element={<Sos />} />
          <Route path="/route/:routeId" element={<RouteDetail />} />
          <Route path="/notifications" element={<Notifications />} />
        </Routes>
      </main>
      <BottomNav />
    </div>
  );
}
