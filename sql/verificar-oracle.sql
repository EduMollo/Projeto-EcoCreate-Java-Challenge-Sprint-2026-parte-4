-- Somente leitura, após Flyway e um cadastro/atendimento pela interface.
SELECT name,type,line,position,text FROM user_errors WHERE name IN ('PKG_CLYVOVET','TRG_AUD_PET') ORDER BY name,sequence;
SELECT object_name,object_type,status FROM user_objects WHERE object_name IN ('PKG_CLYVOVET','TRG_AUD_PET');
SELECT id,nome,peso_kg,ativo FROM tb_pet ORDER BY id;
SELECT id_pet,nome,peso_kg FROM pet ORDER BY id_pet;
SELECT id,status,valor,data_consulta FROM tb_consulta ORDER BY id;
SELECT id_atendimento,status,valor,data_atendimento FROM atendimento ORDER BY id_atendimento;
SELECT id_auditoria,id_registro,tipo_operacao,nome_usuario,data_hora,valor_anterior,valor_novo FROM aud_pet ORDER BY id_auditoria;
