/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo3d.backend.manejadores;

import codigo3d.backend.objetos.*;
import java.util.LinkedList;
import java.util.Stack;

/**
 *
 * @author fabricioRG
 */
public class ManejadorParser {

    private ManejadorAreaTexto manejadorAreaTexto = null;
    private TablaSimbolos tablaSimbolos = null;

    public ManejadorParser(ManejadorAreaTexto mi) {
        this.manejadorAreaTexto = mi;
        this.tablaSimbolos = new TablaSimbolos();
        setEtiquetaResult(getSimpleEtiqueta());
    }

    // --------------------ANALIZADOR NO 1------------------------ //
    public void setSubprogramDecl(Tipo tipo, String var, int fila, int columna) throws Exception {
        if (!tablaSimbolos.existSubprogram(var)) {
            tablaSimbolos.setSubprogram(tipo, var);
            subprogramaActual = var;
        } else {
            throw new Exception("Declaracion de Subprograma" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().lexema(var).fila(fila).token(tipo).columna(columna).build())
                    + SALTO_LN + "Subprograma ya declarado");
        }
    }

    public void setMainDecl(String var) {
        tablaSimbolos.setSubprogram(null, BOOL);
    }

    public void setParametroDecl(Tipo tipo, String name, int fila, int columna) throws Exception {
        if (!TablaTipos.getInstance().isVoid(tipo)) {
            if (!tablaSimbolos.existLocalVariableOrParametro(name, subprogramaActual)) {
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
            if (!global) {
                if (!tablaSimbolos.existLocalVariableOrParametro(cuarteto.getOperador(), subprogramaActual)) {
                    if (cuarteto.getComponentes() != null) {
                        if (TablaTipos.getInstance().isCompatible(cuarteto.getComponentes().getResultado().getToken(), tipo)) {
                            tablaSimbolos.setLocalVariable(tipo, cuarteto.getOperador(), subprogramaActual);
                        } else {
                            cuarteto.getOperando1().setToken(tipo);
                            throw new Exception("Asignacion" + SALTO_LN + getInvalidAsign(cuarteto.getOperando1(),
                                    cuarteto.getComponentes().getResultado().getToken()));
                        }
                    } else {
                        tablaSimbolos.setLocalVariable(tipo, cuarteto.getOperador(), subprogramaActual);
                    }
                } else {
                    cuarteto.getOperando1().setToken(tipo);
                    throw new Exception("Declaracion de variable local" + SALTO_LN + getValorInfo(cuarteto.getOperando1())
                            + "Variable o Parametro ya declarada en subprograma: " + subprogramaActual);
                }
            } else {
                if (!tablaSimbolos.existGlobalVariable(cuarteto.getOperador())) {
                    tablaSimbolos.setGlobalVariable(tipo, cuarteto.getOperador());
                } else {
                    cuarteto.getOperando1().setToken(tipo);
                    throw new Exception("Declaracion de variable global" + SALTO_LN + getValorInfo(cuarteto.getOperando1())
                            + "Variable global ya declarada");
                }
            }
        } else {
            throw new Exception("Declaracion de Variable" + SALTO_LN + getInvalidAsignVoid(cuarteto.getOperando1()));
        }
    }

    // --------------------ANALIZADOR NO 2------------------------ //
    private String setEtiquetaResult(String et) {
        String anterior = etiquetaResult;
        etiquetaResult = et;
        return anterior;
    }

    private String getResult() {
        return T + ++contador;
    }

    private String getEtiqueta() {
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
        return new SimboloBuilder().lexema(num).token(TablaTipos.getInstance().getDouble()).build();
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

    private String getValorInfo(Simbolo sim) {
        return "Lexema: " + sim.getLexema() + SALTO_LN + "Token: " + sim.getToken().getNombre() + SALTO_LN
                + "Fila: " + sim.getFila() + SALTO_LN + "Columna: " + sim.getColumna() + SALTO_LN;
    }

    private String getInvalidSimbol(Simbolo sim1, Simbolo sim2, int error) {
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

    private String getInvalidAsign(Simbolo sim1, Tipo tipo) {
        return "==Variable para asignacion==" + SALTO_LN + getValorInfo(sim1) + "===Valor de asignacion==="
                + SALTO_LN + "Asignacion incopatible (tipo): " + tipo.getNombre() + SALTO_LN + SALTO_LN + sim1.getToken().getNombre()
                + " incopatible con " + tipo.getNombre();
    }
    
    private String getInvalidAsignVoid(Simbolo sim){
        sim.setToken(TablaTipos.getInstance().getVoid());
        return getValorInfo(sim) + "Asignacion de tipo \"void\" no posible para variables y parametros";
    }

    private boolean isNotTypeNumber(Tipo token1, Tipo token2) {
        return TablaTipos.getInstance().isString(token1) || TablaTipos.getInstance().isString(token2)
                || TablaTipos.getInstance().isBoolean(token1) || TablaTipos.getInstance().isBoolean(token2);
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
        if (tablaSimbolos.existSimbol(key)) {
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
        }
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
            return new CuartetoBuilder().operador(not).operando1(sim.getResultado()).
                    resultado(getSimbolBoolean(getResult())).build();
        } else {
            throw new Exception("Variable " + sim.getResultado().getLexema() + " no es de tipo booleana ");
        }
    }

    public Cuarteto getOr(Cuarteto sim1, String or, Cuarteto sim2) throws Exception {
        if (areBoolean(sim1.getResultado(), sim2.getResultado())) {
            Cuarteto sim3 = sim1;
            Cuarteto i = sim1.getSiguiente();
            while (i != null) {
                sim3 = i;
                i = i.getSiguiente();
            }
            setEtiquetaResult(sim3.getOperando2().getLexema());
            sim3.setOperador(or);
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
                sim2.setOperador(or);
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
            Cuarteto sim3 = sim1;
            Cuarteto i = sim1.getSiguiente();
            while (i != null) {
                sim3 = i;
                i = i.getSiguiente();
            }
            sim2.setResultado(sim3.getResultado());
            sim3.setOperador(and);
            setMinusEtiqueta();
            sim3.setSiguiente(sim2);
            if (sim2.getOperador() == null) {
                sim2.setOperador(and);
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
        return new CuartetoBuilder().operador(print).resultado(sim.getResultado()).build();
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
        if (!tablaSimbolos.existSimbol(cuarteto.getOperador())) {
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

    /*private Cuarteto asign(Cuarteto cuarteto, Tipo tipo) throws Exception {
        if (!tablaSimbolos.existSimbol(cuarteto.getOperador())) {
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
    }*/
    public Cuarteto asignByToken(String token, Cuarteto expresion) throws Exception {
        if (tablaSimbolos.existSimbol(token)) {
            Simbolo sim = tablaSimbolos.getSimbol(token);
            Cuarteto last = getLastCuarteto(expresion);
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
        }
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

    public Cuarteto setArray(Cuarteto actual, Tipo tipo) {
        Cuarteto i = actual;
        Cuarteto j = i;
        while (i != null) {
            j = i;
            setSimpleArray(i, tipo);
            i = i.getSiguiente();
        }
        return j;
    }

    public Cuarteto getVariable(String var, int fila, int columna) throws Exception {
        if (tablaSimbolos.existSimbol(var)) {
            Simbolo sim = tablaSimbolos.getSimbol(var);
            sim.setFila(fila);
            sim.setColumna(columna);
            return new CuartetoBuilder().resultado(sim).build();
        } else {
            throw new Exception("Variable <" + var + "> no existente o no declarada");
        }
    }

    public void setSimpleArray(Cuarteto sim, Tipo tipo) {
        tablaSimbolos.setSimbol(sim.getResultado().getLexema(), tipo);
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

    public void setOperadorAll(Cuarteto head) {
        Cuarteto sim = head;
        while (sim != null) {
            sim.setOperador(CICLO);
            sim = sim.getSiguiente();
        }
    }

    public Cuarteto whileOperacion(String whil, Cuarteto restriccion, Cuarteto instructions) throws Exception {
        if (TablaTipos.getInstance().isBoolean(restriccion.getResultado().getToken())) {
            Cuarteto last = getLastCuarteto(restriccion);
            etiquetaResult = getEtiqueta();
            Cuarteto result = new CuartetoBuilder().operador(whil).operando1(last.getOperando2()).
                    operando2(restriccion.getOperando1()).resultado(last.getResultado()).componentes(instructions).build();
            setOperadorAll(restriccion);
            return setAtLast(restriccion, result);
        } else {
            throw new Exception("Variable " + restriccion.getResultado().getLexema() + " no es de tipo booleana");
        }
    }

    public Cuarteto doWhileOperacion(String doW, Cuarteto restriccion, Cuarteto instructions) throws Exception {
        if (TablaTipos.getInstance().isBoolean(restriccion.getResultado().getToken())) {
            Cuarteto last = getLastCuarteto(restriccion);
            etiquetaResult = getEtiqueta();
            Cuarteto result = new CuartetoBuilder().operador(doW).operando1(last.getOperando2()).
                    operando2(restriccion.getOperando1()).resultado(last.getResultado()).build();
            setOperadorAll(restriccion);
            restriccion.setComponentes(instructions);
            return setAtLast(restriccion, result);
        } else {
            throw new Exception("Variable " + restriccion.getResultado().getLexema() + " no es de tipo booleana");
        }
    }

    public Cuarteto getArrayPosition(String var, Cuarteto position) {
        if (tablaSimbolos.existSimbol(var)) {
            Simbolo sim = tablaSimbolos.getSimbol(var);
            return new CuartetoBuilder().operador(ARRAY).resultado(sim).componentes(position).build();
        }
        return null;
    }

    public Cuarteto setArrayAsign(Cuarteto arrayPosition, Cuarteto expresion) throws Exception {
        if (TablaTipos.getInstance().isCompatible(arrayPosition.getResultado().getToken(), expresion.getResultado().getToken())) {
            if (TablaTipos.getInstance().isCompatible(expresion.getResultado().getToken(), arrayPosition.getResultado().getToken())) {
                Cuarteto last = getLastCuarteto(arrayPosition);
                Cuarteto last2 = getLastCuarteto(expresion);
                return new CuartetoBuilder().operador(ARRAY_ASIGN).resultado(last2.getResultado()).
                        operando1(last.getResultado()).componentes(expresion).auxiliar(arrayPosition).build();
            } else {
                throw new Exception("Variable " + arrayPosition.getResultado().getLexema() + " no es compatible");
            }
        } else {
            throw new Exception("No es compatible");
        }
    }

    public Cuarteto forOperacion(Cuarteto asignacion, Cuarteto opBol, String signo, String entero, Cuarteto instructions, int fila, int columna) throws Exception {
        if (TablaTipos.getInstance().isBoolean(opBol.getResultado().getToken())) {
            Cuarteto last = getLastCuarteto(opBol);
            Cuarteto instruccion = null;
            Cuarteto num = getCuartetoNum(getSimbolEntero(entero, fila, columna));
            Simbolo simResult = new SimboloBuilder().lexema(getResult()).token(getTipo(num.getResultado(), num.getResultado())).build();
            if (signo.equals(PLUS)) {
                instruccion = new CuartetoBuilder().operador(PLUS).operando1(asignacion.getResultado()).operando2(num.getResultado()).resultado(simResult).build();
            } else if (signo.equals(MINUS)) {
                instruccion = new CuartetoBuilder().operador(MINUS).operando1(asignacion.getResultado()).operando2(num.getResultado()).resultado(simResult).build();
            }
            Cuarteto asign = asignByToken(asignacion.getResultado().getLexema(), instruccion);
            etiquetaResult = getEtiqueta();
            Cuarteto result = new CuartetoBuilder().operador(FOR).operando1(last.getOperando2()).
                    operando2(opBol.getOperando1()).resultado(last.getResultado()).componentes(instructions).restriccion(asign).build();
            setOperadorAll(opBol);
            setAtLast(opBol, result);
            return setAtLast(asignacion, opBol);
        } else {
            throw new Exception();
        }
    }

    public Cuarteto getInstructions(Cuarteto e1, Cuarteto e2) {
        return setAtLast(e1, e2);
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
                case ARRAY:
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
        println(sim.getResultado().getLexema() + SIGN_EQUAL + sim.getOperando1().getLexema()
                + sim.getOperador() + sim.getOperando2().getLexema());
    }

    private void printRel(Cuarteto sim) {
        print(sim.getSiguiente());
        print(getIf(sim.getResultado().getLexema()));
    }

    private void printPrint(Cuarteto sim) {
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

    private void printCiclo(Cuarteto sim) {
        println(getEtiquetaPrint(sim.getOperando1()));
        if (sim.getComponentes() != null) {
            print(sim.getComponentes());
        }
        printInstruction(sim.getRestriccion());
        println(GOTO + sim.getOperando2().getLexema());
        println(GOTO + sim.getResultado().getLexema());
    }

    private void printAsign(Cuarteto sim) {
        print(sim.getComponentes());
        println(sim.getResultado().getLexema() + SIGN_EQUAL + sim.getOperando1().getLexema());
    }

    private void printAsignBool(Cuarteto sim) {
        print(sim.getComponentes());
        if (isTrue(sim.getOperando1())) {
            println(sim.getResultado().getLexema() + SIGN_EQUAL + TRUE);
        } else {
            println(sim.getResultado().getLexema() + SIGN_EQUAL + FALSE);
        }
    }

    private void printWhile(Cuarteto sim) {
        println(getEtiquetaPrint(sim.getOperando1()));
        print(sim.getComponentes());
        println(GOTO + sim.getOperando2().getLexema());
        println(getEtiquetaPrint(sim.getResultado()));
    }

    private void printFor(Cuarteto sim) {
        println(getEtiquetaPrint(sim.getOperando1()));
        print(sim.getRestriccion());
        print(sim.getComponentes());
        println(GOTO + sim.getOperando2().getLexema());
        println(getEtiquetaPrint(sim.getResultado()));
    }

    private void printDo(Cuarteto sim) {
        println(getEtiquetaPrint(sim.getOperando1()));
        println(GOTO + sim.getOperando2().getLexema());
        println(getEtiquetaPrint(sim.getResultado()));
    }

    private void printArrayAsign(Cuarteto sim) {
        print(sim.getAuxiliar());
        print(sim.getComponentes());
        println(sim.getResultado().getLexema() + SIGN_EQUAL + sim.getOperando1().getLexema());
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

    public void printSintaxError(String error) {
        manejadorAreaTexto.printTerminal(error + SALTO_LN);
    }

    public void setGlobal(boolean global) {
        this.global = global;
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
    public final static String COLON = ":";
    public final static String T = "t";
    public final static String ETQ = "et";
    public final static String ARRAY = "ARRAY";
    public final static String ARRAY_ASIGN = "ARRAY<-";
    public final static String PRINT = "PRINT";
    public final static String PRINTLN = "PRINTLN";
    public final static String ASSIGN = "<-";
    public final static String ASSIGN_BOOL = "<-BOOL";
    public final static String WHILE = "WHILE";
    public final static String FOR = "FOR";
    public final static String DO = "DO";
    public final static String AND = "AND";
    public final static String OR = "OR";
    public final static String MULTI = "*";
    public final static String DIVISION = "/";
    public final static String MODULE = "%";
    public final static String PLUS = "+";
    public final static String MINUS = "-";
    public final static String BOOL = "BOOL";
    public final static String VAR_BOOL = "VARBOOL";
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
    private int contador = 0;
    private int contadorEtq = 1;
    private String etiquetaResult = null;
    public String subprogramaActual = "";
    public boolean global = true;
}
