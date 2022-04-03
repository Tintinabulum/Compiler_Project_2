package compiler.astclasses;

public class NumExpression implements Expression {

    private int value;
    
    public NumExpression(int num){
        num = value;
    }

    public void print(){
        System.out.print(value);
    }
}
