package compiler;

import java.io.BufferedReader;
import java.util.ArrayList;
import compiler.Token.TokenType;
import compiler.astclasses.*;

public class CMinusParser implements Parser{
    //Private variables
    private CMinusScanner scan;
    private Program root;
    //Constructors
    public CMinusParser(BufferedReader r){
        this(new CMinusScanner(r));
    }
    public CMinusParser(CMinusScanner c){
        scan = c;
        root = null;
    }
    //Print
    public void printTree(){
        if(root!=null)
            parse();
        root.print();
    }
    //Parses
    public void parse(){
        root = parseProgram();
    }
    private Program parseProgram(){
        Declaration d = parseDeclaration();
        Program p = new Program(d);
        Token nextToken = scan.viewNextToken();
        TokenType type = nextToken.getType();
        while(type!=TokenType.EOF){
            d = parseDeclaration();
            p.addDeclaration(d);
            nextToken = scan.viewNextToken();
            type = nextToken.getType();
        }
        if(type==TokenType.ERROR)
            throw new CMinusScannerException("Invalid syntax: "+(String)(nextToken.getData()));
        return p;
    }
    private Declaration parseDeclaration(){
        Token nextToken = scan.getNextToken();
        TokenType type = nextToken.getType();
        boolean isVoid = false;
        switch(type){
            case VOID:
                isVoid = true;
            case INT:
                nextToken = scan.getNextToken();
                type = nextToken.getType();
                switch(type){
                    case ID:
                        String data = (String)(nextToken.getData());
                        if(isVoid)
                            return parseFunDecl(true, data);
                        return parseDeclarationPrime(data);
                    case ERROR:
                        throw new CMinusScannerException("Invalid syntax: "+(String)(nextToken.getData()));
                    default:
                        throw new CMinusParserException("Invalid semantics in Declaration:\nGot "+type.toString()+" instead of ID");
                }
            case ERROR:
                throw new CMinusScannerException("Invalid syntax: "+(String)(nextToken.getData()));
            default:
                throw new CMinusParserException("Invalid semantics in Declaration:\nGot "+type.toString()+" instead of VOID or INT");
        }
    }
    private Declaration parseDeclarationPrime(String id){
        Token nextToken = scan.viewNextToken();
        TokenType type = nextToken.getType();
        switch(type){
            case BEGSBRA:
                //Consume the [
                scan.getNextToken();
                nextToken = scan.getNextToken();
                type = nextToken.getType();
                int val;
                switch(type){
                    case NUM:
                        val = (Integer)(nextToken.getData());
                        break;
                    case ERROR:
                        throw new CMinusScannerException("Invalid syntax: "+(String)(nextToken.getData()));
                    default:
                        throw new CMinusParserException("Invalid semantics in Variable Declaration:\nGot "+type.toString()+" instead of NUM");
                }
                nextToken = scan.getNextToken();
                type = nextToken.getType();
                switch(type){
                    case ENDSBRA:
                        break;
                    case ERROR:
                        throw new CMinusScannerException("Invalid syntax: "+(String)(nextToken.getData()));
                    default:
                        throw new CMinusParserException("Invalid semantics in Variable Declaration:\nGot "+type.toString()+" instead of ]");
                }
                nextToken = scan.getNextToken();
                type = nextToken.getType();
                switch(type){
                    case SEMICOLON:
                        return new VarDecl(id,val);
                    case ERROR:
                        throw new CMinusScannerException("Invalid syntax: "+(String)(nextToken.getData()));
                    default:
                        throw new CMinusParserException("Invalid semantics in Variable Declaration:\nGot "+type.toString()+" instead of ;");
                }
            case SEMICOLON:
                //Consume the ;
                scan.getNextToken();
                return new VarDecl(id);
            default:
                return parseFunDecl(false, id);
        }
    }
    private FunDecl parseFunDecl(boolean isVoid, String id){
        Token nextToken = scan.getNextToken();
        TokenType type = nextToken.getType();
        switch(type){
            case BEGPAR:
                break;
            case ERROR:
                throw new CMinusScannerException("Invalid syntax: "+(String)(nextToken.getData()));
            default:
                throw new CMinusParserException("Invalid semantics in Function Declaration:\nGot "+type.toString()+" instead of (");
        }
        ArrayList<Param> p = null;
        nextToken = scan.viewNextToken();
        type = nextToken.getType();
        switch(type){
            case INT:
                p = new ArrayList(3);
                while(type!=TokenType.ENDPAR){
                    p.add(parseParam());
                    nextToken = scan.getNextToken();
                    type = nextToken.getType();
                    if(type==TokenType.ERROR)
                        throw new CMinusScannerException("Invalid syntax: "+(String)(nextToken.getData()));
                    if(type!=TokenType.COMMA && type!=TokenType.ENDPAR)
                        throw new CMinusParserException("Invalid semantics in Param-list:\nGot "+type.toString()+" instead of , or )");
                }
                break;
            case VOID:
                //Consume VOID
                scan.getNextToken();
                nextToken = scan.getNextToken();
                type = nextToken.getType();
                if(type==TokenType.ERROR)
                    throw new CMinusScannerException("Invalid syntax: "+(String)(nextToken.getData()));
                if(type!=TokenType.ENDPAR)
                    throw new CMinusParserException("Invalid semantics in Param-list:\nGot "+type.toString()+" instead of )");
                break;
            case ERROR:
                throw new CMinusScannerException("Invalid syntax: "+(String)(nextToken.getData()));
            default:
                throw new CMinusParserException("Invalid semantics in Param-list:\nGot "+type.toString()+" instead of INT or VOID");
        }
        CompoundStatement cs = parseCompoundStatement();
        return new FunDecl(isVoid, id, p, cs);
    }
    private Param parseParam(){
           return null; 
    }
    private CompoundStatement parseCompoundStatement(){
        return null;
    }
}
