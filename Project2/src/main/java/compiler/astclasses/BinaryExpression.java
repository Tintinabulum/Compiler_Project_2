package compiler.astclasses;

import compiler.Token;

public class BinaryExpression implements Expression
{

    private Expression leftExp;
    private Token op;
    private Expression rightExp;

    public BinaryExpression(Expression ls, Token operation, Expression rs)
    {
        leftExp = ls;
        op = operation;
        rightExp = rs;
    }
    public BinaryExpression(Expression ls)
    {
        leftExp = ls;
        op = null;
        rightExp = null;
    }

    @Override
    public void print()
    {
        leftExp.print();
        if(op != null && rightExp != null)
        {
            System.out.print(" " + op.getType() + " ");
            rightExp.print();
        }
    }   
}
