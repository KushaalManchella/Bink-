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
import java.math.*;

/**
 * Handles most game logic
 * 
 * @author  James Wilkinson <jhwilkin@purdue.edu>
 * @version 1.6
 * @since   2011-08-08
 **/
public class GameLogic extends Thread{
    /** Set to true to synchronously reset the game **/
    public boolean syncReset;
    
    /** True if the game is over **/
    public boolean gameOverFlag;
    
    /** Current game level **/
    public int level;
    
    /** Current game difficulty expressed as centipede delay **/
    public int difficulty;
    
    /** True if the game is paused **/
    public boolean paused;
    
    /** Current game score **/
    public int score;
    
    /** Used to change the Pause Game text in the frame **/
    public Frame myFrame;
    
    private GameSounds myGameSounds;
    private Centipede myCentipedes[];
    private Mushroom myMushrooms[][];
    private GameCanvas myGameCanvas;
    private Ship myShip;
    private HighScores myHighScores;    
    private Point myProjectiles[];
    private Random generator;
    
    /**
     * @param inGameSounds  Called to play sound effects
     * @param inGameCanvas  Controls display of game objects
     * @param inHighScores  Called to add to high scores
     * @param inMushrooms   Array of mushrooms
     * @param inCentipedes  Array of centipedes
     * @param inShip        Player's ship
     * @param inProjectiles Array of projectiles
     **/
    public GameLogic(GameSounds inGameSounds, GameCanvas inGameCanvas, HighScores inHighScores, Mushroom inMushrooms[][], Centipede inCentipedes[], Ship inShip, Point inProjectiles[]){
        myGameCanvas = inGameCanvas;
        myGameSounds = inGameSounds;
        myHighScores = inHighScores;
        myMushrooms = inMushrooms;
        myCentipedes = inCentipedes;
        myProjectiles = inProjectiles;
        myShip = inShip;
        
        generator = new Random();
        syncReset = false;
        difficulty = Settings.startDifficulty;
    }
    
    /**
     * Starts the GameLogic thread
     **/
    public void run(){
        int i;
        int iteration = 0;
        int life_lost = 0;    // variable to track if life has been lost so that centipede resets to beginning 
        int life_wait = -1;   // variable to make centipede wait while explosion is happening to ship 
        long lastCentUpdate = System.currentTimeMillis();
        long lastFireUpdate = System.currentTimeMillis();

        if(iteration == 100)
        {
            iteration = 1;
        }
        
        // This sets up double buffering to smooth out the display
        myGameCanvas.c.createBufferStrategy(2);
        
        // The game logic will loop as long as the program is running
        while(true){
            // Game should only reset at the top of the game loop
            // If the syncReset flag is true, then reset the game
            if (syncReset == true){
                initGame();
                myGameSounds.newGame();
                syncReset = false;
                paused = false;
                myFrame.pauseGame.setText("Pause Game");
            }
            
            // If game is paused, don't process any logic
            if (!paused){
                // Has player defeated all centipedes?
                for (i = 0; i < myCentipedes.length; i++){
                    if ((myCentipedes[i] != null) &&
                        (myCentipedes[i].length != 0)){
                        break;
                    }
                }
                
                // All centipedes defeated.  Proceed to next level
                if (i == myCentipedes.length){
                    // Initialize centipede array
                    myCentipedes[0] = new Centipede(Settings.centipedeStartSize, Settings.RIGHT, Settings.DOWN);
                    for (i = 0; i < Settings.centipedeStartSize; i++){
                        myCentipedes[0].segments[i] = new Point(- i * myCentipedes[0].horizontal, 0);
                    }
                    
                    // Clear all but the first centipede
                    for (i = 1; i < Settings.centipedeStartSize; i++){
                        myCentipedes[i] = null;
                    }
                    
                    // Play next level sound increment level
                    myGameSounds.nextLevel();
                    level++;
                    Settings.centDelay = (int) Math.round(difficulty / Math.pow(Settings.levelFactor, level));
                }
                
                // Only move centipedes after centDelay time has passed
                if(life_wait != 1){
                    if (System.currentTimeMillis() - lastCentUpdate > Settings.centDelay){
                        lastCentUpdate = System.currentTimeMillis();
                        moveCentipedes();
                    }
                }
                
                // Check if ship should be firing
                // replace settings.superlaserdelay to 300
                if ((myShip.firing) && 
                    (System.currentTimeMillis() - lastFireUpdate > Settings.superLaserDelay)){
                    lastFireUpdate = System.currentTimeMillis();
                    myShip.fire();
                }
                
                // Move the projectiles
                moveProjectiles();
                

                // Move the ship
                if (!gameOverFlag){
                    life_lost = moveShip();
                }

                if(life_lost == 1)
                {
                    // Initialize centipede array
                    myCentipedes[0] = new Centipede(Settings.centipedeStartSize, Settings.RIGHT, Settings.DOWN);
                    for (i = 0; i < Settings.centipedeStartSize; i++){
                        myCentipedes[0].segments[i] = new Point(- i * myCentipedes[0].horizontal, 0);
                    }
                    
                    // Clear all but the first centipede
                    for (i = 1; i < Settings.centipedeStartSize; i++){
                        myCentipedes[i] = null;
                    }
                    

                    // restore all centipedes to original health 

                    for (int m = 0; m < Settings.width; m++)
                    {
                        for(int n = 0; n < Settings.height; n++)
                        {
                            if(myMushrooms[m][n] != null)
                            {
                                myMushrooms[m][n].health = 3;
                            }
                        }
                    }

                    


                    life_lost = 0;
                    life_wait = 1;
                }

                
                
                // Decrement the ship's invulnerable time counter
                if (myShip.invulnerableTime != 0){
                    myShip.invulnerableTime--;
                }
                else if(myShip.invulnerableTime == 0 && iteration != 0)
                {
                    life_wait = -1;
                }

            }
            // Update the canvas
            myGameCanvas.drawFrame();
            iteration++;
        }
    }
    
