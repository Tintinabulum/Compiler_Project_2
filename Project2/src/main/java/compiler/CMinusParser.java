package compiler;

import java.io.BufferedReader;
import java.util.ArrayList;

import java.io.FileReader;
import java.io.File;
import java.io.IOException;

import compiler.Token.TokenType;
import compiler.astclasses.*;

public class CMinusParser implements Parser{
    //Private variables
    private CMinusScanner scan;
    private Program root;
    private boolean debug;
    //Constructors
    public CMinusParser(BufferedReader r){
        this(new CMinusScanner(r));
    }
    public CMinusParser(BufferedReader r,boolean debugMode){
        this(new CMinusScanner(r),debugMode);
    }
    public CMinusParser(CMinusScanner c){
        this(c, false);
    }
    public CMinusParser(CMinusScanner c,boolean debugMode){
        scan = c;
        root = null;
        debug = debugMode;
    }
    //Print
    public void printTree(){
        if(root==null)
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
        if(debug){
            if(t.getType()==TokenType.ID)
                System.out.println("ID: "+(String)(t.getData()));
            else System.out.println(t.getType());
        }
        return t;
    }
    private Token viewNext(){
        Token t = scan.viewNextToken();
        if(t.getType()==TokenType.ERROR)
            throw new CMinusScannerException("Invalid syntax: "+(String)(t.getData()));
        if(debug)
            System.out.println("\t"+t.getType());
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
                p = new ArrayList<Param>(3);
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
        Object[] a = p.toArray();
        Param[] r = new Param[a.length];
        for(int i=0;i<a.length;i++)
            r[i] = (Param)a[i];
        return new FunDecl(isVoid, id, r, cs);
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
        Token nextToken = nextToken();
        TokenType type = nextToken.getType();
        if(type!=TokenType.BEGBRA)
            throw new CMinusParserException("Invalid semantics in CompoundStatement:\nGot "+type.toString()+" instead of {");
        nextToken = viewNext();
        type = nextToken.getType();
        CompoundStatement ret = new CompoundStatement();
        //ParseLocalDecl
        while(type==TokenType.INT){
            //consume INT
            scan.getNextToken();
            ret.addVarDecl(parseLocalDeclaration());
            nextToken = viewNext();
            type = nextToken.getType();
        }
        //Statement List
        while(type!=TokenType.ENDBRA){
            if(type!=TokenType.BEGBRA && type!=TokenType.IF && type!=TokenType.WHILE &&
                    type!=TokenType.RETURN && type!=TokenType.SEMICOLON && type!=TokenType.ID
                    && type!=TokenType.NUM && type!=TokenType.BEGPAR)
                throw new CMinusParserException("Invalid semantics in StatementList:\nGot "+type.toString()+" instead of {, IF, WHILE, RETURN, ;, ID, NUM, or (");
            ret.addStatement(parseStatement());
            nextToken = viewNext();
            type = nextToken.getType();
        }
        //Consume the }
        scan.getNextToken();
        return ret;
    }
    private VarDecl parseLocalDeclaration (){
        Token nextToken = nextToken();
        TokenType type = nextToken.getType();
            if(type!=TokenType.ID)
                throw new CMinusParserException("Invalid semantics in Local Declaration:\nGot "+type.toString()+" instead of ID");
            String id = (String)(nextToken.getData());
            nextToken = nextToken();
            type = nextToken.getType();
            if(type==TokenType.BEGSBRA){
                nextToken = nextToken();
                type = nextToken.getType();
                if(type!=TokenType.NUM)
                    throw new CMinusParserException("Invalid semantics in Local Declaration:\nGot "+type.toString()+" instead of NUM");
                int num = (Integer)(nextToken.getData());
                nextToken = nextToken();
                type = nextToken.getType();
                if(type!=TokenType.ENDSBRA)
                    throw new CMinusParserException("Invalid semantics in Local Declaration:\nGot "+type.toString()+" instead of ]");
                nextToken = nextToken();
                type = nextToken.getType();
                if(type!=TokenType.SEMICOLON)
                    throw new CMinusParserException("Invalid semantics in Local Declaration:\nGot "+type.toString()+" instead of ;1");
                return new VarDecl(id, num);
            }
            if(type!=TokenType.SEMICOLON)
                throw new CMinusParserException("Invalid semantics in Local Declaration:\nGot "+type.toString()+" instead of ;2");
            return new VarDecl(id);
    }
    private Statement parseStatement(){
        Token nextToken = viewNext();
        TokenType type = nextToken.getType();
        switch(type){
                case BEGBRA: return parseCompoundStatement();
                case IF: return parseSelectionStatement();
                case WHILE: return parseIterationStatement();
                case RETURN: return parseReturnStatement();
                case SEMICOLON:
                case ID:
                case NUM:
                case BEGPAR:
                    return parseExpressionStatement();
                default:
                    throw new CMinusParserException("Invalid semantics in Statement:\nGot "+type.toString()+" instead of {, IF, WHILE, RETURN, ;, ID, NUM, or (");                    
            }
    }
    private SelectionStatement parseSelectionStatement(){
        Token nextToken = nextToken();
        TokenType type = nextToken.getType();
        if(type!=TokenType.IF)
            throw new CMinusParserException("Invalid semantics in SelectionStatement:\nGot "+type.toString()+" instead of IF");
        nextToken = nextToken();
        type = nextToken.getType();
        if(type!=TokenType.BEGPAR)
            throw new CMinusParserException("Invalid semantics in SelectionStatement:\nGot "+type.toString()+" instead of (");
        Expression con = parseExpression();
        nextToken = nextToken();
        type = nextToken.getType();
        if(type!=TokenType.ENDPAR)
            throw new CMinusParserException("Invalid semantics in SelectionStatement:\nGot "+type.toString()+" instead of )");
        Statement ifS = parseStatement();
        nextToken = viewNext();
        type = nextToken.getType();
        if(type==TokenType.ELSE){
            scan.getNextToken();
            return new SelectionStatement(con, ifS, parseStatement());
        }
        return new SelectionStatement(con, ifS);
    }
    private IterationStatement parseIterationStatement(){
        Token nextToken = nextToken();
        TokenType type = nextToken.getType();
        if(type!=TokenType.WHILE)
            throw new CMinusParserException("Invalid semantics in IterationStatement:\nGot "+type.toString()+" instead of WHILE");
        nextToken = nextToken();
        type = nextToken.getType();
        if(type!=TokenType.BEGPAR)
            throw new CMinusParserException("Invalid semantics in IterationStatement:\nGot "+type.toString()+" instead of (");
        Expression con = parseExpression();
        nextToken = nextToken();
        type = nextToken.getType();
        if(type!=TokenType.ENDPAR)
            throw new CMinusParserException("Invalid semantics in IterationStatement:\nGot "+type.toString()+" instead of )");
        Statement whileS = parseStatement();
        return new IterationStatement(con ,whileS);
    }
    private ReturnStatement parseReturnStatement(){
        Token nextToken = nextToken();
        TokenType type = nextToken.getType();
        if(type!=TokenType.RETURN)
            throw new CMinusParserException("Invalid semantics in ReturnStatement:\nGot "+type.toString()+" instead of RETURN");
        nextToken = viewNext();
        type = nextToken.getType();
        if (type!=TokenType.SEMICOLON){
            Expression retVal = parseExpression();
            return new ReturnStatement(retVal);
        }
        return new ReturnStatement();
    }
    private ExpressionStatement parseExpressionStatement(){
        Token nextToken = viewNext();
        TokenType type = nextToken.getType();
        if (type!=TokenType.SEMICOLON)
            return new ExpressionStatement(parseExpression());
        //consume ;
        scan.getNextToken();
        return new ExpressionStatement();
    }
    private Expression parseExpression(){
        Expression exp;
        Token nextToken = nextToken();
        TokenType type = nextToken.getType();
        switch(type){
            case ID:
                return parseExpressionPrime((String)(nextToken.getData()));
            case NUM:
                Integer v = (Integer)nextToken.getData();
                exp = new NumExpression(v);
                return parseSimpleExpressionPrime(exp);
            case BEGPAR:
                exp = parseExpression();
                nextToken = nextToken();
                type = nextToken.getType();
                if(type!=TokenType.ENDPAR)
                    throw new CMinusParserException("Invalid semantics in parseExpression:\nGot "+type.toString()+" instead of )");
                return parseSimpleExpressionPrime(exp);
            default:
                throw new CMinusParserException("Invalid semantics in parseExpression:\nGot "+type.toString()+" instead of ID, NUM, or (");
        }
    }
    private Expression parseExpressionPrime(String id){
        Token nextToken = viewNext();
        TokenType type = nextToken.getType();
        switch(type){
            case ASSIGN:
                //Consume =
                scan.getNextToken();
                return new AssignExpression(new VarExpression(id), parseExpression());
            case BEGSBRA:
                //Consume [
                scan.getNextToken();
                Expression exp = parseExpression();
                nextToken = nextToken();
                type = nextToken.getType();
                if (type!=TokenType.ENDSBRA)
                    throw new CMinusParserException("Invalid semantics in parseExpressionPrime:\nGot "+type.toString()+" instead of ]");
                nextToken = viewNext();
                type = nextToken.getType();
                //parseExpressionPrimePrime
                switch(type){
                    case ASSIGN:
                        //consume =
                        scan.getNextToken();
                        return new AssignExpression(new VarExpression(id,exp), parseExpression());
                    case ADD:
                    case SUB:
                    case MULT:
                    case DIV:
                    case GRE:
                    case GREEQU:
                    case EQUAL:
                    case LESS:
                    case LESSEQU:
                    case NOTEQUAL:
                    case SEMICOLON:
                    case ENDPAR:
                    case ENDSBRA:
                    case COMMA:
                        return parseSimpleExpressionPrime(new VarExpression(id,exp));
                    default:
                        throw new CMinusParserException("Invalid semantics in parseExpressionPrime:\nGot "+type.toString()+" instead of +, -, *, /, >, >=, ==, <, <=, !=, ), ], ;, or ,");
                }
            case BEGPAR:
                exp = parseCall(new VarExpression(id));
                return parseSimpleExpressionPrime(exp);
            case ADD:
            case SUB:
            case MULT:
            case DIV:
            case GRE:
            case GREEQU:
            case LESS:
            case LESSEQU:
            case EQUAL:
            case NOTEQUAL:
            case SEMICOLON:
            case ENDPAR:
            case ENDSBRA:
            case COMMA:
                return parseSimpleExpressionPrime(new VarExpression(id));
            default:
                throw new CMinusParserException("Invalid semantics in parseExpressionPrime:\nGot "+type.toString()+
                    " instead of =, [, (, +, -, *, /, >, >=, ==, <, <=, !=, ), ], ;, or ,"); 
        }
    }
    private Expression parseSimpleExpressionPrime(Expression lhs){
        Token nextToken = viewNext();
        TokenType type = nextToken.getType();
        switch(type){
            case ADD:
            case SUB:
            case MULT:
            case DIV:
                return parseAdditiveExpression(lhs);
            case GRE:
            case GREEQU:
            case LESS:
            case LESSEQU:
            case EQUAL:
            case NOTEQUAL:
                //Consume the relop
                Token t = scan.getNextToken();
                return new BinaryExpression(lhs,t,parseAdditiveExpression(null));
            case SEMICOLON:
            case ENDPAR:
            case ENDSBRA:
            case COMMA:
                return lhs;
            default:
                throw new CMinusParserException("Invalid semantics in parseExpressionPrime:\nGot "+type.toString()+" instead of +, -, *, /, >, >=, ==, <, <=, !=, ), ], ;, or ,"); 
        }
    }
    private CallExpression parseCall(VarExpression vd){
        Token nextToken = nextToken();
        TokenType type = nextToken.getType();
        if(type!=TokenType.BEGPAR)
            throw new CMinusParserException("Invalid semantics in parseExpressionPrime:\nGot "+type.toString()+" instead of ");
        CallExpression ce = new CallExpression(vd);
        nextToken = viewNext();
        type = nextToken.getType();
        while (type!=TokenType.ENDPAR){
            ce.addArgs(parseExpression());
            nextToken = viewNext();
            type = nextToken.getType();

            if (type != TokenType.COMMA && type != TokenType.ENDPAR)
                 throw new CMinusParserException("Invalid semantics in parseCall:\nGot "+type.toString()+" instead of , or )");
            
            //Consume the ,
            scan.getNextToken();
        }
        return ce;
    }
    private BinaryExpression parseAdditiveExpression(Expression lhs){
        lhs = parseTerm(lhs);
        Token nextToken = viewNext();
        TokenType type = nextToken.getType();
        while(type==TokenType.ADD || type==TokenType.SUB){
            //Consume the + or -
            scan.getNextToken();
            lhs = new BinaryExpression(lhs, nextToken, parseTerm(null));
            nextToken = viewNext();
            type = nextToken.getType();
        }
        switch(type){
            case SEMICOLON:
            case ENDPAR:
            case ENDSBRA:
                break;
            default:
                throw new CMinusParserException("Invalid semantics in parseAdditiveExpression:\nGot "+type.toString()+" instead of ;, ), or ]");         
        }
        return new BinaryExpression(lhs);
    }
    private BinaryExpression parseTerm(Expression lhs){
        if(lhs==null)
            lhs = parseFactor();
        Token nextToken = viewNext();
        TokenType type = nextToken.getType();
        while(type==TokenType.MULT || type==TokenType.DIV){
            //Consume the * or /
            scan.getNextToken();
            lhs = new BinaryExpression(lhs,nextToken,parseFactor());
            nextToken = viewNext();
            type = nextToken.getType();
        }
        return new BinaryExpression(lhs);
    }

