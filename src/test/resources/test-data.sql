-- Insertar roles de prueba con IDs específicos (usa MERGE para evitar duplicados)
MERGE INTO rol (idrol, descripcion) KEY(idrol) VALUES (1, 'Administrador');
MERGE INTO rol (idrol, descripcion) KEY(idrol) VALUES (2, 'Gestor');
MERGE INTO rol (idrol, descripcion) KEY(idrol) VALUES (3, 'Usuario');

-- Insertar estados de prueba con IDs específicos (usa MERGE para evitar duplicados)
MERGE INTO estado (idestado, descripcion) KEY(idestado) VALUES (1, 'PENDIENTE');
MERGE INTO estado (idestado, descripcion) KEY(idestado) VALUES (2, 'RESPONDIDO');
MERGE INTO estado (idestado, descripcion) KEY(idestado) VALUES (3, 'CERRADO');
MERGE INTO estado (idestado, descripcion) KEY(idestado) VALUES (4, 'ESPECIAL');

-- Insertar tipos de PQRS con IDs específicos (usa MERGE para evitar duplicados)
MERGE INTO tipo (idtipo, descripcion) KEY(idtipo) VALUES (1, 'PETICION');
MERGE INTO tipo (idtipo, descripcion) KEY(idtipo) VALUES (2, 'QUEJA');
MERGE INTO tipo (idtipo, descripcion) KEY(idtipo) VALUES (3, 'RECLAMO');
MERGE INTO tipo (idtipo, descripcion) KEY(idtipo) VALUES (4, 'SUGERENCIA');
