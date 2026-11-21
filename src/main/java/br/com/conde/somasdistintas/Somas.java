package br.com.conde.somasdistintas;

/**
 * As somas de subconjuntos como um vetor de bits: o bit s ligado quer dizer que algum
 * subconjunto soma s. Acrescentar o elemento a e fazer {@code S | (S << a)}; as somas
 * continuam distintas exatamente quando {@code S & (S << a)} e vazio.
 */
public final class Somas {

    private final long[] bits;

    private Somas(long[] bits) {
        this.bits = bits;
    }

    /** So a soma vazia, com espaco para somas ate teto. */
    public static Somas vazio(long teto) {
        long[] b = new long[(int) (teto / 64) + 2];
        b[0] = 1L;
        return new Somas(b);
    }

    /** As somas com a acrescentado, ou nulo se alguma soma repetir. */
    public Somas com(long a) {
        int palavras = (int) (a >>> 6);
        int resto = (int) (a & 63);
        long[] novo = bits.clone();
        for (int i = bits.length - 1; i >= palavras; i--) {
            int origem = i - palavras;
            long deslocado = bits[origem] << resto;
            if (resto != 0 && origem > 0) {
                deslocado |= bits[origem - 1] >>> (64 - resto);
            }
            if ((bits[i] & deslocado) != 0) {
                return null;
            }
            novo[i] |= deslocado;
        }
        return new Somas(novo);
    }

    /** Quantas somas diferentes. */
    public long quantas() {
        long c = 0;
        for (long b : bits) {
            c += Long.bitCount(b);
        }
        return c;
    }

    /** Se as somas de todos os elementos sao distintas, montando de um em um. */
    public static boolean distintas(long[] a) {
        long total = 0;
        for (long x : a) {
            total += x;
        }
        Somas s = vazio(total);
        for (long x : a) {
            s = s.com(x);
            if (s == null) {
                return false;
            }
        }
        return true;
    }
}
