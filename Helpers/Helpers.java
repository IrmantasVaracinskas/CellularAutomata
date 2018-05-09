package Helpers;

/**
 * Created by Irmis on 2018-01-07.
 */
public class Helpers
{
    public static int pow(double a, double b)
    {
        return (int)Math.pow(a, b);
    }

    public static int max(int a, int b)
    {
        if(a > b)
            return a;
        return b;
    }

    public static int min(int a, int b)
    {
        if(a < b)
            return a;
        return b;
    }
}