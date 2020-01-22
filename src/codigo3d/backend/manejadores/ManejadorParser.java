/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codigo3d.backend.manejadores;

import codigo3d.backend.objetos.*;
import codigo3d.backend.parseractions.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
        this.selectedCPP = mi.isSelectedCPP();
        this.selectedC3D = mi.isSelectedC3D();
    }

    // --------------------ANALIZADOR NO 1------------------------ //
    public void setSubprogramDecl(Tipo tipo, String var, int fila, int columna) throws Exception {
        if (!existSubprogram(var)) {
            tablaSimbolos.setSubprogram(tipo, new SimboloBuilder().lexema(var).fila(fila).columna(columna).build());
            subprogramaActual = var;
        } else {
            throw new Exception("Declaracion de Subprograma" + SALTO_LN
                    + getValorInfo(new SimboloBuilder().lexema(var).fila(fila).token(tipo).columna(columna).build())
                    + SALTO_LN + "Subprograma ya declarado");
        }
    }

    public void setMainDecl(String var, int fila, int columna) {
        tablaSimbolos.setSubprogram(TablaTipos.getInstance().getVoid(), new SimboloBuilder().lexema(var).fila(fila).columna(columna).build());
        subprogramaActual = var;
    }

    public void setParametroDecl(Tipo tipo, String name, int fila, int columna) throws Exception {
        if (!TablaTipos.getInstance().isVoid(tipo)) {
            if (!existLocalVariableOrParametro(name)) {
                tablaSimbolos.setParametro(tipo, new SimboloBuilder().lexema(name).fila(fila).columna(columna).build(), subprogramaActual);
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
                            getTablaSimbolos().setLocalVariable(tipo, cuarteto.getOperando1(), getSubprogramaActual());
                        } else {
                            cuarteto.getOperando1().setToken(tipo);
                            throw new Exception("Asignacion" + ManejadorParser.SALTO_LN + getInvalidAsign(cuarteto.getOperando1(),
                                    cuarteto.getComponentes().getResultado().getToken()));
                        }
                    } else {
                        getTablaSimbolos().setLocalVariable(tipo, cuarteto.getOperando1(), getSubprogramaActual());
                    }
                } else {
                    cuarteto.getOperando1().setToken(tipo);
                    throw new Exception("Declaracion de variable local" + ManejadorParser.SALTO_LN + getValorInfo(cuarteto.getOperando1())
                            + "Variable, Parametro o Arreglo ya declarado en subprograma: " + getSubprogramaActual());
                }
            } else {
                if (!existGlobalVariableOrArray(cuarteto.getOperador())) {
                    getTablaSimbolos().setGlobalVariable(tipo, cuarteto.getOperando1());
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
                tablaSimbolos.setLocalArray(tipo, sim.getResultado(), dimension, subprogramaActual);
            } else {
                sim.getResultado().setToken(tipo);
                throw new Exception("Declaracion de arreglo local" + SALTO_LN + getValorInfo(sim.getResultado())
                        + "Variable, Parametro o Arreglo ya declarada en subprograma: " + subprogramaActual);
            }
        } else {
            if (!existGlobalVariable(sim.getResultado().getLexema())) {
                tablaSimbolos.setGlobalArray(tipo, sim.getResultado(), dimension);
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
        return new SimboloBuilder().lexema(bool).token(TablaTipos.getInstance().getBoolean())
                .categoria(tablaSimbolos.getCategoriaBool()).fila(fila).columna(columna).build();
    }

    public Simbolo getSimbolEntero(String num, int fila, int columna) {
        long value = Long.parseLong(num);
        Simbolo sim = new SimboloBuilder().lexema(num).token(TablaTipos.getInstance().getInt())
                .categoria(tablaSimbolos.getCategoriaNumero()).fila(fila).columna(columna).build();
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
        return new SimboloBuilder().lexema(num.substring(0, num.length() - 1))
                .categoria(tablaSimbolos.getCategoriaNumero()).token(TablaTipos.getInstance().getFloat()).build();
    }

    public Simbolo getSimbolDouble(String num, int fila, int columna) {
        return new SimboloBuilder().lexema(num).token(TablaTipos.getInstance().getDouble())
                .categoria(tablaSimbolos.getCategoriaNumero()).fila(fila).columna(columna).build();
    }

    public Simbolo getSimbolString(String text) {
        return new SimboloBuilder().lexema(text).token(TablaTipos.getInstance().getString()).build();
    }

    public Simbolo getSimbolError(String text) {
        return new SimboloBuilder().lexema(text).token(TablaTipos.getInstance().getError()).build();
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

    private String getNameWithoutPosition(Simbolo var) {
        if (var.getLexema() != null) {
            String result = var.getLexema();
            if (tablaSimbolos.isAmbitoLocal(var.getAmbito())) {
                if (result.contains(SEPARATOR)) {
                    return result.substring(0, result.lastIndexOf(SEPARATOR));
                } else {
                    return result;
                }
            } else {
                return result;
            }
        }
        return null;
    }

    public String getValorInfo(Simbolo sim) {
        String info = "";
        if (sim.getLexema() != null) {
            info = "Lexema: " + getNameWithoutPosition(sim) + SALTO_LN;
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

    public Cuarteto getTerminalDeclaration(String name, Tipo tipo) {
        String nameTerminal;
        if (!global) {
            nameTerminal = getLocalName(name);
        } else {
            nameTerminal = name;
        }
        return new CuartetoBuilder().operador(DECLARATION).resultado(new SimboloBuilder()
                .lexema(nameTerminal).token(tipo).categoria(tablaSimbolos.getCategoriaVariable())
                .ambito(tablaSimbolos.getAmbitoLocal()).build()).build();
    }

    public Cuarteto getSumaConcatenacion(Cuarteto sim2, String plus, Cuarteto sim1) throws Exception {
        if (TablaTipos.getInstance().isString(sim1.getResultado().getToken())
                || TablaTipos.getInstance().isString(sim2.getResultado().getToken())) {
            Cuarteto terminal = getTerminalDeclaration(getResult(), TablaTipos.getInstance().getString());
            return new CuartetoBuilder().operador(plus).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(terminal.getResultado()).auxiliar(terminal).siguiente(setAtLast(sim1, sim2)).build();
        } else if (TablaTipos.getInstance().isNumber(sim1.getResultado().getToken())
                && TablaTipos.getInstance().isNumber(sim2.getResultado().getToken())) {
            Cuarteto terminal = getTerminalDeclaration(getResult(), getTipo(sim1.getResultado(), sim2.getResultado()));
            return new CuartetoBuilder().operador(plus).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(terminal.getResultado()).auxiliar(terminal).siguiente(setAtLast(sim1, sim2)).build();
        } else {
            Cuarteto terminal = getTerminalDeclaration(getResult(), TablaTipos.getInstance().getString());
            return new CuartetoBuilder().operador(plus).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(getSimbolError(terminal.getResultado().getLexema())).auxiliar(terminal).siguiente(setAtLast(sim1, sim2)).build();
        }
    }

    public Cuarteto getMulti(Cuarteto sim2, String multi, Cuarteto sim1) throws Exception {
        if (isNotTypeNumber(sim1.getResultado().getToken(), sim2.getResultado().getToken())) {
            throw new Exception("Multiplicacion " + SALTO_LN + getInvalidSimbol(sim2.getResultado(), sim1.getResultado(), ERROR_NUM));
        } else {
            Cuarteto terminal = getTerminalDeclaration(getResult(), getTipo(sim1.getResultado(), sim2.getResultado()));
            return new CuartetoBuilder().operador(multi).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(terminal.getResultado()).auxiliar(terminal).siguiente(setAtLast(sim1, sim2)).build();
        }
    }

    public Cuarteto getDivision(Cuarteto sim2, String division, Cuarteto sim1) throws Exception {//sim2 <- sim1
        if (isNotTypeNumber(sim1.getResultado().getToken(), sim2.getResultado().getToken())) {
            throw new Exception("Division ( / )" + SALTO_LN + getInvalidSimbol(sim2.getResultado(), sim1.getResultado(), ERROR_NUM));
        } else {
            Cuarteto terminal = getTerminalDeclaration(getResult(), getTipo(sim1.getResultado(), sim2.getResultado()));
            return new CuartetoBuilder().operador(division).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(terminal.getResultado()).auxiliar(terminal).siguiente(setAtLast(sim1, sim2)).build();
        }
    }

    public Cuarteto getResta(Cuarteto sim2, String minus, Cuarteto sim1) throws Exception {
        if (isNotTypeNumber(sim1.getResultado().getToken(), sim2.getResultado().getToken())) {
            throw new Exception("Resta ( - )" + SALTO_LN + getInvalidSimbol(sim2.getResultado(), sim1.getResultado(), ERROR_NUM));
        } else {
            Cuarteto terminal = getTerminalDeclaration(getResult(), getTipo(sim1.getResultado(), sim2.getResultado()));
            return new CuartetoBuilder().operador(minus).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(terminal.getResultado()).auxiliar(terminal).siguiente(setAtLast(sim1, sim2)).build();
        }
    }

    public Cuarteto getModulo(Cuarteto sim2, String module, Cuarteto sim1) throws Exception {
        if (isNotTypeNumber(sim1.getResultado().getToken(), sim2.getResultado().getToken())) {
            throw new Exception("Modulo ( % )" + SALTO_LN + getInvalidSimbol(sim2.getResultado(), sim1.getResultado(), ERROR_NUM));
        } else {
            Cuarteto terminal = getTerminalDeclaration(getResult(), getTipo(sim1.getResultado(), sim2.getResultado()));
            return new CuartetoBuilder().operador(module).operando1(sim2.getResultado()).
                    operando2(sim1.getResultado()).resultado(terminal.getResultado()).auxiliar(terminal).siguiente(setAtLast(sim1, sim2)).build();
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

    public Cuarteto orderOpNum(Cuarteto operacion, int fila, int columna) throws Exception {
        if (!TablaTipos.getInstance().isError(operacion.getResultado().getToken())) {
            Stack<Cuarteto> pila = new Stack<>();
            Cuarteto i = operacion;
            while (i != null) {
                pila.push(i);
                if (TablaTipos.getInstance().isError(operacion.getResultado().getToken())) {
                    i.getResultado().setToken(TablaTipos.getInstance().getString());
                }
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
            Cuarteto head = j;
            Cuarteto l = j;
            Cuarteto m;
            Cuarteto n = null;
            Cuarteto actual;
            Cuarteto anterior = null;
            if (j.getAuxiliar() != null) {
                head = j.getAuxiliar();
            }
            while (l != null) {
                if (l.getAuxiliar() != null) {
                    m = l.getAuxiliar();
                    actual = l;
                    if (isDeclaration(m.getOperador()) || isArraDeclaration(m.getOperador())) {
                        if (m.getSiguiente() != null) {
                            n = m.getSiguiente();
                        }
                        l.setAuxiliar(n);
                        m.setSiguiente(actual);
                        if (anterior != null) {
                            anterior.setSiguiente(m);
                        }
                    }
                }
                anterior = l;
                l = l.getSiguiente();
            }
            return head;
        } else {
            throw new Exception("Concatenacion en expresion" + SALTO_LN + getValorInfo(new SimboloBuilder().columna(columna).fila(fila).build()));
        }
    }

    private boolean isDeclaration(String operador) {
        return operador.equals(DECLARATION);
    }

    private boolean isArraDeclaration(String operador) {
        return operador.equals(ARRAY_DECLARATION);
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
        Cuarteto terminal = getTerminalDeclaration(getResult(), array.getResultado().getToken());
        return new CuartetoBuilder().operador(ARRAY).resultado(terminal.getResultado()).componentes(array.getComponentes())
                .operando1(array.getResultado()).operando2(array.getOperando1()).auxiliar(terminal).build();
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
        getLastCuarteto(posicionAnterior).setSiguiente(orderOpNum(resultadoAnterior, 0, 0));
        return posicionAnterior;
    }

    public void addSubprogramInstruction(Tipo tipo, Cuarteto parametros, Cuarteto body) {
        if (parametros != null) {
            tablaSimbolos.addSubprogramEstructura(parametros, subprogramaActual);
        } else {
            tablaSimbolos.addSubprogramEstructura(body, subprogramaActual);
        }
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
                return new CuartetoBuilder().operador(RETURN).operando1(getLastCuarteto(expresion).getResultado()).resultado(last.getResultado()).componentes(expresion).build();
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
            Cuarteto terminal = getTerminalDeclaration(getResult(), subprograma.getToken());
            subprograma.setLexema(var);
            Cuarteto parametros = compareSubprogramParams(subprograma.getParametros(), params, fila, columna, subprogramaActual);
            terminal.setSiguiente(parametros);
            return new CuartetoBuilder().operador(SUBPROGRAM_CALL).resultado(terminal.getResultado())
                    .operando1(subprograma).componentes(params).auxiliar(terminal).build();
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
                                .getLexema()).categoria(last.getResultado().getCategoria()).build()).operador(PARAM).build());
                    } else {
                        parametros = new CuartetoBuilder().resultado(new SimboloBuilder().lexema(last.getResultado()
                                .getLexema()).categoria(last.getResultado().getCategoria()).build()).operador(PARAM).build();
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

    public Cuarteto asignByToken(Cuarteto sim, Cuarteto expresion, boolean declaration, int fila, int columna) throws Exception {
        Cuarteto last = getLastCuarteto(expresion);
        if (TablaTipos.getInstance().isCompatible(last.getResultado().getToken(), sim.getResultado().getToken())) {
            String asign = ASSIGN;
            if (TablaTipos.getInstance().isBoolean(expresion.getResultado().getToken())) {
                asign = ASSIGN_BOOL;
            }
            getLastCuarteto(expresion).setSiguiente(getCuartetoString(sim.getResultado().getLexema(), fila, columna));
            Cuarteto result = new CuartetoBuilder().operador(asign).componentes(expresion).
                    operando1(last.getResultado()).resultado(sim.getResultado()).build();
            if (declaration) {
                sim.setOperador(DECLARATION);
            }
            return setAtLast(sim, result);
        } else {
            throw new Exception("Asignacion de valor en variable" + SALTO_LN + getInvalidAsign(sim.getResultado(), last.getResultado().getToken()));
        }
    }

    public Cuarteto getSimpleSubprogramCall(Cuarteto subprogram) throws Exception {
        return orderOpNum(subprogram, 0, 0);
    }

    public Cuarteto getDeclaration(String var) {
        Simbolo result = null;
        if (!global) {
            result = tablaSimbolos.getLocalVariable(var, subprogramaActual);
            result.setLexema(getLocalName(var));
        } else {
            result = tablaSimbolos.getGlobalVariable(var);
            result.setLexema(var);
        }
        return new CuartetoBuilder().operador(DECLARATION).resultado(result).build();
    }

    public Cuarteto getArrayDeclaration(String var) {
        Simbolo result = null;
        if (!global) {
            result = tablaSimbolos.getLocalVariable(var, subprogramaActual);
            result.setLexema(getLocalName(var));
        } else {
            result = tablaSimbolos.getGlobalVariable(var);
            result.setLexema(var);
        }
        return new CuartetoBuilder().operador(ARRAY_DECLARATION).resultado(result).build();
    }

    public void setDeclarationSubprogram(String var) {
        Simbolo result = tablaSimbolos.getLocalVariable(var, subprogramaActual);
        tablaSimbolos.addSubprogramEstructura(getTerminalDeclaration(var, result.getToken()), subprogramaActual);
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
            sim.setLexema(getLocalName(var, sim.getFila(), sim.getColumna()));
            //sim.setFila(fila);
            //sim.setColumna(columna);
            return new CuartetoBuilder().resultado(sim).build();
        } else if (existGlobalVariable(var)) {
            Simbolo sim = getGlobalVariable(var);
            //sim.setFila(fila);
            //sim.setColumna(columna);
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
        if (ifB != null) {
            setAtLast(ifA, ifB);
            setResultadoAllIfElse(ifA, getLastCuarteto(ifB).getResultado());
        }
        getLastCuarteto(ifA).setAuxiliar(new CuartetoBuilder().build());
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
        return new CuartetoBuilder().operador(ELSE).operando1(getSimbolString(setEtiquetaResult(getEtiqueta())))
                .resultado(getSimbolString(setEtiquetaResult(getSimpleEtiqueta()))).componentes(instructions).build();
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
            if (signo.equals(PLUS)) {
                instruccion = getSumaConcatenacion(getCuarteto(asignacion.getResultado()), PLUS, getCuartetoNum(getSimbolEntero(entero, fila, columna)));
            } else if (signo.equals(MINUS)) {
                instruccion = getResta(asignacion, MINUS, getCuartetoNum(getSimbolEntero(entero, fila, columna)));
            }
            instruccion = orderOpNum(instruccion, fila, columna);
            Cuarteto result = new CuartetoBuilder().operador(FOR).operando1(last.getOperando2()).
                    operando2(opBol.getOperando1()).resultado(last.getResultado()).componentes(setAtLast(instruccion, instructions)).build();
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

    public void print(Cuarteto bodyInstruction, Cuarteto mainInstruction) throws IOException {
        isCode3D = true;
        isCodeC = false;
        if (selectedC3D) {
            if (bodyInstruction != null) {
                printBodyInstruction(bodyInstruction);
            }
            printMainInstruction(mainInstruction);
        }

        isCode3D = false;
        isCodeC = true;
        if (selectedCPP) {
            fileWriter = new FileWriter(PATH_FILE);
            printWriter = new PrintWriter(fileWriter);
            printWriter.println(LIBRARIES_VALUE);
            printWriter.println(OPEN_MAIN);
            if (bodyInstruction != null) {
                printBodyInstruction(bodyInstruction);
            }
            printMainInstruction(mainInstruction);
            printWriter.print(declaracionesCodigoC);
            printWriter.print(cuerpoCodigoC);
            printWriter.print(CLOSE_MAIN);
            printWriter.close();

            String cmd = "g++ CodigoC.cpp"; //Comando de apagado en linux
            Runtime.getRuntime().exec(cmd);

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
                case DECLARATION:
                    printDeclaration(cuarteto);
                    break;
                case ARRAY_DECLARATION:
                    printArrayDeclaration(cuarteto);
                    break;
            }
        }
    }

    private void printArrayDeclaration(Cuarteto sim) {
        if (isCodeC) {
            if (!TablaTipos.getInstance().isVoid(sim.getResultado().getToken())) {
                printDeclaracion(sim.getResultado().getToken().getValor() + SPACE + getResult(sim.getResultado())
                        + OPEN_SQR + ARRAY_SIZE + CLOSE_SQR + SEMICOLON);
            }
        }
    }

    private String getEtiquetaPrint(Simbolo sim) {
        if (isCode3D) {
            return sim.getLexema() + COLON;
        } else {
            return currentTerminal + sim.getLexema() + COLON;
        }
    }

    private String getEtiquetaPrintGoto(Simbolo sim) {
        return currentTerminal + sim.getLexema() + SEMICOLON;
    }

    private String getResult(Simbolo sim) {
        if (sim != null) {
            if (tablaSimbolos.isAmbitoLocal(sim.getAmbito())) {
                return currentTerminal + sim.getLexema();
            } else {
                return sim.getLexema();
            }
        } else {
            return currentTerminal;
        }
    }

    private String getEtiquetaPrint(Cuarteto sim) {
        Simbolo last = getLastCuarteto(sim).getResultado();
        return last.getLexema() + last.getFila() + sim.getOperando1().getLexema() + COLON;
    }

    private String getIf(String condicion) {
        return IF_AB + condicion + IF_CE;
    }

    private void printBodyInstruction(Cuarteto sim) {
        if (isCode3D) {
            print(sim);
        }
        if (isCodeC) {
            print(sim);
        }
    }

    private void printMainInstruction(Cuarteto sim) {
        if (isCode3D) {
            println(getEtiquetaPrint(new SimboloBuilder().lexema(MAIN_VALUE).build()));
            print(sim);
            printReturn(new CuartetoBuilder().build());
        }
        if (isCodeC) {
            print(sim);
            printReturn(new CuartetoBuilder().build());
        }
    }

    private void printDeclaration(Cuarteto sim) {
        if (isCodeC) {
            if (!TablaTipos.getInstance().isVoid(sim.getResultado().getToken())) {
                printDeclaracion(sim.getResultado().getToken().getValor() + SPACE + getResult(sim.getResultado()) + SEMICOLON);
            }
        }
    }

    private void printVarBool(Cuarteto sim) {
        if (isCode3D) {
            print(getIf(getNameWithoutPosition(sim.getResultado()) + EQUAL + TRUE));
        }
        if (isCodeC) {
            print(getIf(getResult(sim.getResultado()) + EQUAL + TRUE));
        }
    }

    private void printBool(Cuarteto sim) {
        if (isCode3D) {
            if (isTrue(sim.getResultado())) {
                print(getIf(TRUE + EQUAL + TRUE));
            } else if (isFalse(sim.getResultado())) {
                print(getIf(FALSE + EQUAL + TRUE));
            }
        }
        if (isCodeC) {
            if (isTrue(sim.getResultado())) {
                print(getIf(TRUE + EQUAL + TRUE));
            } else if (isFalse(sim.getResultado())) {
                print(getIf(FALSE + EQUAL + TRUE));
            }
        }
    }

    private void printOperacion(Cuarteto sim) {
        if (isCode3D) {
            println(getNameWithoutPosition(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + getNameWithoutPosition(sim.getOperando1())
                    + SPACE + sim.getOperador() + SPACE + getNameWithoutPosition(sim.getOperando2()));
        }
        if (isCodeC) {
            if (!tablaSimbolos.isValor(sim.getOperando1().getCategoria()) && !tablaSimbolos.isValor(sim.getOperando2().getCategoria())) {
                println(getResult(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + getResult(sim.getOperando1())
                        + SPACE + sim.getOperador() + SPACE + getResult(sim.getOperando2()) + SEMICOLON);
            } else if (!tablaSimbolos.isValor(sim.getOperando1().getCategoria())) {
                println(getResult(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + getResult(sim.getOperando1())
                        + SPACE + sim.getOperador() + SPACE + sim.getOperando2().getLexema() + SEMICOLON);
            } else if (!tablaSimbolos.isValor(sim.getOperando2().getCategoria())) {
                println(getResult(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + sim.getOperando1().getLexema()
                        + SPACE + sim.getOperador() + SPACE + getResult(sim.getOperando2()) + SEMICOLON);
            } else {
                println(getResult(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + sim.getOperando1().getLexema()
                        + SPACE + sim.getOperador() + SPACE + sim.getOperando2().getLexema() + SEMICOLON);
            }
        }
    }

    private void printRel(Cuarteto sim) {
        if (isCode3D) {
            print(sim.getSiguiente());
            print(getIf(getNameWithoutPosition(sim.getOperando1()) + SPACE + sim.getOperador() + SPACE + getNameWithoutPosition(sim.getOperando2())));
        }
        if (isCodeC) {
            print(sim.getSiguiente());
            print(getIf(getResult(sim.getOperando1()) + SPACE + sim.getOperador() + SPACE + getResult(sim.getOperando2())));
        }
    }

    private void printPrint(Cuarteto sim) {
        if (isCode3D) {
            print(sim.getComponentes());
            if (TablaTipos.getInstance().isBoolean(sim.getResultado().getToken())) {
                if (sim.getResultado().getLexema().equals(TRUE_VAL)) {
                    println(PRINT + SPACE + TRUE);
                } else if (sim.getResultado().getLexema().equals(FALSE_VAL)) {
                    println(PRINT + SPACE + FALSE);
                }
            } else {
                println(PRINT + SPACE + getNameWithoutPosition(sim.getResultado()));
            }
        }
        if (isCodeC) {
            print(sim.getComponentes());
            if (TablaTipos.getInstance().isBoolean(sim.getResultado().getToken())) {
                if (sim.getResultado().getLexema().equals(TRUE_VAL)) {
                    println(PRINT_AB + SPACE + TRUE + SEMICOLON);
                } else if (sim.getResultado().getLexema().equals(FALSE_VAL)) {
                    println(PRINT_AB + SPACE + FALSE + SEMICOLON);
                }
            } else {
                if (sim.getComponentes().getResultado().getCategoria() != null) {
                    if (tablaSimbolos.isVariable(sim.getComponentes().getResultado().getCategoria())) {
                        println(PRINT_AB + SPACE + getResult(sim.getResultado()) + SEMICOLON);
                    } else {
                        println(PRINT_AB + SPACE + sim.getResultado().getLexema() + SEMICOLON);
                    }
                } else {
                    println(PRINT_AB + SPACE + sim.getResultado().getLexema() + SEMICOLON);
                }
            }
        }
    }

    private void printPrintln(Cuarteto sim) {
        if (isCode3D) {
            print(sim.getComponentes());
            if (TablaTipos.getInstance().isBoolean(sim.getResultado().getToken())) {
                if (sim.getResultado().getLexema().equals(TRUE_VAL)) {
                    println(PRINTLN + SPACE + TRUE);
                } else if (sim.getResultado().getLexema().equals(FALSE_VAL)) {
                    println(PRINTLN + SPACE + FALSE);
                }
            } else {
                println(PRINTLN + SPACE + getNameWithoutPosition(sim.getResultado()));
            }
        }
        if (isCodeC) {
            print(sim.getComponentes());
            if (TablaTipos.getInstance().isBoolean(sim.getResultado().getToken())) {
                if (sim.getResultado().getLexema().equals(TRUE_VAL)) {
                    println(PRINT_AB + SPACE + TRUE + PRINT_CE + SEMICOLON);
                } else if (sim.getResultado().getLexema().equals(FALSE_VAL)) {
                    println(PRINT_AB + SPACE + FALSE + PRINT_CE + SEMICOLON);
                }
            } else {
                println(PRINT_AB + SPACE + getResult(sim.getResultado()) + PRINT_CE + SEMICOLON);
            }
        }
    }

    private void printScanString(Cuarteto sim) {
        if (isCode3D) {
            print(sim.getComponentes());
            if (sim.getOperando1() != null) {
                println(SCAN_VALUE + SPACE + getNameWithoutPosition(sim.getResultado()) + OPEN_SQR + getNameWithoutPosition(sim.getOperando1()) + CLOSE_SQR);
            } else {
                println(SCAN_VALUE + SPACE + getNameWithoutPosition(sim.getResultado()));
            }
        }
        if (isCodeC) {
            print(sim.getComponentes());
            if (sim.getOperando1() != null) {
                println(SCAN_AB + SPACE + getResult(sim.getResultado()) + OPEN_SQR + sim.getOperando1().getLexema() + CLOSE_SQR + SEMICOLON);
            } else {
                println(SCAN_AB + SPACE + getResult(sim.getResultado()) + SEMICOLON);
            }
        }
    }

    private void printScanNumber(Cuarteto sim) {
        if (isCode3D) {
            print(sim.getComponentes());
            if (sim.getOperando1() != null) {
                println(SCAN_VALUE + SPACE + getNameWithoutPosition(sim.getResultado()) + OPEN_SQR + getNameWithoutPosition(sim.getOperando1()) + CLOSE_SQR);
            } else {
                println(SCAN_VALUE + SPACE + getNameWithoutPosition(sim.getResultado()));
            }
        }
        if (isCodeC) {
            print(sim.getComponentes());
            if (sim.getOperando1() != null) {
                println(SCAN_AB + SPACE + getResult(sim.getResultado()) + OPEN_SQR + sim.getOperando1().getLexema() + CLOSE_SQR + SEMICOLON);
            } else {
                println(SCAN_AB + SPACE + getResult(sim.getResultado()) + SEMICOLON);
            }
        }
    }

    private void printCiclo(Cuarteto sim) {
        if (isCode3D) {
            println(getEtiquetaPrint(sim.getOperando1()));
            if (sim.getComponentes() != null) {
                print(sim.getComponentes());
            }
            printInstruction(sim.getRestriccion());
            if (sim.getAuxiliar() != null) {
                if (isNot(sim.getAuxiliar().getResultado().getLexema())) {
                    println(GOTO + getNameWithoutPosition(sim.getResultado()));
                    println(GOTO + getNameWithoutPosition(sim.getOperando2()));
                } else {
                    println(GOTO + getNameWithoutPosition(sim.getOperando2()));
                    println(GOTO + getNameWithoutPosition(sim.getResultado()));
                }
            } else {
                println(GOTO + getNameWithoutPosition(sim.getOperando2()));
                println(GOTO + getNameWithoutPosition(sim.getResultado()));
            }
        }
        if (isCodeC) {
            println(getEtiquetaPrint(sim.getOperando1()));
            if (sim.getComponentes() != null) {
                print(sim.getComponentes());
            }
            printInstruction(sim.getRestriccion());
            if (sim.getAuxiliar() != null) {
                if (isNot(sim.getAuxiliar().getResultado().getLexema())) {
                    println(GOTO + getEtiquetaPrintGoto(sim.getResultado()));
                    println(GOTO + getEtiquetaPrintGoto(sim.getOperando2()));
                } else {
                    println(GOTO + getEtiquetaPrintGoto(sim.getOperando2()));
                    println(GOTO + getEtiquetaPrintGoto(sim.getResultado()));
                }
            } else {
                println(GOTO + getEtiquetaPrintGoto(sim.getOperando2()));
                println(GOTO + getEtiquetaPrintGoto(sim.getResultado()));
            }
        }
        //println(RETURN_VALUE);
    }

    private void printAsign(Cuarteto sim) {
        if (isCode3D) {
            print(sim.getComponentes());
            println(getNameWithoutPosition(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + getNameWithoutPosition(sim.getOperando1()));
        }
        if (isCodeC) {
            print(sim.getComponentes());
            if (!tablaSimbolos.isValor(sim.getOperando1().getCategoria())) {
                println(getResult(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + getResult(sim.getOperando1()) + SEMICOLON);
            } else {
                println(getResult(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + sim.getOperando1().getLexema() + SEMICOLON);
            }
        }
    }

    private void printAsignBool(Cuarteto sim) {
        if (isCode3D) {
            print(sim.getComponentes());
            if (isTrue(sim.getOperando1())) {
                println(getNameWithoutPosition(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + TRUE);
            } else if (isFalse(sim.getOperando1())) {
                println(getNameWithoutPosition(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + FALSE);
            } else {
                println(getNameWithoutPosition(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + getNameWithoutPosition(sim.getOperando1()));
            }
        }
        if (isCodeC) {
            print(sim.getComponentes());
            if (isTrue(sim.getOperando1())) {
                println(getResult(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + TRUE + SEMICOLON);
            } else if (isFalse(sim.getOperando1())) {
                println(getResult(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + FALSE + SEMICOLON);
            } else {
                println(getResult(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + sim.getOperando1().getLexema() + SEMICOLON);
            }
        }
    }

    private void printIf(Cuarteto sim) {
        if (isCode3D) {
            println(getEtiquetaPrint(sim.getOperando1()));
            print(sim.getComponentes());
            println(GOTO + SPACE + getNameWithoutPosition(sim.getResultado()));
            if (sim.getAuxiliar() != null) {
                println(getEtiquetaPrint(sim.getResultado()));
            }
        }
        if (isCodeC) {
            println(getEtiquetaPrint(sim.getOperando1()));
            print(sim.getComponentes());
            println(GOTO + SPACE + getEtiquetaPrintGoto(sim.getResultado()));
            if (sim.getAuxiliar() != null) {
                println(getEtiquetaPrint(sim.getResultado()));
            }
        }
    }

    private void printElseIf(Cuarteto sim) {
        if (isCode3D) {
            println(getEtiquetaPrint(sim.getOperando1()));
            print(sim.getComponentes());
            println(GOTO + SPACE + getNameWithoutPosition(sim.getResultado()));
            if (sim.getAuxiliar() != null) {
                println(getEtiquetaPrint(sim.getResultado()));
            }
        }
        if (isCodeC) {
            println(getEtiquetaPrint(sim.getOperando1()));
            print(sim.getComponentes());
            println(GOTO + SPACE + getEtiquetaPrintGoto(sim.getResultado()));
            if (sim.getAuxiliar() != null) {
                println(getEtiquetaPrint(sim.getResultado()));
            }
        }
    }

    private void printElse(Cuarteto sim) {
        if (isCode3D) {
            println(getEtiquetaPrint(sim.getOperando1()));
            print(sim.getComponentes());
            println(GOTO + SPACE + getNameWithoutPosition(sim.getResultado()));
            println(getEtiquetaPrint(sim.getResultado()));
        }
        if (isCodeC) {
            println(getEtiquetaPrint(sim.getOperando1()));
            print(sim.getComponentes());
            println(GOTO + SPACE + getEtiquetaPrintGoto(sim.getResultado()));
            println(getEtiquetaPrint(sim.getResultado()));
        }
    }

    private void printWhile(Cuarteto sim) {
        if (isCode3D) {
            println(getEtiquetaPrint(sim.getOperando1()));
            print(sim.getComponentes());
            println(GOTO + getNameWithoutPosition(sim.getOperando2()));
            //println(RETURN_VALUE);
            println(getEtiquetaPrint(sim.getResultado()));
            //println(RETURN_VALUE);
        }
        if (isCodeC) {
            println(getEtiquetaPrint(sim.getOperando1()));
            print(sim.getComponentes());
            println(GOTO + getEtiquetaPrintGoto(sim.getOperando2()));
            //println(RETURN_VALUE);
            println(getEtiquetaPrint(sim.getResultado()));
            //println(RETURN_VALUE);
        }
    }

    private void printFor(Cuarteto sim) {
        if (isCode3D) {
            println(getEtiquetaPrint(sim.getOperando1()));
            //print(sim.getRestriccion());
            print(sim.getComponentes());
            println(GOTO + getNameWithoutPosition(sim.getOperando2()));
            println(getEtiquetaPrint(sim.getResultado()));
            println(RETURN_VALUE);
        }
        if (isCodeC) {
            println(getEtiquetaPrint(sim.getOperando1()));
            //print(sim.getRestriccion());
            print(sim.getComponentes());
            println(GOTO + getEtiquetaPrintGoto(sim.getOperando2()));
            println(getEtiquetaPrint(sim.getResultado()));
            //println(RETURN_VALUE);
        }
    }

    private void printDo(Cuarteto sim) {
        if (isCode3D) {
            println(getEtiquetaPrint(sim.getOperando1()));
            println(GOTO + getNameWithoutPosition(sim.getOperando2()));
            println(getEtiquetaPrint(sim.getResultado()));
            println(RETURN_VALUE);
        }
        if (isCodeC) {
            println(getEtiquetaPrint(sim.getOperando1()));
            println(GOTO + getEtiquetaPrintGoto(sim.getOperando2()));
            println(getEtiquetaPrint(sim.getResultado()));
            //println(RETURN_VALUE);
        }
    }

    private void printArrayPosition(Cuarteto sim) {
        if (isCode3D) {
            printLista(sim.getOperando1().getDimensiones());
            print(sim.getComponentes());
            println(getNameWithoutPosition(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + getNameWithoutPosition(sim.getOperando1())
                    + SPACE + OPEN_SQR + getNameWithoutPosition(sim.getOperando2()) + CLOSE_SQR);
        }
        if (isCodeC) {
            printLista(sim.getOperando1().getDimensiones());
            print(sim.getComponentes());
            if (sim.getOperando1().getAmbito() == 0) {
                println(getResult(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + sim.getOperando1().getLexema()
                        + SPACE + OPEN_SQR + getResult(sim.getOperando2()) + CLOSE_SQR + SEMICOLON);
            } else {
                println(getResult(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + getResult(sim.getOperando1())
                        + SPACE + OPEN_SQR + getResult(sim.getOperando2()) + CLOSE_SQR + SEMICOLON);
            }
        }
    }

    private void printArrayAsign(Cuarteto sim) {
        if (isCode3D) {
            printLista(sim.getOperando1().getDimensiones());
            print(sim.getAuxiliar().getComponentes());
            print(sim.getComponentes());
            if (isTrue(sim.getResultado()) || isFalse(sim.getResultado())) {
                println(getResult(sim.getOperando1()) + SPACE + OPEN_SQR + getNameWithoutPosition(sim.getOperando1()) + CLOSE_SQR
                        + SPACE + SIGN_EQUAL + SPACE + getValueTrueOrFalse(getNameWithoutPosition(sim.getResultado())) + SEMICOLON);
            } else {
                println(getResult(sim.getOperando1()) + SPACE + OPEN_SQR + getNameWithoutPosition(sim.getOperando1()) + CLOSE_SQR
                        + SPACE + SIGN_EQUAL + SPACE + getNameWithoutPosition(sim.getResultado()) + SEMICOLON);
            }
        }
        if (isCodeC) {
            printLista(sim.getOperando1().getDimensiones());
            print(sim.getAuxiliar().getComponentes());
            print(sim.getComponentes());
            if (isTrue(sim.getResultado()) || isFalse(sim.getResultado())) {
                println(getResult(sim.getOperando1()) + SPACE + OPEN_SQR + getResult(sim.getAuxiliar().getOperando1()) + CLOSE_SQR
                        + SPACE + SIGN_EQUAL + SPACE + getValueTrueOrFalse(sim.getResultado().getLexema()) + SEMICOLON);
            } else {
                println(getResult(sim.getOperando1()) + SPACE + OPEN_SQR + getResult(sim.getAuxiliar().getOperando1()) + CLOSE_SQR
                        + SPACE + SIGN_EQUAL + SPACE + getResult(sim.getResultado()) + SEMICOLON);
            }
        }
    }

    private void printLista(LinkedList<Cuarteto> cuartetos) {
        if (isCode3D) {
            cuartetos.forEach((cuarteto) -> {
                print(cuarteto);
            });
        }
        if (isCodeC) {
            cuartetos.forEach((cuarteto) -> {
                print(cuarteto);
            });
        }
    }

    private void printSubprogram(Cuarteto sim) {
        if (isCode3D) {
            println(getEtiquetaPrint(sim.getOperando2()));
            print(sim.getComponentes());
        }
        if (isCodeC) {
            //println(getEtiquetaPrint(sim.getOperando2()));
            //print(sim.getComponentes());
        }
    }

    private void printSubprogramCall(Cuarteto sim) {
        if (isCode3D) {
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
            println(getNameWithoutPosition(sim.getResultado()) + SPACE + SIGN_EQUAL + SPACE + CALL + SPACE + getNameWithoutPosition(sim.getOperando1()) + COMMA + SPACE + size);
        }
        if (isCodeC) {
            Simbolo actualVar = getLastCuarteto(sim).getResultado();
            if (actualVar.getCategoria() != null) {
                if (tablaSimbolos.isVariable(actualVar.getCategoria())) {
                    currentTerminal = actualVar.getLexema() + actualVar.getFila() + actualVar.getColumna();
                } else {
                    currentTerminal = VOID_VALUE;
                }
            } else {
                currentTerminal = sim.getResultado().getLexema() + SEPARATOR + sim.getResultado().getFila() + sim.getResultado().getColumna();
            }
            //currentTerminal = actualVar.getLexema();
            terminalResult = sim.getResultado().getLexema();
            if (sim.getOperando1() != null) {
                Simbolo operando1 = sim.getOperando1();
                Cuarteto result = null;
                if (!operando1.getEstructura().isEmpty()) {
                    result = operando1.getEstructura().get(0);
                }
                Cuarteto actual = null;
                Cuarteto componente = sim.getComponentes();
                while (componente != null) {
                    print(componente.getComponentes());
                    componente = componente.getSiguiente();
                }
                if (!operando1.getParametros().isEmpty()) {
                    actual = sim.getAuxiliar();
                    int contador = 0;
                    while (result != null) {
                        if (contador < operando1.getNoParametros()) {
                            printDeclaration(result);
                            printParametro(new CuartetoBuilder().resultado(actual.getResultado()).operando1(result.getResultado()).build());
                            actual = actual.getSiguiente();
                        } else {
                            printInstruction(result);
                        }
                        result = result.getSiguiente();
                        contador++;
                    }
                } else {
                    if (!operando1.getEstructura().isEmpty()) {
                        print(operando1.getEstructura().get(0));
                    }
                }
                if (!operando1.getEstructura().isEmpty()) {
                }
            }
            currentTerminal = "";
        }
    }

    private void printReturn(Cuarteto sim) {
        if (isCode3D) {
            print(sim.getComponentes());
            if (sim.getResultado() != null) {
                if (isTrue(sim.getResultado()) || isFalse(sim.getResultado())) {
                    println(RETURN_VALUE + SPACE + getValueTrueOrFalse(getNameWithoutPosition(sim.getResultado())));
                } else {
                    println(RETURN_VALUE + SPACE + getNameWithoutPosition(sim.getResultado()));
                }
            } else {
                println(RETURN_VALUE);
            }
        }
        if (isCodeC) {
            print(sim.getComponentes());
            if (sim.getResultado() != null) {
                if (isTrue(sim.getResultado()) || isFalse(sim.getResultado())) {
                    println(terminalResult + SPACE + SIGN_EQUAL + SPACE + getValueTrueOrFalse(sim.getResultado().getLexema()) + SEMICOLON);
                } else {
                    println(terminalResult + SPACE + SIGN_EQUAL + SPACE + getResult(sim.getResultado()) + SEMICOLON);
                }
            }
        }
    }

    private void printParametro(Cuarteto sim) {
        if (isCode3D) {
            if (isTrue(sim.getResultado()) || isFalse(sim.getResultado())) {
                println(PARAM_VALUE + SPACE + getValueTrueOrFalse(getNameWithoutPosition(sim.getResultado())));
            } else {
                println(PARAM_VALUE + SPACE + getNameWithoutPosition(sim.getResultado()));
            }
        }
        if (isCodeC) {
            if (isTrue(sim.getResultado()) || isFalse(sim.getResultado())) {
                println(sim.getOperando1().getLexema() + SPACE + SIGN_EQUAL + SPACE + getValueTrueOrFalse(sim.getResultado().getLexema()) + SEMICOLON);
            } else {
                if (!tablaSimbolos.isValor(sim.getResultado().getCategoria())) {
                    println(getResult(sim.getOperando1()) + SPACE + SIGN_EQUAL + SPACE + getResult(sim.getResultado()) + SEMICOLON);
                } else {
                    println(getResult(sim.getOperando1()) + SPACE + SIGN_EQUAL + SPACE + sim.getResultado().getLexema() + SEMICOLON);
                }
            }
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
        if (isCode3D) {
            manejadorAreaTexto.printTerminal(print);
        }
        if (isCodeC) {
            cuerpoCodigoC = cuerpoCodigoC.concat(print);
            //printWriter.print(print);
        }

    }

    private void println(String print) {
        if (isCode3D) {
            manejadorAreaTexto.printTerminal(print + SALTO_LN);
        }
        if (isCodeC) {
            cuerpoCodigoC = cuerpoCodigoC.concat(print + SALTO_LN);
            //printWriter.println(print);
        }
    }

    private void printDeclaracion(String print) {
        if (isCodeC) {
            declaracionesCodigoC = declaracionesCodigoC.concat(print + SALTO_LN);
        }
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
        return var;
    }

    private String getLocalName(String var, int fila, int columna) {
        return var + SEPARATOR + fila + columna;
    }

    private String getSubprogramName() {
        return resultSubprogramActual + subprogramaActual;
    }

    private boolean existSimbol(String var) {
        return tablaSimbolos.existSimbol(var);
    }

    public Cuarteto getSubprograma(String name, Cuarteto params, Cuarteto body) {
        if (TablaTipos.getInstance().isVoid(tablaSimbolos.getSubprogram(name).getToken())) {
            if (body != null) {
                getLastCuarteto(body).setSiguiente(getVoidReturn());
            } else {
                body = getVoidReturn();
            }
        }
        if (params != null) {
            getLastCuarteto(params).setSiguiente(body);
        } else {
            params = body;
        }
        return new CuartetoBuilder().operador(SUBPROGRAM).operando2(getSimbolString(name))
                .resultado(tablaSimbolos.getSubprogram(name)).componentes(params).build();
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

    private boolean isSubprogram(String operador) {
        return operador.equals(SUBPROGRAM);
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
    public final static String SEMICOLON = ";";
    public final static String T = "t";
    public final static String ETQ = "et";
    public final static String ARRAY = "ARRAY";
    public final static String ARRAY_ASIGN = "ARRAY<-";
    public final static String PRINT = "PRINT";
    public final static String PRINTLN = "PRINTLN";
    public final static String PRINT_AB = "cout <<";
    public final static String PRINT_CE = "<< endl";
    public final static String SCANS = "SCANS";
    public final static String SCANN = "SCANN";
    public final static String SCAN_VALUE = "scan";
    public final static String ASSIGN = "<-";
    public final static String ASSIGN_BOOL = "<-BOOL";
    public final static String IF = "IF";
    public final static String ELSEIF = "ELSEIF";
    public final static String ELSE = "ELSE";
    public final static String FINAL_IF = "FINAL_IF";
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
    public final static String MAIN_VALUE = "MAIN";
    public final static String RETURN = "RETURN";
    public final static String RETURN_VALUE = "return";
    public final static String VOID_VALUE = "void$";
    public final static String SCAN = "SCAN";
    public final static String SCAN_AB = "cin >> ";
    public final static String DECLARATION = "DECLARATION";
    public final static String ARRAY_DECLARATION = "ARRAY_DECLARATION";
    public final static String ARRAY_SIZE = "N";
    public final static String STRING_ARRAY_SIZE = "N";
    public final static String LOWER_EQ = "<=";
    public final static String HIGHER_EQ = ">=";
    public final static String LOWER = "<";
    public final static String HIGHER = ">";
    public final static String NOT_EQUAL = "!=";
    public final static String EQUAL = "==";
    public final static String CICLO = "CICLO";
    public final static String SPACE = " ";
    public final static String SEPARATOR = "$";
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
    public final static String LIBRARIES_VALUE = "#include <iostream> \n#include <string>\n#define N 256 \nusing namespace std;";
    public final static String OPEN_MAIN = "int main(){";
    public final static String CLOSE_MAIN = "\nreturn 0;\n}";
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
    private boolean isCode3D = false;
    private boolean isCodeC = false;
    private String currentTerminal = "";
    private String terminalResult = "";
    FileWriter fileWriter;
    PrintWriter printWriter;
    public final static String PATH_FILE = "./CodigoC.cpp";
    private String cuerpoCodigoC = "";
    private String declaracionesCodigoC = "";
    private boolean selectedCPP = false;
    private boolean selectedC3D = false;
}
