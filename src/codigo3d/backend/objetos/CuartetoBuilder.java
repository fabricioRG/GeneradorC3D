package codigo3d.backend.objetos;

/**
 * 
 * @author fabricioRG
 */
public class CuartetoBuilder {
    
    private String operador;
    private Simbolo operando1;
    private Simbolo operando2;
    private Simbolo resultado;
    private Cuarteto siguiente;
    private Cuarteto componentes;
    private Cuarteto restriccion;
    private Cuarteto auxiliar;
    
    public CuartetoBuilder(){
    }
    
    public CuartetoBuilder auxiliar(Cuarteto auxiliar){
        this.auxiliar = auxiliar;
        return this;
    }
    
    public CuartetoBuilder operador(String operador){
        this.operador = operador;
        return this;
    }
    
    public CuartetoBuilder operando1(Simbolo operador1){
        this.operando1 = operador1;
        return this;
    }
    
    public CuartetoBuilder operando2(Simbolo operando2){
        this.operando2 = operando2;
        return this;
    }
    
    public CuartetoBuilder resultado(Simbolo resultado){
        this.resultado = resultado;
        return this;
    }
    
    public CuartetoBuilder siguiente(Cuarteto siguiente){
        this.siguiente = siguiente;
        return this;
    }
    
    public CuartetoBuilder componentes(Cuarteto componentes){
        this.componentes = componentes;
        return this;
    }
    
    public CuartetoBuilder restriccion(Cuarteto restriccion){
        this.restriccion = restriccion;
        return this;
    }
    
    public Cuarteto build(){
        return new Cuarteto(this);
    }

    public Cuarteto getAuxiliar() {
        return auxiliar;
    }

    public Cuarteto getRestriccion() {
        return restriccion;
    }

    public Cuarteto getComponentes() {
        return componentes;
    }

    public String getOperador() {
        return operador;
    }

    public Simbolo getOperando1() {
        return operando1;
    }

    public Simbolo getOperando2() {
        return operando2;
    }

    public Simbolo getResultado() {
        return resultado;
    }

    public Cuarteto getSiguiente() {
        return siguiente;
    }
    
}
