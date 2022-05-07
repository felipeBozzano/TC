grammar compiladores;

@header {
package compiladores;
}

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
IGUALDAD : '==';
DESIGUALDAD : '!=';
MENOR : '<';
MAYOR : '>';
MENOR_IGUAL : '<=';
MAYOR_IGUAL : '>=';
comparador : IGUALDAD | DESIGUALDAD | MENOR | MAYOR | MENOR_IGUAL | MAYOR_IGUAL;

ENTERO : DIGITO+;

WS : [ \t\n\r] -> skip;

programa : opal EOF;

opal : term or;

term : factor t;

factor : ENTERO
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
