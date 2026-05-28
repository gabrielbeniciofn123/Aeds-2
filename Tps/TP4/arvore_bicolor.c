/*
  Árvore Bicolor (Red-Black Tree) em C 
  Arquivo Java equivalente: arvore_avl.java
  Refaz arvore_binaria.c usando balanceamento garantido por coloração dos nós.
   Log gerado: 889921.arvore_bicolor.txt
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <time.h>

static const char* MATRICULA = "889921";
static long long comparacoesPesquisa = 0;
=========================================================== */

typedef struct { int ano; int mes; int dia; } Data;
typedef struct { int hora; int minuto; } Hora;

typedef struct {
    int id;
    char* nome;
    char* cidade;
    int capacidade;
    double avaliacao;
    int n_tipos_cozinha;
    char** tipos_cozinha;
    int faixa_preco;
    Hora horario_abertura;
    Hora horario_fechamento;
    Data data_abertura;
    bool aberto;
} Restaurante;

typedef struct {
    int tamanho;
    Restaurante** restaurantes;
} Colecao_Restaurantes;

Data parse_data(char* s) {
    Data d; d.ano = 0; d.mes = 0; d.dia = 0;
    int parte = 0;
    for (int i = 0; s[i]; i++) {
        char c = s[i];
        if (c == '-') parte++;
        else if (c >= '0' && c <= '9') {
            int dig = c - '0';
            if (parte == 0) d.ano = d.ano * 10 + dig;
            else if (parte == 1) d.mes = d.mes * 10 + dig;
            else d.dia = d.dia * 10 + dig;
        }
    }
    return d;
}

void formatar_data(Data* data, char* buffer) {
    sprintf(buffer, "%02d/%02d/%04d", data->dia, data->mes, data->ano);
}

Hora parse_hora(char* s) {
    Hora h; h.hora = 0; h.minuto = 0;
    int parte = 0;
    for (int i = 0; s[i]; i++) {
        char c = s[i];
        if (c == ':') parte++;
        else if (c >= '0' && c <= '9') {
            int dig = c - '0';
            if (parte == 0) h.hora = h.hora * 10 + dig;
            else h.minuto = h.minuto * 10 + dig;
        }
    }
    return h;
}

void formatar_hora(Hora* hora, char* buffer) {
    sprintf(buffer, "%02d:%02d", hora->hora, hora->minuto);
}

static int parse_int(char* s) {
    int r = 0;
    for (int i = 0; s[i]; i++)
        if (s[i] >= '0' && s[i] <= '9') r = r * 10 + (s[i] - '0');
    return r;
}

static double parse_double(char* s) {
    double r = 0; bool dec = false; double div = 10.0;
    for (int i = 0; s[i]; i++) {
        char c = s[i];
        if (c >= '0' && c <= '9') {
            int dig = c - '0';
            if (!dec) r = r * 10 + dig;
            else { r += dig / div; div *= 10; }
        } else if (c == '.') dec = true;
    }
    return r;
}

static char* duplicar_string(char* s) {
    int len = 0;
    while (s[len]) len++;
    char* novo = (char*) malloc((len + 1) * sizeof(char));
    for (int i = 0; i <= len; i++) novo[i] = s[i];
    return novo;
}

Restaurante* parse_restaurante(char* s) {
    Restaurante* r = (Restaurante*) malloc(sizeof(Restaurante));
    char campos[10][512];
    int campo = 0, bi = 0;
    for (int i = 0; s[i] && s[i] != '\n' && s[i] != '\r'; i++) {
        if (s[i] == ',') { campos[campo][bi] = '\0'; campo++; bi = 0; }
        else campos[campo][bi++] = s[i];
    }
    campos[campo][bi] = '\0';
    r->id = parse_int(campos[0]);
    r->nome = duplicar_string(campos[1]);
    r->cidade = duplicar_string(campos[2]);
    r->capacidade = parse_int(campos[3]);
    r->avaliacao = parse_double(campos[4]);
    r->n_tipos_cozinha = 1;
    for (int i = 0; campos[5][i]; i++) if (campos[5][i] == ';') r->n_tipos_cozinha++;
    r->tipos_cozinha = (char**) malloc(r->n_tipos_cozinha * sizeof(char*));
    int ti = 0, ci = 0; char tipo_buf[256];
    for (int i = 0; ; i++) {
        char c = campos[5][i];
        if (c == ';' || c == '\0') {
            tipo_buf[ci] = '\0';
            r->tipos_cozinha[ti++] = duplicar_string(tipo_buf);
            ci = 0;
            if (c == '\0') break;
        } else tipo_buf[ci++] = c;
    }
    r->faixa_preco = 0;
    for (int i = 0; campos[6][i]; i++) if (campos[6][i] == '$') r->faixa_preco++;
    int dash_pos = -1;
    for (int i = 0; campos[7][i]; i++) if (campos[7][i] == '-') { dash_pos = i; break; }
    char hor_ab[6], hor_fech[6];
    for (int i = 0; i < dash_pos; i++) hor_ab[i] = campos[7][i];
    hor_ab[dash_pos] = '\0';
    int j = 0;
    for (int i = dash_pos + 1; campos[7][i]; i++) hor_fech[j++] = campos[7][i];
    hor_fech[j] = '\0';
    r->horario_abertura  = parse_hora(hor_ab);
    r->horario_fechamento = parse_hora(hor_fech);
    r->data_abertura = parse_data(campos[8]);
    r->aberto = (campos[9][0] == 't');
    return r;
}

