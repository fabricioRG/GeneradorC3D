package codigo3d.backend.objetos;

/**
 * 
 * @author fabricioRG
 */
public class Simbolo {

    private String lexema;
    private Tipo token;
    private int fila;
    private int columna;

    public Simbolo() {
    }
    
    public Simbolo(String lexema, Tipo token) {
        this.lexema = lexema;
        this.token = token;
    }

    public Simbolo(String lexema, Tipo token, int fila, int columna) {
        this.lexema = lexema;
        this.token = token;
        this.fila = fila;
        this.columna = columna;
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
    
}
