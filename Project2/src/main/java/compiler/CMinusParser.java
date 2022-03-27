package compiler;

import java.io.BufferedReader;
import compiler.astclasses.Program;

public class CMinusParser implements Parser{
    //Private variables
    private CMinusScanner scan;
    private Program root;
    //Constructors
    public CMinusParser(BufferedReader r){
        this(new CMinusScanner(r));
    }
    public CMinusParser(CMinusScanner c){
        scan = c;
        root = null;
    }
    //Print
    public void printTree(){
        if(root!=null)
            parse();
        root.print();
    }
    //Parse
    public void parse(){
        
    }
    
}
