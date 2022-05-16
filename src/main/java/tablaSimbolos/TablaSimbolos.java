package tablaSimbolos;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class TablaSimbolos {
    List< Map<String,ID> > simbolos;

    public TablaSimbolos() {
        this.simbolos = new LinkedList<Map<String,ID>>();
    }

    public List<Map<String, ID>> getSimbolos() {
        return simbolos;
    }

    public void setSimbolos(List<Map<String, ID>> simbolos) {
        this.simbolos = simbolos;
    }

    public void addID (ID id) {

    };

    public ID searchLocalID (String id) {
        return new Variable(id, new TipoDato("int"), '=');
    };

    public ID searchID (String id){
        return new Variable(id, new TipoDato("double"), ';');
    };

    public void addContext (){
        simbolos.add(new TreeMap<String,ID>());
    };

    public void deleteContext (){
        simbolos.remove(simbolos.size()-1);
    };
}
