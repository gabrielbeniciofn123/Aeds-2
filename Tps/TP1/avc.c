#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <time.h>
#include <ctype.h>
#include <math.h>

#define MAX_LINE_SIZE 4096
#define MAX_FIELD_SIZE 512
#define MAX_ARRAY_ELEMENTS 50
#define MAX_IDS 100

// Estrutura para armazenar os dados de um jogo
typedef struct
{
    int id;
    char *name;
    char *releaseDate;
    int estimatedOwners;
    float price;
    char **supportedLanguages;
    int supportedLanguagesCount;
    int metacriticScore;
    float userScore;
    int achievements;
    char **publishers;
    int publishersCount;
    char **developers;
    int developersCount;
    char **categories;
    int categoriesCount;
    char **genres;
    int genresCount;
    char **tags;
    int tagsCount;
} Game;

// Estrutura do Nó AVL
typedef struct NoAVL
{
    Game *game;
    int altura;
    struct NoAVL *esq, *dir;
} NoAVL;

// Estrutura da Árvore AVL
typedef struct
{
    NoAVL *raiz;
} ArvoreAVL;

void parseAndLoadGame(Game *game, char *line);
void printGame(Game *game);
void freeGame(Game *game);
char *getNextField(char *line, int *pos);
char **splitString(const char *str, char delimiter, int *count);
char *trim(char *str);
char *formatDate(char *dateStr);
void printStringArray(char **arr, int count);
void deepCopyGame(Game *dest, const Game *src);

ArvoreAVL *criarArvore();
int alturaNo(NoAVL *no);
int getFatorBalanceamento(NoAVL *no);
int maior(int a, int b);
NoAVL *rotacaoDir(NoAVL *no);
NoAVL *rotacaoEsq(NoAVL *no);
NoAVL *inserirRec(NoAVL *no, Game *game);
void inserir(ArvoreAVL *arvore, Game *game);

// Funções de pesquisa modificadas para saída de direções
bool pesquisar(ArvoreAVL *arvore, const char *nome, FILE *logFile);
bool pesquisarRec(NoAVL *no, const char *nome, FILE *logFile);

void liberarArvoreRec(NoAVL *no);
void liberarArvore(ArvoreAVL *arvore);

// Variáveis globais
char **ids;
int idsTamanho = 0;
int comparacoes = 0; 

// Lógica Principal
int main()
{
    char lineBuffer[MAX_LINE_SIZE];
    const char *filePath = "/tmp/games.csv";
    
    // Configuração para log (simulação do log em Java)
    FILE *logFile = fopen("885375_av.txt", "w");
    if (logFile == NULL) {
        perror("Erro ao criar arquivo de log");
        return 1;
    }

    // Alocar memória para ids
    ids = (char **)malloc(sizeof(char *) * MAX_IDS);
    for (int i = 0; i < MAX_IDS; i++)
    {
        ids[i] = (char *)malloc(sizeof(char) * MAX_FIELD_SIZE);
    }

    // Ler IDs da entrada até "FIM"
    char input[MAX_FIELD_SIZE];
    while (fgets(input, MAX_FIELD_SIZE, stdin) != NULL)
    {
        input[strcspn(input, "\n")] = 0;
        if (strcmp(input, "FIM") == 0)
            break;
        strcpy(ids[idsTamanho++], input);
    }

    // Carregar todos os jogos do arquivo
    FILE *file = fopen(filePath, "r");
    if (file == NULL)
    {
        perror("Erro ao abrir o arquivo");
        return 1;
    }

    int gameCount = 0;
    fgets(lineBuffer, MAX_LINE_SIZE, file); // Pular cabeçalho
    while (fgets(lineBuffer, MAX_LINE_SIZE, file) != NULL)
    {
        gameCount++;
    }
    fclose(file);

    Game *allGames = (Game *)malloc(sizeof(Game) * gameCount);
    if (allGames == NULL)
    {
        printf("Erro de alocação de memória\n");
        return 1;
    }

    file = fopen(filePath, "r");
    if (file == NULL)
    {
        perror("Erro ao reabrir o arquivo");
        free(allGames);
        return 1;
    }

    fgets(lineBuffer, MAX_LINE_SIZE, file); // Pular cabeçalho
    int i = 0;
    while (fgets(lineBuffer, MAX_LINE_SIZE, file) != NULL)
    {
        parseAndLoadGame(&allGames[i], lineBuffer);
        i++;
    }
    fclose(file);

    // Criar Árvore AVL e inserir jogos baseados nos IDs
    ArvoreAVL *arvore = criarArvore();

    for (i = 0; i < idsTamanho; i++)
    {
        int targetId = atoi(ids[i]);
        for (int j = 0; j < gameCount; j++)
        {
            if (allGames[j].id == targetId)
            {
                // Criar cópia profunda do jogo para a árvore
                Game *novoGame = (Game *)malloc(sizeof(Game));
                deepCopyGame(novoGame, &allGames[j]);
                inserir(arvore, novoGame);
                break;
            }
        }
    }

    // Processar pesquisas
    while (fgets(input, MAX_FIELD_SIZE, stdin) != NULL)
    {
        input[strcspn(input, "\n")] = 0;

        if (strcmp(input, "FIM") == 0)
            break;

        printf("%s: ", input);
        fprintf(logFile, "%s: ", input);

        if (pesquisar(arvore, input, logFile))
        {
            printf("SIM\n");
            fprintf(logFile, "SIM\n");
        }
        else
        {
            printf("NAO\n");
            fprintf(logFile, "NAO\n");
        }
    }
    
    // --- Liberação de memória ---
    
    liberarArvore(arvore);

    for (i = 0; i < gameCount; i++)
    {
        freeGame(&allGames[i]);
    }
    free(allGames);

    for (i = 0; i < MAX_IDS; i++)
    {
        free(ids[i]);
    }
    free(ids);
    
    fclose(logFile);

    return 0;
}


