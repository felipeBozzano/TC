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
           | asignacion
           ;

bloque: LA instrucciones LC ;

/*-----------------------------      DECLARACIONES ASIGNACIONES      ----------------------------------*/

declaracion : tipoDato expresion PUNTOYCOMA ;

// Cambiar regla declaracion: tipoDato ID listaDeclaracion PUNTOYCOMA ;

// listaDeclaracion: ... ;

expresion: ID expresion
         | COMA expresion
         | asignar expresion
         |
         ;

asignacion: asignar PUNTOYCOMA ;

asignar: ID IGUAL (opal|invocacionFuncion) ;

/*-----------------------------               FUNCIONES              ----------------------------------*/

funcion: declaracionFuncion PUNTOYCOMA
       | invocacionFuncion PUNTOYCOMA
       | definicionFuncion
       ;

declaracionFuncion: tipoDato ID PA listaParams PC;

listaParams: (param COMA)* param;

param: tipoDato ID?;

invocacionFuncion: ID PA listaArgs PC;

listaArgs: ((ID|ENTERO|invocacionFuncion) COMA)* (ID|ENTERO|invocacionFuncion);

definicionFuncion: tipoDato ID PA listaParamsObligatorios PC bloque;

listaParamsObligatorios: (paramObligatorio COMA)* paramObligatorio;

paramObligatorio: tipoDato ID;

/*-----------------------------           WHILE, IF, FOR             ----------------------------------*/

bloqueDeControl: iwhile
               | iif
               | ifor
               ;

iwhile: 'while' PA opal PC (bloque|inst_simple);

iif: 'if' PA opal PC (bloque|inst_simple);

ifor: 'for' PA condicionFor PC (bloque|inst_simple);

condicionFor: (ID|asignar)? PUNTOYCOMA opal PUNTOYCOMA asignar?;

/*-----------------------------                OPAL                  ----------------------------------*/

opal : term or;

term : factor t;

factor : (ENTERO|ID)
       | PA opal PC
       |
       ;

t : MULT factor t
  | DIV factor t
  | MOD factor t
  |
  ; 

exp : SUMA term exp
    | RESTA term exp
    | 
    ;

comp : exp c
     |
     ;

c : comparador term exp
  |
  ;

and : comp a
    |
    ;

a : AND term and
  |
  ;

or : and o
   |
   ;

o : OR term or
  |
  ;
