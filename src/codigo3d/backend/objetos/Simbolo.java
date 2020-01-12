package codigo3d.backend.objetos;

import java.util.LinkedList;

/**
 * 
 * @author fabricioRG
 */
public class Simbolo implements Cloneable{

    private String lexema;
    private Tipo token;
    private String categoria;
    private LinkedList <Tipo> parametros;
    private LinkedList <Cuarteto> dimensiones;
    private LinkedList <Cuarteto> estructura;
    private int noParametros;
    private int noDimensiones;
    private int ambito;
    private int fila;
    private int columna;
    private boolean valorRetorno;

    Simbolo(SimboloBuilder builder) {
        this.lexema = builder.getLexema();
        this.token = builder.getToken();
        this.categoria = builder.getCategoria();
        this.parametros = builder.getParametros();
        this.dimensiones = builder.getDimesiones();
        this.noParametros = builder.getNoParametros();
        this.noDimensiones = builder.getNoDimensiones();
        this.ambito = builder.getAmbito();
        this.fila = builder.getFila();
        this.columna = builder.getColumna();
        this.estructura = builder.getEstructura();
        this.valorRetorno = builder.isValorRetorno();
    }

    @Override
    public Simbolo clone() throws CloneNotSupportedException {
        Simbolo cloneSimbolo = (Simbolo) super.clone();
        return cloneSimbolo;
    }
    
    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public Tipo getToken() {
        return token;
    }

    public void setToken(Tipo token) {
        this.token = token;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getNoParametros() {
        return noParametros;
    }

    public void setNoParametros(int noParametros) {
        this.noParametros = noParametros;
    }

    public int getAmbito() {
        return ambito;
    }

    public void setAmbito(int ambito) {
        this.ambito = ambito;
    }

    public LinkedList<Tipo> getParametros() {
        return parametros;
    }

    public void setParametros(LinkedList<Tipo> parametros) {
        this.parametros = parametros;
    }

    public LinkedList<Cuarteto> getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(LinkedList<Cuarteto> dimensiones) {
        this.dimensiones = dimensiones;
    }

    public int getNoDimensiones() {
        return noDimensiones;
    }

    public void setNoDimensiones(int noDimensiones) {
        this.noDimensiones = noDimensiones;
    }

    public LinkedList<Cuarteto> getEstructura() {
        return estructura;
    }

    public void setEstructura(LinkedList<Cuarteto> estructura) {
        this.estructura = estructura;
    }

    public boolean isValorRetorno() {
        return valorRetorno;
    }

    public void setValorRetorno(boolean valorRetorno) {
        this.valorRetorno = valorRetorno;
    }
}
