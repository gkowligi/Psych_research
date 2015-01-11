package memorygame;
//package edu.ucsb.cs56.projects.games.memorycard;

import java.awt.*;
import java.awt.event.*; // for ActionListener and ActionEvent

import javax.swing.*;

import java.util.ArrayList;
import java.util.Date;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;
import java.net.URL;
import java.nio.file.Files;

/**
 * A Swing component for playing the Memory Card Game
@author Bryce McGaw and Jonathan Yau (with some of Phill Conrad's code as a basis)
@author Ryan Halbrook and Yun Suk Chang
@author Mathew Glodack, Christina Morris
@version CS56 Spring 2013
@see MemoryGrid
 */
public class MemoryGameComponent extends JComponent implements ActionListener,KeyListener
{
	private static final int FLIP_BACK_TIME=1000;


	private ArrayList<Long> timeFinished;
	private int nGames;
	private int currentTrial;
	private int currentNumMoves; //2 flips = 1 move
	private ArrayList<Integer> numMoves; 
	/*    ClickedImage	Time
	 * 	0     
	 * 	1
	 * 	2
	 */
	private ArrayList<ArrayList<long[]>> moveHistory;
	private ArrayList<long[]> currentMoves;
	private int[] button2imageMap;
	//----------------------Old code-------------------------
	private JButton []        buttons;
	private ArrayList<ImageIcon>   imgIcons          = new ArrayList<ImageIcon>();

	private Icon              imgBlank;

	private Timer             timer; // used to get an event every 1 ms to
	// update the time remaining display
	private MemoryGrid grid;


	private boolean           firstImageFlipped = false;
	private int               gameCounter       = 0;
	private long              startTime         = 0; // used to calculate the
	// total game time.


	private JFrame	      mainFrame		= null;
	private Timer		flipBackTimer=null;
	/** Constructor

	@param game an object that implements the MemoryGrid interface
	to keep track of the moves in each game, ensuring the rules are
	followed and detecting when the user has won.
	 */
	public MemoryGameComponent(MemoryGrid game,int ngames) {
		super(); 
		timeFinished = new ArrayList<Long>(); 
		nGames=ngames;
		moveHistory=new ArrayList<ArrayList<long[]>>();
		currentMoves=new ArrayList<long[]>();
		currentTrial=0;
		numMoves=new ArrayList<Integer>();
		currentNumMoves=0;
		
		
		mainFrame=new JFrame();
		timer = new Timer(0, this);
		grid = game;
		buttons= new JButton[grid.getSize()];
		button2imageMap=new int[grid.getSize()];
		grid.shuffle();

		loadImageIcons(); // loads the array list of icons and sets imgBlank
		buildTiles();
		startTime = 0;//dummy val. it is initialized when first image is flipped
		mainFrame.addKeyListener(this);
		mainFrame.setFocusable(true);
		mainFrame.setFocusTraversalKeysEnabled(false);
		//mainFrame.addKeyListener(this);
		//timer.start();
	}


	/**
       Callback from the timer. This is used to update the time remaining
       label and to check if the game has ended as a result of the level
       time running out.
	 */
	public void actionPerformed(ActionEvent e) {
			//buttons[0].doClick();
			//System.out.println(System.currentTimeMillis());
		
	}


