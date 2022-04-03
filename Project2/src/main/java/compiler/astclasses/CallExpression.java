package compiler.astclasses;
import java.util.ArrayList;

public class CallExpression implements Expression{
    private Expression varExp;
    //private VarExpression varExp;
    private ArrayList<Expression> args;
    //private ArrayList<VarExpression> args;
    public CallExpression(/*Var*/Expression funName){
        varExp = funName;
        args = new ArrayList<Expression>();
    }
    public void addArgs(Expression e){
        args.add(e);
    }
    
    public void print(){
        varExp.print();
        System.out.print('(');
        for (int i = 0; i < args.size(); i++){
            if (i != 0)
                System.out.print(", ");
            args.get(i).print();
        }
        //logic for going through the args;
        System.out.print(')');
    }
}
