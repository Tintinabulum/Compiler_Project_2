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
                    throw new CMinusParserException("Invalid semantics in Local Declaration:\nGot "+type.toString()+" instead of ;");
                return new VarDecl(id, num);
            }
            if(type!=TokenType.SEMICOLON)
                throw new CMinusParserException("Invalid semantics in Local Declaration:\nGot "+type.toString()+" instead of ;");
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
                return parseExpressionPrime();
            case NUM:
                exp = new NumExpression((int)nextToken.getData());
                nextToken = viewNext();
                type = nextToken.getType();
                switch(type){
                case SEMICOLON:
                    return exp;
                case ADD:
                case SUB:
                case MULT:
                case DIV:
                    nextToken = nextToken();
                    return new BinaryExpression(exp, nextToken, parseFactor());
                default:
                    return new BinaryExpression(exp, parseRelop(), parseAdditiveExpression());
                }
            case BEGPAR:
                exp = parseExpression();
                nextToken = nextToken();
                type = nextToken.getType();
                if(type!=TokenType.ENDPAR)
                    throw new CMinusParserException("Invalid semantics in parseExpression:\nGot "+type.toString()+" instead of )");
                nextToken = viewNext();
                type = nextToken.getType();
                switch(type){
                    case SEMICOLON:
                        return exp;
                    case ADD:
                    case SUB:
                    case MULT:
                    case DIV:
                        nextToken = nextToken();
                        return new BinaryExpression(exp, nextToken, parseFactor());
                    default:
                        return new BinaryExpression(exp, parseRelop(), parseAdditiveExpression()); 
                }
            default:
                throw new CMinusParserException("Invalid semantics in parseExpression:\nGot "+type.toString()+" instead of ID, NUM, or (");
        }
    }
    private Expression parseExpressionPrime(){
        Token nextToken = nextToken();
        TokenType type = nextToken.getType();
        Expression exp = new VarExpression();
        switch(type){
            case EQUAL:
                exp = new VarExpression();
                return (new AssignExpression(exp, parseExpression()));
            case BEGSBRA:
                Expression exp2 = parseExpression();
                exp = new VarExpression(exp2);
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
                        return new AssignExpression(exp, parseExpression());
                    case SEMICOLON:
                        return exp;
                    case ADD:
                    case SUB:
                    case MULT:
                    case DIV:
                        nextToken = nextToken();
                        return new BinaryExpression(exp, nextToken, parseFactor());
                    default:
                        return new BinaryExpression(exp, parseRelop(), parseAdditiveExpression()); 
                }
            case BEGPAR:
                exp = new CallExpression(new NumExpression());
                exp = new CallExpression(new VarExpression());
                nextToken = nextToken();
                type = nextToken.getType();
                while (type!=TokenType.ENDPAR){
                    ((CallExpression) exp).addArgs(parseExpression());
                   nextToken = nextToken();
                   type = nextToken.getType();
                   if (type!=TokenType.COMMA && type!=TokenType.ENDPAR)
                       throw new CMinusParserException("Invalid semantics in parseExpressionPrime:\nGot "+type.toString()+" instead of , or ]");
                }
                nextToken = viewNext();
                type = nextToken.getType();
                switch(type){
                    case SEMICOLON:
                        return exp;
                    case ADD:
                    case SUB:
                    case MULT:
                    case DIV:
                        nextToken = nextToken();
                        return new BinaryExpression(exp, nextToken, parseFactor());
                    default:
                        return new BinaryExpression(exp, parseRelop(), parseAdditiveExpression()); 
                }
            case SEMICOLON:
                return exp;
            case ADD:
            case SUB:
            case MULT:
            case DIV:
                nextToken = nextToken();
                return new BinaryExpression(exp, nextToken, parseFactor());
            default:
                return new BinaryExpression(exp, parseRelop(), parseAdditiveExpression()); 
        }
    }
    private Token parseRelop(){
        Token nextToken = nextToken();
        TokenType type = nextToken.getType();
        switch(type){
            case LESSEQU:
            case LESS:
            case GRE:
            case GREEQU:
            case EQUAL:
            case NOTEQUAL:
                return nextToken;
            default :
                throw new CMinusParserException("Invalid semantics in parseExpressionPrime:\nGot "+type.toString()+" instead of <=, <, >, >=, ==, or !=");
        }
    }

    private BinaryExpression parseAdditiveExpression()
    {
        BinaryExpression lhs = parseTerm();
        Token nextToken = viewNext();

        if(nextToken.getType() == TokenType.ADD || nextToken.getType() == TokenType.SUB)
        {
            Token op = nextToken();
            nextToken = viewNext();
            if(nextToken.getType() != TokenType.BEGPAR && nextToken.getType() != TokenType.NUM && nextToken.getType() != TokenType.ID)
                throw new CMinusParserException("Invalid semantics in BinaryExpression (add)\n. Got " + nextToken.getType());
            
            BinaryExpression rhs = parseTerm();

            return new BinaryExpression(lhs, op, rhs);
        }

        return new BinaryExpression(lhs);
    
    }

    private BinaryExpression parseAdditiveExpressionPrime(Expression lhs)
    {
        Token nextToken = viewNext();
        TokenType type = nextToken.getType();
        if(type == TokenType.MULT || type == TokenType.DIV)
            lhs = ParseTermPrime(lhs);
        
        nextToken = nextToken();
        type = nextToken.getType();
        if(type != TokenType.ADD || type != TokenType.SUB)
            throw new CMinusParserException("Invalid semantics in BinaryExpression (add)\n Got " + type);
        
        BinaryExpression rhs = parseTerm();

        return new BinaryExpression(lhs, nextToken, rhs);
    }

    private BinaryExpression ParseTermPrime(Expression lhs)
    {
        Token op = nextToken();
        Expression rhs = parseFactor();
        return new BinaryExpression(lhs, op, rhs);
    }

    private BinaryExpression parseTerm()
    {
        Expression lhs = parseFactor();
        Token nextToken = viewNext();

        if(nextToken.getType() == TokenType.MULT || nextToken.getType() == TokenType.DIV)
        {
            Token op = nextToken();
            nextToken = viewNext();
            if(nextToken.getType() != TokenType.BEGPAR && nextToken.getType() != TokenType.NUM && nextToken.getType() != TokenType.ID)
                throw new CMinusParserException("Invalid semantics in BinaryExpression (mult)\n. Got " + nextToken.getType());
            
            Expression rhs = parseFactor();

            return new BinaryExpression(lhs, op, rhs);
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
                nextToken = nextToken();
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
                {
                    CallExpression returnExp = new CallExpression(new VarExpression((String)id.getData()));
                    nextToken = viewNext();

                    if(nextToken.getType() != TokenType.ENDPAR)
                    {
                        do
                        {
                            returnExp.addArgs(parseExpression());
                            nextToken = nextToken();
                        } while(nextToken.getType() == TokenType.COMMA);
                    }

                    if(nextToken.getType() != TokenType.ENDPAR)
                        throw new CMinusParserException("Invalid semantics in factor. Got " + nextToken.getType() + " instead of ')'");

                    return returnExp;
                }

                return new VarExpression((String)id.getData());
            }
        default:
            throw new CMinusParserException("Invalid semantics in factor. Got " + nextToken.getType());

        }
    }
    
}