-- =========================================================================
-- Script DML: Datos Iniciales de Catálogo - Sistema de Gestión "CarMotors"
-- =========================================================================

USE carmotors_db;

-- 1. Catálogo: Estados de Servicio
INSERT IGNORE INTO servicio_estados (id, nombre) VALUES
(1, 'Recibido'),
(2, 'En Diagnóstico'),
(3, 'En Proceso'),
(4, 'Finalizado'),
(5, 'Cancelado');

-- 2. Catálogo: Tipos de Mantenimiento
INSERT IGNORE INTO mantenimiento_tipos (id, nombre) VALUES
(1, 'Mantenimiento Preventivo'),
(2, 'Mantenimiento Correctivo'),
(3, 'Revisión y Diagnóstico General');

-- 3. Catálogo: Tipos de Repuesto
INSERT IGNORE INTO repuesto_tipos (id, nombre) VALUES
(1, 'Frenos'),
(2, 'Motor'),
(3, 'Suspensión y Dirección'),
(4, 'Sistema Eléctrico'),
(5, 'Fluidos y Filtros');
