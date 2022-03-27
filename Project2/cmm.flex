
package compiler.project1;
import java.io.IOException;

%%

%public
%class CMinusScanner2
%implements Scanner

%type Token

%unicode

%line
%column

%scanerror CMinusScannerException
%yylexthrow CMinusScannerException

%init{
  nextToken = null;
%init}


%{
  private Token nextToken;

  private Token next()
  {
    try
    {
      return yylex();
    } catch(IOException e)
    {
      throw new CMinusScannerException(e.toString());
    }
  }

	public Token getNextToken()
	{
    if(nextToken == null)
      nextToken = next();

    Token t = nextToken;
    if(t.getType() != Token.TokenType.EOF)
    {
        nextToken = next();
    }

    return t;
	}
	
	public Token viewNextToken()
	{
    if(nextToken == null)
      nextToken = next();

		return nextToken;
	}
%}

/* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

WhiteSpace = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {DocumentationComment}

TraditionalComment = "/*" [^*] ~"*/" | "/*" "*"+ "/"
DocumentationComment = "/*" "*"+ [^/*] ~"*/"

/* identifiers */
Identifier = ([a-zA-Z])+

/* integer literals */
DecIntegerLiteral = [0-9]+

%%

<YYINITIAL> {

  /* keywords */
  "else"		{ return new Token(Token.TokenType.ELSE);   }
  "if"			{ return new Token(Token.TokenType.IF);	   } 
  "int"			{ return new Token(Token.TokenType.INT);	   }
  "return"	{ return new Token(Token.TokenType.RETURN); }
  "void"		{ return new Token(Token.TokenType.VOID);   }
  "while"		{ return new Token(Token.TokenType.WHILE);  }
    
  /* separators */
  "("                            { return new Token(Token.TokenType.BEGPAR); 	}
  ")"                            { return new Token(Token.TokenType.ENDPAR); 	}
  "{"                            { return new Token(Token.TokenType.BEGBRA); 	}
  "}"                            { return new Token(Token.TokenType.ENDBRA); 	}
  "["                            { return new Token(Token.TokenType.BEGSBRA); 	}
  "]"                            { return new Token(Token.TokenType.ENDSBRA); 	}
  ";"                            { return new Token(Token.TokenType.SEMICOLON);  }
  ","                            { return new Token(Token.TokenType.COMA);		}
  
  /* operators */
  "="                            { return new Token(Token.TokenType.EQUAL);		}
  ">"                            { return new Token(Token.TokenType.LESS);		}
  "<"                            { return new Token(Token.TokenType.GRE);		}
  "=="                           { return new Token(Token.TokenType.EQUAL);		}
  "<="                           { return new Token(Token.TokenType.LESSEQU);	}
  ">="                           { return new Token(Token.TokenType.GREEQU);		}
  "!="                           { return new Token(Token.TokenType.NOTEQUAL);	}
  "+"                            { return new Token(Token.TokenType.ADD);		}
  "-"                            { return new Token(Token.TokenType.SUB);		}
  "*"                            { return new Token(Token.TokenType.MULT);		}
  "/"                            { return new Token(Token.TokenType.DIV);		}

  /* numeric literals */

  /* This is matched together with the minus, because the number is too big to 
     be represented by a positive integer. 
  "-2147483648"                  { return symbol(INTEGER_LITERAL, Integer.valueOf(Integer.MIN_VALUE)); }*/
  
  {DecIntegerLiteral}            { return new Token(Token.TokenType.NUM, Integer.valueOf(yytext())); }
  
  /* comments */
  {Comment}                      { /* ignore */ }

  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }

  /* identifiers */ 
  {Identifier}                   { return new Token(Token.TokenType.ID, yytext()); }  
}

/* error fallback */
[^]                              { throw new CMinusScannerException("Illegal character \""+yytext()+
                                                              "\" at line "+yyline+", column "+yycolumn); }
<<EOF>>                          { return new Token(Token.TokenType.EOF); }