void formatar_restaurante(Restaurante* r, char* buffer) {
    char hor_ab[6], hor_fech[6], data_ab[11];
    formatar_hora(&r->horario_abertura, hor_ab);
    formatar_hora(&r->horario_fechamento, hor_fech);
    formatar_data(&r->data_abertura, data_ab);
    char tipos[512]; int ti = 0;
    for (int i = 0; i < r->n_tipos_cozinha; i++) {
        if (i > 0) tipos[ti++] = ',';
        for (int k = 0; r->tipos_cozinha[i][k]; k++) tipos[ti++] = r->tipos_cozinha[i][k];
    }
    tipos[ti] = '\0';
    char fp[5];
    for (int i = 0; i < r->faixa_preco; i++) fp[i] = '$';
    fp[r->faixa_preco] = '\0';
    sprintf(buffer, "[%d ## %s ## %s ## %d ## %.1f ## [%s] ## %s ## %s-%s ## %s ## %s]",
            r->id, r->nome, r->cidade, r->capacidade, r->avaliacao,
            tipos, fp, hor_ab, hor_fech, data_ab, r->aberto ? "true" : "false");
}

void ler_csv_colecao(Colecao_Restaurantes* colecao, char* path) {
    FILE* f = fopen(path, "r");
    if (!f) return;
    colecao->restaurantes = (Restaurante**) malloc(2000 * sizeof(Restaurante*));
    colecao->tamanho = 0;
    char linha[1024];
    fgets(linha, sizeof(linha), f);
    while (fgets(linha, sizeof(linha), f))
        if (linha[0] != '\n' && linha[0] != '\0')
            colecao->restaurantes[colecao->tamanho++] = parse_restaurante(linha);
    fclose(f);
}

Colecao_Restaurantes* ler_csv() {
    Colecao_Restaurantes* c = (Colecao_Restaurantes*) malloc(sizeof(Colecao_Restaurantes));
    ler_csv_colecao(c, "/tmp/restaurantes.csv");
    return c;
}

#define VERMELHO 0
#define PRETO    1

typedef struct No {
    Restaurante* restaurante;
    struct No* esq;
    struct No* dir;
    struct No* pai;
    int cor;
} No;


static No NIL_NODE;
static No* NIL = &NIL_NODE;

void init_nil() {
    NIL->cor = PRETO;
    NIL->esq = NIL->dir = NIL->pai = NULL;
    NIL->restaurante = NULL;
}


void rotacao_esq(No** raiz, No* x) {
    No* y = x->dir;

    x->dir = y->esq;
    if (y->esq != NIL) y->esq->pai = x;

    y->pai = x->pai;
    if (x->pai == NIL)       *raiz = y;          /* x era a raiz */
    else if (x == x->pai->esq) x->pai->esq = y;
    else                       x->pai->dir = y;

    y->esq = x;
    x->pai = y;
}

void rotacao_dir(No** raiz, No* y) {
    No* x = y->esq;

    y->esq = x->dir;
    if (x->dir != NIL) x->dir->pai = y;

    x->pai = y->pai;
    if (y->pai == NIL)       *raiz = x;
    else if (y == y->pai->dir) y->pai->dir = x;
    else                       y->pai->esq = x;

    x->dir = y;
    y->pai = x;
}

void inserir_fixup(No** raiz, No* z) {
    while (z->pai->cor == VERMELHO) {
        if (z->pai == z->pai->pai->esq) {   /* pai é filho ESQUERDO do avô */
            No* tio = z->pai->pai->dir;

            if (tio->cor == VERMELHO) {                   /* Caso 1 */
                z->pai->cor        = PRETO;
                tio->cor           = PRETO;
                z->pai->pai->cor   = VERMELHO;
                z = z->pai->pai;                          /* sobe para o avô */
            } else {
                if (z == z->pai->dir) {                   /* Caso 2 → Caso 3 */
                    z = z->pai;
                    rotacao_esq(raiz, z);
                }
                z->pai->cor      = PRETO;                 /* Caso 3 */
                z->pai->pai->cor = VERMELHO;
                rotacao_dir(raiz, z->pai->pai);
            }
        } else {                            /* pai é filho DIREITO do avô (simétrico) */
            No* tio = z->pai->pai->esq;

            if (tio->cor == VERMELHO) {                   /* Caso 1 simétrico */
                z->pai->cor        = PRETO;
                tio->cor           = PRETO;
                z->pai->pai->cor   = VERMELHO;
                z = z->pai->pai;
            } else {
                if (z == z->pai->esq) {                   /* Caso 2 simétrico */
                    z = z->pai;
                    rotacao_dir(raiz, z);
                }
                z->pai->cor      = PRETO;                 /* Caso 3 simétrico */
                z->pai->pai->cor = VERMELHO;
                rotacao_esq(raiz, z->pai->pai);
            }
        }
    }
    /* Garante que a raiz seja sempre PRETA (propriedade 2 da RB). */
    (*raiz)->cor = PRETO;
}


