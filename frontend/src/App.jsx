import React, { useState, useEffect } from 'react';
import { Routes, Route, Link, useLocation, Navigate } from 'react-router-dom';
import { LayoutDashboard, BookOpen, MessageSquare, HelpCircle, Menu, X, LogOut } from 'lucide-react';
import Dashboard from './components/Dashboard';
import DiplomaturaList from './components/DiplomaturaList';
import ConsultationList from './components/ConsultationList';
import FAQManager from './components/FAQManager';
import Login from './components/Login';

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);
    const location = useLocation();

    useEffect(() => {
        const auth = localStorage.getItem('isAuthenticated');
        if (auth === 'true') {
            setIsAuthenticated(true);
        }
    }, []);

    const handleLogin = () => {
        setIsAuthenticated(true);
        localStorage.setItem('isAuthenticated', 'true');
    };

    const handleLogout = () => {
        setIsAuthenticated(false);
        localStorage.removeItem('isAuthenticated');
    };

    const isActive = (path) => location.pathname === path ? 'bg-blue-700' : '';

    const toggleSidebar = () => {
        setIsSidebarOpen(!isSidebarOpen);
    };

    const [teamMembers, setTeamMembers] = useState([]);
    const [teamStats, setTeamStats] = useState({});
    const [showTeamModal, setShowTeamModal] = useState(false);
    const [newTeamMemberName, setNewTeamMemberName] = useState("");

    useEffect(() => {
        if (isAuthenticated) {
            fetchTeamMembers();
            fetchTeamStats();
            const interval = setInterval(fetchTeamStats, 10000); // Poll stats
            return () => clearInterval(interval);
        }
    }, [isAuthenticated]);

    const fetchTeamMembers = () => {
        // We'll need to define this endpoint in the backend or use axios
        // Assuming we have axios available globally or import it
        import('axios').then(axios => {
            axios.default.get('/api/team/members')
                .then(res => setTeamMembers(res.data))
                .catch(console.error);
        });
    };

    const fetchTeamStats = () => {
        import('axios').then(axios => {
            axios.default.get('/api/team/stats')
                .then(res => setTeamStats(res.data))
                .catch(console.error);
        });
    };

    const handleCreateTeamMember = () => {
        if (!newTeamMemberName.trim()) return;
        import('axios').then(axios => {
            axios.default.post('/api/team/members', { name: newTeamMemberName })
                .then(() => {
                    setNewTeamMemberName("");
                    setShowTeamModal(false);
                    fetchTeamMembers();
                })
                .catch(console.error);
        });
    };

    const handleDeleteTeamMember = (id, e) => {
        e.preventDefault();
        e.stopPropagation();
        if (!window.confirm("¿Eliminar este miembro? Se perderán las asignaciones.")) return;

        import('axios').then(axios => {
            axios.default.delete(`/api/team/members/${id}`)
                .then(() => fetchTeamMembers())
                .catch(console.error);
        });
    }

    if (!isAuthenticated) {
        return <Login onLogin={handleLogin} />;
    }

    return (
        <div className="flex h-screen bg-gray-100 overflow-hidden">
            {/* Mobile Header */}
            <div className="md:hidden fixed w-full bg-blue-800 text-white z-20 flex justify-between items-center p-4 shadow-md">
                <span className="font-bold text-xl">IGSM Admin</span>
                <button onClick={toggleSidebar}>
                    {isSidebarOpen ? <X size={24} /> : <Menu size={24} />}
                </button>
            </div>

            {/* Sidebar Overlay for Mobile */}
            {isSidebarOpen && (
                <div
                    className="fixed inset-0 bg-black bg-opacity-50 z-10 md:hidden"
                    onClick={() => setIsSidebarOpen(false)}
                ></div>
            )}

            {/* Sidebar */}
            <div className={`
                fixed md:static inset-y-0 left-0 z-50 w-64 bg-blue-800 text-white flex flex-col transition-transform duration-300 ease-in-out transform 
                ${isSidebarOpen ? 'translate-x-0' : '-translate-x-full'} 
                md:translate-x-0
            `}>
                <div className="p-6 text-2xl font-bold border-b border-blue-700 hidden md:block">
                    IGSM Admin
                </div>
                <div className="p-6 text-2xl font-bold border-b border-blue-700 md:hidden mt-12">
                    Menú
                </div>
                <nav className="flex-1 p-4 overflow-y-auto">
                    <Link to="/" onClick={() => setIsSidebarOpen(false)} className={`flex items-center gap-3 p-3 rounded mb-2 hover:bg-blue-700 ${isActive('/')}`}>
                        <LayoutDashboard size={20} />
                        Dashboard
                    </Link>
                    <Link to="/diplomaturas" onClick={() => setIsSidebarOpen(false)} className={`flex items-center gap-3 p-3 rounded mb-2 hover:bg-blue-700 ${isActive('/diplomaturas')}`}>
                        <BookOpen size={20} />
                        Diplomaturas
                    </Link>
                    <Link to="/consultations" onClick={() => setIsSidebarOpen(false)} className={`flex items-center gap-3 p-3 rounded mb-2 hover:bg-blue-700 ${isActive('/consultations')}`}>
                        <MessageSquare size={20} />
                        Consultas
                    </Link>
                    <Link to="/faqs" onClick={() => setIsSidebarOpen(false)} className={`flex items-center gap-3 p-3 rounded mb-2 hover:bg-blue-700 ${isActive('/faqs')}`}>
                        <HelpCircle size={20} />
                        Preguntas Frecuentes
                    </Link>

                    <div className="mt-6 pt-6 border-t border-blue-700">
                        <div className="flex justify-between items-center mb-2 px-3 text-sm font-semibold text-blue-300 uppercase">
                            <span>Chats Privados</span>
                            <button onClick={() => setShowTeamModal(true)} className="hover:text-white" title="Crear Chat Personalizado">+</button>
                        </div>
                        {teamMembers.map(member => (
                            <Link
                                key={member.id}
                                to={`/consultations?teamMemberId=${member.id}`}
                                onClick={() => setIsSidebarOpen(false)}
                                className={`flex items-center justify-between p-3 rounded mb-1 hover:bg-blue-700 text-sm ${location.search.includes(`teamMemberId=${member.id}`) ? 'bg-blue-700' : ''}`}
                            >
                                <span className="flex items-center gap-2 truncate">
                                    👤 {member.name}
                                    {teamStats[member.id] > 0 && (
                                        <span className="bg-red-500 text-white text-xs font-bold px-2 py-0.5 rounded-full">
                                            {teamStats[member.id]}
                                        </span>
                                    )}
                                </span>
                                <button onClick={(e) => handleDeleteTeamMember(member.id, e)} className="text-blue-400 hover:text-red-300">×</button>
                            </Link>
                        ))}
                    </div>
                </nav>
                <div className="p-4 border-t border-blue-700">
                    <button onClick={handleLogout} className="flex items-center gap-3 p-3 rounded hover:bg-blue-700 w-full text-left text-red-300 hover:text-red-100">
                        <LogOut size={20} />
                        Cerrar Sesión
                    </button>
                </div>
            </div>

            {/* Modal for New Team Member */}
            {showTeamModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
                    <div className="bg-white rounded-lg p-6 w-full max-w-sm">
                        <h3 className="text-lg font-bold mb-4">Crear Chat Personalizado</h3>
                        <p className="text-sm text-gray-600 mb-4">Ingrese el nombre del miembro del equipo.</p>
                        <input
                            type="text"
                            className="w-full border p-2 rounded mb-4"
                            placeholder="Ej: Juan"
                            value={newTeamMemberName}
                            onChange={e => setNewTeamMemberName(e.target.value)}
                            autoFocus
                        />
                        <div className="flex justify-end gap-2">
                            <button onClick={() => setShowTeamModal(false)} className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded">Cancelar</button>
                            <button onClick={handleCreateTeamMember} className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700">Crear</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Main Content */}
            <div className="flex-1 flex flex-col h-full overflow-hidden w-full">
                <div className="flex-1 overflow-auto p-4 md:p-8 mt-16 md:mt-0">
                    <Routes>
                        <Route path="/" element={<Dashboard />} />
                        <Route path="/diplomaturas" element={<DiplomaturaList />} />
                        <Route path="/consultations" element={<ConsultationList />} />
                        <Route path="/faqs" element={<FAQManager />} />
                        <Route path="*" element={<Navigate to="/" replace />} />
                    </Routes>
                </div>
            </div>
        </div>
    );
}

export default App;
