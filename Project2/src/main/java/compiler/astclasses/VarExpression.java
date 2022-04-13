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
    public void print(int tabs)
    {
        for(int i=0;i<tabs;i++) System.out.print('\t');
        System.out.println(id);
        if(arrayselect != null)
        {
            for(int i=0;i<tabs;i++) System.out.print('\t');
            System.out.println('[');
            arrayselect.print(tabs+1);
            for(int i=0;i<tabs;i++) System.out.print('\t');
            System.out.println(']');
        }
    }
    
}