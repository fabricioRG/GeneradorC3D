package codigo3d.backend.manejadores;

import codigo3d.frontend.Analizador;
import codigo3d.frontend.AreaTexto;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author fabricio
 */
public class ManejadorAnalizador {

    static final String NEW_TAB = "new tab";
    static final String TIPO_TXT = ".txt";
    private Analizador analizador = null;
    private static ManejadorAnalizador INSTANCE = null;

    private ManejadorAnalizador() {
    }

    private synchronized static void createInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ManejadorAnalizador();
        }
    }

    public static ManejadorAnalizador getInstance() {
        if (INSTANCE == null) {
            createInstance();
        }
        return INSTANCE;
    }

    public void setAnalizador(Analizador analizador) {
        this.analizador = analizador;
    }

    //Metodo encargado de abrir una ventana en blanco
    public void agregarVentana() {
        AreaTexto at = new AreaTexto();
        analizador.jTabbedPane.add(NEW_TAB, at);
        estadoGuardar();
    }

    //Metodo encargado de agregar una nueva ventana con el texto cargado colocando como
    //nombre de la ventana el path absoluta de tal
    public void agregarVentana(String path, String texto) {
        AreaTexto at = new AreaTexto();
        at.getjEditorPane1().setText(texto);
        analizador.jTabbedPane.add(path, at);
    }

    //Metodo encargado de abrir una nueva ventana colocandole como texto inicial el seleccionado
    public void abrirDocumento(Component comp) {
        String aux = "";
        String texto = "";
        try {
            /**
             * llamamos el metodo que permite cargar la ventana
             */
            JFileChooser file = new JFileChooser();
            file.showOpenDialog(analizador);
            /**
             * abrimos el archivo seleccionado
             */
            File abre = file.getSelectedFile();

            /**
             * recorremos el archivo, lo leemos para plasmarlo en el area de texto
             */
            if (abre != null) {
                FileReader archivos = new FileReader(abre);
                BufferedReader lee = new BufferedReader(archivos);
                while ((aux = lee.readLine()) != null) {
                    texto += aux + "\n";
                }
                lee.close();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex + ""
                    + "\nNo se ha encontrado el archivo",
                    "ADVERTENCIA!!!", JOptionPane.WARNING_MESSAGE);
        }
        AreaTexto at = (AreaTexto) comp;
        at.getInterprete().setText(texto);
    }

    //Metodo encargado de cerrar una ventanva, verificando que se hayan guardado los cambios o no
    public void cerrarVentana(int ventana) {
        AreaTexto at = (AreaTexto) analizador.jTabbedPane.getSelectedComponent();
        if (at.isModificado()) {
            int respuesta = JOptionPane.showConfirmDialog(analizador, "¿Desea guardar los cambios?",
                    "Alerta!", JOptionPane.YES_NO_CANCEL_OPTION);
            if (respuesta == JOptionPane.NO_OPTION) {
                analizador.jTabbedPane.remove(ventana);
                estadoGuardar();
            } else if (respuesta == JOptionPane.YES_OPTION) {
                analizador.jTabbedPane.remove(ventana);
                estadoGuardar();
            }
        } else {
            analizador.jTabbedPane.remove(ventana);
            estadoGuardar();
        }
    }

    //Metodo encargado de guardar un archivo, en el cual dependiendo si ya ha sido guardado o no
    public void guardarArchivo(Component comp) {
        guardarArchivoComo(comp, 1);
    }

    public void guardarArchivoComo(Component comp, int opcion) {
        try {
            String nombre = "";
            JFileChooser file = new JFileChooser();
            file.showSaveDialog(analizador);
            File guarda = file.getSelectedFile();

            if (guarda != null) {
                /*guardamos el archivo y le damos el formato directamente,
    * si queremos que se guarde en formato doc lo definimos como .doc*/
                FileWriter save = new FileWriter(guarda + ".txt");
                AreaTexto at = (AreaTexto) comp;
                save.write(at.getInterprete().getText());
                save.close();
                JOptionPane.showMessageDialog(null,
                        "El archivo se a guardado Exitosamente",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Su archivo no se ha guardado",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

//Metodo que muestra un dialogo que contiene la informacion del desarrollador
    public void mostrarInformacionDesarrollador() {
        ImageIcon desarrollador = new ImageIcon("desarrollador.png");
        String informacion = "";
        informacion = "                     Analizador Lexico\n\n"
                + "                      Desarrollado por:\n"
                + "            Ivan Fabricio Racancoj García\n"
                + "                            201731115\n5to Semestre Ing. Sistemas CUNOC - USAC";
        JOptionPane.showMessageDialog(analizador, informacion, "About...", JOptionPane.INFORMATION_MESSAGE, desarrollador);
    }

    //Metodo encargado de guardar en un archivo con nombre del path recibido como parametro y escribiendo
    //en el documento el texto contenido en el jEditorPanel del la tabla actual o abierta
    private void guardarComo(String path) {

    }

    //Metodo que cambia el estado de los botones "guardar" y "guardar como" dependiendo de las tablas abiertas
    private void estadoGuardar() {
        boolean estado = true;
        if (analizador.jTabbedPane.getComponentCount() < 1) {
            estado = false;
        } else {
            estado = true;
        }
        analizador.jMenuItemSave.setEnabled(estado);
        analizador.jMenuItemSaveAs.setEnabled(estado);
        analizador.jMenuItemCloseTab.setEnabled(estado);
    }

    //Metodo encargado de cerrar todas las ventanas abiertas
    public void cerrarVentanas(int tamaño) {
        for (int i = tamaño - 1; i >= 0; i--) {
            cerrarVentana(i);
        }
        if (analizador.getComponentCount() < 1) {
            System.exit(0);
        }
    }

}
