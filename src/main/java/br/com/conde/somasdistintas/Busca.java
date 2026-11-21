package br.com.conde.somasdistintas;

import java.util.ArrayList;
import java.util.List;

/**
 * Procura conjuntos de n inteiros positivos, todos ate um teto, com as 2^n somas de
 * subconjuntos distintas.
 *
 * <p>Os elementos entram em ordem crescente, e as somas ja feitas vivem num vetor de bits
 * ({@link Somas}): um elemento novo so entra se nenhuma soma repetir. Duas podas ligaveis:
 *
 * <ul>
 *   <li><b>prefixo</b>: os k menores tem 2^k somas distintas entre 0 e a soma deles, entao
 *       somam pelo menos 2^k - 1. Isso da o menor valor possivel do proximo elemento.
 *   <li><b>total</b>: o conjunto inteiro soma pelo menos 2^n - 1. Se nem os maiores valores
 *       que ainda cabem abaixo do teto chegam la, a busca volta.
 * </ul>
 */
public final class Busca {

    private final int n;
    private final int teto;
    private final boolean podaPrefixo;
    private final boolean podaTotal;
    private final boolean pararNoPrimeiro;
    private final int[] escolhidos;
    private final List<int[]> achados = new ArrayList<>();
    private long nos;

    private Busca(int n, int teto, boolean podaPrefixo, boolean podaTotal, boolean parar) {
        this.n = n;
        this.teto = teto;
        this.podaPrefixo = podaPrefixo;
        this.podaTotal = podaTotal;
        this.pararNoPrimeiro = parar;
        this.escolhidos = new int[n];
    }

    /** O que uma busca devolve: os conjuntos achados e os nos gastos. */
    public record Resultado(List<int[]> conjuntos, long nos) {}

    /** Todos os conjuntos de n elementos ate o teto com somas distintas. */
    public static Resultado todos(int n, int teto, boolean podaPrefixo, boolean podaTotal) {
        Busca b = new Busca(n, teto, podaPrefixo, podaTotal, false);
        b.desce(0, 1, 0L, Somas.vazio((long) n * teto));
        return new Resultado(List.copyOf(b.achados), b.nos);
    }

    /** O primeiro conjunto achado, ou lista vazia se nao existe nenhum ate o teto. */
    public static Resultado algum(int n, int teto, boolean podaPrefixo, boolean podaTotal) {
        Busca b = new Busca(n, teto, podaPrefixo, podaTotal, true);
        b.desce(0, 1, 0L, Somas.vazio((long) n * teto));
        return new Resultado(List.copyOf(b.achados), b.nos);
    }

    /** O menor maximo, partindo do conjunto de Conway e Guy e provando que nao ha menor. */
    public record Minimo(int n, int maximo, int[] exemplo, long nos, int baixas) {}

    /** m(n): o menor maximo possivel para n elementos com somas distintas. */
    public static Minimo minimo(int n, boolean podaPrefixo, boolean podaTotal) {
        long[] cg = ConwayGuy.conjunto(n);
        int melhor = (int) cg[n - 1];
        int[] exemplo = new int[n];
        for (int i = 0; i < n; i++) {
            exemplo[i] = (int) cg[i];
        }
        long nos = 0;
        int baixas = 0;
        while (true) {
            Resultado r = algum(n, melhor - 1, podaPrefixo, podaTotal);
            nos += r.nos();
            if (r.conjuntos().isEmpty()) {
                return new Minimo(n, melhor, exemplo, nos, baixas);
            }
            exemplo = r.conjuntos().get(0);
            melhor = exemplo[n - 1];
            baixas++;
        }
    }

    /**
     * A mesma pergunta com os elementos entrando do maior para o menor. Escolhidos os j
     * maiores, os r = n - j que faltam ficam abaixo do ultimo escolhido b, e precisam de
     * somas distintas entre si: somam pelo menos 2^r - 1, e no maximo (b-1) + ... + (b-r).
     */
    public static Resultado algumDecrescente(int n, int teto) {
        Busca b = new Busca(n, teto, false, false, true);
        b.desceDecrescente(0, teto, 0L, Somas.vazio((long) n * teto));
        return new Resultado(List.copyOf(b.achados), b.nos);
    }

    /** Todos, do maior para o menor. */
    public static Resultado todosDecrescente(int n, int teto) {
        Busca b = new Busca(n, teto, false, false, false);
        b.desceDecrescente(0, teto, 0L, Somas.vazio((long) n * teto));
        return new Resultado(List.copyOf(b.achados), b.nos);
    }

    private boolean desceDecrescente(int j, int maior, long soma, Somas somas) {
        nos++;
        if (j == n) {
            int[] c = new int[n];
            for (int i = 0; i < n; i++) {
                c[i] = escolhidos[n - 1 - i];
            }
            achados.add(c);
            return pararNoPrimeiro;
        }
        int r = n - j;
        for (long b = maior; b >= r; b--) {
            // os r que faltam, incluindo b, cabem em b, b-1, ..., b-r+1 e precisam somar
            // 2^r - 1 entre si e 2^n - 1 junto com os escolhidos.
            long teto = (long) r * b - (long) r * (r - 1) / 2;
            if (teto < (1L << r) - 1 || soma + teto < (1L << n) - 1) {
                return false;
            }
            Somas novas = somas.com(b);
            if (novas == null) {
                continue;
            }
            escolhidos[j] = (int) b;
            if (desceDecrescente(j + 1, (int) b - 1, soma + b, novas)) {
                return true;
            }
        }
        return false;
    }

    private boolean desce(int k, int menor, long soma, Somas somas) {
        nos++;
        if (k == n) {
            achados.add(escolhidos.clone());
            return pararNoPrimeiro;
        }
        int falta = n - k;
        if (podaTotal) {
            // os maiores que ainda cabem: teto, teto - 1, ..., teto - falta + 1.
            long maiores = (long) falta * teto - (long) falta * (falta - 1) / 2;
            if (soma + maiores < (1L << n) - 1) {
                return false;
            }
        }
        long inicio = menor;
        if (podaPrefixo) {
            inicio = Math.max(inicio, (1L << (k + 1)) - 1 - soma);
        }
        for (long a = inicio; a <= teto - (falta - 1); a++) {
            Somas novas = somas.com(a);
            if (novas == null) {
                continue;
            }
            escolhidos[k] = (int) a;
            if (desce(k + 1, (int) a + 1, soma + a, novas)) {
                return true;
            }
        }
        return false;
    }
}
