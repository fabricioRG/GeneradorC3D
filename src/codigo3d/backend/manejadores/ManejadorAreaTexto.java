package codigo3d.backend.manejadores;

import codigo3d.frontend.AreaTexto;
import codigo3d.backend.analizadores.Lexer1;
import codigo3d.backend.analizadores.parser;
import codigo3d.backend.analizadores.parser2;
import java.io.StringReader;

/**
 *
 * @author fabricio
 */
public class ManejadorAreaTexto {

    private AreaTexto at = null;
    public static int SALTO_LINEA = 10;
    public static String ERROR_MESSAGE = "---------ERROR---------\n";

    public ManejadorAreaTexto(AreaTexto at) {
        this.at = at;
    }

    public AreaTexto getAt() {
        return at;
    }

    public void runText(String entrada) {
        ManejadorParser mp = new ManejadorParser(this);
        at.getNavegador().setText("");
        if (!entrada.trim().isEmpty()) {
            StringReader sr = new StringReader(entrada);
            StringReader sr2 = new StringReader(entrada);

            Lexer1 lexer = new Lexer1(sr);
            Lexer1 lexer2 = new Lexer1(sr2);

            lexer.setManejadorParser(mp);
            lexer2.setManejadorParser(mp);

            parser generador = new parser(lexer2, mp);
            parser2 declarador = new parser2(lexer, mp);
            
            try {
                //Primer analisis - reconocimiento de variables, subprogramas y parametros
                declarador.parse();
            } catch (Exception e) {
                e.printStackTrace();
                mp.setNoError(false);
                printError(e.getMessage());
            }
            
            if (mp.isNoError()) {
                try {
                    //Segundo analisis - generacion de cuartetos
                    generador.parse();
                } catch (Exception e) {
                    e.printStackTrace();
                    printError(e.getMessage());
                }
            }
        }
    }

    //Metodo que devuelve la columna actual del cursor
    public int getColumn(String entrada) {
        String texto = at.getjEditorPane1().getText();
        int posicion = at.getjEditorPane1().getCaretPosition();
        String textoLimpio = texto.replaceAll("\r\n", "\n");
        char[] cadenaChar = textoLimpio.toCharArray();
        int contador = 1;
        if (posicion > 0) {
            for (int i = 0; i < posicion; i++) {
                contador++;
                if (cadenaChar[i] == SALTO_LINEA) {
                    contador = 1;
                }
            }
        } else {
            contador = posicion + 1;
        }
        return contador;
    }

    //Metodo que devuelve la linea actual del cursor
    public int getLine() {
        String texto = at.getjEditorPane1().getText();
        int posicion = at.getjEditorPane1().getCaretPosition();
        String textoLimpio = texto.replaceAll("\r\n", "\n");
        char[] cadenaChar = textoLimpio.toCharArray();
        int contador = 1;
        for (int i = 0; i < posicion; i++) {
            if (cadenaChar[i] == SALTO_LINEA) {
                contador++;
            }
        }
        return contador;
    }

    public void printTerminal(String text) {
        at.getNavegador().setText(at.getNavegador().getText() + text);
    }

    public void printError(String error) {
        at.getNavegador().setText(ERROR_MESSAGE + "Tipo de error: " + error);
    }

    public void printSemanticError(String error) {
        at.getNavegador().setText(at.getNavegador().getText() + ERROR_MESSAGE + "Tipo de error: " + error + SALTO_LINEA + "Imposible de recuperar");
    }

    public void printSintaxError(String error) {
        printTerminal(ERROR_MESSAGE + "Tipo de error: " + error);
    }

    public boolean isSelectedCPP(){
        return at.isSelectedCPP();
    }
    
    public boolean isSelectedC3D(){
        return at.isSelectedC3D();
    }
}
