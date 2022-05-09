package business.bundle.matrix;

import java.util.ArrayList;
import java.util.Iterator;

public class Row
{
    private Object obj;
    private Float  cortege[];
    public Row(Object obj, Float cortege[])
    {
        this.obj = obj;
        this.cortege = cortege;
    }

    public Row(Row orig)
    {
        this.obj = orig.obj;
        this.cortege = orig.cortege;
    }

    public float length()
    {
        double l = 0;
        for(int i=0;i<cortege.length;i++)
            l = l + Math.pow(cortege[i].doubleValue(), 2);
        l = Math.pow(l, 0.5);
        return (float) l;
    }

    public float scalarProduct(Row b)
    {
        float res =0;
        for(int i=0;i<cortege.length;i++)
            res = res + cortege[i] * b.cortege[i];
        return res;
    }

    public Float[] getCortege() {
        return cortege;
    }

    public float avg()
    {
        float sum=0;
        for(int i=0;i<cortege.length;i++)
            sum+=cortege[i];
        return sum/cortege.length;
    }

    public float mse(Row b)
    {
        float buf=0;
        for(int i=0;i<b.cortege.length;i++)
            buf += Math.pow(cortege[i]-b.cortege[i], 2);
        return buf/cortege.length;
    }

    public void rank()
    {
        ArrayList<Float> sorted = new ArrayList<Float>();
        for(Float num:cortege)
            sorted.add(num);
        sorted.sort(Float::compareTo);
        Float res[] =new Float[cortege.length];
        for (int i=0;i<cortege.length;i++)
        {
            float            elem      =cortege[i];
            ArrayList<Float> positions = new ArrayList<Float>();
            Iterator<Float>  iterator  = sorted.iterator();
            float            pos       =0;
            boolean flag = true;
            while (iterator.hasNext() && flag)
            {
                Float val = iterator.next();
                pos++;
                if(val == elem)
                {
                    while(val==elem)
                    {
                        positions.add(pos);
                        if(!iterator.hasNext())
                            break;
                        val = iterator.next();
                        pos++;
                    }
                    flag=false;
                }
            }
            res[i] = new Row(null,positions.toArray(new Float[0])).avg();
        }
        cortege=res;
    }

    public Object getObj() {
        return obj;
    }
}
