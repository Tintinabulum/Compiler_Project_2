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
    public void print(){
        System.out.print("if(");
        ifExpression.print();
        System.out.print(") ");
        ifStatement.print();
        System.out.println();
        if(elseStatement!=null){
            System.out.print("else ");
            elseStatement.print();
            System.out.println();
        }
    }
}