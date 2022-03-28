package compiler.astclasses;

import java.util.ArrayList;

public class CompoundStatement implements Statement{
    //Private variables
    private ArrayList<VarDecl> localDecls;
    private ArrayList<Statement> stateList;
    //Constructor
    public CompoundStatement(){
        this(new ArrayList(1),new ArrayList(1));
    }
    public CompoundStatement(ArrayList<VarDecl> decls, ArrayList<Statement> statements){
        if(decls==null) decls = new ArrayList(1);
        if(statements==null) statements = new ArrayList(1);
        localDecls = decls;
        stateList = statements;
    }
    //Add methods
    public void addVarDecl(VarDecl vd){
        localDecls.add(vd);
    }
    public void addStatement(Statement s){
        stateList.add(s);
    }
    //Print
    public void print(){
        System.out.println("{");
        for(VarDecl vd : localDecls){
            vd.print();
            System.out.println();
        }
        for(Statement s : stateList){
            s.print();
            System.out.println();
        }
        System.out.println("}");
    }
}