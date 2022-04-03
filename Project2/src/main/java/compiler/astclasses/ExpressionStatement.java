package compiler.astclasses;

public class ExpressionStatement implements Statement{
    private Expression exp;
    public ExpressionStatement(){
        this(null);
    }
    public ExpressionStatement(Expression e){
        exp = e;
    }
    public void print(int tabs){
        if(exp!=null)
            exp.print();
        System.out.println(';');
    }
}