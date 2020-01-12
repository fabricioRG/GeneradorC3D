package codigo3d.backend.objetos;

/**
 * 
 * @author fabricioRG
 */
public class Cuarteto implements Cloneable{

    private String operador;
    private Simbolo operando1;
    private Simbolo operando2;
    private Simbolo resultado;
    private Cuarteto siguiente;
    private Cuarteto componentes;
    private Cuarteto restriccion;
    private Cuarteto auxiliar;
    
    private Cuarteto(){
    }
    
    @Override
    public Cuarteto clone() throws CloneNotSupportedException{
        Cuarteto cloneCuarteto=(Cuarteto) super.clone();
        return cloneCuarteto;
    }
    
    Cuarteto(CuartetoBuilder builder){
        this.operador = builder.getOperador();
        this.operando1 = builder.getOperando1();
        this.operando2 = builder.getOperando2();
        this.resultado = builder.getResultado();
        this.siguiente = builder.getSiguiente();
        this.componentes = builder.getComponentes();
        this.restriccion = builder.getRestriccion();
        this.auxiliar = builder.getAuxiliar();
    }

    public Cuarteto getAuxiliar() {
        return (Cuarteto)auxiliar;
    }
    
    public Cuarteto getAuxiliarClone() throws CloneNotSupportedException {
        return (Cuarteto)auxiliar.clone();
    }

    public void setAuxiliar(Cuarteto auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public Simbolo getOperando1() {
        return operando1;
    }
    
    public Simbolo getOperando1Clone() throws CloneNotSupportedException {
        return (Simbolo) operando1.clone();
    }

    public void setOperando1(Simbolo operando1) {
        this.operando1 = operando1;
    }

    public Simbolo getOperando2() {
        return operando2;
    }
    
    public Simbolo getOperando2Clone() throws CloneNotSupportedException {
        return (Simbolo) operando2.clone();
    }
    
    public void setOperando2(Simbolo operando2) {
        this.operando2 = operando2;
    }

    public Simbolo getResultado() {
        return resultado;
    }
    
    public Simbolo getResultadoClone() throws CloneNotSupportedException {
        return resultado.clone();
    }

    public void setResultado(Simbolo resultado) {
        this.resultado = resultado;
    }

    public Cuarteto getSiguiente() {
        return (Cuarteto)siguiente;
    }
    
    public Cuarteto getSiguienteClone() throws CloneNotSupportedException {
        return (Cuarteto)siguiente.clone();
    }

    public void setSiguiente(Cuarteto siguiente) {
        this.siguiente = siguiente;
    }

    public Cuarteto getComponentes() {
        return (Cuarteto)componentes;
    }
    
    public Cuarteto getComponentesClone() throws CloneNotSupportedException {
        return (Cuarteto)componentes.clone();
    }

    public void setComponentes(Cuarteto componentes) {
        this.componentes = componentes;
    }

    public Cuarteto getRestriccion() {
        return (Cuarteto)restriccion;
    }
    
    public Cuarteto getRestriccionClone() throws CloneNotSupportedException {
        return (Cuarteto)restriccion.clone();
    }

    public void setRestriccion(Cuarteto restriccion) {
        this.restriccion = restriccion;
    }
    
}
