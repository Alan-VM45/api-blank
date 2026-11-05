ALTER TABLE listas ADD COLUMN usuario_id BIGINT;

ALTER TABLE listas
    ADD CONSTRAINT fk_listas_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id);

CREATE INDEX idx_listas_usuario_id ON listas(usuario_id);

ALTER TABLE listas DROP CONSTRAINT IF EXISTS listas_nombre_key;
ALTER TABLE listas
    ADD CONSTRAINT uk_listas_usuario_nombre UNIQUE (usuario_id, nombre);