    /**
     * Initialize a new game
     **/
    public void initGame(){
        int i, j, x, y;
        
        Settings.centDelay = difficulty;
        level = Settings.startLevel;
        myShip.lives = Settings.startLives;
        myShip.invulnerableTime = 0;
        score = 0;
        
        // Initialize centipede array
        myCentipedes[0] = new Centipede(Settings.centipedeStartSize, Settings.RIGHT, Settings.DOWN);
        for (i = 0; i < Settings.centipedeStartSize; i++){
            myCentipedes[0].segments[i] = new Point(- i * myCentipedes[0].horizontal, 0);
        }
        
        // Clear all but the first centipede
        for (i = 1; i < Settings.centipedeStartSize; i++){
            myCentipedes[i] = null;
        }
        
        // Create array of mushrooms
        for (i = 0; i < Settings.width; i++){
            for (j = 0; j < Settings.height; j++){
                myMushrooms[i][j] = null;
            }
        }
        
        // Randomly place starting mushrooms
        for (i = 0; i < Settings.startShrooms; i++){
            do{
                x = (generator.nextInt(Settings.width-2))+1; // Keep mushrooms off the edge of the screen
                y = (generator.nextInt(Settings.height-3))+1; // Keep mushrooms away from the last 2 lines of the screen and the top line
            }while(myMushrooms[x][y] != null);
            myMushrooms[x][y] = new Mushroom(new Point(x, y), Settings.shroomStartHealth);
        }
        
        // Clear array of projectiles
        for (i = 0; i < myProjectiles.length; i++){
            myProjectiles[i] = null;
        }
        
        // Switch off gameOverFlag and play the new game sound
        gameOverFlag = false;
        myGameSounds.newGame();
    }
    
    /**
     * Returns sum corresponding to overlapped objects
     * 
     * @param loc Unscaled grid coordinate
     * @return Each potentially overlapping object has a value that is a power of two
     * These values are summed up and then returned to the calling method.
     * The calling method can then run a modulus to find exactly what was impacted.
     * Refer to Settings.java for the return values.
     **/
    private int overlap(Point loc){
        int result = 0;
        int i;
        Point scaledPoint;
        
        // Check centipedes
        for (i = 0; i < myCentipedes.length; i++){
            if ((myCentipedes[i] != null) &&
                (myCentipedes[i].length != 0)){
                if (myCentipedes[i].contains(loc) != -1){
                    result += Settings.CENT;
                    break;
                }
            }
        }
        
        // Check mushrooms
        if (((loc.x < myMushrooms.length) && (loc.x >= 0)) &&
            ((loc.y < myMushrooms[loc.x].length) && (loc.y >= 0)) &&
            (myMushrooms[loc.x][loc.y] != null)){
            result += Settings.SHROOM;
        }
        
        // Check ship
        scaledPoint = new Point((int) Math.floor(myShip.loc.x/Settings.scale), (int) Math.floor(myShip.loc.y/Settings.scale));
        if (loc.equals(scaledPoint)){
            result += Settings.SHIP;
        }
        
        // Check projectiles
        for (i = 0; i < myProjectiles.length; i++){
            if (myProjectiles[i] != null){
                scaledPoint = new Point((int) Math.floor(myProjectiles[i].x/Settings.scale), (int) Math.floor(myProjectiles[i].y/Settings.scale));
                if (loc.equals(scaledPoint)){
                    result += Settings.PROJECTILE;
                    break;
                }
            }
        }
        
        // Check for wall impact
        if ((loc.x < 0) ||
            (loc.y < 0) ||
            (loc.x >= Settings.width) ||
            (loc.y >= Settings.height)){
            result += Settings.WALL;
        }
        
        return result;
    }
    
