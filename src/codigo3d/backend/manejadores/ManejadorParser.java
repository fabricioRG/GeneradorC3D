/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo3d.backend.manejadores;

import codigo3d.backend.objetos.*;
import codigo3d.backend.parseractions.*;
import java.util.LinkedList;
import java.util.Stack;
import java_cup.runtime.Symbol;
import org.jdesktop.el.impl.parser.Token;

/**
 *
 * @author fabricioRG
 */
public class ManejadorParser {

    public ManejadorParser(ManejadorAreaTexto mi) {
        this.manejadorAreaTexto = mi;
        this.tablaSimbolos = new TablaSimbolos();
        //setEtiquetaResult(getSimpleEtiqueta());
        /*this.declarador = new DeclaradorValores(this);
        this.asignador = new AsignadorValores(this);
        this.retornador = new RetornadorValores(this);
        this.operador = new ProcesadorNumerico(this);
        this.procesadorFunciones = new ProcesadorFunciones(this);*/
        //this.acciones = new AccionParser();
    }

    // --------------------ANALIZADOR NO 1------------------------ //
    public void setSubprogramDecl(Tipo tipo, String var, int fila, int columna) throws Exception {
        if (!existSubprogram(var)) {
            tablaSimbolos.setSubprogram(tipo, var);
            subprogramaActual = var;
        } else {
            throw new Exception("Declaracion de Subprograma" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().lexema(var).fila(fila).token(tipo).columna(columna).build())
                    + SALTO_LN + "Subprograma ya declarado");
        }
    }

    public void setMainDecl(String var) {
        tablaSimbolos.setSubprogram(TablaTipos.getInstance().getVoid(), var);
        subprogramaActual = var;
    }

    public void setParametroDecl(Tipo tipo, String name, int fila, int columna) throws Exception {
        if (!TablaTipos.getInstance().isVoid(tipo)) {
            if (!existLocalVariableOrParametro(name)) {
                tablaSimbolos.setParametro(tipo, name, subprogramaActual);
                tablaSimbolos.addSubprogramParametro(tipo, subprogramaActual);
            } else {
                throw new Exception("Declaracion de Parametro" + SALTO_LN + getValorInfo(new SimboloBuilder().lexema(name).
                        fila(fila).token(tipo).columna(columna).build())
                        + "Variable o Parametro ya declarada en subprograma: " + subprogramaActual);
            }
        } else {
            throw new Exception("Declaracion de Parametro" + SALTO_LN + getInvalidAsignVoid(new SimboloBuilder().lexema(name).
                    fila(fila).token(tipo).columna(columna).build()));
        }
    }

    public void setVariableDecl(Cuarteto cuarteto, Tipo tipo) throws Exception {
        Cuarteto k = cuarteto;
        while (k != null) {
            asignDecl(k, tipo);
            k = k.getSiguiente();
        }
    }

    public void asignDecl(Cuarteto cuarteto, Tipo tipo) throws Exception {
        if (!TablaTipos.getInstance().isVoid(tipo)) {
            if (!isGlobal()) {
                if (!existLocalVariableParametroOrArray(cuarteto.getOperador())) {
                    if (cuarteto.getComponentes() != null) {
                        if (TablaTipos.getInstance().isCompatible(cuarteto.getComponentes().getResultado().getToken(), tipo)) {
                            getTablaSimbolos().setLocalVariable(tipo, cuarteto.getOperador(), getSubprogramaActual());
                        } else {
                            cuarteto.getOperando1().setToken(tipo);
                            throw new Exception("Asignacion" + ManejadorParser.SALTO_LN + getInvalidAsign(cuarteto.getOperando1(),
                                    cuarteto.getComponentes().getResultado().getToken()));
                        }
                    } else {
                        getTablaSimbolos().setLocalVariable(tipo, cuarteto.getOperador(), getSubprogramaActual());
                    }
                } else {
                    cuarteto.getOperando1().setToken(tipo);
                    throw new Exception("Declaracion de variable local" + ManejadorParser.SALTO_LN + getValorInfo(cuarteto.getOperando1())
                            + "Variable, Parametro o Arreglo ya declarado en subprograma: " + getSubprogramaActual());
                }
            } else {
                if (!existGlobalVariableOrArray(cuarteto.getOperador())) {
                    getTablaSimbolos().setGlobalVariable(tipo, cuarteto.getOperador());
                } else {
                    cuarteto.getOperando1().setToken(tipo);
                    throw new Exception("Declaracion de variable global" + ManejadorParser.SALTO_LN + getValorInfo(cuarteto.getOperando1())
                            + "Variable o Arreglo global ya declarado");
                }
            }
        } else {
            throw new Exception("Declaracion de Variable" + ManejadorParser.SALTO_LN + getInvalidAsignVoid(cuarteto.getOperando1()));
        }
    }

    public void setSimpleArrayDecl(Tipo tipo, LinkedList<Cuarteto> dimension, Cuarteto sim) throws Exception {
        if (!global) {
            if (!existLocalVariableOrParametro(sim.getResultado().getLexema())) {
                tablaSimbolos.setLocalArray(tipo, sim.getResultado().getLexema(), dimension, subprogramaActual);
            } else {
                sim.getResultado().setToken(tipo);
                throw new Exception("Declaracion de arreglo local" + SALTO_LN + getValorInfo(sim.getResultado())
                        + "Variable, Parametro o Arreglo ya declarada en subprograma: " + subprogramaActual);
            }
        } else {
            if (!existGlobalVariable(sim.getResultado().getLexema())) {
                tablaSimbolos.setGlobalArray(tipo, sim.getResultado().getLexema(), dimension);
            } else {
                sim.getResultado().setToken(tipo);
                throw new Exception("Declaracion de variable global" + SALTO_LN + getValorInfo(sim.getResultado())
                        + "Variable global ya declarada");
            }
        }
    }

    public Cuarteto setArrayDecl(Tipo tipo, LinkedList<Cuarteto> dimension, Cuarteto actual) throws Exception {
        Cuarteto i = actual;
        Cuarteto j = i;
        while (i != null) {
            j = i;
            setSimpleArrayDecl(tipo, dimension, i);
            i = i.getSiguiente();
        }
        return j;
    }

    // --------------------ANALIZADOR NO 2------------------------ //
    public String setEtiquetaResult(String et) {
        String anterior = etiquetaResult;
        etiquetaResult = et;
        return anterior;
    }

    private String getResult() {
        return T + ++contador;
    }

    public String getSubprogramResult() {
        return T + ++contadorSubprograma;
    }

    public String getEtiqueta() {
        return ETQ + ++contadorEtq;
    }

    private String getSimpleEtiqueta() {
        return ETQ + contadorEtq;
    }

    private String setMinusEtiqueta() {
        return ETQ + --contadorEtq;
    }

    private Simbolo getEtiquetaSimbol() {
        return getSimbolString(getEtiqueta());
    }

    private boolean isTrue(Simbolo sim) {
        return sim.getLexema().equals(TRUE_VAL);
    }

    private boolean isFalse(Simbolo sim) {
        return sim.getLexema().equals(FALSE_VAL);
    }

    public Tipo getTipoByKey(String key) {
        return TablaTipos.getInstance().getTipoByKey(key);
    }

    public Simbolo getSimbolBoolean(String bool) {
        return new SimboloBuilder().lexema(bool).token(TablaTipos.getInstance().getBoolean()).build();
    }

    public Simbolo getSimbolBoolean(String bool, int fila, int columna) {
        return new SimboloBuilder().lexema(bool).token(TablaTipos.getInstance().getBoolean()).fila(fila).columna(columna).build();
    }

    public Simbolo getSimbolEntero(String num, int fila, int columna) {
        long value = Long.parseLong(num);
        Simbolo sim = new SimboloBuilder().lexema(num).token(TablaTipos.getInstance().getInt()).fila(fila).columna(columna).build();
        if (value <= TIPE_CHAR) {
            sim.setToken(TablaTipos.getInstance().getChar());
        } else if (value <= TIPE_BYTE) {
            sim.setToken(TablaTipos.getInstance().getByte());
        } else if (value <= TIPE_INT) {
            sim.setToken(TablaTipos.getInstance().getInt());
        } else {
            sim.setToken(TablaTipos.getInstance().getLong());
        }
        return sim;
    }

    public Simbolo getSimbolFloat(String num, int fila, int columna) {
        return new SimboloBuilder().lexema(num.substring(0, num.length() - 1)).token(TablaTipos.getInstance().getFloat()).build();
    }

    public Simbolo getSimbolDouble(String num, int fila, int columna) {
        return new SimboloBuilder().lexema(num).token(TablaTipos.getInstance().getDouble()).fila(fila).columna(columna).build();
    }

    public Simbolo getSimbolString(String text) {
        return new SimboloBuilder().lexema(text).token(TablaTipos.getInstance().getString()).build();
    }

    public Simbolo getSimbolString(String text, int fila, int columna) {
        return new SimboloBuilder().lexema(text).token(TablaTipos.getInstance().getString()).fila(fila).columna(columna).build();
    }

    public Simbolo getSimbol(Tipo tipo, String text, int fila, int columna) {
        return new SimboloBuilder().lexema(text).token(tipo).fila(fila).columna(columna).build();
    }

    public Cuarteto getCuarteto(Simbolo sim) {
        return new CuartetoBuilder().resultado(sim).build();
    }

    public Cuarteto getCuartetoNum(Simbolo sim) {
        return new CuartetoBuilder().resultado(sim).build();
    }

    public Cuarteto getCuartetoBool(Simbolo sim) {
        return new CuartetoBuilder().resultado(sim).build();
    }

    public Cuarteto getCuartetoString(String text, int fila, int columna) {
        return new CuartetoBuilder().resultado(getSimbolString(text, fila, columna)).build();
    }

    public String getValorInfo(Simbolo sim) {
        String info = "";
        if (sim.getLexema() != null) {
            info = "Lexema: " + sim.getLexema() + SALTO_LN;
        }
        if (sim.getToken() != null) {
            info = info + "Token: " + sim.getToken().getNombre() + SALTO_LN;
        }
        if (sim.getFila() > 0) {
            info = info + "Fila: " + sim.getFila() + SALTO_LN;
        }
        if (sim.getColumna() > 0) {
            info = info + "Columna: " + sim.getColumna() + SALTO_LN;
        }
        return info;
    }

    public String getInvalidSimbol(Simbolo sim1, Simbolo sim2, int error) {
        switch (error) {
            case ERROR_NUM:
                if (TablaTipos.getInstance().isNumber(sim1.getToken())) {
                    return getValorInfo(sim2);
                } else {
                    return getValorInfo(sim1);
                }
            case ERROR_STRING:
                if (TablaTipos.getInstance().isString(sim1.getToken())) {
                    return getValorInfo(sim2);
                } else {
                    return getValorInfo(sim1);
                }
            case ERROR_BOOL:
                if (TablaTipos.getInstance().isBoolean(sim1.getToken())) {
                    return getValorInfo(sim2);
                } else {
                    return getValorInfo(sim1);
                }
            case ERROR_VOID:
                return getValorInfo(sim1);
        }
        return null;
    }

    public String getInvalidAsign(Simbolo sim1, Tipo tipo) {
        return "==Variable para asignacion==" + SALTO_LN + getValorInfo(sim1) + "===Valor de asignacion==="
                + SALTO_LN + "Asignacion incopatible (tipo): " + tipo.getNombre() + SALTO_LN + SALTO_LN + sim1.getToken().getNombre()
                + " incopatible con " + tipo.getNombre();
    }

    public String getInvalidAsignVoid(Simbolo sim) {
        sim.setToken(TablaTipos.getInstance().getVoid());
        return getValorInfo(sim) + "Asignacion de tipo \"void\" no posible para variables y parametros";
    }

    private String getInvalidCategory(Simbolo sim) {
        return getValorInfo(sim) + "Categoria: " + sim.getCategoria() + SALTO_LN + "Variable definida como arreglo";
    }

    private boolean isNotTypeNumber(Tipo token1, Tipo token2) {
        return TablaTipos.getInstance().isString(token1) || TablaTipos.getInstance().isString(token2)
                || TablaTipos.getInstance().isBoolean(token1) || TablaTipos.getInstance().isBoolean(token2);
    }

    public LinkedList<Cuarteto> getDimension(Cuarteto expresion, int fila, int columna) throws Exception {
        Cuarteto last = getLastCuarteto(expresion);
        if (TablaTipos.getInstance().isIntegerNumber(last.getResultado().getToken())) {
            LinkedList<Cuarteto> resultado = new LinkedList<>();
            resultado.add(expresion);
            return resultado;
        } else {
            throw new Exception("Declaracion de ARRAY" + getValorInfo(
                    new SimboloBuilder().token(last.getResultado().getToken()).fila(fila).columna(columna).build()) + "Valor de dimension requerido: int (entero)");
        }
    }

    public LinkedList<Cuarteto> getDimensions(LinkedList<Cuarteto> sim1, LinkedList<Cuarteto> sim2) {
        sim1.add(sim2.getFirst());
        return sim1;
    }

    private Tipo getTipo(Simbolo sim1, Simbolo sim2) {
        if (sim1.getToken().getPosicion() >= sim2.getToken().getPosicion()) {
            return sim1.getToken();
        } else {
            return sim2.getToken();
        }
    }

    private Cuarteto setAtLast(Cuarteto sim1, Cuarteto sim2) {
        Cuarteto i = sim1;
        Cuarteto j = i.getSiguiente();
        while (j != null) {
            i = j;
            j = j.getSiguiente();
        }
        i.setSiguiente(sim2);
        return sim1;
    }

    public Cuarteto getSumaConcatenacion(Cuarteto sim2, String plus, Cuarteto sim1) throws Exception {
        if (TablaTipos.getInstance().isString(sim1.getResultado().getToken())
                || TablaTipos.getInstance().isString(sim2.getResultado().getToken())) {
            return new CuartetoBuilder().operador(plus).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(getSimbolString(getResult())).siguiente(setAtLast(sim1, sim2)).build();
        } else if (TablaTipos.getInstance().isBoolean(sim1.getResultado().getToken())
                || TablaTipos.getInstance().isBoolean(sim2.getResultado().getToken())) {
            throw new Exception("Concatenacion erronea entre booleano");
        } else {
            Simbolo simResult = new SimboloBuilder().lexema(getResult()).token(getTipo(sim1.getResultado(), sim2.getResultado())).build();
            return new CuartetoBuilder().operador(plus).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(simResult).siguiente(setAtLast(sim1, sim2)).build();
        }
    }

    public Cuarteto getMulti(Cuarteto sim2, String multi, Cuarteto sim1) throws Exception {
        if (isNotTypeNumber(sim1.getResultado().getToken(), sim2.getResultado().getToken())) {
            throw new Exception("Multiplicacion " + SALTO_LN + getInvalidSimbol(sim2.getResultado(), sim1.getResultado(), ERROR_NUM));
        } else {
            Simbolo simResult = new SimboloBuilder().lexema(getResult()).token(getTipo(sim1.getResultado(), sim2.getResultado())).build();
            return new CuartetoBuilder().operador(multi).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(simResult).siguiente(setAtLast(sim1, sim2)).build();
        }
    }

    public Cuarteto getDivision(Cuarteto sim2, String division, Cuarteto sim1) throws Exception {//sim2 <- sim1
        if (isNotTypeNumber(sim1.getResultado().getToken(), sim2.getResultado().getToken())) {
            throw new Exception("Division ( / )" + SALTO_LN + getInvalidSimbol(sim2.getResultado(), sim1.getResultado(), ERROR_NUM));
        } else {
            Simbolo simResult = new SimboloBuilder().lexema(getResult()).token(getTipo(sim1.getResultado(), sim2.getResultado())).build();
            return new CuartetoBuilder().operador(division).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(simResult).siguiente(setAtLast(sim1, sim2)).build();
        }
    }

    public Cuarteto getResta(Cuarteto sim2, String minus, Cuarteto sim1) throws Exception {
        if (isNotTypeNumber(sim1.getResultado().getToken(), sim2.getResultado().getToken())) {
            throw new Exception("Resta ( - )" + SALTO_LN + getInvalidSimbol(sim2.getResultado(), sim1.getResultado(), ERROR_NUM));
        } else {
            Simbolo simResult = new SimboloBuilder().lexema(getResult()).token(getTipo(sim1.getResultado(), sim2.getResultado())).build();
            return new CuartetoBuilder().operador(minus).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(simResult).siguiente(setAtLast(sim1, sim2)).build();
        }
    }

    public Cuarteto getModulo(Cuarteto sim2, String module, Cuarteto sim1) throws Exception {
        if (isNotTypeNumber(sim1.getResultado().getToken(), sim2.getResultado().getToken())) {
            throw new Exception("Modulo ( % )" + SALTO_LN + getInvalidSimbol(sim2.getResultado(), sim1.getResultado(), ERROR_NUM));
        } else {
            Simbolo simResult = new SimboloBuilder().lexema(getResult()).token(getTipo(sim1.getResultado(), sim2.getResultado())).build();
            return new CuartetoBuilder().operador(module).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(simResult).siguiente(setAtLast(sim1, sim2)).build();
        }
    }

    public Cuarteto getRelacion(Cuarteto sim2, String rel, Cuarteto sim1) throws Exception {
        if (!isNotTypeNumber(sim1.getResultado().getToken(), sim2.getResultado().getToken())) {
            if (sim1.getOperador() == null || sim2.getOperador() == null) {
                String relacion = getLastCuarteto(sim2).getResultado().getLexema() + rel + getLastCuarteto(sim1).getResultado().getLexema();
                Cuarteto rela = new CuartetoBuilder().operador(rel).operando1(sim2.getResultado()).operando2(sim1.getResultado()).
                        resultado(getSimbolBoolean(relacion)).siguiente(setAtLast(sim2, sim1)).build();
                return new CuartetoBuilder().operador(rel).operando1(getSimbolString(setEtiquetaResult(getEtiqueta()))).
                        operando2(getSimbolString(getSimpleEtiqueta())).resultado(getSimbolBoolean(getEtiqueta())).restriccion(rela).build();
            } else {
                if (sim1.getOperador() == null) {
                    sim1.setSiguiente(setAtLast(sim1, sim2));
                    return sim1;
                } else if (sim2.getOperador() == null) {
                    sim2.setSiguiente(setAtLast(sim2, sim1));
                    return sim2;
                }
            }
        } else {
            throw new Exception("Relacion incopatible entre tipos ( " + rel + " )" + SALTO_LN + getInvalidSimbol(sim2.getResultado(), sim1.getResultado(), ERROR_NUM));
        }
        return null;
    }

    public Cuarteto getSingleRelacion() {

        return null;
    }

    public Cuarteto getBool(Simbolo sim) {
        Cuarteto simb = getCuartetoBool(sim);
        simb.setOperador(BOOL);
        return new CuartetoBuilder().operador(sim.getLexema()).operando1(getSimbolString(setEtiquetaResult(getEtiqueta()))).
                operando2(getSimbolString(getSimpleEtiqueta())).resultado(getSimbolBoolean(getEtiqueta())).restriccion(simb).build();
    }

    public Cuarteto getVariableRel(String key, int fila, int columna) throws Exception {
        Simbolo sim = null;
        if (!global && existLocalVariableOrParametro(key)) {
            sim = getLocalVariable(key, subprogramaActual);
            sim.setLexema(getLocalName(key));
        } else if (existGlobalVariable(key)) {
            sim = tablaSimbolos.getGlobalVariable(key);
        } else {
            throw new Exception("Variable sin declarar" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().lexema(key).fila(fila).columna(columna).build())
                    + "Variable local o global no ha sido declarada");
        }
        if (TablaTipos.getInstance().isBoolean(sim.getToken())) {
            return new CuartetoBuilder().operador(VAR_BOOL).operando1(getSimbolString(setEtiquetaResult(getEtiqueta()))).
                    operando2(getSimbolString(getSimpleEtiqueta())).resultado(getSimbolBoolean(getEtiqueta())).
                    restriccion(new CuartetoBuilder().resultado(sim).build()).build();
        } else {
            throw new Exception("Operacion relacional" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().lexema(key).fila(fila).columna(columna).build())
                    + "Token necesario: boolean (booleano)");
        }

        /*if (existSimbol(key)) {
            Cuarteto var = getVariable(key, fila, columna);
            var.setOperador(VAR_BOOL);
            if (TablaTipos.getInstance().isBoolean(var.getResultado().getToken())) {
                return new CuartetoBuilder().operando1(getSimbolString(setEtiquetaResult(getEtiqueta()))).
                        operando2(getSimbolString(getSimpleEtiqueta())).resultado(getSimbolBoolean(getEtiqueta())).restriccion(var).build();
            } else {
                throw new Exception("Variable " + key + " no puede ser de tipo booleana ");
            }
        } else {
            throw new Exception("Variable " + key + " no ha sido declarada");
        }*/
    }

    public Cuarteto getArrayPositionRel(Cuarteto arrayPosition) {
        if (TablaTipos.getInstance().isBoolean(arrayPosition.getResultado().getToken())) {
            return new CuartetoBuilder().operando1(getSimbolString(setEtiquetaResult(getEtiqueta()))).
                    operando2(getSimbolString(getSimpleEtiqueta())).resultado(getSimbolBoolean(getEtiqueta())).restriccion(arrayPosition).build();
        }
        return null;
    }

    public Cuarteto getNot(String not, Cuarteto sim) throws Exception {
        if (TablaTipos.getInstance().isBoolean(sim.getResultado().getToken())) {
            sim.setAuxiliar(getCuartetoString(NOT, 0, 0));
            return sim;
        } else {
            throw new Exception("Variable " + sim.getResultado().getLexema() + " no es de tipo booleana ");
        }
    }

    public Cuarteto getOr(Cuarteto sim1, String or, Cuarteto sim2) throws Exception {
        if (areBoolean(sim1.getResultado(), sim2.getResultado())) {
            Cuarteto sim3 = getLastCuarteto(sim1);
            setEtiquetaResult(sim3.getOperando2().getLexema());
            sim3.setOperador(OR);
            sim3.setAuxiliar(getCuartetoString(OR, 0, 0));
            if (sim2.getOperador() != null) {
                if (!sim2.getOperador().equals(AND)) {
                    sim2.setOperando2(sim3.getOperando2());
                }
            } else {
                sim2.setOperando2(sim3.getOperando2());
            }
            sim2.setOperando1(sim3.getResultado());
            sim2.setResultado(getSimbolString(setMinusEtiqueta()));
            sim3.setSiguiente(sim2);
            if (sim2.getOperador() == null) {
                sim2.setOperador(OR);
            }
            return sim1;
        } else {
            throw new Exception("Relacion " + or + " entre numeros no es posible (Diferencia de tipos) " + sim2.getResultado().getLexema() + " - " + sim1.getResultado().getLexema());
        }
    }

    private boolean areBoolean(Simbolo sim1, Simbolo sim2) {
        return TablaTipos.getInstance().isBoolean(sim1.getToken())
                && TablaTipos.getInstance().isBoolean(sim2.getToken());
    }

    public Cuarteto getAnd(Cuarteto sim1, String and, Cuarteto sim2) throws Exception {
        if (areBoolean(sim1.getResultado(), sim2.getResultado())) {
            Cuarteto sim3 = getLastCuarteto(sim1);
            sim2.setResultado(sim3.getResultado());
            sim3.setOperador(AND);
            sim3.setAuxiliar(getCuartetoString(AND, 0, 0));
            setMinusEtiqueta();
            sim3.setSiguiente(sim2);
            if (sim2.getOperador() == null) {
                sim2.setOperador(AND);
                sim2.setAuxiliar(getCuartetoString(AND, 0, 0));
            }
            return sim1;
        } else {
            throw new Exception("Relacion " + and + " entre numeros no es posible (Diferencia de tipos) " + sim2.getResultado().getLexema() + " - " + sim1.getResultado().getLexema());
        }
    }

    public Cuarteto orderOpNum(Cuarteto operacion) {
        Stack<Cuarteto> pila = new Stack<>();
        Cuarteto i = operacion;
        while (i != null) {
            pila.push(i);
            i = i.getSiguiente();
        }
        Cuarteto j = pila.pop();
        Cuarteto k = j;
        Cuarteto h = null;
        while (!pila.empty()) {
            h = pila.pop();
            k.setSiguiente(h);
            k = h;
        }
        k.setSiguiente(null);
        return j;
    }

    public Cuarteto getSimplePrint(Cuarteto sim, String print) {
        return new CuartetoBuilder().operador(print).resultado(getLastCuarteto(sim).getResultado()).componentes(sim).build();
    }

    public Cuarteto getPrint(Cuarteto anterior, Cuarteto actual, String print) {
        Cuarteto i = anterior;
        Cuarteto j = i.getSiguiente();
        while (j != null) {
            i = j;
            j = j.getSiguiente();
        }
        i.setSiguiente(getSimplePrint(actual, print));
        return anterior;
    }

    public Cuarteto getScanString(String var, int fila, int columna) throws Exception {
        Simbolo sim = null;
        if (!global && existLocalVariableOrParametro(var)) {
            sim = getLocalVariable(var, subprogramaActual);
            sim.setLexema(getLocalName(var));
        } else if (existGlobalVariable(var)) {
            sim = getGlobalVariable(var);
            sim.setLexema(var);
        } else {
            throw new Exception("Parametro en SCANS" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().lexema(var).fila(fila).columna(columna).build())
                    + "Variable local o global no declarado");
        }
        if (!TablaTipos.getInstance().isString(sim.getToken())) {
            throw new Exception("Diferencia de tipos en SCANS" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().lexema(var).token(sim.getToken()).fila(fila).columna(columna).build())
                    + "Token esperado: string");
        }
        return new CuartetoBuilder().operador(SCANS).resultado(sim).build();
    }

    public Cuarteto getScanString(Cuarteto array, int fila, int columna) throws Exception {
        if (!TablaTipos.getInstance().isString(array.getResultado().getToken())) {
            throw new Exception("Diferencia de tipos en SCANS" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().lexema(array.getResultado().getLexema())
                            .token(array.getResultado().getToken()).fila(fila).columna(columna).build())
                    + "Token esperado: string");
        }
        array.setOperador(SCANS);
        return array;
    }

    public Cuarteto getScanNumber(String var, int fila, int columna) throws Exception {
        Simbolo sim = null;
        if (!global && existLocalVariableOrParametro(var)) {
            sim = getLocalVariable(var, subprogramaActual);
            sim.setLexema(getLocalName(var));
        } else if (existGlobalVariable(var)) {
            sim = getGlobalVariable(var);
            sim.setLexema(var);
        } else {
            throw new Exception("Parametro en SCANN" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().lexema(var).fila(fila).columna(columna).build())
                    + "Variable local o global no declarado");
        }
        if (!TablaTipos.getInstance().isNumber(sim.getToken())) {
            throw new Exception("Diferencia de tipos en SCANN" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().lexema(var).token(sim.getToken()).fila(fila).columna(columna).build())
                    + "Token esperado: char, byte, int, long, float, double");
        }
        return new CuartetoBuilder().operador(SCANN).resultado(sim).build();
    }

    public Cuarteto getScanNumber(Cuarteto array, int fila, int columna) throws Exception {
        if (!TablaTipos.getInstance().isNumber(array.getResultado().getToken())) {
            throw new Exception("Diferencia de tipos en SCANN" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().lexema(array.getResultado().getLexema())
                            .token(array.getResultado().getToken()).fila(fila).columna(columna).build())
                    + "Token esperado: char, byte, int, long, float, double");
        }
        array.setOperador(SCANN);
        return array;
    }

    public Cuarteto getComponentes(Cuarteto sim1, Cuarteto sim2) {
        Cuarteto last = getLastCuarteto(sim1);
        last.setSiguiente(new CuartetoBuilder().componentes(sim2).build());
        return sim1;
    }

    public Cuarteto getComponente(Cuarteto sim) {
        return new CuartetoBuilder().componentes(sim).build();
    }

    public Cuarteto getPosition(Cuarteto sim, int fila, int columna) throws Exception {
        Cuarteto last = getLastCuarteto(sim);
        if (!TablaTipos.getInstance().isIntegerNumber(last.getResultado().getToken())) {
            throw new Exception("Posicion de Arreglo" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().token(last.getResultado().getToken()).fila(fila).columna(columna).build())
                    + "Token esperado: Entero");
        }
        return new CuartetoBuilder().componentes(sim).build();
    }

    public Cuarteto getArrayPosition(String var, Cuarteto dimensions, int fila, int columna) throws Exception {
        Simbolo sim = null;
        if (!global && existLocalArray(var)) {
            sim = getLocalArray(var, subprogramaActual);
            sim.setLexema(getLocalName(var));
        } else if (existGlobalArray(var)) {
            sim = getGlobalArray(var);
            sim.setLexema(var);
        } else {
            throw new Exception("Posicion de Arreglo" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().lexema(var).fila(fila).columna(columna).build())
                    + "Arreglo local o global no declarado");
        }
        LinkedList<Cuarteto> lista = getCuartetoList(dimensions);
        if (lista.size() == sim.getDimensiones().size()) {
            sim.setFila(fila);
            sim.setColumna(columna);
            Cuarteto result = getOneDimension(dimensions, sim);
            LinkedList<Cuarteto> list = new LinkedList<>(sim.getDimensiones());
            return new CuartetoBuilder().operando1(getLastCuarteto(result).getResultado())
                    .componentes(result).resultado(sim).build();
        } else {
            throw new Exception("Numero de dimensiones en posicion de arreglo" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().lexema(var).fila(fila).columna(columna).build())
                    + "Dimension(es) necesaria(s): " + sim.getDimensiones().size());
        }
    }

    public Cuarteto getArrayPosition(Cuarteto array) {
        Simbolo simResult = new SimboloBuilder().lexema(getResult()).token(array.getResultado().getToken()).build();
        return new CuartetoBuilder().operador(ARRAY).resultado(simResult).componentes(array.getComponentes())
                .operando1(array.getResultado()).operando2(array.getOperando1()).build();
    }

    private Cuarteto getOneDimension(Cuarteto dimensiones, Simbolo array) throws Exception {
        LinkedList<Cuarteto> posiciones = new LinkedList<>(getCuartetoList(dimensiones));
        LinkedList<Cuarteto> tamanos = new LinkedList<>(array.getDimensiones());
        Cuarteto resultado = null;
        Cuarteto resultadoAnterior = null;
        Cuarteto tamanoAnterior = null;
        Cuarteto posicionAnterior = null;
        int contador = 0;
        int j = 0;
        for (int i = 0; i < posiciones.size(); i++) {
            resultado = new CuartetoBuilder().resultado(getLastCuarteto((Cuarteto) posiciones.get(i).getComponentesClone()).getResultadoClone()).build();
            j = ++contador;
            while (j < tamanos.size()) {
                resultado = getMulti(resultado, MULTI, new CuartetoBuilder().resultado(getLastCuarteto((Cuarteto) tamanos.get(j)).getResultadoClone()).build());
                j++;
            }
            if (resultadoAnterior != null) {
                resultadoAnterior = getSumaConcatenacion(resultadoAnterior, PLUS, resultado);
            } else {
                resultadoAnterior = resultado;
            }
            if (posicionAnterior != null) {
//                getLastCuarteto(tamanoAnterior).setSiguiente((Cuarteto) tamanos.get(i).clone());
                getLastCuarteto(posicionAnterior).setSiguiente((Cuarteto) posiciones.get(i).getComponentesClone().clone());
            } else {
//                tamanoAnterior = (Cuarteto) tamanos.get(i).clone();
                posicionAnterior = (Cuarteto) posiciones.get(i).getComponentes().clone();
            }
        }
//        getLastCuarteto(tamanoAnterior).setSiguiente(posicionAnterior);
//        getLastCuarteto(tamanoAnterior).setSiguiente(orderOpNum(resultadoAnterior));
        getLastCuarteto(posicionAnterior).setSiguiente(orderOpNum(resultadoAnterior));
        return posicionAnterior;
    }

    public void addSubprogramInstruction(Cuarteto instruction) {
        tablaSimbolos.addSubprogramEstructura(instruction, subprogramaActual);
    }

    public void setSubprogramReturn(boolean valorReturn) {
        tablaSimbolos.setSubprogramBooleanValorReturn(valorReturn, subprogramaActual);
    }

    public void verificateSubprogram() throws Exception {
        Simbolo sim = getSubprogram(subprogramaActual);
        if (!TablaTipos.getInstance().isVoid(sim.getToken()) && !sim.isValorRetorno()) {
            throw new Exception("Return en subprograma" + SALTO_LN + "Subprograma \"" + subprogramaActual + "\" sin valor de retorno");
        }
    }

    public Cuarteto getReturn(Cuarteto expresion, int fila, int columna) throws Exception {
        Cuarteto last = getLastCuarteto(expresion);
        Simbolo sim = getSubprogram(subprogramaActual);
        if (!TablaTipos.getInstance().isVoid(sim.getToken())) {
            if (TablaTipos.getInstance().isCompatible(last.getResultado().getToken(), sim.getToken())) {
                return new CuartetoBuilder().operador(RETURN).resultado(last.getResultado()).componentes(expresion).build();
            } else {
                throw new Exception("Return en subprograma" + SALTO_LN + getInvalidAsign(sim, last.getResultado().getToken()));
            }
        } else {
            throw new Exception("Return en subprograma" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().token(sim.getToken()).fila(fila).columna(columna).build())
                    + "Subprograma de tipo \"void\" no puede devolver ningun valor");
        }
    }

    public Cuarteto getSubprogramCall(String var, Cuarteto params, int fila, int columna) throws Exception {
        if (existSubprogram(var)) {
            Simbolo subprograma = getSubprogram(var);
            Simbolo result = new SimboloBuilder().lexema(getResult()).token(subprograma.getToken()).build();
            subprograma.setLexema(var);
            Cuarteto parametros = compareSubprogramParams(subprograma.getParametros(), params, fila, columna, subprogramaActual);
            return new CuartetoBuilder().operador(SUBPROGRAM_CALL).resultado(result)
                    .operando1(subprograma).componentes(params).auxiliar(parametros).build();
        } else {
            throw new Exception("Llamada de subprograma" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().lexema(var).fila(fila).columna(columna).build())
                    + "Subprograma no existente");
        }
    }

    public Cuarteto compareSubprogramParams(LinkedList<Tipo> params, Cuarteto callParams, int fila, int columna, String subprogram) throws Exception {
        LinkedList<Cuarteto> list = getCuartetoList(callParams);
        Cuarteto parametros = null;
        if (list.size() == params.size()) {
            if (params.size() > 0) {
                for (int i = 0; i < params.size(); i++) {
                    Cuarteto last = getLastCuarteto(list.get(i).getComponentes());
                    if (parametros != null) {
                        getLastCuarteto(parametros).setSiguiente(new CuartetoBuilder().resultado(new SimboloBuilder().lexema(last.getResultado()
                                .getLexema()).build()).operador(PARAM).build());
                    } else {
                        parametros = new CuartetoBuilder().resultado(new SimboloBuilder().lexema(last.getResultado()
                                .getLexema()).build()).operador(PARAM).build();
                    }
                    if (!TablaTipos.getInstance().isCompatible(last.getResultado().getToken(), params.get(i))) {
                        throw new Exception("Parametro en llamada de subprograma" + SALTO_LN + getValorInfo(
                                new SimboloBuilder().token(last.getResultado().getToken()).fila(fila).columna(columna).build()) + "Token esperado: " + params.get(i).getNombre());
                    }
                }
            }
            return parametros;
        } else {
            throw new Exception("Llamada de subprograma" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().lexema(subprogram).fila(fila).columna(columna).build())
                    + "El numero de parametros no coincide: " + list.size() + SALTO_LN + "No. parametros necesarios: " + params.size() + SALTO_LN);
        }
    }

    public LinkedList<Cuarteto> getCuartetoList(Cuarteto head) {
        LinkedList<Cuarteto> list = new LinkedList<>();
        Cuarteto i = head;
        while (i != null) {
            list.add(i);
            i = i.getSiguiente();
        }
        return list;
    }

    public Cuarteto getDecls(Cuarteto anterior, Cuarteto actual) {
        Cuarteto i = anterior;
        Cuarteto j = i.getSiguiente();
        while (j != null) {
            i = j;
            j = j.getSiguiente();
        }
        i.setSiguiente(actual);
        return anterior;
    }

    public Cuarteto getAsign(String name, Cuarteto exp) {
        String var = name.substring(0, name.indexOf("["));
        String datos = name.substring(name.indexOf("[") + 1, name.indexOf("]"));
        String info[] = datos.split("-");
        int fila = Integer.parseInt(info[0]);
        int columna = Integer.parseInt(info[1]);
        return new CuartetoBuilder().operador(var).operando1(new SimboloBuilder().
                lexema(var).fila(fila).columna(columna).build()).componentes(exp).build();
    }

    public Cuarteto getSimpleAsign(String name, int fila, int columna) {
        return new CuartetoBuilder().operador(name).operando1(new SimboloBuilder().lexema(name).fila(fila).columna(columna).build()).build();
    }

    public Cuarteto setAsign(Cuarteto cuarteto, Tipo tipo) throws Exception {
        Cuarteto inicial = asign(cuarteto, tipo);
        Cuarteto k = inicial;
        Cuarteto i = cuarteto.getSiguiente();
        while (i != null) {
            k.setSiguiente(asign(i, tipo));
            i = i.getSiguiente();
            k = k.getSiguiente();
        }
        return inicial;
    }

    private Cuarteto asign(Cuarteto cuarteto, Tipo tipo) throws Exception {
        if (!existSimbol(cuarteto.getOperador())) {
            if (cuarteto.getComponentes() != null) {
                if (TablaTipos.getInstance().isCompatible(cuarteto.getComponentes().getResultado().getToken(), tipo)) {
                    tablaSimbolos.setSimbol(cuarteto.getOperador(), tipo);
                    String asign = ASSIGN;
                    if (TablaTipos.getInstance().isBoolean(tipo)) {
                        asign = ASSIGN_BOOL;
                    }
                    return new CuartetoBuilder().operador(asign).componentes(cuarteto.
                            getComponentes()).operando1(getLastCuarteto(cuarteto.getComponentes()).
                                    getResultado()).resultado(new SimboloBuilder().lexema(cuarteto.getOperador()).token(tipo).build()).build();
                } else {
                    cuarteto.getOperando1().setToken(tipo);
                    throw new Exception("Asignacion" + SALTO_LN + getInvalidAsign(cuarteto.getOperando1(),
                            cuarteto.getComponentes().getResultado().getToken()));
                }
            } else {
                tablaSimbolos.setSimbol(cuarteto.getOperador(), tipo);
                return new CuartetoBuilder().build();
            }
        } else {
            throw new Exception("Variable <" + cuarteto.getOperador() + "> no existente o no declarada");
        }
    }

    public Cuarteto asignByToken(Cuarteto sim, Cuarteto expresion, int fila, int columna) throws Exception {
        Cuarteto last = getLastCuarteto(expresion);
        if (TablaTipos.getInstance().isCompatible(last.getResultado().getToken(), sim.getResultado().getToken())) {
            String asign = ASSIGN;
            if (TablaTipos.getInstance().isBoolean(sim.getResultado().getToken())) {
                asign = ASSIGN_BOOL;
            }
            return new CuartetoBuilder().operador(asign).componentes(expresion).
                    operando1(last.getResultado()).resultado(sim.getResultado()).build();
        } else {
            throw new Exception("Asignacion de valor en variable" + SALTO_LN + getInvalidAsign(sim.getResultado(), last.getResultado().getToken()));
        }
        /*if (existSimbol(token)) {
            sim = tablaSimbolos.getSimbol(token);
            last = getLastCuarteto(expresion);
            if (TablaTipos.getInstance().isCompatible(last.getResultado().getToken(), sim.getToken())) {
                String asign = ASSIGN;
                if (TablaTipos.getInstance().isBoolean(sim.getToken())) {
                    asign = ASSIGN_BOOL;
                }
                return new CuartetoBuilder().operador(asign).componentes(expresion).
                        operando1(last.getResultado()).resultado(sim).build();
            } else {
                throw new Exception("Variable " + token + " no es compatible con expresion de tipo " + expresion.getResultado().getToken().getNombre());
            }
        } else {
            throw new Exception("Variable <" + token + "> no existente o no declarada");
        }*/
    }

    public Cuarteto getSimpleArrayAsign(String text) {
        return new CuartetoBuilder().resultado(getSimbolString(text)).build();
    }

    public Cuarteto getArrayAsign(Cuarteto anterior, String actual) {
        Cuarteto inicio = anterior;
        Cuarteto i = inicio;
        Cuarteto j = inicio.getSiguiente();
        while (j != null) {
            i = j;
            j = j.getSiguiente();
        }
        i.setSiguiente(getSimpleArrayAsign(actual));
        return inicio;
    }

    public Cuarteto getVariable(String var, int fila, int columna) throws Exception {
        if (!global && existLocalVariableOrParametro(var)) {
            Simbolo sim = getLocalVariable(var, subprogramaActual);
            sim.setFila(fila);
            sim.setColumna(columna);
            sim.setLexema(getLocalName(var));
            return new CuartetoBuilder().resultado(sim).build();
        } else if (existGlobalVariable(var)) {
            Simbolo sim = getGlobalVariable(var);
            sim.setFila(fila);
            sim.setColumna(columna);
            sim.setLexema(var);
            return new CuartetoBuilder().resultado(sim).build();
        } else {
            throw new Exception("Asignacion" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().lexema(var).fila(fila).columna(columna).build())
                    + "Variable no declarada o no definida");
        }
    }

    private Cuarteto getLastCuarteto(Cuarteto sim) {
        Cuarteto sim2 = sim;
        Cuarteto i = sim2.getSiguiente();
        while (i != null) {
            sim2 = i;
            i = i.getSiguiente();
        }
        return sim2;
    }

    private void setOperadorAll(Cuarteto head, String operador) {
        Cuarteto sim = head;
        while (sim != null) {
            sim.setOperador(operador);
            sim = sim.getSiguiente();
        }
    }

    private void setOperando1All(Cuarteto head, Simbolo operando1) {
        Cuarteto sim = head;
        while (sim != null) {
            sim.setOperando1(operando1);
            sim = sim.getSiguiente();
        }
    }

    private void setOperando2All(Cuarteto head, Simbolo operando2) {
        Cuarteto sim = head;
        while (sim != null) {
            sim.setOperando2(operando2);
            sim = sim.getSiguiente();
        }
    }

    private void setResultadoAll(Cuarteto head, Simbolo resultado) {
        Cuarteto sim = head;
        while (sim != null) {
            sim.setResultado(resultado);
            sim = sim.getSiguiente();
        }
    }

    private void setResultadoAllIfElse(Cuarteto head, Simbolo resultado) {
        Cuarteto sim = head;
        while (sim != null) {
            if (sim.getOperador() != null) {
                if (isIfElse(sim.getOperador())) {
                    sim.setResultado(resultado);
                }
            }
            sim = sim.getSiguiente();
        }
    }

    private boolean isIfElse(String operador) {
        return operador.equals(IF) || operador.equals(ELSEIF) || operador.equals(ELSE);
    }

    public Cuarteto ifOperacion(Cuarteto ifA, Cuarteto ifB) {
        setAtLast(ifA, ifB);
        setResultadoAllIfElse(ifA, getLastCuarteto(ifB).getResultado());
        return ifA;
    }

    public Cuarteto ifOperacion(Cuarteto restriccion, Cuarteto instructions, int fila, int columna) throws Exception {
        if (TablaTipos.getInstance().isBoolean(restriccion.getResultado().getToken())) {
            Cuarteto last = getLastCuarteto(restriccion);
            etiquetaResult = getSimpleEtiqueta();
            setOperadorAll(restriccion, CICLO);
            Cuarteto result = new CuartetoBuilder().operador(IF).operando1(last.getOperando2()).
                    operando2(restriccion.getOperando1()).resultado(last.getResultado()).componentes(instructions).build();
            return setAtLast(restriccion, result);
        } else {
            throw new Exception("Funcion IF" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().token(restriccion.getResultado().getToken()).fila(fila).columna(columna).build())
                    + " Token esperado: boolean");
        }
    }

    public Cuarteto elseIfOperacion(Cuarteto restriccion, Cuarteto instructions, int fila, int columna) throws Exception {
        if (TablaTipos.getInstance().isBoolean(restriccion.getResultado().getToken())) {
            Cuarteto last = getLastCuarteto(restriccion);
            etiquetaResult = getSimpleEtiqueta();
            setOperadorAll(restriccion, CICLO);
            Cuarteto result = new CuartetoBuilder().operador(ELSEIF).operando1(last.getOperando2()).
                    operando2(restriccion.getOperando1()).resultado(last.getResultado()).componentes(instructions).build();
            return setAtLast(restriccion, result);
        } else {
            throw new Exception("Funcion ELSIF" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().token(restriccion.getResultado().getToken()).fila(fila).columna(columna).build())
                    + " Token esperado: boolean");
        }
    }

    public Cuarteto elseOperacion(Cuarteto elseA, Cuarteto elseB) {
        return setAtLast(elseA, elseB);
    }

    public Cuarteto elseOperacion(Cuarteto instructions, int fila, int columna) {
        return new CuartetoBuilder().operador(ELSE).resultado(getSimbolString(getSimpleEtiqueta())).componentes(instructions).build();
    }

    public Cuarteto whileOperacion(String whil, Cuarteto restriccion, Cuarteto instructions, int fila, int columna) throws Exception {
        if (TablaTipos.getInstance().isBoolean(restriccion.getResultado().getToken())) {
            Cuarteto last = getLastCuarteto(restriccion);
            etiquetaResult = getEtiqueta();
            Cuarteto result = new CuartetoBuilder().operador(WHILE).operando1(last.getOperando2()).
                    operando2(restriccion.getOperando1()).resultado(last.getResultado()).componentes(instructions).build();
            setOperadorAll(restriccion, CICLO);
            return setAtLast(restriccion, result);
        } else {
            throw new Exception("Funcion WHILE" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().token(restriccion.getResultado().getToken()).fila(fila).columna(columna).build())
                    + " Token esperado: boolean");
        }
    }

    public Cuarteto doWhileOperacion(String doW, Cuarteto restriccion, Cuarteto instructions, int fila, int columna) throws Exception {
        if (TablaTipos.getInstance().isBoolean(restriccion.getResultado().getToken())) {
            Cuarteto last = getLastCuarteto(restriccion);
            etiquetaResult = getEtiqueta();
            Cuarteto result = new CuartetoBuilder().operador(doW).operando1(last.getOperando2()).
                    operando2(restriccion.getOperando1()).resultado(last.getResultado()).build();
            setOperadorAll(restriccion, CICLO);
            restriccion.setComponentes(instructions);
            return setAtLast(restriccion, result);
        } else {
            throw new Exception("Funcion DO WHILE" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().token(restriccion.getResultado().getToken()).fila(fila).columna(columna).build())
                    + " Token esperado: boolean");
        }
    }

    public Cuarteto setArrayAsign(Cuarteto arrayPosition, Cuarteto expresion) throws Exception {
        Cuarteto lastCuarteto = getLastCuarteto(expresion);
        if (TablaTipos.getInstance().isCompatible(lastCuarteto.getResultado().getToken(), arrayPosition.getResultado().getToken())) {
            Cuarteto last = getLastCuarteto(arrayPosition);
            Cuarteto last2 = getLastCuarteto(expresion);
            return new CuartetoBuilder().operador(ARRAY_ASIGN).resultado(last2.getResultado()).
                    operando1(last.getResultado()).componentes(expresion).auxiliar(arrayPosition).build();
        } else {
            throw new Exception("Asignacion de arreglo" + SALTO_LN + getInvalidAsign(arrayPosition.getResultado(), lastCuarteto.getResultado().getToken()));
        }
    }

    public Cuarteto forOperacion(Cuarteto asignacion, Cuarteto opBol, String signo, String entero, Cuarteto instructions, int fila, int columna, int fila2, int columna2) throws Exception {
        if (TablaTipos.getInstance().isBoolean(opBol.getResultado().getToken())) {
            Cuarteto last = getLastCuarteto(opBol);
            Cuarteto instruccion = null;
            Cuarteto num = getCuartetoNum(getSimbolEntero(entero, 0, 0));
            Simbolo simResult = new SimboloBuilder().lexema(getResult()).token(getTipo(num.getResultado(), num.getResultado())).build();
            if (signo.equals(PLUS)) {
                instruccion = new CuartetoBuilder().operador(PLUS).operando1(asignacion.getResultado()).operando2(num.getResultado()).resultado(simResult).build();
            } else if (signo.equals(MINUS)) {
                instruccion = new CuartetoBuilder().operador(MINUS).operando1(asignacion.getResultado()).operando2(num.getResultado()).resultado(simResult).build();
            }
            Cuarteto asign = asignByToken(asignacion, instruccion, fila, columna);
            etiquetaResult = getEtiqueta();
            Cuarteto result = new CuartetoBuilder().operador(FOR).operando1(last.getOperando2()).
                    operando2(opBol.getOperando1()).resultado(last.getResultado()).componentes(instructions).restriccion(asign).build();
            setOperadorAll(opBol, CICLO);
            setAtLast(opBol, result);
            return setAtLast(asignacion, opBol);
        } else {
            throw new Exception("Funcion FOR" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().token(opBol.getResultado().getToken()).fila(fila2).columna(columna2).build())
                    + " Token esperado: boolean");
        }
    }

    public Cuarteto getInstructions(Cuarteto e1, Cuarteto e2) {
        if (e1 != null && e2 != null) {
            return setAtLast(e1, e2);
        } else if (e1 != null) {
            return e1;
        } else if (e2 != null) {
            return e2;
        } else {
            return null;
        }
    }

    public void print(Cuarteto bodyInstruction, Cuarteto mainInstruction) {
        if (bodyInstruction != null) {
            printBodyInstruction(bodyInstruction);
        }
        if (mainInstruction != null) {
            printMainInstruction(mainInstruction);
        }
    }

    public boolean isOperadorRelacional(String rel) {
        return rel.equals(LOWER) || rel.equals(HIGHER) || rel.equals(LOWER_EQ)
                || rel.equals(HIGHER_EQ) || rel.equals(NOT_EQUAL) || rel.equals(EQUAL);
    }

    public void print(Cuarteto instructions) {
        Cuarteto sim = instructions;
        while (sim != null) {
            printInstruction(sim);
            sim = sim.getSiguiente();
        }
    }

    private void printCuarteto(Cuarteto cuarteto) {
        Cuarteto sim = cuarteto.getComponentes();
        while (sim != null) {
            printInstruction(sim);
            sim = sim.getSiguiente();
        }
    }

    private void printInstruction(Cuarteto cuarteto) {
        String operador = cuarteto.getOperador();
        if (operador != null) {
            switch (operador) {
                case HIGHER:
                    printRel(cuarteto);
                    break;
                case LOWER:
                    printRel(cuarteto);
                    break;
                case HIGHER_EQ:
                    printRel(cuarteto);
                    break;
                case LOWER_EQ:
                    printRel(cuarteto);
                    break;
                case EQUAL:
                    printRel(cuarteto);
                    break;
                case NOT_EQUAL:
                    printRel(cuarteto);
                    break;
                case PRINT:
                    printPrint(cuarteto);
                    break;
                case PRINTLN:
                    printPrintln(cuarteto);
                    break;
                case SCANS:
                    printScanString(cuarteto);
                    break;
                case SCANN:
                    printScanNumber(cuarteto);
                    break;
                case ARRAY:
                    printArrayPosition(cuarteto);
                    break;
                case ARRAY_ASIGN:
                    printArrayAsign(cuarteto);
                    break;
                case ASSIGN:
                    printAsign(cuarteto);
                    break;
                case ASSIGN_BOOL:
                    printAsignBool(cuarteto);
                    break;
                case SUBPROGRAM:
                    printSubprogram(cuarteto);
                    break;
                case SUBPROGRAM_CALL:
                    printSubprogramCall(cuarteto);
                    break;
                case RETURN:
                    printReturn(cuarteto);
                    break;
                case PARAM:
                    printParametro(cuarteto);
                    break;
                case IF:
                    printIf(cuarteto);
                    break;
                case ELSEIF:
                    printElseIf(cuarteto);
                    break;
                case ELSE:
                    printElse(cuarteto);
                    break;
                case WHILE:
                    printWhile(cuarteto);
                    break;
                case FOR:
                    printFor(cuarteto);
                    break;
                case DO:
                    printDo(cuarteto);
                    break;
                case CICLO:
                    printCiclo(cuarteto);
                    break;
                case PLUS:
                    printOperacion(cuarteto);
                    break;
                case MINUS:
                    printOperacion(cuarteto);
                    break;
                case MODULE:
                    printOperacion(cuarteto);
                    break;
                case MULTI:
                    printOperacion(cuarteto);
                    break;
                case DIVISION:
                    printOperacion(cuarteto);
                    break;
                case BOOL:
                    printBool(cuarteto);
                    break;
                case VAR_BOOL:
                    printVarBool(cuarteto);
                    break;
            }
        }
    }

    private String getEtiquetaPrint(Simbolo sim) {
        return sim.getLexema() + COLON;
    }

    private String getIf(String condicion) {
        return IF_AB + condicion + IF_CE;
    }

    private void printBodyInstruction(Cuarteto sim) {
        print(sim);
    }

    private void printMainInstruction(Cuarteto sim) {

    }

    private void printVarBool(Cuarteto sim) {
        print(getIf(sim.getResultado().getLexema() + EQUAL + TRUE));
    }

    private void printBool(Cuarteto sim) {
        if (isTrue(sim.getResultado())) {
            print(getIf(TRUE + EQUAL + TRUE));
        } else if (isFalse(sim.getResultado())) {
            print(getIf(FALSE + EQUAL + TRUE));
        }
    }

    private void printOperacion(Cuarteto sim) {
        println(sim.getResultado().getLexema() + SPACE + SIGN_EQUAL + SPACE + sim.getOperando1().getLexema()
                + SPACE + sim.getOperador() + SPACE + sim.getOperando2().getLexema());
    }

    private void printRel(Cuarteto sim) {
        print(sim.getSiguiente());
        print(getIf(sim.getResultado().getLexema()));
    }

    private void printPrint(Cuarteto sim) {
        print(sim.getComponentes());
        if (TablaTipos.getInstance().isBoolean(sim.getResultado().getToken())) {
            if (sim.getResultado().getLexema().equals(TRUE_VAL)) {
                println(PRINT + SPACE + TRUE);
            } else if (sim.getResultado().getLexema().equals(FALSE_VAL)) {
                println(PRINT + SPACE + FALSE);
            }
        } else {
            println(PRINT + SPACE + sim.getResultado().getLexema());
        }
    }

    private void printPrintln(Cuarteto sim) {
        print(sim.getComponentes());
        if (TablaTipos.getInstance().isBoolean(sim.getResultado().getToken())) {
            if (sim.getResultado().getLexema().equals(TRUE_VAL)) {
                println(PRINTLN + SPACE + TRUE);
            } else if (sim.getResultado().getLexema().equals(FALSE_VAL)) {
                println(PRINTLN + SPACE + FALSE);
            }
        } else {
            println(PRINTLN + SPACE + sim.getResultado().getLexema());
        }
    }

    private void printScanString(Cuarteto sim) {
        print(sim.getComponentes());
        if (sim.getOperando1() != null) {
            println(SCAN_VALUE + SPACE + sim.getResultado().getLexema() + OPEN_SQR + sim.getOperando1().getLexema() + CLOSE_SQR);
        } else {
            println(SCAN_VALUE + SPACE + sim.getResultado().getLexema());
        }
    }

    private void printScanNumber(Cuarteto sim) {
        print(sim.getComponentes());
        if (sim.getOperando1() != null) {
            println(SCAN_VALUE + SPACE + sim.getResultado().getLexema() + OPEN_SQR + sim.getOperando1().getLexema() + CLOSE_SQR);
        } else {
            println(SCAN_VALUE + SPACE + sim.getResultado().getLexema());
        }
    }

    private void printCiclo(Cuarteto sim) {
        println(getEtiquetaPrint(sim.getOperando1()));
        if (sim.getComponentes() != null) {
            print(sim.getComponentes());
        }
        printInstruction(sim.getRestriccion());
        if (sim.getAuxiliar() != null) {
            if (isNot(sim.getAuxiliar().getResultado().getLexema())) {
                println(GOTO + sim.getResultado().getLexema());
                println(GOTO + sim.getOperando2().getLexema());
            } else {
                println(GOTO + sim.getOperando2().getLexema());
                println(GOTO + sim.getResultado().getLexema());
            }
        } else {
            println(GOTO + sim.getOperando2().getLexema());
            println(GOTO + sim.getResultado().getLexema());
        }
        //println(RETURN_VALUE);
    }

    private void printAsign(Cuarteto sim) {
        print(sim.getComponentes());
        println(sim.getResultado().getLexema() + SPACE + SIGN_EQUAL + SPACE + sim.getOperando1().getLexema());
    }

    private void printAsignBool(Cuarteto sim) {
        print(sim.getComponentes());
        if (isTrue(sim.getOperando1())) {
            println(sim.getResultado().getLexema() + SPACE + SIGN_EQUAL + SPACE + TRUE);
        } else if (isFalse(sim.getOperando1())) {
            println(sim.getResultado().getLexema() + SPACE + SIGN_EQUAL + SPACE + FALSE);
        } else {
            println(sim.getResultado().getLexema() + SPACE + SIGN_EQUAL + SPACE + sim.getOperando1().getLexema());
        }
    }

    private void printIf(Cuarteto sim) {
        println(getEtiquetaPrint(sim.getOperando1()));
        print(sim.getComponentes());
        println(GOTO + SPACE + sim.getResultado().getLexema());
    }

    private void printElseIf(Cuarteto sim) {
        println(getEtiquetaPrint(sim.getOperando1()));
        print(sim.getComponentes());
        println(GOTO + SPACE + sim.getResultado().getLexema());
    }

    private void printElse(Cuarteto sim) {

    }

    private void printWhile(Cuarteto sim) {
        println(getEtiquetaPrint(sim.getOperando1()));
        print(sim.getComponentes());
        println(GOTO + sim.getOperando2().getLexema());
        //println(RETURN_VALUE);
        println(getEtiquetaPrint(sim.getResultado()));
        //println(RETURN_VALUE);
    }

    private void printFor(Cuarteto sim) {
        println(getEtiquetaPrint(sim.getOperando1()));
        print(sim.getRestriccion());
        print(sim.getComponentes());
        println(GOTO + sim.getOperando2().getLexema());
        println(getEtiquetaPrint(sim.getResultado()));
        println(RETURN_VALUE);
    }

    private void printDo(Cuarteto sim) {
        println(getEtiquetaPrint(sim.getOperando1()));
        println(GOTO + sim.getOperando2().getLexema());
        println(getEtiquetaPrint(sim.getResultado()));
        println(RETURN_VALUE);
    }

    private void printArrayPosition(Cuarteto sim) {
        printLista(sim.getOperando1().getDimensiones());
        print(sim.getComponentes());
        println(sim.getResultado().getLexema() + SPACE + SIGN_EQUAL + SPACE + sim.getOperando1().getLexema()
                + SPACE + OPEN_SQR + sim.getOperando2().getLexema() + CLOSE_SQR);
    }

    private void printArrayAsign(Cuarteto sim) {
        printLista(sim.getOperando1().getDimensiones());
        print(sim.getAuxiliar().getComponentes());
        print(sim.getComponentes());
        if (isTrue(sim.getResultado()) || isFalse(sim.getResultado())) {
            println(sim.getOperando1().getLexema() + SPACE + OPEN_SQR + sim.getAuxiliar().getOperando1().getLexema() + CLOSE_SQR
                    + SPACE + SIGN_EQUAL + SPACE + getValueTrueOrFalse(sim.getResultado().getLexema()));
        } else {
            println(sim.getOperando1().getLexema() + SPACE + OPEN_SQR + sim.getAuxiliar().getOperando1().getLexema() + CLOSE_SQR
                    + SPACE + SIGN_EQUAL + SPACE + sim.getResultado().getLexema());
        }
    }

    private void printLista(LinkedList<Cuarteto> cuartetos) {
        cuartetos.forEach((cuarteto) -> {
            print(cuarteto);
        });
    }

    private void printSubprogram(Cuarteto sim) {
        println(getEtiquetaPrint(sim.getOperando2()));
        print(sim.getComponentes());
    }

    private void printSubprogramCall(Cuarteto sim) {
        int size = 0;
        if (sim.getComponentes() != null) {
            Cuarteto componentes = sim.getComponentes();
            Cuarteto parametros = sim.getAuxiliar();
            while (componentes != null) {
                print(componentes.getComponentes());
                componentes = componentes.getSiguiente();
                size++;
            }
            print(parametros);
        }
        println(sim.getResultado().getLexema() + SPACE + SIGN_EQUAL + SPACE + CALL + SPACE + sim.getOperando1().getLexema() + COMMA + SPACE + size);
    }

    private void printReturn(Cuarteto sim) {
        print(sim.getComponentes());
        if (sim.getResultado() != null) {
            if (isTrue(sim.getResultado()) || isFalse(sim.getResultado())) {
                println(RETURN_VALUE + SPACE + getValueTrueOrFalse(sim.getResultado().getLexema()));
            } else {
                println(RETURN_VALUE + SPACE + sim.getResultado().getLexema());
            }
        } else {
            println(RETURN_VALUE);
        }
    }

    private void printParametro(Cuarteto sim) {
        if (isTrue(sim.getResultado()) || isFalse(sim.getResultado())) {
            println(PARAM_VALUE + SPACE + getValueTrueOrFalse(sim.getResultado().getLexema()));
        } else {
            println(PARAM_VALUE + SPACE + sim.getResultado().getLexema());
        }
    }

    private String getValueTrueOrFalse(String value) {
        if (value.equals(TRUE_VAL)) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    private void print(String print) {
        manejadorAreaTexto.printTerminal(print);
    }

    private void println(String print) {
        manejadorAreaTexto.printTerminal(print + SALTO_LN);
    }

    public void printError(String error) throws Exception {
        throw new Exception(error);
    }

    public void printInAreaTexto(String error) {
        manejadorAreaTexto.printTerminal(error);
    }

    public void printSintaxError(Symbol sim) {
        this.noError = false;
        if (sim.value != null) {
            errorSintactico = SALTO_LN + "Linea: " + sim.left + SALTO_LN + "Columna: " + sim.right + SALTO_LN + "Token: " + sim.value + SALTO_LN;
        } else {
            errorSintactico = SALTO_LN + "Linea: " + sim.left + SALTO_LN + "Columna: " + sim.right + SALTO_LN + "Token sin ingresar" + SALTO_LN;
        }
    }

    public void printSintaxError(String error) /*throws Exception*/ {
        this.noError = false;
        printInAreaTexto("----ERROR----\nTipo de error: Sintactico" + SALTO_LN + error + errorSintactico);
        //throw new Exception("Error Sintactico" + SALTO_LN + error + errorSintactico);
    }

    private boolean existSubprogram(String var) {
        return tablaSimbolos.existSubprogram(var);
    }

    private Simbolo getSubprogram(String var) {
        return tablaSimbolos.getSubprogram(var);
    }

    private boolean existGlobalArray(String var) {
        return tablaSimbolos.existGlobalArray(var);
    }

    private Simbolo getGlobalArray(String var) {
        Simbolo sim = tablaSimbolos.getGlobalArray(var);
        sim.setLexema(var);
        return sim;
    }

    private boolean existGlobalVariable(String var) {
        return tablaSimbolos.existGlobalVariable(var);
    }

    private Simbolo getGlobalVariable(String var) {
        Simbolo sim = tablaSimbolos.getGlobalVariable(var);
        sim.setLexema(var);
        return sim;
    }

    private boolean existGlobalVariableOrArray(String var) {
        return tablaSimbolos.existGlobalVariableOrArray(var);
    }

    private boolean existLocalArray(String var) {
        return tablaSimbolos.existLocalArray(var, subprogramaActual);
    }

    private Simbolo getLocalArray(String var, String subprogram) {
        Simbolo sim = tablaSimbolos.getLocalArray(var, subprogram);
        sim.setLexema(var);
        return sim;
    }

    private boolean existLocalVariableOrParametro(String var) {
        return tablaSimbolos.existLocalVariableOrParametro(var, subprogramaActual);
    }

    private boolean existLocalVariableParametroOrArray(String var) {
        return tablaSimbolos.existLocalVariableParametroOrArray(var, subprogramaActual);
    }

    private Simbolo getLocalVariable(String var, String subprogram) {
        return tablaSimbolos.getLocalVariable(var, subprogram);
    }

    private String getLocalName(String var) {
        //return resultSubprogramActual + subprogramaActual + var;
        return subprogramaActual + var;
    }

    private String getSubprogramName() {
        return resultSubprogramActual + subprogramaActual;
    }

    private boolean existSimbol(String var) {
        return tablaSimbolos.existSimbol(var);
    }

    public Cuarteto getSubprograma(String name, Cuarteto body) {
        if (TablaTipos.getInstance().isVoid(tablaSimbolos.getSubprogram(name).getToken())) {
            if (body != null) {
                getLastCuarteto(body).setSiguiente(getVoidReturn());
            } else {
                body = getVoidReturn();
            }
        }
        return new CuartetoBuilder().operador(SUBPROGRAM).operando2(getSimbolString(name))
                .resultado(tablaSimbolos.getSubprogram(name)).componentes(body).build();
    }

    private Cuarteto getVoidReturn() {
        return new CuartetoBuilder().operador(RETURN).build();
    }

    private boolean isNot(String not) {
        return not.equals(NOT);
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    //public void setEtiqueta
    public void setResultSubprogramActual(String resultSubprogramActual) {
        this.resultSubprogramActual = resultSubprogramActual;
    }

    public void setSubprogramaActual(String subprogramaActual) {
        this.subprogramaActual = subprogramaActual;
    }

    public TablaSimbolos getTablaSimbolos() {
        return tablaSimbolos;
    }

    public int getContadorEtq() {
        return contadorEtq;
    }

    public String getEtiquetaResult() {
        return etiquetaResult;
    }

    public String getSubprogramaActual() {
        return subprogramaActual;
    }

    public String getErrorSintactico() {
        return errorSintactico;
    }

    public boolean isGlobal() {
        return global;
    }

    public boolean isNoError() {
        return noError;
    }

    public void setNoError(boolean noError) {
        this.noError = noError;
    }

    public final static String SIGN_EQUAL = "=";
    public final static String SALTO_LN = "\n";
    public final static String TRUE = "1";
    public final static String TRUE_VAL = "TRUE";
    public final static String FALSE = "0";
    public final static String FALSE_VAL = "FALSE";
    public final static String GOTO = "goto ";
    public final static String IF_AB = "if (";
    public final static String IF_CE = ") ";
    public final static String OPEN_SQR = "[";
    public final static String CLOSE_SQR = "]";
    public final static String COLON = ":";
    public final static String COMMA = ",";
    public final static String T = "t";
    public final static String ETQ = "et";
    public final static String ARRAY = "ARRAY";
    public final static String ARRAY_ASIGN = "ARRAY<-";
    public final static String PRINT = "PRINT";
    public final static String PRINTLN = "PRINTLN";
    public final static String SCANS = "SCANS";
    public final static String SCANN = "SCANN";
    public final static String SCAN_VALUE = "scan";
    public final static String ASSIGN = "<-";
    public final static String ASSIGN_BOOL = "<-BOOL";
    public final static String IF = "IF";
    public final static String ELSEIF = "ELSEIF";
    public final static String ELSE = "ELSE";
    public final static String WHILE = "WHILE";
    public final static String FOR = "FOR";
    public final static String DO = "DO";
    public final static String AND = "AND";
    public final static String OR = "OR";
    public final static String NOT = "NOT";
    public final static String MULTI = "*";
    public final static String DIVISION = "/";
    public final static String MODULE = "%";
    public final static String PLUS = "+";
    public final static String MINUS = "-";
    public final static String BOOL = "BOOL";
    public final static String VAR_BOOL = "VARBOOL";
    public final static String SUBPROGRAM_CALL = "SUBPROGRAM_CALL";
    public final static String PARAM = "PARAM";
    public final static String PARAM_VALUE = "param";
    public final static String CALL = "call";
    public final static String SUBPROGRAM = "SUBPROGRAM";
    public final static String RETURN = "RETURN";
    public final static String RETURN_VALUE = "return";
    public final static String SCAN = "SCAN";
    public final static String LOWER_EQ = "<=";
    public final static String HIGHER_EQ = ">=";
    public final static String LOWER = "<";
    public final static String HIGHER = ">";
    public final static String NOT_EQUAL = "!=";
    public final static String EQUAL = "==";
    public final static String CICLO = "CICLO";
    public final static String SPACE = " ";
    public final static long TIPE_INT = (long) Math.pow(2, 31);
    public final static long TIPE_BYTE = (long) Math.pow(2, 16);
    public final static long TIPE_CHAR = (long) Math.pow(2, 8);
    public final static int ERROR_NUM = 1;
    public final static int ERROR_STRING = 2;
    public final static int ERROR_BOOL = 3;
    public final static int ERROR_VOID = 4;
    public final static int VAR_GLOBAL = 1;
    public final static int VAR_LOCAL = 2;
    public final static int PARAMETRO = 3;
    public final static int SUBPROGRAMA = 4;
    /*private AccionParser acciones = null;
    private AsignadorValores asignador = null;
    private DeclaradorValores declarador = null;
    private ProcesadorFunciones procesadorFunciones = null;
    private ProcesadorNumerico operador = null;
    private RetornadorValores retornador = null;*/
    //private LinkedList <String>
    private int contador = 0;
    private int contadorEtq = 0;
    private int contadorSubprograma = 0;
    private ManejadorAreaTexto manejadorAreaTexto = null;
    private TablaSimbolos tablaSimbolos = null;
    private String etiquetaResult = null;
    private String resultSubprogramActual = "";
    public String subprogramaActual = "";
    private String errorSintactico = null;
    public boolean global = true;
    private boolean noError = true;
}
