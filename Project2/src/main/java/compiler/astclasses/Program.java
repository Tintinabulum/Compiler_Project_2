package compiler.astclasses;
import java.util.ArrayList;
public class Program {
    //Private variables
    private ArrayList<Declaration> decls;
    //Constructors
    public Program(){
        decls = new ArrayList();
    }
    public Program(Declaration d){
        this(d,9);
    }
    public Program(Declaration d, int extra){
        decls = new ArrayList(1+extra);
        decls.add(d);
    }
    public Program(Declaration[] d){
        this(d,9);
    }
    public Program(Declaration[] d,int extra){
        decls = new ArrayList(d.length+extra);
        for(Declaration e : d)
            decls.add(e); 
    }
    public Program(ArrayList<Declaration> d){
        this(d,9);
    }
    public Program(ArrayList<Declaration> d, int extra){
        decls = new ArrayList(d.size()+extra);
        for(int i=0;i<d.size();i++)
            decls.add(d.get(i));
    }
    //Add Declaration
    public void addDeclaration(Declaration d){
        decls.add(d);
    }
    public void addDeclaration(Declaration[] d){
        for(Declaration e:d)
            decls.add(e);
    }
    public void addDeclaration(ArrayList<Declaration> d){
        for(int i=0;i<d.size();i++)
            decls.add(d.get(i));
    }
    //Print
    public void print(){
        for(int i=0;i<decls.size();i++){
            decls.get(i).print(0);
            System.out.println();
        }
    }
}
