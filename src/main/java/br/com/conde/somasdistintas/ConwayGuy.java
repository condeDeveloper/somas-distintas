package br.com.conde.somasdistintas;

/**
 * A construcao de Conway e Guy (1968): u(0) = 0, u(1) = 1 e
 * u(k+1) = 2 u(k) - u(k - r), com r o inteiro mais proximo de raiz de 2k. O conjunto de
 * n elementos e { u(n) - u(i) : 0 <= i < n }.
 */
public final class ConwayGuy {

    private ConwayGuy() {}

    /** u(0) ate u(n). */
    public static long[] sequencia(int n) {
        long[] u = new long[n + 1];
        if (n >= 1) {
            u[1] = 1;
        }
        for (int k = 1; k < n; k++) {
            int r = (int) Math.round(Math.sqrt(2.0 * k));
            u[k + 1] = 2 * u[k] - u[k - r];
        }
        return u;
    }

    /** O conjunto de n elementos, em ordem crescente. */
    public static long[] conjunto(int n) {
        long[] u = sequencia(n);
        long[] a = new long[n];
        for (int i = 0; i < n; i++) {
            a[i] = u[n] - u[n - 1 - i];
        }
        return a;
    }
}
