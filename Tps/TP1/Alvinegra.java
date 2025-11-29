// Gabriel Benicio Fonseca do Nascimento
// Matrícula: 889921
// TP07Q04 - Árvore Alvinegra em Java
// Refaço a primeira questão (árvore binária) usando agora uma Árvore Alvinegra,
// mantendo a mesma lógica de inserção por Name, pesquisa e geração de log.

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

// Aqui eu defino os tamanhos máximos que vou usar para arrays de strings.
class NoArrayListAN {
    public static final int MAX_GAMES = 500;
    public static final int MAX_INNER_ARRAY = 50;
    public static final int MAX_IDS = 100;
}

// Classe que representa um jogo (registro do arquivo games.csv)
class GameAN {
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

    // Construtor padrão, inicializo tudo com valores neutros.
    GameAN() {
        this.id = 0;
        this.name = "";
        this.releaseDate = "";
        this.estimatedOwners = 0;
        this.price = 0.0f;
        this.supportedLanguages = new String[NoArrayListAN.MAX_INNER_ARRAY];
        this.supportedLanguagesCount = 0;
        this.metacriticScore = -1;
        this.userScore = -1.0f;
        this.achievements = 0;
        this.publishers = new String[NoArrayListAN.MAX_INNER_ARRAY];
        this.publishersCount = 0;
        this.developers = new String[NoArrayListAN.MAX_INNER_ARRAY];
        this.developersCount = 0;
        this.categories = new String[NoArrayListAN.MAX_INNER_ARRAY];
        this.categoriesCount = 0;
        this.genres = new String[NoArrayListAN.MAX_INNER_ARRAY];
        this.genresCount = 0;
        this.tags = new String[NoArrayListAN.MAX_INNER_ARRAY];
        this.tagsCount = 0;
    }

    // Construtor completo, usado quando eu já tenho todos os dados do CSV.
    GameAN(int id, String name, String releaseDate, int estimatedOwners, float price,
           String[] supportedLanguages, int supportedLanguagesCount, int metacriticScore, float userScore,
           int achievements,
           String[] publishers, int publishersCount, String[] developers, int developersCount,
           String[] categories, int categoriesCount, String[] genres, int genresCount, String[] tags, int tagsCount) {

        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
        this.estimatedOwners = estimatedOwners;
        this.price = price;

        // Aqui eu só copio as referências dos arrays preenchidos na leitura.
        this.supportedLanguages = supportedLanguages;
        this.supportedLanguagesCount = supportedLanguagesCount;
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

        this.metacriticScore = metacriticScore;
        this.userScore = userScore;
        this.achievements = achievements;
    }
}

// Nó da árvore alvinegra
class NoAN {
    public boolean cor;   // true = vermelho, false = preto (alvinegra preto/branco)
    public GameAN game;
    public NoAN esq, dir;

    public NoAN() {
        this(null, false);
    }

    public NoAN(GameAN game, boolean cor) {
        this.cor = cor;
        this.game = game;
        esq = dir = null;
    }
}

// Árvore Alvinegra (tipo Rubro-Negra) organizada por NAME
class AlvinegraArvore {
    private NoAN raiz;

    public AlvinegraArvore() {
        raiz = null;
    }

    // Pesquisa por nome, exibindo todo o caminho e registrando no log.
    public boolean pesquisar(String nome) {
        Alvinegra.logWriter.print(nome + ": =>raiz  ");
        System.out.print(nome + ": =>raiz  ");
        return pesquisar(nome, raiz);
    }

    private boolean pesquisar(String nome, NoAN i) {
        boolean resp;
        if (i == null) {
            // Se cheguei num nó nulo, não achei.
            resp = false;
            Alvinegra.logWriter.println("NAO");
            System.out.println("NAO");
        } else if (nome.equals(i.game.name)) {
            // Quando o nome bate, retorno SIM.
            resp = true;
            Alvinegra.logWriter.println("SIM");
            System.out.println("SIM");
        } else if (nome.compareTo(i.game.name) < 0) {
            // Se o nome buscado é menor, vou para a esquerda.
            Alvinegra.logWriter.print("esq ");
            System.out.print("esq ");
            resp = pesquisar(nome, i.esq);
        } else {
            // Se o nome buscado é maior, vou para a direita.
            Alvinegra.logWriter.print("dir ");
            System.out.print("dir ");
            resp = pesquisar(nome, i.dir);
        }
        return resp;
    }

