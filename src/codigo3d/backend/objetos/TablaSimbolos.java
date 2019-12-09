package codigo3d.backend.objetos;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author fabricioRG
 */
public class TablaSimbolos {

    public final static String SUBPROGRAMA = "subprograma";
    public final static String VARIABLE = "variable";
    public final static String PARAMETRO = "parametro";
    public final static String GLOBAL_VAR = "$";
    public final static int AMBITO_GLOBAL = 0;
    public final static int AMBITO_LOCAL = 1;

    private HashMap<String, Simbolo> simbolos = null;

    public TablaSimbolos() {
        this.simbolos = new HashMap<>();
    }

    public void setSimbol(String lexema, Tipo token) {
        Simbolo simbolo = new SimboloBuilder().lexema(lexema).token(token).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }

    public boolean existSimbol(String simbol) {
        return simbolos.containsKey(simbol);
    }

    public Simbolo getSimbol(String simbol) {
        return simbolos.get(simbol);
    }

    public boolean isSubprogram(String categoria) {
        return categoria.equals(SUBPROGRAMA);
    }

    public boolean isVariable(String categoria) {
        return categoria.equals(VARIABLE);
    }

    public boolean isParametro(String categoria) {
        return categoria.equals(PARAMETRO);
    }

    public boolean existSubprogram(String simbol) {
        if (existSimbol(simbol)) {
            return isSubprogram(getSimbol(simbol).getCategoria());
        }
        return false;
    }
    
    public boolean existGlobalVariable(String var){
        String variable = GLOBAL_VAR + var;
        if(existSimbol(variable)){
            return isVariable(getSimbol(variable).getCategoria());
        }
        return false;
    }

    public boolean existLocalVariableOrParametro(String var, String subprograma){
        String nombreVar = var + subprograma;
        if(existSimbol(nombreVar)){
            return isVariable(getSimbol(nombreVar).getCategoria()) || isParametro(getSimbol(nombreVar).getCategoria());
        }
        return false;
    }
    
    public void setSubprogram(Tipo tipo, String simbol) {
        Simbolo simbolo = new SimboloBuilder().lexema(simbol).categoria(SUBPROGRAMA).
                ambito(AMBITO_LOCAL).token(tipo).parametros(new LinkedList<>()).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }
    
    public void setLocalVariable(Tipo tipo, String var, String subprograma) {
        String nombreVar = var + subprograma;
        Simbolo simbolo = new SimboloBuilder().token(tipo).lexema(nombreVar).categoria(VARIABLE).
                ambito(AMBITO_LOCAL).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }

    public void setParametro(Tipo tipo, String var, String subprograma) {
        String nombreVar = var + subprograma;
        Simbolo simbolo = new SimboloBuilder().token(tipo).lexema(nombreVar).categoria(PARAMETRO).
                ambito(AMBITO_LOCAL).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }

    public void setGlobalVariable(Tipo tipo, String var) {
        String nombreVar = GLOBAL_VAR + var;
        Simbolo simbolo = new SimboloBuilder().token(tipo).lexema(nombreVar).categoria(VARIABLE).
                ambito(AMBITO_GLOBAL).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }
    
    public void addSubprogramParametro(Tipo tipo, String name){
        Simbolo subprogram = getSimbol(name);
        subprogram.getParametros().add(tipo);
        subprogram.setNoParametros(subprogram.getParametros().size());
    }

}
