%{/*说明部分*/
#include <stdlib.h>
#include <stdio.h>
#include <ctype.h>
#include <string.h>

#define INT_TYPE    0
#define REAL_TYPE   1

extern int yylex();
FILE * yyin;
void yyerror();
extern char str1[20];

%}

%union
{
    struct exp_node
    {
        int type;
        int val;
    } *exp_info;
};

%start  Line

%token  digit  300

%type <str> digit
%type <exp_info> Expr
%type <exp_info> Term
%type <exp_info> Factor


%%	/*规则部分*/
Line   :  Expr '\n'
          {
              if(INT_TYPE==$1->type) {
                  printf("=%d\n",$1->val);/*打印表达式值*/
              }
          }
Expr   :  Expr '+' Term
          {   /*Expr=Expr+Term*/
              if(NULL==$1 || NULL==$3)
              {
                  printf("乘法参数指针为空\n");
                  exit(0);
              }
              $$->val=$1->val + $3->val;
          }
       |  Term ;/*Expr=Term*/
Term   :  Term '*' Factor
          {/*Term=Term*Factor*/
              if(NULL==$1 || NULL==$3)
              {
                  printf("乘法参数指针为空\n");
                  exit(0);
              }
              $$->val=$1->val * $3->val;
          }
       |  Factor /*Term=Factor*/;
Factor :  '(' Expr ')'
          {/*Factor=Expr*/
              $$=$2;
          }
       |  digit
          {
              struct exp_node *tmp_node=NULL;
              tmp_node=(struct exp_node*)malloc(sizeof(struct exp_node));
              if(NULL==tmp_node)
              {
                  printf("memory alloc failed\n");
                  exit(0);
              }
              
              $$=tmp_node;
              $$->type=INT_TYPE;
              $$->val=atoi(str1);
              memset(str1,0, sizeof(str1));
          }
%%
/* 程序部分 */

/* 出错处理程序 */
void yyerror()
{
    printf("Syntax error\n");
}

int main(int argc, char *argv[])
{
    /* 设置输入文件。如不进行设置，则从标准输入读取输入 */
    yyin = stdin;

    if(argc>1) {
        if((yyin=fopen(argv[1],"r"))==NULL) {
            printf("Can't open file %s\n",argv[1]);
            return -1;
        }
    }

    /* 调用语法分析程序 */
    yyparse();

    return 0;
}
