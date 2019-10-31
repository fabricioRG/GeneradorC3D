
package codigo3d.backend.manejadores;

import codigo3d.backend.analizadores.Lexer1;
import codigo3d.backend.analizadores.parser;
import codigo3d.frontend.Interprete;
import java.io.StringReader;

/**
 * 
 * @author fabricioRG
 */
public class ManejadorInterprete {

    private Interprete interprete = null;
    
    public ManejadorInterprete(Interprete interprete) {
        this.interprete = interprete;
    }
    
    public void processText(String entrada){/*
        if (!entrada.trim().isEmpty()) {
            StringReader sr = new StringReader(entrada);
            Lexer1 lexer = new Lexer1(sr);
            parser pars = new parser(lexer, new ManejadorParser(this));
            try {
                pars.parse();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }
    
}
