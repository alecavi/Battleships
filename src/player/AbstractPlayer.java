package player;

import java.io.Serializable;
import java.util.InputMismatchException;

import types.Coordinate;
import types.ShotResults;

/**
 * An abstract base player. Used both to provide common functionality to the concrete players and to make use of polymorphism
 * @author Alessandro
 * @version 1.0
 */
public abstract class AbstractPlayer
implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Board board;
	private String playerName;

	
	/**
	 * Constructs a new player object with the specified player name and the specified board size.
	 * Note that the object is still not ready for use, and will require the ships to be placed.
	 * @param playerName the name of the player.
	 * @param boardSize the size of the board.
	 */
	public AbstractPlayer(String playerName, int boardSize)
	{
		this.board = new Board(boardSize);
		this.playerName = playerName;
	}

	
	/**
	 * @return the board
	 */
	public Board getBoard()
	{
		return board;
	}

	
	/**
	 * @return the playerName
	 */
	public String getPlayerName()
	{
		return playerName;
	}

	
	/**
	 * Places the ships specified by the input array on the grid.
	 * @param lengths an array of integers. For every value in the array, a ship with that length
	 * will be created.
	 * @throws InputMismatchException if this function does not manage to place all ships (for example,
	 * if it's impossible to do so).
	 */
	public abstract void placeShips(int[] lengths);

	
	/**
	 * Return a shot coordinate to be fired, or {@code null} if this player wants to exit the game
	 */
	public abstract Coordinate getShot();

	
	/**
	 * Returns the result of an incoming shot and keeps track of the result of that shot.
	 * Displays the result message.
	 * 
	 * @param c the coordinate to fire to.
	 */
	public ShotResults checkFiredShot(Coordinate c)
	{
		ShotResults result = board.getReceivedAt(c);

		if (result == null)					//It's a new hit
			result = board.fireShotAt(c);
		
		if (result != null)
			System.out.println(result.getMsg());
		
		return result;
	}
	

	/**
	 * Records the result of a shot at the specified coordinate.
	 * @param c coordinate that was shot at.
	 * @return {@code ShotResults.HIT} if the shot hit, {@code ShotResults.MISS} 
	 * if it didn't and {@code ShotResults.SINK} if it sinked the ship it hit.
	 */
	public void recordShot(Coordinate c, ShotResults result)
	{
		board.setResultsAt(c, result);
		System.out.println();
		board.displayShotGrid();
	}

	
	
	/**
	 * Returns whether this player was defeated.
	 * @return {@code true} if this player was defeated, {@code false} otherwise.
	 */
	public boolean isDefeated()
	{
		return board.getShipCount() == 0;
	}

	/**
	 * Displays the ship grid used by the player with reported damages.
	 */
	public void displayGrid()
	{
		board.displayShipGrid();
		board.displayShotGrid();
	}

}