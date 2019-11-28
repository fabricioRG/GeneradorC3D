package codigo3d.backend.objetos;

import java.util.LinkedList;

/**
 * 
 * 
 */
public class SimboloBuilder {

    private String lexema;
    private Tipo token;
    private String categoria;
    private LinkedList <Tipo> parametros;
    private int noParametros;
    private int ambito;
    private int fila;
    private int columna;

    public SimboloBuilder() {
    }
    
    public SimboloBuilder lexema(String lexema){
        this.lexema = lexema;
        return this;
    }
    
    public SimboloBuilder token(Tipo token){
        this.token = token;
        return this;
    }
    
    public SimboloBuilder categoria(String categoria){
        this.categoria = categoria;
        return this;
    }
    
    public SimboloBuilder parametros(LinkedList <Tipo> parametros){
        this.parametros = parametros;
        return this;
    }
    
    public SimboloBuilder noParametros(int noParametros){
        this.noParametros = noParametros;
        return this;
    }
    
    public SimboloBuilder ambito(int ambito){
        this.ambito = ambito;
        return this;
    }
    
    public SimboloBuilder fila(int fila){
        this.fila = fila;
        return this;
    }
    
    public SimboloBuilder columna(int columna){
        this.columna = columna;
        return this;
    }
    
    public Simbolo build(){
        return new Simbolo(this);
    }

    public String getLexema() {
        return lexema;
    }

    public Tipo getToken() {
        return token;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
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
    
}