    // Inserção pública: delego para o método interno que trabalha com os ponteiros.
    public void inserir(GameAN game) throws Exception {
        // Primeiros nós tratados manualmente, como é comum na alvinegra.
        if (raiz == null) {
            // Primeiro nó da árvore sempre preto.
            raiz = new NoAN(game, false);

        } else if (raiz.esq == null && raiz.dir == null) {
            // Caso em que só tenho a raiz.
            if (game.name.compareTo(raiz.game.name) < 0) {
                raiz.esq = new NoAN(game, true);
            } else if (game.name.compareTo(raiz.game.name) > 0) {
                raiz.dir = new NoAN(game, true);
            } // se for igual, ignoro (mesmo name não entra)

        } else if (raiz.esq == null) {
            // Raiz e filho à direita
            if (game.name.compareTo(raiz.game.name) < 0) {
                raiz.esq = new NoAN(raiz.game, true);
                raiz.game = game;
            } else if (game.name.compareTo(raiz.dir.game.name) < 0) {
                raiz.esq = new NoAN(game, true);
            } else if (game.name.compareTo(raiz.dir.game.name) > 0) {
                raiz.esq = new NoAN(raiz.game, true);
                raiz.game = raiz.dir.game;
                raiz.dir.game = game;
            }

            raiz.esq.cor = raiz.dir.cor = false; // filhos pretos

        } else if (raiz.dir == null) {
            // Raiz e filho à esquerda
            if (game.name.compareTo(raiz.game.name) > 0) {
                raiz.dir = new NoAN(raiz.game, true);
                raiz.game = game;
            } else if (game.name.compareTo(raiz.esq.game.name) > 0) {
                raiz.dir = new NoAN(game, true);
            } else if (game.name.compareTo(raiz.esq.game.name) < 0) {
                raiz.dir = new NoAN(raiz.game, true);
                raiz.game = raiz.esq.game;
                raiz.esq.game = game;
            }

            raiz.esq.cor = raiz.dir.cor = false; // filhos pretos

        } else {
            // Caso geral: árvore com 3 ou mais elementos.
            inserir(game, null, null, null, raiz);
        }

        // A raiz sempre termina preta.
        raiz.cor = false;
    }

    // Balanceamento da árvore (casos de pai vermelho)
    private void balancear(NoAN bisavo, NoAN avo, NoAN pai, NoAN i) {
        if (pai != null && pai.cor == true) {
            // Pai vermelho → preciso reequilibrar em cima do avô
            if (pai.game.name.compareTo(avo.game.name) > 0) {
                // Pai está à direita do avô
                if (i.game.name.compareTo(pai.game.name) > 0) {
                    // Caso direita-direita: rotação simples à esquerda
                    avo = rotacaoEsq(avo);
                } else {
                    // Caso direita-esquerda: rotação dupla direita-esquerda
                    avo = rotacaoDirEsq(avo);
                }
            } else {
                // Pai está à esquerda do avô
                if (i.game.name.compareTo(pai.game.name) < 0) {
                    // Caso esquerda-esquerda: rotação simples à direita
                    avo = rotacaoDir(avo);
                } else {
                    // Caso esquerda-direita: rotação dupla esquerda-direita
                    avo = rotacaoEsqDir(avo);
                }
            }

            // Atualizo o ponteiro do bisavô para o novo sub-raiz (avô rotacionado)
            if (bisavo == null) {
                raiz = avo;
            } else if (avo.game.name.compareTo(bisavo.game.name) < 0) {
                bisavo.esq = avo;
            } else {
                bisavo.dir = avo;
            }

            // Ajusto as cores após as rotações (regra da alvinegra)
            avo.cor = false;
            if (avo.esq != null) avo.esq.cor = true;
            if (avo.dir != null) avo.dir.cor = true;
        }
    }

