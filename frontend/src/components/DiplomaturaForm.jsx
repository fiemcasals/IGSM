import React, { useState, useEffect } from 'react';

const DiplomaturaForm = ({ diplomatura, onSave, onCancel }) => {
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        content: ''
    });

    useEffect(() => {
        if (diplomatura) {
            setFormData(diplomatura);
        }
    }, [diplomatura]);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSave(formData);
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4">
            <div className="bg-white rounded-lg p-6 w-full max-w-2xl">
                <h2 className="text-2xl font-bold mb-4">{diplomatura ? 'Editar' : 'Nueva'} Diplomatura</h2>
                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label className="block text-sm font-medium mb-1">Nombre</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            className="w-full border rounded p-2"
                            required
                        />
                    </div>
                    <div className="mb-4">
                        <label className="block text-sm font-medium mb-1">Tipo</label>
                        <select
                            name="type"
                            value={formData.type || 'DIPLOMATURA'}
                            onChange={handleChange}
                            className="w-full border rounded p-2"
                        >
                            <option value="DIPLOMATURA">Diplomatura</option>
                            <option value="TECNICATURA">Tecnicatura</option>
                            <option value="LICENCIATURA">Licenciatura</option>
                            <option value="PROFESORADO">Profesorado</option>
                            <option value="OTROS">Otros</option>
                        </select>
                    </div>
                    <div className="mb-4">
                        <label className="block text-sm font-medium mb-1">Descripción (Corta)</label>
                        <input
                            type="text"
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                            className="w-full border rounded p-2"
                        />
                    </div>
                    <div className="mb-4">
                        <label className="block text-sm font-medium mb-1">Contenido (Respuesta del Bot)</label>
                        <textarea
                            name="content"
                            value={formData.content}
                            onChange={handleChange}
                            className="w-full border rounded p-2 h-32"
                            required
                        />
                    </div>
                    <div className="flex justify-end gap-2">
                        <button type="button" onClick={onCancel} className="px-4 py-2 bg-gray-200 rounded">Cancelar</button>
                        <button type="submit" className="px-4 py-2 bg-blue-600 text-white rounded">Guardar</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default DiplomaturaForm;