    private Expression parseFactor()
    {
        Token nextToken = nextToken();

        switch(nextToken.getType())
        {
        case BEGPAR:
            {
                Expression returnExp = parseExpression();
                nextToken(); //consume ')'
                return returnExp;
            }
        case NUM:
            return new NumExpression((int)nextToken.getData());

        case ID: //Handle factor'
            {
                Token id = nextToken;
                nextToken = viewNext();
                TokenType type = nextToken.getType();
                
                if(type == TokenType.BEGSBRA)
                {
                    nextToken = nextToken();
                    Expression expr = parseExpression();
                    if(nextToken.getType() != TokenType.ENDSBRA)
                        throw new CMinusParserException("Invalid semantics in factor. Got " + nextToken.getType() + " instead of ']'");
                    
                    return new VarExpression((String)id.getData(), expr);
                }
                else if(type == TokenType.BEGPAR)
                    return parseCall(new VarExpression((String)id.getData()));

                return new VarExpression((String)id.getData());
            }
        default:
            throw new CMinusParserException("Invalid semantics in factor. Got " + nextToken.getType());

        }
    }
    
    public static void main(String[] args) {
        try{
            BufferedReader r = new BufferedReader(new FileReader(new File("input.txt")));
            CMinusParser c = new CMinusParser(r, true);
            c.parse();
            c.printTree();
        }catch(CMinusScannerException e){
            System.out.println("Scanner Error: ");
            System.out.println(e);
        }catch(CMinusParserException e){
            System.out.println("Parser Error: ");
            System.out.println(e);
        }catch(IOException e){
            System.out.println("IO Error: ");
            System.out.println(e);
        }
    }
    
}