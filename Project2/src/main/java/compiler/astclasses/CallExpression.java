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
    
    public void print(int tabs){
        varExp.print(tabs+1);
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println('(');
        for (int i = 0; i < args.size(); i++){
            if (i != 0){
                for(int j=0;j<tabs;j++)
                    System.out.print('\t');
                System.out.println(',');
            }
            args.get(i).print(tabs+1);
        }
        //logic for going through the args;
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println(')');
    }
}
