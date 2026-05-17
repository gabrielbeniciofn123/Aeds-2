#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>


typedef struct Data {
    int ano;
    int mes;
    int dia;
} Data;


typedef struct Hora {
    int hora;
    int minuto;
} Hora;


typedef struct Restaurante {
    int id;
    char* nome;
    char* cidade;
    int capacidade;
    double avaliacao;
    int n_tipos_cozinha;
    char** tipos_cozinha;
    char preco[10];
    Hora horario_abertura;
    Hora horario_fechamento;
    Data data_abertura;
    int aberto;
} Restaurante;

typedef struct Colecao_Restaurantes {
    int tamanho;
    Restaurante** restaurantes;
} Colecao_Restaurantes;

Data parse_data(char *s) {
    Data d;
    sscanf(s, "%d-%d-%d", &d.ano, &d.mes, &d.dia);
    return d;
}

void formatar_data(Data *data, char *buffer) {
    sprintf(buffer, "%02d/%02d/%04d", data->dia, data->mes, data->ano);
}

Hora parse_hora(char *s) {
    Hora h;
    sscanf(s, "%d:%d", &h.hora, &h.minuto);
    return h;
}

void formatar_hora(Hora *hora, char *buffer) {
    sprintf(buffer, "%02d:%02d", hora->hora, hora->minuto);
}

Restaurante* parse_restaurante(char *s) {
    Restaurante* r = (Restaurante*) malloc(sizeof(Restaurante));
    char tipos_str[100];
    char horario_str[20];
    char data_str[15];
    char aberto_str[10];
    char nome_temp[100];
    char cidade_temp[100];

    sscanf(s, "%d,%[^,],%[^,],%d,%lf,%[^,],%[^,],%[^,],%[^,],%s",
           &r->id, nome_temp, cidade_temp, &r->capacidade, &r->avaliacao,
           tipos_str, r->preco, horario_str, data_str, aberto_str);
    
    r->nome = (char*) malloc((strlen(nome_temp) + 1) * sizeof(char));
    strcpy(r->nome, nome_temp);
    
    r->cidade = (char*) malloc((strlen(cidade_temp) + 1) * sizeof(char));
    strcpy(r->cidade, cidade_temp);

    r->n_tipos_cozinha = 2;
    r->tipos_cozinha = (char**) malloc(2 * sizeof(char*));
    r->tipos_cozinha[0] = (char*) malloc(50 * sizeof(char));
    r->tipos_cozinha[1] = (char*) malloc(50 * sizeof(char));
    sscanf(tipos_str, "%[^;];%s", r->tipos_cozinha[0], r->tipos_cozinha[1]);


    char abertura_str[10], fechamento_str[10];
    sscanf(horario_str, "%[^-]-%s", abertura_str, fechamento_str);
    r->horario_abertura = parse_hora(abertura_str);
    r->horario_fechamento = parse_hora(fechamento_str);
    r->data_abertura = parse_data(data_str);
    r->aberto = (strcmp(aberto_str, "true") == 0) ? 1 : 0;

    return r;
}


void formatar_restaurante(Restaurante *r, char *buffer) {
    char data_buf[15], abertura_buf[10], fechamento_buf[10];

    formatar_data(&r->data_abertura, data_buf);
    formatar_hora(&r->horario_abertura, abertura_buf);
    formatar_hora(&r->horario_fechamento, fechamento_buf);

    sprintf(buffer, "[%d ## %s ## %s ## %d ## %.1f ## [%s,%s] ## %s ## %s-%s ## %s ## %s]",
            r->id, r->nome, r->cidade, r->capacidade, r->avaliacao,
            r->tipos_cozinha[0], r->tipos_cozinha[1],
            r->preco, abertura_buf, fechamento_buf,
            data_buf, r->aberto ? "true" : "false");
}


void ler_csv_colecao(Colecao_Restaurantes *colecao, char *path) {
    FILE *f = fopen(path, "r");
    if (f == NULL) return;

    char linha[500];
    fgets(linha, 500, f); 

    while (fgets(linha, 500, f) != NULL) {
        int len = 0;
        while (linha[len] != '\0' && linha[len] != '\n') len++;
        linha[len] = '\0';

        if (linha[0] != '\0') {
            colecao->restaurantes[colecao->tamanho] = parse_restaurante(linha);
            colecao->tamanho++;
        }
    }
    fclose(f);
}

