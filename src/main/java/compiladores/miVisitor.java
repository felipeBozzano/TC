/* package compiladores;

import compiladores.compiladoresParser.FactorContext;
import compiladores.compiladoresParser.SiContext;

public class miVisitor extends compiladoresBaseVisitor<String> {

    @Override
    public String visitSi(SiContext ctx) {
        System.out.println("Comenzamos a recorrer el arbol");
        visitChildren(ctx);
        System.out.println("Fin del recorrido");
        return "Hola";
    }

    @Override
    public String visitFactor(FactorContext ctx) {
        System.out.println("\tFactor -> tiene " + ctx.getChildCount() + " hijos");
        System.out.println("\t--> " + ctx.getText());
        if (ctx.getChildCount() != 3)
            visitChildren(ctx);
        return "Factor";
    }
    
}
 */

package compiladores;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ErrorNode;

import compiladores.compiladoresParser.DeclaracionContext;
import compiladores.compiladoresParser.InstruccionContext;
import compiladores.compiladoresParser.InstruccionesContext;
import compiladores.compiladoresParser.SiContext;

public class miVisitor extends compiladoresBaseVisitor<String> {
    String texto;
    Integer indent;
    List<ErrorNode> errores;
    
    public miVisitor() {
        errores = new ArrayList<>();
        initString();
    }
    
    /**
     * Inicia el recorrido por el arbol desde el nodo indicado
     * 
     * @param tree La raiz desde donde comenzar, puede ser un subarbol
     */
    @Override
    public String visit(ParseTree tree) {
        return super.visit(tree);
    }
        
    @Override
    public String visitSi(SiContext ctx) {
        // texto += " -<(prog) " + ctx.getText() + " | " + ctx.">- \n";
        // texto += " -<(prog) " + ctx.getStart() + " <-> " + ctx.getStop() + ">- \n";
        // texto += " -<(prog) {" + ctx.getStart().getText() + " <-> " +
        // ctx.getStop().getText() + "} >- \n";
        // texto += " -<(prog) {" + ctx.getChildCount() + " hijos -> ";
        addTextoNodo(ctx, "programa");
        visitAllHijos(ctx);
        // texto += "} >- \n";
        return texto;
    }

    @Override
    public String visitInstrucciones(InstruccionesContext ctx) {
        // texto += " -<(instrucciones) " + ctx.getChildCount() + " hijos -> ";
        addTextoNodo(ctx, "instrucciones");
        visitAllHijos(ctx.getRuleContext());
        // texto += "} >- \n";
        return texto;
    }
    
    @Override
    public String visitInstruccion(InstruccionContext ctx) {
        addTextoNodo(ctx, "instruccion");
        visitAllHijos(ctx);
        // texto += "} >- \n";
        return texto;
    }
    
    @Override
    public String visitDeclaracion(DeclaracionContext ctx) {
        addTextoNodo(ctx, "declaracion");
        visitAllHijos(ctx);
        return texto;
    }
    
    @Override
    public String visitTerminal(TerminalNode node) {
        addTextoHoja(node.getText());
        return texto;
    }

    @Override
    public String visitErrorNode(ErrorNode node) {
        addErrorNode(node);
        texto += " -<(ERROR) " + node.getText() + "> lin " + node.getSymbol().getLine() + " - \n";
        return texto;
    }
    
    public void addErrorNode (ErrorNode node) {
        errores.add(node);
    }
    
    public List<ErrorNode> getErrorNodes () {
        return errores;
    }
    
    /**
     * Visita todos los nodos hijo.
     * 
     * @param ctx Contexto del nodo donde estamos parados
     */
    public String visitAllHijos(RuleContext ctx) {
        incrementarIndentacion();
        for (int hijo = 0; hijo < ctx.getChildCount(); hijo++) {
            addTextoNuevoNodo();
            visit(ctx.getChild(hijo));
        }
        decrementarIndentacion();
        return texto;
    }

    private void initString() {
        texto = "**Caminante**\n  |\n  +--> ";
        indent = -1;
    }
    
    private void incrementarIndentacion() {
        indent += 1;
    }

    private void decrementarIndentacion() {
        indent -= 1;
    }

    private void addTextoNodo(RuleContext ctx, String nombre) {
        texto += "(" + nombre + ") " + ctx.getChildCount() + " Hijos\n";

    }

    private void addTextoHoja(String nombre) {
        texto += "token [" + nombre + "]\n";
    }

    private void addTextoNuevoNodo() {
        texto += "     " + "  |  ".repeat(indent) + "  +--> ";
    }

    @Override
    public String toString() {
        return texto;
    }

}