// Executar após importar o JSON em uma coleção de demonstração.
db.atendimentos.createIndex({id_atendimento:1},{unique:true});
db.atendimentos.createIndex({'pet.id':1,data:-1});
db.atendimentos.createIndex({clinica:1,status:1});
printjson(db.atendimentos.findOne());
printjson(db.atendimentos.getIndexes());
