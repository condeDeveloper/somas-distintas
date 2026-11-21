package br.com.conde.somasdistintas;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * As tabelas do README. Cada uma roda sozinha: {@code exec:java -Dexec.args="nome"}.
 *
 * <p>As linhas de n = 8 levam minutos; {@code exec:java -Dexec.args="minimos 8"} vai ate
 * elas, e sem o numero o medidor para em 7.
 */
public final class Medidor {

    private static final List<String> TABELAS =
            List.of("minimos", "conway-guy", "otimos", "poda", "juiz");

    public static void main(String[] argumentos) {
        String tabela = argumentos.length > 0 ? argumentos[0] : "todas";
        int ate = argumentos.length > 1 ? Integer.parseInt(argumentos[1]) : 7;
        if (tabela.equals("todas")) {
            for (String nome : TABELAS) {
                roda(nome, ate);
                System.out.println();
            }
            return;
        }
        roda(tabela, ate);
    }

    private static void roda(String tabela, int ate) {
        switch (tabela) {
            case "minimos" -> minimos(ate);
            case "conway-guy" -> conwayGuy();
            case "otimos" -> otimos(ate);
            case "poda" -> poda();
            case "juiz" -> juiz();
            default -> {
                System.out.println("tabela desconhecida: " + tabela);
                System.out.println("tabelas: " + String.join(", ", TABELAS));
            }
        }
    }

    private static void minimos(int ate) {
        titulo("m(n): o menor maximo de n inteiros com somas de subconjuntos distintas");
        System.out.println("A busca parte do conjunto de Conway e Guy e prova que nao ha nenhum");
        System.out.println("com maximo menor.");
        System.out.println();
        System.out.println("n   m(n)  2^(n-1)  (2^n-1)/n  nos da prova   um otimo");
        for (int n = 1; n <= ate; n++) {
            Busca.Minimo m = Busca.minimo(n, true, true);
            System.out.printf(Locale.ROOT, "%-3d %-5d %-8d %-10s %-14s %s%s%n", n, m.maximo(),
                    1L << (n - 1), String.format(Locale.ROOT, "%.1f", ((1L << n) - 1) / (double) n),
                    pontos(m.nos()), Arrays.toString(m.exemplo()),
                    m.baixas() > 0 ? "  (abaixo de Conway e Guy)" : "");
        }
    }

    private static void conwayGuy() {
        titulo("Os conjuntos de Conway e Guy");
        System.out.println("n   u(n)        somas distintas  u(n)/2^n");
        for (int n = 1; n <= 24; n++) {
            long[] c = ConwayGuy.conjunto(n);
            boolean ok = Somas.distintas(c);
            long u = ConwayGuy.sequencia(n)[n];
            if (n <= 10 || n % 2 == 0) {
                System.out.printf(Locale.ROOT, "%-3d %-11s %-16s %.4f%n", n, pontos(u),
                        ok ? "sim" : "NAO", u / Math.pow(2, n));
            }
        }
        System.out.println();
        System.out.println("so a razao, sem conferir as somas (o vetor de bits nao cabe):");
        long[] u = ConwayGuy.sequencia(60);
        for (int n = 30; n <= 60; n += 10) {
            System.out.printf(Locale.ROOT, "%-3d %-22s %.4f%n", n, pontos(u[n]), u[n] / Math.pow(2, n));
        }
    }

    private static void otimos(int ate) {
        titulo("Todos os conjuntos otimos");
        System.out.println("n   m(n)  quantos  quais");
        int[] m = minimosConhecidos(ate);
        for (int n = 1; n <= ate; n++) {
            Busca.Resultado r = Busca.todos(n, m[n], true, true);
            StringBuilder sb = new StringBuilder();
            for (int[] c : r.conjuntos()) {
                sb.append(Arrays.toString(c));
                if (Arrays.equals(c, comoInt(ConwayGuy.conjunto(n)))) {
                    sb.append(" (Conway e Guy)");
                }
                sb.append(' ');
            }
            System.out.printf(Locale.ROOT, "%-3d %-5d %-8d %s%n", n, m[n], r.conjuntos().size(),
                    sb.toString().trim());
        }
    }

    private static void poda() {
        titulo("Quanto cada poda corta na prova de que nao ha nada abaixo de m(n)");
        System.out.println("n   teto  nada         so prefixo   so total     as duas      decrescente");
        int[] m = minimosConhecidos(7);
        for (int n = 4; n <= 7; n++) {
            int teto = m[n] - 1;
            System.out.printf(Locale.ROOT, "%-3d %-5d %-12s %-12s %-12s %-12s %s%n", n, teto,
                    pontos(Busca.algum(n, teto, false, false).nos()),
                    pontos(Busca.algum(n, teto, true, false).nos()),
                    pontos(Busca.algum(n, teto, false, true).nos()),
                    pontos(Busca.algum(n, teto, true, true).nos()),
                    pontos(Busca.algumDecrescente(n, teto).nos()));
        }
    }

    private static void juiz() {
        titulo("A busca contra a forca bruta");
        System.out.println("n   teto  combinacoes  com somas distintas  busca");
        int[][] casos = {{3, 6}, {4, 10}, {5, 13}, {5, 20}, {6, 24}, {6, 30}};
        for (int[] c : casos) {
            long juiz = Juiz.conta(c[0], c[1]);
            long busca = Busca.todos(c[0], c[1], true, true).conjuntos().size();
            System.out.printf(Locale.ROOT, "%-3d %-5d %-12s %-20s %s%n", c[0], c[1],
                    pontos(binomial(c[1], c[0])), pontos(juiz), juiz == busca ? "bate" : "DIVERGE");
        }
        System.out.println();
        for (int n = 1; n <= 5; n++) {
            System.out.println("m(" + n + ") pela forca bruta: " + Juiz.menorMaximo(n));
        }
    }

    /** m(n) de 1 ate ate, cada um provado pela busca. */
    public static int[] minimosConhecidos(int ate) {
        int[] m = new int[ate + 1];
        for (int n = 1; n <= ate; n++) {
            m[n] = Busca.minimo(n, true, true).maximo();
        }
        return m;
    }

    static int[] comoInt(long[] a) {
        int[] r = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            r[i] = (int) a[i];
        }
        return r;
    }

    /** C(a, b) em long. */
    public static long binomial(int a, int b) {
        long r = 1;
        for (int i = 1; i <= b; i++) {
            r = r * (a - b + i) / i;
        }
        return r;
    }

    // --------------------------------------------------------------- utilidades

    private static void titulo(String texto) {
        System.out.println("== " + texto);
        System.out.println();
    }

    /** 1234567 vira 1.234.567. */
    public static String pontos(long valor) {
        return String.format(Locale.ROOT, "%,d", valor).replace(',', '.');
    }
}
