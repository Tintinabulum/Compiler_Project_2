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
    }/*
    public BinaryExpression(Expression ls)
    {
        leftExp = ls;
        op = null;
        rightExp = null;
    }*/

    @Override
    public void print(int tabs)
    {
        leftExp.print(tabs+1);
        //if(op != null && rightExp != null)
       // {
            for(int i=0;i<tabs;i++) System.out.print('\t');
            System.out.println(op.getType());
            rightExp.print(tabs+1);
        //}
    }   
}
