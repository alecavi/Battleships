package player;

import java.util.InputMismatchException;
import java.util.Random;

import main.*;
import types.Coordinate;
import types.Directions;
import types.ShotResults;

/**
 * An automated player that can play its turn without any user input
 * @author Alessandro Cavicchioli
 * @version 1.0
 */
public class CPUPlayer extends AbstractPlayer
{
	private static final long serialVersionUID = 1L;
	
	private Coordinate lastHitPos;
	private Directions lastHitDir;
	
	private Random random = new Random();
	
	/**
	 * Constructs a new player object with the specified player name and the specified board size.
	 * Note that the object is still not ready for use, and will require the ships to be placed.
	 * @param playerName the name of the player.
	 * @param boardSize the size of the board.
	 */
	public CPUPlayer(String playerName, int boardSize)
	{
		super(playerName, boardSize);
		lastHitPos = null;
		lastHitDir = null;
	}

	/**
	 * {@inheritDoc}
	 * Every ship will be placed in a random location.
	 */
	@Override
	public void placeShips(int[] lengths)
	{
		System.out.println("\n" + getPlayerName() + " is positioning its ships.");
		
		int size = getBoard().getGridSize();
		Directions[] directionsValues = Directions.values();
		
		int attemptsCounter = 0;	//Let's avoid infinite loops on impossible inputs
		
		for (int length : lengths)
		{
			Coordinate position = new Coordinate();
			boolean validShip = false;
			while (!validShip)
			{
				if (attemptsCounter >= (size * size)) 
					throw new InputMismatchException("Couldn't place all ships.");
				attemptsCounter++;
				
				// Select randomly a position and direction and checks if the ship can be placed there
				position.set(random.nextInt(size), random.nextInt(size));
				Directions direction = directionsValues[random.nextInt(Directions.values().length)];
				
				validShip = getBoard().placeShip(position, direction, length);
			}	
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 * This method return any position used by a ship in the box (3x3) outer a given position.
	 */
	private Coordinate findNearShip(Coordinate c) 
	{
		int size = getBoard().getGridSize();

		//Top left cell of the bounding box of the pos
		int boundTLX = Math.max(0, c.getX() - 1);
		int boundTLY = Math.max(0, c.getY() - 1);
		
		//Bottom right cell of the bounding box of the ship
		int boundBRX = Math.min(size - 1, c.getX() + 1);
		int boundBRY = Math.min(size - 1, c.getY() + 1);
		
		// Search for any ship in the 3x3 box outer the given position, excluding the given position itself.
		Coordinate pos = new Coordinate();
		for (int x = boundTLX; x <= boundBRX; x++)
			for (int y = boundTLY; y <= boundBRY; y++)
				if (!(c.getX() == x && c.getY() == y))
				{
					pos.set(x, y);
					if (getBoard().getResultAt(pos) == ShotResults.HIT || getBoard().getResultAt(pos) == ShotResults.SINK)
						return pos;
				}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * This method will determine a new coordinate to shoot using some AI.
	 */
	public Coordinate getShot()
	{
		System.out.println("\nIt's " + getPlayerName() +"'s turn.");

		System.out.println("Thinking...");

		int size = getBoard().getGridSize();

		Coordinate newShot = null;

		if (lastHitPos == null)
		{
			int[][]	aiMap = new int[size][size];	// Map to be used for AI calculations
			
			// Initialise the AI map using available Shot results
			// Set each cell of the map to:
			// 0 - if the cell contains a ship
			// 1 - if the cell is a either a miss or is next to a cell with a ship (which can't contain a ship either)
			// 2 - otherwise (it's a cell which can contain a ship)
			
			Coordinate pos = new Coordinate();		
		    for (int x = 0; x < size; x++)			
		    	for (int y = 0; y < size; y++)
		    	{
		    		pos.set(x, y);
		    		ShotResults result = getBoard().getResultAt(pos);
		    		
		    		if (result == ShotResults.HIT || result == ShotResults.SINK)
						aiMap[x][y] = 0;
		    		else if (result == ShotResults.MISS)
						aiMap[x][y] = 1;
					else
					{
						if (findNearShip(pos) != null)
							aiMap[x][y] = 1;
						else
							aiMap[x][y] = 2;
					}
		    	}
	
			// For each cell calculates the probability (density) to have a ship in it
			// considering all possible lengths except length = 1 (which is not important
			// since all free cell can contain a 1-length ship).
			// Every time a cell can contain a ship its probability is increased by 1
			int maxDensity = 2;
			for (int length = Game.MAX_SHIP_LENGTH; length > 1; length--) {
	
				// Calculate probability for possible horizontal ships...
				for (int x = 0; x <= size - length; x++) 
					for (int y = 0; y < size; y++) {
						int i = 0;
						while (i < length && aiMap[x + i][y] > 1)
							i++;
						if (i == length)  
							for (int x1 = x; x1 < x + length; x1++) 
							{
								aiMap[x1][y]++;
								maxDensity = Math.max(maxDensity, aiMap[x1][y]);
							}
					}
	
				// and calculate probability for possible vertical ships
				for (int x = 0; x < size; x++) 
					for (int y = 0; y <= size - length; y++) {
						int i = 0;
						while (i < length && aiMap[x][y + i] > 1)
							i++;
						if (i == length) 
							for (int y1 = y; y1 < y + length; y1++) {
								aiMap[x][y1]++;
								maxDensity = Math.max(maxDensity, aiMap[x][y1]);
							}
					}
			}
	
			// maxDensity is the highest probability calculated until now.
            // Counts the cells with a value equal to maxDensity, and selects one randomly
			int maxDensityCount = 0;
			for (int x = 0; x < size; x++) 
				for (int y = 0; y < size; y++) 
					maxDensityCount += (aiMap[x][y] == maxDensity ? 1 : 0);
			
			int randomCell = random.nextInt(maxDensityCount) + 1;
			
			int x = 0;
			int y = 0;
			while (x < size && randomCell > 0) {
				y = 0;
				while (y < size && randomCell > 0) {
					randomCell -= (aiMap[x][y] == maxDensity ? 1 : 0); 
					y++;
				}
				x++;
			}
			
			// The random cell is selected as new candidate shot
			newShot = new Coordinate(x - 1, y - 1);
		}
		else if (lastHitDir == null) {
			// If an enemy ship has just been hit for the first time its direction has not been found yet (lastHitDir == null), so it gets next new shot...
			Directions[] directionsValues = Directions.values();
	
			int i = random.nextInt(directionsValues.length);	// ...cycling through the available directions (selecting first randomly). 
																// Such a shot has to exist otherwise the ship would have been already sunk (and no hit directions would have been set).
			Coordinate searchShot = new Coordinate();
			while (newShot == null) {
				// So it starts searching from the selected direction until the first new candidate shot is found (it has to be inside the board and on a cell that has not been hit yet)
				Directions searchDir = directionsValues[i];																			
				searchShot.set(lastHitPos.getX() + searchDir.getX(), lastHitPos.getY() + searchDir.getY());							
				if (!getBoard().isOutside(searchShot.getX(), searchShot.getY()) && (getBoard().getResultAt(searchShot) == null)) 	
					newShot = searchShot;																							
				else i = (i + 1) % directionsValues.length;																						
			}
		}
		else
		{
			// If the enemy ship has been hit in at least two positions then its direction has already been found (lastHitDir != null)
			// A next shot position has to exists (otherwise the ship would have been sunk already), so it selects the first next cell in the specified direction after lastHitPos...
			Coordinate searchShot = new Coordinate(lastHitPos.getX() + lastHitDir.getX(), lastHitPos.getY() + lastHitDir.getY());
			
			// ...and checks if the direction has to be reversed in case of...
			boolean reverseSearch = false;																		
			if (getBoard().isOutside(searchShot.getX(), searchShot.getY())) reverseSearch = true;		// ...the candidate shot is outside the board
			else if (getBoard().getResultAt(searchShot) != null) reverseSearch = true;				// ...the candidate shot has already been taken.
			
			
			if (!reverseSearch)
	    		newShot = searchShot;
	    	else
	    	{	
	    		// In that case it has to search in the opposite direction... 
	    		lastHitDir = Directions.opposite(lastHitDir);													
	    		
	    		// ...going back to the opposite side of the ship and selecting the first next free cell as new shot.
	    		searchShot.set(lastHitPos.getX() + lastHitDir.getX(), lastHitPos.getY() + lastHitDir.getY());
	    		while (getBoard().getResultAt(searchShot) != null)												
	    		{
	    			lastHitPos = searchShot;
	    			searchShot.set(lastHitPos.getX() + lastHitDir.getX(), lastHitPos.getY() + lastHitDir.getY());
	    		}
	    		newShot = searchShot;
	    	}
	    }

	System.out.println("Shooting in " + (char)('A' + newShot.getX()) + newShot.getY());
		
	return newShot;
	}

	/**
	 * {@inheritDoc}
	 * It records the last hit position and last hit direction in order to make the AI select the proper shot on next turn.
	 */
	@Override
	public void recordShot(Coordinate c, ShotResults result)
	{
		super.recordShot(c, result);
		
		// If the result is a hit it's recorded, and in case it's not the first hit of the ship (so lastHitPos was already set), the sinking direction is calculated
		if (result == ShotResults.HIT)
		{
			if (lastHitPos != null)
				lastHitDir = Directions.set(c.getX() - lastHitPos.getX(), c.getY() - lastHitPos.getY());
			lastHitPos = c;
		}
		// If the result is a sunk, it resets lastHitPos and lastHitDir to start searching a new ship in the next turn
		if (result == ShotResults.SINK)
		{
			lastHitPos = null;
			lastHitDir = null;
		}
	}
}





























