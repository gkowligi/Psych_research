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
	private static final int FLIP_BACK_TIME=1000;


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
		startTime = 0;//dummy val. it is initialized when first image is flipped
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
		for(int i=0; i<gridSize; i++) {
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
			

			//if 2 MemoryCards are flipped, flip back over
			flipBack();
			//if no MemoryCards are flipped, flip one
			if (!grid.isOneFlipped()){
				if (!firstImageFlipped) {
					startTime = (new Date().getTime());
					timer.start();
					firstImageFlipped = true;
					//		    		pauseButton.setEnabled(true);
				}
				grid.flip(num);
				JButton jb = buttons[num];
				Icon i = imgIcons.get(num);
				jb.setIcon(i); //set image according to val

				jb.setEnabled(false); //make unclickable

			}

			//if one MemoryCard is flipped, flip other
			//then check if theyre matching
			else{


				grid.flip(num);
				JButton jb = buttons[num];

				jb.setIcon(imgIcons.get(num)); //set image according to val

				jb.setEnabled(false);
				if (grid.flippedEquals(num)){ //if they're matching keep num displayed and set flipped as false
					gameCounter++;
					grid.flip(num); 
					buttons[grid.getFlipped()].setEnabled(false);
					grid.flip(grid.getFlipped());

					//check if game is over
					if(gameCounter==grid.getSize()/2){

						endGame();
					}
				} else {
					// start the flip back timer

					ActionListener listener = new ActionListener() {
						public void actionPerformed(ActionEvent e) { flipBack(); }
					};
					Timer t = new Timer(FLIP_BACK_TIME, listener);
					t.setRepeats(false);
					t.start();
				} // end of inner if else

			} // end of outer if else
		}
	}


	public void nextLevel(int newGrid) {
		gameCounter = 0;
		if (currentTrial < nGames) {
			currentTrial++;
		}

		if(newGrid==1){
			grid = new MemoryGrid(grid.getSize());
			grid.shuffle();
		}
		buildTiles();
		if (timer != null) timer.stop();

		firstImageFlipped = false;
		//startTime=System.currentTimeMillis();
	}


	/**
       Ends the game and starts a new game if the user selects new game
       from a dialog menu.
	 */
	public void endGame() {
		timeFinished.add(System.currentTimeMillis()-startTime);
		
		nextLevel((int)(Math.random()*2));
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
		//URL url = this.getClass().getClassLoader().getResource("images/shape01.tiff");
		//System.out.println(url);
		//imgIcons.add(new ImageIcon(url));

		for(int i=0;i<grid.getSize();i++){
			String image;
			if(grid.getVal(i)<10)
				image="images/shape0"+grid.getVal(i)+".png";
			else
				image="images/shape"+grid.getVal(i)+".png";
			imgIcons.add(new ImageIcon(classLoader.getResource(image)));
		}
		
		imgBlank = new ImageIcon(classLoader.getResource("images/000.jpg"));
	}

}
