-- Inserting data into Papel table
INSERT INTO Papel (nome, descricao)
VALUES ('CR', 'Membro do Conselho de Representantes'),
       ('P', 'Presidente do ISEL'),
       ('CCE', 'Membro do Conselho COnsultivo Estratégico'),
       ('CA', 'Membro do Conselho Administrativo'),
       ('CP', 'Membro do Conselho Pedagógico'),
       ('CTC', 'Membro do Conselho Técnico-Científico'),
       ('CPR', 'Membro do Conselho Permanente'),
       ('CCD', 'Membro do Conselho Coordenador de Departamento'),
       ('CUID', 'Membro do Conselho das Unidades de I&D'),
       ('CCC', 'Membro da Comissão Coordenadora de Curso'),
       ('UID', 'Membro de Unidades de Investigação e Desenvolvimento');

-- Inserting data into Utilizador table
INSERT INTO Utilizador (email, nome, authToken, pass)
VALUES ('jose.nascimento@isel.pt', 'José Nascimento', '6049ddab-f1ae-44fb-a083-468f70868875', '41d823ab960ef49973ce628377d7bf5d'),
('joao.alfredo.santos@isel.pt', 'João Alfredo Santos', 'bbf5f642-785a-4d4d-bbd5-740016e350c6', '41d823ab960ef49973ce628377d7bf5d'),
('antonio.silvestre@isel.pt', 'Antonio Silvestre', 'dfa81f3f-2ac1-4742-a0a9-86a217658939', '41d823ab960ef49973ce628377d7bf5d'),
('pedro.silva@isel.pt', 'Pedro Silva', '8999de2b-ac32-4a93-8c1f-c9d940c7f130', '41d823ab960ef49973ce628377d7bf5d'),
('pedro.patricio@isel.pt', 'Pedro Patrício', '4f02ba1f-ee80-4859-ba1d-d73d4e372d04', '41d823ab960ef49973ce628377d7bf5d'),
('alessandro.fantoni@isel.pt', 'Alessandro Fantoni', '5bbba4cf-f9da-4de5-a67d-9c1a668588bb', '41d823ab960ef49973ce628377d7bf5d'),
('sandra.aleixo@isel.pt', 'Sandra Aleixo', 'a1fab80b-ec5e-4a5a-ab73-044de58fcc09', '41d823ab960ef49973ce628377d7bf5d'),
('artur.ferreira@isel.pt', 'Artur Ferreira', '58961ae4-4e13-4db5-872e-f538da7cfb3c', '41d823ab960ef49973ce628377d7bf5d'),
('jose.simao@isel.pt', 'José Simão', 'a0e60524-3f45-4e24-a605-e0e775a24a61', '41d823ab960ef49973ce628377d7bf5d'),
('pedro.miguens@isel.pt', 'Pedro Miguens', '175207a2-d244-4561-b6c6-1607620822aa', '41d823ab960ef49973ce628377d7bf5d'),
('nuno.leite@isel.pt', 'Nuno Leite', 'd90880c5-7706-49a1-8dec-ab11f160042f', '41d823ab960ef49973ce628377d7bf5d');

-- Inserting data into template_processo table
INSERT INTO template_processo (nome, descricao, path)
VALUES ('FUC', 'Template para Mudança de FUC', '/templates/mudFUC'),
       ('HORARIO', 'Template para Mudança de Horario', '/templates/horario');

-- Inserting data into acesso_template table
INSERT INTO acesso_template (nome_template, utilizador)
VALUES ('FUC', 'pedro.miguens@isel.pt'),
       ('HORARIO', 'nuno.leite@isel.pt');

-- Inserting data into Processo table
INSERT INTO Processo (id, nome, autor, descricao, data_inicio, data_fim, estado, template_processo)
VALUES ('1', 'FUC IA', 'nuno.leite@isel.pt', 'Mudança do Plano da UC IA', '2023-07-14', '2023-09-14', 'PENDING', 'FUC'),
       ('2', 'HORARIO LEIC', 'nuno.leite@isel.pt', 'Mudança do Horario de LEIC', '2023-07-15', '2023-08-15', 'PENDING', 'HORARIO');
