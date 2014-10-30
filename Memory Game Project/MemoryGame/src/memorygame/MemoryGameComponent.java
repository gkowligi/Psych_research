package memorygame;
//package edu.ucsb.cs56.projects.games.memorycard;

import java.awt.*;
import java.awt.event.*; // for ActionListener and ActionEvent

import javax.swing.*;

import java.util.ArrayList;
import java.util.Date;
import java.lang.Math;
import java.net.URL;

/**
* A Swing component for playing the Memory Card Game
@author Bryce McGaw and Jonathan Yau (with some of Phill Conrad's code as a basis)
@author Ryan Halbrook and Yun Suk Chang
@author Mathew Glodack, Christina Morris
@version CS56 Spring 2013
@see MemoryGrid
*/
public class MemoryGameComponent extends JComponent implements ActionListener
{
    private ArrayList<Long> timeFinished;
	private int nGames;
	private int currentTrial;
	private int numMoves=0; //2 flips = 1 move
	/*    ClickedImage	Time
	 * 	0     
	 * 	1
	 * 	2
	 */
	private ArrayList<ArrayList<Long>> moveHistory;
	
	//----------------------Old code-------------------------
    private JButton []        buttons;
    private ArrayList<Icon>   imgIcons          = new ArrayList<Icon>();
    
    private Icon              imgBlank;
    
    private Timer             timer; // used to get an event every 1 ms to
                                     // update the time remaining display
    private MemoryGrid grid;


    private boolean           firstImageFlipped = false;
    private int               gameCounter       = 0;
    private long              startTime         = 0; // used to calculate the
                                                     // total game time.
    private boolean           cheatEnabled      = false; // Cheat code related.
    private boolean	      isOver            = false; // Cheat code related.
    
    private JFrame	      mainFrame		= null;
    
    /** Constructor
	
	@param game an object that implements the MemoryGrid interface
	to keep track of the moves in each game, ensuring the rules are
	followed and detecting when the user has won.
    */
    public MemoryGameComponent(MemoryGrid game,int ngames) {
	super(); 
	timeFinished = new ArrayList<Long>(); 
	nGames=ngames;
	
	mainFrame=new JFrame();
	timer = new Timer(1, this);
	grid = game;
	buttons= new JButton[grid.getSize()];
	
	
	loadImageIcons(); // loads the array list of icons and sets imgBlank
	buildTiles();
	startTime = System.currentTimeMillis();
    }

    
    /**
       Callback from the timer. This is used to update the time remaining
       label and to check if the game has ended as a result of the level
       time running out.
     */
    public void actionPerformed(ActionEvent e) {

    }
    
    
    public void setMainFrame(JFrame f){
	mainFrame =f;
    }
  
  
    /**buildTiles() constructs the tiles
     */
    public void buildTiles() {
	this.removeAll();
	int gridSize = grid.getSize();
	
	//set layout to a grid of length sqrt(grid size)
	this.setLayout(new GridLayout(0,(int)Math.sqrt(gridSize)));
	buttons = new JButton[gridSize];
	for(int i=0; i<=(gridSize-1); i++) {
	    //initially all buttons are blank
	    JButton jb = new JButton(imgBlank);
	    
	    buttons[i] = jb;
	    jb.addActionListener(new ButtonListener(i));
	    
	    //get rid of annoying boxes appearing around icon next to clicked icon
	    jb.setFocusPainted(false);
	    
	    this.add(jb);
	}
	this.repaint();
	this.validate();
	startTime = new Date().getTime();
    }

    /**Class ButtonListener implents ActionLister
     *defines the actionPerformed methods for the buttons
     */
    class ButtonListener implements ActionListener {
	private int num;
	
	/**ButtonListener method
	 *@param i for the num
	 */
	public ButtonListener(int i) {
	    super();
	    this.num = i;
	}
	
