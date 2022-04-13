package compiler.astclasses;

public class FunDecl implements Declaration{
    //Private variables
    private boolean voidReturn;
    private String id;
    private Param[] params;
    private CompoundStatement statement;
    //Constructor
    public FunDecl(boolean vRet, String ID, CompoundStatement cs){
        this(vRet, ID, null, cs);
    }
    public FunDecl(boolean vRet, String ID, Param[] p, CompoundStatement cs){
        voidReturn = vRet;
        id = ID;
        params = p;
        statement = cs;
    }
    //Print
    @Override
    public void print(int tabs){
        for(int i=0;i<tabs;i++) System.out.print('\t');
        if(voidReturn) System.out.println("void");
        else System.out.println("int");
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println(id);
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println('(');
        if(params==null){
            for(int i=0;i<tabs;i++) System.out.print('\t');
            System.out.print("void");
        }else{
            params[0].print(tabs+1);
            for(int i=1; i<params.length; i++){
                for(int j=0;j<tabs;j++) System.out.print('\t');
                System.out.println(',');
                params[i].print(tabs+1);
            }
        }
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println(')');
        statement.print(tabs+1);
    }
}