grammar compiladores;

@header {
package compiladores;
}

// DIGITO = '0' | '1' | '2' | ... | '9'  Así se debería escribir si no exiestiera el operador [] -->  DIGITO : [0-9] (No hay metacaracteres dentro de  [], salvo el  - (ion))
fragment LETRA : [A-Za-z] ;
fragment DIGITO : [0-9] ;

// SIGNOS : [-+*/]; // Para convertir el - en metacaracter dentro de los [] hay que ponerlo al principio
// SEQ : '3'[4-9] | '4'[0-5] // del 34 al 45
WS : [ \t\n\r] -> skip; // Saltea los saltos de linea, espacios en blanco, y retornos de carro

//NUMERO : DIGITO+ ;

FECHASPARES : ((('0'[1-9]|'1'[0-9]|'2'[0-8])'/''0''2')|(('0'[1-9]|[12][0-9]|'3''0')'/''0'[46])|(('0'[1-9]|[12][0-9]|'3'[01])'/'('0'[8]|'1'[02])))'/'('1'[0-9][0-9][0-9]|('2''0'[01][0-9]|'2''0''2'[0-2])) ;

HORASUNO : ('0'[89]|'1'[0-2])':'[0-5]DIGITO ;

HORASDOS: ('18'':'[3-5]DIGITO)|(('19'|'20')':'[0-5]DIGITO)|('21'':'([0-2]DIGITO|'30')) ;

ID : (LETRA | '_')(LETRA | DIGITO | '_')* ;

OTRO : . ;

s : FECHASPARES { System.out.println("FECHASPARES ->" + $FECHASPARES.getText() + "<--"); } s
  | HORASUNO { System.out.println("HORASUNO ->" + $HORASUNO.getText() + "<--"); } s
  | HORASDOS { System.out.println("HORASDOS ->" + $HORASDOS.getText() + "<--"); } s
  | OTRO   { System.out.println("Otro ->" + $OTRO.getText() + "<--"); }           s
  | EOF
  ;
