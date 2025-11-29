import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

/*
 * Apenas guardo limites que uso para criar arrays
 * e armazenar os dados dos jogos.
 */
class NoArrayListT {
    public static final int MAX_GAMES = 500;
    public static final int MAX_INNER_ARRAY = 50;
    public static final int MAX_IDS = 100;
}

/*
 * Classe onde guardo todas as informações do jogo
 * lidas diretamente do CSV.
 */
class GameBinarioT {
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

    // Construtor padrão
    GameBinarioT() {
        this.id = 0;
        this.name = "";
        this.releaseDate = "";
        this.estimatedOwners = 0;
        this.price = 0.0f;

        supportedLanguages = new String[NoArrayListT.MAX_INNER_ARRAY];
        supportedLanguagesCount = 0;

        metacriticScore = -1;
        userScore = -1.0f;
        achievements = 0;

        publishers = new String[NoArrayListT.MAX_INNER_ARRAY];
        publishersCount = 0;

        developers = new String[NoArrayListT.MAX_INNER_ARRAY];
        developersCount = 0;

        categories = new String[NoArrayListT.MAX_INNER_ARRAY];
        categoriesCount = 0;

        genres = new String[NoArrayListT.MAX_INNER_ARRAY];
        genresCount = 0;

        tags = new String[NoArrayListT.MAX_INNER_ARRAY];
        tagsCount = 0;
    }

