package tablaSimbolos;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class TablaSimbolos {
    LinkedList< Map<String,ID> > simbolos;

    public TablaSimbolos() {
        this.simbolos = new LinkedList<Map<String,ID>>();
        this.addContext();
    }

    public List<Map<String, ID>> getSimbolos() {
        return simbolos;
    }

    public void setSimbolos(LinkedList<Map<String, ID>> simbolos) {
        this.simbolos = simbolos;
    }

    public void addID (ID id) {
        this.simbolos.get(this.simbolos.size()-1).put(id.id, id);
    };

    public ID searchLocalID (String id, Map<String, ID> map) {
        return map.get(id);
    };

    public ID searchID (String id){
        Iterator<Map<String, ID>> iterator = this.simbolos.descendingIterator();
        while (iterator.hasNext()) 
        {
            Map<String, ID> temp = iterator.next();
            if(this.searchLocalID(id, temp) != null){
                return this.searchLocalID(id, temp);
            }
        }
        return null;
    };

    public void addContext (){
        simbolos.add(new HashMap<String,ID>());
    };

    public void deleteContext (){
        simbolos.remove(simbolos.size()-1);
    };
}
