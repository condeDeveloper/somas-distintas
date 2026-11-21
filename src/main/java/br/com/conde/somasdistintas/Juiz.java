package br.com.conde.somasdistintas;

import java.util.HashSet;
import java.util.Set;

/**
 * A definicao ao pe da letra: soma cada um dos 2^n subconjuntos e guarda num conjunto. Se
 * dois deram a mesma soma, o conjunto cresce menos que 2^n.
 *
 * <p>Nao usa mascara de bits nem a ordem da {@link Busca}. Lento de proposito.
 */
public final class Juiz {

    private Juiz() {}

    /** Se as 2^n somas de subconjuntos sao todas diferentes. */
    public static boolean distintas(int[] a) {
        if (a.length > 24) {
            throw new IllegalArgumentException("forca bruta so ate 24 elementos");
        }
        Set<Long> vistas = new HashSet<>();
        for (int m = 0; m < 1 << a.length; m++) {
            long soma = 0;
            for (int i = 0; i < a.length; i++) {
                if ((m >>> i & 1) != 0) {
                    soma += a[i];
                }
            }
            if (!vistas.add(soma)) {
                return false;
            }
        }
        return true;
    }

    /**
     * O menor maximo para n elementos, olhando todas as combinacoes de n numeros de 1 a M
     * para M = 1, 2, 3, ... Serve ate n = 5.
     */
    public static int menorMaximo(int n) {
        for (int teto = n; ; teto++) {
            if (existe(n, teto)) {
                return teto;
            }
        }
    }

    /** Quantas combinacoes de n numeros de 1 a teto tem somas distintas. */
    public static long conta(int n, int teto) {
        int[] escolha = new int[n];
        for (int i = 0; i < n; i++) {
            escolha[i] = i + 1;
        }
        long total = 0;
        if (n > teto) {
            return 0;
        }
        while (true) {
            if (distintas(escolha)) {
                total++;
            }
            int i = n - 1;
            while (i >= 0 && escolha[i] == teto - n + 1 + i) {
                i--;
            }
            if (i < 0) {
                return total;
            }
            escolha[i]++;
            for (int j = i + 1; j < n; j++) {
                escolha[j] = escolha[j - 1] + 1;
            }
        }
    }

    private static boolean existe(int n, int teto) {
        return conta(n, teto) > 0;
    }
}
