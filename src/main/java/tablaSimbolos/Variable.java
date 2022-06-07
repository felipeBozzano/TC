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
        return this.tipo + " " + this.id + " inicializada: " + this.getInicializada() + " usada: " + this.usada ;
    }

    public String getInicializada() {
        return this.inicializada == true ? "true" : "false";
    }
    
}