bool pesquisarRec(NoAVL *no, const char *nome, FILE *logFile)
{
    if (no == NULL)
        return false;

    int cmp = strcmp(nome, no->game->name);

    if (cmp == 0)
    {
        return true;
    }
    else if (cmp < 0)
    {
        printf("esq ");
        fprintf(logFile, "esq ");
        return pesquisarRec(no->esq, nome, logFile);
    }
    else
    {
        printf("dir ");
        fprintf(logFile, "dir ");
        return pesquisarRec(no->dir, nome, logFile);
    }
}

bool pesquisar(ArvoreAVL *arvore, const char *nome, FILE *logFile)
{
    if (arvore->raiz != NULL)
    {
        printf("raiz ");
        fprintf(logFile, "raiz ");
        return pesquisarRec(arvore->raiz, nome, logFile);
    }
    return false;
}
ArvoreAVL *criarArvore()
{
    ArvoreAVL *arvore = (ArvoreAVL *)malloc(sizeof(ArvoreAVL));
    arvore->raiz = NULL;
    return arvore;
}

int maior(int a, int b)
{
    return (a > b) ? a : b;
}

int alturaNo(NoAVL *no)
{
    return (no == NULL) ? 0 : no->altura;
}

int getFatorBalanceamento(NoAVL *no)
{
    return (no == NULL) ? 0 : alturaNo(no->dir) - alturaNo(no->esq);
}

NoAVL *novoNo(Game *game)
{
    NoAVL *novo = (NoAVL *)malloc(sizeof(NoAVL));
    novo->game = game;
    novo->esq = novo->dir = NULL;
    novo->altura = 1; 
    return novo;
}

NoAVL *rotacaoDir(NoAVL *y)
{
    NoAVL *x = y->esq;
    NoAVL *T2 = x->dir;

    x->dir = y;
    y->esq = T2;

    y->altura = maior(alturaNo(y->esq), alturaNo(y->dir)) + 1;
    x->altura = maior(alturaNo(x->esq), alturaNo(x->dir)) + 1;

    return x;
}

NoAVL *rotacaoEsq(NoAVL *x)
{
    NoAVL *y = x->dir;
    NoAVL *T2 = y->esq;

    y->esq = x;
    x->dir = T2;

    x->altura = maior(alturaNo(x->esq), alturaNo(x->dir)) + 1;
    y->altura = maior(alturaNo(y->esq), alturaNo(y->dir)) + 1;

    return y;
}

NoAVL *inserirRec(NoAVL *no, Game *game)
{
    if (no == NULL)
        return novoNo(game);

    int cmp = strcmp(game->name, no->game->name);

    if (cmp < 0)
        no->esq = inserirRec(no->esq, game);
    else if (cmp > 0)
        no->dir = inserirRec(no->dir, game);
    else
    {
        freeGame(game);
        free(game);
        return no; 
    }

    no->altura = 1 + maior(alturaNo(no->esq), alturaNo(no->dir));

    int bal = getFatorBalanceamento(no);

    if (bal < -1 && strcmp(game->name, no->esq->game->name) < 0)
        return rotacaoDir(no);

    if (bal > 1 && strcmp(game->name, no->dir->game->name) > 0)
        return rotacaoEsq(no);

    if (bal < -1 && strcmp(game->name, no->esq->game->name) > 0)
    {
        no->esq = rotacaoEsq(no->esq);
        return rotacaoDir(no);
    }

    if (bal > 1 && strcmp(game->name, no->dir->game->name) < 0)
    {
        no->dir = rotacaoDir(no->dir);
        return rotacaoEsq(no);
    }

    return no;
}

