# Dados fictícios para demonstração

A carga acrescenta 768 registros de negócio, todos identificados com [DEMO], e mantém os cadastros e credenciais existentes.

| Cadastro | Quantidade |
|---|---:|
| Clínicas (uma inativa e sem vínculos) | 6 |
| Tutores (dois sem pets, um deles inativo) | 32 |
| Pets (dez inativos e sem histórico) | 60 |
| Consultas | 190 |
| Vacinas | 120 |
| Medicamentos | 50 |
| Eventos na linha do tempo | 310 |

As datas são relativas ao dia em que a carga é executada. O cenário pode mudar naturalmente com o passar dos dias. Os documentos são sintéticos, os e-mails usam o domínio reservado example.invalid e as informações clínicas não representam orientações de tratamento.

## Começar pelos primeiros pets

| Pet | Cenário no dia da carga |
|---|---|
| [DEMO] Luna 01 - em dia | Risco baixo (0), consulta recente, vacinas em dia e retorno agendado |
| [DEMO] Thor 02 - alerta alto | Risco alto (100), sênior, check-up atrasado, vacinas e doses vencidas, consulta não realizada |
| [DEMO] Mel 03 - primeira consulta | Risco médio (40), filhote sem atendimento concluído, consulta agendada |
| [DEMO] Nina 04 - alerta medio | Risco médio (50), sênior com pendências de acompanhamento |
| [DEMO] Bento 05 - acompanhamento | Risco baixo (0), atendimento em andamento, medicamento contínuo e temporário ativos |
| [DEMO] Simba 06 - inativo | Sem histórico; permite testar reativação e edição |

Os seis cenários se repetem em 60 pets, cobrindo as seis espécies. As consultas incluem os sete tipos e os quatro status.

## Roteiro de exploração

1. Abrir Início e conferir agenda do dia e consultas atrasadas.
2. Em Consultas, filtrar cada status; iniciar uma AGENDADA e concluir uma EM_ANDAMENTO, informando uma data de retorno futura.
3. Cancelar outra consulta aberta com motivo; verificar que CONCLUIDA/CANCELADA bloqueiam transições indevidas.
4. Abrir o dashboard e histórico dos pets acima; conferir alertas, vacinas, medicamentos e linha do tempo.
5. No atendimento em andamento, registrar vacina e medicamento fictícios.
6. Editar e desativar/reativar cadastros. Os tutores 31/32 e a clínica 6 permitem testar cenários sem vínculos.
7. Em Relatórios, consultar faturamento do ano atual e dos anteriores, exportar JSON e testar filtro de clínica.
8. Criar em Usuários uma conta VETERINARIO e duas TUTOR, com senhas escolhidas por você. Vincular os tutores a [DEMO] Tutor 01 e 02: o primeiro possui Luna 01 e Thor 02, o segundo Mel 03 e Nina 04. Testar permissão por perfil e a restrição de um tutor aos próprios pets.
9. Testar formulários inválidos pela interface: campos obrigatórios, CPF/CNPJ duplicados, datas incoerentes, retorno anterior ao atendimento e cadastro de usuário repetido. A carga contém apenas registros válidos; as rejeições devem ser exercitadas pelo formulário.

## Execução controlada

### Aplicar diretamente na base da versão publicada

O deploy `https://clyvovet.vercel.app` usa o projeto Supabase `zznnuwevoeuxaznplxhy`, schema `clyvovet`. A URL JDBC e o usuário foram conferidos com a configuração de produção da Vercel. O Oracle FIAP da entrega Database permanece separado.

No terminal da pasta CS4, executar:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\CS4 JAVA\Projeto-EcoCreate-Java-Challenge-Sprint-2026-parte-4\popular-deploy.ps1"
```

O comando pede somente a senha do **banco Supabase** de produção de forma oculta. Não usa a senha Oracle nem a senha do login do site. Reutiliza `CargaDemonstracaoService` do JAR já compilado, com uma única conexão/transação, lock e verificação das quantidades. Não inicia o servidor nem executa migrations. A configuração aponta exclusivamente para o mesmo projeto do deploy; o resultado fica em `docs/DADOS-DEMO-DEPLOY-RESULTADO.json`.

Se a carga já foi aplicada nessa base, o marcador evita duplicação e mantém as alterações feitas nos registros. Depois da confirmação, atualizar a página publicada: os dados vêm da mesma base e não exigem um novo deploy. `-Verificar` testa apenas a conexão e as contagens; `-SomentePreparar` somente prepara o executor sem conectar.

A carga não é uma migration do Flyway e não roda por padrão. O runner só existe no perfil postgres e exige CLYVOVET_DEMO_ENABLED=true.

Uma transação reúne todas as inserções. Em PostgreSQL, um advisory lock transacional impede duas instâncias de duplicarem a carga. O marcador da clínica 1 impede repetição e não restaura registros já editados. Não apagar o marcador para tentar repetir a carga; a carga não funciona como sincronização.

Após a aplicação e conferência dos totais, definir CLYVOVET_DEMO_ENABLED=false para os próximos deployments. O log CARGA_DEMO mostra somente os totais; nenhuma senha é necessária além da conexão já configurada no servidor.

Nenhuma conta de acesso é criada pela carga. Use seu administrador atual e cadastre contas de teste pela tela Usuários com suas próprias senhas.

## Limites da evidência

Os testes de integração verificam os relacionamentos, estados, pontuações de risco e repetição no H2. A aplicação real da carga e o resumo no PostgreSQL precisam ser conferidos pelos logs após o deploy. Popular o banco não equivale a executar todos os fluxos de UI nem a comprovar as evidências QA.
