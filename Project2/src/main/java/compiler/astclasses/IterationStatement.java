package compiler.astclasses;

public class IterationStatement implements Statement{
    private Expression condition;
    private Statement body;
    public IterationStatement(Expression e,Statement s){
        condition = e;
        body = s;
    }
    public void print(int tabs){
        System.out.print("while(");
        condition.print();
        System.out.print(") ");
        body.print(tabs+1);
    }
}