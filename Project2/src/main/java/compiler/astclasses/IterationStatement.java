package compiler.astclasses;

public class IterationStatement implements Statement{
    private Expression condition;
    private Statement body;
    public IterationStatement(Expression e,Statement s){
        condition = e;
        body = s;
    }
    public void print(int tabs){
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println("while");
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println('(');
        condition.print(tabs+1);
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.print(')');
        body.print(tabs+1);
    }
}