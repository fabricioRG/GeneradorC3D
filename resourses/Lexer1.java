/* The following code was generated by JFlex 1.6.1 */

package codigo3d.backend.analizadores;

import java_cup.runtime.*;
import static codigo3d.backend.analizadores.sym.*;
import codigo3d.backend.manejadores.ManejadorParser;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.6.1
 * from the specification file <tt>AnalizadorLexico1.lex</tt>
 */
public class Lexer1 implements java_cup.runtime.Scanner {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0, 0
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\3\1\2\1\0\1\3\1\1\22\0\1\3\1\6\1\12"+
    "\1\0\1\17\1\63\2\0\1\66\1\67\1\7\1\73\1\64\1\4"+
    "\1\15\1\11\1\13\11\14\1\65\1\72\1\5\1\74\1\10\2\0"+
    "\1\43\1\17\1\60\1\44\1\47\1\50\1\17\1\56\1\54\2\17"+
    "\1\51\1\61\1\40\1\41\1\57\1\17\1\45\1\52\1\42\1\46"+
    "\1\17\1\55\1\17\1\53\1\17\1\70\1\0\1\71\1\0\1\17"+
    "\1\0\1\24\1\20\1\26\1\35\1\23\1\16\1\34\1\27\1\33"+
    "\2\17\1\22\1\17\1\25\1\21\2\17\1\30\1\37\1\32\1\36"+
    "\1\62\2\17\1\31\1\17\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uff95\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\2\2\1\3\1\4\1\1\1\5\1\6"+
    "\1\7\1\1\2\10\26\11\1\12\1\13\1\14\1\15"+
    "\1\16\1\17\1\20\1\21\1\22\1\1\1\23\1\24"+
    "\1\0\1\25\1\26\1\27\1\0\1\30\1\0\1\31"+
    "\12\11\1\32\3\11\1\33\4\11\1\34\4\11\1\0"+
    "\1\35\2\23\1\0\1\30\1\36\6\11\1\37\2\11"+
    "\1\40\1\11\1\41\2\11\1\42\6\11\1\43\1\0"+
    "\2\11\1\44\1\45\1\46\3\11\1\47\1\11\1\50"+
    "\5\11\1\51\1\52\2\0\1\53\4\11\1\54\1\55"+
    "\1\56\1\57\1\60\1\61\1\62\2\0\1\11\1\63"+
    "\1\64\1\65\1\11\2\0\1\66\1\67\2\70";

