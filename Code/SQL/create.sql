BEGIN TRANSACTION;
CREATE DATABASE dbPWP;

CREATE DOMAIN email AS varchar(32) 
CHECK (value ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

CREATE DOMAIN alfanumeric_password AS varchar(32) 
CHECK (value ~* '^[A-Za-z0-9]+$');

--Papel(id,nome,descricao)
CREATE TABLE IF NOT EXISTS dbDWP.Papel (
    id serial PRIMARY KEY,
    nome varchar(32) NOT NULL,
    descricao varchar(100)
);

--Utilizador(email, nome, authToken, password)
CREATE TABLE IF NOT EXISTS dbDWP.Utilizador (
    email email PRIMARY KEY,
    nome varchar(32) UNIQUE NOT NULL,
    authToken varchar(64) UNIQUE NOT NULL,
    pass alfanumeric_password NOT NULL CHECK (pass ~ '^[A-Za-z0-9]{10,}$') --verifica se tem pelo menos 10 caracteres minimo
);
--Processo(id,nome,responsavel,descricao,data_inicio,data_fim,prazo,estado,tipo)
CREATE TABLE IF NOT EXISTS dbDWP.Processo (
    id serial PRIMARY KEY,
    nome varchar(32) NOT NULL,
    responsavel varchar(32) NOT NULL,
    descricao varchar(100),
    data_inicio date NOT NULL,
    data_fim date,
    prazo date NOT NULL,
    estado varchar(32) NOT NULL,
    tipo varchar(32) NOT NULL,
    FOREIGN KEY (responsavel) REFERENCES dbDWP.Utilizador (email) ON DELETE CASCADE ON UPDATE CASCADE
);

--Documento(id,descricao,status,nome,localizacao)
CREATE TABLE IF NOT EXISTS dbDWP.Documento (
    id serial PRIMARY KEY,
    descricao varchar(100),
    estado varchar(32) NOT NULL,
    nome varchar(32) NOT NULL,
    localizacao varchar(100) NOT NULL
);

--Etapa(id,nome,responsavel,descricao,data_inicio,data_fim,prazo,estado)
CREATE TABLE IF NOT EXISTS dbDWP.Etapa (
    id serial PRIMARY KEY,
    nome varchar(32) NOT NULL,
    responsavel varchar(32) NOT NULL,
    descricao varchar(100),
    data_inicio date NOT NULL,
    data_fim date,
    prazo date NOT NULL,
    estado varchar(32) NOT NULL,
    FOREIGN KEY (responsavel) REFERENCES dbDWP.Utilizador (email) ON DELETE CASCADE ON UPDATE CASCADE
);
--Comentario(id,texto,data)
CREATE TABLE IF NOT EXISTS dbDWP.Comentario(
    id serial PRIMARY KEY,
    data date NOT NULL,
    hora time NOT NULL,
    texto varchar(150),
    remetente varchar(32) NOT NULL,
    FOREIGN KEY (remetente) REFERENCES dbDWP.Utilizador (email) ON DELETE CASCADE ON UPDATE CASCADE


);

--Tabela de associação entre Documentos e Processos
CREATE TABLE IF NOT EXISTS dbDWP.DocumentoProcesso (
    id_documento int NOT NULL,
    id_processo int NOT NULL,
    PRIMARY KEY (id_documento, id_processo),
    FOREIGN KEY (id_documento) REFERENCES dbDWP.Documento (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_processo) REFERENCES dbDWP.Processo (id) ON DELETE CASCADE ON UPDATE CASCADE
);

--Tabela de associação entre Etapas e Processos
CREATE TABLE IF NOT EXISTS dbDWP.EtapaProcesso (
    id_etapa int NOT NULL,
    id_processo int NOT NULL,
    PRIMARY KEY (id_etapa, id_processo),
    FOREIGN KEY (id_etapa) REFERENCES dbDWP.Etapa (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_processo) REFERENCES dbDWP.Processo (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabela que associa Comentarios a Etapas
CREATE TABLE IF NOT EXISTS dbDWP.ComentarioEtapa (
    id_comentario int NOT NULL,
    id_etapa int NOT NULL,
    FOREIGN KEY (id_comentario) REFERENCES dbDWP.Comentario(num) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_etapa) REFERENCES dbDWP.Etapa(id) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (id_comentario, id_etapa)
);

-- Tabela que associa Utilizadores a Papel
CREATE TABLE IF NOT EXISTS dbDWP.UtilizadorPapel (
    id_papel int NOT NULL,
    email_utilizador varchar(32) NOT NULL,
    FOREIGN KEY (id_papel) REFERENCES dbDWP.Papel(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (email_utilizador) REFERENCES dbDWP.Utilizador(email) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (id_papel, email_utilizador)
);

-- Tabela que associa Comentarios a Utilizadores
CREATE TABLE IF NOT EXISTS dbDWP.ComentarioUtilizador (
    num_comentario int NOT NULL,
    email_utilizador varchar(32) NOT NULL,
    FOREIGN KEY (num_comentario) REFERENCES dbDWP.Comentario(num) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (email_utilizador) REFERENCES dbDWP.Utilizador(email) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (num_comentario, email_utilizador)
);

-- Tabela que associa Utilizadores a Processos
CREATE TABLE IF NOT EXISTS dbDWP.UtilizadorProcesso (
    email_utilizador varchar(32) NOT NULL,
    id_processo int NOT NULL,
    FOREIGN KEY (email_utilizador) REFERENCES dbDWP.Utilizador(email) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_processo) REFERENCES dbDWP.Processo(id) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (email_utilizador, id_processo)
);


-- Tabela que associa Utilizadores a Etapas
CREATE TABLE IF NOT EXISTS dbDWP.UtilizadorEtapa (
    email_utilizador varchar(32) NOT NULL,
    id_etapa int NOT NULL,
    FOREIGN KEY (email_utilizador) REFERENCES dbDWP.Utilizador(email) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_etapa) REFERENCES dbDWP.Etapa(id) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (email_utilizador, id_etapa)
);

COMMIT TRANSACTION;

