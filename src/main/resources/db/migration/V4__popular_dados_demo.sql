-- Dados de demonstracao. Datas relativas a CURRENT_DATE para que os alertas de risco
-- continuem coerentes em qualquer dia em que a aplicacao for executada.

insert into tb_tutor (nome, cpf, email, telefone, data_nascimento) values
    ('Mariana Souza',  '12345678901', 'mariana.souza@email.com', '11987654321', '1990-04-12'),
    ('Carlos Lima',    '98765432100', 'carlos.lima@email.com',   '11912345678', '1985-09-30'),
    ('Ana Pereira',    '45678912300', 'ana.pereira@email.com',   '11955556666', '1998-01-22');

insert into tb_clinica (nome, cnpj, telefone, email, endereco, cidade, estado) values
    ('ClyvoVet Paulista',  '12345678000199', '1130001000', 'paulista@clyvovet.com.br',  'Av. Paulista, 1000',       'São Paulo', 'SP'),
    ('ClyvoVet Pinheiros', '98765432000188', '1130002000', 'pinheiros@clyvovet.com.br', 'Rua dos Pinheiros, 500',   'São Paulo', 'SP');

insert into tb_pet (nome, especie, raca, data_nascimento, peso_kg, sexo, castrado, microchip, observacoes, id_tutor, id_clinica_principal) values
    ('Thor', 'CACHORRO', 'Labrador',  dateadd('YEAR', -8, current_date),  32.50, 'MACHO', true,  '985112000001', 'Sensível a anti-inflamatórios.', 1, 1),
    ('Luna', 'GATO',     'SRD',       dateadd('YEAR', -3, current_date),   4.20, 'FEMEA', true,  '985112000002', null,                             1, 1),
    ('Rex',  'CACHORRO', 'Bulldog',   dateadd('YEAR', -5, current_date),  24.00, 'MACHO', false, null,           'Nunca passou por avaliação.',    2, 2),
    ('Mel',  'GATO',     'Persa',     dateadd('YEAR', -11, current_date),  3.80, 'FEMEA', true,  '985112000004', 'Doença renal crônica.',          2, 2),
    ('Nina', 'PASSARO',  'Calopsita', dateadd('YEAR', -2, current_date),   0.09, 'FEMEA', false, null,           null,                             3, 2);

-- Thor: check-up antigo, retorno nunca realizado, vacina vencida -> risco alto
insert into tb_consulta (data_consulta, status, tipo_consulta, veterinario, diagnostico, prescricao, observacoes, valor, data_retorno, criado_em, id_pet, id_clinica) values
    (dateadd('DAY', -400, current_date), 'CONCLUIDA', 'ROTINA', 'Dra. Camila Rocha', 'Displasia coxofemoral leve.', 'Anti-inflamatório por 10 dias.', 'Controle de peso recomendado.', 180.00, dateadd('DAY', -370, current_date), dateadd('DAY', -400, current_timestamp), 1, 1),
    (dateadd('DAY', -370, current_date), 'AGENDADA',  'RETORNO', 'Dra. Camila Rocha', null, null, null, null, null, dateadd('DAY', -400, current_timestamp), 1, 1),
    (dateadd('DAY',    3, current_date), 'AGENDADA',  'ROTINA', 'Dra. Camila Rocha', null, null, 'Reavaliação da displasia.', null, null, current_timestamp, 1, 1);

-- Luna: acompanhamento em dia -> risco baixo
insert into tb_consulta (data_consulta, status, tipo_consulta, veterinario, diagnostico, prescricao, observacoes, valor, data_retorno, criado_em, id_pet, id_clinica) values
    (dateadd('DAY', -60, current_date), 'CONCLUIDA', 'VACINACAO', 'Dr. Rafael Nunes', 'Saudável. Vacina V4 aplicada.', null, null, 120.00, null, dateadd('DAY', -60, current_timestamp), 2, 1),
    (dateadd('DAY', -90, current_date), 'CANCELADA', 'ROTINA',    'Dr. Rafael Nunes', null, null, null, null, null, dateadd('DAY', -95, current_timestamp), 2, 1);