        public void actionPerformed (ActionEvent event) {
	    
	    Class classs = this.getClass();
	    Icon imgBlank = new ImageIcon(
					  classs.getResource("/images/000.jpg"));
	    
	    //if 2 MemoryCards are flipped, flip back over
	    flipBack();
            //if no MemoryCards are flipped, flip one
            if (!grid.isOneFlipped()){
		if (!firstImageFlipped) {
		    startTime = (new Date().getTime());
		    timer.start();
		    firstImageFlipped = true;
//		    pauseButton.setEnabled(true);
		}
		grid.flip(num);
		JButton jb = buttons[num];
		Icon i = imgIcons.get(grid.getVal(num)-1);
		jb.setIcon(i); //set image according to val
		if(num!=1) //cheat code
		    jb.setEnabled(false); //make unclickable
                else
		    //cheat code. Needs to override the button so that button is same color as regular button.
		    cheatEnabled=true;	
	    }
	    
            //if one MemoryCard is flipped, flip other
            //then check if theyre matching
            else{
		if((num==1&&cheatEnabled))//cheat code
		    {
			cheatEnabled=false;
                        isOver=true;
			endGame();
			return;
		    }
		cheatEnabled=false;//cheat code
                grid.flip(num);
                JButton jb = buttons[num];
		
		jb.setIcon(imgIcons.get(grid.getVal(num)-1)); //set image according to val
		
                jb.setEnabled(false);
                if (grid.flippedEquals(num)){ //if they're matching keep num displayed and set flipped as false
                    gameCounter++;
                    grid.flip(num); 
		    buttons[grid.getFlipped()].setEnabled(false);
                    grid.flip(grid.getFlipped());
                    score+=30;
                    //check if game is over
                    if(gameCounter==grid.getSize()/2){
			isOver=true;
			endGame();
                    }
                } else {
		    // start the flip back timer
		    int delay = level.getFlipTime();
		    ActionListener listener = new ActionListener() {
			    public void actionPerformed(ActionEvent e) { flipBack(); }
			};
		    Timer t = new Timer(delay, listener);
		    t.setRepeats(false);
		    t.start();
		} // end of inner if else
		
            } // end of outer if else
        }
    }
    
    /**Starts a new level or restarts the current level
     *@param lvl changes the level of the game
     */
    public void newGame(int lvl) {
	gameCounter = 0;
	if (currentLevel < levels.length) {
	    currentLevel=lvl;
	    level = levels[currentLevel];
	}
	int gridSize = level.getGridSize();
	grid = new MemoryGrid(gridSize);
	buildTiles();
	if (timer != null) timer.stop();
	
	firstImageFlipped = false;
	pauseButton.setEnabled(false);
    }

    /**Resets the game
     *
     */
    public void reset() {
	pauseTime = 0;
	score=0;
	updateScore();
	updateTimeLabel(level.getSecondsToSolve() / 60, 
			level.getSecondsToSolve() % 60);	    
 	newGame(currentLevel);
	firstImageFlipped = false;
	pauseButton.setEnabled(false);
    }
    
    /**
       Ends the game and starts a new game if the user selects new game
       from a dialog menu.
    */
    public void endGame() {
    	timeFinished.add(System.currentTimeMillis()-startTime);
    	startTime=System.currentTimeMillis();
    	
    }
    
    /**
       If two cards are showing, flips them back over
    */
    public void flipBack() {
	
	if(grid.isTwoFlipped()){
	    JButton jb = buttons[grid.getFlipped()];
	    jb.setEnabled(true);
	    jb.setIcon(imgBlank);
	    grid.flip(grid.getFlipped());
	    jb = buttons[grid.getFlipped()];
	    jb.setEnabled(true);
	    jb.setIcon(imgBlank);
	    grid.flip(grid.getFlipped());
	    
	}
    }
    
    /**Loads the imageIcons
     *
     */
    public void loadImageIcons() {
	//get the current classloader (needed for getResource method.. )
	// (which is required for jws to work)
	ClassLoader classLoader = this.getClass().getClassLoader();
	//Class classs = this.getClass();
	//load Icons
	//URL url = this.getClass().getClassLoader().getResource("image/200.jpg");
	//System.out.println(url);
	//imgIcons.add(new ImageIcon(url));
	
	for (String image : images8) {
	    imgIcons.add(new ImageIcon(classLoader.getResource(image)));
	}
	for (String image : images10) {
	    imgIcons.add(new ImageIcon(classLoader.getResource(image)));
	}
	imgBlank = new ImageIcon(classLoader.getResource("images/000.jpg"));
    }
    
}