void inserir(ArvoreAVL *arvore, Game *game)
{
    arvore->raiz = inserirRec(arvore->raiz, game);
}

void liberarArvoreRec(NoAVL *no)
{
    if (no != NULL)
    {
        liberarArvoreRec(no->esq);
        liberarArvoreRec(no->dir);
        freeGame(no->game);
        free(no->game);
        free(no);
    }
}

void liberarArvore(ArvoreAVL *arvore)
{
    liberarArvoreRec(arvore->raiz);
    free(arvore);
}

// Função auxiliar para deep copy de arrays de strings
char **copyStringArray(char **arr, int count) {
    char **newArr = (char **)malloc(sizeof(char *) * count);
    for (int i = 0; i < count; i++) {
        newArr[i] = strdup(arr[i]);
    }
    return newArr;
}

// Realiza a cópia profunda de um Game, alocando nova memória para strings e arrays.
void deepCopyGame(Game *dest, const Game *src)
{
    *dest = *src; 

    dest->name = strdup(src->name);
    dest->releaseDate = strdup(src->releaseDate);

    dest->supportedLanguages = copyStringArray(src->supportedLanguages, src->supportedLanguagesCount);
    dest->publishers = copyStringArray(src->publishers, src->publishersCount);
    dest->developers = copyStringArray(src->developers, src->developersCount);
    dest->categories = copyStringArray(src->categories, src->categoriesCount);
    dest->genres = copyStringArray(src->genres, src->genresCount);
    dest->tags = copyStringArray(src->tags, src->tagsCount);
}

void parseAndLoadGame(Game *game, char *line)
{
    int pos = 0;

    game->id = atoi(getNextField(line, &pos));
    game->name = getNextField(line, &pos);
    game->releaseDate = formatDate(getNextField(line, &pos));
    game->estimatedOwners = atoi(getNextField(line, &pos));

    char *priceStr = getNextField(line, &pos);
    game->price = (strcmp(priceStr, "Free to Play") == 0 || strlen(priceStr) == 0) ? 0.0f : atof(priceStr);
    free(priceStr);

    char *langStr = getNextField(line, &pos);
    langStr[strcspn(langStr, "]")] = 0;
    memmove(langStr, langStr + 1, strlen(langStr));
    for (int i = 0; langStr[i]; i++)
        if (langStr[i] == '\'')
            langStr[i] = ' ';
    game->supportedLanguages = splitString(langStr, ',', &game->supportedLanguagesCount);
    free(langStr);

    game->metacriticScore = atoi(getNextField(line, &pos));
    game->userScore = atof(getNextField(line, &pos));
    game->achievements = atoi(getNextField(line, &pos));

    game->publishers = splitString(getNextField(line, &pos), ',', &game->publishersCount);
    game->developers = splitString(getNextField(line, &pos), ',', &game->developersCount);
    game->categories = splitString(getNextField(line, &pos), ',', &game->categoriesCount);
    game->genres = splitString(getNextField(line, &pos), ',', &game->genresCount);
    game->tags = splitString(getNextField(line, &pos), ',', &game->tagsCount);
}

void printGame(Game *game)
{
    char formattedDate[12];
    strcpy(formattedDate, game->releaseDate);
    if (formattedDate[1] == '/')
    {
        memmove(formattedDate + 1, formattedDate, strlen(formattedDate) + 1);
        formattedDate[0] = '0';
    }

    printf("=> %d ## %s ## %s ## %d ## %.2f ## ",
           game->id, game->name, formattedDate, game->estimatedOwners, game->price);
    printStringArray(game->supportedLanguages, game->supportedLanguagesCount);
    printf(" ## %d ## %.1f ## %d ## ",
           game->metacriticScore,
           game->userScore,
           game->achievements);
    printStringArray(game->publishers, game->publishersCount);
    printf(" ## ");
    printStringArray(game->developers, game->developersCount);
    printf(" ## ");
    printStringArray(game->categories, game->categoriesCount);
    printf(" ## ");
    printStringArray(game->genres, game->genresCount);
    printf(" ## ");
    printStringArray(game->tags, game->tagsCount);
    printf(" ##\n");
}

