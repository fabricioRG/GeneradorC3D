package codigo3d.backend.objetos;

/**
 * 
 * @author fabricioRG
 */
public class Simbolo {

    private String lexema;
    private Tipo token;

    public Simbolo() {
    }
    
    public Simbolo(String lexema, Tipo token) {
        this.lexema = lexema;
        this.token = token;
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
    
}
