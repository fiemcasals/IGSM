-- Script para actualizar las diplomaturas/carreras
-- Ejecutar en la base de datos PostgreSQL

-- 1. Limpiar tabla existente (CRÍTICO: Esto borra las que sobran)
TRUNCATE TABLE diplomaturas CASCADE;

-- 2. Insertar TODAS las carreras (Profesorado, Tecnicaturas, Licenciaturas y Diplomaturas)
-- NOTA: Usamos E'...' para que los \n se interpreten como saltos de línea reales.

-- PROFESORADO
INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Profesorado en Docencia Superior',
    'PROFESORADO',
    'Ciclo de complementación pedagógica curricular.',
    E'Carrera: Profesorado en Docencia Superior\nDescripción: Ciclo de complementación pedagógica curricular. Conforme la Resolución N° 12767/97, el título que se otorga, habilita ingresar al sistema educativo público de la Provincia de Buenos Aires, facultando a los inscriptos/as a tomar cargos/módulos y/o horas cátedra en las instituciones educativas bonaerenses. Permite el ejercicio de la docencia en el listado oficial o 108A.\n📋 Requisitos: Poseer título de educación superior (detalles en el link)\n🎓Certificación: UTN\n⏱️ Duración: 20 meses\n📚 Modalidad: Semipresencial: 1 sábado presencial al mes, el resto de las actividades es mediante campus virtual en modalidad asincrónica.\n📌Matrícula de Inscripción: $85.000 (hasta el 31/01/2026)\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/profesorado-en-docencia-superior/'
);

-- TECNICATURAS
INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Tecnicatura en Higiene y Seguridad en el Trabajo',
    'TECNICATURA',
    'Organización, planificación y gestión de seguridad laboral.',
    E'Carrera: Tecnicatura en Higiene y Seguridad en el Trabajo\nDescripción: Organización, la planificación y organización de actividades, el diseño, la gestión de los recursos de los servicios, la evaluación y control y la capacitación en aspectos inherentes a la higiene y seguridad en el trabajo\n📋 Requisitos: Título secundario completo\n🎓Certificación: ISM\n⏱️ Duración: 3 años\n📚 Modalidad: Presencial.\n📌Matrícula de Inscripción: $115.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/tecnicatura-superior-en-seguridad-e-higiene/'
);

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Tecnicatura Superior en Marketing',
    'TECNICATURA',
    'Tecnicatura Superior en Marketing',
    E'Carrera: Tecnicatura Superior en Marketing\nObjetivo/Perfil profesional/descripción:\n📋 Requisito: Título secundario completo\n🎓Certificación: ISM\n⏱️ Duración: 3 años\n📚 Modalidad: Presencial\n📌Matrícula de Inscripción: $115.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/tecnicatura-superior-en-marketing/'
);

-- LICENCIATURAS
INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Licenciatura en Higiene y Seguridad en el Trabajo',
    'LICENCIATURA',
    'Licenciatura en Higiene y Seguridad en el Trabajo',
    E'Carrera: Licenciatura en Higiene y Seguridad en el Trabajo\nDescripción: \n📋 Requisito: Título de Técnico Superior en Higiene y Seguridad.\n🎓Certificación: UTN\n⏱️ Duración: 34 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción:\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/licenciatura-en-higiene-y-seguridad/'
);

-- DIPLOMATURAS
INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Desarrollo de Software',
    'DIPLOMATURA',
    'Formación práctica en desarrollo de software.',
    E'Carrera: Diplomatura en Desarrollo de Software\nDescripción: Formación práctica en desarrollo de software, estructuras de datos y algoritmos.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomatura-en-desarrollo-de-software/'
);

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Tecnologías Agropecuarias',
    'DIPLOMATURA',
    'Aplicar robótica y automatización en el agro.',
    E'Carrera: Diplomatura en Tecnologías Agropecuarias\nDescripción: Aplicar robótica y automatización para mejorar productividad y sostenibilidad agrícola.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: \nhttps://ism.edu.ar/diplomatura-en-tecnologias-agropecuarias/'
);

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Energías Renovables',
    'DIPLOMATURA',
    'Formación en energías renovables.',
    E'Carrera: Diplomatura en Energías Renovables\nDescripción: Formación científico-tecnológica para la inserción laboral en el sector de energías renovables.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera:\nhttps://ism.edu.ar/diplomatura-en-energias-renovables/'
);

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Gestión de Riesgos',
    'DIPLOMATURA',
    'Diplomatura en Gestión de Riesgos',
    E'Carrera: Diplomatura en Gestión de Riesgos\nDescripción:\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera:\nhttps://ism.edu.ar/diplomatura-en-gestion-de-riesgo/'
);

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Robótica',
    'DIPLOMATURA',
    'Diseño y programación de sistemas robóticos.',
    E'Carrera: Diplomatura en Robótica\nDescripción: Diseño, programación e implementación de sistemas robóticos y automatización.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomatura-en-robotica/'
);

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Medio Ambiente',
    'DIPLOMATURA',
    'Detectar y diagnosticar problemas ambientales.',
    E'Carrera: Diplomatura en Medio Ambiente\nDescripción: Detectar y diagnosticar problemas ambientales, preservación sustentable de recursos.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomatura-en-medio-ambiente/'
);

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Desarrollo Web',
    'DIPLOMATURA',
    'Desarrollo de soluciones web modernas.',
    E'Carrera: Diplomatura en Desarrollo Web\nDescripción: Formar recursos capacitados en el desarrollo de soluciones web, capaces de crear, implementar y optimizar aplicaciones y sitios web modernos, utilizando tecnologías y metodologías actuales en el campo del desarrollo web. \n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomatura-en-desarrollo-web/'
);

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Industrias Alimentarias',
    'DIPLOMATURA',
    'Producción de alimentos y control de calidad.',
    E'Carrera: Diplomatura en Industrias Alimentarias\nDescripción: Brindar una formación técnica y práctica en la producción de alimentos, el control de procesos y la aplicación de normas de calidad vigentes.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomatura-en-industrias-alimentarias/'
);

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Agricultura de Precisión',
    'DIPLOMATURA',
    'Tecnologías avanzadas para el agro.',
    E'Carrera: Diplomatura en Agricultura de Precisión\nDescripción: Aplicar tecnologías avanzadas para optimizar recursos y productividad agropecuaria.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción:$85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomatura-en-agricultura-de-precision/'
);
