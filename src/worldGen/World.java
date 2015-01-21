/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package worldGen;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JPanel;

/**
 *
 * @author Dasty
 */
public class World extends JPanel implements Runnable
{
    ImprovedNoise n;
    double RawNoise[][] = new double[500][500];
    Thread thread;
    RollingParticle rp;
    double waterLine;
    public World()
    {
        this.setSize(500, 500);
        n = new ImprovedNoise();
        double increment = 0.007;
        double xoff = 0.0; // Start xoff at 0
        for(int x = 0; x < RawNoise.length; x++)
        {
            xoff += increment;
            double yoff = 0.0;
            for(int y = 0; y < RawNoise[x].length; y++)
            {
                yoff += increment;
                RawNoise[x][y] = (n.noise(xoff,yoff,0)+1)/2;
            }
        }
        rp = new RollingParticle(true);
        for(int x = 0; x < RawNoise.length; x++)
        {
            for(int y = 0; y < RawNoise[x].length; y++)
            {
                RawNoise[x][y] = (RawNoise[x][y])*(rp.tiles[x][y]);
            }
        }
        //rp.tiles = RawNoise;
        //rp.normalize();
        //RawNoise = rp.tiles;
        normalize();
        findWaterLine();
        thread = new Thread(this);
        thread.start();
    }
    
    public void findWaterLine()
    {
        ArrayList<Double> w = new ArrayList();
        int count = 0;
        for(int x = 0; x < RawNoise.length; x++)
        {
            for(int y = 0; y < RawNoise[x].length; y++)
            {
                w.add(RawNoise[x][y]);
            }
        }
        Object[] o = w.toArray();
        Double[] doubleA = Arrays.copyOf(o, o.length, Double[].class);
        Arrays.sort(doubleA);
        waterLine = doubleA[(int)((doubleA.length-1)*.6)];
        System.out.println(waterLine);
    }
    
    public void paintComponent(Graphics g)
    {
        for(int x = 0; x < RawNoise.length; x++)
        {
            for(int y = 0; y < RawNoise[x].length; y++)
            {
                //g.setColor(new Color((int)rp.tiles[x][y],(int)rp.tiles[x][y],(int)rp.tiles[x][y]));
                //g.setColor(new Color((int)RawNoise[x][y],(int)RawNoise[x][y],(int)RawNoise[x][y]));
                //g.setColor(new Color((int)(RawNoise[x][y]*255),(int)(RawNoise[x][y]*255),(int)(RawNoise[x][y]*255)));
                if(RawNoise[x][y] < waterLine)
                {
                    if(RawNoise[x][y] >= waterLine-40)
                        g.setColor(Color.cyan);
                    else
                        g.setColor(Color.blue);
                }
                else
                {
                    if(RawNoise[x][y] <= waterLine+15)
                        g.setColor(Color.yellow);
                    else if(RawNoise[x][y] >= waterLine+15 && RawNoise[x][y] <= waterLine+35)
                        g.setColor(Color.green);
                    else if(RawNoise[x][y] >= 255-25)
                        g.setColor(Color.LIGHT_GRAY);
                    else if(RawNoise[x][y] >= 255-50 && RawNoise[x][y] < 255-25)
                        g.setColor(new Color(139,69,19));
                    else
                        g.setColor(new Color(0,100,0));
                }
                g.drawRect(x, y, 1, 1);
            }
        }
        
        
    }
    
    public void update()
    { 
        repaint();
    }
    
    public void normalize()
    {
        double smallest = 1000000;    // A large value, INT_MAX would work well here.
        double largest = 0;

        // Find the largest and smallest tiles
        for (int ix = 0; ix < RawNoise.length; ix++)
        {
            for (int iy = 0; iy < RawNoise[ix].length; iy++)
            {
                if (RawNoise[ix][iy] > largest)
                {
                    largest = RawNoise[ix][iy];
                }
                if (RawNoise[ix][iy] < smallest)
                {
                    smallest = RawNoise[ix][iy];
                }
            }
        }

        // Normalize
        for (int ix = 0; ix < RawNoise.length; ix++)
        {
            for (int iy = 0; iy < RawNoise[ix].length; iy++)
            {
                double percent = (RawNoise[ix][iy] - smallest) / (largest-smallest);
                RawNoise[ix][iy] = Math.round(percent * 255);
            }
        }
    }
    
    @Override
    public void run() 
    {
        while(true)
        {
            try
            {
                Thread.sleep(28);
                update();
            }
            catch(Exception e)
            {

            }
        }
    }
}
