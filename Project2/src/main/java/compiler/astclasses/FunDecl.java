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
    public void print(){
        if(voidReturn) System.out.print("void ");
        else System.out.print("int ");
        System.out.print(id);
        System.out.print('(');
        if(params==null) System.out.print("void");
        else{
            params[0].print();
            for(int i=1; i<params.length; i++){
                System.out.print(", ");
                params[1].print();
            }
        }
        System.out.print(')');
        statement.print();
    }
}