void inserir(No** raiz, Restaurante* r) {
    No* novo = (No*) malloc(sizeof(No));
    novo->restaurante = r;
    novo->esq = novo->dir = NIL;
    novo->cor = VERMELHO;
    novo->pai = NIL;

    /* Busca a posição de inserção (BST normal). */
    No* y = NIL;
    No* x = *raiz;
    while (x != NIL) {
        y = x;
        int cmp = strcmp(r->nome, x->restaurante->nome);
        if      (cmp < 0) x = x->esq;
        else if (cmp > 0) x = x->dir;
        else { free(novo); return; }   /* nome duplicado: ignora */
    }

    novo->pai = y;
    if (y == NIL)                                  *raiz = novo;
    else if (strcmp(r->nome, y->restaurante->nome) < 0) y->esq = novo;
    else                                            y->dir = novo;

    /* Restaura as propriedades bicolores. */
    inserir_fixup(raiz, novo);
}

void pesquisar(No* raiz, char* nome) {
    if (raiz == NIL) { printf("NAO\n"); return; }
    char caminho[4096];
    int ci = 0;
    caminho[ci++] = 'r'; caminho[ci++] = 'a'; caminho[ci++] = 'i'; caminho[ci++] = 'z';
    No* atual = raiz;
    while (atual != NIL) {
        comparacoesPesquisa++;
        int cmp = strcmp(nome, atual->restaurante->nome);
        if (cmp == 0) {
            caminho[ci++] = ' '; caminho[ci++] = 'S'; caminho[ci++] = 'I'; caminho[ci++] = 'M';
            caminho[ci] = '\0'; printf("%s\n", caminho); return;
        } else if (cmp < 0) {
            caminho[ci++] = ' '; caminho[ci++] = 'e'; caminho[ci++] = 's'; caminho[ci++] = 'q';
            atual = atual->esq;
        } else {
            caminho[ci++] = ' '; caminho[ci++] = 'd'; caminho[ci++] = 'i'; caminho[ci++] = 'r';
            atual = atual->dir;
        }
    }
    caminho[ci++] = ' '; caminho[ci++] = 'N'; caminho[ci++] = 'A'; caminho[ci++] = 'O';
    caminho[ci] = '\0'; printf("%s\n", caminho);
}

/* Impressão em ordem crescente por nome (in-order). */
void em_ordem(No* raiz) {
    if (raiz == NIL) return;
    em_ordem(raiz->esq);
    char buffer[2048];
    formatar_restaurante(raiz->restaurante, buffer);
    printf("%s\n", buffer);
    em_ordem(raiz->dir);
}

int main() {
    init_nil();  /* inicializa o sentinela NIL antes de qualquer operação */

    Colecao_Restaurantes* colecao = ler_csv();
    No* raiz = NIL;

    int id;
    while (scanf("%d", &id) == 1 && id != -1) {
        for (int i = 0; i < colecao->tamanho; i++) {
            if (colecao->restaurantes[i]->id == id) {
                inserir(&raiz, colecao->restaurantes[i]); break;
            }
        }
    }

    { int c; while ((c = getchar()) != '\n' && c != EOF); }

    clock_t t0 = clock();
    char nome_buf[512];
    while (fgets(nome_buf, sizeof(nome_buf), stdin)) {
        int len = 0;
        while (nome_buf[len] && nome_buf[len] != '\n' && nome_buf[len] != '\r') len++;
        nome_buf[len] = '\0';
        if (len == 0) continue;
        if (strcmp(nome_buf, "FIM") == 0) break;
        pesquisar(raiz, nome_buf);
    }
    clock_t t1 = clock();
    double tempo = ((double)(t1 - t0)) / CLOCKS_PER_SEC * 1000.0;

    em_ordem(raiz);

    /* Log: matrícula.arvore_bicolor.txt */
    FILE* log = fopen("889921.arvore_bicolor.txt", "w");
    fprintf(log, "%s\t%lld\t%.2f\n", MATRICULA, comparacoesPesquisa, tempo);
    fclose(log);

    return 0;
}
