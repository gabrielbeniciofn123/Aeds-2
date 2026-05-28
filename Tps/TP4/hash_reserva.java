import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class hash_reserva {

    private static final String MATRICULA = "889921";
    private static long comparacoesPesquisa = 0;

    // =========================================================================
    // Tamanhos da tabela
    // =========================================================================

    // Área principal: posições 0 a 30  (31 slots)
    private static final int TAM_TAB     = 31;
    // Área de reserva: posições 31 a 49  (19 slots)
    private static final int TAM_RESERVA = 19;
    // Total: 50 posições acessíveis de 0 a 49
    private static final int TAM_TOTAL   = TAM_TAB + TAM_RESERVA;

    // Tabela unificada: [0..30] = hash principal, [31..49] = reserva
    private static Restaurante[] tabela = new Restaurante[TAM_TOTAL];
    // Próxima posição livre na área de reserva (começa em 31)
    private static int proxReserva = TAM_TAB;

    // =========================================================================
    // Classes auxiliares — idênticas à arvore_binaria.java do TP03
    // =========================================================================

    static class Data {
        private int ano, mes, dia;
        public Data(int ano, int mes, int dia) { this.ano = ano; this.mes = mes; this.dia = dia; }
        public static Data parseData(String s) {
            int ano = 0, mes = 0, dia = 0, parte = 0;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '-') parte++;
                else if (c >= '0' && c <= '9') {
                    int d = c - '0';
                    if (parte == 0) ano = ano * 10 + d;
                    else if (parte == 1) mes = mes * 10 + d;
                    else dia = dia * 10 + d;
                }
            }
            return new Data(ano, mes, dia);
        }
        public String formatar() { return String.format("%02d/%02d/%04d", dia, mes, ano); }
    }

    static class Hora {
        private int hora, minuto;
        public Hora(int hora, int minuto) { this.hora = hora; this.minuto = minuto; }
        public static Hora parseHora(String s) {
            int hora = 0, minuto = 0, parte = 0;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == ':') parte++;
                else if (c >= '0' && c <= '9') {
                    int d = c - '0';
                    if (parte == 0) hora = hora * 10 + d;
                    else minuto = minuto * 10 + d;
                }
            }
            return new Hora(hora, minuto);
        }
        public String formatar() { return String.format("%02d:%02d", hora, minuto); }
    }

    static class Restaurante {
        private int id;
        private String nome, cidade;
        private int capacidade;
        private double avaliacao;
        private String[] tiposCozinha;
        private int faixaPreco;
        private Hora horarioAbertura, horarioFechamento;
        private Data dataAbertura;
        private boolean aberto;

        public Restaurante(int id, String nome, String cidade, int capacidade, double avaliacao,
                           String[] tiposCozinha, int faixaPreco, Hora horarioAbertura,
                           Hora horarioFechamento, Data dataAbertura, boolean aberto) {
            this.id = id; this.nome = nome; this.cidade = cidade;
            this.capacidade = capacidade; this.avaliacao = avaliacao;
            this.tiposCozinha = tiposCozinha; this.faixaPreco = faixaPreco;
            this.horarioAbertura = horarioAbertura; this.horarioFechamento = horarioFechamento;
            this.dataAbertura = dataAbertura; this.aberto = aberto;
        }

        public int getId()     { return id; }
        public String getNome() { return nome; }

        private static int parseInt(String s) {
            int r = 0;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c >= '0' && c <= '9') r = r * 10 + (c - '0');
            }
            return r;
        }

        private static double parseDouble(String s) {
            double r = 0; boolean dec = false; double div = 10.0;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c >= '0' && c <= '9') {
                    int d = c - '0';
                    if (!dec) r = r * 10 + d;
                    else { r += d / div; div *= 10; }
                } else if (c == '.') dec = true;
            }
            return r;
        }

        public static Restaurante parseRestaurante(String s) {
            String[] campos = new String[10];
            int campo = 0;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '\r' || c == '\n') break;
                if (c == ',') { campos[campo++] = sb.toString(); sb = new StringBuilder(); }
                else sb.append(c);
            }
            campos[campo] = sb.toString();
            int id = parseInt(campos[0]);
            String nome = campos[1], cidade = campos[2];
            int capacidade = parseInt(campos[3]);
            double avaliacao = parseDouble(campos[4]);
            int contTipos = 1;
            for (int i = 0; i < campos[5].length(); i++)
                if (campos[5].charAt(i) == ';') contTipos++;
            String[] tiposCozinha = new String[contTipos];
            int ti = 0; StringBuilder tipo = new StringBuilder();
            for (int i = 0; i < campos[5].length(); i++) {
                char c = campos[5].charAt(i);
                if (c == ';') { tiposCozinha[ti++] = tipo.toString(); tipo = new StringBuilder(); }
                else tipo.append(c);
            }
            tiposCozinha[ti] = tipo.toString();
            int faixaPreco = 0;
            for (int i = 0; i < campos[6].length(); i++)
                if (campos[6].charAt(i) == '$') faixaPreco++;
            int dashIdx = -1;
            for (int i = 0; i < campos[7].length(); i++)
                if (campos[7].charAt(i) == '-') { dashIdx = i; break; }
            StringBuilder horAb = new StringBuilder(), horFech = new StringBuilder();
            for (int i = 0; i < campos[7].length(); i++) {
                if (i < dashIdx) horAb.append(campos[7].charAt(i));
                else if (i > dashIdx) horFech.append(campos[7].charAt(i));
            }
            Data dataAbertura = Data.parseData(campos[8]);
            boolean aberto = campos[9].length() > 0 && campos[9].charAt(0) == 't';
            return new Restaurante(id, nome, cidade, capacidade, avaliacao, tiposCozinha,
                    faixaPreco, Hora.parseHora(horAb.toString()),
                    Hora.parseHora(horFech.toString()), dataAbertura, aberto);
        }

        public String formatar() {
            StringBuilder sb = new StringBuilder();
            sb.append('[').append(id).append(" ## ").append(nome).append(" ## ").append(cidade)
              .append(" ## ").append(capacidade).append(" ## ")
              .append(String.format("%.1f", avaliacao)).append(" ## [");
            for (int i = 0; i < tiposCozinha.length; i++) {
                if (i > 0) sb.append(',');
                sb.append(tiposCozinha[i]);
            }
            sb.append("] ## ");
            for (int i = 0; i < faixaPreco; i++) sb.append('$');
            sb.append(" ## ").append(horarioAbertura.formatar()).append('-')
              .append(horarioFechamento.formatar()).append(" ## ")
              .append(dataAbertura.formatar()).append(" ## ").append(aberto).append(']');
            return sb.toString();
        }
    }

    static class ColecaoRestaurantes {
        private int tamanho;
        private Restaurante[] restaurantes;
        public ColecaoRestaurantes() { tamanho = 0; restaurantes = new Restaurante[2000]; }
        public int getTamanho()          { return tamanho; }
        public Restaurante[] getRestaurantes() { return restaurantes; }
        public void lerCsv(String path) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(path));
                br.readLine();
                String linha;
                while ((linha = br.readLine()) != null)
                    if (linha.length() > 0) restaurantes[tamanho++] = Restaurante.parseRestaurante(linha);
                br.close();
            } catch (IOException e) {}
        }
        public static ColecaoRestaurantes lerCsv() {
            ColecaoRestaurantes c = new ColecaoRestaurantes();
            c.lerCsv("/tmp/restaurantes.csv");
            return c;
        }
    }

    // =========================================================================
    // FUNÇÃO DE TRANSFORMAÇÃO (hash)
    //
    // h(nome) = (soma dos códigos ASCII de cada char do nome) mod TAM_TAB
    //
    // Exemplo: "AB" → (65 + 66) % 31 = 131 % 31 = 7
    // =========================================================================
    static int hash(String nome) {
        int soma = 0;
        for (int i = 0; i < nome.length(); i++)
            soma += (int) nome.charAt(i);
        return soma % TAM_TAB;
    }

    // =========================================================================
    // INSERÇÃO com área de reserva
    //
    // 1. Calcula posição h = hash(nome)
    // 2. Se tabela[h] estiver livre  → insere na posição principal
    // 3. Se tabela[h] já ocupado (colisão) → insere na próxima posição livre
    //    da área de reserva (31 a 49), em ordem de chegada
    // 4. Se a reserva também estiver cheia → imprime o nome do restaurante
    //    e descarta a inserção
    // =========================================================================
    static void inserir(Restaurante r) {
        int h = hash(r.getNome());

        if (tabela[h] == null) {
            // Posição principal livre: inserção direta
            tabela[h] = r;
        } else if (proxReserva < TAM_TOTAL) {
            // Colisão: encaminha para a próxima vaga da área de reserva
            tabela[proxReserva++] = r;
        } else {
            // Reserva esgotada: não é possível inserir — imprime o nome
            System.out.println(r.getNome());
        }
    }

    // =========================================================================
    // PESQUISA
    //
    // 1. Calcula h = hash(nome)
    // 2. Verifica tabela[h]: se ocupado, compara o nome (conta comparação)
    //    → encontrado: retorna "h formatar()"
    // 3. Percorre a área de reserva [31..proxReserva-1] linearmente
    //    → cada slot não-nulo conta uma comparação
    //    → encontrado: retorna "i formatar()"
    // 4. Não encontrado: retorna "-1"
    //
    // Saída: "<posição> <restaurante formatado>"  ou  "-1"
    // =========================================================================
    static String pesquisar(String nome) {
        int h = hash(nome);

        // Verifica posição principal
        if (tabela[h] != null) {
            comparacoesPesquisa++;
            if (tabela[h].getNome().equals(nome))
                return h + " " + tabela[h].formatar();
        }

        // Não encontrado na posição principal: percorre a área de reserva
        for (int i = TAM_TAB; i < proxReserva; i++) {
            comparacoesPesquisa++;
            if (tabela[i].getNome().equals(nome))
                return i + " " + tabela[i].formatar();
        }

        return "-1";
    }

    // =========================================================================
    // MAIN
    // =========================================================================
    public static void main(String[] args) throws IOException {
        ColecaoRestaurantes colecao = ColecaoRestaurantes.lerCsv();
        Restaurante[] todos = colecao.getRestaurantes();
        int tamanho = colecao.getTamanho();

        Scanner sc = new Scanner(System.in);

        // Fase 1: lê IDs e insere na tabela hash
        while (sc.hasNextInt()) {
            int id = sc.nextInt();
            if (id == -1) break;
            for (int i = 0; i < tamanho; i++) {
                if (todos[i].getId() == id) { inserir(todos[i]); break; }
            }
        }

        // Fase 2: pesquisas (cronometradas para o log)
        long t0 = System.nanoTime();
        while (sc.hasNextLine()) {
            String linha = sc.nextLine();
            if (linha.length() == 0) continue;
            if (linha.equals("FIM")) break;
            System.out.println(pesquisar(linha));
        }
        long t1 = System.nanoTime();
        sc.close();

        // Gera o arquivo de log: matrícula.hash_reserva.txt
        PrintWriter pw = new PrintWriter(new FileWriter(MATRICULA + ".hash_reserva.txt"));
        pw.println(MATRICULA + "\t" + comparacoesPesquisa + "\t" + (t1 - t0));
        pw.close();
    }
}
