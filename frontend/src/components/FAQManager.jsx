import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { HelpCircle, Plus, Edit, Trash, Save, X } from 'lucide-react';

const FAQManager = () => {
    const [faqs, setFaqs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [editingId, setEditingId] = useState(null);
    const [formData, setFormData] = useState({ question: '', answer: '' });
    const [isCreating, setIsCreating] = useState(false);

    useEffect(() => {
        fetchFAQs();
    }, []);

    const fetchFAQs = () => {
        axios.get('/api/faqs')
            .then(res => {
                setFaqs(res.data);
                setLoading(false);
            })
            .catch(err => {
                console.error(err);
                setLoading(false);
            });
    };

    const handleEdit = (faq) => {
        setEditingId(faq.id);
        setFormData({ question: faq.question, answer: faq.answer });
        setIsCreating(false);
    };

    const handleDelete = (id) => {
        if (window.confirm('¿Está seguro de eliminar esta pregunta frecuente?')) {
            axios.delete(`/api/faqs/${id}`)
                .then(() => fetchFAQs())
                .catch(console.error);
        }
    };

    const handleSave = () => {
        if (!formData.question.trim() || !formData.answer.trim()) return;

        if (editingId) {
            axios.put(`/api/faqs/${editingId}`, formData)
                .then(() => {
                    setEditingId(null);
                    setFormData({ question: '', answer: '' });
                    fetchFAQs();
                })
                .catch(console.error);
        } else {
            axios.post('/api/faqs', formData)
                .then(() => {
                    setIsCreating(false);
                    setFormData({ question: '', answer: '' });
                    fetchFAQs();
                })
                .catch(console.error);
        }
    };

    const handleCancel = () => {
        setEditingId(null);
        setIsCreating(false);
        setFormData({ question: '', answer: '' });
    };

    if (loading) return <div className="p-6">Cargando...</div>;

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-8">
                <h1 className="text-3xl font-bold flex items-center gap-3">
                    <HelpCircle /> Preguntas Frecuentes
                </h1>
                <button
                    onClick={() => { setIsCreating(true); setEditingId(null); setFormData({ question: '', answer: '' }); }}
                    className="bg-blue-600 text-white px-4 py-2 rounded flex items-center gap-2 hover:bg-blue-700"
                >
                    <Plus size={20} /> Nueva Pregunta
                </button>
            </div>

            {isCreating && (
                <div className="bg-white p-6 rounded-lg shadow mb-6 border border-blue-200">
                    <h3 className="text-lg font-bold mb-4">Nueva Pregunta Frecuente</h3>
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-gray-700 mb-1">Pregunta</label>
                        <input
                            type="text"
                            className="w-full border rounded p-2"
                            value={formData.question}
                            onChange={(e) => setFormData({ ...formData, question: e.target.value })}
                        />
                    </div>
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-gray-700 mb-1">Respuesta</label>
                        <textarea
                            className="w-full border rounded p-2"
                            rows="4"
                            value={formData.answer}
                            onChange={(e) => setFormData({ ...formData, answer: e.target.value })}
                        />
                    </div>
                    <div className="flex gap-2">
                        <button onClick={handleSave} className="bg-green-600 text-white px-4 py-2 rounded flex items-center gap-2 hover:bg-green-700">
                            <Save size={18} /> Guardar
                        </button>
                        <button onClick={handleCancel} className="bg-gray-500 text-white px-4 py-2 rounded flex items-center gap-2 hover:bg-gray-600">
                            <X size={18} /> Cancelar
                        </button>
                    </div>
                </div>
            )}

            <div className="grid gap-4">
                {faqs.map((faq) => (
                    <div key={faq.id} className="bg-white p-6 rounded-lg shadow">
                        {editingId === faq.id ? (
                            <div>
                                <div className="mb-4">
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Pregunta</label>
                                    <input
                                        type="text"
                                        className="w-full border rounded p-2"
                                        value={formData.question}
                                        onChange={(e) => setFormData({ ...formData, question: e.target.value })}
                                    />
                                </div>
                                <div className="mb-4">
                                    <label className="block text-sm font-medium text-gray-700 mb-1">Respuesta</label>
                                    <textarea
                                        className="w-full border rounded p-2"
                                        rows="4"
                                        value={formData.answer}
                                        onChange={(e) => setFormData({ ...formData, answer: e.target.value })}
                                    />
                                </div>
                                <div className="flex gap-2">
                                    <button onClick={handleSave} className="bg-green-600 text-white px-3 py-1 rounded flex items-center gap-1 hover:bg-green-700 text-sm">
                                        <Save size={16} /> Guardar
                                    </button>
                                    <button onClick={handleCancel} className="bg-gray-500 text-white px-3 py-1 rounded flex items-center gap-1 hover:bg-gray-600 text-sm">
                                        <X size={16} /> Cancelar
                                    </button>
                                </div>
                            </div>
                        ) : (
                            <div>
                                <div className="flex justify-between items-start mb-2">
                                    <h3 className="text-xl font-semibold text-gray-800">{faq.question}</h3>
                                    <div className="flex gap-2">
                                        <button onClick={() => handleEdit(faq)} className="text-blue-600 hover:text-blue-800">
                                            <Edit size={18} />
                                        </button>
                                        <button onClick={() => handleDelete(faq.id)} className="text-red-600 hover:text-red-800">
                                            <Trash size={18} />
                                        </button>
                                    </div>
                                </div>
                                <p className="text-gray-600 whitespace-pre-wrap">{faq.answer}</p>
                            </div>
                        )}
                    </div>
                ))}
                {faqs.length === 0 && !isCreating && (
                    <p className="text-center text-gray-500 py-8">No hay preguntas frecuentes cargadas.</p>
                )}
            </div>
        </div>
    );
};

export default FAQManager;
