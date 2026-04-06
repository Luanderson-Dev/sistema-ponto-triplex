CREATE TABLE usuarios
(
    id         SERIAL PRIMARY KEY,
    nome       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL
);

CREATE TABLE registros_ponto
(
    id           SERIAL PRIMARY KEY,
    usuario_id   BIGINT    NOT NULL,
    hora_entrada TIMESTAMP NOT NULL,
    hora_saida   TIMESTAMP,
    CONSTRAINT fk_usuario_ponto FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE INDEX idx_usuario_email ON usuarios (email);
CREATE INDEX idx_ponto_usuario ON registros_ponto (usuario_id);