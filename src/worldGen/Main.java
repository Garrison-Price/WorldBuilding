/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package worldGen;

import javax.swing.JFrame;

/**
 *
 * @author Dasty
 */
public class Main extends JFrame
{
    public Main(World w)
    {
        super("World Generator");
        this.setSize(500, 500);
        add(w);
        this.setVisible(true);
    }
    
    public static void main(String args[])
    {
        World w = new World();
        
        Main m = new Main(w);
        m.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
