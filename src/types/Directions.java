package types;

/**
 * Represents a cardinal direction on a 2d board. The values also have
 * fields that represent the direction of movement as a pair of coordinates.
 * @author Alessandro Cavicchioli
 * @version 1.0
 * 
 */
public enum Directions
{
	UP(0, -1),
	DOWN(0, 1),
	LEFT(-1, 0),
	RIGHT(1, 0);
	
	private int x;
	private int y;
	
	private Directions(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Returns the x value of this enum value. Can be -1, 0 or 1.
	 * @return the x value.
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Returns the y value of this enum value. Can be -1, 0 or 1.
	 * @return the y value.
	 */
	public int getY()
	{
		return y;
	}
	
	/**
	 * Returns the opposite direction of a given direction.
	 * @return the opposite direction.
	 * @throws NullPointerException if the argument is {@code null}.
	 */
	public static Directions opposite(Directions dir) {
		switch (dir) {
		case UP: return Directions.DOWN;
		case DOWN: return Directions.UP;
		case LEFT: return Directions.RIGHT;
		case RIGHT: return Directions.LEFT;
		default: throw new NullPointerException();
		}
	}
	
	/**
	 * Set the direction according to the delta coordinates values.
	 * @return the opposite direction.
	 */
	public static Directions set(int dX, int dY) {
		if (dX == 0 && dY < 0) return Directions.UP;
		else if (dX == 0 && dY > 0) return Directions.DOWN;
		else if (dX < 0 && dY == 0) return Directions.LEFT;
		else if (dX > 0 && dY == 0) return Directions.RIGHT;
		else return null;
	}
	
	/**
	 * Returns the enum value representing the specified direction.
	 * @param letter the first letter of the direction. Can be any of {@code U, D, L, R}.
	 * @return the corresponding direction.
	 * @throws IllegalArgumentException if there is no corresponding direction.
	 */
	public static Directions fromFirstLetter(String letter)
	{
		switch(letter)
		{
		case "U": return Directions.UP;
		case "D": return Directions.DOWN;
		case "L": return Directions.LEFT;
		case "R": return Directions.RIGHT;
		default: throw new IllegalArgumentException();
		}
	}
}


