    // Inserção recursiva geral na árvore alvinegra
    private void inserir(GameAN game, NoAN bisavo, NoAN avo, NoAN pai, NoAN i) throws Exception {
        if (i == null) {
            // Cheguei em um espaço vazio onde o nó será inserido
            if (game.name.compareTo(pai.game.name) < 0) {
                i = pai.esq = new NoAN(game, true);  // novo nó sempre vermelho
            } else if (game.name.compareTo(pai.game.name) > 0) {
                i = pai.dir = new NoAN(game, true);
            } else {
                // Se o nome já existe, não insiro nada
                return;
            }

            // Se pai também é vermelho, pode precisar balancear
            if (pai.cor == true) {
                balancear(bisavo, avo, pai, i);
            }

        } else {
            // Verifico se estou num 4-nó (pai com dois filhos vermelhos)
            if (i.esq != null && i.dir != null && i.esq.cor == true && i.dir.cor == true) {
                // Faço split do 4-nó: pai fica vermelho e filhos ficam pretos
                i.cor = true;
                i.esq.cor = i.dir.cor = false;

                // Se não for raiz e pai também for vermelho, balanceio
                if (i == raiz) {
                    i.cor = false;
                } else if (pai != null && pai.cor == true) {
                    balancear(bisavo, avo, pai, i);
                }
            }

            // Decido se vou para esquerda ou direita pela chave NAME
            if (game.name.compareTo(i.game.name) < 0) {
                inserir(game, avo, pai, i, i.esq);
            } else if (game.name.compareTo(i.game.name) > 0) {
                inserir(game, avo, pai, i, i.dir);
            } else {
                // Nome duplicado: não insiro
                return;
            }
        }
    }

    // Rotação simples à direita
    private NoAN rotacaoDir(NoAN no) {
        NoAN noEsq = no.esq;
        NoAN noEsqDir = noEsq.dir;

        noEsq.dir = no;
        no.esq = noEsqDir;

        return noEsq;
    }

    // Rotação simples à esquerda
    private NoAN rotacaoEsq(NoAN no) {
        NoAN noDir = no.dir;
        NoAN noDirEsq = noDir.esq;

        noDir.esq = no;
        no.dir = noDirEsq;

        return noDir;
    }

    // Rotação dupla direita-esquerda
    private NoAN rotacaoDirEsq(NoAN no) {
        no.dir = rotacaoDir(no.dir);
        return rotacaoEsq(no);
    }

    // Rotação dupla esquerda-direita
    private NoAN rotacaoEsqDir(NoAN no) {
        no.esq = rotacaoEsq(no.esq);
        return rotacaoDir(no);
    }
}

// Classe principal da questão (TP07Q04)
public class Alvinegra {
    public static Scanner sc;
    public static PrintWriter logWriter;

    public static void main(String[] args) {
        sc = new Scanner(System.in);

        // Aqui eu crio o arquivo de log com a minha matrícula, como o enunciado pede.
        try {
            logWriter = new PrintWriter(new FileWriter("889921_arvoreAlvinegra.txt"));
        } catch (IOException e) {
            System.err.println("Erro ao criar o arquivo de log: " + e.getMessage());
        }

        // Leio a primeira parte da entrada: IDs até aparecer "FIM".
        String entrada = sc.nextLine();
        String ids[] = new String[2000];
        int tam = 0;

        while (!entrada.equals("FIM")) {
            ids[tam] = entrada;
            tam++;
            entrada = sc.nextLine();
        }

        // Crio a árvore alvinegra
        AlvinegraArvore arvore = JogosDIgitadosAN.inicializacao(ids, tam);

        // Agora leio a segunda parte da entrada: nomes para pesquisar até "FIM".
        entrada = sc.nextLine();
        while (!entrada.equals("FIM")) {
            try {
                arvore.pesquisar(entrada.trim());
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
            }
            entrada = sc.nextLine();
        }

        sc.close();
        logWriter.close();
    }
}

// Classe responsável por ler o arquivo e montar os objetos GameAN
class JogosDIgitadosAN {
    static int contador = 0;
    static String[] ids;
    static int idsTamanho;

