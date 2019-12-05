package types;

import java.io.Serializable;

/**
 * Represents a ship. Note that this class is designed assuming that all cells
 * belonging to the same ship on a board point to the same Ship object.
 * @author Alessandro Cavicchioli
 * @version 1.0
 */
public class Ship
implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int length;
	private int sunkTiles;
	
	/**
	 * Constructs a Ship with the specified length.
	 * @param length the length of the ship.
	 */
	public Ship(int length)
	{
		this.length = length;
		sunkTiles = 0;
	}
	
	/**
	 * Takes a shot at this ship, returning {@code true} if it was sunk.
	 * @return {@code true} if the ship was sunk, {@code false} otherwise.
	 */
	public boolean shoot()
	{
		sunkTiles++;
		return sunkTiles == length;
	}
}
