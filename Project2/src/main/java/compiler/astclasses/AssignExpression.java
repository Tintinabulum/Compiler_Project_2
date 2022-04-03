package compiler.astclasses;

public class AssignExpression implements Expression{
    private VarExpression var;
    private Expression exp;
    public AssignExpression(VarExpression lExp,Expression rExp){
        var = lExp;
        exp = rExp;
    }
    public void print(){
        var.print();
        System.out.print(" = ");
        exp.print();
    }
}
