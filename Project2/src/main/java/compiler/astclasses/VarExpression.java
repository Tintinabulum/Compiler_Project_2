package compiler.astclasses;

public class VarExpression implements Expression 
{
    String id;

    public VarExpression(String idName)
    {
        id = idName;
    }
    
    public VarExpression()
    {
        this(null);
    }
    
    @Override
    public void print()
    {
        if(id != null)
        {
            System.out.print(id);
        }
    }
    
}