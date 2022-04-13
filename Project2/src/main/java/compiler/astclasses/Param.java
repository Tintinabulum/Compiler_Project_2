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
    public void print(int tabs){
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println("int");
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println(id);
        if(isArray){
            for(int i=0;i<tabs;i++) System.out.print('\t');
            System.out.println("[]");
        }
    }
}