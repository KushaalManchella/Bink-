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
import javax.swing.*;

/**
 * CS 180 - Project 5: Variation of classic Centipede arcade game
 * 
 * @author  James Wilkinson <jhwilkin@purdue.edu>
 * @version 1.6
 * @since   2011-08-08
 **/
public class Project5 extends JApplet{
    /** Plays sounds **/
    private GameSounds myGameSounds;
    
    /** Handles high scores **/
    private HighScores myHighScores;
    
    /** Displays main game screen and handle mouse input **/
    private GameCanvas myGameCanvas;
    
    /** Handles game logic **/
    private GameLogic myGameLogic;
    
    /** Frame with menu **/
    private Frame myFrame;
    
    /** Array of mushrooms **/
    private Mushroom myMushrooms[][];
    
    /** Array of centipedes **/
    private Centipede myCentipedes[];
    
    /** Player's ship **/
    private Ship myShip;
    
    /** Array of projectiles **/
    private Point myProjectiles[];

    /** spider object */
    private Spider mySpider;
    
    /**
     * Starts the program
     * 
     * @param args Array of input strings.  Not used.
     **/
    public static void main(String[] args){
        new Project5();
    }
    
    public Project5(){
        // Create objects for each program class
        myGameSounds = new GameSounds();
        myHighScores = new HighScores();
        myMushrooms = new Mushroom[Settings.width][Settings.height];
        myProjectiles = new Point[Settings.maxProjectiles];        
        myShip = new Ship(myGameSounds, myProjectiles);
        myCentipedes = new Centipede[Settings.centipedeStartSize];  
        //mySpider = new Spider(new Point(20,20), Settings.spiderHealth);      
        myGameCanvas = new GameCanvas(myGameSounds, myMushrooms, myCentipedes, myShip, myProjectiles, mySpider);
        myGameLogic = new GameLogic(myGameSounds, myGameCanvas, myHighScores, myMushrooms, myCentipedes, myShip, myProjectiles);
        myFrame = new Frame(myGameLogic, myGameCanvas, myHighScores);
        
        myGameCanvas.myGameLogic = myGameLogic;
        myHighScores.myFrame = myFrame;
        myGameLogic.myFrame = myFrame;
        myGameLogic.syncReset = true;
        
        myGameLogic.start();
        try{
            myGameLogic.join();
        }catch(Exception e){}
    }
}
