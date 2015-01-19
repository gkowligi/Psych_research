package memorygame;
//package edu.ucsb.cs56.projects.games.memorycard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
/**
 * makes the grid (ArrayList of MemoryCards) for the game

 * @author Mathew Glodack, Christina Morris
 * @version CS56, S13, 5/8/13
 */

public class MemoryGrid{
	
	ArrayList<MemoryCard> memGrid = new ArrayList<MemoryCard>();
	int size;
	boolean isOver=false;
	ArrayList<Integer> currentFlipped;
	int numImage;
	/** MemoryGrid constructor that takes in a parameter
	 * @param gridSize for the size the of the grid
	 */
	public MemoryGrid(int gridSize){
		if (gridSize % 2 == 0) 
			size = gridSize;
		else{
			System.out.println("Grid Size Error: Grid size has to be even");
			System.exit(1);
		}
		numImage=gridSize;
		currentFlipped=new ArrayList<Integer>();
		for(int i=1; i<((size/2)+1); i++){
			MemoryCard temp  = new MemoryCard(i);
			MemoryCard temp2 = new MemoryCard(i);
			memGrid.add(temp);
			memGrid.add(temp2);
		}   

	}

	public MemoryGrid(int gridSize, int nImage){
		if (gridSize % 2 == 0) 
			size = gridSize;
		else{
			System.out.println("Grid Size Error: Grid size has to be even");
			System.exit(1);
		}
		numImage=nImage;
		currentFlipped=new ArrayList<Integer>();
		ArrayList<Integer> a = new ArrayList<Integer>();
		for(int i=1;i<=nImage;i++){
			a.add(i);
		}
		for(int i=0; i<size/2; i++){
			int r=(int)(Math.random()*a.size());
			MemoryCard temp  = new MemoryCard(a.get(r));
			MemoryCard temp2 = new MemoryCard(a.get(r));
			memGrid.add(temp);
			memGrid.add(temp2);
			a.remove(r);
		}   

	}

	public int getNumImage() {
		return numImage;
	}

	public void setNumImage(int numImage) {
		this.numImage = numImage;
	}

	/**Method getSize()
	 * @return the size of the grid (total number of MemoryCard objects)
	 */
	public int getSize(){
		return this.size;
	}
	public void shuffle(){
		Collections.shuffle(memGrid);     //shuffles the in order ArrayList
	}
	/**Method contains2
	 * Checks if array contains 2 of same value
	 * @param temp check to see if temp memory card is in the memorycard arraylist twice
	 */
	public boolean contains2(MemoryCard temp){
		int count=0;
		for(MemoryCard comp: memGrid){
			if(comp.Equals(temp))    
				count++;
		}
		return (count==2);
	}

	/**Method isOneFlipped()
	 * Checks array if one object is flipped
	 * @return boolean true if one card is flipped, false otherwise
	 */
	public boolean isOneFlipped(){
		return currentFlipped.size()==1;
	}

	/**Method is TwoFlipped()
	 * Checks array to see if 2 objects are flipped
	 * @return boolean true if two cards are flipped, false otherwise
	 */
	public boolean isTwoFlipped(){
		return currentFlipped.size()==2;
	}

	/**Method flippedEquals(int i)
	 * Checks if two MemoryCards are equal
	 * @param i the location in the arraylist of the memorycard that is being compared
	 * @return boolean true if the indexed card is equal to the memory card that is being compared
	 */
	public boolean flippedEquals(int i, int j){
		return memGrid.get(i).Equals(memGrid.get(j));
	}

	/**Method getFlipped()
	 * @return retval the position of the flipped MemoryCard
	 */
	public ArrayList<Integer> getFlipped(){
		/*int retVal=-1, count=-1;
		for(MemoryCard comp: memGrid){
			count++;
			if(comp.isFlipped()==true)
				retVal=count;
		}
		return retVal;*/
		return currentFlipped;
	}

	/** Method getFlipped(int i)
	 * @return retval the position of the flipped MemoryCard that is not index i
	 */
	/*public int getFlipped(int i){
		int retVal=-1, count=-1;
		for(MemoryCard comp: memGrid){
			count++;
			if( (comp.isFlipped()==true) && (memGrid.get(i)!=comp))
				retVal=count;
		}
		return retVal;
		
	}*/

	/**Method isOver()
	 * @return boolean true if the came is over, false if the game is not over
	 */
	public boolean isOver(){
		return isOver;
	}

	/**Methd getVal(int i)
	 * @return value of MemoryCard[i]
	 */
	public int getVal(int i){
		
		return memGrid.get(i).getVal();
	}

	/**Method flip(int i)
	 * flips MemoryCard[i]
	 * @param i location in memory grid that should be flipped
	 */
	public void flip(int i){
		MemoryCard temp = memGrid.get(i);
		
		if(temp.isFlipped()&&currentFlipped.size()>0)
			currentFlipped.remove((Integer)i);
		else
			currentFlipped.add(i);
		temp.flip();
	}
	public void flushCurrentFlipped(){
		while(!currentFlipped.isEmpty())
			currentFlipped.remove(currentFlipped.size()-1);
	}
	public void flipAllBack(){
		for(int i=0;i<memGrid.size();++i){
			if(memGrid.get(i).isFlipped())
				memGrid.get(i).flip();
		}
	}
}
