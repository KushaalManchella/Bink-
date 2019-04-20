/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Omar
 */
import java.awt.*;

/**
 * Holds information about the player's ship and handles firing of projectiles
 * 
 * @author              James Wilkinson <jhwilkin@purdue.edu>
 * @version             1.6
 * @since               2011-08-08
 **/
public class Ship{
    /** Position that the ship is trying to move into **/
    public Point tryLoc;
    
    /** Actual position of the ship **/
    public Point loc;
    
    /** Number of lives that the ship has **/
    public int lives;
    
    /** Used by the super laser.  True if firing. **/
    public boolean firing;
    
    /** Time (ms) left for the ship to be invulnerable **/
    public int invulnerableTime;
    
    /** Array of projectiles **/
    private Point myProjectiles[];
    
    /** Time (ms) left for the ship to be invulnerable **/
    private GameSounds myGameSounds;
    
    /**
     * @param inGameSounds  Used to generate sound when ship fires
     * @param inProjectiles Used to create new projectiles
     **/
    public Ship(GameSounds inGameSounds, Point inProjectiles[]){
        myProjectiles = inProjectiles;
        myGameSounds = inGameSounds;
        
        loc = new Point(Settings.shipStartLoc.x, Settings.shipStartLoc.y);
        tryLoc = new Point(loc.x, loc.y);
        lives = Settings.startLives;
        invulnerableTime = 0;
    }
    
    /**
     * Try to fire a projectile from the ship
     * 
     * Fires a projectile from the ship if the ship is not invulnerable and there are less than Settings.maxProjectiles projectiles.
     **/
    
    public void fire(){
        if (invulnerableTime == 0){
            for (int i = 0; i < Settings.maxProjectiles; i++){
                if (myProjectiles[i] == null){
                    // add projectile
                    myProjectiles[i] = new Point((int) Math.round(loc.x + Settings.scale / 2.0), (int) Math.round(loc.y));
                    if (Settings.superLaser){
                        myGameSounds.laser();
                    }else{
                        myGameSounds.cannon();
                    }
                    break;
                }
            }
        }
    }

}



