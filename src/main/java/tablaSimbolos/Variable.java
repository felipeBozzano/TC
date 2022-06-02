package tablaSimbolos;

public class Variable extends ID {

    public Variable(String id, String tipo, boolean inicializado) {
        super.id = id;
        super.tipo = tipo;
        super.inicializada = inicializado;
        super.usada = false;
    }

    @Override
    public String toString() {
        return tipo + " " + id + " inicializada: " + inicializada + " usada: " + usada ;
    }
    
}
