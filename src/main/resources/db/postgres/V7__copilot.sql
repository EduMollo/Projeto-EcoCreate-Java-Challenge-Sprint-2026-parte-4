create table cv4_copilot_window (
 id varchar(36) primary key, id_pet bigint not null references tb_pet(id),
 criado_em varchar(40) not null, cenario varchar(30) not null, leituras text not null
);
create index ix_copilot_window_pet on cv4_copilot_window(id_pet,criado_em);
create table cv4_copilot_insight (
 id varchar(36) primary key, id_pet bigint not null references tb_pet(id),
 criado_em varchar(40) not null, resultado text not null, contexto text not null,
 aprovado_por varchar(60), aprovado_em varchar(40)
);
create index ix_copilot_insight_pet on cv4_copilot_insight(id_pet,criado_em);
create table cv4_copilot_event (
 id varchar(36) primary key, id_insight varchar(36) not null references cv4_copilot_insight(id),
 id_tutor bigint not null references tb_tutor(id), tipo varchar(30) not null, criado_em varchar(40) not null,
 constraint uq_copilot_event unique(id_insight,tipo)
);

-- O backend acessa estas tabelas pelo JDBC. A Data API não recebe acesso.
alter table clyvovet.cv4_copilot_window enable row level security;
alter table clyvovet.cv4_copilot_insight enable row level security;
alter table clyvovet.cv4_copilot_event enable row level security;
revoke all on table clyvovet.cv4_copilot_window, clyvovet.cv4_copilot_insight,
    clyvovet.cv4_copilot_event from public;
do $$
declare papel text;
begin
    foreach papel in array array['anon', 'authenticated'] loop
        if exists (select 1 from pg_catalog.pg_roles where rolname = papel) then
            execute format('revoke all on table clyvovet.cv4_copilot_window, clyvovet.cv4_copilot_insight, clyvovet.cv4_copilot_event from %I', papel);
        end if;
    end loop;
end $$;
