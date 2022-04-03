package compiler.astclasses;

public class VarExpression implements Expression 
{
    String id;
    Expression arrayselect;

    public VarExpression(String idName, Expression as)
    {
        id = idName;
        arrayselect = as;
    }

    public VarExpression(String idName)
    {
        this(idName, null);
    }
    
    @Override
    public void print()
    {
        System.out.print(id);
        if(arrayselect != null)
        {
            System.out.print('[');
            arrayselect.print();
            System.out.print(']');
        }
    }
    
}