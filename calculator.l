%{
#include <stdio.h>
#include <ctype.h>
#include <string.h>

#define    	digit    	300
#define		ERRORCHAR	600

/*Define keywords here:*/

/*Define double_character terminates:   */


char str1[20];
int line_no=1;
/*int id_or_keyword(char *); */
%}
/************************************
*	MACRO DEFINITION	*
*************************************/
letter		[A-Za-z]
alnum		[A-Za-z0-9]
d			[0-9]
white		[\t\040]
white_line  [\n]
%start		COMMENT
/*****************************************
*	RECOGNIION RULES	         *
*****************************************/
%%
{d}+		{
                ECHO;
                strcpy(str1,yytext);
                return digit;
            }

"("			{ECHO;return '(';}
")"			{ECHO;return ')';}

"\."        {ECHO;return '.';}
"*"			{ECHO;return '*';}
"+"			{ECHO;return '+';}
"-"			{ECHO;return '-';}
"/"			{ECHO;return '/';}
"%"			{ECHO;return '%';}

{white}+		{ECHO;}
{white_line}    {
                    //ECHO;
                    return '\n';
                    line_no++;
                }

.			{
                printf("Invalid char %s\n",yytext);
                return ERRORCHAR;
            }

%%
/*int id_or_keyword(char *str)
{
 unsigned int i,j;
 char *p,buf[20];
 for(i=0;i<14;i++)
 { p=str;
   for(j=0;j<strlen(str);j++)
    buf[j]=(char)toupper(p[j]);
   buf[j]='\0';
   if(!strcmp(KeyWord[i],buf))break;
 }
 if(i<14) return i+400;
 return Iden;
}*/

#ifdef LEX_MAIN
int main(int argc, char** argv)
{
    int c;
    FILE *pout;

    yyin = stdin;

    if(argc>1) {
        if((yyin=fopen(argv[1],"r"))==NULL) {
            printf("Can't open file %s\n",argv[1]); 
            exit(0);
        }
    }

    pout=fopen("rslt.dat","w");
    while(c=yylex()) {
        static int i=0;
        fprintf(pout,"(%4d,  %s)\t", c, yytext); i++;
        if( i==i/4*4) fprintf(pout,"\n");
    }
    fprintf(pout,"\n");
    fclose(pout);

    return 0;
}
#endif

#ifndef yywrap
 int yywrap(){return 1;}
#endif
