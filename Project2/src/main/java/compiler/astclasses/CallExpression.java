package compiler.astclasses;

public class CallExpression implements Expression{
    private Expression varExp;
    
    
    public void print(){
        varExp.print();
        System.out.print("(");
        //logic for going through the args;
        System.out.print(")");
    }
}
