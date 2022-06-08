package compiladores;

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
