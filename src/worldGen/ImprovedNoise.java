/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package worldGen;

import java.util.ArrayList;

/**
 *
 * @author Dasty
 */
public final class ImprovedNoise 
{
    public ArrayList<Integer> p;
    public ImprovedNoise() 
    { 
        p = new ArrayList<Integer>();
        for(int i = 0; i < 512; i++)
        {
            p.add((int)(Math.random()*256 % 256));
        }
        for(int i = 0; i < 512; i++)
        {
            int j = (int) (Math.random()*256 % 256);
            int nSwap = p.get(i);
            p.set(i, p.get(j));
            p.set(j, nSwap);
        }
    }
    
    public double noise(double x, double y, double z) 
    {
        
        int X = (int)Math.floor(x) & 255,                  // FIND UNIT CUBE THAT
        Y = (int)Math.floor(y) & 255,                  // CONTAINS POINT.
        Z = (int)Math.floor(z) & 255;
        x -= Math.floor(x);                                // FIND RELATIVE X,Y,Z
        y -= Math.floor(y);                                // OF POINT IN CUBE.
        z -= Math.floor(z);
        double u = fade(x),                                // COMPUTE FADE CURVES
        v = fade(y),                                // FOR EACH OF X,Y,Z.
        w = fade(z);
        
        int A = p.get(X)+Y, AA = p.get(A)+Z, AB = p.get(A+1)+Z,      // HASH COORDINATES OF
            B = p.get(X+1)+Y, BA = p.get(B) +Z, BB = p.get(B+1) +Z;      // THE 8 CUBE CORNERS,

        return lerp(w, lerp(v, lerp(u, grad(p.get(AA), x  , y  , z   ),  // AND ADD
                grad(p.get(BA), x-1, y, z)), // BLENDED
                lerp(u, grad(p.get(AB), x, y-1, z),  // RESULTS
                grad(p.get(BB), x-1, y-1, z))),// FROM  8
                lerp(v, lerp(u, grad(p.get(AA+1), x, y, z-1),  // CORNERS
                grad(p.get(BA+1), x-1, y, z-1)), // OF CUBE
                lerp(u, grad(p.get(AB+1), x, y-1, z-1),
                grad(p.get(BB+1), x-1, y-1, z-1))));
    }
    
    private double fade(double t) 
    { 
        return t * t * t * (t * (t * 6 - 15) + 10); 
    }
   
    private double lerp(double t, double a, double b) 
    { 
        return a + t * (b - a); 
    }
   
    private double grad(int hash, double x, double y, double z) 
    {
        int h = hash & 15;                      // CONVERT LO 4 BITS OF HASH CODE
        double u = h<8 ? x : y, v = h<4 ? y : h==12||h==14 ? x : z;
        return ((h&1) == 0 ? u : -u) + ((h&2) == 0 ? v : -v);
    }
    
}
