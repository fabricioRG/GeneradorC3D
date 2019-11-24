package codigo3d.backend.analizadores;

import java_cup.runtime.*;
import static codigo3d.backend.analizadores.sym.*;
import codigo3d.backend.manejadores.ManejadorParser;

%% //separador de area

/* opciones y declaraciones de jflex */

%public
%class Lexer1
%cup
%cupdebug
%line
%column

%{
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

%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace = [ \t\f]
EndOfLineComment = "--" {InputCharacter}* {LineTerminator}?
Comment   = "<!--" [^*] ~"-->" | "<!--" "-->/"
Text = "\"" [^*] ~"\"" | "\"" "\""
Zero = 0
DecInt = [1-9][0-9]*
Integer = {Zero} | {DecInt}
Double = {Integer} \. [0-9]+
Float = {Double} "f" | {Integer} "f"
Ident = [A-Za-z_$] [A-Za-z_$0-9]*

%% // separador de areas

/* reglas lexicas */
<YYINITIAL> {


	"boolean" {return symbol(BOOLEAN, yytext());}
	"char" {return symbol(CHAR, yytext());}
	"byte" {return symbol(BYTE, yytext());}
	"int" {return symbol(INT, yytext());}
	"long" {return symbol(LONG, yytext());}
	"float" {return symbol(FLOAT, yytext());}
	"double" {return symbol(DOUBLE, yytext());}
	"string" {return symbol(STRING, yytext());}
	"NOT" {return symbol(NOT, yytext());}
	"AND" {return symbol(AND, yytext());}
	"OR" {return symbol(OR, yytext());}
	"TRUE" {return symbol(TRUE, yytext());}
	"FALSE" {return symbol(FALSE, yytext());}
	"ARRAY" {return symbol(ARRAY, yytext());}
	"IF" {return symbol(IF, yytext());}
	"ELSIF" {return symbol(ELSIF, yytext());}
	"ELSE" {return symbol(ELSE, yytext());}
	"WHILE" {return symbol(WHILE, yytext());}
	"DO" {return symbol(DO, yytext());}
	"FOR" {return symbol(FOR, yytext());}
	"PRINT" {return symbol(PRINT, yytext());}
	"PRINTLN" {return symbol(PRINTLN, yytext());}
	"," {return symbol(COMMA, yytext());}
	":" {return symbol(COLON, yytext());}
	"(" {return symbol(OPEN_BRK, yytext());}
	")" {return symbol(CLOSE_BRK, yytext());}
	"[" {return symbol(OPEN_SQR, yytext());}
	"]" {return symbol(CLOSE_SQR, yytext());}
	";" {return symbol(SEMICOLON, yytext());}
	"<-" {return symbol(ASSIGN, yytext());}
	"+" {return symbol(PLUS, yytext());}
	"-" {return symbol(MINUS, yytext());}
	"*" {return symbol(MULTI, yytext());}
	"/" {return symbol(DIVISION, yytext());}
	"%" {return symbol(MODULE, yytext());}
	"<=" {return symbol(LOWER_EQ, yytext());}
	">=" {return symbol(HIGHER_EQ, yytext());}
	"<" {return symbol(LOWER, yytext());}
	">" {return symbol(HIGHER, yytext());}
	"!=" {return symbol(NOT_EQUAL, yytext());}
	"==" {return symbol(EQUAL, yytext());}
	{EndOfLineComment} {/*return symbol(LN_COMMENT, yytext());*/}
	{Comment} {/*return symbol(COMMENT, yytext());*/}
	{Text} {return symbol(TEXT, yytext());}
	{Integer} {return symbol(NUM_INTEGER, yytext());}
	{Double} {return symbol(NUM_DOUBLE, yytext());}
	{Float} {return symbol(NUM_FLOAT, yytext());}
	{Ident} {return symbol(VARIABLE, yytext());}
	{WhiteSpace} 		{/*Nothing to do*/}
	{LineTerminator} 	{/*Nothing to do*/}

}
[^] {error("Simbolo invalido <"+ yytext()+">");}
<<EOF>>                 { return symbol(EOF); }
