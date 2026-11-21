# somas-distintas

Escolha n inteiros positivos de modo que dois subconjuntos diferentes nunca tenham a mesma
soma. As potências de dois resolvem: 1, 2, 4, 8, e o maior é 2^(n−1). Dá para fazer com o
maior elemento menor que isso?

Dá. Com quatro números, {3, 5, 6, 7} funciona e o maior é 7, não 8. Erdős conjecturou que o
maior elemento m(n) nunca fica abaixo de c·2^n para alguma constante c, e isso continua em
aberto. Conway e Guy publicaram em 1968 uma construção que, nos n pequenos medidos aqui, atinge
o mínimo. Este repositório prova m(n) por busca exata até n = 8, conta todos os conjuntos
ótimos, confere a construção de Conway e Guy até n = 24 e mede o que cada poda corta.

```bash
mvn -B -ntp verify                                  # 18 testes (+1 pesado, abaixo)
mvn -B -ntp test -Dexcluir=nenhum -Dgroups=pesado   # n = 8, uns quatro minutos
mvn -B -ntp exec:java -Dexec.args="minimos"         # m(n) até 7 ("minimos 8" vai até 8)
mvn -B -ntp exec:java -Dexec.args="conway-guy"      # a construção conferida até 24
mvn -B -ntp exec:java -Dexec.args="otimos"          # todos os conjuntos ótimos
mvn -B -ntp exec:java -Dexec.args="poda"            # o que cada poda corta
mvn -B -ntp exec:java -Dexec.args="juiz"            # a busca contra a força bruta
```

Java 21, sem dependência fora de JUnit e AssertJ.

## Quatro achados

1. **m(8) = 84**: nenhum conjunto de oito inteiros com somas distintas tem o maior elemento
   abaixo de 84, e a prova custou 101.735.242 nós. De 1 a 8, m(n) é 1, 2, 4, 7, 13, 24,
   44, 84, e em todos o conjunto de Conway e Guy atinge o mínimo.
2. **Em n = 8 há três conjuntos ótimos, e só um é o de Conway e Guy.** Os outros são
   [20, 40, 71, 77, 80, 82, 83, 84] e [39, 59, 70, 77, 78, 79, 81, 84]. Em n = 4, 6 e 7 o
   ótimo é único.
3. **A poda do prefixo não corta um nó sequer.** Os k menores elementos têm 2^k somas
   distintas, então somam pelo menos 2^k − 1: é a desigualdade dos livros. Ligada ou
   desligada, a prova de n = 7 gasta os mesmos 506.881 nós, porque a máscara de somas já
   recusa os mesmos candidatos na hora.
4. **A razão u(n)/2^n de Conway e Guy cai em todo passo de n = 4 a n = 60**, de 0,4375
   para 0,2363. As somas distintas foram conferidas até n = 24, onde u(24) = 4.172.701.

## A busca

As somas já feitas vivem num vetor de bits: o bit s ligado quer dizer que algum
subconjunto soma s. Um elemento novo a entra se `S & (S << a)` for vazio, e as somas
passam a ser `S | (S << a)`. O deslocamento atravessa as palavras de 64 bits, e o teste
confere contra a definição em 3.000 conjuntos sorteados.

Para achar m(n), a busca parte do conjunto de Conway e Guy, com maior elemento u(n), e
procura um conjunto inteiro abaixo dele. Se achasse, desceria de novo a partir do achado.
Não achou em nenhum n de 1 a 8.

```
n   m(n)  2^(n-1)  (2^n-1)/n  nós da prova   um ótimo
4   7     8        3.8        14             [3, 5, 6, 7]
5   13    16       6.2        241            [6, 9, 11, 12, 13]
6   24    32       10.5       7.884          [11, 17, 20, 22, 23, 24]
7   44    64       18.1       506.412        [20, 31, 37, 40, 42, 43, 44]
8   84    128      31.9       101.735.242    [40, 60, 71, 77, 80, 82, 83, 84]
```

A coluna (2^n−1)/n é o piso que sai da conta mais simples: são 2^n somas distintas entre 0
e n·m. O mínimo verdadeiro fica bem acima dela e bem abaixo das potências de dois.

## Os ótimos

