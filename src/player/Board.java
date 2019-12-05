package player;

import java.io.Serializable;

import types.Coordinate;
import types.Directions;
import types.Ship;
import types.ShotResults;

/**
 * A single player's board to play the game on
 * @author Alessandro
 * @version 1.0
 */
public class Board
implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int gridSize;
	private Ship[][] shipGrid;
	private ShotResults[][]	shotGrid;
	private ShotResults[][]	receivedShotGrid;
	private int	shipCount;
	
	/**
	 * Constructs a board with grids of the specified size.
	 * @param boardSize the size of the grids.
	 */
	public Board(int gridSize)
	{	
		this.gridSize = gridSize;
		shipGrid = new Ship[gridSize][gridSize];
		shotGrid = new ShotResults[gridSize][gridSize];
		receivedShotGrid = new ShotResults[gridSize][gridSize];
	}
	
	/**
	 * Returns whether there is a ship at the specified coordinate.
	 * @param c the coordinate to check.
	 * @return {@code true} if there is a ship, {@code false} otherwise.
	 */
	public boolean isShipAt(Coordinate c)
	{
		return shipGrid[c.getX()][c.getY()] != null;
	}
	
	/**
	 * Returns the size of the grids that make up this board.
	 * @return the grid size.
	 */
	public int getGridSize()
	{
		return gridSize;
	}

	/**
	 * Fires a shot at the specified coordinate on the ship grid, then records the result in the received shot grid and
	 * returns it.
	 * @param c the coordinate to fire at.
	 * @return {@code ShotResults.HIT} if the shot hit, {@code ShotResults.MISS} 
	 * if it didn't and {@code ShotResults.SINK} if it sinked the ship it hit.
	 */
	public ShotResults fireShotAt(Coordinate c)
	{
		ShotResults result;
		if (isShipAt(c)) 
		{
			shipCount--;
			result = shipGrid[c.getX()][c.getY()].shoot() ? ShotResults.SINK : ShotResults.HIT;
		}
		else
			result = ShotResults.MISS;
		
		setReceivedAt(c, result);

		return result;
	}
	
	/**
	 * Returns the result of the shot at the specified coordinate, or {@code null} if no such
	 * shot has been taken.
	 * @param c the coordinate.
	 * @return the result of the shot, or {@code null} if no such shot has been taken.
	 */
	public ShotResults getResultAt(Coordinate c)
	{
		return shotGrid[c.getX()][c.getY()];
	}
	
	/**
	 * Sets the cell at the specified coordinate in the result grid to the specified result.
	 * @param c the coordinate to update.
	 * @param result the result to set.
	 */
	public void setResultsAt(Coordinate c, ShotResults result)
	{
		shotGrid[c.getX()][c.getY()] = result;
	}
	
	/**
	 * Returns the result of the received shot at the specified coordinate, or {@code null} if no such
	 * shot has been taken.
	 * @param c the coordinate.
	 * @return the result of the shot, or {@code null} if no such shot has been taken.
	 */
	public ShotResults getReceivedAt(Coordinate c)
	{
		return receivedShotGrid[c.getX()][c.getY()];
	}
	
	/**
	 * Sets the cell at the specified coordinate in the received shots grid to the specified result.
	 * @param c the coordinate to update.
	 * @param result the result to set.
	 */
	public void setReceivedAt(Coordinate c, ShotResults result)
	{
		receivedShotGrid[c.getX()][c.getY()] = result;
	}
	
	/**
	 * Returns the amount of tiles occupied by ships on this board.
	 * @return the ship count.
	 */
	public int getShipCount()
	{
		return shipCount;
	}
	
	public boolean isOutside(int x, int y)
	{
		return (x < 0 || y < 0 || x >= gridSize || y >= gridSize);
	}

	/**
	 * Attempts to position a ship on the board. Will fail if the specified ship extends
	 * out of bounds or overlaps with the bounding box of an existing ship. 
	 * If this method fails, the board will not be changed.
	 * @param c the coordinate of the first cell of the ship.
	 * @param dir the direction the ship extends in.
	 * @param length the length of the ship.
	 * @return {@code true} if the ship could be placed, {@code false} otherwise.
	 */
	public boolean placeShip(Coordinate c, Directions dir, int length)
	{
		// First end of the ship
		int x1 = c.getX();
		int y1 = c.getY();
		
		// Other end of the ship
		int x2 = x1 + (dir.getX() * (length - 1));
		int y2 = y1 + (dir.getY() * (length - 1));
		
		if (isOutside(x1, y1) ||	// Ship starts OOB
		    isOutside(x2, y2)) 	// Ship ends OOB
			return false;

		// Top left cell of the ship
		int tlX = Math.min(x1, x2);
		int tlY = Math.min(y1, y2);
		
		// Bottom right cell of the ship
		int brX = Math.max(x1, x2);
		int brY = Math.max(y1, y2);
		
		// Top left cell of the bounding box of the ship
		int boundTLX = tlX - 1;
		int boundTLY = tlY - 1;
		
		// Bottom right cell of the bounding box of the ship
		int boundBRX = brX + 1;
		int boundBRY = brY + 1;
		
		for (int i = boundTLX; i <= boundBRX; i++)		// From the left edge to the right one
		{
			for (int j = boundTLY; j <= boundBRY; j++) 	// From the top edge to the bottom one
			{
				// Considering OOB as empty cells. If there is a ship in the current cell, return false
				if (!isOutside(i, j) && shipGrid[i][j] != null) return false;
			}
		}
		
		Ship ship = new Ship(length);
		
		for (int i = tlX; i <= brX; i++)		// From one end of the ship to the other, horizontally
			for (int j = tlY; j <= brY; j++)	// From one end of the ship to the other, vertically
				shipGrid[i][j] = ship;		// Set those cells to all be the same ship

		shipCount += length;
		
		return true;	
	}
	
	/**
	 * Displays the ship grid of the board with any received damage.
	 */
	public void displayShipGrid()
	{
		System.out.println("Ship grid");
		
		// Displays the columns letters horizontally
		System.out.print("   ");
		for (int i = 0; i < gridSize; i++)
			System.out.print((char)('A' + i) + " ");
		System.out.println();

		// Displays the rows
		Coordinate pos = new Coordinate();
		for (int j = 0; j < gridSize; j++)
		{
			System.out.printf("%2d ", j);
			for (int i = 0; i < gridSize; i++)
			{
				pos.set(i, j);
				// Select the proper symbol to display
				if (isShipAt(pos))
				{
					ShotResults result = getReceivedAt(pos);
					if (result == null)
						System.out.print('#');
					else
						System.out.print(result.getSymbol());
				}
				else
					System.out.print('.');
				System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	/**
	 * Displays the shot grid of the board.
	 */
	public void displayShotGrid()
	{
		System.out.println("Shot grid");
		
		// Displays the columns letters horizontally
		System.out.print("   ");
		for (int i = 0; i < gridSize; i++)
			System.out.print((char)('A' + i) + " ");
		System.out.println();
		

		// Displays the rows
		Coordinate pos = new Coordinate();
		for (int j = 0; j < gridSize; j++)
		{
			System.out.printf("%2d ", j);
			for (int i = 0; i < gridSize; i++)
			{
				pos.set(i,  j);
				ShotResults result = getResultAt(pos);
				// Select the proper symbol to display
				if (result == null)
					System.out.print('.');
				else
					System.out.print(result.getSymbol());
				System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println();
	}
}