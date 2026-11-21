package br.com.conde.somasdistintas;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;
import org.junit.jupiter.api.Test;

/** O vetor de bits das somas contra a definicao. */
class SomasTest {

    @Test
    void potenciasDeDoisTemSomasDistintas() {
        assertThat(Somas.distintas(new long[] {1, 2, 4, 8})).isTrue();
        assertThat(Juiz.distintas(new int[] {1, 2, 4, 8})).isTrue();
    }

    @Test
    void umDoisTresRepete() {
        // 1 + 2 = 3.
        assertThat(Somas.distintas(new long[] {1, 2, 3})).isFalse();
        assertThat(Juiz.distintas(new int[] {1, 2, 3})).isFalse();
    }

    @Test
    void oDeslocamentoAtravessaPalavrasDeSessentaEQuatroBits() {
        // 70 + 140 = 210 = 70 + 140; mas 1, 70, 140, 211 repete: 1 + 210 = 211.
        assertThat(Somas.distintas(new long[] {1, 70, 140})).isTrue();
        assertThat(Somas.distintas(new long[] {1, 70, 140, 211})).isFalse();
        assertThat(Somas.distintas(new long[] {64, 128, 129, 300})).isTrue();
    }

    @Test
    void quantasContaAs2aNaNSomas() {
        Somas s = Somas.vazio(100).com(3).com(5).com(6).com(7);
        assertThat(s).isNotNull();
        assertThat(s.quantas()).isEqualTo(16);
    }

    @Test
    void bateComAForcaBrutaEmConjuntosSorteados() {
        Random r = new Random(20261121L);
        for (int caso = 0; caso < 3000; caso++) {
            int n = 1 + r.nextInt(9);
            int[] a = new int[n];
            long[] b = new long[n];
            for (int i = 0; i < n; i++) {
                a[i] = 1 + r.nextInt(150);
                b[i] = a[i];
            }
            assertThat(Somas.distintas(b)).as("caso %d", caso).isEqualTo(Juiz.distintas(a));
        }
    }
}