Colecao_Restaurantes* ler_csv() {
    Colecao_Restaurantes* colecao = (Colecao_Restaurantes*) malloc(sizeof(Colecao_Restaurantes));
    colecao->tamanho = 0;
    colecao->restaurantes = (Restaurante**) malloc(1000 * sizeof(Restaurante*));
    ler_csv_colecao(colecao, "/tmp/restaurantes.csv");
    return colecao;
}

int compara_data(Data d1, Data d2) {
    if (d1.ano != d2.ano) return d1.ano - d2.ano;
    if (d1.mes != d2.mes) return d1.mes - d2.mes;
    return d1.dia - d2.dia;
}

void heapify(Restaurante** arr, int n, int i, long *comparacoes, long *movimentacoes) {
    int maior = i;
    int esquerda = 2 * i + 1;
    int direita = 2 * i + 2;

    if (esquerda < n) {
        (*comparacoes)++;
        if (compara_data(arr[esquerda]->data_abertura, arr[maior]->data_abertura) > 0) {
            maior = esquerda;
        }
    }

    if (direita < n) {
        (*comparacoes)++;
        if (compara_data(arr[direita]->data_abertura, arr[maior]->data_abertura) > 0) {
            maior = direita;
        }
    }

    if (maior != i) {
        Restaurante* temp = arr[i];
        arr[i] = arr[maior];
        arr[maior] = temp;
        (*movimentacoes) += 3;
        heapify(arr, n, maior, comparacoes, movimentacoes);
    }
}

void heapsort_parcial(Restaurante** arr, int n, long *comparacoes, long *movimentacoes) {
    int limite = (n < 10) ? n : 10;
    
    // Construir max-heap dos primeiros 'limite' elementos
    for (int i = limite / 2 - 1; i >= 0; i--) {
        heapify(arr, limite, i, comparacoes, movimentacoes);
    }
    
    // Processar elementos restantes
    for (int i = limite; i < n; i++) {
        (*comparacoes)++;
        if (compara_data(arr[i]->data_abertura, arr[0]->data_abertura) < 0) {
            // Elemento é menor que o maior dos 'limite' primeiros (raiz do max-heap)
            Restaurante* temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;
            (*movimentacoes) += 3;
            heapify(arr, limite, 0, comparacoes, movimentacoes);
        }
    }
    
    for (int i = limite - 1; i > 0; i--) {
        Restaurante* temp = arr[0];
        arr[0] = arr[i];
        arr[i] = temp;
        (*movimentacoes) += 3;
        heapify(arr, i, 0, comparacoes, movimentacoes);
    }
}

int main() {
    Colecao_Restaurantes* colecao = ler_csv();

    int id;
    scanf("%d", &id);
    
    Restaurante** selecionados = (Restaurante**) malloc(1000 * sizeof(Restaurante*));
    int n = 0;

    while (id != -1) {
        Restaurante* r = NULL;
        int i;
        for (i = 0; i < colecao->tamanho && r == NULL; i++) {
            if (colecao->restaurantes[i]->id == id) {
                r = colecao->restaurantes[i];
            }
        }

        if (r != NULL) {
            selecionados[n++] = r;
        }

        scanf("%d", &id);
    }

    long comparacoes = 0;
    long movimentacoes = 0;
    clock_t inicio = clock();

    heapsort_parcial(selecionados, n, &comparacoes, &movimentacoes);

    clock_t fim = clock();
    long tempo = (long)((fim - inicio) * 1000 / CLOCKS_PER_SEC);

    for (int i = 0; i < n; i++) {
        char buffer[500];
        formatar_restaurante(selecionados[i], buffer);
        printf("%s\n", buffer);
    }

    FILE* log = fopen("891350_heapsort_parcial.txt", "w");
    if (log != NULL) {
        fprintf(log, "891350\t%ld\t%ld\t%ld", comparacoes, movimentacoes, tempo);
        fclose(log);
    }

    free(selecionados);

    return 0;
}