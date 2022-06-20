package compiladores;

import java.util.Iterator;
import java.util.LinkedList;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import compiladores.compiladoresParser.AsignacionContext;
import compiladores.compiladoresParser.BloqueContext;
import compiladores.compiladoresParser.CondicionForContext;
import compiladores.compiladoresParser.DeclaracionContext;
import compiladores.compiladoresParser.DeclaracionFuncionContext;
import compiladores.compiladoresParser.FactorContext;
import compiladores.compiladoresParser.InvocacionFuncionContext;
import compiladores.compiladoresParser.ListaDeclaracionContext;
import compiladores.compiladoresParser.ParamContext;
import compiladores.compiladoresParser.SiContext;
import tablaSimbolos.Funcion;
import tablaSimbolos.ID;
import tablaSimbolos.TablaSimbolos;
import tablaSimbolos.TipoDato;
import tablaSimbolos.Variable;

/* 
 * 05/06/2021
 * TENIAMOS QUE CAMBIAR DE ESTO
 * 
 * if (lista.getChild(0).getText() == "=")) {
 * A ESTO
 * if (lista.getChild(0).getText().equals("=")) {
 * 
 * Había un problemas, cuando inicializamos una variable con una asignacion "int x = 0"
 * y despues seguimos con una definicion "int x = 0, k"
 * lo que pasaba es que se volvía a agregar la variable x pero con inicializacion = false
 * por eso agregamos if(!lista.getParent().getChild(0).getText().equals("=")) {
 * Si usamos el ejemplo anterior y vamos desglosando las reglas:
 * Paso 1: int x listaDeclaracion -- tipo = int, id = x
 * Paso 2: = 0 listaDeclaracion -- Como el primer hijo es "=", guardamos la variable x inicializada a la tabla de simbolos
 * Paso 3: , k listaDeclaracion -- ANTES DEL CAMBIO -- Como el primer hijo es ",", guardamos la variable x no inicializada a la tabla de simbolos
 *                              -- DESPUES DEL CAMBIO -- Como el primer hijo del padre es "=", no se guarda nada
 * 
 * Había un problema, si la declaración es de varias variables "int x, y", la última no se guardaba
 * por eso se agrega if(lista.listaDeclaracion().getChildCount() == 0) {
 * Si usamos el ejemplo anterior y vamos desglosando las reglas:
 * Paso 1: int x listaDeclaracion -- Tipo = int, id = x
 * Paso 2: , y listaDeclaracion -- ANTES DEL CAMBIO -- guardamos la variable x no inicializada a la tabla de simbolos
 *                              -- DESPUES DEL CAMBIO -- Como el primer hijo es ",", guardamos la variable x no inicializada a la tabla de simbolos
 *                                                    -- Como no tiene hijos, guardamos la variable y no inicializada a la tabla de simbolos
 */

public class miListener extends compiladoresBaseListener {
    private String[] nombres;
    private Integer bloque = 1;
    private Integer count = 0;
    private TablaSimbolos tablaSimbolos = new TablaSimbolos();
    private LinkedList<ID> paramsFuncion = new LinkedList<ID>();

    miListener(compiladoresParser parser){
        nombres = parser.getRuleNames();
    }

    @Override
    public void exitDeclaracion(DeclaracionContext ctx) {
        if(this.tablaSimbolos.searchID(ctx.ID().getText()) != null){
            System.out.println("ERROR SEMANTICO: Doble declaración del mismo identificador -- ID: " + ctx.ID());
            return;
        }

        String tipoDato = ctx.tipoDato().getText();
        String ID = ctx.ID().getText();

        ListaDeclaracionContext lista = ctx.listaDeclaracion();

        // Para el caso de int x donde no hay listaDeclaracion
        if(lista.getChildCount() == 0) {
            Variable id = new Variable(ID, tipoDato, false);
            tablaSimbolos.addID(id);
        }

        // Para una declaracion de varias variables
        while (lista.getChildCount() != 0) {
            if (lista.getChild(0).getText().equals("=")) {
                Variable id = new Variable(ID, tipoDato, true);
                tablaSimbolos.addID(id);
            }
            if (lista.getChild(0).getText().equals(",")) {
                if(!lista.getParent().getChild(0).getText().equals("=")) {
                    Variable id = new Variable(ID, tipoDato, false);
                    tablaSimbolos.addID(id);
                }
                ID = lista.ID().getText();
                if(lista.listaDeclaracion().getChildCount() == 0) {
                    Variable ultimoId = new Variable(ID, tipoDato, false);
                    tablaSimbolos.addID(ultimoId);
                }
            }
            lista = lista.listaDeclaracion();
        }
        //if (nombres[ctx.getRuleIndex()] == "declaracion");
    }

