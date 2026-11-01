package br.com.fiap.clyvovet.security;
import java.io.*;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import br.com.fiap.clyvovet.model.*;
import br.com.fiap.clyvovet.model.enums.PerfilEnum;
class SessaoSerializacaoTest {
 @Test void principalMantemIdentidadeEPermissoesAposPersistenciaDaSessao() throws Exception {
  var tutor = Tutor.builder().id(12L).build();
  var usuario = Usuario.builder().id(7L).username("tutor-teste").senha("hash-de-teste")
   .nomeExibicao("Tutor de teste").ativo(true).tutor(tutor)
   .perfis(Set.of(new Perfil(3L, PerfilEnum.TUTOR))).build();
  var principal = new UsuarioAutenticado(usuario);
  var bytes = new ByteArrayOutputStream();
  try (var out = new ObjectOutputStream(bytes)) { out.writeObject(principal); }
  UsuarioAutenticado restaurado;
  try (var in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
   restaurado = (UsuarioAutenticado) in.readObject();
  }
  assertThat(restaurado.getId()).isEqualTo(7L);
  assertThat(restaurado.getIdTutor()).isEqualTo(12L);
  assertThat(restaurado.getUsername()).isEqualTo("tutor-teste");
  assertThat(restaurado.isTutor()).isTrue();
  assertThat(restaurado.isAdmin()).isFalse();
  assertThat(restaurado.getAuthorities()).extracting(a -> a.getAuthority()).containsExactly("TUTOR");
 }
}
