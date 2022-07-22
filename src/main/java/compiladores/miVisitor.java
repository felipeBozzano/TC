package compiladores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Trees;
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
import compiladores.compiladoresParser.IelseContext;
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
import compiladores.compiladoresParser.ParamContext;
import compiladores.compiladoresParser.RetornoContext;
import compiladores.compiladoresParser.SiContext;
import compiladores.compiladoresParser.TContext;
import compiladores.compiladoresParser.TermContext;

public class miVisitor extends compiladoresBaseVisitor<String> {
    String texto;
    Integer indent;
    List<ErrorNode> errores;
    String codigoDeTresDirecciones;
    int indexVariablesTemporales;
    int indexLabelsTemporales;
    Stack<String> pilaVariablesTemporales;
    Stack<String> pilaLabelsTemporales;
    Stack<String> pilaCodigo;
    Map<String,String> funciones;
    
    public miVisitor() {
        errores = new ArrayList<>();
        indexVariablesTemporales = 0;
        indexLabelsTemporales = 0;
        pilaVariablesTemporales = new Stack<String>();
        pilaLabelsTemporales = new Stack<String>();
        pilaCodigo = new Stack<String>();
        funciones = new HashMap<String,String>();
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
        // System.out.println("------------    LISTA DECLARACION    ------------");
        imprimirCodigo();
        pilaCodigo.pop();
        // System.out.println("pilaCodigo: " + pilaCodigo);


        return texto;
    }
    
    @Override
    public String visitAsignacion(AsignacionContext ctx) {
        addTextoNodo(ctx, "visitAsignacion");
        pilaCodigo.push(ctx.ID().getText());
        pilaCodigo.push(ctx.IGUAL().getText());
        pilaVariablesTemporales.push(ctx.ID().getText());

        visitAllHijos(ctx);

        // System.out.println("------------    ASIGNACION    ------------");
        imprimirCodigo();
        pilaCodigo.pop();
        // System.out.println("pilaCodigo: " + pilaCodigo);
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

        List<ParseTree> ruleFactors = findRuleNodes(ctx.opal(), compiladoresParser.RULE_factor);
        Boolean param = false;
        for(ParseTree ruleFactor : ruleFactors){
            FactorContext fc = ((FactorContext)ruleFactor);
            if(fc.getChildCount() > 0){
                param = true;
            }
        }
        if(param) {
            visitAllHijos(ctx);

            // System.out.println("------------    VISIT ARGS    ------------");
            // System.out.println("pilaCodigo: " + pilaCodigo);

            LinkedList<String> listaVariables = new LinkedList<String>();

            while(!pilaCodigo.lastElement().equals("="))
                listaVariables.add(pilaCodigo.pop());

            Iterator<String> iterator = listaVariables.descendingIterator();
            if(listaVariables.size() > 0){
                while (iterator.hasNext())
                    System.out.println("push " + iterator.next());
            }
        }
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

        String label = nuevoLabel();
        funciones.put(ctx.ID().getText(), label);

        generadorNombresTemporales();
        pilaCodigo.push("pop");

        // System.out.println("-----    VISIT DECLARACION FUNCION    -----");
        System.out.println("label " + label);
        imprimirCodigo();
        visitAllHijos(ctx);
        System.out.println("jmp " + pilaCodigo.pop());
        return texto;
    }

    @Override
    public String visitParam(ParamContext ctx) {
        addTextoNodo(ctx, "visitParam");
        if (ctx.ID() != null){
            pilaCodigo.push(ctx.ID().getText());
            pilaCodigo.push("=");
            pilaCodigo.push("pop");
            pilaVariablesTemporales.push(ctx.ID().getText());
            visitAllHijos(ctx);
            // System.out.println("------------    VISIT PARAM    ------------");
            imprimirCodigo();
            pilaCodigo.pop();
        }
        return texto;
    }

