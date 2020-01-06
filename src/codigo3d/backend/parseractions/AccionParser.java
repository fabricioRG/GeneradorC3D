
package codigo3d.backend.parseractions;

import codigo3d.backend.manejadores.ManejadorAreaTexto;
import codigo3d.backend.manejadores.ManejadorParser;
import codigo3d.backend.objetos.*;

/**
 * 
 * @author Fabricio RG
 */

public class AccionParser {

    public AccionParser(ManejadorParser manejador) {
        this.manejador = manejador;
    }
    
    public void asignDecl(Cuarteto cuarteto, Tipo tipo) throws Exception{
        declarador.asignDecl(cuarteto, tipo);
    }
    
    public TablaSimbolos getTablaSimbolos() {
        return manejador.getTablaSimbolos();
    }

    public int getContadorEtq() {
        return manejador.getContadorEtq();
    }

    public String getEtiquetaResult() {
        return manejador.getEtiquetaResult();
    }

    public String getSubprogramaActual() {
        return manejador.getSubprogramaActual();
    }

    public String getErrorSintactico() {
        return manejador.getErrorSintactico();
    }

    public boolean isGlobal() {
        return manejador.isGlobal();
    }

    private AsignadorValores asignador = null;
    private DeclaradorValores declarador = null;
    private ProcesadorFunciones procesadorFunciones = null;
    private ProcesadorNumerico operador = null;
    private RetornadorValores retornador = null;
    public ManejadorParser manejador = null;
    
}
