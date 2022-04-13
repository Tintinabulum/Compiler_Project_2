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
    public void print(int tabs){
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println("int");
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println(id);
        if(size>-1){
            for(int i=0;i<tabs;i++) System.out.print('\t');
            System.out.println('[');
            for(int i=0;i<tabs;i++) System.out.print('\t');
            System.out.println(size);
            for(int i=0;i<tabs;i++) System.out.print('\t');
            System.out.println(']');
        }
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println(';');
    }
}
