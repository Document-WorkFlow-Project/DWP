BEGIN TRANSACTION;

CREATE DOMAIN email AS varchar(32) CHECK (value ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

--Papel(id,nome,descricao)
CREATE TABLE IF NOT EXISTS Papel(
    nome varchar(32) PRIMARY KEY,
    descricao varchar(100)
);

--Utilizador(email, nome, authToken, password)
CREATE TABLE IF NOT EXISTS Utilizador(
    email email PRIMARY KEY,
    nome varchar(32) NOT NULL,
    authToken text UNIQUE NOT NULL,
    pass text NOT NULL
);

CREATE TABLE IF NOT EXISTS template_processo(
    nome text PRIMARY KEY,
    descricao text NOT NULL,
    etapas json NOT NULL
);

CREATE TABLE IF NOT EXISTS acesso_template(
    nome_template text,
    utilizador email,
    PRIMARY KEY (nome_template, utilizador),
    FOREIGN KEY (nome_template) REFERENCES template_processo(nome) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (utilizador) REFERENCES utilizador(email) ON DELETE CASCADE ON UPDATE CASCADE
);

--Processo(id,nome,autor,descricao,data_inicio,data_fim,prazo,estado,tipo)
CREATE TABLE IF NOT EXISTS Processo(
    id text PRIMARY KEY,
    nome varchar(32) NOT NULL,
    autor varchar(32) NOT NULL,
    descricao varchar(100),
    data_inicio timestamp NOT NULL,
    data_fim timestamp,
    estado varchar(32) NOT NULL,
    template_processo varchar(32) NOT NULL,
    CONSTRAINT estado CHECK (estado IN ('PENDING', 'APPROVED', 'DISAPPROVED')),
    FOREIGN KEY (template_processo) REFERENCES template_processo(nome) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (autor) REFERENCES Utilizador(email) ON DELETE CASCADE ON UPDATE CASCADE
);

--Documento(id,descricao,status,nome,localizacao)
CREATE TABLE IF NOT EXISTS Documento(
    id text PRIMARY KEY,
    nome text NOT NULL,
    tipo text NOT NULL,
    tamanho bigint NOT NULL,
    localizacao text NOT NULL
);

--Etapa(id,nome,descricao,data_inicio,data_fim,prazo,estado)
CREATE TABLE IF NOT EXISTS Etapa(
    id text PRIMARY KEY,
    id_processo text NOT NULL,
    indice int NOT NULL,
    modo varchar(32) NOT NULL,
    CONSTRAINT modo CHECK (modo IN ('Unanimous', 'Majority')),
    nome varchar(32) NOT NULL,
    descricao varchar(100),
    data_inicio timestamp,
    data_fim timestamp,
    prazo int NOT NULL,
    estado varchar(32) NOT NULL,
    CONSTRAINT estado CHECK (estado IN ('PENDING', 'APPROVED', 'DISAPPROVED')),
    FOREIGN KEY (id_processo) REFERENCES Processo(id) ON DELETE CASCADE ON UPDATE CASCADE
);

--Comentario(id,texto,data)
CREATE TABLE IF NOT EXISTS Comentario(
    id text PRIMARY KEY,
    id_etapa text NOT NULL,
    data timestamp NOT NULL,
    texto varchar(150),
    remetente text NOT NULL,
    FOREIGN KEY (id_etapa) REFERENCES Etapa(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (remetente) REFERENCES Utilizador(email) ON DELETE CASCADE ON UPDATE CASCADE
);

--Tabela de associação entre Documentos e Processos
CREATE TABLE IF NOT EXISTS Documento_Processo(
    id_documento text NOT NULL,
    id_processo text NOT NULL,
    PRIMARY KEY (id_documento, id_processo),
    FOREIGN KEY (id_documento) REFERENCES Documento(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_processo) REFERENCES Processo(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabela que associa Utilizadores a Papel
CREATE TABLE IF NOT EXISTS Utilizador_Papel(
    papel varchar(36) NOT NULL,
    email_utilizador varchar(36) NOT NULL,
    FOREIGN KEY (papel) REFERENCES Papel(nome) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (email_utilizador) REFERENCES Utilizador(email) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (papel, email_utilizador)
);

-- Tabela que associa Utilizadores a Etapas
CREATE TABLE IF NOT EXISTS Utilizador_Etapa(
    email_utilizador varchar(32) NOT NULL,
    id_etapa text NOT NULL,
    assinatura boolean,
    data_assinatura timestamp,
    id_notificacao text,
    FOREIGN KEY (email_utilizador) REFERENCES Utilizador(email) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_etapa) REFERENCES Etapa(id) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (email_utilizador, id_etapa)
);

insert into papel values ('admin', 'Administrador');
-- initial admin password = admin1234
INSERT INTO utilizador VALUES ('david.robalo@hotmail.com', 'Administrador principal', 'bd39bea0-ba49-4fda-8660-b8870b3ae187', 'c93ccd78b2076528346216b3b2f701e6');
INSERT INTO utilizador_papel VALUES ('admin', 'david.robalo@hotmail.com');

COMMIT TRANSACTION;