    /**
     * Moves the centipedes to follow the segment ahead of it
     * 
     * Centipedes will try to move its head to the position directly in front of it.
     * It will then have all following segments change its position to that of the segment
     * that was ahead of it.  If the head cannot move to its desired position, then it will
     * move vertically and reverse its horizontal direction.  If at the top/bottom of the screen
     * then reverse the vertical direction as well.  A centipede can share space with a mushroom
     * but that mimics the behavior of the original game and is intended.
     **/
    private void moveCentipedes(){
        Point curHead;
        Point newHead;
        Point tempPoints[] = new Point[2];
        int overlapTest;
        
        // Loop through all centipedes
        for(int i = 0; i < myCentipedes.length; i++){    
            // Only continue if this centipede actually exists
            if (myCentipedes[i] != null){
                // Save the current head for use by the second segment
                curHead = new Point(myCentipedes[i].segments[0].x, myCentipedes[i].segments[0].y);
                
                // Try to move to next horizontal space
                newHead = new Point(curHead.x + myCentipedes[i].horizontal, curHead.y);
                
                // Check to see if the newHead space is blocked
                overlapTest = overlap(newHead);
                
                // If next space is blocked by anything other than a ship or projectile
                // then move vertically and reverse its horizontal direction.
                if ((overlapTest%(Settings.SHIP*2) != Settings.SHIP) &&
                    (overlapTest%(Settings.PROJECTILE*2) != Settings.PROJECTILE) &&
                    (overlapTest != 0)){
                    
                    // If at top/bottom of screen, reverse vertical direction
                    if (curHead.y == 0){
                        myCentipedes[i].vertical = Settings.DOWN;
                    }else if (curHead.y == Settings.height - 1){
                        myCentipedes[i].vertical = Settings.UP;
                    }
                    
                    // Move vertically and reverse horizontal direction
                    newHead.x = curHead.x;
                    newHead.y = curHead.y + myCentipedes[i].vertical;
                    myCentipedes[i].horizontal *= -1;
                }
                
                // Centipede segments follow next in line
                tempPoints[1] = newHead;
                for (int j = 0; j < myCentipedes[i].length; j++){
                    tempPoints[j%2] = myCentipedes[i].segments[j];
                    myCentipedes[i].segments[j] = tempPoints[(j+1)%2];
                }
            }
        }
    }
    
