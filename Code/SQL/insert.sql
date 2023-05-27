-- Inserting data into Papel table
INSERT INTO Papel (nome, descricao)
VALUES ('CR', 'Membro do Conselho de Representantes'),
       ('P', 'Presidente do ISEL'),
       ('CCE', 'Membro do Conselho Consultivo Estratégico'),
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
VALUES ('jose.nascimentomock@isel.pt', 'José Nascimento', '6049ddab-f1ae-44fb-a083-468f70868875', '41d823ab960ef49973ce628377d7bf5d'),
('joao.alfredo.santosmock@isel.pt', 'João Alfredo Santos', 'bbf5f642-785a-4d4d-bbd5-740016e350c6', '41d823ab960ef49973ce628377d7bf5d'),
('antonio.silvestremock@isel.pt', 'Antonio Silvestre', 'dfa81f3f-2ac1-4742-a0a9-86a217658939', '41d823ab960ef49973ce628377d7bf5d'),
('pedro.silvamock@isel.pt', 'Pedro Silva', '8999de2b-ac32-4a93-8c1f-c9d940c7f130', '41d823ab960ef49973ce628377d7bf5d'),
('pedro.patriciomock@isel.pt', 'Pedro Patrício', '4f02ba1f-ee80-4859-ba1d-d73d4e372d04', '41d823ab960ef49973ce628377d7bf5d'),
('alessandro.fantonimock@isel.pt', 'Alessandro Fantoni', '5bbba4cf-f9da-4de5-a67d-9c1a668588bb', '41d823ab960ef49973ce628377d7bf5d'),
('sandra.aleixomock@isel.pt', 'Sandra Aleixo', 'a1fab80b-ec5e-4a5a-ab73-044de58fcc09', '41d823ab960ef49973ce628377d7bf5d'),
('artur.ferreiramock@isel.pt', 'Artur Ferreira', '58961ae4-4e13-4db5-872e-f538da7cfb3c', '41d823ab960ef49973ce628377d7bf5d'),
('jose.simaomock@isel.pt', 'José Simão', 'a0e60524-3f45-4e24-a605-e0e775a24a61', '41d823ab960ef49973ce628377d7bf5d'),
('nuno.datiamock@isel.pt', 'Nuno Datia', 'e193dffd-b0ce-4497-a233-3a4ad8aababa', '41d823ab960ef49973ce628377d7bf5d'),
('pedro.miguensmock@isel.pt', 'Pedro Miguens', '175207a2-d244-4561-b6c6-1607620822aa', '41d823ab960ef49973ce628377d7bf5d'),
('nuno.leitemock@isel.pt', 'Nuno Leite', 'd90880c5-7706-49a1-8dec-ab11f160042f', '41d823ab960ef49973ce628377d7bf5d');

-- Inserting data into template_processo table
INSERT INTO template_processo (nome, descricao, path)
VALUES ('procFUC', 'Template para Mudança de FUC', '/templates/mudFUC'),
       ('procHORARIO', 'Template para Mudança de Horario', '/templates/horario');

-- Inserting data into acesso_template table
INSERT INTO acesso_template (nome_template, utilizador)
VALUES ('procFUC', 'pedro.miguensmock@isel.pt'),
       ('procHORARIO', 'nuno.leitemock@isel.pt');

-- Inserting data into Processo table
INSERT INTO Processo (id, nome, autor, descricao, data_inicio, data_fim, estado, template_processo)
VALUES ('1', 'FUC_IA', 'nuno.leitemock@isel.pt', 'Mudança do Plano da UC IA', '2023-07-14', '2023-09-14', 'PENDING', 'procFUC'),
       ('2', 'HORARIO_LEIC', 'nuno.leitemock@isel.pt', 'Mudança do Horario de LEIC', '2023-07-15', '2023-08-15', 'PENDING', 'procHORARIO');