  private static int [] zzUnpackAction() {
    int [] result = new int[154];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\75\0\172\0\75\0\267\0\364\0\u0131\0\75"+
    "\0\u016e\0\75\0\u01ab\0\u01e8\0\u0225\0\u0262\0\u029f\0\u02dc"+
    "\0\u0319\0\u0356\0\u0393\0\u03d0\0\u040d\0\u044a\0\u0487\0\u04c4"+
    "\0\u0501\0\u053e\0\u057b\0\u05b8\0\u05f5\0\u0632\0\u066f\0\u06ac"+
    "\0\u06e9\0\u0726\0\u0763\0\u07a0\0\75\0\75\0\75\0\75"+
    "\0\75\0\75\0\75\0\75\0\u07dd\0\u081a\0\75\0\u0857"+
    "\0\75\0\75\0\75\0\u0894\0\u0894\0\u08d1\0\75\0\u090e"+
    "\0\u094b\0\u0988\0\u09c5\0\u0a02\0\u0a3f\0\u0a7c\0\u0ab9\0\u0af6"+
    "\0\u0b33\0\u029f\0\u0b70\0\u0bad\0\u0bea\0\u029f\0\u0c27\0\u0c64"+
    "\0\u0ca1\0\u0cde\0\u029f\0\u0d1b\0\u0d58\0\u0d95\0\u0dd2\0\u0e0f"+
    "\0\75\0\u0e4c\0\75\0\u0e89\0\75\0\u0ec6\0\u0f03\0\u0f40"+
    "\0\u0f7d\0\u0fba\0\u0ff7\0\u1034\0\u029f\0\u1071\0\u10ae\0\u029f"+
    "\0\u10eb\0\u029f\0\u1128\0\u1165\0\u029f\0\u11a2\0\u11df\0\u121c"+
    "\0\u1259\0\u1296\0\u12d3\0\75\0\u1310\0\u134d\0\u138a\0\u029f"+
    "\0\u029f\0\u029f\0\u13c7\0\u1404\0\u1441\0\u029f\0\u147e\0\u029f"+
    "\0\u14bb\0\u14f8\0\u1535\0\u1572\0\u15af\0\u029f\0\u029f\0\u15ec"+
    "\0\u1629\0\u029f\0\u1666\0\u16a3\0\u16e0\0\u171d\0\u029f\0\u029f"+
    "\0\u029f\0\u029f\0\u029f\0\u029f\0\u175a\0\u1797\0\u17d4\0\u1811"+
    "\0\u029f\0\u029f\0\u029f\0\u184e\0\u188b\0\u18c8\0\u029f\0\u029f"+
    "\0\75\0\u15ec";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[154];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\2\1\3\2\4\1\5\1\6\1\7\1\10\1\11"+
    "\1\12\1\13\1\14\1\15\1\2\1\16\1\17\1\20"+
    "\1\17\1\21\3\17\1\22\1\17\1\23\2\17\1\24"+
    "\1\17\1\25\1\17\1\26\1\27\1\30\1\31\1\32"+
    "\1\33\2\17\1\34\1\35\1\17\1\36\1\17\1\37"+
    "\1\40\1\17\1\41\1\17\1\42\1\43\1\44\1\45"+
    "\1\46\1\47\1\50\1\51\1\52\1\53\1\54\1\55"+
    "\77\0\1\4\76\0\1\56\74\0\1\57\1\0\1\60"+
    "\65\0\1\61\74\0\1\62\74\0\1\63\7\64\1\0"+
    "\2\64\1\65\62\64\15\0\1\66\1\67\71\0\2\15"+
    "\1\66\1\67\71\0\2\17\1\0\4\17\1\70\40\17"+
    "\25\0\2\17\1\0\45\17\25\0\2\17\1\0\3\17"+
    "\1\71\7\17\1\72\31\17\25\0\2\17\1\0\3\17"+
    "\1\73\41\17\25\0\2\17\1\0\11\17\1\74\33\17"+
    "\25\0\2\17\1\0\5\17\1\75\37\17\25\0\2\17"+
    "\1\0\7\17\1\76\35\17\25\0\2\17\1\0\3\17"+
    "\1\77\41\17\25\0\2\17\1\0\14\17\1\100\30\17"+
    "\25\0\2\17\1\0\23\17\1\101\21\17\25\0\2\17"+
    "\1\0\27\17\1\102\15\17\25\0\2\17\1\0\27\17"+
    "\1\103\15\17\25\0\2\17\1\0\22\17\1\104\4\17"+
    "\1\105\15\17\25\0\2\17\1\0\23\17\1\106\21\17"+
    "\25\0\2\17\1\0\33\17\1\107\11\17\25\0\2\17"+
    "\1\0\23\17\1\110\1\17\1\111\17\17\25\0\2\17"+
    "\1\0\42\17\1\112\2\17\25\0\2\17\1\0\32\17"+
    "\1\113\12\17\25\0\2\17\1\0\40\17\1\114\4\17"+
    "\25\0\2\17\1\0\27\17\1\115\15\17\25\0\2\17"+
    "\1\0\25\17\1\116\17\17\25\0\2\17\1\0\3\17"+
    "\1\117\41\17\75\0\1\120\105\0\1\121\1\56\1\122"+
    "\1\123\72\56\4\0\1\124\70\0\12\64\1\125\62\64"+
    "\13\0\2\126\73\0\2\17\1\0\3\17\1\127\41\17"+
    "\25\0\2\17\1\0\3\17\1\130\41\17\25\0\2\17"+
    "\1\0\14\17\1\131\30\17\25\0\2\17\1\0\7\17"+
    "\1\132\35\17\25\0\2\17\1\0\6\17\1\133\36\17"+
    "\25\0\2\17\1\0\14\17\1\134\30\17\25\0\2\17"+
    "\1\0\14\17\1\135\30\17\25\0\2\17\1\0\20\17"+
    "\1\136\24\17\25\0\2\17\1\0\12\17\1\137\32\17"+
    "\25\0\2\17\1\0\24\17\1\140\20\17\25\0\2\17"+
    "\1\0\30\17\1\141\14\17\25\0\2\17\1\0\26\17"+
    "\1\142\16\17\25\0\2\17\1\0\27\17\1\143\15\17"+
    "\25\0\2\17\1\0\34\17\1\144\10\17\25\0\2\17"+
    "\1\0\27\17\1\145\15\17\25\0\2\17\1\0\33\17"+
    "\1\146\11\17\25\0\2\17\1\0\25\17\1\147\17\17"+
    "\25\0\2\17\1\0\36\17\1\150\6\17\25\0\2\17"+
    "\1\0\36\17\1\151\6\17\25\0\2\17\1\0\36\17"+
    "\1\152\6\17\25\0\2\17\1\0\15\17\1\153\27\17"+
    "\75\0\1\154\13\0\1\123\76\0\1\155\103\0\2\126"+
    "\1\0\1\67\71\0\2\17\1\0\6\17\1\156\36\17"+
    "\25\0\2\17\1\0\4\17\1\157\40\17\25\0\2\17"+
    "\1\0\5\17\1\160\37\17\25\0\2\17\1\0\16\17"+
    "\1\161\26\17\25\0\2\17\1\0\12\17\1\162\32\17"+
    "\25\0\2\17\1\0\20\17\1\163\24\17\25\0\2\17"+
    "\1\0\2\17\1\164\42\17\25\0\2\17\1\0\15\17"+
    "\1\165\27\17\25\0\2\17\1\0\31\17\1\166\13\17"+
    "\25\0\2\17\1\0\25\17\1\167\17\17\25\0\2\17"+
    "\1\0\31\17\1\170\4\17\1\171\6\17\25\0\2\17"+
    "\1\0\34\17\1\172\10\17\25\0\2\17\1\0\22\17"+
    "\1\173\22\17\25\0\2\17\1\0\33\17\1\174\11\17"+
    "\25\0\2\17\1\0\22\17\1\175\22\17\25\0\2\17"+
    "\1\0\22\17\1\176\22\17\25\0\2\17\1\0\17\17"+
    "\1\177\25\17\12\0\4\200\1\201\2\200\1\0\65\200"+
    "\13\0\2\17\1\0\14\17\1\202\30\17\25\0\2\17"+
    "\1\0\5\17\1\203\37\17\25\0\2\17\1\0\12\17"+
    "\1\204\32\17\25\0\2\17\1\0\4\17\1\205\40\17"+
    "\25\0\2\17\1\0\7\17\1\206\35\17\25\0\2\17"+
    "\1\0\35\17\1\207\7\17\25\0\2\17\1\0\32\17"+
    "\1\210\12\17\25\0\2\17\1\0\31\17\1\211\13\17"+
    "\25\0\2\17\1\0\22\17\1\212\11\17\1\213\10\17"+
    "\25\0\2\17\1\0\31\17\1\214\13\17\25\0\2\17"+
    "\1\0\24\17\1\215\20\17\12\0\4\200\1\216\74\200"+
    "\1\217\70\200\13\0\2\17\1\0\6\17\1\220\36\17"+
    "\25\0\2\17\1\0\7\17\1\221\35\17\25\0\2\17"+
    "\1\0\5\17\1\222\37\17\25\0\2\17\1\0\16\17"+
    "\1\223\26\17\25\0\2\17\1\0\33\17\1\224\11\17"+
    "\12\0\4\200\1\225\74\200\1\225\3\200\1\226\64\200"+
    "\13\0\2\17\1\0\7\17\1\227\35\17\25\0\2\17"+
    "\1\0\22\17\1\230\22\17\12\0\4\200\1\225\3\200"+
    "\1\231\70\200\1\216\4\200\1\232\63\200";

  private static int [] zzUnpackTrans() {
    int [] result = new int[6405];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\11\1\1\1\11\3\1\1\11\1\1\1\11"+
    "\32\1\10\11\2\1\1\11\1\0\3\11\1\0\1\1"+
    "\1\0\1\11\30\1\1\0\1\11\1\1\1\11\1\0"+
    "\1\11\26\1\1\11\1\0\22\1\2\0\14\1\2\0"+
    "\5\1\2\0\2\1\1\11\1\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[154];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;
  
  /** 
   * The number of occupied positions in zzBuffer beyond zzEndRead.
   * When a lead/high surrogate has been read from the input stream
   * into the final zzBuffer position, this will have a value of 1;
   * otherwise, it will have a value of 0.
   */
  private int zzFinalHighSurrogate = 0;

  /* user code: */
    StringBuilder string = new StringBuilder();
    ManejadorParser mp = null;
  
  private Symbol symbol(int type) {
    return new Symbol(type, yyline+1, yycolumn+1);
  }

  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline+1, yycolumn+1, value);
  }

