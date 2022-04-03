package compiler.astclasses;

public class AssignExpression implements Expression{
    private Expression leftExp;
    private Expression rightExp;
    public AssignExpression(Expression lExp,Expression rExp){
        leftExp = lExp;
        rightExp = rExp;
    }
    public void print(){
        leftExp.print();
        System.out.print(" = ");
        rightExp.print();
    }
}
