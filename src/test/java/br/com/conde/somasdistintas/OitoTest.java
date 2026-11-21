package br.com.conde.somasdistintas;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * n = 8: provar que nada fica abaixo de 84 e achar todos os otimos leva uns quatro minutos.
 * Fica fora do {@code mvn verify} e roda uma vez so, no job "pesado" do CI:
 * {@code mvn test -Dexcluir=nenhum -Dgroups=pesado}.
 */
@Tag("pesado")
class OitoTest {

    @Test
    void m8E84EConwayEGuyNaoEOUnico() {
        Busca.Minimo m = Busca.minimo(8, true, true);
        assertThat(m.maximo()).isEqualTo(84);
        assertThat(m.baixas()).isZero();
        assertThat(m.nos()).isEqualTo(101_735_242);

        List<String> otimos = Busca.todos(8, 84, true, true).conjuntos().stream()
                .map(Arrays::toString).toList();
        assertThat(otimos).containsExactlyInAnyOrder(
                "[40, 60, 71, 77, 80, 82, 83, 84]",
                "[20, 40, 71, 77, 80, 82, 83, 84]",
                "[39, 59, 70, 77, 78, 79, 81, 84]");
        assertThat(Busca.algumDecrescente(8, 83).nos()).isEqualTo(80_115_117);
    }
}
