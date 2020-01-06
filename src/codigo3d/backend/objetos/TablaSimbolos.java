package codigo3d.backend.objetos;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author fabricioRG
 */
public class TablaSimbolos {

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

    public Simbolo getLocalVariable(String var, String subprograma) {
        return getSimbol(getLocalName(var, subprograma));
    }

    public Simbolo getLocalArray(String var, String subprograma) {
        return getSimbol(getLocalName(var, subprograma));
    }

    public Simbolo getGlobalVariable(String var) {
        return getSimbol(getGlobalName(var));
    }

    public Simbolo getGlobalArray(String var) {
        return getSimbol(getGlobalName(var));
    }

    public Simbolo getSubprogram(String var) {
        return getSimbol(var);
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

    public boolean isArreglo(String categoria) {
        return categoria.equals(ARREGLO);
    }

    public boolean existSubprogram(String simbol) {
        if (existSimbol(simbol)) {
            return isSubprogram(getSimbol(simbol).getCategoria());
        }
        return false;
    }

    public boolean existGlobalVariable(String var) {
        String variable = getGlobalName(var);
        if (existSimbol(variable)) {
            return isVariable(getSimbol(variable).getCategoria());
        }
        return false;
    }

    public boolean existLocalVariableOrParametro(String var, String subprograma) {
        String nombreVar = getLocalName(var, subprograma);
        if (existSimbol(nombreVar)) {
            return isVariable(getSimbol(nombreVar).getCategoria()) || isParametro(getSimbol(nombreVar).getCategoria());
        }
        return false;
    }

    public boolean existLocalArray(String var, String subprograma) {
        String nombreVar = getLocalName(var, subprograma);
        if (existSimbol(nombreVar)) {
            return isArreglo(getSimbol(nombreVar).getCategoria());
        }
        return false;
    }

    public boolean existGlobalArray(String var) {
        String variable = getGlobalName(var);
        if (existSimbol(variable)) {
            return isArreglo(getSimbol(variable).getCategoria());
        }
        return false;
    }

    public boolean existLocalVariableParametroOrArray(String var, String subprograma) {
        return existLocalVariableOrParametro(var, subprograma) || existLocalArray(var, subprograma);
    }

    public boolean existGlobalVariableOrArray(String var) {
        return existGlobalVariable(var) || existGlobalArray(var);
    }

    public void setSubprogram(Tipo tipo, String simbol) {
        Simbolo simbolo = new SimboloBuilder().lexema(simbol).categoria(SUBPROGRAMA).
                ambito(AMBITO_LOCAL).token(tipo).parametros(new LinkedList<>()).estructura(new LinkedList<>()).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }

    public void setLocalVariable(Tipo tipo, String var, String subprograma) {
        String nombreVar = getLocalName(var, subprograma);
        Simbolo simbolo = new SimboloBuilder().token(tipo).lexema(nombreVar).categoria(VARIABLE).
                ambito(AMBITO_LOCAL).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }

    public void setLocalArray(Tipo tipo, String var, LinkedList<Cuarteto> dimension, String subprograma) {
        String nombreVar = getLocalName(var, subprograma);
        Simbolo simbolo = new SimboloBuilder().token(tipo).lexema(nombreVar).categoria(ARREGLO).
                ambito(AMBITO_LOCAL).noDimensiones(dimension.size()).dimensiones(dimension).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }

    public void setParametro(Tipo tipo, String var, String subprograma) {
        String nombreVar = getLocalName(var, subprograma);
        Simbolo simbolo = new SimboloBuilder().token(tipo).lexema(nombreVar).categoria(PARAMETRO).
                ambito(AMBITO_LOCAL).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }

    public void setGlobalVariable(Tipo tipo, String var) {
        String nombreVar = getGlobalName(var);
        Simbolo simbolo = new SimboloBuilder().token(tipo).lexema(nombreVar).categoria(VARIABLE).
                ambito(AMBITO_GLOBAL).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }

    public void setGlobalArray(Tipo tipo, String var, LinkedList<Cuarteto> dimension) {
        String nombreVar = getGlobalName(var);
        Simbolo simbolo = new SimboloBuilder().token(tipo).lexema(nombreVar).categoria(ARREGLO).
                ambito(AMBITO_GLOBAL).noDimensiones(dimension.size()).dimensiones(dimension).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }

    public void addSubprogramParametro(Tipo tipo, String name) {
        Simbolo subprogram = getSimbol(name);
        subprogram.getParametros().add(tipo);
        subprogram.setNoParametros(subprogram.getParametros().size());
    }

    public void addSubprogramEstructura(Cuarteto instruccion, String name) {
        Simbolo subprogram = getSimbol(name);
        subprogram.getEstructura().add(instruccion);
    }

    public void setSubprogramBooleanValorReturn(boolean valorReturn, String name) {
        Simbolo sim = getSubprogram(name);
        sim.setValorRetorno(valorReturn);
    }

    private String getLocalName(String var, String subprograma) {
        return var + subprograma;
    }

    private String getGlobalName(String var) {
        return GLOBAL_VAR + var;
    }

    public final static int AMBITO_LOCAL = 1;
    public final static int AMBITO_GLOBAL = 0;
    public final static String SUBPROGRAMA = "subprograma";
    public final static String VARIABLE = "variable";
    public final static String PARAMETRO = "parametro";
    public final static String ARREGLO = "arreglo";
    public final static String GLOBAL_VAR = "$";
}
