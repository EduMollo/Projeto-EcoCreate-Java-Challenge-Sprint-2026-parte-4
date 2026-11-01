package br.com.fiap.clyvovet.service;
/** Contrato dos relatorios, independente do banco ativo. */
public interface RelatorioBancoService {
 String exportar(Long clinica);
 String faturamento(int ano);
}
