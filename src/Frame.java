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
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

/**
 * Creates the JFrame and its menu
 * 
 * @author  James Wilkinson <jhwilkin@purdue.edu>
 * @version 1.6
 * @since   2011-08-08
 **/
public class Frame extends JFrame implements MenuListener, ActionListener{
    /** Used to add the Canvas to the frame **/
    private GameCanvas myGameCanvas;
    
    /** Used for pausing, unpausing and starting new games.  Also for adjusting difficulty  **/
    private GameLogic myGameLogic;
        
    /** Used to display high scores **/
    private HighScores myHighScores;
    
    private JMenuBar bar;
        
    /** File menu **/
    private JMenu file;
        
    /** Synchronously starts a new game **/
    private JMenuItem newGame;
        
    /** Pauses the game **/
    public JMenuItem pauseGame;
        
    /** Saves high scores and exits the game **/
    private JMenuItem exitGame;
        
    /** Options menu **/
    private JMenu options;
        
    /** Set game speed to easy **/
    private JRadioButtonMenuItem easyDifficulty;
        
    /** Set game speed to medium **/
    private JRadioButtonMenuItem mediumDifficulty;
        
    /** Set game speed to hard **/
    private JRadioButtonMenuItem hardDifficulty;
        
    /** Toggles the super laser **/
    private JMenuItem superLaser;
        
    /** Display high scores **/
    private JMenuItem highScores;
    
    /**
     * @param inGameLogic  Used for pausing, unpausing and starting new games.  Also for adjusting difficulty
     * @param inGameCanvas Used to add the Canvas to the frame
     * @param inHighScores Used to display high scores
     **/
    public Frame(GameLogic inGameLogic, GameCanvas inGameCanvas, HighScores inHighScores){
        myGameCanvas = inGameCanvas;
        myGameLogic = inGameLogic;
        myHighScores = inHighScores;
        
        // Setup the file menu
        newGame = new JMenuItem("New Game");
        newGame.addActionListener(this);
        pauseGame = new JMenuItem("Pause Game");
        pauseGame.addActionListener(this);
        exitGame = new JMenuItem("Exit Game");
        exitGame.addActionListener(this);        
        file = new JMenu("File");
        file.addMenuListener(this);
        file.add(newGame);
        file.add(pauseGame);
        file.add(exitGame);
        
        // Setup the options menu
        easyDifficulty = new JRadioButtonMenuItem("Easy");
        easyDifficulty.addActionListener(this);
        easyDifficulty.setSelected(false);
        mediumDifficulty = new JRadioButtonMenuItem("Medium");
        mediumDifficulty.addActionListener(this);
        mediumDifficulty.setSelected(true);
        hardDifficulty = new JRadioButtonMenuItem("Hard");
        hardDifficulty.addActionListener(this);
        hardDifficulty.setSelected(false);
        superLaser = new JMenuItem("Super Laser is OFF");
        superLaser.addActionListener(this);
        options = new JMenu("Options");
        options.addMenuListener(this);
        options.add(easyDifficulty);
        options.add(mediumDifficulty);
        options.add(hardDifficulty);
        options.addSeparator();
        options.add(superLaser);
        
        // Setup the high scores menu
        highScores = new JMenuItem("High Scores");
        highScores.addActionListener(this);
        
        // Setup the menu bar
        bar = new JMenuBar();
        bar.add(file);
        bar.add(options);
        bar.add(highScores);
        
        // Setup the frame using GridBagLayout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(bar);
        setLocation(100,100);
        setResizable(false);
        
        // Add the game canvas
        add(myGameCanvas.c);
        
        // Shrink the frame to fit its contents
        pack();
        
        // Make the frame visible
        setVisible(true);
    }
    
    /**
     * Unused
     * 
     * @param e MenuEvent that occured
     **/
    public void menuCanceled(MenuEvent e){
    }
    
    /**
     * Unused
     * 
     * @param e MenuEvent that occured
     **/
    public void menuDeselected(MenuEvent e){
    }
    
    /**
     * If the user selected high scores, pause the game and display the high scores table.  Unpause when done.
     * 
     * @param e MenuEvent that occured
     **/
    public void menuSelected(MenuEvent e){
        Object ob = e.getSource();
        
    }
    
    /**
     * Handles the selection of any menu item
     * 
     * @param e ActionEvent that occured
     **/
    public void actionPerformed(ActionEvent e){
        Object ob = e.getSource();
        
        // If new game selected, send a synchronous reset to the GameLogic
        if (ob == newGame){
            myGameLogic.syncReset = true;
        }else if (ob == highScores){
            highScores.setSelected(false);
            myGameLogic.paused = true;
            pauseGame.setText("Unpause Game");
            myHighScores.showHighScores("", 0);
            myGameLogic.paused = false;
            pauseGame.setText("Pause Game");
            
        // If pause game selected, pause/unpause the game and toggle the menu item's display
        }else if (ob == pauseGame){
            if (myGameLogic.paused){
                myGameLogic.paused = false;
                pauseGame.setText("Pause Game");
            }else{
                myGameLogic.paused = true;
                pauseGame.setText("Unpause Game");
            }
        
        // If exit game selected, save the high scores and then exit
        }else if (ob == exitGame){
            myHighScores.writeScores();
            System.exit(0);
            
        // If super laser selected, toggle the super laser and the menu item's display
        }else if (ob == superLaser){
            if (Settings.superLaser){
                superLaser.setText("Super Laser is OFF");
                Settings.superLaser = false;
            }else{
                superLaser.setText("Super Laser is ON");
                Settings.superLaser = true;
            }
            
        // If easy difficulty selected, make sure its radio button is selected, turn off the other difficulties' radio buttons,
        // set the game difficulty and adjust the centipede delay accordingly.
        }else if(ob == easyDifficulty){
            easyDifficulty.setSelected(true);
            mediumDifficulty.setSelected(false);
            hardDifficulty.setSelected(false);
            myGameLogic.difficulty = Settings.centDelayEasy;
            Settings.centDelay = (int) Math.round(myGameLogic.difficulty / Math.pow(Settings.levelFactor, myGameLogic.level));
            
        // If medium difficulty selected, make sure its radio button is selected, turn off the other difficulties' radio buttons,
        // set the game difficulty and adjust the centipede delay accordingly.
        }else if(ob == mediumDifficulty){
            easyDifficulty.setSelected(false);
            mediumDifficulty.setSelected(true);
            hardDifficulty.setSelected(false);
            myGameLogic.difficulty = Settings.centDelayMedium;
            Settings.centDelay = (int) Math.round(myGameLogic.difficulty / Math.pow(Settings.levelFactor, myGameLogic.level));
            
        // If hard difficulty selected, make sure its radio button is selected, turn off the other difficulties' radio buttons,
        // set the game difficulty and adjust the centipede delay accordingly.
        }else if(ob == hardDifficulty){        
            easyDifficulty.setSelected(false);
            mediumDifficulty.setSelected(false);
            hardDifficulty.setSelected(true);
            myGameLogic.difficulty = Settings.centDelayHard;
            Settings.centDelay = (int) Math.round(myGameLogic.difficulty / Math.pow(Settings.levelFactor, myGameLogic.level));
        }
    }
}
