// Gabriel Benicio Fonseca do Nascimento
// Matrícula: 889921
// TP07Q01 - Árvore Binária em Java
// Inserção por Name + Pesquisa mostrando caminho + Geração de Log
// Comentários feitos por mim para explicar cada etapa do raciocínio.

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

// Classe que representa um registro básico do CSV (somente ID e Name)
class Game {
    int id;
    String name;

    Game(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

// Nó da árvore binária (organizada por name)
class No {
    Game game;
    No esq, dir;

    No(Game game) {
        this.game = game;
        this.esq = null;
        this.dir = null;
    }
}

// Implementação da Árvore Binária
class Arvore {

    No raiz;

    // Inserção guiada pelo atributo NAME
    public void inserir(Game g) throws Exception {
        raiz = inserir(g, raiz);
    }

    private No inserir(Game g, No i) throws Exception {

        if (i == null) {
            // Encontrei posição para inserir
            return new No(g);

        } else if (g.name.compareTo(i.game.name) < 0) {
            // Name menor → esquerda
            i.esq = inserir(g, i.esq);

        } else if (g.name.compareTo(i.game.name) > 0) {
            // Name maior → direita
            i.dir = inserir(g, i.dir);

        } else {
            // Name igual → não insere
            throw new Exception("Elemento já existe na árvore!");
        }

        return i;
    }

    // Pesquisa com impressão do caminho
    public void pesquisar(String nome) {
        System.out.print(nome + ": =>raiz ");
        ArvoreBinaria.logWriter.print(nome + ": =>raiz ");

        boolean ok = pesquisar(nome, raiz);

        if (ok) {
            System.out.println("SIM");
            ArvoreBinaria.logWriter.println("SIM");
        } else {
            System.out.println("NAO");
            ArvoreBinaria.logWriter.println("NAO");
        }
    }

    private boolean pesquisar(String nome, No i) {

        if (i == null) return false;

        if (nome.equals(i.game.name)) {
            return true;

        } else if (nome.compareTo(i.game.name) < 0) {
            System.out.print("esq ");
            ArvoreBinaria.logWriter.print("esq ");
            return pesquisar(nome, i.esq);

        } else {
            System.out.print("dir ");
            ArvoreBinaria.logWriter.print("dir ");
            return pesquisar(nome, i.dir);
        }
    }
}

// Classe principal da TP07Q01
public class ArvoreBinaria {

    public static Scanner sc = new Scanner(System.in);
    public static PrintWriter logWriter;

    public static void main(String[] args) {

        try {
            // Log com meu número de matrícula
            logWriter = new PrintWriter(new FileWriter("889921_arvoreBinaria.txt"));
        } catch (IOException e) {
            System.err.println("Erro ao criar o arquivo de log.");
            return;
        }

        // Lê IDs da primeira parte
        String entrada = sc.nextLine();
        String ids[] = new String[2000];
        int tam = 0;

        while (!entrada.equals("FIM")) {
            ids[tam++] = entrada;
            entrada = sc.nextLine();
        }

        // Crio a árvore binária
        Arvore arvore = new Arvore();

        // Carrego do CSV e insiro os nomes
        carregarGames(ids, tam, arvore);

        // Segunda parte → nomes para pesquisar
        entrada = sc.nextLine();
        while (!entrada.equals("FIM")) {
            arvore.pesquisar(entrada.trim());
            entrada = sc.nextLine();
        }

        logWriter.close();
        sc.close();
    }

    // Carrega games do arquivo CSV
    static void carregarGames(String[] ids, int tam, Arvore arvore) {

        for (int j = 0; j < tam; j++) {

            try {
                FileInputStream fis = new FileInputStream("/tmp/games.csv");
                Scanner arq = new Scanner(fis);

                if (arq.hasNextLine()) arq.nextLine(); // pulo cabeçalho

                int idDesejado = Integer.parseInt(ids[j]);
                boolean achou = false;

                while (arq.hasNextLine() && !achou) {
                    String linha = arq.nextLine();

                    int id = capturaId(linha);

                    if (id == idDesejado) {

                        String nome = capturaName(linha);
                        Game g = new Game(id, nome);

                        try {
                            arvore.inserir(g);
                        } catch (Exception e) {
                            // Nome já existe → ignoro conforme enunciado
                        }

                        achou = true;
                    }
                }

                arq.close();
                fis.close();

            } catch (Exception e) {
                System.out.println("Erro ao ler arquivo: " + e.getMessage());
            }
        }
    }

    // Captura ID do CSV
    static int capturaId(String linha) {
        int i = 0;
        int id = 0;

        while (i < linha.length() && Character.isDigit(linha.charAt(i))) {
            id = id * 10 + (linha.charAt(i) - '0');
            i++;
        }

        return id;
    }

    // Captura Name do CSV
    static String capturaName(String linha) {

        int i = 0;

        // pular ID
        while (linha.charAt(i) != ',') i++;
        i++;

        String name = "";

        while (i < linha.length() && linha.charAt(i) != ',') {
            name += linha.charAt(i);
            i++;
        }

        return name.trim();
    }
}
