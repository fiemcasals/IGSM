import { useLocation } from 'react-router-dom';
import axios from 'axios';
import { MessageSquare, Send, Search, User, Check, CheckCheck, PlusCircle, X, Edit2, Users, Trash2 } from 'lucide-react';

const ConsultationList = () => {
    const [consultations, setConsultations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedUser, setSelectedUser] = useState(null);
    const [replyMessage, setReplyMessage] = useState("");
    const [searchTerm, setSearchTerm] = useState("");

    // Team & Profile State
    const location = useLocation();
    const [teamMemberId, setTeamMemberId] = useState(null);
    const [teamMembers, setTeamMembers] = useState([]);
    const [contactProfiles, setContactProfiles] = useState({}); // Map: remoteJid -> profile
    const [showNicknameModal, setShowNicknameModal] = useState(false);
    const [nicknameData, setNicknameData] = useState({ jid: "", nickname: "" });

    // FAQ Modal State
    const [showFaqModal, setShowFaqModal] = useState(false);
    const [faqData, setFaqData] = useState({ question: "", answer: "" });

    const messagesEndRef = useRef(null);

    useEffect(() => {
        fetchConsultations();
        fetchTeamData();
        // Poll for new messages every 10 seconds
        const interval = setInterval(() => {
            fetchConsultations();
            fetchTeamData();
        }, 10000);
        return () => clearInterval(interval);
    }, []);

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const id = params.get('teamMemberId');
        setTeamMemberId(id ? parseInt(id) : null);
        setSelectedUser(null); // Deselect when switching views
    }, [location.search]);

    // Mark as seen when selecting a user
    useEffect(() => {
        if (selectedUser) {
            axios.put(`/api/consultations/user/${selectedUser}/seen`)
                .then(() => {
                    // Optimistically update local state
                    setConsultations(prev => prev.map(c =>
                        c.userId === selectedUser ? { ...c, seen: true } : c
                    ));
                    fetchConsultations(); // Refresh to be sure
                })
                .catch(console.error);
        }
    }, [selectedUser]);

    useEffect(() => {
        scrollToBottom();
    }, [selectedUser, consultations]);

    const fetchConsultations = () => {
        axios.get('/api/consultations')
            .then(res => {
                setConsultations(res.data);
                setLoading(false);
            })
            .catch(err => {
                console.error(err);
                setLoading(false);
            });
    };

    const fetchTeamData = () => {
        axios.get('/api/team/members').then(res => setTeamMembers(res.data)).catch(console.error);
        axios.get('/api/team/profiles').then(res => {
            const map = {};
            res.data.forEach(p => map[p.remoteJid] = p);
            setContactProfiles(map);
        }).catch(console.error);
    }

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    const handleReply = () => {
        if (!replyMessage.trim() || !selectedUser) return;

        // Find the last message from the user to reply to (for quoting)
        const userMessages = consultations.filter(c => c.userId === selectedUser && !c.adminReply)
            .sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));

        const lastMessage = userMessages[userMessages.length - 1];

        if (!lastMessage) return;

        axios.post(`/api/consultations/${lastMessage.id}/reply`, { message: replyMessage })
            .then(() => {
                setReplyMessage("");
                fetchConsultations();
            })
            .catch(err => {
                console.error(err);
                alert("Error al enviar respuesta");
            });
    };

    const handleAddToFAQ = (messageText) => {
        setFaqData({ question: messageText, answer: "" });
        setShowFaqModal(true);
    };

    const handleSaveFAQ = () => {
        if (!faqData.question.trim() || !faqData.answer.trim()) {
            alert("Completa ambos campos");
            return;
        }

        axios.post('/api/faqs', faqData)
            .then(() => {
                setShowFaqModal(false);
                setFaqData({ question: "", answer: "" });
                alert("Pregunta frecuente guardada exitosamente");
            })
            .catch(console.error);
    };

    const handleAssign = (userId, memberId) => {
        axios.post(`/api/team/profiles/${userId}/assign`, { memberId })
            .then(res => {
                setContactProfiles(prev => ({ ...prev, [userId]: res.data }));
                fetchConsultations(); // Refresh to update view if filtering
            })
            .catch(console.error);
    };

    const handleDeleteConversation = (userId, e) => {
        e.stopPropagation();
        if (!window.confirm("¿Estás seguro de eliminar este chat? Se borrarán todos los mensajes.")) return;

        axios.delete(`/api/consultations/user/${userId}`)
            .then(() => {
                if (selectedUser === userId) setSelectedUser(null);
                fetchConsultations();
            })
            .catch(console.error);
    };

    const handleMarkAsSeen = (userId, e) => {
        e.stopPropagation();
        axios.put(`/api/consultations/user/${userId}/seen`)
            .then(() => {
                setConsultations(prev => prev.map(c =>
                    c.userId === userId ? { ...c, seen: true } : c
                ));
            })
            .catch(console.error);
    };

    const handleNickname = () => {
        if (!nicknameData.nickname.trim()) return;
        axios.post(`/api/team/profiles/${nicknameData.jid}/nickname`, { nickname: nicknameData.nickname })
            .then(res => {
                setContactProfiles(prev => ({ ...prev, [res.data.remoteJid]: res.data }));
                setShowNicknameModal(false);
            })
            .catch(console.error);
    };

    const openNicknameModal = (userId, currentNick) => {
        setNicknameData({ jid: userId, nickname: currentNick || "" });
        setShowNicknameModal(true);
    };

    // Group consultations by user
    const threads = consultations.reduce((acc, curr) => {
        const profile = contactProfiles[curr.userId];

        if (!acc[curr.userId]) {
            acc[curr.userId] = {
                userId: curr.userId,
                contactPhone: curr.contactPhone,
                messages: [],
                lastMessage: null,
                unreadCount: 0,
                profile: profile || null
            };
        }
        acc[curr.userId].messages.push(curr);
        return acc;
    }, {});

    // Convert to array and sort by latest message
    const sortedThreads = Object.values(threads).map(thread => {
        thread.messages.sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));
        thread.lastMessage = thread.messages[thread.messages.length - 1];

        // Calculate unread count (messages since last admin reply AND not seen)
        let count = 0;
        for (let i = thread.messages.length - 1; i >= 0; i--) {
            const msg = thread.messages[i];
            if (msg.adminReply) break; // Stop at last reply
            if (!msg.seen) count++;    // Count only unseen messages from user
        }
        thread.unreadCount = count;

        return thread;
    }).sort((a, b) => new Date(b.lastMessage.timestamp) - new Date(a.lastMessage.timestamp));

    const filteredThreads = sortedThreads.filter(thread => {
        // Text Search
        const matchesSearch = thread.contactPhone?.toLowerCase().includes(searchTerm.toLowerCase()) ||
            thread.userId.toLowerCase().includes(searchTerm.toLowerCase()) ||
            thread.profile?.nickname?.toLowerCase().includes(searchTerm.toLowerCase());

        // Team Filter
        if (teamMemberId) {
            return matchesSearch && thread.profile?.assignedMember?.id === teamMemberId;
        }
        return matchesSearch;
    });

    const activeThread = selectedUser ? threads[selectedUser] : null;

    if (loading) return <div className="p-6 flex justify-center items-center h-full">Cargando...</div>;

    return (
        <div className="flex h-[calc(100vh-64px)] bg-gray-100 overflow-hidden relative">
            {/* Sidebar - Thread List */}
            <div className={`w-full md:w-1/3 bg-white border-r border-gray-200 flex flex-col ${selectedUser ? 'hidden md:flex' : 'flex'}`}>
                <div className="p-4 border-b border-gray-200 bg-gray-50">
                    <h2 className="text-xl font-bold mb-4 flex items-center gap-2">
                        <MessageSquare className="text-blue-600" /> Chats
                    </h2>
                    <div className="relative">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
                        <input
                            type="text"
                            placeholder="Buscar por número..."
                            className="w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>
                </div>

                <div className="flex-1 overflow-y-auto">
                    {filteredThreads.map(thread => (
                        <div
                            key={thread.userId}
                            onClick={() => setSelectedUser(thread.userId)}
                            className={`p-4 border-b border-gray-100 cursor-pointer hover:bg-gray-50 transition-colors ${selectedUser === thread.userId ? 'bg-blue-50 border-l-4 border-l-blue-600' : ''}`}
                        >
                            <div className="flex justify-between items-start mb-1">
                                <div>
                                    <span className="font-semibold text-gray-800 block">
                                        {thread.profile?.nickname || thread.contactPhone || thread.userId}
                                    </span>
                                    {thread.profile?.assignedMember && (
                                        <span className="text-[10px] bg-blue-100 text-blue-800 px-1 rounded">
                                            👤 {thread.profile.assignedMember.name}
                                        </span>
                                    )}
                                </div>
                                <span className="text-xs text-gray-500 whitespace-nowrap ml-2">
                                    {new Date(thread.lastMessage.timestamp).toLocaleDateString()}
                                </span>
                            </div>
                            <div className="flex justify-between items-center">
                                <p className="text-sm text-gray-600 truncate w-3/4">
                                    {thread.lastMessage.adminReply && <span className="text-blue-600 mr-1">Tú:</span>}
                                    {thread.lastMessage.message}
                                </p>
                                <div className="flex items-center gap-2">
                                    {thread.unreadCount > 0 && (
                                        <span
                                            onClick={(e) => handleMarkAsSeen(thread.userId, e)}
                                            className="bg-green-500 text-white text-xs font-bold px-2 py-1 rounded-full hover:bg-green-600 cursor-pointer"
                                            title="Marcar como leído"
                                        >
                                            {thread.unreadCount}
                                        </span>
                                    )}
                                    <button
                                        onClick={(e) => handleDeleteConversation(thread.userId, e)}
                                        className="text-gray-400 hover:text-red-500 p-1"
                                        title="Eliminar chat"
                                    >
                                        <Trash2 size={16} />
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                    {filteredThreads.length === 0 && (
                        <div className="p-8 text-center text-gray-500">
                            No se encontraron conversaciones.
                        </div>
                    )}
                </div>
            </div>

            {/* Main Chat Area */}
            <div className={`flex-1 flex flex-col bg-[#e5ddd5] ${!selectedUser ? 'hidden md:flex' : 'flex'}`}>
                {activeThread ? (
                    <>
                        {/* Chat Header */}
                        <div className="p-4 bg-gray-100 border-b border-gray-200 flex items-center justify-between shadow-sm">
                            <div className="flex items-center gap-3">
                                <button
                                    className="md:hidden text-gray-600"
                                    onClick={() => setSelectedUser(null)}
                                >
                                    ← Volver
                                </button>
                                <div className="bg-gray-300 p-2 rounded-full">
                                    <User size={24} className="text-gray-600" />
                                </div>
                                <div>
                                    <div className="flex items-center gap-2">
                                        <h3 className="font-bold text-gray-800">
                                            {activeThread.profile?.nickname || activeThread.contactPhone}
                                        </h3>
                                        <button onClick={() => openNicknameModal(activeThread.userId, activeThread.profile?.nickname)} className="text-gray-400 hover:text-blue-600">
                                            <Edit2 size={14} />
                                        </button>
                                    </div>
                                    <div className="flex items-center gap-2 text-xs text-gray-500">
                                        <span>{activeThread.userId}</span>
                                        <span>•</span>
                                        <select
                                            className="bg-transparent border-none outline-none text-blue-600 font-semibold cursor-pointer p-0"
                                            value={activeThread.profile?.assignedMember?.id || ""}
                                            onChange={(e) => handleAssign(activeThread.userId, e.target.value ? parseInt(e.target.value) : null)}
                                        >
                                            <option value="">Sin Asignar</option>
                                            {teamMembers.map(m => (
                                                <option key={m.id} value={m.id}>{m.name}</option>
                                            ))}
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Messages */}
                        <div className="flex-1 overflow-y-auto p-4 space-y-4">
                            {activeThread.messages.map((msg, index) => (
                                <div
                                    key={msg.id || index}
                                    className={`flex ${msg.adminReply ? 'justify-end' : 'justify-start'}`}
                                >
                                    <div className="flex flex-col gap-1 max-w-[70%]">
                                        <div
                                            className={`p-3 rounded-lg shadow-sm relative ${msg.adminReply
                                                ? 'bg-[#d9fdd3] rounded-tr-none'
                                                : 'bg-white rounded-tl-none'
                                                }`}
                                        >
                                            <p className="text-sm text-gray-800 whitespace-pre-wrap">{msg.message}</p>
                                            <div className="flex justify-end items-center gap-1 mt-1">
                                                <span className="text-[10px] text-gray-500">
                                                    {new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                                </span>
                                                {msg.adminReply && (
                                                    <CheckCheck size={14} className="text-blue-500" />
                                                )}
                                            </div>
                                        </div>
                                        {!msg.adminReply && (
                                            <button
                                                onClick={() => handleAddToFAQ(msg.message)}
                                                className="text-xs text-blue-600 hover:text-blue-800 self-start ml-1 flex items-center gap-1"
                                            >
                                                <PlusCircle size={12} /> Agregar a FAQ
                                            </button>
                                        )}
                                    </div>
                                </div>
                            ))}
                            <div ref={messagesEndRef} />
                        </div>

                        {/* Input Area */}
                        <div className="p-4 bg-gray-100 border-t border-gray-200">
                            <div className="flex gap-2">
                                <input
                                    type="text"
                                    placeholder="Escribe un mensaje..."
                                    className="flex-1 border rounded-full px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    value={replyMessage}
                                    onChange={(e) => setReplyMessage(e.target.value)}
                                    onKeyPress={(e) => e.key === 'Enter' && handleReply()}
                                />
                                <button
                                    onClick={handleReply}
                                    disabled={!replyMessage.trim()}
                                    className="bg-blue-600 text-white p-2 rounded-full hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                                >
                                    <Send size={20} />
                                </button>
                            </div>
                        </div>
                    </>
                ) : (
                    <div className="flex-1 flex flex-col items-center justify-center text-gray-500 p-8">
                        <div className="bg-gray-200 p-6 rounded-full mb-4">
                            <MessageSquare size={48} className="text-gray-400" />
                        </div>
                        <h3 className="text-xl font-semibold mb-2">Selecciona una conversación</h3>
                        <p>Haz clic en un chat de la izquierda para ver el historial.</p>
                    </div>
                )}
            </div>

            {/* FAQ Modal */}
            {showFaqModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-lg shadow-xl w-full max-w-md p-6">
                        <div className="flex justify-between items-center mb-4">
                            <h3 className="text-lg font-bold">Crear Pregunta Frecuente</h3>
                            <button onClick={() => setShowFaqModal(false)} className="text-gray-500 hover:text-gray-700">
                                <X size={20} />
                            </button>
                        </div>
                        <div className="mb-4">
                            <label className="block text-sm font-medium text-gray-700 mb-1">Pregunta (del usuario)</label>
                            <textarea
                                className="w-full border rounded p-2 bg-gray-50"
                                rows="3"
                                value={faqData.question}
                                onChange={(e) => setFaqData({ ...faqData, question: e.target.value })}
                            />
                        </div>
                        <div className="mb-4">
                            <label className="block text-sm font-medium text-gray-700 mb-1">Respuesta (tuya)</label>
                            <textarea
                                className="w-full border rounded p-2 focus:ring-2 focus:ring-blue-500 focus:outline-none"
                                rows="4"
                                value={faqData.answer}
                                onChange={(e) => setFaqData({ ...faqData, answer: e.target.value })}
                                placeholder="Escribe la respuesta aquí..."
                                autoFocus
                            />
                        </div>
                        <div className="flex justify-end gap-2">
                            <button
                                onClick={() => setShowFaqModal(false)}
                                className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded"
                            >
                                Cancelar
                            </button>
                            <button
                                onClick={handleSaveFAQ}
                                className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                            >
                                Guardar y Cerrar
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Nickname Modal */}
            {showNicknameModal && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                    <div className="bg-white rounded-lg shadow-xl w-full max-w-sm p-6">
                        <h3 className="text-lg font-bold mb-4">Editar Nombre</h3>
                        <input
                            type="text"
                            className="w-full border p-2 rounded mb-4"
                            placeholder="Nombre / Apodo"
                            value={nicknameData.nickname}
                            onChange={(e) => setNicknameData({ ...nicknameData, nickname: e.target.value })}
                            autoFocus
                        />
                        <div className="flex justify-end gap-2">
                            <button onClick={() => setShowNicknameModal(false)} className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded">Cancelar</button>
                            <button onClick={handleNickname} className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700">Guardar</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ConsultationList;
