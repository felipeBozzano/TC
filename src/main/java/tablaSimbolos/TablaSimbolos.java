package tablaSimbolos;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;

public class TablaSimbolos {
    List< Map<String,ID> > simbolos;


    public TablaSimbolos() {
        this.simbolos = new LinkedList<Map<String,ID>>();
    }

    void addID (ID id) {

    };

    ID searchLocalID (String id) {
        return new Variable(id, new TipoDato("int"), '=');
    };

    ID searchID (String id){
        return new Variable(id, new TipoDato("double"), ';');
    };

    void addContext (){

    };

    void deleteContext (){

    };
}
