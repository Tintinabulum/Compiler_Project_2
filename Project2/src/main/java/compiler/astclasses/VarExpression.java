package compiler.astclasses;

public class VarExpression implements Expression 
{
    private String id;
    private Expression sel; 

    public VarExpression(String ident, Expression selector)
    {
        id = ident;
        sel = selector;
    }
    
    public VarExpression(String ident)
    {
        id = ident;
        sel = null;
    }
    
    @Override
    public void print()
    {
        System.out.print(id);
        if(sel != null)
        {
            System.out.print('[');
            sel.print();
            System.out.println("];");
        }
    }
    
}