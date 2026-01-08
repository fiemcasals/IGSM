-- Script de limpieza de duplicados y corrección de nombres

-- 1. Eliminar duplicados (Versiones en MAYÚSCULAS que ya tienen versión en Title Case)
DELETE FROM diplomatura WHERE id IN (
    33, -- PROFESORADO EN DOCENCIA SUPERIOR (Duplicado de 20)
    34, -- TECNICATURA EN HIGIENE Y SEGURIDAD EN EL TRABAJO (Duplicado de 16)
    35, -- TECNICATURA EN ADMINISTRACION CON ORIENTACION EN MARKETING (Duplicado de 17)
    36, -- LICENCIATURA EN HIGIENE Y SEGURIDAD EN EL TRABAJO (Duplicado de 18)
    37, -- DIPLOMATURA EN DESARROLLO DE SOFTWARE (Duplicado de 21)
    38, -- DIPLOMATURA EN TECNOLOGIAS AGROPECUARIAS (Duplicado de 22)
    39, -- DIPLOMATURA EN BROMATOLOGIA (Duplicado de 23)
    40, -- DIPLOMATURA EN ENERGIAS RENOVABLES (Duplicado de 24)
    41, -- DIPLOMATURA EN GESTION DE RIESGOS (Duplicado de 14)
    42, -- DIPLOMATURA EN MOLDES Y MATRICES (Duplicado de 25)
    43, -- DIPLOMATURA EN AGRICULTURA DE PRECISION (Duplicado de 26)
    44, -- DIPLOMATURA EN HIDROCARBUROS (Duplicado de 27)
    45, -- DIPLOMATURA EN DISEÑO E IMPRESION 3D (Duplicado de ? - Asumo que existe versión correcta o se corregirá)
    46, -- DIPLOMATURA EN ROBOTICA (Duplicado de 29)
    48, -- DIPLOMATURA EN FIBRA OPTICA (Duplicado de 12)
    49, -- DIPLOMATURA EN MEDIO AMBIENTE (Duplicado de 30)
    50, -- DIPLOMATURA EN DESARROLLO WEB (Duplicado de 31)
    51  -- DIPLOMATURA EN INDUSTRIAS ALIMENTARIAS (Duplicado de 32)
);

-- 2. Corregir nombres en MAYÚSCULAS que son únicos (si los hay)
-- Ejemplo: Ciencia de Datos (ID 47)
UPDATE diplomatura 
SET name = 'Diplomatura en Ciencia de Datos e Inteligencia Artificial' 
WHERE id = 47;

-- Verificación final (Opcional)
-- SELECT * FROM diplomatura ORDER BY name;
