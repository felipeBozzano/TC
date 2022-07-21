grammar compiladores;

@header {
package compiladores;
}

fragment LETRA : [A-Za-z] ;
fragment DIGITO : [0-9] ;

SUMA : '+';
RESTA : '-';
MULT : '*';
DIV : '/';
MOD : '%';
AND : '&&';
OR : '||';
PA: '(';
PC: ')';
CA: '[';
CC: ']';
LA: '{';
LC: '}';
IGUALDAD : '==';
DESIGUALDAD : '!=';
MENOR : '<';
MAYOR : '>';
MENOR_IGUAL : '<=';
MAYOR_IGUAL : '>=';
INT: 'int' ;
DOUBLE: 'double' ;
COMA: ',' ;
PUNTOYCOMA: ';' ;
IGUAL: '=';
RETURN: 'return';
tipoDato: INT | DOUBLE ;
comparador : IGUALDAD | DESIGUALDAD | MENOR | MAYOR | MENOR_IGUAL | MAYOR_IGUAL;
ENTERO : DIGITO+;
ID : (LETRA | '_')(LETRA | DIGITO | '_')* ;

WS : [ \t\n\r] -> skip;


/*-----------------------------               INICIO                ----------------------------------*/

si: instrucciones EOF;

/*-----------------------------             INSTRUCCIONES           ----------------------------------*/

instrucciones: instruccion instrucciones
             | 
             ;

instruccion: inst_simple
           | bloque
           | funcion
           | bloqueDeControl
           ;

inst_simple: declaracion
           | asignar
           | retorno
           ;

bloque: LA instrucciones LC ;

/*-----------------------------      DECLARACIONES ASIGNACIONES      ----------------------------------*/

declaracion: tipoDato ID listaDeclaracion PUNTOYCOMA ;

listaDeclaracion: IGUAL opal listaDeclaracion
                | COMA ID listaDeclaracion
                |
                ;

asignar : asignacion PUNTOYCOMA ;

asignacion: ID IGUAL opal;

/*-----------------------------               FUNCIONES              ----------------------------------*/

funcion: declaracionFuncion
       | invocacionFuncion PUNTOYCOMA
       ;

declaracionFuncion: tipoDato ID PA param PC bloque;

param: tipoDato ID listaParams
     |
     ;

listaParams: COMA param
           |
           ;

invocacionFuncion: ID PA args PC;

args: opal listaArgs
    |
    ;

listaArgs: COMA opal listaArgs
         |
         ;

retorno : RETURN opal PUNTOYCOMA;

/*-----------------------------           WHILE, IF, FOR             ----------------------------------*/

bloqueDeControl: iwhile
               | iif
               | ifor
               ;

iwhile: 'while' PA opal PC (bloque|inst_simple);

iif: 'if' PA opal PC (bloque|inst_simple);

ifor: 'for' PA condicionFor PC (bloque|inst_simple);

condicionFor: (ID|asignacion) PUNTOYCOMA opal PUNTOYCOMA asignacion;

/*-----------------------------                OPAL                  ----------------------------------*/

opal : and o
     ;

o : OR and o
  |
  ;

and : comp a
    ;

a : AND comp a
  |
  ;

comp : expresion c
     ;

c : comparador expresion
  |
  ;

expresion : term exp;

exp : SUMA term exp
    | RESTA term exp
    |
    ;

term : factor t;

t : MULT factor t
  | DIV factor t
  | MOD factor t
  |
  ;

factor : ENTERO
       | ID
       | invocacionFuncion
       | PA opal PC
       |
       ;
