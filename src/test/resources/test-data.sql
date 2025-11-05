-- Insertar roles de prueba con IDs específicos (usa MERGE para evitar duplicados)
MERGE INTO rol (idrol, descripcion) KEY(idrol) VALUES (1, 'Administrador');
MERGE INTO rol (idrol, descripcion) KEY(idrol) VALUES (2, 'Gestor');
MERGE INTO rol (idrol, descripcion) KEY(idrol) VALUES (3, 'Usuario');

-- Insertar estados de prueba con IDs específicos (usa MERGE para evitar duplicados)
MERGE INTO estado (idestado, descripcion) KEY(idestado) VALUES (1, 'Pendiente');
MERGE INTO estado (idestado, descripcion) KEY(idestado) VALUES (2, 'En proceso');
MERGE INTO estado (idestado, descripcion) KEY(idestado) VALUES (3, 'Resuelta');
MERGE INTO estado (idestado, descripcion) KEY(idestado) VALUES (4, 'Cerrada');
MERGE INTO estado (idestado, descripcion) KEY(idestado) VALUES (5, 'Anulada');

-- Insertar tipos de PQRS con IDs específicos (usa MERGE para evitar duplicados)
MERGE INTO tipo (idtipo, descripcion) KEY(idtipo) VALUES (1, 'Queja');
MERGE INTO tipo (idtipo, descripcion) KEY(idtipo) VALUES (2, 'Reclamo');
MERGE INTO tipo (idtipo, descripcion) KEY(idtipo) VALUES (3, 'Sugerencia');
MERGE INTO tipo (idtipo, descripcion) KEY(idtipo) VALUES (4, 'Petición');