    @Override
    public String visitListaParams(ListaParamsContext ctx) {
        addTextoNodo(ctx, "visitListaParams");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitRetorno(RetornoContext ctx) {
        addTextoNodo(ctx, "visitRetorno");
        visitAllHijos(ctx);
        // System.out.println("------------    VISIT RETORNO    ------------");
        // System.out.println("pilaCodigo: " + pilaCodigo);
        System.out.println("push " + pilaCodigo.pop());
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
        String labelIn = nuevoLabel();
        String labelOut = nuevoLabel();
        if(ctx.condicionFor().ID() == null)
            visit(ctx.condicionFor().getChild(0));
        System.out.println("label " + labelIn);
        visit(ctx.condicionFor().opal());
        System.out.println("ifnjmp " + pilaCodigo.pop() + ", " + labelOut );
        visit(ctx.getChild(4));
        visit(ctx.condicionFor().getChild(4));
        System.out.println("jmp " + labelIn);
        System.out.println("label " + labelOut);
        return texto;
    }

    @Override
    public String visitIif(IifContext ctx) {
        addTextoNodo(ctx, "visitIif");
        visit(ctx.opal());
        pilaLabelsTemporales.push(nuevoLabel());
        System.out.println("ifnjmp " + pilaCodigo.pop() + ", " + pilaLabelsTemporales.lastElement());
        visit(ctx.getChild(4));
        if(ctx.ielse().getChildCount() > 0){
            String labelExit = nuevoLabel();
            System.out.println("jmp " + labelExit);
            visit(ctx.ielse());
            System.out.println("label " + labelExit);
        }else
            System.out.println("label " + pilaLabelsTemporales.pop());
        return texto;
    }

    @Override
    public String visitIelse(IelseContext ctx) {
        addTextoNodo(ctx, "visitIelse");
        System.out.println("label " + pilaLabelsTemporales.pop());
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
        // System.out.println("------------    VISIT INVOCACION FUNCION    ------------");
        String label = nuevoLabel();
        System.out.println("push " + label);
        String funcion = ctx.ID().getText();
        System.out.println("jmp " + funciones.get(funcion));
        System.out.println("label " + label);
        pilaCodigo.push("pop");
        return texto;
    }

    @Override
    public String visitIwhile(IwhileContext ctx) {
        addTextoNodo(ctx, "visitIwhile");
        String labelIn = nuevoLabel();
        String labelOut = nuevoLabel();
        System.out.println("label " + labelIn);
        visit(ctx.opal());
        System.out.println("ifnjmp " + pilaCodigo.pop() + ", " + labelOut );
        visit(ctx.getChild(4));
        System.out.println("jmp " + labelIn);
        System.out.println("label " + labelOut);
        return texto;
    }

    @Override
    public String visitListaArgs(ListaArgsContext ctx) {
        addTextoNodo(ctx, "visitListaArgs");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitA(AContext ctx) {
        addTextoNodo(ctx, "visitA");
        
        if (ctx.getChildCount() != 0){
            pilaCodigo.push(ctx.getChild(0).getText());

            Boolean hijos = true;
            for (int i = 1; i < ctx.getChildCount(); i++) {
                if (findRuleNodes(ctx.getChild(i), compiladoresParser.RULE_factor).size() == 0)
                    hijos = false;
            }

            if (hijos) {
                generadorNombresTemporales();
                visitAllHijos(ctx);
                // System.out.println("------------    A    ------------");
                imprimirCodigo();
            }
            else
                visitAllHijos(ctx);
        }

        return texto;
    }

    @Override
    public String visitAnd(AndContext ctx) {
        addTextoNodo(ctx, "visitAnd");
        
        Boolean hijos = true;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (findRuleNodes(ctx.getChild(i), compiladoresParser.RULE_factor).size() == 0)
                hijos = false;
        }

        if (hijos) {
            generadorNombresTemporales();
            visitAllHijos(ctx);
            // System.out.println("------------    AND    ------------");
            imprimirCodigo();
        }
        else
            visitAllHijos(ctx);

        return texto;
    }

    @Override
    public String visitC(CContext ctx) {
        addTextoNodo(ctx, "visitC");
        if (ctx.getChildCount() != 0){
            pilaCodigo.push(ctx.getChild(0).getText());
            visitAllHijos(ctx);
        }
        return texto;
    }

    @Override
    public String visitComp(CompContext ctx) {
        addTextoNodo(ctx, "visitComp");

        Boolean hijos = true;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (findRuleNodes(ctx.getChild(i), compiladoresParser.RULE_factor).size() == 0)
                hijos = false;
        }

        if (hijos) {
            generadorNombresTemporales();
            visitAllHijos(ctx);
            // System.out.println("------------    COMP    ------------");
            imprimirCodigo();
        }
        else
            visitAllHijos(ctx);

        return texto;
    }

