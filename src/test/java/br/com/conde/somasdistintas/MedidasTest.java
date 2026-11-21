package br.com.conde.somasdistintas;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Os numeros que estao escritos no README, presos aqui. O caso de oito elementos, que leva
 * minutos, esta em {@code OitoTest}.
 */
class MedidasTest {

    @Test
    void osMinimosDe1a7() {
        int[] esperado = {0, 1, 2, 4, 7, 13, 24, 44};
        assertThat(Medidor.minimosConhecidos(7)).containsExactly(esperado);
    }

    @Test
    void conwayEGuySaoOtimosAte7EAMascaraNuncaDesceuAbaixoDeles() {
        for (int n = 1; n <= 7; n++) {
            Busca.Minimo m = Busca.minimo(n, true, true);
            assertThat(m.baixas()).as("n=%d", n).isZero();
            assertThat((long) m.maximo()).isEqualTo(ConwayGuy.sequencia(n)[n]);
        }
    }

    @Test
    void conwayEGuyTemSomasDistintasAte24() {
        for (int n = 1; n <= 24; n++) {
            assertThat(Somas.distintas(ConwayGuy.conjunto(n))).as("n=%d", n).isTrue();
        }
        for (int n = 1; n <= 14; n++) {
            assertThat(Juiz.distintas(Medidor.comoInt(ConwayGuy.conjunto(n)))).as("n=%d", n).isTrue();
        }
    }

    @Test
    void aRazaoDeConwayEGuy() {
        long[] u = ConwayGuy.sequencia(60);
        assertThat(u[24]).isEqualTo(4_172_701);
        assertThat(u[24] / Math.pow(2, 24)).isBetween(0.2486, 0.2488);
        assertThat(u[60] / Math.pow(2, 60)).isBetween(0.2362, 0.2364);
        for (int n = 4; n <= 60; n++) {
            assertThat(u[n] / Math.pow(2, n)).as("n=%d", n).isLessThan(u[n - 1] / Math.pow(2, n - 1));
        }
    }

    @Test
    void quantosOtimos() {
        int[] m = Medidor.minimosConhecidos(7);
        int[] quantos = {0, 1, 1, 2, 1, 2, 1, 1};
        for (int n = 1; n <= 7; n++) {
            assertThat(Busca.todos(n, m[n], true, true).conjuntos()).as("n=%d", n).hasSize(quantos[n]);
        }
        assertThat(Busca.todos(5, 13, true, true).conjuntos().get(0)).containsExactly(3, 6, 11, 12, 13);
    }

    @Test
    void aTabelaDaPoda() {
        assertThat(Busca.algum(7, 43, false, false).nos()).isEqualTo(506_881);
        assertThat(Busca.algum(7, 43, true, false).nos()).isEqualTo(506_881);
        assertThat(Busca.algum(7, 43, false, true).nos()).isEqualTo(506_412);
        assertThat(Busca.algum(7, 43, true, true).nos()).isEqualTo(506_412);
        assertThat(Busca.algumDecrescente(7, 43).nos()).isEqualTo(359_429);
        for (int n = 4; n <= 6; n++) {
            int teto = Medidor.minimosConhecidos(n)[n] - 1;
            assertThat(Busca.algum(n, teto, true, false).nos()).as("n=%d", n)
                    .isEqualTo(Busca.algum(n, teto, false, false).nos());
        }
    }

    @Test
    void aForcaBrutaDaTabelaDoJuiz() {
        assertThat(Juiz.conta(5, 20)).isEqualTo(1_328);
        assertThat(Juiz.conta(6, 24)).isEqualTo(1);
    }
}
