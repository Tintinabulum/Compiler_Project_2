package compiler.astclasses;

public class AssignExpression implements Expression{
    private VarExpression var;
    private Expression exp;
    public AssignExpression(VarExpression lExp,Expression rExp){
        var = lExp;
        exp = rExp;
    }
    public void print(int tabs){
        var.print(tabs+1);
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println(" = ");
        exp.print(tabs+1);
    }
}
