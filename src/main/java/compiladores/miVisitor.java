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
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.w3c.dom.TypeInfo;
import org.antlr.v4.runtime.tree.ErrorNode;

import compiladores.compiladoresParser.AContext;
import compiladores.compiladoresParser.AndContext;
import compiladores.compiladoresParser.ArgsContext;
import compiladores.compiladoresParser.AsignacionContext;
import compiladores.compiladoresParser.AsignarContext;
import compiladores.compiladoresParser.BloqueContext;
import compiladores.compiladoresParser.BloqueDeControlContext;
import compiladores.compiladoresParser.CContext;
import compiladores.compiladoresParser.CompContext;
import compiladores.compiladoresParser.ComparadorContext;
import compiladores.compiladoresParser.CondicionForContext;
import compiladores.compiladoresParser.DeclaracionContext;
import compiladores.compiladoresParser.DeclaracionFuncionContext;
import compiladores.compiladoresParser.ExpContext;
import compiladores.compiladoresParser.ExpresionContext;
import compiladores.compiladoresParser.FactorContext;
import compiladores.compiladoresParser.FuncionContext;
import compiladores.compiladoresParser.IforContext;
import compiladores.compiladoresParser.IifContext;
import compiladores.compiladoresParser.Inst_simpleContext;
import compiladores.compiladoresParser.InstruccionContext;
import compiladores.compiladoresParser.InstruccionesContext;
import compiladores.compiladoresParser.InvocacionFuncionContext;
import compiladores.compiladoresParser.IwhileContext;
import compiladores.compiladoresParser.ListaArgsContext;
import compiladores.compiladoresParser.ListaDeclaracionContext;
import compiladores.compiladoresParser.ListaParamsContext;
import compiladores.compiladoresParser.OContext;
import compiladores.compiladoresParser.OpalContext;
import compiladores.compiladoresParser.OrContext;
import compiladores.compiladoresParser.ParamContext;
import compiladores.compiladoresParser.SiContext;
import compiladores.compiladoresParser.TContext;
import compiladores.compiladoresParser.TermContext;

