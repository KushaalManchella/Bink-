/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Omar
 */
import java.io.*;
import javax.swing.*;

/**
 * Displays and updates high scores
 * 
 * @author  James Wilkinson <jhwilkin@purdue.edu>
 * @version 1.6
 * @since   2011-08-08
 **/
class HighScores{
    /** Used to display high scores and name prompt on the main frame **/
    public Frame myFrame;
    
    private String names[];    
    private int scores[];
    
    public HighScores(){        
        File hsFile;
        BufferedReader inStream;
        String inString = "";
        int nameStart;
        int count = 0;
        
        names = new String[Settings.numScores];
        scores = new int[Settings.numScores];
        
        // Test for existence of file
        hsFile = new File(Settings.highScoresFileName);
        if (!hsFile.exists()){
            // Try to create a new high scores file
            try{
                hsFile.createNewFile();
            }catch(Exception e){
                System.out.println("Error creating file " + Settings.highScoresFileName);
                System.out.println(e);
                return;
            }
            for (int i = 0; i < Settings.numScores; i++){
                scores[i] = 0;
                names[i] = "nobody";
            }
            
        }else{
            // Try to open high scores file
            try{
                inStream = new BufferedReader(new FileReader(Settings.highScoresFileName));
            }catch(Exception e){
                System.out.println("Error reading file " + Settings.highScoresFileName);
                System.out.println(e);
                return;
            }
            
            // Read high scores into names and scores arrays
            try{
                inString = inStream.readLine();
            }catch(Exception e){}
            while ((inString != null) && (count < Settings.numScores)){
                nameStart = inString.indexOf('\t');
                scores[count] = Integer.valueOf(inString.substring(0,nameStart));
                names[count] = inString.substring(nameStart+1, inString.length());
                try{
                    inString = inStream.readLine();
                }catch(Exception e){}
                count++;
            }
            
            // Close high scores file
            try{
                inStream.close();
            }catch(Exception e){
                System.out.println("Error closing file " + Settings.highScoresFileName);
                System.out.println(e);
            }
        }
    }
    
    /**
     * Display high scores
     * 
     * @param name  Name of player to highlight
     * @param score Score of player to highlight
     **/
    public void showHighScores(String name, int score){
        String highScoresString = "";
        
        for (int i = 0; i < names.length; i++){
            // Make provided high score stand out
            if ((names[i].equals(name)) && 
                 (score == scores[i])){
                highScoresString += ">>>" + names[i] + " - " + scores[i] + "<<<\n";
            }else{
                highScoresString += names[i] + " - " + scores[i] + "\n";
            }
        }
        
        JOptionPane.showMessageDialog(myFrame,
                                      highScoresString,
                                      "High Scores",
                                      JOptionPane.PLAIN_MESSAGE);
    }
    
    /**
     * Add score to high scores if greater than lowest high score
     * 
     * @param score Score to try to add
     **/
    public void addHighScore(int score){
        String name = "";
        
        // If new score > lowest high score, add
        if (score > scores[scores.length-1]){
            name = JOptionPane.showInputDialog(myFrame, "New high score!  Enter your name:");
            if (name != null){
                scores[scores.length-1] = score;
                names[scores.length-1] = name;
                sortScores();
                showHighScores(name, score);
            }
        }
    }
    
    /**
     * Sort bottom score up to correct position in high scores table
     **/
    public void sortScores(){
        String tempName;
        int tempScore;
        
        // Just need one round of bubble sort
        for (int i = scores.length - 1; i > 0; i--){
            if (scores[i] > scores[i-1]){
                tempName = names[i];
                tempScore = scores[i];
                names[i] = names[i-1];
                scores[i] = scores[i-1];
                names[i-1] = tempName;
                scores[i-1] = tempScore;
            }            
        }
    }
    
    /**
     * Write scores to high scores file
     **/
    public void writeScores(){
        File hsFile;
        BufferedWriter outStream;
        
        // Test for existence of file
        hsFile = new File(Settings.highScoresFileName);
        if (!hsFile.exists()){
            System.out.println(Settings.highScoresFileName + " does not exist.");
            return;
        }
        
        // Try to open output file
        try{
            outStream = new BufferedWriter(new FileWriter(Settings.highScoresFileName));
        }catch(Exception e){
            System.out.println("Error reading file " + Settings.highScoresFileName);
            System.out.println(e);
            return;
        }
        
        // Write output string to the output file
        try{
            for(int i = 0; i < Settings.numScores; i++){
                outStream.write(scores[i] + "\t" + names[i] + "\n");
            }
        }catch(Exception e){}
        
        // Close out file
        try{
            outStream.close();
        }catch(Exception e){
            System.out.println("Error closing file " + Settings.highScoresFileName);
            System.out.println(e);
        }
    }
}
