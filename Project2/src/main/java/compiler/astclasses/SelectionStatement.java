package compiler.astclasses;

public class SelectionStatement implements Statement{
    //Private variables
    private Expression ifExpression;
    private Statement ifStatement;
    private Statement elseStatement;
    //Constructors
    public SelectionStatement(){
        this(null,null,null);
    }
    public SelectionStatement(Expression iE){
        this(iE,null,null);
    }
    public SelectionStatement(Expression iE,Statement iS){
        this(iE,iS,null);
    }
    public SelectionStatement(Expression iE,Statement iS,Statement eS){
        ifExpression = iE;
        ifStatement = iS;
        elseStatement = eS;
    }
    //Setters
    public void setExpression(Expression e){ifExpression = e;}
    public void setIf(Statement s){ifStatement = s;}
    public void setElse(Statement s){elseStatement = s;}
    //Print
    public void print(int tabs){
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println("if");
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println('(');
        ifExpression.print(tabs+1);
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println(')');
        ifStatement.print(tabs+1);
        if(elseStatement!=null){
            for(int i=0;i<tabs;i++) System.out.print('\t');
            System.out.println("else");
            elseStatement.print(tabs+1);
        }
    }
}