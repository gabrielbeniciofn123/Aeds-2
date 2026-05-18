import java.util.Scanner;

class Celula {
    public int elemento;
    public Celula sup, inf, esq, dir;

    public Celula() {
        this(0);
    }

    public Celula(int elemento) {
        this.elemento = elemento;
        sup = inf = esq = dir = null;
    }
}

class Matriz {

    private Celula inicio;
    private int linha, coluna;

    public Matriz(int linha, int coluna) {

        this.linha = linha;
        this.coluna = coluna;

        inicio = new Celula();

        // primeira linha
        Celula atual = inicio;

        for (int j = 1; j < coluna; j++) {
            atual.dir = new Celula();
            atual.dir.esq = atual;
            atual = atual.dir;
        }

        // demais linhas
        Celula linhaAnterior = inicio;

        for (int i = 1; i < linha; i++) {

            Celula novaLinha = new Celula();

            linhaAnterior.inf = novaLinha;
            novaLinha.sup = linhaAnterior;

            Celula atualNova = novaLinha;
            Celula atualAcima = linhaAnterior;

            for (int j = 1; j < coluna; j++) {

                atualNova.dir = new Celula();
                atualNova.dir.esq = atualNova;

                atualAcima = atualAcima.dir;

                atualAcima.inf = atualNova.dir;
                atualNova.dir.sup = atualAcima;

                atualNova = atualNova.dir;
            }

            linhaAnterior = linhaAnterior.inf;
        }
    }

    public void preencher(Scanner sc) {

        Celula linhaAtual = inicio;

        for (int i = 0; i < linha; i++) {

            Celula colunaAtual = linhaAtual;

            for (int j = 0; j < coluna; j++) {

                colunaAtual.elemento = sc.nextInt();
                colunaAtual = colunaAtual.dir;
            }

            linhaAtual = linhaAtual.inf;
        }
    }

    public void mostrarDiagonalPrincipal() {

        Celula atual = inicio;

        while (atual != null) {

            System.out.print(atual.elemento + " ");

            if (atual.inf != null && atual.dir != null) {
                atual = atual.inf.dir;
            } else {
                atual = null;
            }
        }

        System.out.println();
    }

    public void mostrarDiagonalSecundaria() {

        Celula atual = inicio;

        while (atual.dir != null) {
            atual = atual.dir;
        }

        while (atual != null) {

            System.out.print(atual.elemento + " ");

            if (atual.inf != null && atual.esq != null) {
                atual = atual.inf.esq;
            } else {
                atual = null;
            }
        }

        System.out.println();
    }

    public Matriz somar(Matriz m) {

        Matriz resp = new Matriz(linha, coluna);

        Celula linhaA = this.inicio;
        Celula linhaB = m.inicio;
        Celula linhaR = resp.inicio;

        for (int i = 0; i < linha; i++) {

            Celula a = linhaA;
            Celula b = linhaB;
            Celula r = linhaR;

            for (int j = 0; j < coluna; j++) {

                r.elemento = a.elemento + b.elemento;

                a = a.dir;
                b = b.dir;
                r = r.dir;
            }

            linhaA = linhaA.inf;
            linhaB = linhaB.inf;
            linhaR = linhaR.inf;
        }

        return resp;
    }

    public Matriz multiplicar(Matriz m) {

        Matriz resp = new Matriz(this.linha, m.coluna);

        Celula linhaA = this.inicio;
        Celula linhaResp = resp.inicio;

        for (int i = 0; i < this.linha; i++) {

            Celula colunaResp = linhaResp;
            Celula colunaB = m.inicio;

            for (int j = 0; j < m.coluna; j++) {

                int soma = 0;

                Celula a = linhaA;
                Celula b = colunaB;

                for (int k = 0; k < this.coluna; k++) {

                    soma += a.elemento * b.elemento;

                    a = a.dir;
                    b = b.inf;
                }

                colunaResp.elemento = soma;

                colunaResp = colunaResp.dir;
                colunaB = colunaB.dir;
            }

            linhaA = linhaA.inf;
            linhaResp = linhaResp.inf;
        }

        return resp;
    }

    public void mostrar() {

        Celula linhaAtual = inicio;

        for (int i = 0; i < linha; i++) {

            Celula colunaAtual = linhaAtual;

            for (int j = 0; j < coluna; j++) {

                System.out.print(colunaAtual.elemento);

                if (j < coluna - 1) {
                    System.out.print(" ");
                }

                colunaAtual = colunaAtual.dir;
            }

            System.out.println();

            linhaAtual = linhaAtual.inf;
        }
    }
}

public class    Matrizdinamica {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int casos = sc.nextInt();

        for (int t = 0; t < casos; t++) {

            int l1 = sc.nextInt();
            int c1 = sc.nextInt();

            Matriz m1 = new Matriz(l1, c1);
            m1.preencher(sc);

            int l2 = sc.nextInt();
            int c2 = sc.nextInt();

            Matriz m2 = new Matriz(l2, c2);
            m2.preencher(sc);

            m1.mostrarDiagonalPrincipal();
            m1.mostrarDiagonalSecundaria();

            Matriz soma = m1.somar(m2);
            soma.mostrar();

            Matriz mult = m1.multiplicar(m2);
            mult.mostrar();
        }

        sc.close();
    }
}