void freeGame(Game *game)
{
    free(game->name);
    free(game->releaseDate);
    
    for (int i = 0; i < game->supportedLanguagesCount; i++)
        free(game->supportedLanguages[i]);
    free(game->supportedLanguages);
    
    for (int i = 0; i < game->publishersCount; i++)
        free(game->publishers[i]);
    free(game->publishers);
    
    for (int i = 0; i < game->developersCount; i++)
        free(game->developers[i]);
    free(game->developers);
    
    for (int i = 0; i < game->categoriesCount; i++)
        free(game->categories[i]);
    free(game->categories);
    
    for (int i = 0; i < game->genresCount; i++)
        free(game->genres[i]);
    free(game->genres);
    
    for (int i = 0; i < game->tagsCount; i++)
        free(game->tags[i]);
    free(game->tags);
}

char *getNextField(char *line, int *pos)
{
    char *field = (char *)malloc(sizeof(char) * MAX_FIELD_SIZE);
    int i = 0;
    bool inQuotes = false;

    if (line[*pos] == '"')
    {
        inQuotes = true;
        (*pos)++;
    }

    while (line[*pos] != '\0')
    {
        if (inQuotes)
        {
            if (line[*pos] == '"')
            {
                (*pos)++;
                break;
            }
        }
        else
        {
            if (line[*pos] == ',')
            {
                break;
            }
        }
        field[i++] = line[(*pos)++];
    }

    if (line[*pos] == ',')
    {
        (*pos)++;
    }

    field[i] = '\0';
    return field;
}

char **splitString(const char *str, char delimiter, int *count)
{
    int initialCount = 0;
    for (int i = 0; str[i]; i++)
        if (str[i] == delimiter)
            initialCount++;
    *count = initialCount + 1;

    char **result = (char **)malloc(sizeof(char *) * (*count));
    char buffer[MAX_FIELD_SIZE];
    int str_idx = 0;
    int result_idx = 0;

    for (int i = 0; i <= strlen(str); i++)
    {
        if (str[i] == delimiter || str[i] == '\0')
        {
            buffer[str_idx] = '\0';
            result[result_idx] = (char *)malloc(sizeof(char) * (strlen(buffer) + 1));
            strcpy(result[result_idx], trim(buffer));
            result_idx++;
            str_idx = 0;
        }
        else
        {
            buffer[str_idx++] = str[i];
        }
    }
    return result;
}

char *trim(char *str)
{
    char *end;
    while (isspace((unsigned char)*str))
        str++;
    if (*str == 0)
        return str;
    end = str + strlen(str) - 1;
    while (end > str && isspace((unsigned char)*end))
        end--;
    end[1] = '\0';
    return str;
}

char *formatDate(char *dateStr)
{
    char *formattedDate = (char *)malloc(sizeof(char) * 12);
    char monthStr[4] = {0};
    char day[3] = "01";
    char year[5] = "0000";

    sscanf(dateStr, "%s", monthStr);

    char *monthNum = "01";
    if (strcmp(monthStr, "Jan") == 0)
        monthNum = "01";
    else if (strcmp(monthStr, "Feb") == 0)
        monthNum = "02";
    else if (strcmp(monthStr, "Mar") == 0)
        monthNum = "03";
    else if (strcmp(monthStr, "Apr") == 0)
        monthNum = "04";
    else if (strcmp(monthStr, "May") == 0)
        monthNum = "05";
    else if (strcmp(monthStr, "Jun") == 0)
        monthNum = "06";
    else if (strcmp(monthStr, "Jul") == 0)
        monthNum = "07";
    else if (strcmp(monthStr, "Aug") == 0)
        monthNum = "08";
    else if (strcmp(monthStr, "Sep") == 0)
        monthNum = "09";
    else if (strcmp(monthStr, "Oct") == 0)
        monthNum = "10";
    else if (strcmp(monthStr, "Nov") == 0)
        monthNum = "11";
    else if (strcmp(monthStr, "Dec") == 0)
        monthNum = "12";

    char *ptr = dateStr;
    while (*ptr && !isdigit(*ptr))
        ptr++;
    if (isdigit(*ptr))
        sscanf(ptr, "%[^,], %s", day, year);

    sprintf(formattedDate, "%s/%s/%s", day, monthNum, year);
    free(dateStr);
    return formattedDate;
}

void printStringArray(char **arr, int count)
{
    printf("[");
    for (int i = 0; i < count; i++)
    {
        printf("%s", arr[i]);
        if (i < count - 1)
        {
            printf(", ");
        }
    }
    printf("]");
}