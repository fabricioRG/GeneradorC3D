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
    
    public String getCategoriaVariable(){
        return VARIABLE;
    }
    
    public String getCategoriaNumero(){
        return NUMERO;
    }
    
    public String getCategoriaTexto(){
        return TEXTO;
    }
    
    public String getCategoriaBool(){
        return BOOL;
    }
    
    public int getAmbitoGlobal(){
        return AMBITO_GLOBAL;
    }
    
    public int getAmbitoLocal(){
        return AMBITO_LOCAL;
    }
    
    public boolean isAmbitoLocal(int ambito){
        return ambito == AMBITO_LOCAL;
    }
    
    public boolean isAmbitoGlobal(int ambito){
        return ambito == AMBITO_GLOBAL;
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
    
    public boolean isNumero(String categoria){
        return categoria.equals(NUMERO);
    }
    
    public boolean isTexto(String categoria){
        return categoria.equals(TEXTO);
    }
    
    public boolean isBool(String categoria){
        return categoria.equals(BOOL);
    }
    
    public boolean isValor(String categoria){
        return isTexto(categoria) || isNumero(categoria) || isBool(categoria);
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

    public void setSubprogram(Tipo tipo, Simbolo var) {
        Simbolo simbolo = new SimboloBuilder().lexema(var.getLexema()).fila(var.getFila()).columna(var.getColumna()).categoria(SUBPROGRAMA).
                ambito(AMBITO_GLOBAL).token(tipo).parametros(new LinkedList<>()).estructura(new LinkedList<>()).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }

    public void setLocalVariable(Tipo tipo, Simbolo var, String subprograma) {
        String nombreVar = getLocalName(var.getLexema(), subprograma);
        Simbolo simbolo = new SimboloBuilder().token(tipo).lexema(nombreVar).fila(var.getFila()).columna(var.getColumna()).categoria(VARIABLE).
                ambito(AMBITO_LOCAL).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }

    public void setLocalArray(Tipo tipo, Simbolo var, LinkedList<Cuarteto> dimension, String subprograma) {
        String nombreVar = getLocalName(var.getLexema(), subprograma);
        Simbolo simbolo = new SimboloBuilder().token(tipo).lexema(nombreVar).fila(var.getFila()).columna(var.getColumna()).categoria(ARREGLO).
                ambito(AMBITO_LOCAL).noDimensiones(dimension.size()).dimensiones(dimension).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }

    public void setParametro(Tipo tipo, Simbolo var, String subprograma) {
        String nombreVar = getLocalName(var.getLexema(), subprograma);
        Simbolo simbolo = new SimboloBuilder().token(tipo).lexema(nombreVar).fila(var.getFila()).columna(var.getColumna()).categoria(PARAMETRO).
                ambito(AMBITO_LOCAL).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }

    public void setGlobalVariable(Tipo tipo, Simbolo var) {
        String nombreVar = getGlobalName(var.getLexema());
        Simbolo simbolo = new SimboloBuilder().token(tipo).fila(var.getFila()).columna(var.getColumna()).lexema(nombreVar).categoria(VARIABLE).
                ambito(AMBITO_GLOBAL).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }

    public void setGlobalArray(Tipo tipo, Simbolo var, LinkedList<Cuarteto> dimension) {
        String nombreVar = getGlobalName(var.getLexema());
        Simbolo simbolo = new SimboloBuilder().token(tipo).fila(var.getFila()).columna(var.getColumna()).lexema(nombreVar).categoria(ARREGLO).
                ambito(AMBITO_GLOBAL).noDimensiones(dimension.size()).dimensiones(dimension).build();
        simbolos.put(simbolo.getLexema(), simbolo);
    }

    public void addSubprogramParametro(Tipo tipo, String name) {
        Simbolo subprogram = getSubprogram(name);
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
    public final static String NUMERO = "numero";
    public final static String TEXTO = "texto";
    public final static String BOOL = "bool";
    public final static String GLOBAL_VAR = "$";
}