	public void setMainFrame(JFrame f){
		mainFrame =f;
		mainFrame.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		        for(int i=0;i<imgIcons.size();i++){
		        	imgIcons.set(i, new ImageIcon(imgIcons.get(i).getImage().getScaledInstance(buttons[0].getWidth(), buttons[0].getHeight(), java.awt.Image.SCALE_SMOOTH)));
		        }          
		    }
		});
		mainFrame.addKeyListener(this);
		mainFrame.setFocusable(true);
		mainFrame.setFocusTraversalKeysEnabled(false);
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
			if (!firstImageFlipped) {
					startTime = System.currentTimeMillis();
					//timer.start();
					firstImageFlipped = true;
					//		    		pauseButton.setEnabled(true);
			}
			currentMoves.add(new long[]{num,System.currentTimeMillis()-startTime});
			
			
			//if 2 MemoryCards are flipped, flip back over

			if(grid.isTwoFlipped()){
				flipBack();
				flipBackTimer.stop();
			}
			
			
			
			//if no MemoryCards are flipped, flip one
			if (!grid.isOneFlipped()){
				
				grid.flip(num);
				JButton jb = buttons[num];
				Icon i = imgIcons.get(num);
				jb.setIcon(i); //set image according to val

				jb.setEnabled(false); //make unclickable

			}

			//if one MemoryCard is flipped, flip other
			//then check if theyre matching
			else{
				currentNumMoves++;
				int a=grid.getFlipped().get(0);
				grid.flip(num);
				JButton jb = buttons[num];

				jb.setIcon(imgIcons.get(num)); //set image according to val

				jb.setEnabled(false);

				if (grid.flippedEquals(num,a)){ //if they're matching keep num displayed and set flipped as false
					gameCounter++;

					buttons[num].setEnabled(false);
					grid.flushCurrentFlipped();

					//check if game is over
					if(gameCounter==grid.getSize()/2){

						endGame();
					}
				} else {
					// start the flip back timer

					ActionListener listener = new ActionListener() {
						public void actionPerformed(ActionEvent e) { flipBack(); }
					};
					flipBackTimer = new Timer(FLIP_BACK_TIME, listener);
					flipBackTimer.setRepeats(false);
					flipBackTimer.start();
				} // end of inner if else

			} // end of outer if else
			
		}
	}


	public void nextLevel(int newGrid) {
		gameCounter = 0;
		
		
		

		if(newGrid==1){
			grid = new MemoryGrid(grid.getSize(),grid.getNumImage());
			grid.shuffle();
			imgIcons= new ArrayList<ImageIcon>();
			loadImageIcons();
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
		moveHistory.add(currentMoves);
		numMoves.add(currentNumMoves);
		currentTrial++;
		if(currentTrial<nGames)
			nextLevel((int)(Math.random()*2));
		else{
			writeData("MemoryGameData");
			displayEndScreen();
		}
			
	}
	public void displayEndScreen(){
		mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
		mainFrame=new JFrame("Thank You");
		mainFrame.setSize(500,500);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
		mainFrame.add(new JLabel(new ImageIcon(this.getClass().getClassLoader().getResource("images/EndMessage.png"))));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		mainFrame.setLocation((int)(screenSize.getWidth()/2 - mainFrame.getSize().getWidth()/2), (int)(screenSize.getHeight()/2 - mainFrame.getSize().getHeight()/2));
		mainFrame.setVisible(true);
		
	}
	/**
       If two cards are showing, flips them back over
	 */
	public void flipBack() {

		if(grid.isTwoFlipped()){
			JButton jb = buttons[grid.getFlipped().get(0)];
			jb.setEnabled(true);
			jb.setIcon(imgBlank);
			grid.flip(grid.getFlipped().get(0));
			jb = buttons[grid.getFlipped().get(0)];
			jb.setEnabled(true);
			jb.setIcon(imgBlank);
			grid.flip(grid.getFlipped().get(0));

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
			imgIcons.add(new ImageIcon(classLoader.getResource(image)));//.getImage().getScaledInstance(buttons[0].getWidth(), buttons[0].getHeight(), java.awt.Image.SCALE_SMOOTH)));
			button2imageMap[i]=grid.getVal(i);
		}

		imgBlank = new ImageIcon(classLoader.getResource("images/000.jpg"));
	}
	public void writeData(String filename){
		try {

			File dir = new File("Data/");
			if (!Files.exists(dir.toPath())) {
				dir.mkdirs();
			}
			int num=1;
			String name=filename+num;
			File file = new File("Data/"+name+".txt");
			while(Files.exists(file.toPath())){
				num++;
				name=filename+num;
				file = new File("Data/"+name+".txt");
			}
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i=0;i<moveHistory.size();i++){
				bw.write("===================================");
				bw.newLine();
				bw.write("Trial#: "+i+"\tNumber of Moves: "+numMoves.get(i));
				bw.newLine();
				bw.write("Solve Time: "+timeFinished.get(i)+" ms");
				bw.newLine();
				bw.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				bw.newLine();
				bw.write("Button to Image Number Map");
				bw.newLine();
				int col=(int)Math.sqrt(grid.getSize());
				int row=grid.getSize()/col;
				String divLine="";
				for(int l=0;l<col;l++)
					divLine+="------";
				for(int j=0;j<row;j++){
					bw.write(divLine);
					bw.newLine();
					for(int k=0;k<col;k++){
						bw.write("| "+button2imageMap[j*col+k]+" ");
					}
					bw.write("|");
					bw.newLine();
					
				}
				bw.write(divLine);
				bw.newLine();
				bw.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				bw.newLine();
				bw.write("Button Number\tTime Pressed (ms)");
				bw.newLine();
				ArrayList<long[]> temp=moveHistory.get(i);
				for(int j=0;j<temp.size();j++){
					bw.write(temp.get(j)[0]+"\t"+temp.get(j)[1]);
					bw.newLine();
				}
				bw.write("===================================");
				bw.newLine();
			}
			
			
			bw.close();
 

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public void keyPressed(KeyEvent e) {}

	public void keyReleased(KeyEvent e) {
        //System.out.println(KeyEvent.getKeyText(e.getKeyCode()));
		
        if(KeyEvent.getKeyText(e.getKeyCode()).equals("C")||KeyEvent.getKeyText(e.getKeyCode()).equals("c")){
        	System.out.println("Cheat Code Activated. To The Next Level!");
			endGame();
        }
	}

	public void keyTyped(KeyEvent e) {}

}
