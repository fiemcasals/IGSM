import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Pencil, Trash2, Plus } from 'lucide-react';
import DiplomaturaForm from './DiplomaturaForm';

const DiplomaturaList = () => {
    const [diplomaturas, setDiplomaturas] = useState([]);
    const [editingDiplo, setEditingDiplo] = useState(null);
    const [isFormOpen, setIsFormOpen] = useState(false);

    const fetchDiplomaturas = () => {
        axios.get('/api/diplomaturas')
            .then(res => setDiplomaturas(res.data))
            .catch(err => console.error(err));
    };

    useEffect(() => {
        fetchDiplomaturas();
    }, []);

    const handleSave = (diplo) => {
        if (diplo.id) {
            axios.put(`/api/diplomaturas/${diplo.id}`, diplo)
                .then(() => {
                    fetchDiplomaturas();
                    setIsFormOpen(false);
                    setEditingDiplo(null);
                });
        } else {
            axios.post('/api/diplomaturas', diplo)
                .then(() => {
                    fetchDiplomaturas();
                    setIsFormOpen(false);
                });
        }
    };

    const handleDelete = (id) => {
        if (window.confirm('¿Estás seguro de eliminar esta diplomatura?')) {
            axios.delete(`/api/diplomaturas/${id}`)
                .then(() => fetchDiplomaturas());
        }
    };

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-3xl font-bold">Gestión de Diplomaturas</h1>
                <button
                    onClick={() => { setEditingDiplo(null); setIsFormOpen(true); }}
                    className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
                >
                    <Plus size={20} /> Nueva Diplomatura
                </button>
            </div>

            <div className="bg-white rounded-lg shadow overflow-x-auto">
                <table className="w-full">
                    <thead className="bg-gray-50">
                        <tr>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Nombre</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Descripción</th>
                            <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                        {diplomaturas.map((diplo) => (
                            <tr key={diplo.id}>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{diplo.id}</td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{diplo.name}</td>
                                <td className="px-6 py-4 text-sm text-gray-500">{diplo.description}</td>
                                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                    <button
                                        onClick={() => { setEditingDiplo(diplo); setIsFormOpen(true); }}
                                        className="text-indigo-600 hover:text-indigo-900 mr-4"
                                    >
                                        <Pencil size={18} />
                                    </button>
                                    <button
                                        onClick={() => handleDelete(diplo.id)}
                                        className="text-red-600 hover:text-red-900"
                                    >
                                        <Trash2 size={18} />
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {isFormOpen && (
                <DiplomaturaForm
                    diplomatura={editingDiplo}
                    onSave={handleSave}
                    onCancel={() => setIsFormOpen(false)}
                />
            )}
        </div>
    );
};

export default DiplomaturaList;
