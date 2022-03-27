package compiler;



public class Token {
    public enum TokenType {
        ELSE, IF, INT, RETURN, VOID, WHILE, //Page 491 #1
        ADD, SUB, MULT, DIV, LESS, LESSEQU, GRE, GREEQU, //Page 491 #2
        EQUAL, NOTEQUAL, ASSIGN, SEMICOLON, COMMA, BEGPAR, ENDPAR, //Page 491, #2
        BEGSBRA, ENDSBRA, BEGBRA, ENDBRA,  //Page 491, #2
        ID, NUM, //Page 492 #3
        EOF, ERROR
    }
    private TokenType tokenType;
    private Object tokenData;
    public Token (TokenType type) {
        this (type, null);
    }
    public Token (TokenType type, Object data) {
        tokenType = type;
        tokenData = data;
    }
    
    public TokenType getType(){return tokenType;}
    public Object getData(){return tokenData;}
    public void setType(TokenType type){tokenType=type;}
    public void setData(Object data){tokenData=data;}
}