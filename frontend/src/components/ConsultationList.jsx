import React, { useEffect, useState, useRef } from 'react';
import axios from 'axios';
import { MessageSquare, Send, Search, User, Check, CheckCheck, PlusCircle, X } from 'lucide-react';

const ConsultationList = () => {
    const [consultations, setConsultations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedUser, setSelectedUser] = useState(null);
    const [replyMessage, setReplyMessage] = useState("");
    const [searchTerm, setSearchTerm] = useState("");

    // FAQ Modal State
    const [showFaqModal, setShowFaqModal] = useState(false);
    const [faqData, setFaqData] = useState({ question: "", answer: "" });

    const messagesEndRef = useRef(null);

    useEffect(() => {
        fetchConsultations();
        // Poll for new messages every 10 seconds
        const interval = setInterval(fetchConsultations, 10000);
        return () => clearInterval(interval);
    }, []);

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

    // Group consultations by user
    const threads = consultations.reduce((acc, curr) => {
        if (!acc[curr.userId]) {
            acc[curr.userId] = {
                userId: curr.userId,
                contactPhone: curr.contactPhone,
                messages: [],
                lastMessage: null,
                unreadCount: 0
            };
        }
        acc[curr.userId].messages.push(curr);

        if (!curr.seen && !curr.adminReply) {
            acc[curr.userId].unreadCount++;
        }
        return acc;
    }, {});

    // Convert to array and sort by latest message
    const sortedThreads = Object.values(threads).map(thread => {
        thread.messages.sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));
        thread.lastMessage = thread.messages[thread.messages.length - 1];
        return thread;
    }).sort((a, b) => new Date(b.lastMessage.timestamp) - new Date(a.lastMessage.timestamp));

    const filteredThreads = sortedThreads.filter(thread =>
        thread.contactPhone?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        thread.userId.toLowerCase().includes(searchTerm.toLowerCase())
    );

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
                                <span className="font-semibold text-gray-800">{thread.contactPhone || thread.userId}</span>
                                <span className="text-xs text-gray-500">
                                    {new Date(thread.lastMessage.timestamp).toLocaleDateString()}
                                </span>
                            </div>
                            <div className="flex justify-between items-center">
                                <p className="text-sm text-gray-600 truncate w-3/4">
                                    {thread.lastMessage.adminReply && <span className="text-blue-600 mr-1">Tú:</span>}
                                    {thread.lastMessage.message}
                                </p>
                                {thread.unreadCount > 0 && (
                                    <span className="bg-green-500 text-white text-xs font-bold px-2 py-1 rounded-full">
                                        {thread.unreadCount}
                                    </span>
                                )}
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
                                    <h3 className="font-bold text-gray-800">{activeThread.contactPhone}</h3>
                                    <span className="text-xs text-gray-500">{activeThread.userId}</span>
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
        </div>
    );
};

export default ConsultationList;