-- Inserting data into Documento table
INSERT INTO Documento (id, nome, tipo, tamanho, localizacao)
VALUES ('doc4FucIA', 'Nova_FUC_IA', 'PDF', 2048, '/documents/FUC_IA_2324.pdf'),
       ('doc4HorarioLEIC', 'Novo_HorarioLEIC', 'PDF', 2048, '/documents/HORARIO_LEIC_2324.pdf');

      
-- RUC -> Envia para CCC
-- CCC -> CCD
-- CCD -> CP
-- CP -> CTC
-- CTC -> P

-- • O RUC da UC envia proposta de FUC para a comissão coordenadora de curso (CCC) para aprovação;
-- • A CCC aprecia a proposta e envia a proposta aprovada para a(s) comissão(ões) coordenadora(s) do(s)
-- departamento(s) (CCD) envolvido(s) nessa UC;
-- • A(s) CCD envolvida(s) analisam a FUC e após a aprovação de todas as CCD, a FUC é enviada ao CP;
-- • O CP analisa a FUC do ponto de vista pedagógico e aprova, enviando de seguida para o CTC;
-- • O CTC analisa a FUC do ponto de vista científico e aprova, enviando para o secretariado da presidência do ISEL,
-- o qual envia para os Serviços Académicos do ISEL e para o Serviço de Comunicação para publicação na página de internet do ISEL.

      
-- Inserting data into Etapa table
INSERT INTO Etapa (id, id_processo, indice, modo, nome, descricao, data_inicio, data_fim, prazo, estado)
VALUES ('step1', '1', 1, 'Unanimous', 'Step 1', 'RUC da UC -> FUC p/CCC para aprovação', '2023-01-01', '2023-01-10', 10, 'PENDING'),
('step2', '1', 2, 'Unanimous', 'Step 2', 'CCC evals proposta -> CCD', '2023-01-01', '2023-01-10', 10, 'PENDING'),
('step3', '1', 3, 'Unanimous', 'Step 3', 'CCD envolvida(s) analisam a FUC -> ao CP', '2023-01-01', '2023-01-10', 10, 'PENDING'),
('step4', '1', 4, 'Unanimous', 'Step 4', 'CP analisa FUC e aprova -> CTC;', '2023-01-01', '2023-01-10', 10, 'PENDING'),
('step5', '1', 5, 'Unanimous', 'Step 5', 'CTC analisa FUC e aprova -> P ISEL,', '2023-01-11', '2023-01-20', 10, 'PENDING');

-- Inserting data into Documento_Processo table
INSERT INTO Documento_Processo (id_documento, id_processo)
VALUES ('doc4FucIA', '1'),
       ('doc4HorarioLEIC', '2');

-- Inserting data into Utilizador_Papel table
INSERT INTO Utilizador_Papel (papel, email_utilizador)
VALUES ('P', 'jose.nascimentomock@isel.pt'),
('CR', 'antonio.silvestremock@isel.pt'),
('CCE', 'joao.alfredo.santosmock@isel.pt'),
('CP', 'sandra.aleixomock@isel.pt'),
('CPR', 'pedro.patriciomock@isel.pt'),
('CTC', 'alessandro.fantonimock@isel.pt'),
('CA', 'pedro.silvamock@isel.pt'),
('CCC', 'artur.ferreiramock@isel.pt'),
('CCD', 'nuno.datiamock@isel.pt'),
('UID', 'nuno.leitemock@isel.pt');

-- Inserting data into Utilizador_Etapa table
INSERT INTO Utilizador_Etapa (email_utilizador, id_etapa, assinatura, data_assinatura, id_notificacao)
VALUES ('artur.ferreiramock@isel.pt', 'step1', false, null, 'notification1'),
('nuno.datiamock@isel.pt', 'step2', false, null, null),
('sandra.aleixomock@isel.pt', 'step3', false, null, null),
('alessandro.fantonimock@isel.pt', 'step4', false, null, null),
('jose.nascimentomock@isel.pt', 'step5', false, null, null);
      
      