public class miVisitor extends compiladoresBaseVisitor<String> {
    String texto;
    Integer indent;
    List<ErrorNode> errores;
    String codigoDeTresDirecciones;
    int indexVariablesTemporales = 0;
    int indexLabelsTemporales = 0;
    Stack<String> pilaVariablesTemporales = new Stack<String>();
    Stack<String> pilaLabelsTemporales = new Stack<String>();
    Stack<String> pilaCodigo = new Stack<String>();
    String variable;
    
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
    public String visitBloque(BloqueContext ctx) {
        addTextoNodo(ctx, "bloque");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitDeclaracion(DeclaracionContext ctx) {
        addTextoNodo(ctx, "declaracion");

        pilaCodigo.push(ctx.ID().getText());

        pilaVariablesTemporales.push(ctx.ID().getText());

        visitAllHijos(ctx);

        return texto;
    }

    @Override
    public String visitListaDeclaracion(ListaDeclaracionContext ctx) {
        addTextoNodo(ctx, "listaDeclaracion");

        if(ctx.IGUAL() != null){
            pilaCodigo.push(ctx.IGUAL().getText());
        }else{
            if(ctx.COMA() != null){
                pilaCodigo.push(ctx.ID().getText());
                pilaVariablesTemporales.push(ctx.ID().getText());
            }else{
                return texto;
            }
        }

        visitAllHijos(ctx);
        System.out.println("------------    LISTA DECLARACION    ------------");
        imprimirCodigo();
        pilaCodigo.pop();

        return texto;
    }
    
    @Override
    public String visitAsignacion(AsignacionContext ctx) {
        addTextoNodo(ctx, "visitAsignacion");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitAsignar(AsignarContext ctx) {
        addTextoNodo(ctx, "visitAsignar");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitArgs(ArgsContext ctx) {
        addTextoNodo(ctx, "visitAsignar");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitBloqueDeControl(BloqueDeControlContext ctx) {
        addTextoNodo(ctx, "visitBloqueDeControl");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitComparador(ComparadorContext ctx) {
        addTextoNodo(ctx, "visitComparador");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitCondicionFor(CondicionForContext ctx) {
        addTextoNodo(ctx, "visitCondicionFor");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitDeclaracionFuncion(DeclaracionFuncionContext ctx) {
        addTextoNodo(ctx, "visitDeclaracionFuncion");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitFuncion(FuncionContext ctx) {
        addTextoNodo(ctx, "visitFuncion");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitIfor(IforContext ctx) {
        addTextoNodo(ctx, "visitIfor");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitIif(IifContext ctx) {
        addTextoNodo(ctx, "visitIif");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitInst_simple(Inst_simpleContext ctx) {
        addTextoNodo(ctx, "visitInst_simple");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitInvocacionFuncion(InvocacionFuncionContext ctx) {
        addTextoNodo(ctx, "visitInvocacionFuncion");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitIwhile(IwhileContext ctx) {
        addTextoNodo(ctx, "visitIwhile");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitListaArgs(ListaArgsContext ctx) {
        addTextoNodo(ctx, "visitListaArgs");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitListaParams(ListaParamsContext ctx) {
        addTextoNodo(ctx, "visitListaParams");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitParam(ParamContext ctx) {
        addTextoNodo(ctx, "visitParam");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitA(AContext ctx) {
        addTextoNodo(ctx, "visitA");
        
        if (ctx.getChildCount() != 0){
            pilaCodigo.push(ctx.getChild(0).getText());
            generadorNombresTemporales();
            visitAllHijos(ctx);
            System.out.println("------------    A    ------------");
            imprimirCodigo();
        }

        return texto;
    }

    @Override
    public String visitAnd(AndContext ctx) {
        addTextoNodo(ctx, "visitAnd");
        
        if (ctx.getChildCount() != 0){
            generadorNombresTemporales();
            visitAllHijos(ctx);
            System.out.println("------------    AND    ------------");
            imprimirCodigo();
        }

        return texto;
    }

    @Override
    public String visitC(CContext ctx) {
        addTextoNodo(ctx, "visitC");

        if (ctx.getChildCount() != 0){
            pilaCodigo.push(ctx.getChild(0).getText());
            generadorNombresTemporales();
            visitAllHijos(ctx);
            System.out.println("------------    C    ------------");
            imprimirCodigo();
        }

        return texto;
    }

    @Override
    public String visitComp(CompContext ctx) {
        addTextoNodo(ctx, "visitComp");

        if (ctx.getChildCount() != 0){
            generadorNombresTemporales();
            visitAllHijos(ctx);
            System.out.println("------------    COMP    ------------");
            imprimirCodigo();
        }

        return texto;
    }

    @Override
    public String visitExp(ExpContext ctx) {
        addTextoNodo(ctx, "visitExp");

        if (ctx.getChildCount() != 0){
            pilaCodigo.push(ctx.getChild(0).getText());
            generadorNombresTemporales();
            visitAllHijos(ctx);
            System.out.println("------------    EXP    ------------");
            imprimirCodigo();
        }

        return texto;
    }

    @Override
    public String visitExpresion(ExpresionContext ctx) {
        addTextoNodo(ctx, "visitExpresion");

        generadorNombresTemporales();

        visitAllHijos(ctx);

        System.out.println("------------    EXPRESION    ------------");
        imprimirCodigo();

        return texto;
    }

    @Override
    public String visitFactor(FactorContext ctx) {
        addTextoNodo(ctx, "visitFactor");

        if(ctx.getChildCount() == 1 && ctx.invocacionFuncion() == null)
            pilaCodigo.push(ctx.getChild(0).getText());

        visitAllHijos(ctx);

        return texto;
    }

    @Override
    public String visitO(OContext ctx) {
        addTextoNodo(ctx, "visitO");
    
        if (ctx.getChildCount() != 0){
            pilaCodigo.push(ctx.getChild(0).getText());
            generadorNombresTemporales();
            visitAllHijos(ctx);
            System.out.println("------------    O    ------------");
            imprimirCodigo();
        }

        return texto;
    }

    @Override
    public String visitOpal(OpalContext ctx) {
        addTextoNodo(ctx, "visitOpal");

        generadorNombresTemporales();

        visitAllHijos(ctx);

        System.out.println("------------    OPAL    ------------");
        imprimirCodigo();
        
        return texto;
    }

    @Override
    public String visitOr(OrContext ctx) {
        addTextoNodo(ctx, "visitOr");
        
        if (ctx.getChildCount() != 0){
            generadorNombresTemporales();
            visitAllHijos(ctx);
            System.out.println("------------    OR    ------------");
            imprimirCodigo();
        }

        return texto;
    }

    @Override
    public String visitTerm(TermContext ctx) {
        addTextoNodo(ctx, "visitTerm");

        generadorNombresTemporales();

        visitAllHijos(ctx);

        System.out.println("------------    TERM    ------------");
        imprimirCodigo();

        return texto;
    }

    @Override
    public String visitT(TContext ctx) {
        addTextoNodo(ctx, "visitT");

        if (ctx.getChildCount() != 0){
            pilaCodigo.push(ctx.getChild(0).getText());
            generadorNombresTemporales();
            visitAllHijos(ctx);
            System.out.println("------------    T    ------------");
            imprimirCodigo();
        }

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

    private void generadorNombresTemporales() {
        String temporal = "t" + indexVariablesTemporales;
        indexVariablesTemporales++;
        pilaVariablesTemporales.push(temporal);
        pilaCodigo.push(temporal);
        pilaCodigo.push("=");
    }

    private void generadorLabelsTemporales() {
        String temporal = "l" + indexLabelsTemporales;
        indexLabelsTemporales++;
        pilaLabelsTemporales.push(temporal);
    }

    // Tomamos la ultima variable temporal y la comparamos contra la
    // ultima variable de la pila de codigo, hasta que matcheen entonces
    // imprimimos el codigo por pantalla.
    private void imprimirCodigo() {
        System.out.println("pilaCodigo: " + pilaCodigo);
        System.out.println("pilaVariablesTemporales: " + pilaVariablesTemporales);

        String varTemp = pilaVariablesTemporales.pop();
        System.out.println("varTemp: " + varTemp);
        List<String> codigo = new LinkedList<String>();

        while(!varTemp.equals(pilaCodigo.lastElement())){
            codigo.add(0, pilaCodigo.pop());
        }

        System.out.print(varTemp + " ");
        for(int i = 0; i < codigo.size(); i++)
            System.out.print(codigo.get(i) + " ");
        System.out.println("");
    }

}