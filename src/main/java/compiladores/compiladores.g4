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
FIN: ';' ;
IGUAL: '=';
tipoDato: INT | DOUBLE ;
comparador : IGUALDAD | DESIGUALDAD | MENOR | MAYOR | MENOR_IGUAL | MAYOR_IGUAL;
ENTERO : DIGITO+;
ID : (LETRA | '_')(LETRA | DIGITO | '_')* ;

WS : [ \t\n\r] -> skip;

si: instrucciones EOF;

instrucciones: instruccion instrucciones
             | 
             ;

instruccion: inst_simple
           | bloque
           | iwhile
           ;

inst_simple: declaracion
           | asignacion
           ;

declaracion : tipoDato expresion FIN ;

expresion: ID expresion
         | COMA expresion
         | asignar expresion
         |
         ;

asignacion: asignar FIN ;

asignar: ID IGUAL opal ;

bloque: LA instrucciones LC ;

iwhile: 'while' PA opal PC (bloque|inst_simple);

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
