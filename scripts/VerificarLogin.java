import java.io.Console;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** Diagnostico local de leitura: nunca imprime nem altera credenciais. */
public class VerificarLogin {
    enum Resultado {
        CONTA_AUSENTE("Conta nao encontrada. Confira o nome de usuario (diferencia maiusculas)."),
        CONTA_INATIVA("Conta encontrada, mas esta desativada."),
        FORMATO_INVALIDO("Conta encontrada, mas a senha armazenada nao esta no formato BCrypt esperado."),
        SENHA_DIFERENTE("Conta encontrada e ativa. A senha informada NAO corresponde a senha salva no Supabase."),
        SENHA_CONFERE("Conta encontrada e ativa. A senha CONFERE com o Supabase. O proximo passo e verificar a autenticacao no site.");
        final String mensagem;
        Resultado(String mensagem) { this.mensagem = mensagem; }
    }

    static Resultado verificar(Connection conexao, String usuario, String senha) throws SQLException {
        try (var consulta = conexao.prepareStatement(
                "select senha, ativo from clyvovet.tb_usuario where username = ?")) {
            consulta.setString(1, usuario);
            try (var linhas = consulta.executeQuery()) {
                if (!linhas.next()) return Resultado.CONTA_AUSENTE;
                if (!linhas.getBoolean("ativo")) return Resultado.CONTA_INATIVA;
                String hash = linhas.getString("senha");
                if (hash == null || !hash.matches("\\$2[aby]\\$\\d{2}\\$.{53}")) {
                    return Resultado.FORMATO_INVALIDO;
                }
                return new BCryptPasswordEncoder().matches(senha, hash)
                        ? Resultado.SENHA_CONFERE : Resultado.SENHA_DIFERENTE;
            }
        }
    }

    public static void main(String[] args) {
        Console terminal = System.console();
        if (terminal == null) {
            System.err.println("Execute no terminal PowerShell interativo para digitar as senhas de forma oculta.");
            System.exit(2);
        }
        char[] senhaBanco = null;
        char[] senhaSite = null;
        int resultado = 0;
        try {
            terminal.printf("Diagnostico somente de leitura. Nenhuma senha ou conta sera alterada.%n");
            String usuario = terminal.readLine("Usuario do site [admin]: ");
            if (usuario == null) return;
            if (usuario.isBlank()) usuario = "admin";
            senhaBanco = terminal.readPassword("Senha do banco Supabase (digitacao oculta): ");
            senhaSite = terminal.readPassword("Senha que voce tentou no site (digitacao oculta): ");
            if (senhaBanco == null || senhaSite == null) return;
            var propriedades = new Properties();
            propriedades.setProperty("user", "postgres.zznnuwevoeuxaznplxhy");
            propriedades.setProperty("password", new String(senhaBanco));
            propriedades.setProperty("connectTimeout", "15");
            propriedades.setProperty("socketTimeout", "20");
            try (var conexao = DriverManager.getConnection(
                    "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require",
                    propriedades)) {
                conexao.setReadOnly(true);
                terminal.printf("%nRESULTADO: %s%n", verificar(conexao, usuario, new String(senhaSite)).mensagem);
            } finally {
                propriedades.clear();
            }
        } catch (SQLException erro) {
            System.err.println("Nao foi possivel concluir a consulta ao banco (SQLSTATE " + erro.getSQLState()
                    + "). Nenhuma senha foi alterada.");
            resultado = 2;
        } finally {
            if (senhaBanco != null) Arrays.fill(senhaBanco, '\0');
            if (senhaSite != null) Arrays.fill(senhaSite, '\0');
        }
        if (resultado != 0) System.exit(resultado);
    }
}
