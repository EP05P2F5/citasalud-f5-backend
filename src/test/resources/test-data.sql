-- Insertar roles de prueba con IDs específicos (usa MERGE para evitar duplicados)
MERGE INTO rol (idrol, descripcion) KEY(idrol) VALUES (1, 'Administrador');
MERGE INTO rol (idrol, descripcion) KEY(idrol) VALUES (2, 'Gestor');
MERGE INTO rol (idrol, descripcion) KEY(idrol) VALUES (3, 'Usuario');
