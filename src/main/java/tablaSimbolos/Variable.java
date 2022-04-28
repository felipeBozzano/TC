package tablaSimbolos;

public class Variable extends ID {

    public Variable(String id, TipoDato tipo, char inicializado) {
        super.id = id;
        super.tipo = tipo;
        super.inicializada = inicializado == '=';
        super.usada = false;
    }
    
}
