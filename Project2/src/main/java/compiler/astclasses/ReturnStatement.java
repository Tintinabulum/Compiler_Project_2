package compiler.astclasses;

public class ReturnStatement implements Statement{
    private Expression retExp;
    public ReturnStatement(){
        this(null);
    }
    public ReturnStatement(Expression e){
        retExp = e;
    }
    public void print(int tabs){
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println("return");
        if(retExp!=null){
            retExp.print(tabs+1);
        }
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println(';');
    }
}