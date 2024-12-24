INSERT INTO tb_participante(nome, email) VALUES ('José Silva', 'jose@gmail.com');
INSERT INTO tb_participante(nome, email) VALUES ('Tiago Faria', 'tiago@gmail.com');
INSERT INTO tb_participante(nome, email) VALUES ('Maria do Rosário', 'maria@gmail.com');
INSERT INTO tb_participante(nome, email) VALUES ('Teresa Silva', 'teresa@gmail.com');

INSERT INTO tb_categoria(descricao) VALUES ('Curso');
INSERT INTO tb_categoria(descricao) VALUES ('Oficina');

INSERT INTO tb_atividade(nome, descricao, preco, categoria_id) VALUES ('Curso de HTML', 'Aprenda HTML de forma prática', 80.00, 1);
INSERT INTO tb_atividade(nome, descricao, preco, categoria_id) VALUES ('Oficina de Github', 'Controle versões de seus projetos', 50.00, 2);

INSERT INTO tb_bloco(inicio, fim, atividade_id) VALUES ('2017-09-25T08:00:00Z', '2017-09-25T11:00:00Z', 1);
INSERT INTO tb_bloco(inicio, fim, atividade_id) VALUES ('2017-09-25T14:00:00Z', '2017-09-25T18:00:00Z', 2);
INSERT INTO tb_bloco(inicio, fim, atividade_id) VALUES ('2017-09-26T08:00:00Z', '2017-09-26T11:00:00Z', 2);

INSERT INTO tb_participante_atividade(atividade_id, participante_id) VALUES (1,1);
INSERT INTO tb_participante_atividade(atividade_id, participante_id) VALUES (1,2);
INSERT INTO tb_participante_atividade(atividade_id, participante_id) VALUES (1,3);
INSERT INTO tb_participante_atividade(atividade_id, participante_id) VALUES (2,1);
INSERT INTO tb_participante_atividade(atividade_id, participante_id) VALUES (2,3);
INSERT INTO tb_participante_atividade(atividade_id, participante_id) VALUES (2,4);
