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
            exp.print(tabs+1);
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println(';');
    }
}