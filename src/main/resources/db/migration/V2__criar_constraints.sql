alter table tb_perfil
    add constraint uk_perfil_descricao unique (descricao);

alter table tb_usuario
    add constraint uk_usuario_username unique (username);

alter table tb_usuario
    add constraint fk_usuario_tutor
    foreign key (id_tutor) references tb_tutor;

alter table tb_usuario_perfil
    add constraint fk_usuario_perfil_usuario
    foreign key (id_usuario) references tb_usuario;

alter table tb_usuario_perfil
    add constraint fk_usuario_perfil_perfil
    foreign key (id_perfil) references tb_perfil;

alter table tb_tutor
    add constraint uk_tutor_cpf unique (cpf);

alter table tb_tutor
    add constraint uk_tutor_email unique (email);

alter table tb_clinica
    add constraint uk_clinica_cnpj unique (cnpj);

alter table tb_pet
    add constraint uk_pet_microchip unique (microchip);

alter table tb_pet
    add constraint fk_pet_tutor
    foreign key (id_tutor) references tb_tutor;

alter table tb_pet
    add constraint fk_pet_clinica_principal
    foreign key (id_clinica_principal) references tb_clinica;

alter table tb_consulta
    add constraint fk_consulta_pet
    foreign key (id_pet) references tb_pet;

alter table tb_consulta
    add constraint fk_consulta_clinica
    foreign key (id_clinica) references tb_clinica;

alter table tb_evento_saude
    add constraint fk_evento_pet
    foreign key (id_pet) references tb_pet;

alter table tb_evento_saude
    add constraint fk_evento_consulta
    foreign key (id_consulta) references tb_consulta;

alter table tb_vacina
    add constraint fk_vacina_pet
    foreign key (id_pet) references tb_pet;

alter table tb_vacina
    add constraint fk_vacina_clinica
    foreign key (id_clinica) references tb_clinica;

alter table tb_medicamento
    add constraint fk_medicamento_pet
    foreign key (id_pet) references tb_pet;

alter table tb_medicamento
    add constraint fk_medicamento_consulta
    foreign key (id_consulta) references tb_consulta;

create index ix_pet_tutor on tb_pet (id_tutor);
create index ix_consulta_pet_data on tb_consulta (id_pet, data_consulta);
create index ix_consulta_status on tb_consulta (status);
create index ix_vacina_pet on tb_vacina (id_pet);
create index ix_medicamento_pet on tb_medicamento (id_pet);
create index ix_evento_pet on tb_evento_saude (id_pet);
