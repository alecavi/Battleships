package types;

/**
 * Represents the result of a shot. This class also provides methods to display the result to the user
 * @author Alessandro
 * @version 1.0
 */
public enum ShotResults
{
	MISS ("Missed!", 'o'),
	HIT	 ("Hit!", 'x'),
	SINK ("Sunk!", 'X');
	
	private String msg;
	private char symbol;
	
	private ShotResults(String msg, char symbol)
	{
		this.msg = msg;
		this.symbol = symbol;
	}
	
	/**
	 * Returns the word representing the result.
	 * @return the word as a string.
	 */
	public String getMsg()
	{
		return msg;
	}
	
	/**
	 * Returns the graphic symbol representing the result.
	 * @return the word as a ASCII char.
	 */
	public char getSymbol()
	{
		return symbol;
	}
}