  private void error(String message)throws Exception {
    mp.printError("Lexico °° "+ message +" Linea: "+(yyline+1)+", Columna: "+(yycolumn+1));
  }

  public void setManejadorParser(ManejadorParser mp){
  	this.mp = mp;
  }



  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public Lexer1(java.io.Reader in) {
    this.zzReader = in;
  }


  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x110000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 202) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length - zzFinalHighSurrogate) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzBuffer.length*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
    }

    /* fill the buffer with new input */
    int requested = zzBuffer.length - zzEndRead;
    int numRead = zzReader.read(zzBuffer, zzEndRead, requested);

    /* not supposed to occur according to specification of java.io.Reader */
    if (numRead == 0) {
      throw new java.io.IOException("Reader returned 0 characters. See JFlex examples for workaround.");
    }
    if (numRead > 0) {
      zzEndRead += numRead;
      /* If numRead == requested, we might have requested to few chars to
         encode a full Unicode character. We assume that a Reader would
         otherwise never return half characters. */
      if (numRead == requested) {
        if (Character.isHighSurrogate(zzBuffer[zzEndRead - 1])) {
          --zzEndRead;
          zzFinalHighSurrogate = 1;
        }
      }
      /* potentially more input available */
      return false;
    }

    /* numRead < 0 ==> end of stream */
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    zzFinalHighSurrogate = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
    if (zzBuffer.length > ZZ_BUFFERSIZE)
      zzBuffer = new char[ZZ_BUFFERSIZE];
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() throws java.io.IOException {
    if (!zzEOFDone) {
      zzEOFDone = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public java_cup.runtime.Symbol next_token() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      int zzCh;
      int zzCharCount;
      for (zzCurrentPosL = zzStartRead  ;
           zzCurrentPosL < zzMarkedPosL ;
           zzCurrentPosL += zzCharCount ) {
        zzCh = Character.codePointAt(zzBufferL, zzCurrentPosL, zzMarkedPosL);
        zzCharCount = Character.charCount(zzCh);
        switch (zzCh) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn += zzCharCount;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
            zzDoEOF();
              {
                return symbol(EOF);
              }
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1: 
            { error("Simbolo invalido <"+ yytext()+">");
            }
          case 57: break;
          case 2: 
            { /*Nothing to do*/
            }
          case 58: break;
          case 3: 
            { return symbol(MINUS, yytext());
            }
          case 59: break;
          case 4: 
            { return symbol(LOWER, yytext());
            }
          case 60: break;
          case 5: 
            { return symbol(MULTI, yytext());
            }
          case 61: break;
          case 6: 
            { return symbol(HIGHER, yytext());
            }
          case 62: break;
          case 7: 
            { return symbol(DIVISION, yytext());
            }
          case 63: break;
          case 8: 
            { return symbol(NUM_INTEGER, yytext());
            }
          case 64: break;
          case 9: 
            { return symbol(VARIABLE, yytext());
            }
          case 65: break;
          case 10: 
            { return symbol(MODULE, yytext());
            }
          case 66: break;
          case 11: 
            { return symbol(COMMA, yytext());
            }
          case 67: break;
          case 12: 
            { return symbol(COLON, yytext());
            }
          case 68: break;
          case 13: 
            { return symbol(OPEN_BRK, yytext());
            }
          case 69: break;
          case 14: 
            { return symbol(CLOSE_BRK, yytext());
            }
          case 70: break;
          case 15: 
            { return symbol(OPEN_SQR, yytext());
            }
          case 71: break;
          case 16: 
            { return symbol(CLOSE_SQR, yytext());
            }
          case 72: break;
          case 17: 
            { return symbol(SEMICOLON, yytext());
            }
          case 73: break;
          case 18: 
            { return symbol(PLUS, yytext());
            }
          case 74: break;
          case 19: 
            { /*return symbol(LN_COMMENT, yytext());*/
            }
          case 75: break;
          case 20: 
            { return symbol(ASSIGN, yytext());
            }
          case 76: break;
          case 21: 
            { return symbol(LOWER_EQ, yytext());
            }
          case 77: break;
          case 22: 
            { return symbol(NOT_EQUAL, yytext());
            }
          case 78: break;
          case 23: 
            { return symbol(HIGHER_EQ, yytext());
            }
          case 79: break;
          case 24: 
            { return symbol(TEXT, yytext());
            }
          case 80: break;
          case 25: 
            { return symbol(NUM_FLOAT, yytext());
            }
          case 81: break;
          case 26: 
            { return symbol(OR, yytext());
            }
          case 82: break;
          case 27: 
            { return symbol(DO, yytext());
            }
          case 83: break;
          case 28: 
            { return symbol(IF, yytext());
            }
          case 84: break;
          case 29: 
            { return symbol(EQUAL, yytext());
            }
          case 85: break;
          case 30: 
            { return symbol(NUM_DOUBLE, yytext());
            }
          case 86: break;
          case 31: 
            { return symbol(INT, yytext());
            }
          case 87: break;
          case 32: 
            { return symbol(NOT, yytext());
            }
          case 88: break;
          case 33: 
            { return symbol(AND, yytext());
            }
          case 89: break;
          case 34: 
            { return symbol(FOR, yytext());
            }
          case 90: break;
          case 35: 
            { return symbol(SEPARATOR, yytext());
            }
          case 91: break;
          case 36: 
            { return symbol(BYTE, yytext());
            }
          case 92: break;
          case 37: 
            { return symbol(LONG, yytext());
            }
          case 93: break;
          case 38: 
            { return symbol(CHAR, yytext());
            }
          case 94: break;
          case 39: 
            { return symbol(TRUE, yytext());
            }
          case 95: break;
          case 40: 
            { return symbol(ELSE, yytext());
            }
          case 96: break;
          case 41: 
            { return symbol(MAIN, yytext());
            }
          case 97: break;
          case 42: 
            { return symbol (VOID, yytext());
            }
          case 98: break;
          case 43: 
            { return symbol(FLOAT, yytext());
            }
          case 99: break;
          case 44: 
            { return symbol(ARRAY, yytext());
            }
          case 100: break;
          case 45: 
            { return symbol(ELSIF, yytext());
            }
          case 101: break;
          case 46: 
            { return symbol(FALSE, yytext());
            }
          case 102: break;
          case 47: 
            { return symbol(SCANN, yytext());
            }
          case 103: break;
          case 48: 
            { return symbol(SCANS, yytext());
            }
          case 104: break;
          case 49: 
            { return symbol(WHILE, yytext());
            }
          case 105: break;
          case 50: 
            { return symbol(PRINT, yytext());
            }
          case 106: break;
          case 51: 
            { return symbol(RETURN, yytext());
            }
          case 107: break;
          case 52: 
            { return symbol(DOUBLE, yytext());
            }
          case 108: break;
          case 53: 
            { return symbol(STRING, yytext());
            }
          case 109: break;
          case 54: 
            { return symbol(BOOLEAN, yytext());
            }
          case 110: break;
          case 55: 
            { return symbol(PRINTLN, yytext());
            }
          case 111: break;
          case 56: 
            { /*return symbol(COMMENT, yytext());*/
            }
          case 112: break;
          default:
            zzScanError(ZZ_NO_MATCH);
        }
      }
    }
  }

  /**
   * Converts an int token code into the name of the
   * token by reflection on the cup symbol class/interface sym
   *
   * This code was contributed by Karl Meissner <meissnersd@yahoo.com>
   */
  private String getTokenName(int token) {
    try {
      java.lang.reflect.Field [] classFields = sym.class.getFields();
      for (int i = 0; i < classFields.length; i++) {
        if (classFields[i].getInt(null) == token) {
          return classFields[i].getName();
        }
      }
    } catch (Exception e) {
      e.printStackTrace(System.err);
    }

    return "UNKNOWN TOKEN";
  }

  /**
   * Same as next_token but also prints the token to standard out
   * for debugging.
   *
   * This code was contributed by Karl Meissner <meissnersd@yahoo.com>
   */
  public java_cup.runtime.Symbol debug_next_token() throws java.io.IOException {
    java_cup.runtime.Symbol s = next_token();
    System.out.println( "line:" + (yyline+1) + " col:" + (yycolumn+1) + " --"+ yytext() + "--" + getTokenName(s.sym) + "--");
    return s;
  }

  /**
   * Runs the scanner on input files.
   *
   * This main method is the debugging routine for the scanner.
   * It prints debugging information about each returned token to
   * System.out until the end of file is reached, or an error occured.
   *
   * @param argv   the command line, contains the filenames to run
   *               the scanner on.
   */
  public static void main(String argv[]) {
    if (argv.length == 0) {
      System.out.println("Usage : java Lexer1 [ --encoding <name> ] <inputfile(s)>");
    }
    else {
      int firstFilePos = 0;
      String encodingName = "UTF-8";
      if (argv[0].equals("--encoding")) {
        firstFilePos = 2;
        encodingName = argv[1];
        try {
          java.nio.charset.Charset.forName(encodingName); // Side-effect: is encodingName valid? 
        } catch (Exception e) {
          System.out.println("Invalid encoding '" + encodingName + "'");
          return;
        }
      }
      for (int i = firstFilePos; i < argv.length; i++) {
        Lexer1 scanner = null;
        try {
          java.io.FileInputStream stream = new java.io.FileInputStream(argv[i]);
          java.io.Reader reader = new java.io.InputStreamReader(stream, encodingName);
          scanner = new Lexer1(reader);
          while ( !scanner.zzAtEOF ) scanner.debug_next_token();
        }
        catch (java.io.FileNotFoundException e) {
          System.out.println("File not found : \""+argv[i]+"\"");
        }
        catch (java.io.IOException e) {
          System.out.println("IO error scanning file \""+argv[i]+"\"");
          System.out.println(e);
        }
        catch (Exception e) {
          System.out.println("Unexpected exception:");
          e.printStackTrace();
        }
      }
    }
  }


}
