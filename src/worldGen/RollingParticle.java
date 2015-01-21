/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package worldGen;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Dasty
 */
public class RollingParticle 
{

    private static int PARTICLE_ITERATIONS = 60000;
    private static int PARTICLE_LENGTH = 5000;
    private static int EDGE_BIAS = 50;
    private static double OUTER_BLUR = .75;
    private static double INNER_BLUR = 0.88;
    public double[][] tiles;
    private int WIDTH = 500;
    private int HEIGHT = 500;
                
    public RollingParticle(boolean centerBias)
    {
        tiles = new double[WIDTH][HEIGHT];
        for (int iterations = 0; iterations < PARTICLE_ITERATIONS; iterations++)
        {
            // Start nearer the center
            int sourceX;
            int sourceY;
            if (centerBias)
            {
                sourceX = (int)(Math.random() * (WIDTH-(EDGE_BIAS*2)) + EDGE_BIAS);
                sourceY = (int)(Math.random() * (HEIGHT-(EDGE_BIAS*2)) + EDGE_BIAS);
            }
            // Random starting location
            else
            {
                sourceX = (int)(Math.random() * (WIDTH - 1));
                sourceY = (int)(Math.random() * (HEIGHT - 1));
            }

            for (int length = 0; length < PARTICLE_LENGTH; length++)
            {
                sourceX += Math.round(Math.random() * 2 - 1);
                sourceY += Math.round(Math.random() * 2 - 1);

                if (sourceX < 1 || sourceX > WIDTH -2 || sourceY < 1 || sourceY > HEIGHT - 2) break;

                Integer[] hoodx = getNeighborhood(sourceX, sourceY)[0];
                Integer[] hoody = getNeighborhood(sourceX, sourceY)[1];

                for (int i = 0 ; i < hoodx.length; i++)
                {
                    if (tiles[hoodx[i]][hoody[i]] < tiles[sourceX][sourceY])
                    {
                        sourceX = hoodx[i];
                        sourceY = hoody[i];
                        break;
                    }
                }

                tiles[sourceX][sourceY]++;
            }
        }

        blurEdges();

        normalize();
        
//        System.out.print("[");
//        for(int x = 0; x < tiles.length; x++)
//        {
//            System.out.print("[");
//            for(int y = 0; y < tiles[x].length; y++)
//            {
//                System.out.print(tiles[x][y]+",");
//            }
//            System.out.print("],\n");
//        }
//        System.out.println("]");
    }
    
    public void normalize()
    {
        double smallest = 1000000;    // A large value, INT_MAX would work well here.
        double largest = 0;

        // Find the largest and smallest tiles
        for (int ix = 0; ix < WIDTH; ix++)
        {
            for (int iy = 0; iy < HEIGHT; iy++)
            {
                if (tiles[ix][iy] > largest)
                {
                    largest = tiles[ix][iy];
                }
                if (tiles[ix][iy] < smallest)
                {
                    smallest = tiles[ix][iy];
                }
            }
        }

        // Normalize
        for (int ix = 0; ix < WIDTH; ix++)
        {
            for (int iy = 0; iy < HEIGHT; iy++)
            {
                double percent = (tiles[ix][iy] - smallest) / (largest-smallest);
                tiles[ix][iy] = Math.round(percent * 255);
            }
        }
    }

                            
    private Integer[][] getNeighborhood(int x, int y)
    {
        Integer[][] result = new Integer[2][9];
        int count = 0;
        for (int a = -1; a <= 1; a++)
        {
            for (int b = -1; b <= 1; b++)
            {
                if (x + a >= 0 && x + a < WIDTH && y + b >= 0 && y + b < HEIGHT)
                {
                    result[0][count] = x+a;
                    result[1][count] = y+b;
                }
                count++;
            }
        }

        // Return the neighborhood in no particular order
        List ax = Arrays.asList(result[0]);
        List ay = Arrays.asList(result[1]);
        Collections.shuffle(ax);
        Collections.shuffle(ay);
        Object[] nx = ax.toArray();
        Object[] ny = ay.toArray();
        Integer[] integerArrayx = Arrays.copyOf(nx, nx.length, Integer[].class);
        Integer[] integerArrayy = Arrays.copyOf(ny, ny.length, Integer[].class);

        result[0] = integerArrayx;
        result[1] = integerArrayy;

        return result;
    }

    private void blurEdges()
    {
        for (int ix = 0; ix < WIDTH; ix++)
        {
            for (int iy = 0; iy < HEIGHT; iy++)
            {
                // Multiply the outer edge and the second outer edge by some constants to ensure the world does not touch the edges.
                if (ix == 0 || ix == WIDTH -1 || iy == 0 || iy == HEIGHT -1) 
                    tiles[ix][iy] *= OUTER_BLUR;
                else if (ix == 1 || ix == WIDTH -2 || iy == 1 || iy == HEIGHT -2) 
                    tiles[ix][iy] *= INNER_BLUR;
            }
        }
    }
}
