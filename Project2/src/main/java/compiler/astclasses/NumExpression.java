package compiler.astclasses;

public class NumExpression implements Expression {

    private int value;
    
    public NumExpression(int num){
        value = num;
    }

    public void print(){
        System.out.print(value);
    }
}