package codigo3d.backend.objetos;

import java.util.HashMap;

/**
 *
 * @author fabricioRG
 */
public class TablaTipos {

    private HashMap<String, Tipo> tipos = null;
    private static TablaTipos INSTANCE = null;

    private TablaTipos() {
        tipos = new HashMap<>();
        fillTable();
    }

    private synchronized static void createInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TablaTipos();
        }
    }

    public static TablaTipos getInstance() {
        if (INSTANCE == null) {
            createInstance();
        }
        return INSTANCE;
    }

    private void fillTable() {
        for (int i = 0; i < TIPOS.length; i++) {
            Tipo tipo = new Tipo(TIPOS[i]);
            tipo.setPosicion(i);
            tipo.setValor(VALORES[i]);
            if (i > 0) {
                tipo.setHijo(tipos.get(TIPOS[i - 1]));
                if (i < TIPOS.length) {
                    tipos.get(TIPOS[i - 1]).setPadre(tipo);
                }
            }
            tipos.put(tipo.getNombre(), tipo);
        }
    }

    public Tipo getTipoByKey(String key) {
        return tipos.get(key);
    }

    public Tipo getBoolean() {
        return TablaTipos.getInstance().getTipoByKey(TIPOS[BOOLEAN]);
    }

    public Tipo getChar() {
        return TablaTipos.getInstance().getTipoByKey(TIPOS[CHAR]);
    }

    public Tipo getByte() {
        return TablaTipos.getInstance().getTipoByKey(TIPOS[BYTE]);
    }

    public Tipo getInt() {
        return TablaTipos.getInstance().getTipoByKey(TIPOS[INT]);
    }

    public Tipo getLong() {
        return TablaTipos.getInstance().getTipoByKey(TIPOS[LONG]);
    }

    public Tipo getFloat() {
        return TablaTipos.getInstance().getTipoByKey(TIPOS[FLOAT]);
    }

    public Tipo getDouble() {
        return TablaTipos.getInstance().getTipoByKey(TIPOS[DOUBLE]);
    }

    public Tipo getString() {
        return TablaTipos.getInstance().getTipoByKey(TIPOS[STRING]);
    }

    public Tipo getVoid() {
        return TablaTipos.getInstance().getTipoByKey(TIPOS[VOID]);
    }

    public Tipo getError() {
        return TablaTipos.getInstance().getTipoByKey(TIPOS[ERROR]);
    }

    public boolean isError(Tipo token) {
        return token.getNombre().equals(TIPOS[ERROR]);
    }

    public boolean isVoid(Tipo token) {
        return token.getNombre().equals(TIPOS[VOID]);
    }

    public boolean isString(Tipo token) {
        return token.getNombre().equals(TIPOS[STRING]);
    }

    public boolean isDouble(Tipo token) {
        return token.getNombre().equals(TIPOS[DOUBLE]);
    }

    public boolean isFloat(Tipo token) {
        return token.getNombre().equals(TIPOS[FLOAT]);
    }

    public boolean isLong(Tipo token) {
        return token.getNombre().equals(TIPOS[LONG]);
    }

    public boolean isInt(Tipo token) {
        return token.getNombre().equals(TIPOS[INT]);
    }

    public boolean isByte(Tipo token) {
        return token.getNombre().equals(TIPOS[BYTE]);
    }

    public boolean isChar(Tipo token) {
        return token.getNombre().equals(TIPOS[CHAR]);
    }

    public boolean isBoolean(Tipo token) {
        return token.getNombre().equals(TIPOS[BOOLEAN]);
    }

    public boolean isNumber(Tipo token) {
        return isByte(token) || isChar(token) || isInt(token) || isLong(token) || isFloat(token)
                || isDouble(token);
    }

    public boolean isIntegerNumber(Tipo token) {
        return isByte(token) || isChar(token) || isInt(token) || isLong(token);
    }

    public boolean isCompatible(Tipo token1, Tipo token2) {
        if (isBoolean(token1) || isBoolean(token2)) {
            return isBoolean(token1) && isBoolean(token2);
        } else if (isNumber(token1) && isNumber(token2)) {
            return token1.getPosicion() <= token2.getPosicion();
        } else {
            return isString(token1) && isString(token2);
        }
    }

    public String getTypeBoolean() {
        return TYPE_BOOLEAN_C;
    }

    public String getTypeChar() {
        return TYPE_CHAR_C;
    }

    public String getTypeByte() {
        return TYPE_BYTE_C;
    }

    public String getTypeLong() {
        return TYPE_LONG_C;
    }

    public String getTypeFloat() {
        return TYPE_FLOAT_C;
    }

    public String getTypeDouble() {
        return TYPE_DOUBLE_C;
    }

    public String getTypeString() {
        return TYPE_STRING_C;
    }

    public String getTypeVoid() {
        return TYPE_VOID_C;
    }

    public final static String[] TIPOS = {"boolean", "char", "byte", "int", "long", "float", "double", "string", "void", "error"};
    public final static String[] VALORES = {TablaTipos.TYPE_BOOLEAN_C, TablaTipos.TYPE_CHAR_C, TablaTipos.TYPE_BYTE_C,
        TablaTipos.TYPE_INT_C, TablaTipos.TYPE_LONG_C, TablaTipos.TYPE_FLOAT_C, TablaTipos.TYPE_DOUBLE_C,
        TablaTipos.TYPE_STRING_C, TablaTipos.TYPE_VOID_C, TablaTipos.TYPE_ERROR_C};
    public final int BOOLEAN = 0;
    public final int CHAR = 1;
    public final int BYTE = 2;
    public final int INT = 3;
    public final int LONG = 4;
    public final int FLOAT = 5;
    public final int DOUBLE = 6;
    public final int STRING = 7;
    public final int VOID = 8;
    public final int ERROR = 9;
    public static final String TYPE_BOOLEAN_C = "bool";
    public static final String TYPE_CHAR_C = "char";
    public static final String TYPE_BYTE_C = "unsigned char";
    public static final String TYPE_INT_C = "int";
    public static final String TYPE_LONG_C = "long";
    public static final String TYPE_FLOAT_C = "float";
    public static final String TYPE_STRING_C = "string";
    public static final String TYPE_DOUBLE_C = "double";
    public static final String TYPE_VOID_C = "void";
    public static final String TYPE_ERROR_C = "error";
}
