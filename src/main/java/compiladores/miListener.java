package compiladores;

import java.util.Iterator;
import java.util.LinkedList;

import org.antlr.v4.runtime.ParserRuleContext;

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

public class miListener extends compiladoresBaseListener {
    private String[] nombres;
    private Integer bloque = 1;
    private Integer count = 0;
    private TablaSimbolos tablaSimbolos = new TablaSimbolos();
    private LinkedList<ID> paramsFuncion = new LinkedList<ID>();
    private Boolean error;

    miListener(compiladoresParser parser){
        nombres = parser.getRuleNames();
        error = false;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    @Override
    public void exitDeclaracion(DeclaracionContext ctx) {
        if(this.tablaSimbolos.searchID(ctx.ID().getText()) != null){
            setError(true);
            System.out.println("\t\tERROR SEMANTICO: Doble declaración del mismo identificador -- ID: " + ctx.ID());
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
    }

    @Override
    public void exitAsignacion(AsignacionContext ctx) {
        ID temp = this.tablaSimbolos.searchID(ctx.ID().getText());
        if(temp == null){
            setError(true);
            System.out.println("\t\tERROR SEMANTICO: Uso de un identificador no declarado -- ID: " + ctx.ID());
            return;
        }
        temp.setInicializada(true);
    }
  
    @Override
    public void exitDeclaracionFuncion(DeclaracionFuncionContext ctx) {
        if(this.tablaSimbolos.searchID(ctx.ID().getText()) != null){
            setError(true);
            System.out.println("\t\tERROR SEMANTICO: Doble declaración del mismo identificador -- ID: " + ctx.ID());
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
        paramsFuncion.clear();
    }

    @Override
    public void exitInvocacionFuncion(InvocacionFuncionContext ctx) {
        ID temp = this.tablaSimbolos.searchID(ctx.ID().getText());
        if(temp == null){
            setError(true);
            System.out.println("\t\tERROR SEMANTICO: Uso de un identificador no declarado -- ID: " + ctx.ID());
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
            setError(true);
            System.out.println("\t\tERROR SEMANTICO: Uso de un identificador no declarado -- ID: " + ctx.ID());
            return;
        }
        else {
            if(!temp.getInicializada()) {
                setError(true);
                System.out.println("\t\tERROR SEMANTICO: Uso de un identificador no inicializado -- ID: " + ctx.ID());
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
                setError(true);
                System.out.println("\t\tERROR SEMANTICO: Uso de un identificador no declarado -- ID: " + ctx.ID());
                return;
            }
            if(!temp.getInicializada()) {
                setError(true);
                System.out.println("\t\tERROR SEMANTICO: Uso de un identificador no inicializado -- ID: " + ctx.ID());
                return;
            }
            temp.setUsada(true);
        }
    }

    @Override
    public void enterBloque(BloqueContext ctx) {
        bloque++;
        System.out.println("\t\tINICIO DEL CONTEXTO");
        tablaSimbolos.addContext();
        if(paramsFuncion.size() > 0){
            for (ID id : paramsFuncion) {
                tablaSimbolos.addID(id);
            }
        }
    }

    @Override
    public void exitBloque(BloqueContext ctx) {
        int posicionUltimoContexto = tablaSimbolos.getSimbolos().size()-1;
        for (var entry : tablaSimbolos.getSimbolos().get(posicionUltimoContexto).entrySet()) {
            if(!entry.getValue().getUsada()) {
                setError(true);
                System.out.println("\t\tERROR SEMANTICO: Identificador declarado pero no usado");
                System.out.print("\t");
            }
            System.out.println("\t\t" + entry.getKey() + " --- " + entry.getValue());
        }
        System.out.println("\t\tFIN DEL CONTEXTO");
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
        System.out.println("\tINICIO DEL CONTEXTO GENERAL");
    }

    @Override
    public void exitSi(SiContext ctx) {
        int posicionUltimoContexto = tablaSimbolos.getSimbolos().size()-1;
        for (var entry : tablaSimbolos.getSimbolos().get(posicionUltimoContexto).entrySet()) {
            if(!entry.getValue().getUsada()) {
                setError(true);
                System.out.println("\t\tERROR SEMANTICO: Identificador declarado pero no usado");
                System.out.print("\t");
            }
            System.out.println("\t\t" + entry.getKey() + " --- " + entry.getValue());
        }
        tablaSimbolos.deleteContext();
        System.out.println("\tFIN DEL CONTEXTO GENERAL");
        System.out.println("FIN DEL PARSEO");
        System.out.println("Visitamos " + bloque + " contextos");
        System.out.println("Visitamos " + count + " nodos");
    }
    
}
