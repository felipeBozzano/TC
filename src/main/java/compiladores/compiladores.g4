grammar compiladores;

@header {
package compiladores;
}

fragment LETRA : [A-Za-z] ;
fragment DIGITO : [0-9] ;

PA: '(';
PC: ')';
CA: '[';
CC: ']';
LA: '{';
LC: '}';
INT: 'int' ;
DOUBLE: 'double' ;
COMA: ',' ;
FIN: ';' ;
IGUAL: '=';
tipoDato: INT | DOUBLE ;
COMPARADOR: '==' | '!=' | '>' | '>=' | '<' | '<='  ;
NUMERO : DIGITO+ ;
ID : (LETRA | '_')(LETRA | DIGITO | '_')* ;

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

asignar: ID IGUAL (NUMERO|ID) ;

bloque: LA instrucciones LC ;

iwhile: 'while' PA comparacion PC (bloque|inst_simple);

comparacion: (NUMERO|ID) COMPARADOR (NUMERO|ID);

WS : [ \t\n\r] -> skip; // Saltea los saltos de linea, espacios en blanco, y retornos de carro

OTRO : . ;
