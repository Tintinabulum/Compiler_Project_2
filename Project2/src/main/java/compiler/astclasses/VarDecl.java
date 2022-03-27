package compiler.astclasses;

public class VarDecl implements Declaration{
    //Private Variables
    private String id;
    private int size;
    //Constructors
    public VarDecl(String id){
        this.id = id;
        size = -1;
    }
    public VarDecl(String id, int size){
        this.id = id;
        this.size = size;
    }
    //Print
    @Override
    public void print(){
        System.out.print("int ");
        System.out.print(id);
        if(size>-1){
            System.out.print('[');
            System.out.print(size);
            System.out.print(']');
        }
        System.out.print(';');
    }
}
