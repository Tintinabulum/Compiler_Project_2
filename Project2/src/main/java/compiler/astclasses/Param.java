package compiler.astclasses;

public class Param {
    //Private variables
    private String id;
    private boolean isArray;
    //Constructors
    public Param(String name){
        this(name, false);
    }
    public Param(String name, boolean array){
        id = name;
        isArray = array;
    }
    //Print
    public void print(){
        System.out.print("int ");
        System.out.print(id);
        if(isArray)
            System.out.print("[]");
    }
}