    // Aqui eu inicializo a árvore: leio o CSV e insiro cada jogo na Alvinegra.
    static AlvinegraArvore inicializacao(String[] idArray, int tamanho) {
        AlvinegraArvore arvore = new AlvinegraArvore();

        ids = idArray;
        idsTamanho = tamanho;

        for (int j = 0; j < tamanho; j++) {
            try {
                java.io.File arquivo = new java.io.File("/tmp/games.csv");
                if (!arquivo.exists()) {
                    System.out.println("Arquivo 'games.csv' não encontrado!");
                    return arvore;
                }

                InputStream is = new FileInputStream(arquivo);
                Scanner sc = new Scanner(is);

                // Pulo o cabeçalho
                if (sc.hasNextLine())
                    sc.nextLine();

                int idProcurado = Integer.parseInt(ids[j]);
                boolean encontrado = false;

                while (sc.hasNextLine() && !encontrado) {
                    String linha = sc.nextLine();
                    contador = 0;

                    int id = capturaId(linha);

                    if (id == idProcurado) {
                        String name = capturaName(linha);
                        String releaseDate = capturaReleaseDate(linha);
                        int estimatedOwners = capturaEstimatedOwners(linha);
                        float price = capturaPrice(linha);

                        String[] supportedLanguages = new String[NoArrayListAN.MAX_INNER_ARRAY];
                        int supportedLanguagesCount = capturaSupportedLanguages(linha, supportedLanguages);
                        int metacriticScore = capturaMetacriticScore(linha);
                        float userScore = capturaUserScore(linha);
                        int achievements = capturaAchievements(linha);

                        String[] publishers = new String[NoArrayListAN.MAX_INNER_ARRAY];
                        int publishersCount = capturaUltimosArryays(linha, publishers);
                        String[] developers = new String[NoArrayListAN.MAX_INNER_ARRAY];
                        int developersCount = capturaUltimosArryays(linha, developers);
                        String[] categories = new String[NoArrayListAN.MAX_INNER_ARRAY];
                        int categoriesCount = capturaUltimosArryays(linha, categories);
                        String[] genres = new String[NoArrayListAN.MAX_INNER_ARRAY];
                        int genresCount = capturaUltimosArryays(linha, genres);
                        String[] tags = new String[NoArrayListAN.MAX_INNER_ARRAY];
                        int tagsCount = capturaUltimosArryays(linha, tags);

                        GameAN jogo = new GameAN(id, name, releaseDate, estimatedOwners, price,
                                supportedLanguages, supportedLanguagesCount, metacriticScore, userScore, achievements,
                                publishers, publishersCount, developers, developersCount, categories, categoriesCount,
                                genres, genresCount, tags, tagsCount);

                        // Aqui finalmente insiro o jogo na árvore alvinegra pela chave NAME.
                        arvore.inserir(jogo);
                        encontrado = true;
                    }
                }

                sc.close();
                is.close();

            } catch (Exception e) {
                System.out.println("Erro ao abrir ou ler o arquivo: " + e.getMessage());
            }
        }

        return arvore;
    }

