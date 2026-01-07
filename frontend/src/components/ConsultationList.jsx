import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { MessageSquare, CheckCircle, Circle, Reply, Send, BookPlus, X, Save } from 'lucide-react';

const ConsultationList = () => {
    const [consultations, setConsultations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [replyingTo, setReplyingTo] = useState(null);
    const [replyMessage, setReplyMessage] = useState("");
    const [faqModalOpen, setFaqModalOpen] = useState(false);
    const [faqData, setFaqData] = useState({ question: '', answer: '' });

    useEffect(() => {
        fetchConsultations();
    }, []);

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

    const toggleSeen = (id) => {
        axios.put(`/api/consultations/${id}/seen`)
            .then(res => {
                setConsultations(consultations.map(c => c.id === id ? res.data : c));
            })
            .catch(console.error);
    };

    const handleReply = (id) => {
        if (!replyMessage.trim()) return;

        axios.post(`/api/consultations/${id}/reply`, { message: replyMessage })
            .then(() => {
                setReplyingTo(null);
                setReplyMessage("");
                fetchConsultations(); // Refresh to show replied status if we add it to UI
                alert("Respuesta enviada correctamente");
            })
            .catch(err => {
                console.error(err);
                alert("Error al enviar respuesta");
            });
    };

    const openFaqModal = (consultation) => {
        setFaqData({
            question: consultation.message,
            answer: "" // We could pre-fill if there was a reply, but usually we want a generic answer
        });
        setFaqModalOpen(true);
    };

    const saveFaq = () => {
        if (!faqData.question.trim() || !faqData.answer.trim()) return;

        axios.post('/api/faqs', faqData)
            .then(() => {
                setFaqModalOpen(false);
                setFaqData({ question: '', answer: '' });
                alert("Pregunta frecuente guardada correctamente");
            })
            .catch(err => {
                console.error(err);
                alert("Error al guardar FAQ");
            });
    };

    if (loading) return <div className="p-6">Cargando...</div>;

    return (
        <div className="p-6">
            <h1 className="text-3xl font-bold mb-8 flex items-center gap-3">
                <MessageSquare /> Consultas
            </h1>

            <div className="bg-white rounded-lg shadow overflow-hidden">
                <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                        <tr>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fecha</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Contacto</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Mensaje</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
                        </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                        {consultations.map((consultation) => (
                            <tr key={consultation.id} className={consultation.seen ? "bg-white" : "bg-blue-50"}>
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <button
                                        onClick={() => toggleSeen(consultation.id)}
                                        className={`flex items-center gap-1 text-sm ${consultation.seen ? 'text-green-600' : 'text-blue-600 font-bold'}`}
                                    >
                                        {consultation.seen ? <CheckCircle size={18} /> : <Circle size={18} fill="currentColor" />}
                                        {consultation.seen ? "Visto" : "Nuevo"}
                                    </button>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    {new Date(consultation.timestamp).toLocaleDateString()} {new Date(consultation.timestamp).toLocaleTimeString()}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                                    <div className="font-medium">{consultation.contactPhone}</div>
                                    <div className="text-xs text-gray-500">ID: {consultation.userId}</div>
                                </td>
                                <td className="px-6 py-4 text-sm text-gray-700 max-w-md break-words">
                                    {consultation.message}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                    {replyingTo === consultation.id ? (
                                        <div className="flex flex-col gap-2">
                                            <textarea
                                                className="border rounded p-2 text-sm w-full"
                                                rows="3"
                                                placeholder="Escriba su respuesta..."
                                                value={replyMessage}
                                                onChange={(e) => setReplyMessage(e.target.value)}
                                            />
                                            <div className="flex gap-2">
                                                <button
                                                    onClick={() => handleReply(consultation.id)}
                                                    className="bg-blue-600 text-white px-3 py-1 rounded text-xs flex items-center gap-1 hover:bg-blue-700"
                                                >
                                                    <Send size={14} /> Enviar
                                                </button>
                                                <button
                                                    onClick={() => setReplyingTo(null)}
                                                    className="text-gray-500 px-3 py-1 text-xs hover:text-gray-700"
                                                >
                                                    Cancelar
                                                </button>
                                            </div>
                                        </div>
                                    ) : (
                                        <button
                                            onClick={() => {
                                                setReplyingTo(consultation.id);
                                                setReplyMessage("");
                                            }}
                                            className="text-blue-600 hover:text-blue-900 flex items-center gap-1"
                                        >
                                            <Reply size={16} /> Responder
                                        </button>
                                    )}
                                    <button
                                        onClick={() => openFaqModal(consultation)}
                                        className="text-purple-600 hover:text-purple-900 flex items-center gap-1 mt-2"
                                        title="Guardar como Pregunta Frecuente"
                                    >
                                        <BookPlus size={16} /> FAQ
                                    </button>
                                    {consultation.replied && <span className="text-xs text-green-600 block mt-1">Respondido</span>}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
                {consultations.length === 0 && (
                    <div className="p-8 text-center text-gray-500">
                        No hay consultas registradas.
                    </div>
                )}
            </div>

            {faqModalOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-lg">
                        <div className="flex justify-between items-center mb-4">
                            <h3 className="text-xl font-bold">Guardar como Pregunta Frecuente</h3>
                            <button onClick={() => setFaqModalOpen(false)} className="text-gray-500 hover:text-gray-700">
                                <X size={24} />
                            </button>
                        </div>
                        <div className="mb-4">
                            <label className="block text-sm font-medium text-gray-700 mb-1">Pregunta</label>
                            <input
                                type="text"
                                className="w-full border rounded p-2"
                                value={faqData.question}
                                onChange={(e) => setFaqData({ ...faqData, question: e.target.value })}
                            />
                        </div>
                        <div className="mb-4">
                            <label className="block text-sm font-medium text-gray-700 mb-1">Respuesta Genérica</label>
                            <textarea
                                className="w-full border rounded p-2"
                                rows="4"
                                value={faqData.answer}
                                onChange={(e) => setFaqData({ ...faqData, answer: e.target.value })}
                                placeholder="Escriba una respuesta general para esta pregunta..."
                            />
                        </div>
                        <div className="flex justify-end gap-2">
                            <button onClick={() => setFaqModalOpen(false)} className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded">
                                Cancelar
                            </button>
                            <button onClick={saveFaq} className="bg-purple-600 text-white px-4 py-2 rounded flex items-center gap-2 hover:bg-purple-700">
                                <Save size={18} /> Guardar FAQ
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ConsultationList;
