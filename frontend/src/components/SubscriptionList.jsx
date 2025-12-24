import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { FileText, Download } from 'lucide-react';

const SubscriptionList = () => {
    const [subscriptions, setSubscriptions] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        axios.get('/api/stats/subscriptions')
            .then(res => {
                setSubscriptions(res.data);
                setLoading(false);
            })
            .catch(err => {
                console.error(err);
                setLoading(false);
            });
    }, []);

    const groupedSubscriptions = {
        DIPLOMATURA: subscriptions.filter(s => s.diplomaturaType === 'DIPLOMATURA'),
        TECNICATURA: subscriptions.filter(s => s.diplomaturaType === 'TECNICATURA'),
        LICENCIATURA: subscriptions.filter(s => s.diplomaturaType === 'LICENCIATURA'),
    };

    const renderTable = (title, subs) => (
        <div className="bg-white p-6 rounded-lg shadow mb-8">
            <h2 className="text-2xl font-bold mb-4 border-b pb-2">{title} ({subs.length})</h2>
            {subs.length === 0 ? (
                <p className="text-gray-500">No hay inscripciones registradas.</p>
            ) : (
                <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-50">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fecha</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Carrera</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Nombre</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">DNI</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Contacto</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Archivo</th>
                            </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                            {subs.map((sub) => (
                                <tr key={sub.id}>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                        {new Date(sub.timestamp).toLocaleDateString()}
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                        {sub.diplomaturaName}
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                        {sub.name} {sub.surname}
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                        {sub.dni}
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                        <div>{sub.mail}</div>
                                        <div>{sub.phone}</div>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                        {sub.fileUrl ? (
                                            <a
                                                href={sub.fileUrl}
                                                target="_blank"
                                                rel="noopener noreferrer"
                                                className="text-blue-600 hover:text-blue-900 flex items-center gap-1"
                                            >
                                                <FileText size={16} /> Ver Archivo
                                            </a>
                                        ) : (
                                            <span className="text-gray-400">-</span>
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );

    if (loading) return <div className="p-6">Cargando...</div>;

    return (
        <div className="p-6">
            <h1 className="text-3xl font-bold mb-8">Listado de Inscripciones</h1>
            {renderTable('Licenciaturas', groupedSubscriptions.LICENCIATURA)}
            {renderTable('Tecnicaturas', groupedSubscriptions.TECNICATURA)}
            {renderTable('Diplomaturas', groupedSubscriptions.DIPLOMATURA)}
        </div>
    );
};

export default SubscriptionList;
