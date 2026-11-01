-- Evolucao do schema: o cancelamento de consulta passou a exigir um motivo.
alter table tb_consulta add column motivo_cancelamento varchar(300);