    // A partir daqui são os mesmos métodos de captura da Q01, adaptados para esse TP.

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
        while (contador < jogo.length() && jogo.charAt(contador) != ',') {
            contador++;
        }
        contador++;
        while (contador < jogo.length() && jogo.charAt(contador) != ',') {
            name += jogo.charAt(contador);
            contador++;
        }
        return name.trim();
    }

    static String capturaReleaseDate(String jogo) {
        while (contador < jogo.length() && jogo.charAt(contador) != '"') {
            contador++;
        }
        contador++;
        String dia = "", mes = "", ano = "";
        for (int i = 0; contador < jogo.length() && i < 3; i++) {
            mes += jogo.charAt(contador);
            contador++;
        }
        mes = mes.trim();
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
            default: mes = "01"; break;
        }
        while (contador < jogo.length() && !Character.isDigit(jogo.charAt(contador)) && jogo.charAt(contador) != ',') {
            contador++;
        }
        while (contador < jogo.length() && Character.isDigit(jogo.charAt(contador))) {
            dia += jogo.charAt(contador);
            contador++;
        }
        while (contador < jogo.length() && !Character.isDigit(jogo.charAt(contador))) {
            contador++;
        }
        while (contador < jogo.length() && jogo.charAt(contador) != '"') {
            ano += jogo.charAt(contador);
            contador++;
        }
        if (dia.isEmpty()) dia = "01";
        if (mes.isEmpty()) mes = "01";
        if (ano.isEmpty()) ano = "0000";
        return dia + "/" + mes + "/" + ano;
    }

    static int capturaEstimatedOwners(String jogo) {
        int estimatedOwners = 0;
        while (contador < jogo.length() && jogo.charAt(contador) != ',') {
            contador++;
        }
        contador++;
        while (contador < jogo.length() && jogo.charAt(contador) != ',') {
            if (Character.isDigit(jogo.charAt(contador))) {
                estimatedOwners = estimatedOwners * 10 + (jogo.charAt(contador) - '0');
            }
            contador++;
        }
        return estimatedOwners;
    }

    static float capturaPrice(String jogo) {
        String price = "";
        while (contador < jogo.length()
                && !Character.isDigit(jogo.charAt(contador))
                && jogo.charAt(contador) != 'F') {
            contador++;
        }
        while (contador < jogo.length() && jogo.charAt(contador) != ',') {
            price += jogo.charAt(contador);
            contador++;
        }
        price = price.trim();
        if (price.isEmpty() || price.toLowerCase().contains("free to play")) {
            return 0.0f;
        }
        price = price.replaceAll("[^0-9.]", "");
        try {
            return Float.parseFloat(price);
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }

    static int capturaSupportedLanguages(String jogo, String[] supportedLanguages) {
        int count = 0;
        while (contador < jogo.length() && jogo.charAt(contador) != ']' && count < supportedLanguages.length) {
            String lingua = "";
            while (contador < jogo.length() && !Character.isAlphabetic(jogo.charAt(contador))) {
                contador++;
            }
            while (contador < jogo.length() && jogo.charAt(contador) != ',' && jogo.charAt(contador) != ']') {
                if (jogo.charAt(contador) != '"') {
                    lingua += jogo.charAt(contador);
                }
                contador++;
            }
            supportedLanguages[count++] = lingua.trim();
        }
        while (contador < jogo.length() && jogo.charAt(contador) != ',') {
            contador++;
        }
        if (contador < jogo.length())
            contador++;
        return count;
    }

    static int capturaMetacriticScore(String jogo) {
        String metacriticScore = "";
        while (contador < jogo.length() && jogo.charAt(contador) != ',') {
            contador++;
        }
        contador++;
        while (contador < jogo.length() && Character.isDigit(jogo.charAt(contador))) {
            metacriticScore += jogo.charAt(contador);
            contador++;
        }
        if (metacriticScore.isEmpty())
            return -1;
        else
            return Integer.parseInt(metacriticScore);
    }

    static float capturaUserScore(String jogo) {
        String userScore = "";
        while (contador < jogo.length() && jogo.charAt(contador) != ',') {
            contador++;
        }
        contador++;
        while (contador < jogo.length()
                && (Character.isDigit(jogo.charAt(contador)) || jogo.charAt(contador) == '.')) {
            userScore += jogo.charAt(contador);
            contador++;
        }
        if (userScore.isEmpty())
            return -1.0f;
        else
            return Float.parseFloat(userScore);
    }

    static int capturaAchievements(String jogo) {
        String achievements = "";
        while (contador < jogo.length() && jogo.charAt(contador) != ',') {
            contador++;
        }
        contador++;
        while (contador < jogo.length()
                && (Character.isDigit(jogo.charAt(contador)) || jogo.charAt(contador) == '.')) {
            achievements += jogo.charAt(contador);
            contador++;
        }
        if (achievements.isEmpty())
            return 0;
        else
            return Integer.parseInt(achievements);
    }

    static int capturaUltimosArryays(String jogo, String[] categoria) {
        int count = 0;
        while (contador < jogo.length() && jogo.charAt(contador) != '"') {
            contador++;
        }

        if (contador < jogo.length() && jogo.charAt(contador) == '"') {
            contador++;
            while (contador < jogo.length() && jogo.charAt(contador) != '"' && count < categoria.length) {
                String parte = "";
                while (contador < jogo.length()
                        && jogo.charAt(contador) != ','
                        && jogo.charAt(contador) != '"') {
                    parte += jogo.charAt(contador);
                    contador++;
                }
                categoria[count++] = parte.trim();
                if (contador < jogo.length() && jogo.charAt(contador) == ',') {
                    contador++;
                }
            }
            if (contador < jogo.length() && jogo.charAt(contador) == '"') {
                contador++;
            }
        } else {
            if (count < categoria.length) {
                String parte = "";
                while (contador < jogo.length() && jogo.charAt(contador) != ',') {
                    parte += jogo.charAt(contador);
                    contador++;
                }
                categoria[count++] = parte.trim();
            }
        }
        if (contador < jogo.length() && jogo.charAt(contador) == ',') {
            contador++;
        }
        return count;
    }
}
