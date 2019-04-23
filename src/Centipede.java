/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kushaal Manchella
 */
import java.awt.*;
import java.util.*;

/**
 * Centipede direction, length and its segments
 * 
 * @author  James Wilkinson <jhwilkin@purdue.edu>
 * @version 1.6
 * @since   2011-08-08
 **/




public class Centipede{
    /** Array of the centipede's body segments **/
    public Segment segments[];
    
    /** Centipede head's horizontal heading **/
    public int horizontal;
    
    /** Centipede body's vertical heading **/
    public int vertical;
    
    /** Number of segments left in the centipede **/
    public int length;

    
    /**
     * @param inLength     Length of the centipede to create
     * @param inHorizontal Initial horizontal heading of the centipede head
     * @param inVertical   Initial vertical heading of the centipede head
     **/
    public Centipede(int inLength, int inHorizontal, int inVertical){
        length = inLength;
        horizontal = inHorizontal;
        vertical = inVertical;
        segments = new Segment[length];
    }
    
    /**
     * Returns the segment that matches that Point.
     * 
     * @param testLoc Grid location
     * @return int corresponding to the segment that is at <code>testLoc</code> location. -1 if none match
     */
    public int contains(Point testLoc){     
        for (int i = 0; i < length; i++){
            if ((segments[i].location.x == testLoc.x) &&
                (segments[i].location.y == testLoc.y)){
                return i;
            }
        }
        return -1;
    }
}
