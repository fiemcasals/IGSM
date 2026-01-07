import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8', '#82ca9d'];

const Dashboard = () => {
    const [stats, setStats] = useState({ inquiries: [], subscriptions: [] });

    useEffect(() => {
        axios.get('/api/stats')
            .then(res => setStats(res.data))
            .catch(err => console.error(err));
    }, []);

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-3xl font-bold">Dashboard</h1>
                <a
                    href="/api/stats/export"
                    target="_blank"
                    rel="noopener noreferrer"
                    className="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2"
                >
                    📥 Exportar Estadísticas
                </a>
            </div>

            <div className="grid grid-cols-1 gap-8">
                <div className="bg-white p-4 rounded-lg shadow">
                    <h2 className="text-xl font-semibold mb-4">Consultas por Diplomatura</h2>
                    <div className="h-[600px]">
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart
                                layout="vertical"
                                data={stats.inquiries}
                                margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                            >
                                <CartesianGrid strokeDasharray="3 3" />
                                <XAxis type="number" />
                                <YAxis dataKey="name" type="category" width={250} style={{ fontSize: '12px' }} />
                                <Tooltip />
                                <Legend />
                                <Bar dataKey="count" fill="#8884d8" name="Consultas" />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                </div>


            </div>
        </div>
    );
};

export default Dashboard;
