package types;

/**
 * A class meant to store a grid coordinate
 * @author Alessandro Cavicchioli
 * @version 1.0
 */
public class Coordinate
{
	private int x;
	private int y;
	
	/**
	 * Constructs a Coordinate object and initializes it to {@code (0,0)}.
	 */
	public Coordinate()
	{
		x = 0;
		y = 0;
	}
	
	public Coordinate(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the x
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * @return the y
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * @param x the x to set
	 * @param y the y to set
	 */
	public void set(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
}