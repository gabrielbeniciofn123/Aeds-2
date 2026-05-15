import java.io.*;
import java.util.*;

// ordenacao parcial por selecao
// a ideia e simples: ao inves de ordenar o array intero, a gente so ordena
// os primeiros k elementos. isso e util quando voce quer so os k menores
// sem se preocupar com o resto da lista

public class SelecaoParcial {

    // variaveis globais pra contar as operacoes do algoritmo
    // o professor pediu pra registrar isso no log
    static long comparacoes   = 0;
    static long movimentacoes = 0;

    // classe interna que representa um registro do nosso dataset
    // cada linha do arquivo .txt vira um objeto desse tipo
    static class Registro {
        String nome;
        String email;
        String curso;
        int    idade;

        Registro(String nome, int idade, String email, String curso) {
            this.nome  = nome;
            this.idade = idade;
            this.email = email;
            this.curso = curso;
        }

        // formata igual ao arquivo orignal pra nao baguncar a saida
        public String toString() {
            return nome + ";" + idade + ";" + email + ";" + curso;
        }
    }

    // metodo principal do algoritmo
    // recebe o array e o k, e ordena so ate a posicao k
    // o restante do array pode ta em qualquer ordem, nao importa
    static void selecaoParcial(Registro[] arr, int k) {
        int n = arr.length;

        // loop externo vai de 0 ate k (ou ate o fim do array se k > n)
        for (int i = 0; i < k && i < n; i++) {

            // começa assumindo que o menor esta na posicao i mesmo
            int minIdx = i;

            // varre o restante procurando alguem menor
            for (int j = i + 1; j < n; j++) {
                comparacoes++; // cada comparacao de nome conta

                if (arr[j].nome.compareTo(arr[minIdx].nome) < 0) {
                    minIdx = j;
                }
            }

            // so troca se realmente encontrou um menor la na frente
            if (minIdx != i) {
                Registro tmp = arr[minIdx];
                arr[minIdx]  = arr[i];
                arr[i]       = tmp;
                movimentacoes += 3; // uma troca envolve 3 atribuicoes
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String matricula = "889921";
        int k = 10;
         // so precisa dos 10 primeiros ordenados

        // le o dataset e monta a lista de registros
        List<Registro> lista = new ArrayList<>();
        BufferedReader br    = new BufferedReader(new FileReader("dataset.txt"));
        String linha;

        while ((linha = br.readLine()) != null) {
            String[] partes = linha.split(";");
            if (partes.length == 4) {
                lista.add(new Registro(
                    partes[0].trim(),
                    Integer.parseInt(partes[1].trim()),
                    partes[2].trim(),
                    partes[3].trim()
                ));
            }
        }
        br.close();

        // transforma em array porque e mais facil de indexar
        Registro[] arr = lista.toArray(new Registro[0]);

        // cronometra so o algoritmo em si, nao a leitura do arquivo
        long inicio = System.nanoTime();
        selecaoParcial(arr, k);
        long fim    = System.nanoTime();

        double tempo = (fim - inicio) / 1e9;

        // exibe os k menores registros ordenados por nome
        System.out.println("=== Ordenacao Parcial por Selecao (k=" + k + ") ===");
        for (int i = 0; i < k && i < arr.length; i++) {
            System.out.println((i + 1) + ". " + arr[i]);
        }
        System.out.printf("Comparacoes: %d | Movimentacoes: %d | Tempo: %.9fs%n",
                comparacoes, movimentacoes, tempo);

        // salva o arquivo de log no formato exigido
        // uma unica linha separada por tabulacao
        PrintWriter pw = new PrintWriter(matricula + "_selecao_parcial.txt");
        pw.printf("%s\t%d\t%d\t%.9f%n", matricula, comparacoes, movimentacoes, tempo);
        pw.close();

        System.out.println("Log salvo: " + matricula + "_selecao_parcial.txt");
    }
}