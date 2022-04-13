package compiler.astclasses;

public class NumExpression implements Expression {

    private int value;
    
    public NumExpression(int num){
        value = num;
    }

    public void print(int tabs){
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println(value);
    }
}