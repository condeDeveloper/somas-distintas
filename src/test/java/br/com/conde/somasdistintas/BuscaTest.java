package br.com.conde.somasdistintas;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/** A busca contra a forca bruta, e as ordens e podas umas contra as outras. */
class BuscaTest {

    @Test
    void aContagemBateComTodasAsCombinacoes() {
        int[][] casos = {{3, 6}, {4, 10}, {5, 13}, {5, 20}, {6, 24}};
        for (int[] c : casos) {
            assertThat(Busca.todos(c[0], c[1], true, true).conjuntos())
                    .as("n=%d teto=%d", c[0], c[1]).hasSize((int) Juiz.conta(c[0], c[1]));
        }
    }

    @Test
    void oMinimoBateComAForcaBrutaAte5() {
        for (int n = 1; n <= 5; n++) {
            assertThat(Busca.minimo(n, true, true).maximo()).as("n=%d", n)
                    .isEqualTo(Juiz.menorMaximo(n));
        }
    }

    @Test
    void asDuasOrdensAchamOsMesmosOtimos() {
        int[] m = Medidor.minimosConhecidos(7);
        for (int n = 1; n <= 7; n++) {
            List<String> crescente = texto(Busca.todos(n, m[n], true, true).conjuntos());
            List<String> decrescente = texto(Busca.todosDecrescente(n, m[n]).conjuntos());
            assertThat(decrescente).as("n=%d", n).containsExactlyInAnyOrderElementsOf(crescente);
        }
    }

    @Test
    void asPodasNaoMudamAResposta() {
        for (int n = 3; n <= 6; n++) {
            int teto = 2 * n + 6;
            int base = Busca.todos(n, teto, false, false).conjuntos().size();
            assertThat(Busca.todos(n, teto, true, false).conjuntos()).hasSize(base);
            assertThat(Busca.todos(n, teto, false, true).conjuntos()).hasSize(base);
            assertThat(Busca.todos(n, teto, true, true).conjuntos()).hasSize(base);
        }
    }

    @Test
    void todoConjuntoAchadoPassaNoJuiz() {
        for (int[] c : Busca.todos(6, 30, true, true).conjuntos()) {
            assertThat(Juiz.distintas(c)).as(Arrays.toString(c)).isTrue();
        }
    }

    @Test
    void umaBuscaSemAMascaraSeriaPega() {
        // controle: 5 numeros ate 12 existem aos montes, com somas distintas nenhum.
        assertThat(Juiz.conta(5, 12)).isZero();
        assertThat(Busca.algum(5, 12, true, true).conjuntos()).isEmpty();
        assertThat(Busca.algum(5, 13, true, true).conjuntos()).isNotEmpty();
    }

    private static List<String> texto(List<int[]> conjuntos) {
        return conjuntos.stream().map(Arrays::toString).toList();
    }
}
