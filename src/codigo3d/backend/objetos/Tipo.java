package codigo3d.backend.objetos;

/**
 * 
 * @author fabricioRG
 */
public class Tipo {

    private String nombre;
    private String valor;
    private Tipo padre;
    private Tipo hijo;
    private int posicion;

    public Tipo(String nombre, String valor, Tipo padre, Tipo hijo, int posicion) {
        this.nombre = nombre;
        this.valor = valor;
        this.padre = padre;
        this.hijo = hijo;
        this.posicion = posicion;
    }

    public Tipo(String nombre) {
        this.nombre = nombre;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Tipo getPadre() {
        return padre;
    }

    public void setPadre(Tipo padre) {
        this.padre = padre;
    }

    public Tipo getHijo() {
        return hijo;
    }

    public void setHijo(Tipo hijo) {
        this.hijo = hijo;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
    
}
