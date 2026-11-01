-- Senhas em BCrypt: admin123 / vet123 / tutor123
insert into tb_usuario (username, senha, nome_exibicao, ativo, data_criacao, id_tutor) values
    ('admin', '$2a$10$Gs1N42bmGbxWDcpGiW1wB.Lqug0Aaye7BTHrjBvntv5hqpENQTuLu', 'Administração ClyvoVet', true, current_date, null),
    ('vet',   '$2a$10$T438rMISmaLQoHTPpKPW0.XMhn/gfyz6MoeF7gcpXowXiVXnUJC32', 'Dra. Camila Rocha',      true, current_date, null),
    ('tutor', '$2a$10$FWYZ01du4Fy4tBvVPZ50GOltFXq9kSq1c3ClScNQEOd.Z/cPOsxre', 'Mariana Souza',          true, current_date, 1);

insert into tb_usuario_perfil (id_usuario, id_perfil) values
    (1, (select id from tb_perfil where descricao = 'ADMIN')),
    (2, (select id from tb_perfil where descricao = 'VETERINARIO')),
    (3, (select id from tb_perfil where descricao = 'TUTOR'));