```
n   m(n)  quantos  quais
3   4     2        [1, 2, 4] [2, 3, 4]
4   7     1        [3, 5, 6, 7]
5   13    2        [3, 6, 11, 12, 13] [6, 9, 11, 12, 13]
6   24    1        [11, 17, 20, 22, 23, 24]
7   44    1        [20, 31, 37, 40, 42, 43, 44]
8   84    3        [40, 60, 71, 77, 80, 82, 83, 84] [20, 40, 71, 77, 80, 82, 83, 84]
                   [39, 59, 70, 77, 78, 79, 81, 84]
```

Em toda linha um dos ótimos é o de Conway e Guy; em n = 3 e n = 5 ele é o segundo
listado. O segundo de n = 8 troca o 60 do de Conway e Guy por 20, o
que deixa 20 e 40 juntos, um o dobro do outro; o terceiro não divide com ele nada além
do 77 e do 84.

## Conway e Guy

A sequência começa em u(0) = 0, u(1) = 1 e segue u(k+1) = 2u(k) − u(k−r), com r o inteiro
mais próximo de √(2k). O conjunto de n elementos é {u(n) − u(i) : i < n}.

```
n   u(n)        somas distintas  u(n)/2^n
8   84          sim              0.3281
12  1.164       sim              0.2842
16  17.305      sim              0.2641
20  267.420     sim              0.2550
24  4.172.701   sim              0.2487
40  262.885.436.596              0.2391
60  272.410.968.267.083.196      0.2363
```

Acima de 24 o vetor de bits das somas, que tem um bit por soma possível, fica grande demais
para copiar a cada elemento, e a tabela mostra só a razão, sem dizer que as somas foram
conferidas.

## O que cada poda corta

Nós para provar que não há conjunto abaixo de m(n):

```
n   teto  nada         só prefixo   só total     as duas      decrescente
4   6     16           16           14           14           7
5   12    245          245          241          241          140
6   23    7.913        7.913        7.884        7.884        5.249
7   43    506.881      506.881      506.412      506.412      359.429
8   83                                           101.735.242  80.115.117
```

A poda do total (o conjunto inteiro soma pelo menos 2^n − 1) corta 469 nós em 506.881. O
que corta de verdade é a ordem: escolher do maior para o menor gasta 29% menos nós em
n = 7 e 21% menos em n = 8. Em n = 8 a
ordem decrescente gastou menos nós e mais tempo de relógio na minha máquina, então a busca
principal ficou na ordem crescente.

## O juiz

`Juiz` soma os 2^n subconjuntos de cada candidato e guarda as somas num `HashSet`. Ele
conta todas as combinações de n números até um teto e acha m(n) olhando teto por teto:

```
n   teto  combinações  com somas distintas  busca
4   10    210          55                   bate
5   20    15.504       1.328                bate
6   24    134.596      1                    bate
6   30    593.775      1.145                bate
```

Até n = 5 o m(n) da força bruta é o mesmo da busca.

## O que as medidas me corrigiram

**Copiei a recorrência de Conway e Guy com o índice deslocado.** Escrevi u(k+1−r) onde é
u(k−r), e o conjunto de oito elementos saiu [0, 0, 0, 0, 0, 0, 0, 1]. A conferência de
somas distintas recusou todos de n = 2 em diante.

**Eu achava que a poda do prefixo ia ajudar um pouco.** Ela pula candidatos pequenos
demais para o próximo elemento. Mas cada um desses candidatos já repete uma soma, e a
máscara recusa todos antes de abrir um nó. A tabela mostra o mesmo número nas duas colunas
em todo n.

## Dentro

| arquivo | o que faz |
|---|---|
| `Somas` | as somas de subconjuntos num vetor de bits, com deslocamento entre palavras |
| `ConwayGuy` | a sequência u(n) e o conjunto de n elementos |
| `Busca` | as duas ordens, as duas podas, m(n) e a contagem dos ótimos |
| `Juiz` | os 2^n subconjuntos somados um por um; não importa a busca |
| `Medidor` | as cinco tabelas acima |

18 testes no `verify` e 1 pesado no job próprio do CI. Os números deste README estão
presos em `MedidasTest` e `OitoTest`.

## Licença

MIT.
