import React from 'react';
import { Routes, Route, Link, useLocation } from 'react-router-dom';
import { LayoutDashboard, BookOpen, Users, MessageSquare, HelpCircle } from 'lucide-react';
import Dashboard from './components/Dashboard';
import DiplomaturaList from './components/DiplomaturaList';
import SubscriptionList from './components/SubscriptionList';
import ConsultationList from './components/ConsultationList';
import FAQManager from './components/FAQManager';

function App() {
    const location = useLocation();

    const isActive = (path) => location.pathname === path ? 'bg-blue-700' : '';

    return (
        <div className="flex h-screen bg-gray-100">
            {/* Sidebar */}
            <div className="w-64 bg-blue-800 text-white flex flex-col">
                <div className="p-6 text-2xl font-bold border-b border-blue-700">
                    IGSM Admin
                </div>
                <nav className="flex-1 p-4">
                    <Link to="/" className={`flex items-center gap-3 p-3 rounded mb-2 hover:bg-blue-700 ${isActive('/')}`}>
                        <LayoutDashboard size={20} />
                        Dashboard
                    </Link>
                    <Link to="/diplomaturas" className={`flex items-center gap-3 p-3 rounded mb-2 hover:bg-blue-700 ${isActive('/diplomaturas')}`}>
                        <BookOpen size={20} />
                        Diplomaturas
                    </Link>
                    <Link to="/subscriptions" className={`flex items-center gap-3 p-3 rounded mb-2 hover:bg-blue-700 ${isActive('/subscriptions')}`}>
                        <Users size={20} />
                        Inscripciones
                    </Link>
                    <Link to="/consultations" className={`flex items-center gap-3 p-3 rounded mb-2 hover:bg-blue-700 ${isActive('/consultations')}`}>
                        <MessageSquare size={20} />
                        Consultas
                    </Link>
                    <Link to="/faqs" className={`flex items-center gap-3 p-3 rounded mb-2 hover:bg-blue-700 ${isActive('/faqs')}`}>
                        <HelpCircle size={20} />
                        Preguntas Frecuentes
                    </Link>
                </nav>
            </div>

            {/* Main Content */}
            <div className="flex-1 overflow-auto">
                <Routes>
                    <Route path="/" element={<Dashboard />} />
                    <Route path="/diplomaturas" element={<DiplomaturaList />} />
                    <Route path="/subscriptions" element={<SubscriptionList />} />
                    <Route path="/consultations" element={<ConsultationList />} />
                    <Route path="/faqs" element={<FAQManager />} />
                </Routes>
            </div>
        </div>
    );
}

export default App;