    @Override
    public String visitExp(ExpContext ctx) {
        addTextoNodo(ctx, "visitExp");

        if (ctx.getChildCount() != 0){
            pilaCodigo.push(ctx.getChild(0).getText());

            Boolean hijos = true;
            for (int i = 1; i < ctx.getChildCount(); i++) {
                if (findRuleNodes(ctx.getChild(i), compiladoresParser.RULE_factor).size() == 0)
                    hijos = false;
            }

            if (hijos) {
                generadorNombresTemporales();
                visitAllHijos(ctx);
                // System.out.println("------------    EXP    ------------");
                imprimirCodigo();
            }
            else
                visitAllHijos(ctx);
        }

        return texto;
    }

    @Override
    public String visitExpresion(ExpresionContext ctx) {
        addTextoNodo(ctx, "visitExpresion");

        Boolean hijos = true;
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (findRuleNodes(ctx.getChild(i), compiladoresParser.RULE_factor).size() == 0)
                hijos = false;
        }

        if (hijos) {
            generadorNombresTemporales();
            visitAllHijos(ctx);
            // System.out.println("------------    EXPRESION    ------------");
            imprimirCodigo();
        }
        else
            visitAllHijos(ctx);

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

            Boolean hijos = true;
            for (int i = 1; i < ctx.getChildCount(); i++) {
                if (findRuleNodes(ctx.getChild(i), compiladoresParser.RULE_factor).size() == 0)
                    hijos = false;
            }

            if (hijos) {
                generadorNombresTemporales();
                visitAllHijos(ctx);
                // System.out.println("------------    O    ------------");
                imprimirCodigo();
            }
            else
                visitAllHijos(ctx);
        }

        return texto;
    }

    @Override
    public String visitOpal(OpalContext ctx) {
        addTextoNodo(ctx, "visitOpal");
        visitAllHijos(ctx);
        return texto;
    }

    @Override
    public String visitTerm(TermContext ctx) {
        addTextoNodo(ctx, "visitTerm");

        /* List<ParseTree> ruleFactors = findRuleNodes(ctx, compiladoresParser.RULE_factor); */

        /* if (ruleFactors.size() > 1) {
            generadorNombresTemporales();
            visitAllHijos(ctx);
            // System.out.println("------------    TERM    ------------");
            imprimirCodigo();
        }
        else
            visitAllHijos(ctx); */

        if (ctx.t().getChildCount() > 0) {
            generadorNombresTemporales();
            visitAllHijos(ctx);
            // System.out.println("------------    TERM    ------------");
            imprimirCodigo();
        }
        else
            visitAllHijos(ctx);

        return texto;
    }

    @Override
    public String visitT(TContext ctx) {
        addTextoNodo(ctx, "visitT");

        if (ctx.getChildCount() != 0){
            pilaCodigo.push(ctx.getChild(0).getText());

            if (ctx.t().getChildCount() > 0) {
                generadorNombresTemporales();
                visitAllHijos(ctx);
                // System.out.println("------------    T    ------------");
                imprimirCodigo();
            }
            else
                visitAllHijos(ctx);
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

    /**
     * Obtengo un subarbol en formato ArrayList segun la regla que especifiquemos
     */
    private List<ParseTree> findRuleNodes(ParseTree ctx, int ruleIndex) {
        return new ArrayList<ParseTree>(Trees.findAllRuleNodes(ctx, ruleIndex));
    }

    private void generadorNombresTemporales() {
        String temporal = "t" + indexVariablesTemporales;
        indexVariablesTemporales++;
        pilaVariablesTemporales.push(temporal);
        pilaCodigo.push(temporal);
        pilaCodigo.push("=");
    }

    private String nuevoLabel() {
        return "l" + indexLabelsTemporales++;
    }

    // Tomamos la ultima variable temporal y la comparamos contra la
    // ultima variable de la pila de codigo, hasta que matcheen entonces
    // imprimimos el codigo por pantalla.
    private void imprimirCodigo() {
        /* System.out.println("pilaCodigo: " + pilaCodigo);
        System.out.println("pilaVariablesTemporales: " + pilaVariablesTemporales); */

        String varTemp = pilaVariablesTemporales.pop();
        /* System.out.println("varTemp: " + varTemp); */
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