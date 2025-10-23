import java.io.*;      // Importa classes para leitura/escrita de arquivos
import java.util.*;    // Importa classes utilitárias (List, ArrayList, Set, etc.)

public class mergesort {

    // Matrícula do aluno - será usada no nome do arquivo de log
    private static final String MATRICULA = "891378";

    // Contadores globais para estatísticas do algoritmo
    private static long comparisons = 0;  // Conta número de comparações feitas
    private static long movements = 0;     // Conta número de movimentações de elementos

    public static void main(String[] args) throws Exception {
        // Abre o arquivo CSV para leitura
        BufferedReader br = new BufferedReader(new FileReader("/tmp/games.csv"));
        String header = br.readLine(); 
        List<Game> lista = new ArrayList<>();
        String linha;
        while ((linha = br.readLine()) != null) {
            List<String> fields = CsvParser.parseLine(linha);
            if (fields.size() < 14) continue;
            lista.add(Game.fromCsvFields(fields));
        }
        br.close();

        // DETECÇÃO RÁPIDA DE DUPLICATAS (opcional)
        Map<Integer,Integer> counts = new HashMap<>();
        for (Game g : lista) counts.put(g.id, counts.getOrDefault(g.id, 0) + 1);
        for (Map.Entry<Integer,Integer> e : counts.entrySet()) {
            if (e.getValue() > 1) System.err.println("Duplicado AppID=" + e.getKey() + " vezes=" + e.getValue());
        }

        // REMOVER DUPLICATAS: mantém a primeira ocorrência de cada AppID
        Map<Integer, Game> uniq = new LinkedHashMap<>();
        for (Game g : lista) {
            if (!uniq.containsKey(g.id)) uniq.put(g.id, g);
        }
        Game[] arr = uniq.values().toArray(new Game[0]);

        // PREPARA ARRAY ORDENADO POR ID PARA BUSCA BINÁRIA
        Game[] arrById = Arrays.copyOf(arr, arr.length);
        Arrays.sort(arrById, new Comparator<Game>() {
            @Override public int compare(Game a, Game b) { return Integer.compare(a.id, b.id); }
        });

        // LÊ IDS DA ENTRADA PADRÃO E MONTA O VETOR DE PESQUISA USANDO BUSCA BINÁRIA
        Scanner sc = new Scanner(System.in);
        Game pesquisaTemp[] = new Game[100];
        int pesquisaAux = 0;
        while (sc.hasNextLine()) {
            String buscaId = sc.nextLine().trim();
            if (buscaId.equals("FIM")) break;
            if (buscaId.isEmpty()) continue;
            try {
                int idBusca = Integer.parseInt(buscaId);
                int idx = binarySearchById(arrById, idBusca);
                if (idx >= 0) pesquisaTemp[pesquisaAux++] = arrById[idx];
            } catch (NumberFormatException e) { /* ignora */ }
        }
        sc.close();

        // copia apenas os pesquisados para um array sem nulls e ordena por preço (mergeSort)
        Game[] pesquisa = Arrays.copyOf(pesquisaTemp, pesquisaAux);
        long t0 = System.nanoTime();
        if (pesquisa.length > 1) mergeSort(pesquisa);
        long t1 = System.nanoTime();
        long tempoNano = t1 - t0;

        // IMPRESSÃO: 5 preços mais caros (pulando duplicatas por ID)
        System.out.println("| 5 preços mais caros |");
        Set<Integer> usedIds = new HashSet<>();
        int printed = 0;

        for (int i = pesquisa.length - 1; i >= 0 && printed < 5; i--) {
            Game g = pesquisa[i];
            if (g == null) continue;
            if (usedIds.contains(g.id)) continue;
            System.out.println(g.toString());
            usedIds.add(g.id);
            printed++;
        }
        System.out.println();

        // IMPRESSÃO: 5 preços mais baratos (pulando duplicatas)
        System.out.println("| 5 preços mais baratos |");
        usedIds.clear();
        printed = 0;
        for (int i = 0; i < pesquisa.length && printed < 5; i++) {
            Game g = pesquisa[i];
            if (g == null) continue;
            if (usedIds.contains(g.id)) continue;
            System.out.println(g.toString());
            usedIds.add(g.id);
            printed++;
        }

        // GERAÇÃO DO ARQUIVO DE LOG
        String logName = MATRICULA + "_mergesort.txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(logName))) {
            pw.printf("%s %d %d %d\n", MATRICULA, comparisons, movements, tempoNano);
        }
    }

    // === MÉTODO PRINCIPAL DO MERGESORT ===
    // Ponto de entrada para ordenar o array
    private static void mergeSort(Game[] a) {
        // Se array for nulo ou ter menos de 2 elementos, já está ordenado
        if (a == null || a.length < 2) return;
        
        // Cria array auxiliar do mesmo tamanho (usado no merge)
        Game[] aux = new Game[a.length];
        
        // Chama a função recursiva passando limites inicial e final
        mergesort(a, aux, 0, a.length - 1);
    }

    // === FUNÇÃO RECURSIVA DO MERGESORT ===
    // Divide o array em metades e ordena recursivamente
    private static void mergesort(Game[] a, Game[] aux, int left, int right) {
        // Caso base: se left >= right, subarray tem 0 ou 1 elemento (já ordenado)
        if (left >= right) return;
        
        // Calcula o índice do meio
        int mid = (left + right) / 2;
        
        // Ordena recursivamente a metade esquerda
        mergesort(a, aux, left, mid);
        
        // Ordena recursivamente a metade direita
        mergesort(a, aux, mid + 1, right);
        
        // Mescla as duas metades ordenadas
        merge(a, aux, left, mid, right);
    }

    // === FUNÇÃO DE MESCLAGEM (MERGE) ===
    // Combina duas metades ordenadas em uma única sequência ordenada
    private static void merge(Game[] a, Game[] aux, int left, int mid, int right) {
        // Copia elementos do intervalo [left, right] para o array auxiliar
        for (int k = left; k <= right; k++) {
            aux[k] = a[k];  // Copia elemento por elemento
        }
        
        // i: índice da metade esquerda (começa em left)
        int i = left;
        
        // j: índice da metade direita (começa em mid+1)
        int j = mid + 1;
        
        // k: índice de inserção no array original
        int k = left;
        
        // Mescla enquanto houver elementos em ambas as metades
        while (i <= mid && j <= right) {
            // Incrementa contador de comparações
            comparisons++;
            
            // Se elemento da esquerda <= elemento da direita
            if (compareAux(aux[i], aux[j]) <= 0) {
                a[k++] = aux[i++];  // Copia da esquerda e avança ambos índices
                movements++;         // Conta a movimentação
            } else {
                a[k++] = aux[j++];  // Copia da direita e avança ambos índices
                movements++;         // Conta a movimentação
            }
        }
        
        // Copia elementos restantes da metade esquerda (se houver)
        while (i <= mid) {
            a[k++] = aux[i++];  // Copia e avança
            movements++;         // Conta a movimentação
        }
        
        // Copia elementos restantes da metade direita (se houver)
        while (j <= right) {
            a[k++] = aux[j++];  // Copia e avança
            movements++;         // Conta a movimentação
        }
    }

    // === FUNÇÃO DE COMPARAÇÃO ===
    // Compara dois jogos: primeiro por preço, depois por AppID (desempate)
    // Retorna: -1 se x < y, 0 se x == y, 1 se x > y
    private static int compareAux(Game x, Game y) {
        // Compara preços
        if (Float.compare(x.price, y.price) < 0) return -1;  // x mais barato
        if (Float.compare(x.price, y.price) > 0) return 1;   // x mais caro
        
        // Preços são iguais, desempata por AppID (ordem crescente)
        if (x.id < y.id) return -1;  // x tem ID menor
        if (x.id > y.id) return 1;   // x tem ID maior
        return 0;  // IDs iguais (empate total)
    }

    // ========== CLASSES AUXILIARES ==========
    
    // === PARSER DE CSV ===
    // Classe responsável por fazer parse correto de linhas CSV
    // Trata aspas duplas, vírgulas dentro de campos, etc.
    public static class CsvParser {
        
        // Converte uma linha CSV em lista de campos (strings)
        public static List<String> parseLine(String line) {
            // Lista que armazenará os campos extraídos
            List<String> out = new ArrayList<>();
            
            // Se linha for nula, retorna lista vazia
            if (line == null) return out;
            
            // StringBuilder para construir o campo atual
            StringBuilder cur = new StringBuilder();
            
            // Flag que indica se estamos dentro de aspas
            boolean inQuotes = false;
            
            // Percorre cada caractere da linha
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);  // Pega o caractere atual
                
                // Trata aspas duplas
                if (c == '"') {
                    // Se estamos dentro de aspas E o próximo char também é ", é escape ("")
                    if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');  // Adiciona uma aspa ao campo
                        i++;              // Pula o próximo caractere
                    } else {
                        // Alterna o estado (abre/fecha aspas)
                        inQuotes = !inQuotes;
                    }
                }
                // Trata vírgula (separador de campos) somente se fora de aspas
                else if (c == ',' && !inQuotes) {
                    out.add(cur.toString());  // Adiciona campo completo à lista
                    cur.setLength(0);         // Limpa o buffer para o próximo campo
                }
                // Qualquer outro caractere: adiciona ao campo atual
                else {
                    cur.append(c);
                }
            }
            
            // Adiciona o último campo (após a última vírgula)
            out.add(cur.toString());
            
            // Retorna lista com todos os campos
            return out;
        }
    }

    // === CLASSE GAME ===
    // Representa um jogo com todos os seus atributos
    public static class Game {
        public int id;
        public String name;
        public String releaseDate;
        public int estimatedOwners;
        public float price;
        public List<String> supportedLanguages = new ArrayList<>();
        public int metacriticScore;
        public float userScore;
        public int achievements;
        public List<String> publishers = new ArrayList<>();
        public List<String> developers = new ArrayList<>();
        public List<String> categories = new ArrayList<>();
        public List<String> genres = new ArrayList<>();
        public List<String> tags = new ArrayList<>();

        public static Game fromCsvFields(List<String> f) {
            Game g = new Game();
            g.id = parseIntDefault(get(f,0), 0);
            g.name = unquote(get(f,1));
            g.releaseDate = normalizeDate(get(f,2));
            g.estimatedOwners = parseIntDefault(cleanNumber(get(f,3)), 0);
            g.price = parsePrice(get(f,4));
            g.supportedLanguages = parseList(get(f,5), true);
            g.metacriticScore = parseIntOrDefault(get(f,6), -1);
            g.userScore = parseUserScore(get(f,7));
            g.achievements = parseIntOrDefault(get(f,8), 0);
            g.publishers = parseList(get(f,9), false);
            g.developers = parseList(get(f,10), false);
            g.categories = parseList(get(f,11), false);
            g.genres = parseList(get(f,12), false);
            g.tags = parseList(get(f,13), false);
            return g;
        }

        private static String get(List<String> f, int idx) {
            if (idx >= f.size()) return "";
            return f.get(idx) == null ? "" : f.get(idx).trim();
        }

        private static String unquote(String s) {
            if (s == null) return "";
            s = s.trim();
            if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
                s = s.substring(1, s.length()-1);
            }
            return s;
        }

        private static String cleanNumber(String s) {
            if (s == null) return "";
            s = unquote(s).trim();
            return s.replaceAll("[^0-9]", "");
        }

        private static int parseIntDefault(String s, int def) {
            if (s == null) return def;
            s = s.trim();
            if (s.isEmpty()) return def;
            try { return Integer.parseInt(s); } catch (Exception e) { return def; }
        }

        private static int parseIntOrDefault(String s, int def) {
            s = unquote(s).trim();
            if (s.isEmpty()) return def;
            s = s.replaceAll("[^0-9-]", "");
            if (s.isEmpty()) return def;
            try { return Integer.parseInt(s); } catch (Exception e) { return def; }
        }

        private static float parseUserScore(String s) {
            s = unquote(s).trim().toLowerCase();
            if (s.isEmpty() || s.equals("tbd")) return -1.0f;
            if (s.equals("0") || s.equals("0.0")) return 0.0f;
            s = s.replaceAll("[^0-9,.-]", "");
            s = s.replace(',', '.');
            if (s.isEmpty()) return -1.0f;
            try { return Float.parseFloat(s); } catch (Exception e) { return -1.0f; }
        }

        private static float parsePrice(String s) {
            s = unquote(s).trim();
            if (s.isEmpty()) return 0.0f;
            if (s.equalsIgnoreCase("Free to Play")) return 0.0f;
            String cleaned = s.replaceAll("[^0-9,.-]", "");
            cleaned = cleaned.replace(',', '.');
            if (cleaned.isEmpty()) return 0.0f;
            try { return Float.parseFloat(cleaned); } catch (Exception e) { return 0.0f; }
        }

        private static List<String> parseList(String s, boolean preserveSingleQuotes) {
            s = unquote(s).trim();
            List<String> out = new ArrayList<>();
            if (s.isEmpty()) return out;
            if (s.startsWith("[") && s.endsWith("]") && s.length() >= 2) {
                s = s.substring(1, s.length()-1);
            }
            StringBuilder cur = new StringBuilder();
            boolean inSingle = false;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '\'') {
                    inSingle = !inSingle;
                    continue;
                }
                if (c == ',' && !inSingle) {
                    String item = cur.toString().trim();
                    item = stripQuotes(item);
                    if (!item.isEmpty()) out.add(item);
                    cur.setLength(0);
                } else {
                    cur.append(c);
                }
            }
            String last = cur.toString().trim();
            last = stripQuotes(last);
            if (!last.isEmpty()) out.add(last);
            return out;
        }

        private static String stripQuotes(String s) {
            if (s == null) return "";
            s = s.trim();
            if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) s = s.substring(1, s.length()-1);
            if (s.length() >= 2 && s.startsWith("'") && s.endsWith("'")) s = s.substring(1, s.length()-1);
            return s.trim();
        }

        private static String normalizeDate(String raw) {
            raw = unquote(raw).trim();
            if (raw.isEmpty()) return "";
            String[] patterns = {
                "MMM d, uuuu", "MMMM d, uuuu", "MMM dd, uuuu", "MMMM dd, uuuu",
                "d MMM uuuu", "d MMM, uuuu", "uuuu-MM-dd", "MM/dd/uuuu", "dd/MM/uuuu", "uuuu"
            };
            for (String p : patterns) {
                try {
                    if (p.equals("uuuu")) {
                        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d{4})").matcher(raw);
                        if (m.find()) {
                            int y = Integer.parseInt(m.group(1));
                            return String.format("01/01/%04d", y);
                        } else continue;
                    } else {
                        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern(p, Locale.ENGLISH);
                        java.time.LocalDate ld = java.time.LocalDate.parse(raw, fmt);
                        return ld.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu"));
                    }
                } catch (Exception ex) {}
            }

            Map<String,Integer> meses = new HashMap<>();
            meses.put("jan",1); meses.put("january",1);
            meses.put("feb",2); meses.put("february",2);
            meses.put("mar",3); meses.put("march",3);
            meses.put("apr",4); meses.put("april",4);
            meses.put("may",5);
            meses.put("jun",6); meses.put("june",6);
            meses.put("jul",7); meses.put("july",7);
            meses.put("aug",8); meses.put("august",8);
            meses.put("sep",9); meses.put("sept",9); meses.put("september",9);
            meses.put("oct",10); meses.put("october",10);
            meses.put("nov",11); meses.put("november",11);
            meses.put("dec",12); meses.put("december",12);

            String low = raw.toLowerCase();
            int year = -1, month = 1, day = 1;

            java.util.regex.Matcher my = java.util.regex.Pattern.compile("(\\d{4})").matcher(raw);
            if (my.find()) year = Integer.parseInt(my.group(1));

            for (String k : meses.keySet()) {
                if (low.contains(k)) { month = meses.get(k); break; }
            }

            java.util.regex.Matcher md = java.util.regex.Pattern.compile("\\b(\\d{1,2})\\b").matcher(raw);
            while (md.find()) {
                int v = Integer.parseInt(md.group(1));
                if (v == year) continue;
                if (v >= 1 && v <= 31) { day = v; break; }
            }

            // Se não encontrou ano válido, retorna string original
            if (year == -1) return raw;
            
            // Retorna data formatada como dd/MM/yyyy
            return String.format("%02d/%02d/%04d", day, month, year);
        }

        // Sobrescreve toString para formatar saída do jogo
        @Override
        public String toString() { return mergesort.gameToString(this); }
    }

    // === MÉTODO DE FORMATAÇÃO DE SAÍDA ===
    // Converte um objeto Game para string no formato especificado
    public static String gameToString(Game g) {
        return "=> " + g.id + " ## " +                               // AppID
            g.name + " ## " +                                        // Nome do jogo
            g.releaseDate + " ## " +                                 // Data de lançamento
            g.estimatedOwners + " ## " +                             // Número estimado de donos
            g.price + " ## " +                                       // Preço
            auxiliarMostrar(g.supportedLanguages) + " ## " +         // Idiomas suportados
            g.metacriticScore + " ## " +                             // Nota Metacritic
            g.userScore + " ## " +                                   // Nota dos usuários
            g.achievements + " ## " +                                // Número de conquistas
            auxiliarMostrar(g.publishers) + " ## " +                 // Publicadores
            auxiliarMostrar(g.developers) + " ## " +                 // Desenvolvedores
            auxiliarMostrar(g.categories) + " ## " +                 // Categorias
            auxiliarMostrar(g.genres) + " ## " +                     // Gêneros
            auxiliarMostrar(g.tags) + " ##";                         // Tags
    }

    // === MÉTODO AUXILIAR PARA FORMATAR LISTAS ===
    // Converte uma lista de strings para o formato [item1, item2, item3]
    private static String auxiliarMostrar(List<String> array) {
        // Inicia a string com colchete de abertura
        String result = "[";
        
        // Se a lista não for nula, percorre todos os elementos
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                // Adiciona o item atual
                result += array.get(i);
                
                // Se não for o último item, adiciona vírgula e espaço
                if (i < array.size() - 1) {
                    result += ", ";
                }
            }
        }
        
        // Fecha o colchete e retorna
        result += "]";
        return result;
    }

    // --- método auxiliar de busca binária por id ---
    private static int binarySearchById(Game[] a, int id) {
        int l = 0, r = a.length - 1;
        while (l <= r) {
            int m = (l + r) >>> 1;
            if (a[m].id == id) return m;
            if (a[m].id < id) l = m + 1;
            else r = m - 1;
        }
        return -1;
    }
}




