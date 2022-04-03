package compiler.astclasses;

public class ReturnStatement implements Statement{
    private Expression retExp;
    public ReturnStatement(){
        this(null);
    }
    public ReturnStatement(Expression e){
        retExp = e;
    }
    public void print(){
        System.out.print("return");
        if(retExp!=null){
            System.out.print(' ');
            retExp.print();
        }
        System.out.println(';');
    }
}