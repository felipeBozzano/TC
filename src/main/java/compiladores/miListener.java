package compiladores;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import compiladores.compiladoresParser.BloqueContext;
import compiladores.compiladoresParser.DeclaracionContext;
import compiladores.compiladoresParser.SiContext;
import tablaSimbolos.TablaSimbolos;

public class miListener extends compiladoresBaseListener {
    @Override
    public void enterDeclaracion(DeclaracionContext ctx) {
    }

    @Override
    public void exitDeclaracion(DeclaracionContext ctx) {
        for(int i = 0; i < ctx.getChildCount(); i++) {
            System.out.println("HIJO " + i + ": " + ctx.getChild(i).getText());
        }
    }

    private Integer bloque = 1;
    private Integer count = 0;
    private TablaSimbolos tablaSimbolos = new TablaSimbolos();

    @Override
    public void enterBloque(BloqueContext ctx) {
        /* System.out.println("TEXTO: AL ENTRAR AL BLOQUE " + ctx.getText()); */
        bloque++;
        tablaSimbolos.addContext();
    }

    @Override
    public void exitBloque(BloqueContext ctx) {
        System.out.println("TEXTO AL SALIR DEL BLOQUE: " + ctx.getText());
        System.out.println("CANTIDAD DE CONTEXTOS: " + tablaSimbolos.getSimbolos().size());
        tablaSimbolos.deleteContext();
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        count++;
    }

    @Override
    public void enterSi(SiContext ctx) {
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
