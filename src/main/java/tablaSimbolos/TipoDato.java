package tablaSimbolos;

public class TipoDato {
    String tipo;

    public TipoDato(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return this.tipo + " ";
    }
}
