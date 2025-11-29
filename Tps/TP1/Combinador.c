#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

void Combinador(char str1[], char str2[] ){
    int i=0;
    int j=0;
    while(str1[i]!='\0' || str2[j]!='\0'){
        if(str1[i]!='\0'){
            printf("%c", str1[i]);
            i++;
        }
        if(str2[j]!='\0'){
            printf("%c", str2[j]);
            j++;
        }
    }
    printf("\n");
}

int main(){
    char str1[1000];
    char str2[1000];
 while (scanf("%s %s", str1, str2)==2){
    Combinador(str1,str2);
 }
    
}