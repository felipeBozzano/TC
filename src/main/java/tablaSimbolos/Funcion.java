package tablaSimbolos;

import java.util.List;

public class Funcion extends ID {
    List<TipoDato> args;

    public Funcion(List<TipoDato> args, String id, String tipo, char inicializado) {
        this.args = args;
        super.id = id;
        super.tipo = tipo;
        super.inicializada = inicializado == ';';
        super.usada = false;
    }

    @Override
    public String toString() {
        return this.tipo + " " + this.id + " inicializada: " + this.inicializada + " usada: " + this.usada + " tipos args: " + this.args;
    }
}
