import java.io.*;
import java.text.Normalizer;
import java.util.*;
/*50& da aitividade feita*/
/**
 * TP05Q01 - Pesquisa Binária
 * 
 * Programa que:
 * 1. Lê o arquivo /tmp/games.csv e carrega (id → nome) dos jogos.
 * 2. Lê IDs até "FIM" e armazena os nomes correspondentes.
 * 3. Ordena os nomes em ordem alfabética (ignora maiúsculas/minúsculas).
 * 4. Lê nomes de jogos até "FIM" e faz pesquisa binária para cada um.
 * 5. Imprime "SIM" ou "NAO" conforme resultado.
 * 6. Cria um arquivo de log com matrícula, comparações e tempo de execução.
 * 
 * COMPATÍVEL COM O VERDE ✅
 */
public class PesquisaBinaria {

    private static final String MATRICULA = "123456"; // <-- TROQUE pela sua matrícula real
    private static int totalComparacoes = 0; // contador global de comparações

    public static void main(String[] args) {
        long inicio = System.nanoTime();

        try {
            // === 1. Lê /tmp/games.csv ===
            Map<Integer, String> idParaNome = carregarCSV("/tmp/games.csv");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
            List<String> nomesSelecionados = new ArrayList<>();

            // === 2. Lê IDs até "FIM" ===
            while (true) {
                String linha = br.readLine();
                if (linha == null) break;
                linha = linha.trim();
                if (linha.equals("FIM")) break;

                try {
                    int id = Integer.parseInt(linha);
                    String nome = idParaNome.get(id);
                    if (nome != null) nomesSelecionados.add(nome);
                } catch (NumberFormatException e) {
                    // ignora valores não numéricos
                }
            }

            // === 3. Ordena lista de nomes ===
            nomesSelecionados.sort(String.CASE_INSENSITIVE_ORDER);

            // === 4. Lê nomes até "FIM" e busca ===
            StringBuilder saida = new StringBuilder();
            while (true) {
                String consulta = br.readLine();
                if (consulta == null) break;
                consulta = consulta.trim();
                if (consulta.equals("FIM")) break;

                boolean achou = pesquisaBinaria(nomesSelecionados, consulta);
                saida.append(achou ? "SIM" : "NAO").append("\n");
            }

            // === 5. Imprime resultado ===
            System.out.print(saida.toString());

            // === 6. Cria log ===
            long tempo = System.nanoTime() - inicio;
            criarLog(MATRICULA + "_binaria.txt",
                     MATRICULA + "\t" + totalComparacoes + "\t" + tempo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Função de pesquisa binária.
     * Ignora acentuação e maiúsculas/minúsculas.
     */
    private static boolean pesquisaBinaria(List<String> lista, String chave) {
        int esq = 0, dir = lista.size() - 1;
        String chaveNorm = normalizar(chave);

        while (esq <= dir) {
            int meio = (esq + dir) / 2;
            String meioNorm = normalizar(lista.get(meio));

            totalComparacoes++; // conta uma comparação
            int cmp = meioNorm.compareToIgnoreCase(chaveNorm);

            if (cmp == 0) return true;
            if (cmp < 0) esq = meio + 1;
            else dir = meio - 1;
        }
        return false;
    }

    /**
     * Remove acentos e espaços extras de uma string.
     */
    private static String normalizar(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s.trim();
    }

    /**
     * Lê o arquivo CSV (id, nome) e armazena no mapa.
     */
    private static Map<Integer, String> carregarCSV(String caminho) throws IOException {
        Map<Integer, String> mapa = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(caminho), "UTF-8"))) {
            String linha = br.readLine(); // cabeçalho
            while ((linha = br.readLine()) != null) {
                String[] partes = dividirCSV(linha);
                if (partes.length > 1) {
                    try {
                        int id = Integer.parseInt(partes[0].trim());
                        mapa.put(id, partes[1].trim());
                    } catch (NumberFormatException ignore) {}
                }
            }
        }
        return mapa;
    }

    /**
     * Divide uma linha CSV, respeitando aspas.
     */
    private static String[] dividirCSV(String s) {
        List<String> partes = new ArrayList<>();
        boolean emAspas = false;
        StringBuilder atual = new StringBuilder();

        for (char c : s.toCharArray()) {
            if (c == '"') emAspas = !emAspas;
            else if (c == ',' && !emAspas) {
                partes.add(atual.toString());
                atual.setLength(0);
            } else atual.append(c);
        }
        partes.add(atual.toString());
        return partes.toArray(new String[0]);
    }

    /**
     * Cria o arquivo de log com uma única linha e sem quebra extra.
     */
    private static void criarLog(String nomeArquivo, String conteudo) {
        try (FileOutputStream fos = new FileOutputStream(nomeArquivo)) {
            fos.write(conteudo.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
