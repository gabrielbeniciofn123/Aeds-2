package tp7;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

// Classe que só guarda limites dos arrays que eu vou usar
class NoArrayListM {
    public static final int MAX_GAMES = 500;
    public static final int MAX_INNER_ARRAY = 50;
    public static final int MAX_IDS = 100;
}

// Classe onde eu armazeno todas as informações dos jogos lidos do CSV
class GameBinario {
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

    // Construtor padrão para iniciar tudo zerado
    GameBinario() {
        this.id = 0;
        this.name = "";
        this.releaseDate = "";
        this.estimatedOwners = 0;
        this.price = 0.0f;

        // Inicializando todos os arrays que guardam várias infos
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

    // Construtor completo (quando eu já extraí tudo do CSV)
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

        // Só copio todos os arrays já preenchidos
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

// ============================ ÁRVORE BINÁRIA ===============================
class ArvoreB {
    private No raiz; // minha raiz da árvore

    // Método para inserir um jogo na árvore
    public void inserir(GameBinario game) throws Exception {
        raiz = inserir(game, raiz);
    }

    // Inserção recursiva
    private No inserir(GameBinario game, No i) throws Exception {

        // Se não tem nó aqui ainda, insiro o novo
        if (i == null) {
            i = new No(game);

        // Se tiver jogo repetido, eu não deixo inserir
        } else if (game.name.equals(i.game.name)) {
            throw new Exception("Elemento já existe na árvore");

        // Se o nome for menor, vou para a esquerda
        } else if (game.name.compareTo(i.game.name) < 0) {
            i.esq = inserir(game, i.esq);

        // Se for maior, vou para a direita
        } else if (game.name.compareTo(i.game.name) > 0) {
            i.dir = inserir(game, i.dir);

        } else {
            throw new Exception("Erro ao inserir!");
        }

        return i;
    }

    // Método principal para pesquisar, já começando da raiz
    public void pesquisa(String entrada) throws Exception {
        if (raiz != null) {
            System.out.print(entrada + ": =>raiz ");
            ArvoreBinaria.logWriter.print(entrada + ": =>raiz ");
        }
        pesquisa(entrada, raiz);
    }

    // Pesquisa recursiva mostrando o caminho percorrido
    private void pesquisa(String entrada, No i) throws Exception {

        // Caso não encontre o nome na árvore
        if (i == null) {
            System.out.println("NAO");
            ArvoreBinaria.logWriter.print("NAO");
            return;
        }

        // Se o nome bate exatamente, encontrei
        if (entrada.equals(i.game.name)) {
            System.out.println("SIM");
            ArvoreBinaria.logWriter.print("SIM");
            return;
        }

        // Se o nome é menor, vou para a esquerda
        if (entrada.compareTo(i.game.name) < 0) {
            System.out.print("esq ");
            ArvoreBinaria.logWriter.print("esq ");
            pesquisa(entrada, i.esq);

        // Se o nome é maior, vou para a direita
        } else {
            System.out.print("dir ");
            ArvoreBinaria.logWriter.print("dir ");
            pesquisa(entrada, i.dir);
        }
    }
}

// Nó da minha árvore (guarda o jogo + ponteiros)
class No {
    GameBinario game;
    No esq, dir;

    No(GameBinario game) {
        this.game = game;
        esq = dir = null;
    }
}

// ============================ PROGRAMA PRINCIPAL ===============================
public class ArvoreBinaria {

    public static Scanner sc;
    public static PrintWriter logWriter;

    public static void main(String[] args) {

        sc = new Scanner(System.in);

        try {
            // Arquivo de log com minha matrícula
            logWriter = new PrintWriter(new FileWriter("889921_arvoreBinaria.txt"));
        } catch (IOException e) {
            System.err.println("Erro ao criar log");
            return;
        }

        // Aqui eu leio os IDs digitados até achar "FIM"
        String entrada = sc.nextLine();
        String ids[] = new String[2000];
        int tam = 0;

        while (!entrada.equals("FIM")) {
            ids[tam++] = entrada;
            entrada = sc.nextLine();
        }

        // Crio a árvore com os jogos correspondentes aos IDs digitados
        ArvoreB arvore = JogosDigitadosArvoreBinaria.inicializacao(ids, tam);

        // Depois faço as pesquisas pelo nome do jogo
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

// ============================ LEITURA DO CSV ===============================
class JogosDigitadosArvoreBinaria {

    static int contador = 0;
    static String[] ids;
    static int idsTamanho;

    // Aqui eu leio o CSV e construo a árvore com os jogos pedidos
    static ArvoreB inicializacao(String[] idArray, int tamanho) {

        ArvoreB arvore = new ArvoreB();

        ids = idArray;
        idsTamanho = tamanho;

        // Para cada ID digitado, eu percorro o CSV até encontrar
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

                // Pulo o cabeçalho
                if (sc.hasNextLine()) sc.nextLine();

                // Percorro todo o CSV até achar um ID que bate
                while (sc.hasNextLine() && indiceEncontrado == -1) {

                    String linha = sc.nextLine();
                    contador = 0;

                    int id = capturaId(linha);
                    indiceEncontrado = igualId(id);

                    if (indiceEncontrado != -1) {

                        // Se achei o ID certo, capturo todas as informações
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

                        // Crio o objeto jogo com tudo preenchido
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

                        // Tiro o ID da lista (não quero buscar de novo)
                        removerId(indiceEncontrado);

                        // E insiro na árvore
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

    // Aqui eu verifico se um ID digitado bate com o ID do CSV
    static int igualId(int id) {
        for (int i = 0; i < idsTamanho; i++) {
            if (Integer.parseInt(ids[i]) == id) {
                return i;
            }
        }
        return -1;
    }

    // Removo o ID para não processar de novo
    static void removerId(int indice) {
        for (int j = indice; j < idsTamanho - 1; j++) {
            ids[j] = ids[j + 1];
        }
        idsTamanho--;
    }
}
