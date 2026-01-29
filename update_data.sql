-- Script para actualizar las diplomaturass/carreras
-- Ejecutar en la base de datos PostgreSQL

-- 1. Limpiar tabla existente (CRÍTICO: Esto borra las que sobran)
TRUNCATE TABLE diplomaturas CASCADE;

-- 2. Insertar SOLO las 9 diplomaturass indicadas

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Desarrollo de Software',
    'DIPLOMATURA',
    'Formación práctica en desarrollo de software.',
    'Carrera: Diplomatura en Desarrollo de Software\nObjetivo/Perfil profesional/descripción: Formación práctica en desarrollo de software, estructuras de datos y algoritmos.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomaturas-en-desarrollo-de-software/'
);

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Tecnologías Agropecuarias',
    'DIPLOMATURA',
    'Aplicar robótica y automatización en el agro.',
    'Carrera: Diplomatura en Tecnologías Agropecuarias\nObjetivo/Perfil profesional/descripción: Aplicar robótica y automatización para mejorar productividad y sostenibilidad agrícola.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: '
);

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Energías Renovables',
    'DIPLOMATURA',
    'Formación en energías renovables.',
    'Carrera: Diplomatura en Energías Renovables\nObjetivo/Perfil profesional/descripción: Formación científico-tecnológica para la inserción laboral en el sector de energías renovables.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera:'
);

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Gestión de Riesgos',
    'DIPLOMATURA',
    'Diplomatura en Gestión de Riesgos',
    'Carrera: Diplomatura en Gestión de Riesgos\nObjetivo/Perfil profesional/descripción:\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomaturas-en-gestion-de-riesgo/'
);

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Robótica',
    'DIPLOMATURA',
    'Diseño y programación de sistemas robóticos.',
    'Carrera: Diplomatura en Robótica\nObjetivo/Perfil profesional/descripción: Diseño, programación e implementación de sistemas robóticos y automatización.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomaturas-en-robotica/'
);

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Medio Ambiente',
    'DIPLOMATURA',
    'Detectar y diagnosticar problemas ambientales.',
    'Carrera: Diplomatura en Medio Ambiente\nObjetivo/Perfil profesional/descripción: Detectar y diagnosticar problemas ambientales, preservación sustentable de recursos.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomaturas-en-medio-ambiente/'
);

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Desarrollo Web',
    'DIPLOMATURA',
    'Desarrollo de soluciones web modernas.',
    'Carrera: Diplomatura en Desarrollo Web\nObjetivo/Perfil profesional/descripción: Formar recursos capacitados en el desarrollo de soluciones web, capaces de crear, implementar y optimizar aplicaciones y sitios web modernos, utilizando tecnologías y metodologías actuales en el campo del desarrollo web. \n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomaturas-en-desarrollo-web/'
);

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Industrias Alimentarias',
    'DIPLOMATURA',
    'Producción de alimentos y control de calidad.',
    'Carrera: Diplomatura en Industrias Alimentarias\nObjetivo/Perfil profesional/descripción: Brindar una formación técnica y práctica en la producción de alimentos, el control de procesos y la aplicación de normas de calidad vigentes.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción: $85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/diplomaturas-en-industrias-alimentarias/'
);

INSERT INTO diplomaturas (name, type, description, content) VALUES (
    'Diplomatura en Agricultura de Precisión',
    'DIPLOMATURA',
    'Tecnologías avanzadas para el agro.',
    'Carrera: Diplomatura en Agricultura de Precisión\nObjetivo/Perfil profesional/descripción: Aplicar tecnologías avanzadas para optimizar recursos y productividad agropecuaria.\n📋 Requisitos: Título secundario completo.Se exceptúa de este requisito a los aspirantes mayores de 25 años, de acuerdo con la normativa establecida.\n🎓Certificación: UTN\n⏱️ Duración: 10 meses\n📚 Modalidad: Semipresencial\n📌Matrícula de Inscripción:$85.000 hasta el 28/02/26\n📌Cuotas: mensuales, correspondientes a la duración de la carrera. Valores 2026 a confirmar.\n🖊️Si desea inscribirse, haga click en el enlace para completar la solicitud de inscripción: https://forms.gle/HmVfwEE2AZMo97ax9\n🌐Para conocer más detalles de la carrera: https://ism.edu.ar/'
);
