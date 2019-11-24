package codigo3d.backend.objetos;

import java.util.HashMap;

/**
 * 
 * @author fabricioRG
 */
public class TablaTipos {
    
    public final static String [] TIPOS = {"boolean", "char", "byte", "int", "long", "float", "double", "string"};
    public final int BOOLEAN = 0;
    public final int CHAR = 1;
    public final int BYTE = 2;
    public final int INT = 3;
    public final int LONG = 4;
    public final int FLOAT = 5;
    public final int DOUBLE = 6;
    public final int STRING = 7;
    
    private HashMap <String,Tipo> tipos = null;
    private static TablaTipos INSTANCE = null;

    private TablaTipos() {
        tipos = new HashMap<>();
        fillTable();
    }
    
    private synchronized static void createInstance(){
        if(INSTANCE == null){
            INSTANCE = new TablaTipos();
        }
    }
    
    public static TablaTipos getInstance(){
        if(INSTANCE == null){
            createInstance();
        }
        return INSTANCE;
    }
    
    private void fillTable(){
        for (int i = 0; i < TIPOS.length; i++) {
            Tipo tipo = new Tipo(TIPOS[i]);
            tipo.setPosicion(i);
            if(i > 0){
                tipo.setHijo(tipos.get(TIPOS[i-1]));
                if(i < TIPOS.length){
                    tipos.get(TIPOS[i - 1]).setPadre(tipo);
                }
            }
            tipos.put(tipo.getNombre(), tipo);
        }
    }
    
    public Tipo getTipoByKey(String key){
        return tipos.get(key);
    }
    
    public Tipo getBoolean(){
        return TablaTipos.getInstance().getTipoByKey(TIPOS[BOOLEAN]);
    }
    
    public Tipo getChar(){
        return TablaTipos.getInstance().getTipoByKey(TIPOS[CHAR]);
    }
    
    public Tipo getByte(){
        return TablaTipos.getInstance().getTipoByKey(TIPOS[BYTE]);
    }
    
    public Tipo getInt(){
        return TablaTipos.getInstance().getTipoByKey(TIPOS[INT]);
    }
    
    public Tipo getLong(){
        return TablaTipos.getInstance().getTipoByKey(TIPOS[LONG]);
    }
    
    public Tipo getFloat(){
        return TablaTipos.getInstance().getTipoByKey(TIPOS[FLOAT]);
    }
    
    public Tipo getDouble(){
        return TablaTipos.getInstance().getTipoByKey(TIPOS[DOUBLE]);
    }
    
    public Tipo getString(){
        return TablaTipos.getInstance().getTipoByKey(TIPOS[STRING]);
    }
    
    public boolean isString(Tipo token){
        return token.getNombre().equals(TIPOS[STRING]);
    }
    
    public boolean isDouble(Tipo token){
        return token.getNombre().equals(TIPOS[DOUBLE]);
    }
    
    public boolean isFloat(Tipo token){
        return token.getNombre().equals(TIPOS[FLOAT]);
    }
    
    public boolean isLong(Tipo token){
        return token.getNombre().equals(TIPOS[LONG]);
    }
    
    public boolean isInt(Tipo token){
        return token.getNombre().equals(TIPOS[INT]);
    }
    
    public boolean isByte(Tipo token){
        return token.getNombre().equals(TIPOS[BYTE]);
    }
    
    public boolean isChar(Tipo token){
        return token.getNombre().equals(TIPOS[CHAR]);
    }
    
    public boolean isBoolean(Tipo token){
        return token.getNombre().equals(TIPOS[BOOLEAN]);
    }
    
    public boolean isNumber(Tipo token){
        return isByte(token) || isChar(token) || isInt(token) || isLong(token) || isFloat(token)
                || isDouble(token);
    }
    
    public boolean isCompatible(Tipo token1, Tipo token2){
        if(isString(token1) || isString(token2)){
            return token1.getNombre().equals(token2.getNombre());
        } else {            
            return token1.getPosicion() <= token2.getPosicion();
        }
    }
    
}
