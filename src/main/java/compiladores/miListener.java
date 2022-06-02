package compiladores;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import compiladores.compiladoresParser.BloqueContext;
import compiladores.compiladoresParser.DeclaracionContext;
import compiladores.compiladoresParser.ListaDeclaracionContext;
import compiladores.compiladoresParser.SiContext;
import tablaSimbolos.TablaSimbolos;
import tablaSimbolos.Variable;
import tablaSimbolos.ID;

public class miListener extends compiladoresBaseListener {
    private String[] nombres;
    private Integer bloque = 1;
    private Integer count = 0;
    private TablaSimbolos tablaSimbolos = new TablaSimbolos();

    miListener(compiladoresParser parser){
        nombres = parser.getRuleNames();
    }

    @Override
    public void enterDeclaracion(DeclaracionContext ctx) {
    }

    @Override
    public void exitListaDeclaracion(ListaDeclaracionContext ctx) {
    }

    @Override
    public void enterListaDeclaracion(ListaDeclaracionContext ctx) {
    }

    @Override
    public void exitDeclaracion(DeclaracionContext ctx) {
        String tipoDato = ctx.tipoDato().getText();
        String ID = ctx.ID().getText();
        /* System.out.println( tipoDato + " " + ID); */
        ListaDeclaracionContext lista = ctx.listaDeclaracion();

        // PARA EL CASO DE int x; DONDE NO HAY listaDeclaracion
        if(lista.getChildCount() == 0) {
            Variable id = new Variable(ID, tipoDato, false);
            tablaSimbolos.addID(id);
        }

        /* System.out.println("CANTIDAD DE HIJOS" + lista.getChildCount()); */

        while (lista.getChildCount() != 0){
            if (lista.getChild(0).getText() == "=") {
                Variable id = new Variable(ID, tipoDato, true);
                tablaSimbolos.addID(id);
            }
            if (lista.getChild(0).getText() == ",") {
                Variable id = new Variable(ID, tipoDato, false);
                tablaSimbolos.addID(id);
                ID = ctx.ID().getText();
            }
            lista = lista.listaDeclaracion();
            /* System.out.println("CANTIDAD DE HIJOS" + lista.getChildCount()); */
        }

        //if (nombres[ctx.getRuleIndex()] == "declaracion");
    }

    @Override
    public void enterBloque(BloqueContext ctx) {
        /* System.out.println("TEXTO: AL ENTRAR AL BLOQUE " + ctx.getText()); */
        bloque++;
        tablaSimbolos.addContext();
    }

    @Override
    public void exitBloque(BloqueContext ctx) {
        /* System.out.println("TEXTO AL SALIR DEL BLOQUE: " + ctx.getText());
        System.out.println("CANTIDAD DE CONTEXTOS: " + tablaSimbolos.getSimbolos().size()); */
        /* for (String name: tablaSimbolos.getSimbolos().get(0).keySet()) {
            String key = name.toString();
            String value = tablaSimbolos.getSimbolos().get(0).toString();
            System.out.println(key + " " + value);
        } */
        for (var entry : tablaSimbolos.getSimbolos().get(1).entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }
        tablaSimbolos.deleteContext();
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        count++;
    }

    @Override
    public void enterSi(SiContext ctx) {
        System.out.println("");
        System.out.println("INICIO DEL PARSEO");
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        /* System.out.println("TEXTO DEL NODO: " + node.getSymbol().getText()); */
    }

    @Override
    public void exitSi(SiContext ctx) {
        System.out.println("FIN DEL PARSEO");
        System.out.println("Visitamos " + bloque + " bloques");
        System.out.println("Visitamos " + count + " nodos");
        System.out.println("CANTIDAD DE CONTEXTOS: " + tablaSimbolos.getSimbolos().size());
    }
    
}
