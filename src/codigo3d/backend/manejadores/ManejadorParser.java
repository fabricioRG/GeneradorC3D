/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo3d.backend.manejadores;

import codigo3d.backend.objetos.*;
import java.util.Stack;

/**
 *
 * @author fabricioRG
 */
public class ManejadorParser {

    private int contador = 0;
    private int contadorEtq = 1;
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
    public final static String LOWER_EQ = "<=";
    public final static String HIGHER_EQ = ">=";
    public final static String LOWER = "<";
    public final static String HIGHER = ">";
    public final static String NOT_EQUAL = "!=";
    public final static String EQUAL = "==";
    public final static String CICLO = "CICLO";
    private String etiquetaResult = null;

    private ManejadorAreaTexto manejadorAreaTexto = null;
    private TablaSimbolos tablaSimbolos = null;

    public ManejadorParser(ManejadorAreaTexto mi) {
        this.manejadorAreaTexto = mi;
        this.tablaSimbolos = new TablaSimbolos();
        setEtiquetaResult(getSimpleEtiqueta());
    }

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
        return new Simbolo(bool, TablaTipos.getInstance().getBoolean());
    }

    public Simbolo getSimbolBoolean(String bool, int fila, int columna) {
        return new Simbolo(bool, TablaTipos.getInstance().getBoolean(), fila, columna);
    }

    public Simbolo getSimbolInteger(String num, int fila, int columna) {
        Simbolo sim = new Simbolo(num, TablaTipos.getInstance().getInt(), fila, columna);
        return sim;
    }

    public Simbolo getSimbolFloat(String num, int fila, int columna) {
        return new Simbolo(num, TablaTipos.getInstance().getFloat());
    }

    public Simbolo getSimbolDouble(String num, int fila, int columna) {
        return new Simbolo(num, TablaTipos.getInstance().getDouble());
    }

    public Simbolo getSimbolString(String text) {
        return new Simbolo(text, TablaTipos.getInstance().getString());
    }
    
    public Simbolo getSimbolString(String text, int fila, int columna) {
        Simbolo sim = new Simbolo(text, TablaTipos.getInstance().getString(), fila, columna);
        return sim;
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

    public Cuarteto getSumaConcatenacion(Cuarteto sim2, String plus, Cuarteto sim1) {
        if (TablaTipos.getInstance().isString(sim1.getResultado().getToken())
                || TablaTipos.getInstance().isString(sim2.getResultado().getToken())) {
            return new CuartetoBuilder().operador(plus).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(getSimbolString(getResult())).siguiente(setAtLast(sim1, sim2)).build();
        } else if (TablaTipos.getInstance().isBoolean(sim1.getResultado().getToken())
                || TablaTipos.getInstance().isBoolean(sim2.getResultado().getToken())) {
            printError("Concatenacion erronea entre booleano");
        } else {
            Simbolo simResult = new Simbolo(getResult(), getTipo(sim1.getResultado(), sim2.getResultado()));
            return new CuartetoBuilder().operador(plus).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(simResult).siguiente(setAtLast(sim1, sim2)).build();
        }
        return null;
    }

    public Cuarteto getMulti(Cuarteto sim2, String multi, Cuarteto sim1) {
        if (isNotTypeNumber(sim1.getResultado().getToken(), sim2.getResultado().getToken())) {
            printError("Multiplicacion entre numeros no es posible (No son numeros) " + sim2.getResultado().getLexema() + " - " + sim1.getResultado().getLexema());
        } else {
            Simbolo simResult = new Simbolo(getResult(), getTipo(sim1.getResultado(), sim2.getResultado()));
            return new CuartetoBuilder().operador(multi).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(simResult).siguiente(setAtLast(sim1, sim2)).build();
        }
        return null;
    }

    public Cuarteto getDivision(Cuarteto sim2, String division, Cuarteto sim1) {//sim2 <- sim1
        if (isNotTypeNumber(sim1.getResultado().getToken(), sim2.getResultado().getToken())) {
            printError("Division entre numeros no es posible (No son numeros) " + sim2.getResultado().getLexema() + " - " + sim1.getResultado().getLexema());
        } else {
            Simbolo simResult = new Simbolo(getResult(), getTipo(sim1.getResultado(), sim2.getResultado()));
            return new CuartetoBuilder().operador(division).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(simResult).siguiente(setAtLast(sim1, sim2)).build();
        }
        return null;
    }

    public Cuarteto getResta(Cuarteto sim2, String minus, Cuarteto sim1) {
        if (isNotTypeNumber(sim1.getResultado().getToken(), sim2.getResultado().getToken())) {
            printError("Resta entre numeros no es posible (No son numeros) " + sim2.getResultado().getLexema() + " - " + sim1.getResultado().getLexema());
        } else {
            Simbolo simResult = new Simbolo(getResult(), getTipo(sim1.getResultado(), sim2.getResultado()));
            return new CuartetoBuilder().operador(minus).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(simResult).siguiente(setAtLast(sim1, sim2)).build();
        }
        return null;
    }

    public Cuarteto getModulo(Cuarteto sim2, String module, Cuarteto sim1) {
        if (isNotTypeNumber(sim1.getResultado().getToken(), sim2.getResultado().getToken())) {
            printError("Modulo entre numeros no es posible (No son numeros) " + sim2.getResultado().getLexema() + " - " + sim1.getResultado().getLexema());
        } else {
            Simbolo simResult = new Simbolo(getResult(), getTipo(sim1.getResultado(), sim2.getResultado()));
            return new CuartetoBuilder().operador(module).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(simResult).siguiente(setAtLast(sim1, sim2)).build();
        }
        return null;
    }

    public Cuarteto getRelacion(Cuarteto sim2, String rel, Cuarteto sim1) {
        if (!isNotTypeNumber(sim1.getResultado().getToken(), sim2.getResultado().getToken())) {
            if (sim1.getOperador() == null && sim2.getOperador() == null) {
                if (TablaTipos.getInstance().isCompatible(sim2.getResultado().getToken(), sim1.getResultado().getToken())) {
                    String relacion = sim1.getResultado().getLexema() + rel + sim2.getResultado().getLexema();
                    Cuarteto rela = new CuartetoBuilder().operador(rel).operando1(sim2.getResultado()).operando2(sim1.getResultado()).
                            resultado(getSimbolBoolean(relacion)).siguiente(setAtLast(sim1, sim2)).build();
                    return new CuartetoBuilder().operador(rel).operando1(getSimbolString(setEtiquetaResult(getEtiqueta()))).
                            operando2(getSimbolString(getSimpleEtiqueta())).resultado(getSimbolBoolean(getEtiqueta())).restriccion(rela).build();
                } else {
                    printError("Relacion " + rel + " entre numero no es posible (Diferencia de tipos) " + sim2.getResultado().getLexema() + " - " + sim1.getResultado().getLexema());
                }
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
            printError("Relacion erronea entre valores (No son numeros) " + sim2.getResultado().getLexema() + " - " + sim1.getResultado().getLexema());
        }
        return null;
    }

    public Cuarteto getBool(Simbolo sim) {
        Cuarteto simb = getCuartetoBool(sim);
        simb.setOperador(BOOL);
        return new CuartetoBuilder().operador(sim.getLexema()).operando1(getSimbolString(setEtiquetaResult(getEtiqueta()))).
                operando2(getSimbolString(getSimpleEtiqueta())).resultado(getSimbolBoolean(getEtiqueta())).restriccion(simb).build();
    }

    public Cuarteto getVariableRel(String key, int fila, int columna) {
        if (tablaSimbolos.existSimbol(key)) {
            Cuarteto var = getVariable(key, fila, columna);
            var.setOperador(VAR_BOOL);
            if (TablaTipos.getInstance().isBoolean(var.getResultado().getToken())) {
                return new CuartetoBuilder().operando1(getSimbolString(setEtiquetaResult(getEtiqueta()))).
                        operando2(getSimbolString(getSimpleEtiqueta())).resultado(getSimbolBoolean(getEtiqueta())).restriccion(var).build();
            } else {
                printError("Variable " + key + " no puede ser de tipo booleana ");
            }
        } else {
            printError("Variable " + key + " no ha sido declarada");
        }
        return null;
    }

    public Cuarteto getArrayPositionRel(Cuarteto arrayPosition) {
        if (TablaTipos.getInstance().isBoolean(arrayPosition.getResultado().getToken())) {
            return new CuartetoBuilder().operando1(getSimbolString(setEtiquetaResult(getEtiqueta()))).
                    operando2(getSimbolString(getSimpleEtiqueta())).resultado(getSimbolBoolean(getEtiqueta())).restriccion(arrayPosition).build();
        }
        return null;
    }

    public Cuarteto getNot(String not, Cuarteto sim) {
        if (TablaTipos.getInstance().isBoolean(sim.getResultado().getToken())) {
            return new CuartetoBuilder().operador(not).operando1(sim.getResultado()).
                    resultado(getSimbolBoolean(getResult())).build();
        } else {
            printError("Variable " + sim.getResultado().getLexema() + " no es de tipo booleana ");
        }
        return null;
    }

    public Cuarteto getOr(Cuarteto sim1, String or, Cuarteto sim2) {
        if (areBoolean(sim1.getResultado(), sim2.getResultado())) {
            Cuarteto sim3 = sim1;
            Cuarteto i = sim1.getSiguiente();
            while (i != null) {
                sim3 = i;
                i = i.getSiguiente();
            }
            setEtiquetaResult(sim3.getOperando2().getLexema());
            sim3.setOperador(or);
            if (!sim2.getOperador().equals(AND)) {
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
            printError("Relacion " + or + " entre numeros no es posible (Diferencia de tipos) " + sim2.getResultado().getLexema() + " - " + sim1.getResultado().getLexema());
        }
        return null;
    }

    private boolean areBoolean(Simbolo sim1, Simbolo sim2) {
        return TablaTipos.getInstance().isBoolean(sim1.getToken())
                && TablaTipos.getInstance().isBoolean(sim2.getToken());
    }

    public Cuarteto getAnd(Cuarteto sim1, String and, Cuarteto sim2) {
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
            printError("Relacion " + and + " entre numeros no es posible (Diferencia de tipos) " + sim2.getResultado().getLexema() + " - " + sim1.getResultado().getLexema());
        }
        return null;
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
        return new CuartetoBuilder().operador(name).componentes(exp).build();
    }

    public Cuarteto getSimpleAsign(String name) {
        return new CuartetoBuilder().operador(name).build();
    }

    public Cuarteto setAsign(Cuarteto cuarteto, Tipo tipo) {
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

    private Cuarteto asign(Cuarteto cuarteto, Tipo tipo) {
        if (!tablaSimbolos.existSimbol(cuarteto.getOperador())) {
            if (cuarteto.getComponentes() != null) {
                if (TablaTipos.getInstance().isCompatible(cuarteto.getComponentes().getResultado().getToken(), tipo)) {
                    tablaSimbolos.setSimbol(cuarteto.getOperador(), tipo);
                    return new CuartetoBuilder().operador(ASSIGN).componentes(cuarteto.
                            getComponentes()).operando1(cuarteto.getComponentes().getResultado()).resultado(new Simbolo(cuarteto.getOperador(), tipo)).build();
                } else {
                    printError("Asignacion entre tipo y variable no valida");
                }
            } else {
                tablaSimbolos.setSimbol(cuarteto.getOperador(), tipo);
                return new CuartetoBuilder().build();
            }
        } else {
            printError("Variable " + cuarteto.getOperador() + " no existente o no declarada");
        }
        return null;
    }

    public Cuarteto asignByToken(String token, Cuarteto expresion) {
        if (tablaSimbolos.existSimbol(token)) {
            Simbolo sim = tablaSimbolos.getSimbol(token);
            Cuarteto last = getLastCuarteto(expresion);
            if (TablaTipos.getInstance().isCompatible(last.getResultado().getToken(), sim.getToken())) {
                return new CuartetoBuilder().operador(ASSIGN).componentes(expresion).
                        operando1(last.getResultado()).resultado(sim).build();
            } else {
                printError("Variable " + token + " no es compatible con expresion de tipo " + expresion.getResultado().getToken().getNombre());

            }
        } else {
            printError("Variable " + token + " no existente o no declarada");
        }
        return null;
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

    public Cuarteto getVariable(String var, int fila, int columna) {
        if (tablaSimbolos.existSimbol(var)) {
            Simbolo sim = tablaSimbolos.getSimbol(var);
            sim.setFila(fila);
            sim.setColumna(columna);
            return new CuartetoBuilder().resultado(sim).build();
        } else {
            printError("Variable " + var + " no existente o no declarada");
        }
        return null;
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

    public Cuarteto whileOperacion(String whil, Cuarteto restriccion, Cuarteto instructions) {
        if (TablaTipos.getInstance().isBoolean(restriccion.getResultado().getToken())) {
            Cuarteto last = getLastCuarteto(restriccion);
            etiquetaResult = getEtiqueta();
            Cuarteto result = new CuartetoBuilder().operador(whil).operando1(last.getOperando2()).
                    operando2(restriccion.getOperando1()).resultado(last.getResultado()).componentes(instructions).build();
            setOperadorAll(restriccion);
            return setAtLast(restriccion, result);
        } else {
            printError("Variable " + restriccion.getResultado().getLexema() + " no es de tipo booleana");
        }
        return null;
    }

    public void setOperadorAll(Cuarteto head) {
        Cuarteto sim = head;
        while (sim != null) {
            sim.setOperador(CICLO);
            sim = sim.getSiguiente();
        }
    }

    public Cuarteto whileVariable(String whil, String var, Cuarteto instructions, int fila, int columna) {
        Cuarteto sim = getVariableRel(var, fila, columna);
        etiquetaResult = getEtiqueta();
        Cuarteto result = new CuartetoBuilder().operador(whil).operando1(sim.getOperando2()).
                operando2(sim.getOperando1()).resultado(sim.getResultado()).componentes(instructions).build();
        setOperadorAll(sim);
        return setAtLast(sim, result);
    }

    public Cuarteto whileBool(String whil, Simbolo sim, Cuarteto instructions) {
        Cuarteto sim1 = getBool(sim);
        etiquetaResult = getEtiqueta();
        Cuarteto result = new CuartetoBuilder().operador(whil).operando1(sim1.getOperando2()).
                operando2(sim1.getOperando1()).resultado(sim1.getResultado()).componentes(instructions).build();
        setOperadorAll(sim1);
        return setAtLast(sim1, result);
    }

    public Cuarteto doWhileOperacion(String doW, Cuarteto restriccion, Cuarteto instructions) {
        if (TablaTipos.getInstance().isBoolean(restriccion.getResultado().getToken())) {
            Cuarteto last = getLastCuarteto(restriccion);
            etiquetaResult = getEtiqueta();
            Cuarteto result = new CuartetoBuilder().operador(doW).operando1(last.getOperando2()).
                    operando2(restriccion.getOperando1()).resultado(last.getResultado()).build();
            setOperadorAll(restriccion);
            restriccion.setComponentes(instructions);
            return setAtLast(restriccion, result);
        } else {
            printError("Variable " + restriccion.getResultado().getLexema() + " no es de tipo booleana");
        }
        return null;
    }

    public Cuarteto doWhileVariable(String doW, String var, Cuarteto instructions, int fila, int columna) {
        Cuarteto sim = getVariableRel(var, fila, columna);
        etiquetaResult = getEtiqueta();
        Cuarteto result = new CuartetoBuilder().operador(doW).operando1(sim.getOperando2()).
                operando2(sim.getOperando1()).resultado(sim.getResultado()).build();
        setOperadorAll(sim);
        sim.setComponentes(instructions);
        return setAtLast(sim, result);
    }

    public Cuarteto doWhileBool(String doW, Simbolo sim, Cuarteto instructions) {
        Cuarteto sim1 = getBool(sim);
        etiquetaResult = getEtiqueta();
        Cuarteto result = new CuartetoBuilder().operador(doW).operando1(sim1.getOperando2()).
                operando2(sim1.getOperando1()).resultado(sim1.getResultado()).build();
        setOperadorAll(sim1);
        sim1.setComponentes(instructions);
        return setAtLast(sim1, result);
    }

    public Cuarteto getArrayPosition(String var, Cuarteto position) {
        if (tablaSimbolos.existSimbol(var)) {
            Simbolo sim = tablaSimbolos.getSimbol(var);
            return new CuartetoBuilder().operador(ARRAY).resultado(sim).componentes(position).build();
        }
        return null;
    }

    public Cuarteto setArrayAsign(Cuarteto arrayPosition, Cuarteto expresion) {
        if (TablaTipos.getInstance().isCompatible(arrayPosition.getResultado().getToken(), expresion.getResultado().getToken())) {
            if (TablaTipos.getInstance().isCompatible(expresion.getResultado().getToken(), arrayPosition.getResultado().getToken())) {
                Cuarteto last = getLastCuarteto(arrayPosition);
                Cuarteto last2 = getLastCuarteto(expresion);
                return new CuartetoBuilder().operador(ARRAY_ASIGN).resultado(last2.getResultado()).
                        operando1(last.getResultado()).componentes(expresion).auxiliar(arrayPosition).build();
            } else {
                printError("Variable " + arrayPosition.getResultado().getLexema() + " no es compatible");
            }
        } else {
            printError("No es compatible");
        }
        return null;
    }

    public Cuarteto forOperacion(Cuarteto asignacion, Cuarteto opBol, String signo, String entero, Cuarteto instructions, int fila, int columna) {
        if (TablaTipos.getInstance().isBoolean(opBol.getResultado().getToken())) {
            Cuarteto last = getLastCuarteto(opBol);
            Cuarteto instruccion = null;
            Cuarteto num = getCuartetoNum(getSimbolInteger(entero, fila, columna));
            Simbolo simResult = new Simbolo(getResult(), getTipo(num.getResultado(), num.getResultado()));
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
            //Error
        }
        return null;
    }

    public Cuarteto forVariable(Cuarteto asignacion, String var, String signo, String entero, Cuarteto instructions,
            int fila1, int columna1, int fila2, int columna2) {
        Cuarteto sim = getVariableRel(var, fila1, columna1);
        Cuarteto instruccion = null;
        Cuarteto num = getCuartetoNum(getSimbolInteger(entero, fila2, columna2));
        Simbolo simResult = new Simbolo(getResult(), getTipo(num.getResultado(), num.getResultado()));
        if (signo.equals(PLUS)) {
            instruccion = new CuartetoBuilder().operador(PLUS).operando1(asignacion.getResultado()).operando2(num.getResultado()).resultado(simResult).build();
        } else if (signo.equals(MINUS)) {
            instruccion = new CuartetoBuilder().operador(MINUS).operando1(asignacion.getResultado()).operando2(num.getResultado()).resultado(simResult).build();
        }
        Cuarteto asign = asignByToken(asignacion.getResultado().getLexema(), instruccion);
        etiquetaResult = getEtiqueta();
        Cuarteto result = new CuartetoBuilder().operador(FOR).operando1(sim.getOperando2()).
                operando2(sim.getOperando1()).resultado(sim.getResultado()).componentes(instructions).
                auxiliar(asignacion).restriccion(asign).build();
        setOperadorAll(sim);
        setAtLast(sim, result);
        return setAtLast(asignacion, sim);
    }

    public Cuarteto forBool(Cuarteto asignacion, Simbolo bool, String signo, String entero, Cuarteto instructions, int fila, int columna) {
        Cuarteto sim = getBool(bool);
        Cuarteto instruccion = null;
        Cuarteto num = getCuartetoNum(getSimbolInteger(entero, fila, columna));
        Simbolo simResult = new Simbolo(getResult(), getTipo(num.getResultado(), num.getResultado()));
        if (signo.equals(PLUS)) {
            instruccion = new CuartetoBuilder().operador(PLUS).operando1(asignacion.getResultado()).operando2(num.getResultado()).resultado(simResult).build();
        } else if (signo.equals(MINUS)) {
            instruccion = new CuartetoBuilder().operador(MINUS).operando1(asignacion.getResultado()).operando2(num.getResultado()).resultado(simResult).build();
        }
        Cuarteto asign = asignByToken(asignacion.getResultado().getLexema(), instruccion);
        etiquetaResult = getEtiqueta();
        Cuarteto result = new CuartetoBuilder().operador(FOR).operando1(sim.getOperando2()).
                operando2(sim.getOperando1()).resultado(sim.getResultado()).componentes(instructions).
                auxiliar(asignacion).restriccion(asign).build();
        setOperadorAll(sim);
        setAtLast(sim, result);
        return setAtLast(asignacion, sim);
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
        print(getIf(sim.getResultado().getLexema() + SIGN_EQUAL + TRUE));
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
        print(getIf(sim.getOperando1().getLexema() + sim.getOperador() + sim.getOperando2().getLexema()));
    }

    private void printPrint(Cuarteto sim) {
        if (TablaTipos.getInstance().isBoolean(sim.getResultado().getToken())) {
            if (sim.getResultado().getLexema().equals(TRUE_VAL)) {
                println(PRINT + " " + TRUE);
            } else if (sim.getResultado().getLexema().equals(FALSE_VAL)) {
                println(PRINT + " " + FALSE);
            }
        } else {
            println(PRINT + " " + sim.getResultado().getLexema());
        }
    }

    private void printPrintln(Cuarteto sim) {
        if (TablaTipos.getInstance().isBoolean(sim.getResultado().getToken())) {
            if (sim.getResultado().getLexema().equals(TRUE_VAL)) {
                println(PRINTLN + " " + TRUE);
            } else if (sim.getResultado().getLexema().equals(FALSE_VAL)) {
                println(PRINTLN + " " + FALSE);
            }
        } else {
            println(PRINTLN + " " + sim.getResultado().getLexema());
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
        Cuarteto last = getLastCuarteto(sim);
        print(sim.getComponentes());
        println(sim.getResultado().getLexema() + SIGN_EQUAL + sim.getOperando1().getLexema());
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

    public void printError(String error) {
        manejadorAreaTexto.printError(error + SALTO_LN);
    }

}
