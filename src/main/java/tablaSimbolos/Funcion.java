package tablaSimbolos;

import java.util.List;

public class Funcion extends ID {
    List<TipoDato> args;

    public Funcion(List<TipoDato> args, String id, TipoDato tipo, char inicializado) {
        this.args = args;
        super.id = id;
        super.tipo = tipo;
        super.inicializada = inicializado == ';';
        super.usada = false;
    }
}
