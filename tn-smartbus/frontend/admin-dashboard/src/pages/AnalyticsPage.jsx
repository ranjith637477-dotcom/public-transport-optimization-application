import React, { useEffect, useState } from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';
import api from '../lib/api.js';

export default function AnalyticsPage() {
  const [crowded, setCrowded] = useState([]);

  useEffect(() => {
    api.get('/api/v1/admin/analytics/crowded-routes').then((res) => {
      setCrowded(
        res.data.map((r) => ({
          route: r.routeNumber,
          name: r.routeName,
          occupancy: Math.round((r.avgOccupancyRatio || 0) * 100),
        }))
      );
    });
  }, []);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-display text-2xl font-semibold text-ink">Analytics</h1>
        <p className="text-sm text-muted mt-1">Based on conductor-reported occupancy, last 7 days.</p>
      </div>

      <div className="bg-panel border border-border rounded-lg p-5">
        <h2 className="font-display font-semibold text-ink mb-4">Most crowded routes</h2>
        {crowded.length === 0 ? (
          <p className="text-sm text-muted py-8 text-center">
            No occupancy data reported yet — this fills in once conductors start logging crowd counts.
          </p>
        ) : (
          <ResponsiveContainer width="100%" height={280}>
            <BarChart data={crowded}>
              <CartesianGrid strokeDasharray="3 3" stroke="#232E47" />
              <XAxis dataKey="route" stroke="#8592AD" fontSize={12} />
              <YAxis stroke="#8592AD" fontSize={12} unit="%" />
              <Tooltip
                contentStyle={{ background: '#182236', border: '1px solid #232E47', borderRadius: 8 }}
                labelStyle={{ color: '#E7ECF5' }}
                formatter={(value, name, props) => [`${value}%`, props.payload.name]}
              />
              <Bar dataKey="occupancy" fill="#35D399" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        )}
      </div>
    </div>
  );
}
