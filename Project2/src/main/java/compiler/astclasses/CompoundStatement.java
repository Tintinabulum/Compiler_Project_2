package compiler.astclasses;

import java.util.ArrayList;

public class CompoundStatement implements Statement{
    //Private variables
    private ArrayList<VarDecl> localDecls;
    private ArrayList<Statement> stateList;
    //Constructor
    public CompoundStatement(){
        this(new ArrayList(5),new ArrayList(20));
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
    public void print(int tabs){
        System.out.println('{');
        for(VarDecl vd : localDecls){
            vd.print(tabs+1);
            System.out.println();
        }
        for(Statement s : stateList){
            for(int i=0;i<tabs;i++) System.out.print('\t');
            s.print(tabs+1);
        }
        for(int i=1;i<tabs;i++) System.out.print('\t');
        System.out.println('}');
    }
}