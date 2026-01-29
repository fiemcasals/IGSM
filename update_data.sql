-- Script para actualizar las diplomaturas/carreras
-- Ejecutar en la base de datos PostgreSQL

-- 1. Limpiar tabla existente
TRUNCATE TABLE diplomatura CASCADE;

-- 2. Insertar nuevos registros

-- PROFESORADO
INSERT INTO diplomatura (name, type, description, content) VALUES (
    'Profesorado en Docencia Superior',
    'PROFESORADO',
    'Ciclo de complementación pedagógica curricular.',
    'Carrera: Profesorado en Docencia Superior\nObjetivo/Perfil profesional/descripción: Ciclo de complementación pedagógica curricular. Conforme la Resolución N° 12767/97, el título que se otorga, habilita ingresar al sistema educativo público de la Provincia de Buenos Aires, facultando a los inscriptos/as a tomar cargos/módulos y/o horas cátedra en las instituciones educativas bonaerenses. Permite el ejercicio de la docencia en el listado oficial o 108A.\n📋 Requisitos: Poseer título de educación superior emitido por una institución educativa reconocida oficialmente que certifique el egreso de una carrera cuya trayectoria formativa conste de una carga horaria mínima de mil ochocientas (1.800) horas reloj y dos y medio (2 1/2) años de duración (mínimo).\n🎓Certificación: UTN\n⏱️ Duración: 20 meses\n📚 Modalidad: Semipresencial: 1 sábado presencial al mes, el resto de las actividades es mediante campus virtual en modalidad asincrónica.\n📌Matrícula de Inscripción: $85.000 (hasta el 31/01/2026)\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/profesorado-en-docencia-superior/'
);

-- TECNICATURAS
INSERT INTO diplomatura (name, type, description, content) VALUES (
    'Tecnicatura en Higiene y Seguridad en el Trabajo',
    'TECNICATURA',
    'Organización, planificación y gestión de seguridad laboral.',
    'Carrera: Tecnicatura en Higiene y Seguridad en el Trabajo\nDescripción breve: organización, la planificación y organización de actividades, el diseño, la gestión de los recursos de los servicios, la evaluación y control y la capacitación en aspectos inherentes a la higiene y seguridad en el trabajo\n📋 Requisitos: Título secundario completo\n🎓Certificación: ISM\n⏱️ Duración: 3 años\n📚 Modalidad: Presencial.\n📌Matrícula de Inscripción: $115.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/tecnicatura-superior-en-seguridad-e-higiene/'
);

INSERT INTO diplomatura (name, type, description, content) VALUES (
    'Tecnicatura Superior en Marketing',
    'TECNICATURA',
    'Tecnicatura Superior en Marketing',
    'Carrera: Tecnicatura Superior en Marketing\nObjetivo/Perfil profesional/descripción:\n📋 Requisitos: Título secundario completo\n🎓Certificación: ISM\n⏱️ Duración: 3 años\n📚 Modalidad: Presencial ?\n📌Matrícula de Inscripción: $115.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/tecnicatura-superior-en-marketing/'
);

-- LICENCIATURAS
INSERT INTO diplomatura (name, type, description, content) VALUES (
    'Licenciatura en Higiene y Seguridad en el Trabajo',
    'LICENCIATURA',
    'Licenciatura en Higiene y Seguridad en el Trabajo',
    'Carrera: Licenciatura en Higiene y Seguridad en el Trabajo\nObjetivo/Perfil profesional/descripción:\n📋 Requisitos: \n🎓Certificación: UTN\n⏱️ Duración: 34 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción:\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/licenciatura-en-higiene-y-seguridad/'
);

-- DIPLOMATURAS
INSERT INTO diplomatura (name, type, description, content) VALUES (
    'Diplomatura en Desarrollo de Software',
    'DIPLOMATURA',
    'Formación práctica en desarrollo de software.',
    'Carrera: Diplomatura en Desarrollo de Software\nObjetivo/Perfil profesional/descripción: Formación práctica en desarrollo de software, estructuras de datos y algoritmos.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomatura-en-desarrollo-de-software/'
);

INSERT INTO diplomatura (name, type, description, content) VALUES (
    'Diplomatura en Tecnologías Agropecuarias',
    'DIPLOMATURA',
    'Aplicar robótica y automatización en el agro.',
    'Carrera: Diplomatura en Tecnologías Agropecuarias\nObjetivo/Perfil profesional/descripción: Aplicar robótica y automatización para mejorar productividad y sostenibilidad agrícola.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: '
);

INSERT INTO diplomatura (name, type, description, content) VALUES (
    'Diplomatura en Energías Renovables',
    'DIPLOMATURA',
    'Formación en energías renovables.',
    'Carrera: Diplomatura en Energías Renovables\nObjetivo/Perfil profesional/descripción: Formación científico-tecnológica para la inserción laboral en el sector de energías renovables.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera:'
);

INSERT INTO diplomatura (name, type, description, content) VALUES (
    'Diplomatura en Gestión de Riesgos',
    'DIPLOMATURA',
    'Diplomatura en Gestión de Riesgos',
    'Carrera: Diplomatura en Gestión de Riesgos\nObjetivo/Perfil profesional/descripción:\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomatura-en-gestion-de-riesgo/'
);

INSERT INTO diplomatura (name, type, description, content) VALUES (
    'Diplomatura en Robótica',
    'DIPLOMATURA',
    'Diseño y programación de sistemas robóticos.',
    'Carrera: Diplomatura en Robótica\nObjetivo/Perfil profesional/descripción: Diseño, programación e implementación de sistemas robóticos y automatización.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomatura-en-robotica/'
);

INSERT INTO diplomatura (name, type, description, content) VALUES (
    'Diplomatura en Medio Ambiente',
    'DIPLOMATURA',
    'Detectar y diagnosticar problemas ambientales.',
    'Carrera: Diplomatura en Medio Ambiente\nObjetivo/Perfil profesional/descripción: Detectar y diagnosticar problemas ambientales, preservación sustentable de recursos.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomatura-en-medio-ambiente/'
);

INSERT INTO diplomatura (name, type, description, content) VALUES (
    'Diplomatura en Desarrollo Web',
    'DIPLOMATURA',
    'Desarrollo de soluciones web modernas.',
    'Carrera: Diplomatura en Desarrollo Web\nObjetivo/Perfil profesional/descripción: Formar recursos capacitados en el desarrollo de soluciones web, capaces de crear, implementar y optimizar aplicaciones y sitios web modernos, utilizando tecnologías y metodologías actuales en el campo del desarrollo web. \n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomatura-en-desarrollo-web/'
);

INSERT INTO diplomatura (name, type, description, content) VALUES (
    'Diplomatura en Industrias Alimentarias',
    'DIPLOMATURA',
    'Producción de alimentos y control de calidad.',
    'Carrera: Diplomatura en Industrias Alimentarias\nObjetivo/Perfil profesional/descripción: Brindar una formación técnica y práctica en la producción de alimentos, el control de procesos y la aplicación de normas de calidad vigentes.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomatura-en-industrias-alimentarias/'
);

INSERT INTO diplomatura (name, type, description, content) VALUES (
    'Diplomatura en Agricultura de Precisión',
    'DIPLOMATURA',
    'Tecnologías avanzadas para el agro.',
    'Carrera: Diplomatura en Agricultura de Precisión\nObjetivo/Perfil profesional/descripción: Aplicar tecnologías avanzadas para optimizar recursos y productividad agropecuaria.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción:$85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/'
);