    /**
     * Tries to move the ship according to its tryLoc provided by the GameCanvas
     * 
     * If the ship tries to move into a mushroom, its location will be set to a distance away
     * from the mushroom equal to the game's scaling factor (Settings.scale).  A ship can slip
     * between two diagonal mushrooms because of this.  If the ship tries to move into a centipede
     * then it will lose a life, blow up and become invulnerable for Settings.invulnerableTime ms.
     * If the ship loses its last life, then the game is over.
     **/
    private int moveShip(){
        int life_lost = 0;
        int i, j;
        double distance, angle;
        double shipCenterX, shipCenterY;
        double testCenterX, testCenterY;
        int testX, testY;
        Point scaledDownPoint;
        
        // Comparisons need to be done based on the distance from the ship's center
        shipCenterX = myShip.tryLoc.x + (Settings.scale/2.0);
        shipCenterY = myShip.tryLoc.y + (Settings.scale/2.0);
        
        // First check if ship has impacted a centipede
        for (i = 0; i < myCentipedes.length; i++){
            // Only test against valid centipedes
            if (myCentipedes[i] != null){
                // Loop through each segment of the centipede, testing for impact
                for (j = 0; j < myCentipedes[i].length; j++){
                    // Find the center of the centipede segment
                    testCenterX = Settings.scale*(myCentipedes[i].segments[j].x + 0.5);
                    testCenterY = Settings.scale*(myCentipedes[i].segments[j].y + 0.5);
                    
                    // Calculate the distance between the two centers
                    distance = Math.sqrt(Math.pow(shipCenterX - testCenterX, 2) + Math.pow(shipCenterY - testCenterY, 2));
                                        
                    // If distance less than the game scale and the ship is not invulnerable, then they have collided.
                    if ((distance < Settings.scale) &&
                        (myShip.invulnerableTime == 0)){
                        // Play the game explosion sound, decrement the lives counter and set the invulnerable timer.
                        myGameSounds.shipExplode();
                        myShip.lives--;
                        life_lost = 1;
                        myShip.invulnerableTime = Settings.invulnerableTime;

                        // If the ship has run out of lives, then the game is over.  
                        if (myShip.lives < 0){
                            gameOver();
                        }
                    }
                }
            }
        }
        
        // Because the ship's position is based on a full pixel grid but the mushrooms positions are based on the smaller non-scaled grid,
        // it is necessary to create a scaled down coordinate corresponding to the ship's location to get the nine mushrooms to test.
        scaledDownPoint = new Point((int) Math.floor(shipCenterX/Settings.scale), (int) Math.floor(shipCenterY/Settings.scale));
        
        // Check if the ship is trying to move into a mushroom.  Limit search to the 9 squares surrounding the ship
        // If the ship is trying to move into several mushrooms, this will work by resetting the ship's test position
        // based on each mushroom's position.  So if it is too close to the first mushroom, the ship's test position is
        // moved away from it.  Then that new test position is tested against the other mushrooms.  Consequently, it is
        // possible for a ship to be wedged squarely between two mushrooms.
        for (i = -1; i < 2; i++){
            for (j = -1; j < 2; j++){
                testX = scaledDownPoint.x + i;
                testY = scaledDownPoint.y + j;
                
                // Make sure that tested mushrooms are valid and within the ship's allowed movable range
                if (((testX < myMushrooms.length) && (testX >= 0)) &&
                    ((testY < myMushrooms[testX].length) && (testY >= 0)) &&
                    (myMushrooms[testX][testY] != null) &&
                    (myMushrooms[testX][testY].loc.y > (Settings.height - Settings.shipVerticalRange - 2))){
                    
                    // Calculate a full scale center for the mushroom.
                    testCenterX = Settings.scale*(myMushrooms[testX][testY].loc.x + 0.5);
                    testCenterY = Settings.scale*(myMushrooms[testX][testY].loc.y + 0.5);
                    
                    // Calculate the full scale distance between the ship and the mushroom.
                    distance = Math.sqrt(Math.pow(shipCenterX - testCenterX, 2) + Math.pow(shipCenterY - testCenterY, 2));

                    // Move the ship so that it is a valid distance away from the mushroom
                    if (distance == 0){
                        shipCenterX = testCenterX + Settings.scale;
                        shipCenterY = testCenterY + Settings.scale;
                    }else if (distance < Settings.scale){
                        shipCenterX = testCenterX + Settings.scale*(shipCenterX - testCenterX)/distance;
                        shipCenterY = testCenterY + Settings.scale*(shipCenterY - testCenterY)/distance;
                    }
                }
            }
        }
        
        // Set the ship's location based on the calculations above
        myShip.loc.x = (int) Math.round(shipCenterX - (Settings.scale/2.0));
        myShip.loc.y = (int) Math.round(shipCenterY - (Settings.scale/2.0));

        return life_lost;
    }
    
