package codigo3d.backend.objetos;

import java.util.HashMap;

/**
 * 
 * @author fabricioRG
 */
public class TablaSimbolos {

    private HashMap <String,Simbolo> simbolos = null;

    public TablaSimbolos() {
        this.simbolos = new HashMap<>();
    }
    
    public void setSimbol(String lexema, Tipo token){
        Simbolo simbolo = new SimboloBuilder().lexema(lexema).token(token).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }
    
    public boolean existSimbol(String simbol){
        return simbolos.containsKey(simbol);
    }
    
    public Simbolo getSimbol(String simbol){
        return simbolos.get(simbol);
    }
    
}