    // Construtor completo
    GameBinarioT(int id, String name, String releaseDate, int estimatedOwners, float price,
            String[] supportedLanguages, int supportedLanguagesCount, int metacriticScore, float userScore,
            int achievements,
            String[] publishers, int publishersCount, String[] developers, int developersCount,
            String[] categories, int categoriesCount, String[] genres, int genresCount, String[] tags, int tagsCount) {

        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
        this.estimatedOwners = estimatedOwners;
        this.price = price;

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

/*
 * Nó da segunda árvore (organizada por nome do jogo)
 */
class NoString {
    GameBinarioT game;
    NoString esq, dir;

    NoString(GameBinarioT game) {
        this.game = game;
        esq = dir = null;
    }
}

/*
 * Segunda árvore: organizada por nome do jogo
 */
class ArvoreString {

    // Inserção normal por nome
    public NoString inserir(GameBinarioT game, NoString i) throws Exception {
        if (i == null) {
            return new NoString(game);
        } else if (game.name.compareTo(i.game.name) < 0) {
            i.esq = inserir(game, i.esq);
        } else if (game.name.compareTo(i.game.name) > 0) {
            i.dir = inserir(game, i.dir);
        } else {
            throw new Exception("Elemento já existe na segunda árvore.");
        }
        return i;
    }

    // Pesquisa por nome
    public boolean pesquisa(String entrada, NoString i) {
        if (i == null) return false;

        if (entrada.equals(i.game.name)) {
            System.out.println(" SIM");
            return true;
        } else if (entrada.compareTo(i.game.name) < 0) {
            System.out.print("esq ");
            return pesquisa(entrada, i.esq);
        } else {
            System.out.print("dir ");
            return pesquisa(entrada, i.dir);
        }
    }
}

/*
 * Nó da Primeira Árvore (árvore de inteiros)
 * Cada nó contém:
 * - chave (estimatedOwners % 15)
 * - Uma segunda árvore de nomes
 */
class NoInt {
    int chave;
    NoInt esq, dir;
    NoString raizSegundaArvore;

    NoInt(int chave) {
        this.chave = chave;
        esq = dir = null;
        raizSegundaArvore = null;
    }
}

/*
 * Árvore principal (de inteiros)
 * Cada nó aponta para uma segunda árvore
 */
class ArvoreDeArvore {
    private NoInt raiz;
    private ArvoreString arvoreString;

    public ArvoreDeArvore() {
        arvoreString = new ArvoreString();
        inicializarPrimeiraArvore();
    }

    /*
     * Aqui eu crio a árvore fixa com as chaves:
     * 7, 3, 11, 1, 5, 9, 13, 0, 2, 4, 6, 8, 10, 12, 14
     * Essa estrutura já é definida pelo professor.
     */
    private void inicializarPrimeiraArvore() {
        int[] chaves = { 7, 3, 11, 1, 5, 9, 13, 0, 2, 4, 6, 8, 10, 12, 14 };
        for (int chave : chaves) {
            raiz = inserirNoInt(chave, raiz);
        }
    }

    private NoInt inserirNoInt(int chave, NoInt i) {
        if (i == null) return new NoInt(chave);
        if (chave < i.chave) i.esq = inserirNoInt(chave, i.esq);
        else if (chave > i.chave) i.dir = inserirNoInt(chave, i.dir);
        return i;
    }

    /*
     * Inserção de um jogo:
     * calculo a chave = estimatedOwners % 15
     * insiro o jogo na segunda árvore ligada àquele nó
     */
    public void inserirGame(GameBinarioT game) throws Exception {
        int chave = game.estimatedOwners % 15;
        inserirGame(game, chave, raiz);
    }

    private void inserirGame(GameBinarioT game, int chave, NoInt i) throws Exception {
        if (i == null) {
            throw new Exception("Chave não encontrada");
        } else if (chave == i.chave) {
            i.raizSegundaArvore = arvoreString.inserir(game, i.raizSegundaArvore);
        } else if (chave < i.chave) {
            inserirGame(game, chave, i.esq);
        } else {
            inserirGame(game, chave, i.dir);
        }
    }

    /*
     * Pesquisa:
     * Percorro a primeira árvore e, em cada nó,
     * pesquiso dentro da segunda árvore.
     */
    public void pesquisa(String nomeGame) {
        System.out.print("=> " + nomeGame + " => raiz ");
        boolean achou = pesquisaNaPrimeiraArvore(nomeGame, raiz);
        if (!achou) System.out.println(" NAO");
    }

    private boolean pesquisaNaPrimeiraArvore(String nomeGame, NoInt i) {
        if (i == null) return false;

        System.out.print("ESQ ");
        if (pesquisaNaPrimeiraArvore(nomeGame, i.esq)) return true;

        if (i.raizSegundaArvore != null) {
            if (arvoreString.pesquisa(nomeGame, i.raizSegundaArvore)) return true;
        }

        System.out.print("DIR ");
        return pesquisaNaPrimeiraArvore(nomeGame, i.dir);
    }
}

/*
 * Classe main
 */
public class ADeArvore {
    public static Scanner sc;

    public static void main(String[] args) {
        sc = new Scanner(System.in);

        // Lendo IDs até "FIM"
        String ids[] = new String[2000];
        int tam = 0;
        String entrada = sc.nextLine();

        while (!entrada.equals("FIM")) {
            ids[tam++] = entrada;
            entrada = sc.nextLine();
        }

        // Montando a Árvore de Árvores
        ArvoreDeArvore arvore = JogosDigitadosArvoreDeArvore.inicializacao(ids, tam);

        // Pesquisas por nome
        entrada = sc.nextLine();
        while (!entrada.equals("FIM")) {
            arvore.pesquisa(entrada);
            entrada = sc.nextLine();
        }

        sc.close();
    }
}

/*
 * Aqui faço toda a leitura do arquivo CSV
 * e devolvo a Árvore preenchida com os jogos informados pelo usuário.
 */
class JogosDigitadosArvoreDeArvore {

    static int contador = 0;
    static String[] ids;
    static int idsTamanho;

    static ArvoreDeArvore inicializacao(String[] idArray, int tamanho) {
        ArvoreDeArvore arvore = new ArvoreDeArvore();

        ids = idArray;
        idsTamanho = tamanho;

        for (int j = 0; j < tamanho; j++) {

            try {
                java.io.File arquivo = new java.io.File("/tmp/games.csv");
                if (!arquivo.exists()) {
                    return arvore;
                }

                InputStream is = new FileInputStream(arquivo);
                Scanner sc = new Scanner(is);

                if (sc.hasNextLine()) sc.nextLine();

                boolean encontrado = false;

                while (sc.hasNextLine() && !encontrado) {
                    String linha = sc.nextLine();
                    contador = 0;

                    int id = capturaId(linha);
                    int indiceEncontrado = igualId(id);

                    if (indiceEncontrado != -1) {

                        // Capto todos os dados desse jogo
                        String name = capturaName(linha);
                        String releaseDate = capturaReleaseDate(linha);
                        int estimatedOwners = capturaEstimatedOwners(linha);
                        float price = capturaPrice(linha);

                        String[] supportedLanguages = new String[NoArrayListT.MAX_INNER_ARRAY];
                        int supportedLanguagesCount = capturaSupportedLanguages(linha, supportedLanguages);
                        int metacriticScore = capturaMetacriticScore(linha);
                        float userScore = capturaUserScore(linha);
                        int achievements = capturaAchievements(linha);

                        String[] publishers = new String[NoArrayListT.MAX_INNER_ARRAY];
                        int publishersCount = capturaUltimosArryays(linha, publishers);

                        String[] developers = new String[NoArrayListT.MAX_INNER_ARRAY];
                        int developersCount = capturaUltimosArryays(linha, developers);

                        String[] categories = new String[NoArrayListT.MAX_INNER_ARRAY];
                        int categoriesCount = capturaUltimosArryays(linha, categories);

                        String[] genres = new String[NoArrayListT.MAX_INNER_ARRAY];
                        int genresCount = capturaUltimosArryays(linha, genres);

                        String[] tags = new String[NoArrayListT.MAX_INNER_ARRAY];
                        int tagsCount = capturaUltimosArryays(linha, tags);

                        GameBinarioT jogo = new GameBinarioT(id, name, releaseDate, estimatedOwners, price,
                                supportedLanguages, supportedLanguagesCount, metacriticScore, userScore, achievements,
                                publishers, publishersCount, developers, developersCount, categories, categoriesCount,
                                genres, genresCount, tags, tagsCount);

                        removerId(indiceEncontrado);
                        arvore.inserirGame(jogo);
                        encontrado = true;
                    }
                }

                sc.close();
                is.close();

            } catch (Exception e) {
                // Ignoro erros para evitar quebra
            }
        }

        return arvore;
    }

    /*
     * Aqui corrijo o erro do seu código:
     * agora eu comparo TODOS os IDs digitados.
     */
    static int igualId(int id) {
        for (int i = 0; i < idsTamanho; i++) {
            if (Integer.parseInt(ids[i]) == id) return i;
        }
        return -1;
    }

    /*
     * Removo da posição correta (não só do índice 0)
     */
    static void removerId(int indice) {
        for (int j = indice; j < idsTamanho - 1; j++) {
            ids[j] = ids[j + 1];
        }
        ids[idsTamanho - 1] = null;
        idsTamanho--;
    }

    // As funções abaixo são apenas leitura e parsing (não mexi)
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
        return name.trim();
    }

    static String capturaReleaseDate(String jogo) {
        while (contador < jogo.length() && jogo.charAt(contador) != '"') contador++;
        contador++;

        String dia = "", mes = "", ano = "";

        for (int i = 0; i < 3; i++) mes += jogo.charAt(contador++);

        switch (mes) {
            case "Jan": mes = "01"; break;
            case "Feb": mes = "02"; break;
            case "Mar": mes = "03"; break;
            case "Apr": mes = "04"; break;
            case "May": mes = "05"; break;
            case "Jun": mes = "06"; break;
            case "Jul": mes = "07"; break;
            case "Aug": mes = "08"; break;
            case "Sep": mes = "09"; break;
            case "Oct": mes = "10"; break;
            case "Nov": mes = "11"; break;
            case "Dec": mes = "12"; break;
        }

        while (!Character.isDigit(jogo.charAt(contador))) contador++;
        while (Character.isDigit(jogo.charAt(contador))) dia += jogo.charAt(contador++);

        while (!Character.isDigit(jogo.charAt(contador))) contador++;
        while (jogo.charAt(contador) != '"') ano += jogo.charAt(contador++);

        return dia + "/" + mes + "/" + ano;
    }

    static int capturaEstimatedOwners(String jogo) {
        while (jogo.charAt(contador) != ',') contador++;
        contador++;

        String n = "";
        while (contador < jogo.length() && jogo.charAt(contador) != ',') {
            if (Character.isDigit(jogo.charAt(contador))) n += jogo.charAt(contador);
            contador++;
        }
        return n.equals("") ? 0 : Integer.parseInt(n);
    }

    static float capturaPrice(String jogo) {
        String price = "";
        while (contador < jogo.length() && !Character.isDigit(jogo.charAt(contador))) contador++;
        while (contador < jogo.length() && (Character.isDigit(jogo.charAt(contador)) || jogo.charAt(contador)=='.')) {
            price += jogo.charAt(contador++);
        }
        price = price.replaceAll("[^0-9.]", "");
        try { return Float.parseFloat(price); } catch(Exception e){ return 0f; }
    }

    static int capturaSupportedLanguages(String jogo, String[] arr) {
        int count = 0;
        while (contador < jogo.length() && jogo.charAt(contador) != ']') {
            String s = "";
            while (contador < jogo.length() && !Character.isAlphabetic(jogo.charAt(contador))) contador++;
            while (contador < jogo.length() && jogo.charAt(contador) != ',' && jogo.charAt(contador) != ']') {
                s += jogo.charAt(contador++);
            }
            if (!s.trim().equals("")) arr[count++] = s.trim();
        }
        return count;
    }

    static int capturaMetacriticScore(String jogo) {
        while (jogo.charAt(contador) != ',') contador++;
        contador++;
        String n = "";
        while (contador < jogo.length() && Character.isDigit(jogo.charAt(contador)))
            n += jogo.charAt(contador++);
        return n.equals("") ? -1 : Integer.parseInt(n);
    }

    static float capturaUserScore(String jogo) {
        while (jogo.charAt(contador) != ',') contador++;
        contador++;
        String n = "";
        while (contador < jogo.length() && (Character.isDigit(jogo.charAt(contador)) || jogo.charAt(contador)=='.'))
            n += jogo.charAt(contador++);
        return n.equals("") ? -1f : Float.parseFloat(n);
    }

    static int capturaAchievements(String jogo) {
        while (jogo.charAt(contador) != ',') contador++;
        contador++;
        String n = "";
        while (contador < jogo.length() && Character.isDigit(jogo.charAt(contador)))
            n += jogo.charAt(contador++);
        return n.equals("") ? 0 : Integer.parseInt(n);
    }

    static int capturaUltimosArryays(String jogo, String[] arr) {
        int count = 0;
        while (contador < jogo.length() && jogo.charAt(contador) != '"') contador++;
        contador++;
        while (contador < jogo.length() && jogo.charAt(contador) != '"' && count < arr.length) {
            String p = "";
            while (jogo.charAt(contador) != ',' && jogo.charAt(contador) != '"') p += jogo.charAt(contador++);
            arr[count++] = p.trim();
            if (jogo.charAt(contador) == ',') contador++;
        }
        contador++;
        return count;
    }
}