    /**
     * Moves the projectiles and checks for collision
     * 
     * Tries to move the projectiles vertically by one pixel.  If the destination overlaps something else,
     * then remove the projectile and react appropriately to the impacted object.
     **/
    private void moveProjectiles(){
        int overlapResult;
        int hitSegment;
        int newCentLength;
        int newCentVert;
        int newCentHor;
        Point scaledDownPoint;
        Point newCentHead;
        
        // Cycle through all projectiles
        for (int i = 0; i < Settings.maxProjectiles; i++){
            
            // Only perform operations on valid projectiles
            if (myProjectiles[i] != null){
                
                // Scale down the projectile's position to the smaller grid and then move it up a grid square for testing
                scaledDownPoint = new Point((int) Math.round(myProjectiles[i].x/Settings.scale), (int) Math.floor(myProjectiles[i].y/Settings.scale));
                scaledDownPoint.y--;
                
                // Test for collision with the new location
                overlapResult = overlap(scaledDownPoint);
                
                // If the projectile will hit a wall, then just remove the projectile
                if (overlapResult%(Settings.WALL*2) >= Settings.WALL){
                    myProjectiles[i] = null;
                    
                // If the projectile will hit a centipede, then remove the projectile and perform appropriate reaction
                }else if (overlapResult%(Settings.CENT*2) >= Settings.CENT){
                    myProjectiles[i] = null;
                    
                    // Increase score according to difficulty.  Faster centipedes yield more points.  Super laser decreases score by a factor of Settings.maxProjectiles.
                    if (Settings.superLaser){
                        score += (int) Math.round((Settings.centDelayEasy - Settings.centDelay)*Math.pow(Settings.levelFactor, level)/Settings.maxProjectiles);
                    }else{
                        score += (int) Math.round((Settings.centDelayEasy - Settings.centDelay)*Math.pow(Settings.levelFactor, level));
                    }
                    
                    // Find impacted centipede
                    for(int j = 0; j < Settings.centipedeStartSize; j++){
                        
                        // Only check centipedes that exist and have segments
                        if ((myCentipedes[j] != null) &&
                            (myCentipedes[j].length != 0)){
                            hitSegment = myCentipedes[j].contains(scaledDownPoint);
                            
                            // If the centipede contains the projectile, then get the segment that was hit
                            if (hitSegment != -1){
                                
                                // Only create a new centipede if it did not hit the tail
                                if (hitSegment < myCentipedes[j].length - 1){
                                    
                                    // Loop through all centipede array positions until a vacant one is found
                                    for (int k = 0; k < myCentipedes.length; k++){
                                        
                                        // If the current centipede position is vacant, create a new centipede there
                                        if (myCentipedes[k] == null){
                                            // Copy over information from hit centipede
                                            newCentLength = myCentipedes[j].length - hitSegment - 1;
                                            newCentVert = myCentipedes[j].vertical;
                                            newCentHor = myCentipedes[j].horizontal;
                                            newCentHead = new Point(myCentipedes[j].segments[hitSegment+1].x, myCentipedes[j].segments[hitSegment+1].y);
                                            
                                            // Create a new centipede object in the vacant space
                                            myCentipedes[k] = new Centipede(newCentLength, newCentHor, newCentVert);
                                            
                                            // Copy over hit centipede's segments
                                            for (int m = 0; m < newCentLength; m++){
                                                myCentipedes[k].segments[m] = new Point(myCentipedes[j].segments[hitSegment+1+m].x, myCentipedes[j].segments[hitSegment+1+m].y);
                                            }
                                            
                                            // Only need to create one new centipede so break from the loop
                                            break;
                                        }
                                    }
                                }
                                
                                // Create mushroom
                                Point newShroom = new Point(myCentipedes[j].segments[hitSegment].x, myCentipedes[j].segments[hitSegment].y);
                                myMushrooms[newShroom.x][newShroom.y] = new Mushroom(newShroom, Settings.shroomStartHealth);
                                
                                // Adjust length of hit centipede
                                myCentipedes[j].length = hitSegment;
                                myGameSounds.centDie();
                                break;
                            }
                        }
                    }
                    
                // If the projectile hit a mushroom, decrement its life (delete if 0) and play mushroom hit sound
                }else if (overlapResult%(Settings.SHROOM*2) >= Settings.SHROOM){
                    if (myMushrooms[scaledDownPoint.x][scaledDownPoint.y].health == 1){
                        myMushrooms[scaledDownPoint.x][scaledDownPoint.y] = null;
                        score += 5;
                    }else{
                        myMushrooms[scaledDownPoint.x][scaledDownPoint.y].health--;
                        score += 1;
                    }
                    myGameSounds.shroomHit();
                    myProjectiles[i] = null;
                    
                // If the projectile did not hit anything, then move it up a pixel
                }else{
                    myProjectiles[i].y--;
                }
            }
        }
    }
    
    /**
     * Flips gameOverFlag to true, plays the game over sound and tries to add a high score
     **/
    public void gameOver(){
        gameOverFlag = true;
        myShip.firing = false;
        myGameSounds.gameOver();
        myHighScores.addHighScore(score);        
    }
}