    @Override
    public void exitAsignacion(AsignacionContext ctx) {
        ID temp = this.tablaSimbolos.searchID(ctx.ID().getText());
        if(temp == null){
            System.out.println("ERROR SEMANTICO: Uso de un identificador no declarado -- ID: " + ctx.ID());
            return;
        }
        temp.setInicializada(true);
    }
  
    @Override
    public void exitDeclaracionFuncion(DeclaracionFuncionContext ctx) {
        if(this.tablaSimbolos.searchID(ctx.ID().getText()) != null){
            System.out.println("ERROR SEMANTICO: Doble declaración del mismo identificador -- ID: " + ctx.ID());
            return;
        }

        LinkedList<TipoDato> list = new LinkedList<>();
        // RECORREMOS LA LISTA AL REVES, PORQUE EN exitParam LA PRIMER VARIABLE QUE INGRESA
        // A LA LISTA paramsFuncion ES LA ULTIMA DE LA LISTA DE PARAMETROS DE LA FUNCION
        Iterator<ID> iterator = paramsFuncion.descendingIterator();
        if(paramsFuncion.size() > 0){
            while (iterator.hasNext())
            {
                TipoDato tipoDato = new TipoDato(iterator.next().getTipo());
                list.add(tipoDato);
            }
        }
        Funcion funcion = new Funcion(list, ctx.ID().getText(), ctx.tipoDato().getText(), '{');
        tablaSimbolos.addID(funcion);
    }

    @Override
    public void exitInvocacionFuncion(InvocacionFuncionContext ctx) {
        ID temp = this.tablaSimbolos.searchID(ctx.ID().getText());
        if(temp == null){
            System.out.println("ERROR SEMANTICO: Uso de un identificador no declarado -- ID: " + ctx.ID());
        }else{
            temp.setUsada(true);
        }
    }

    @Override
    public void exitFactor(FactorContext ctx) {
        if(ctx.ID() == null){
            return;
        }
        ID temp = this.tablaSimbolos.searchID(ctx.ID().getText());
        if(temp == null) {
            System.out.println("ERROR SEMANTICO: Uso de un identificador no declarado -- ID: " + ctx.ID());
            return;
        }
        else {
            if(!temp.getInicializada()) {
                System.out.println("ERROR SEMANTICO: Uso de un identificador no inicializado -- ID: " + ctx.ID());
                return;
            }
            temp.setUsada(true);
        }
    }

    @Override
    public void exitParam(ParamContext ctx) {
        if(ctx.getChildCount() != 0){
            Variable id = new Variable(ctx.ID().getText(), ctx.tipoDato().getText(), true);
            this.paramsFuncion.add(id);
        }
    }
    
    @Override
    public void exitCondicionFor(CondicionForContext ctx) {
        if (ctx.ID() != null) {
            ID temp = this.tablaSimbolos.searchID(ctx.ID().getText());
            if(temp == null) {
                System.out.println("ERROR SEMANTICO: Uso de un identificador no declarado -- ID: " + ctx.ID());
                return;
            }
            if(!temp.getInicializada()) {
                System.out.println("ERROR SEMANTICO: Uso de un identificador no inicializado -- ID: " + ctx.ID());
                return;
            }
            temp.setUsada(true);
        }
    }

    @Override
    public void enterBloque(BloqueContext ctx) {
        bloque++;
        tablaSimbolos.addContext();
        if(paramsFuncion.size() > 0){
            for (ID id : paramsFuncion) {
                tablaSimbolos.addID(id);
            }
        }
        paramsFuncion.clear();
    }

    @Override
    public void exitBloque(BloqueContext ctx) {
        int posicionUltimoContexto = tablaSimbolos.getSimbolos().size()-1;
        for (var entry : tablaSimbolos.getSimbolos().get(posicionUltimoContexto).entrySet()) {
            if(!entry.getValue().getUsada()) {
                System.out.println("ERROR SEMANTICO: Identificador declarado pero no usado");
                System.out.print("\t");
            }
            System.out.println(entry.getKey() + " --- " + entry.getValue());
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
        int posicionUltimoContexto = tablaSimbolos.getSimbolos().size()-1;
        for (var entry : tablaSimbolos.getSimbolos().get(posicionUltimoContexto).entrySet()) {
            if(!entry.getValue().getUsada()) {
                System.out.println("ERROR SEMANTICO: Identificador declarado pero no usado");
                System.out.print("\t");
            }
            System.out.println(entry.getKey() + " --- " + entry.getValue());
        }
        tablaSimbolos.deleteContext();
        System.out.println("FIN DEL PARSEO");
        System.out.println("Visitamos " + bloque + " contextos");
        System.out.println("Visitamos " + count + " nodos");
        System.out.println("CANTIDAD DE CONTEXTOS: " + tablaSimbolos.getSimbolos().size());
    }
    
}
