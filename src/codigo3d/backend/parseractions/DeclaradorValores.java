package codigo3d.backend.parseractions;

import codigo3d.backend.manejadores.ManejadorParser;
import codigo3d.backend.objetos.Cuarteto;
import codigo3d.backend.objetos.TablaTipos;
import codigo3d.backend.objetos.Tipo;

/**
 *
 * @author FabricioRG
 */

public class DeclaradorValores extends AccionParser{

    public DeclaradorValores(ManejadorParser manejador) {
        super(manejador);
    }

    @Override
    public void asignDecl(Cuarteto cuarteto, Tipo tipo) throws Exception {
        if (!TablaTipos.getInstance().isVoid(tipo)) {
            if (!isGlobal()) {
                if (!getTablaSimbolos().existLocalVariableParametroOrArray(cuarteto.getOperador(), getSubprogramaActual())) {
                    if (cuarteto.getComponentes() != null) {
                        if (TablaTipos.getInstance().isCompatible(cuarteto.getComponentes().getResultado().getToken(), tipo)) {
                            getTablaSimbolos().setLocalVariable(tipo, cuarteto.getOperador(), getSubprogramaActual());
                        } else {
                            cuarteto.getOperando1().setToken(tipo);
                            throw new Exception("Asignacion" + ManejadorParser.SALTO_LN + manejador.getInvalidAsign(cuarteto.getOperando1(),
                                    cuarteto.getComponentes().getResultado().getToken()));
                        }
                    } else {
                        getTablaSimbolos().setLocalVariable(tipo, cuarteto.getOperador(), getSubprogramaActual());
                    }
                } else {
                    cuarteto.getOperando1().setToken(tipo);
                    throw new Exception("Declaracion de variable local" + ManejadorParser.SALTO_LN + manejador.getValorInfo(cuarteto.getOperando1())
                            + "Variable, Parametro o Arreglo ya declarado en subprograma: " + getSubprogramaActual());
                }
            } else {
                if (!getTablaSimbolos().existGlobalVariableOrArray(cuarteto.getOperador())) {
                    getTablaSimbolos().setGlobalVariable(tipo, cuarteto.getOperador());
                } else {
                    cuarteto.getOperando1().setToken(tipo);
                    throw new Exception("Declaracion de variable global" + ManejadorParser.SALTO_LN + manejador.getValorInfo(cuarteto.getOperando1())
                            + "Variable o Arreglo global ya declarado");
                }
            }
        } else {
            throw new Exception("Declaracion de Variable" + ManejadorParser.SALTO_LN + manejador.getInvalidAsignVoid(cuarteto.getOperando1()));
        }
    }
    
}
