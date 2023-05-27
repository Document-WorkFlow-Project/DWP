BEGIN TRANSACTION;

CREATE DOMAIN email AS varchar(32) 
CHECK (value ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

--Papel(id,nome,descricao)
CREATE TABLE IF NOT EXISTS Papel (
    nome varchar(32) primary key,
    descricao varchar(100)
);

--Utilizador(email, nome, authToken, password)
CREATE TABLE IF NOT EXISTS Utilizador (
    email email PRIMARY KEY,
    nome varchar(32) UNIQUE NOT NULL,
    authToken varchar(64) UNIQUE NOT NULL,
    pass text NOT NULL
);

create table if not exists template_processo (
	nome text primary key,
	descricao text not null,
	path text not null
);

create table if not exists acesso_template (
	nome_template text,
	utilizador email,
	primary key(nome_template, utilizador),
	foreign key (nome_template) references template_processo (nome) ON DELETE CASCADE ON UPDATE cascade,
	foreign key (utilizador) references utilizador (email) ON DELETE CASCADE ON UPDATE cascade
);

--Processo(id,nome,autor,descricao,data_inicio,data_fim,prazo,estado,tipo)
CREATE TABLE IF NOT EXISTS Processo (
    id varchar(36) PRIMARY KEY,
    nome varchar(32) NOT NULL,
    autor varchar(32) NOT NULL,
    descricao varchar(100),
    data_inicio date NOT NULL,
    data_fim date,
    estado varchar(32) NOT NULL,
    template_processo varchar(32) NOT NULL,
    constraint estado check (estado IN ('PENDING', 'APPROVED', 'DISAPPROVED')),
    foreign key (template_processo) references template_processo (nome) ON DELETE CASCADE ON UPDATE cascade,
    FOREIGN KEY (autor) REFERENCES Utilizador (email) ON DELETE CASCADE ON UPDATE CASCADE
);

--Documento(id,descricao,status,nome,localizacao)
CREATE TABLE IF NOT EXISTS Documento (
    id varchar(36) PRIMARY KEY,
    nome text NOT NULL,
    tipo varchar(32) not null,
    tamanho bigint not null, 
    localizacao varchar(100) NOT NULL
);

--Etapa(id,nome,descricao,data_inicio,data_fim,prazo,estado)
CREATE TABLE IF NOT EXISTS Etapa (
    id varchar(36) PRIMARY KEY,
    id_processo varchar(36) NOT NULL,
    indice int not null,
    modo varchar(32) NOT NULL,
    constraint modo check (modo IN ('Unanimous', 'Majority')),
    nome varchar(32) NOT NULL,
    descricao varchar(100),
    data_inicio date,
    data_fim date,
    prazo int NOT NULL,
    estado varchar(32) NOT NULL,
    constraint estado check (estado in ('PENDING', 'APPROVED', 'DISAPPROVED')),
    FOREIGN KEY (id_processo) REFERENCES Processo (id) ON DELETE CASCADE ON UPDATE CASCADE
);

--Comentario(id,texto,data)
CREATE TABLE IF NOT EXISTS Comentario (
    id varchar(36) PRIMARY KEY,
    id_etapa varchar(36) NOT NULL,
    data date NOT NULL,
    texto varchar(150),
    remetente varchar(36) NOT NULL,
    FOREIGN KEY (id_etapa) REFERENCES Etapa(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (remetente) REFERENCES Utilizador (email) ON DELETE CASCADE ON UPDATE CASCADE
);

--Tabela de associação entre Documentos e Processos
CREATE TABLE IF NOT EXISTS Documento_Processo (
    id_documento varchar(36) NOT NULL,
    id_processo varchar(36) NOT NULL,
    PRIMARY KEY (id_documento, id_processo),
    FOREIGN KEY (id_documento) REFERENCES Documento (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_processo) REFERENCES Processo (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabela que associa Utilizadores a Papel
CREATE TABLE IF NOT EXISTS Utilizador_Papel (
    papel varchar(36) NOT NULL,
    email_utilizador varchar(36) NOT NULL,
    FOREIGN KEY (papel) REFERENCES Papel(nome) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (email_utilizador) REFERENCES Utilizador(email) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (papel, email_utilizador)
);

-- Tabela que associa Utilizadores a Etapas
CREATE TABLE IF NOT EXISTS Utilizador_Etapa (
    email_utilizador varchar(32) NOT NULL,
    id_etapa varchar(36) NOT NULL,
    assinatura boolean,
    data_assinatura date,
    id_notificacao text,
    FOREIGN KEY (email_utilizador) REFERENCES Utilizador(email) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (id_etapa) REFERENCES Etapa(id) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (email_utilizador, id_etapa)
);

COMMIT TRANSACTION;

insert into utilizador values ('example@gmail.com','test user', 'exampleToken', 'hashPass')

rollback;