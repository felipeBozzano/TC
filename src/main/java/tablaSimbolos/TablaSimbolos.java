package tablaSimbolos;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

public class TablaSimbolos {
    List< Map<String,ID> > simbolos;

    public TablaSimbolos() {
        this.simbolos = new LinkedList<Map<String,ID>>();
        this.addContext();
    }

    public List<Map<String, ID>> getSimbolos() {
        return simbolos;
    }

    public void setSimbolos(List<Map<String, ID>> simbolos) {
        this.simbolos = simbolos;
    }

    public void addID (ID id) {
        this.simbolos.get(this.simbolos.size()-1).put(id.id, id);
    };

    public ID searchLocalID (String id) {
        return new Variable(id, "int", true);
    };

    public ID searchID (String id){
        return new Variable(id, "double", true);
    };

    public void addContext (){
        simbolos.add(new HashMap<String,ID>());
    };

    public void deleteContext (){
        simbolos.remove(simbolos.size()-1);
    };
}
