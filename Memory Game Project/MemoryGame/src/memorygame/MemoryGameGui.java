package memorygame;
//package edu.ucsb.cs56.projects.games.memorycard;
import java.awt.GridLayout;
import javax.swing.JComponent;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.*;
import java.awt.ComponentOrientation;
import java.io.*;
/**
*
* @author Bryce McGaw and Jonathan Yau
* @author Ryan Halbrook and Yun Suk Chang
* @author Mathew Glodack, Christina Morris
* @version CS56 Spring 2013
* Edited Professor Phill Conrad's code from Lab06
*/
public class MemoryGameGui {
	static final int NUMBER_OF_GAMES=10;
    static final int WINDOW_SIZE = 500;

    static JFrame frame = new JFrame("Memory Card Game");
    static MemoryGrid grid = new MemoryGrid(30);
    static MemoryGameComponent mgc = new MemoryGameComponent(grid,NUMBER_OF_GAMES);
    
    /** main method to open JFrame 
     *
     */
    
    public static void main (String[] args) {

	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	
	frame.getContentPane().add(mgc);
	
	mgc.setMainFrame(frame);
	// to make sure that grids go left to right
	frame.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	frame.setSize(WINDOW_SIZE, WINDOW_SIZE);
	
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	frame.setLocation((int)(screenSize.getWidth()/2 - frame.getSize().getWidth()/2), (int)(screenSize.getHeight()/2 - frame.getSize().getHeight()/2));
	frame.setVisible(true);

    }

 }
