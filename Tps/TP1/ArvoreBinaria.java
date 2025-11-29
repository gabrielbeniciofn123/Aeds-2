package tp7;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Classe apenas para armazenar limites de arrays usados pelos objetos GameBinario
 */
class NoArrayListM {
    public static final int MAX_GAMES = 500;
    public static final int MAX_INNER_ARRAY = 50;
    public static final int MAX_IDS = 100;
}

/**
 * Classe que representa um registro de jogo carregado do CSV
 */
class GameBinario {

    // --- Atributos do jogo ---
    int id;
    String name;
    String releaseDate;
    int estimatedOwners;
    float price;

    String[] supportedLanguages;
    int supportedLanguagesCount;

    int metacriticScore;
    float userScore;
    int achievements;

    String[] publishers;
    int publishersCount;

    String[] developers;
    int developersCount;

    String[] categories;
    int categoriesCount;

    String[] genres;
    int genresCount;

    String[] tags;
    int tagsCount;

    /** Construtor padrão */
    GameBinario() {
        this.id = 0;
        this.name = "";
        this.releaseDate = "";
        this.estimatedOwners = 0;
        this.price = 0.0f;

        this.supportedLanguages = new String[NoArrayListM.MAX_INNER_ARRAY];
        this.supportedLanguagesCount = 0;

        this.metacriticScore = -1;
        this.userScore = -1.0f;
        this.achievements = 0;

        this.publishers = new String[NoArrayListM.MAX_INNER_ARRAY];
        this.publishersCount = 0;

        this.developers = new String[NoArrayListM.MAX_INNER_ARRAY];
        this.developersCount = 0;

        this.categories = new String[NoArrayListM.MAX_INNER_ARRAY];
        this.categoriesCount = 0;

        this.genres = new String[NoArrayListM.MAX_INNER_ARRAY];
        this.genresCount = 0;

        this.tags = new String[NoArrayListM.MAX_INNER_ARRAY];
        this.tagsCount = 0;
    }

    /** Construtor com todos os atributos */
    GameBinario(int id, String name, String releaseDate, int estimatedOwners, float price,
            String[] supportedLanguages, int supportedLanguagesCount, int metacriticScore, float userScore,
            int achievements,
            String[] publishers, int publishersCount, String[] developers, int developersCount,
            String[] categories, int categoriesCount, String[] genres, int genresCount, String[] tags, int tagsCount) {

        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
        this.estimatedOwners = estimatedOwners;
        this.price = price;

        // Copiando arrays
        this.supportedLanguages = supportedLanguages;
        this.supportedLanguagesCount = supportedLanguagesCount;

        this.metacriticScore = metacriticScore;
        this.userScore = userScore;
        this.achievements = achievements;

        this.publishers = publishers;
        this.publishersCount = publishersCount;

        this.developers = developers;
        this.developersCount = developersCount;

        this.categories = categories;
        this.categoriesCount = categoriesCount;

        this.genres = genres;
        this.genresCount = genresCount;

        this.tags = tags;
        this.tagsCount = tagsCount;
    }
}

/* ========================= ÁRVORE BINÁRIA ========================== */
class ArvoreB {

    private No raiz;

    /** Insere um jogo na árvore usando recursão */
    public void inserir(GameBinario game) throws Exception {
        raiz = inserir(game, raiz);
    }

    private No inserir(GameBinario game, No i) throws Exception {

        if (i == null) {
            // Inserção normal de nó
            i = new No(game);

        } else if (game.name.equals(i.game.name)) {
            throw new Exception("Elemento já existe na árvore");

        } else if (game.name.compareTo(i.game.name) < 0) {
            i.esq = inserir(game, i.esq);

        } else if (game.name.compareTo(i.game.name) > 0) {
            i.dir = inserir(game, i.dir);

        } else {
            throw new Exception("Erro ao inserir!");
        }

        return i;
    }

    /** Método público de pesquisa */
    public void pesquisa(String entrada) throws Exception {
        if (raiz != null) {
            ArvoreBinaria.logWriter.print(entrada + ": =>raiz ");
            System.out.print(entrada + ": =>raiz ");
        }
        pesquisa(entrada, raiz);
    }

    /**
     * Pesquisa recursiva mostrando caminho percorrido
     */
    private void pesquisa(String entrada, No i) throws Exception {

        if (i == null) {
            System.out.println("NAO");
            ArvoreBinaria.logWriter.print("NAO");
            return;
        }

        if (entrada.equals(i.game.name)) {
            System.out.println("SIM");
            ArvoreBinaria.logWriter.print("SIM");
            return;
        }

        if (entrada.compareTo(i.game.name) < 0) {
            System.out.print("esq ");
            ArvoreBinaria.logWriter.print("esq ");
            pesquisa(entrada, i.esq);
        } else {
            System.out.print("dir ");
            ArvoreBinaria.logWriter.print("dir ");
            pesquisa(entrada, i.dir);
        }
    }
}

/**
 * Estrutura de um nó da árvore (esq, dir + o jogo)
 */
class No {
    GameBinario game;
    No esq, dir;

    No(GameBinario game) {
        this.game = game;
        esq = dir = null;
    }
}

/* ========================= PROGRAMA PRINCIPAL ========================== */

public class ArvoreBinaria {

    public static Scanner sc;
    public static PrintWriter logWriter;

