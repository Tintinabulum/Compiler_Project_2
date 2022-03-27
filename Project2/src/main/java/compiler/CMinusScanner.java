package compiler;

import java.io.BufferedReader;
import java.io.IOException;
import compiler.Token.TokenType;
//Testing imports
import java.io.FileReader;
import java.io.PrintWriter;

public class CMinusScanner implements compiler.Scanner {
    private BufferedReader inFile;
    private Token nextToken;

    public CMinusScanner (BufferedReader file) {
        inFile = file;

        nextToken = scanToken();
    }
    
    public Token getNextToken () {
        Token returnToken = nextToken;
        //Scan the next token if the current is not EOF
        if (nextToken.getType() != Token.TokenType.EOF)
            nextToken = scanToken();
        return returnToken;
    }
    public Token viewNextToken () {
        return nextToken;
    }
    
    public Token scanToken(){
        State current = State.Start;
        StringBuilder sb = new StringBuilder(10);
        while(true){
            try{
                inFile.mark(1);
                int i = inFile.read();
                if(i==-1) return new Token(TokenType.EOF);
                char nextChar = (char) i;
                switch(current){
                    case Start:
                        if(('A'<=nextChar && nextChar<='Z') || ('a'<=nextChar&&nextChar<='z')){
                            sb.append(nextChar);
                            current = State.Letters;
                        }else if('0'<=nextChar && nextChar<='9'){
                            sb.append(nextChar);
                            current = State.Numbers;
                        }else switch(nextChar){
                            case '/':
                                current = State.Div;
                                break;
                            case '=':
                                current = State.Assign;
                                break;
                            case '>':
                                current = State.Greater;
                                break;
                            case '<':
                                current = State.Less;
                                break;
                            case '!':
                                current = State.Not;
                                break;
                            case '+': return new Token(TokenType.ADD);
                            case '-': return new Token(TokenType.SUB);
                            case '*': return new Token(TokenType.MULT);
                            case ';': return new Token(TokenType.SEMICOLON);
                            case ',': return new Token(TokenType.COMMA);
                            case '(': return new Token(TokenType.BEGPAR);
                            case ')': return new Token(TokenType.ENDPAR);
                            case '[': return new Token(TokenType.BEGSBRA);
                            case ']': return new Token(TokenType.ENDSBRA);
                            case '{': return new Token(TokenType.BEGBRA);
                            case '}': return new Token(TokenType.ENDBRA);
                            case ' ':
                            case '\t':
                            case '\n':
                            case '\r': break;
                            default:
                                return new Token(TokenType.ERROR, "Unknown token: \""+nextChar+'"');
                                //throw new CMinusScannerException("Unknown character in Start: "+nextChar);
                        }
                        break;
                    case Letters:
                        if(('A'<=nextChar && nextChar<='Z') || ('a'<=nextChar && nextChar<='z'))
                            sb.append(nextChar);
                        else if('0'<=nextChar && nextChar<='9')
                            return new Token(TokenType.ERROR, "Letters cannot be immediately followed by a number.");
                        else{
                            inFile.reset();
                            String retStr = sb.toString();
                            if(retStr.equals("while")) return new Token(TokenType.WHILE);
                            if(retStr.equals("if")) return new Token(TokenType.IF);
                            if(retStr.equals("int")) return new Token(TokenType.INT);
                            if(retStr.equals("return")) return new Token(TokenType.RETURN);
                            if(retStr.equals("void")) return new Token(TokenType.VOID);
                            if(retStr.equals("else")) return new Token(TokenType.ELSE);
                            return new Token(TokenType.ID,retStr);
                        }
                        break;
                    case Numbers:
                        if('0'<=nextChar && nextChar<='9')
                            sb.append(nextChar);
                        else if(('A'<=nextChar && nextChar<='Z') || ('a'<=nextChar && nextChar<='z'))
                            return new Token(TokenType.ERROR, "Numbers cannot be immediately followed by a letter.");
                        else{
                            inFile.reset();
                            Integer retInt = new Integer(Integer.parseInt(sb.toString()));
                            return new Token(TokenType.NUM, retInt);
                        }
                        break;
                    case Div:
                        if(nextChar=='*') current = State.Comment;
                        else{
                            inFile.reset();
                            return new Token(TokenType.DIV);
                        }
                        break;
                    case Comment:
                        if(nextChar=='*') current = State.CommentStar;
                        break;
                    case CommentStar:
                        if(nextChar=='/') current = State.Start;
                        else current = State.Comment;
                        break;
                    case Assign:
                        if(nextChar=='=') return new Token(TokenType.EQUAL);
                        inFile.reset();
                        return new Token(TokenType.ASSIGN);
                    case Greater:
                        if(nextChar=='=') return new Token(TokenType.GREEQU);
                        inFile.reset();
                        return new Token(TokenType.GRE);
                    case Less:
                        if(nextChar=='=') return new Token(TokenType.LESSEQU);
                        inFile.reset();
                        return new Token(TokenType.LESS);
                    case Not:
                        if(nextChar=='=') return new Token(TokenType.NOTEQUAL);
                        inFile.reset();
                        return new Token(TokenType.ERROR,"Unexpected character following !");
                }
            }catch(IOException e){
                throw new CMinusScannerException("Unknown error: "+e);
            }
        }
    }
    public static void main (String[] args) {
        try{
            BufferedReader read = new BufferedReader(new FileReader("input.txt"));
            PrintWriter pw = new PrintWriter("output.txt");
            Scanner cScan = new CMinusScanner(read);
            Token lastToken = cScan.getNextToken();
            TokenType type = lastToken.getType();
            while(type!=Token.TokenType.EOF&&type!=Token.TokenType.ERROR){
                pw.write(type.toString());
                if(type==Token.TokenType.ID){
                    pw.write(" : ");
                    pw.write((String)(lastToken.getData()));
                }else if (type==Token.TokenType.NUM){
                    pw.write(" : ");
                    pw.write(((Integer)(lastToken.getData())).toString());
                }
                pw.write('\n');
                lastToken = cScan.getNextToken();
                type = lastToken.getType();
            }
            read.close();
            pw.write(type.toString());
            if(type==TokenType.ERROR){
                pw.write((String)(lastToken.getData()));
                System.out.println((String)(lastToken.getData()));
            }
            pw.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
    private enum State{
        Start,Letters,Numbers,
        Div,Comment,CommentStar,
        Assign,Greater,Less,Not
    }
}