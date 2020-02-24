package JFlexNCup;

import java_cup.runtime.*;
import APIServices.CompileError;
import java.util.ArrayList;

%%

%{
    StringBuffer string = new StringBuffer();
    public ArrayList<CompileError> errors = new ArrayList();
%}

%public
%class Scanner
%cupsym Sym
%cup
%char
%column
%full
%ignorecase
%line
%unicode

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]
Digit          = [0-9]
Number         = {Digit}+("." {Digit}+)?
//CharString     = "\"" ~"\""
Letter         = [a-zA-ZñÑ]
Id             = {Letter}("."|"_"|{Digit}|{Letter})*|"."(({Letter}|"_"|".")({Letter}|"."|"_"|{Digit})*)?
simpleComment  = "#" {InputCharacter}* {LineTerminator}?
multiLineComment   = "#*" [^*] ~"*#" | "#*" "*"+ "#"

%state STRING

%%

// ------> Operators and Symbols

<YYINITIAL> "%%"                      { return new Symbol(Sym.mod, yycolumn, yyline, "%%"); }
<YYINITIAL> "=="                      { return new Symbol(Sym.equals, yycolumn, yyline, "=="); }
<YYINITIAL> "!="                      { return new Symbol(Sym.notEquals, yycolumn, yyline, "!="); }
<YYINITIAL> ">="                      { return new Symbol(Sym.greaterEquals, yycolumn, yyline, ">="); }
<YYINITIAL> "<="                      { return new Symbol(Sym.lesserEquals, yycolumn, yyline, "<="); }
<YYINITIAL> "=>"                      { return new Symbol(Sym.arrowFunction, yycolumn, yyline, "=>"); }
<YYINITIAL> "="                       { return new Symbol(Sym.valAsignment, yycolumn, yyline, "="); }
<YYINITIAL> "("                       { return new Symbol(Sym.openingPar, yycolumn, yyline, "("); }
<YYINITIAL> ")"                       { return new Symbol(Sym.closingPar, yycolumn, yyline, ")"); }
<YYINITIAL> "["                       { return new Symbol(Sym.openingBracket, yycolumn, yyline, "["); }
<YYINITIAL> "]"                       { return new Symbol(Sym.closingBracket, yycolumn, yyline, "]"); }
<YYINITIAL> "{"                       { return new Symbol(Sym.openingCurly, yycolumn, yyline, "{"); }
<YYINITIAL> "}"                       { return new Symbol(Sym.closingCurly, yycolumn, yyline, "}"); }
<YYINITIAL> ","                       { return new Symbol(Sym.comma, yycolumn, yyline, ","); }
<YYINITIAL> ";"                       { return new Symbol(Sym.semicolon, yycolumn, yyline, ";"); }
<YYINITIAL> "+"                       { return new Symbol(Sym.plus, yycolumn, yyline, "+"); }
<YYINITIAL> "-"                       { return new Symbol(Sym.minus, yycolumn, yyline, "-"); }
<YYINITIAL> "*"                       { return new Symbol(Sym.times, yycolumn, yyline, "*"); }
<YYINITIAL> "/"                       { return new Symbol(Sym.div, yycolumn, yyline, "/"); }
<YYINITIAL> "^"                       { return new Symbol(Sym.power, yycolumn, yyline, "^"); }
<YYINITIAL> ">"                       { return new Symbol(Sym.greater, yycolumn, yyline, ">"); }
<YYINITIAL> "<"                       { return new Symbol(Sym.lesser, yycolumn, yyline, "<"); }
<YYINITIAL> "!"                       { return new Symbol(Sym.not, yycolumn, yyline, "!"); }
<YYINITIAL> "&"                       { return new Symbol(Sym.and, yycolumn, yyline, "&"); }
<YYINITIAL> "|"                       { return new Symbol(Sym.or, yycolumn, yyline, "|"); }
<YYINITIAL> "?"                       { return new Symbol(Sym.ternary, yycolumn, yyline, "?"); }
<YYINITIAL> ":"                       { return new Symbol(Sym.colon, yycolumn, yyline, ":"); }

// -------> Key Words

<YYINITIAL> "if"                      { return new Symbol(Sym.ifKeyword, yycolumn, yyline, "if"); }
<YYINITIAL> "else"                    { return new Symbol(Sym.elseKeyword, yycolumn, yyline, "else"); }
<YYINITIAL> "switch"                  { return new Symbol(Sym.switchKeyword, yycolumn, yyline, "switch"); }
<YYINITIAL> "case"                    { return new Symbol(Sym.caseKeyword, yycolumn, yyline, "case"); }
<YYINITIAL> "break"                   { return new Symbol(Sym.breakKeyword, yycolumn, yyline, "break"); }
<YYINITIAL> "while"                   { return new Symbol(Sym.whileKeyword, yycolumn, yyline, "while"); }
<YYINITIAL> "do"                      { return new Symbol(Sym.doKeyword, yycolumn, yyline, "do"); }
<YYINITIAL> "for"                     { return new Symbol(Sym.forKeyword, yycolumn, yyline, "for"); }
<YYINITIAL> "in"                      { return new Symbol(Sym.inKeyword, yycolumn, yyline, "in"); }
<YYINITIAL> "continue"                { return new Symbol(Sym.continueKeyword, yycolumn, yyline, "continue"); }
<YYINITIAL> "return"                  { return new Symbol(Sym.returnKeyword, yycolumn, yyline, "return"); }
<YYINITIAL> "function"                { return new Symbol(Sym.functionKeyword, yycolumn, yyline, "else"); }
<YYINITIAL> "default"                 { return new Symbol(Sym.defaultKeyword, yycolumn, yyline, "default"); }

// -----> Values

<YYINITIAL> "null"                    { return new Symbol(Sym.nullValue, yycolumn, yyline, "null"); }
<YYINITIAL> {Number}                  { return new Symbol(Sym.numberValue, yycolumn, yyline, yytext()); }
<YYINITIAL> "false"                   { return new Symbol(Sym.falseValue, yycolumn, yyline, "false"); }
<YYINITIAL> "true"                    { return new Symbol(Sym.trueValue, yycolumn, yyline, "true"); }
<YYINITIAL> {Id}                      { return new Symbol(Sym.id, yycolumn, yyline, yytext()); }

// --------> String

<YYINITIAL> "\""                      { string.setLength(0); yybegin(STRING); }
<STRING> "\""                         { yybegin(YYINITIAL); 
                                        return new Symbol(Sym.stringValue, 
                                        string.toString()); 
                                      }
<STRING> [^\n\r"\""\\]+               { string.append( yytext() ); } 
<STRING> \\t                          { string.append('\t'); }
<STRING> \\n                          { string.append('\n'); }
<STRING> \\r                          { string.append('\r'); }
<STRING> "\\\""                       { string.append('\"'); }
<STRING> \\                           { string.append('\\'); }

<YYINITIAL> {WhiteSpace}              {}
<YYINITIAL> {simpleComment}           { System.out.println("encontre un comentario simple"); }
<YYINITIAL> {multiLineComment}        { System.out.println("encontre un comentario multilinea"); }

<YYINITIAL> .                         { errors.add(new CompileError("Lexico", yytext() + " no pertenece al lenguaje Arit", yyline, yycolumn)); } 