    public static void main(String[] args) {

        sc = new Scanner(System.in);

        // Arquivo de log com matrícula corrigida
        try {
            logWriter = new PrintWriter(new FileWriter("889921_arvoreBinaria.txt"));
        } catch (IOException e) {
            System.err.println("Erro ao criar log: " + e.getMessage());
            return;
        }

        // Lendo IDs digitados
        String entrada = sc.nextLine();
        String ids[] = new String[2000];
        int tam = 0;

        while (!entrada.equals("FIM")) {
            ids[tam++] = entrada;
            entrada = sc.nextLine();
        }

        // Montando árvore
        ArvoreB arvore = JogosDigitadosArvoreBinaria.inicializacao(ids, tam);

        // Realizando pesquisas
        entrada = sc.nextLine();
        while (!entrada.equals("FIM")) {
            try {
                arvore.pesquisa(entrada);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            entrada = sc.nextLine();
        }

        sc.close();
        logWriter.close();
    }
}

/* ========================= LEITURA DO CSV E INSERÇÃO ========================== */

class JogosDigitadosArvoreBinaria {

    static int contador = 0;
    static String[] ids;
    static int idsTamanho;

    /** Inicializa árvore lendo o CSV e inserindo jogos correspondentes aos IDs */
    static ArvoreB inicializacao(String[] idArray, int tamanho) {

        ArvoreB arvore = new ArvoreB();

        ids = idArray;
        idsTamanho = tamanho;

        // Para cada id digitado pelo usuário
        for (int j = 0; j < tamanho; j++) {
            int indiceEncontrado = -1;

            try {
                java.io.File arquivo = new java.io.File("tp7/games.csv");

                if (!arquivo.exists()) {
                    System.out.println("games.csv NÃO encontrado!");
                    return arvore;
                }

                InputStream is = new FileInputStream(arquivo);
                Scanner sc = new Scanner(is);

                // pula cabeçalho
                if (sc.hasNextLine()) sc.nextLine();

                while (sc.hasNextLine() && indiceEncontrado == -1) {
                    String linha = sc.nextLine();
                    contador = 0;

                    int id = capturaId(linha);
                    indiceEncontrado = igualId(id);

                    if (indiceEncontrado != -1) {

                        // Coleta TODOS os dados do jogo
                        String name = capturaName(linha);
                        String releaseDate = capturaReleaseDate(linha);
                        int estimatedOwners = capturaEstimatedOwners(linha);
                        float price = capturaPrice(linha);

                        String[] supportedLanguages = new String[NoArrayListM.MAX_INNER_ARRAY];
                        int supportedLanguagesCount = capturaSupportedLanguages(linha, supportedLanguages);

                        int metacriticScore = capturaMetacriticScore(linha);
                        float userScore = capturaUserScore(linha);
                        int achievements = capturaAchievements(linha);

                        String[] publishers = new String[NoArrayListM.MAX_INNER_ARRAY];
                        int publishersCount = capturaUltimosArryays(linha, publishers);

                        String[] developers = new String[NoArrayListM.MAX_INNER_ARRAY];
                        int developersCount = capturaUltimosArryays(linha, developers);

                        String[] categories = new String[NoArrayListM.MAX_INNER_ARRAY];
                        int categoriesCount = capturaUltimosArryays(linha, categories);

                        String[] genres = new String[NoArrayListM.MAX_INNER_ARRAY];
                        int genresCount = capturaUltimosArryays(linha, genres);

                        String[] tags = new String[NoArrayListM.MAX_INNER_ARRAY];
                        int tagsCount = capturaUltimosArryays(linha, tags);

                        GameBinario jogo = new GameBinario(
                            id, name, releaseDate, estimatedOwners, price,
                            supportedLanguages, supportedLanguagesCount,
                            metacriticScore, userScore, achievements,
                            publishers, publishersCount,
                            developers, developersCount,
                            categories, categoriesCount,
                            genres, genresCount,
                            tags, tagsCount
                        );

                        removerId(indiceEncontrado);
                        arvore.inserir(jogo);
                    }
                }

                sc.close();
                is.close();

            } catch (Exception e) {
                System.out.println("Erro ao processar CSV: " + e.getMessage());
            }
        }

        return arvore;
    }

    /**
     * Corrigido: verifica TODOS os ids, não apenas ids[0]
     */
    static int igualId(int id) {
        for (int i = 0; i < idsTamanho; i++) {
            if (Integer.parseInt(ids[i]) == id) {
                return i;
            }
        }
        return -1;
    }

    /** Remove ID já utilizado */
    static void removerId(int indice) {
        for (int j = indice; j < idsTamanho - 1; j++) {
            ids[j] = ids[j + 1];
        }
        idsTamanho--;
    }

    // -------------- Funções de captura do CSV abaixo --------------

    static int capturaId(String jogo) {
        int id = 0;
        while (contador < jogo.length() && Character.isDigit(jogo.charAt(contador))) {
            id = id * 10 + (jogo.charAt(contador) - '0');
            contador++;
        }
        return id;
    }

    static String capturaName(String jogo) {
        String name = "";
        while (contador < jogo.length() && jogo.charAt(contador) != ',') contador++;
        contador++;
        while (contador < jogo.length() && jogo.charAt(contador) != ',') {
            name += jogo.charAt(contador++);
        }
        return name;
    }

    // ---- Outras capturas (releaseDate, price, arrays...) permanecem iguais ----
    // Para economizar espaço não repito tudo aqui, mas está igual ao seu com comentários
}
