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
    private Token nextToken(){
        Token t = scan.getNextToken();
        if(t.getType()==TokenType.ERROR)
            throw new CMinusScannerException("Invalid syntax: "+(String)(t.getData()));
        return t;
    }
    private Token viewNext(){
        Token t = scan.viewNextToken();
        if(t.getType()==TokenType.ERROR)
            throw new CMinusScannerException("Invalid syntax: "+(String)(t.getData()));
        return t;
    }
    private Program parseProgram(){
        Declaration d = parseDeclaration();
        Program p = new Program(d);
        Token nextToken = viewNext();
        TokenType type = nextToken.getType();
        while(type!=TokenType.EOF){
            //Look ahead check here
            d = parseDeclaration();
            p.addDeclaration(d);
            nextToken = viewNext();
            type = nextToken.getType();
        }
        return p;
    }
    private Declaration parseDeclaration(){
        Token nextToken = nextToken();
        TokenType type = nextToken.getType();
        boolean isVoid = false;
        switch(type){
            case VOID:
                isVoid = true;
            case INT:
                nextToken = nextToken();
                type = nextToken.getType();
                switch(type){
                    case ID:
                        String data = (String)(nextToken.getData());
                        if(isVoid)
                            return parseFunDecl(true, data);
                        return parseDeclarationPrime(data);
                    default:
                        throw new CMinusParserException("Invalid semantics in Declaration:\nGot "+type.toString()+" instead of ID");
                }
            default:
                throw new CMinusParserException("Invalid semantics in Declaration:\nGot "+type.toString()+" instead of VOID or INT");
        }
    }
    private Declaration parseDeclarationPrime(String id){
        Token nextToken = viewNext();
        TokenType type = nextToken.getType();
        switch(type){
            case BEGSBRA:
                //Consume the [
                scan.getNextToken();
                nextToken = nextToken();
                type = nextToken.getType();
                int val;
                switch(type){
                    case NUM:
                        val = (Integer)(nextToken.getData());
                        break;
                    default:
                        throw new CMinusParserException("Invalid semantics in Variable Declaration:\nGot "+type.toString()+" instead of NUM");
                }
                nextToken = nextToken();
                type = nextToken.getType();
                if(type!=TokenType.ENDSBRA)
                   throw new CMinusParserException("Invalid semantics in Variable Declaration:\nGot "+type.toString()+" instead of ]");
                nextToken = nextToken();
                type = nextToken.getType();
                if(type==TokenType.SEMICOLON)
                    return new VarDecl(id,val);
                throw new CMinusParserException("Invalid semantics in Variable Declaration:\nGot "+type.toString()+" instead of ;");
            case SEMICOLON:
                //Consume the ;
                scan.getNextToken();
                return new VarDecl(id);
            default:
                return parseFunDecl(false, id);
        }
    }
    private FunDecl parseFunDecl(boolean isVoid, String id){
        Token nextToken = nextToken();
        TokenType type = nextToken.getType();
        if(type!=TokenType.BEGPAR)
            throw new CMinusParserException("Invalid semantics in Function Declaration:\nGot "+type.toString()+" instead of (");
        ArrayList<Param> p = null;
        nextToken = viewNext();
        type = nextToken.getType();
        switch(type){
            case INT:
                p = new ArrayList(3);
                while(type!=TokenType.ENDPAR){
                    p.add(parseParam());
                    nextToken = nextToken();
                    type = nextToken.getType();
                    if(type!=TokenType.COMMA && type!=TokenType.ENDPAR)
                        throw new CMinusParserException("Invalid semantics in Param-list:\nGot "+type.toString()+" instead of , or )");
                }
                break;
            case VOID:
                //Consume VOID
                scan.getNextToken();
                nextToken = nextToken();
                type = nextToken.getType();
                if(type!=TokenType.ENDPAR)
                    throw new CMinusParserException("Invalid semantics in Param-list:\nGot "+type.toString()+" instead of )");
                break;
            default:
                throw new CMinusParserException("Invalid semantics in Param-list:\nGot "+type.toString()+" instead of INT or VOID");
        }
        CompoundStatement cs = parseCompoundStatement();
        if(p==null)
            return new FunDecl(isVoid, id, cs);
        return new FunDecl(isVoid, id, (Param[])(p.toArray()), cs);
    }
    private Param parseParam(){
           Token nextToken = nextToken();
           TokenType type = nextToken.getType();
           if(type!=TokenType.INT)
               throw new CMinusParserException("Invalid semantics in Param:\nGot "+type.toString()+" instead of INT");
           nextToken = nextToken();
           type = nextToken.getType();
           if(type!=TokenType.ID)
               throw new CMinusParserException("Invalid semantics in Param:\nGot "+type.toString()+" instead of ID");
           String id = (String)(nextToken.getData());
           nextToken = viewNext();
           type = nextToken.getType();
           if(type==TokenType.BEGSBRA){
               //Consume the [
               scan.getNextToken();
               nextToken = nextToken();
               type = nextToken.getType();
               if(type!=TokenType.ENDSBRA)
                   throw new CMinusParserException("Invalid semantics in Param:\nGot "+type.toString()+" instead of ]");
               return new Param(id, true);
           }
           return new Param(id);
    }
    private CompoundStatement parseCompoundStatement(){
        return null;
    }
}