-- Mel: idosa, ultima consulta ha muito tempo, vacina vencida, medicamento continuo -> risco alto
insert into tb_consulta (data_consulta, status, tipo_consulta, veterinario, diagnostico, prescricao, observacoes, valor, data_retorno, criado_em, id_pet, id_clinica) values
    (dateadd('DAY', -600, current_date), 'CONCLUIDA', 'EXAME', 'Dra. Camila Rocha', 'Doença renal crônica estágio 2.', 'Ração renal e suplemento contínuo.', null, 350.00, null, dateadd('DAY', -600, current_timestamp), 4, 2);

-- Nina: consulta agendada para hoje (fluxo de atendimento na demonstracao)
insert into tb_consulta (data_consulta, status, tipo_consulta, veterinario, diagnostico, prescricao, observacoes, valor, data_retorno, criado_em, id_pet, id_clinica) values
    (current_date, 'AGENDADA', 'ROTINA', 'Dra. Camila Rocha', null, null, 'Primeira consulta.', null, null, current_timestamp, 5, 2);

insert into tb_vacina (nome, fabricante, lote, data_aplicacao, data_validade, proxima_dose, criado_em, id_pet, id_clinica) values
    ('V10',         'Zoetis',  'L2301', dateadd('DAY', -400, current_date), dateadd('DAY', -35, current_date),  dateadd('DAY', -35, current_date),  dateadd('DAY', -400, current_timestamp), 1, 1),
    ('V4',          'MSD',     'F8891', dateadd('DAY',  -60, current_date), dateadd('DAY', 305, current_date),  dateadd('DAY', 305, current_date),  dateadd('DAY',  -60, current_timestamp), 2, 1),
    ('Antirrábica', 'Boehringer', 'R1102', dateadd('DAY', -600, current_date), dateadd('DAY', -235, current_date), dateadd('DAY', -235, current_date), dateadd('DAY', -600, current_timestamp), 4, 2);

insert into tb_medicamento (nome, principio_ativo, dosagem, frequencia, data_inicio, data_fim, uso_continuo, observacoes, id_pet, id_consulta) values
    ('Meloxicam',            'Meloxicam',        '0,1 mg/kg', '1x ao dia',  dateadd('DAY', -400, current_date), dateadd('DAY', -390, current_date), false, 'Administrar após alimentação.', 1, 1),
    ('Suplemento renal',     'Quitosana',        '1 sachê',   '2x ao dia',  dateadd('DAY', -600, current_date), null,                               true,  'Uso contínuo.',                  4, 6);

insert into tb_evento_saude (tipo_evento, descricao, data_evento, data_proxima_acao, concluido, criado_em, id_pet, id_consulta) values
    ('CONSULTA',    'Consulta rotina concluída. Diagnóstico: Displasia coxofemoral leve.', dateadd('DAY', -400, current_date), null,                               true,  dateadd('DAY', -400, current_timestamp), 1, 1),
    ('MEDICAMENTO', 'Medicamento prescrito: Meloxicam — 0,1 mg/kg, 1x ao dia.',            dateadd('DAY', -400, current_date), dateadd('DAY', -390, current_date), true,  dateadd('DAY', -400, current_timestamp), 1, 1),
    ('RETORNO',     'Retorno agendado automaticamente.',                                   dateadd('DAY', -400, current_date), dateadd('DAY', -370, current_date), false, dateadd('DAY', -400, current_timestamp), 1, 1),
    ('VACINA',      'Vacina aplicada: V10 (Zoetis).',                                     dateadd('DAY', -400, current_date), dateadd('DAY',  -35, current_date), false, dateadd('DAY', -400, current_timestamp), 1, 1),
    ('CONSULTA',    'Consulta vacinação concluída. Diagnóstico: Saudável. Vacina V4 aplicada.', dateadd('DAY', -60, current_date), null,                          true,  dateadd('DAY',  -60, current_timestamp), 2, 4),
    ('VACINA',      'Vacina aplicada: V4 (MSD).',                                          dateadd('DAY',  -60, current_date), dateadd('DAY', 305, current_date),  false, dateadd('DAY',  -60, current_timestamp), 2, 4),
    ('CONSULTA',    'Consulta exame concluída. Diagnóstico: Doença renal crônica estágio 2.', dateadd('DAY', -600, current_date), null,                            true,  dateadd('DAY', -600, current_timestamp), 4, 6),
    ('MEDICAMENTO', 'Medicamento prescrito: Suplemento renal — 1 sachê, 2x ao dia (uso contínuo).', dateadd('DAY', -600, current_date), null,                     false, dateadd('DAY', -600, current_timestamp), 4, 6);
