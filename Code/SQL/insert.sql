INSERT INTO dbDWP.Papel (nome, descricao)
VALUES ('Gestor', 'Responsável pela gestão e administração do sistema de gestão de processos'),
('Colaborador', 'Responsável por realizar as etapas dos processos');

INSERT INTO dbDWP.Utilizador (email, nome, authToken, pass)
VALUES ('joao@example.com', 'João Silva', 'token1', 'abc1234567'),
('ana@example.com', 'Ana Santos', 'token2', 'def1234567');

INSERT INTO dbDWP.Processo (nome, responsavel, descricao, data_inicio, data_fim, prazo, estado, tipo)
VALUES ('Processo de Compras', 'joao@example.com', 'Processo para aquisição de materiais', '2022-01-01', NULL, '2022-02-01', 'Em andamento', 'Operacional'),
('Processo de Vendas', 'ana@example.com', 'Processo para venda de produtos', '2022-02-01', NULL, '2022-03-01', 'Em andamento', 'Operacional');

INSERT INTO dbDWP.Documento (descricao, estado, nome, localizacao)
VALUES ('Contrato de Fornecimento de Material', 'Aprovado', 'Contrato1', 'https://example.com/docs/contrato1.pdf'),
('Pedido de Venda', 'Pendente', 'Pedido1', 'https://example.com/docs/pedido1.pdf');

INSERT INTO dbDWP.Etapa (nome, responsavel, descricao, data_inicio, data_fim, prazo, estado)
VALUES ('Análise de fornecedores', 'joao@example.com', 'Análise e seleção de fornecedores', '2022-01-01', '2022-01-15', '2022-01-15', 'Concluída'),
('Análise de estoque', 'ana@example.com', 'Análise de estoque de produtos', '2022-02-01', '2022-02-15', '2022-02-15', 'Concluída');

INSERT INTO dbDWP.Comentario (data, hora, texto, remetente)
VALUES ('2022-01-10', '14:30', 'A empresa X tem um bom histórico de fornecimento', 'joao@example.com'),
('2022-01-12', '10:15', 'Concordo, mas precisamos de verificar se os preços são competitivos', 'ana@example.com');

INSERT INTO dbDWP.DocumentoProcesso (id_documento, id_processo)
VALUES (1, 1),
(2, 2);

INSERT INTO dbDWP.EtapaProcesso (id_etapa, id_processo)
VALUES (1, 1),
(2, 2);

INSERT INTO dbDWP.ComentarioEtapa (id_etapa, id_etapa)
VALUES (1, 1),
(2, 2);

INSERT INTO dbDWP.UtilizadorPapel (id_papel, email_utilizador)
VALUES (1, 'joao@example.com'),
(2, 'ana@example.com');

INSERT INTO dbDWP.ComentarioUtilizador (num_comentario, email_utilizador)
VALUES (1, 'joao@example.com'),
(2, 'ana@example.com');

INSERT INTO dbDWP.UtilizadorProcesso (email_utilizador, id_processo)
VALUES ('joao@example.com', 1),
('ana@example.com', 2);

INSERT INTO dbDWP.UtilizadorEtapa (id_etapa, id_processo)
VALUES (1, 1),
(2, 2);