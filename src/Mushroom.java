/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kushaal
 */
import java.awt.*;

/**
 * Holds health and location of a mushroom
 * 
 * @author  James Wilkinson <jhwilkin@purdue.edu>
 * @version 1.6
 * @since   2011-08-08
 **/
public class Mushroom{
    /** Small grid location of the mushroom **/
    public Point loc;
    
    /** Health of the mushroom **/
    public int health;
    
    /**
     * @param loc    Small grid location of the mushroom
     * @param health Initial health of the mushroom
     **/
    public Mushroom(Point loc, int health){
        this.loc = loc;
        this.health = health;
    }
}
