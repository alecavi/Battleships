package player;

import main.*;
import types.Coordinate;
import types.Directions;
import types.ShotResults;

/**
 * A human-controlled player
 * @author Alessandro
 * @version 1.0
 */
public class HumanPlayer extends AbstractPlayer
{	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs a new player object with the specified player name and the specified board size.
	 * Note that the object is still not ready for use, and will require the ships to be placed.
	 * @param playerName the name of the player.
	 * @param boardSize the size of the board.
	 */
	public HumanPlayer(String playerName, int boardSize)
	{
		super(playerName, boardSize);
	}
	
	/**
	 * {@inheritDoc}
	 * This method will prompt the human player for a ship placement until all the ships have been placed.
	 */
	@Override
	public void placeShips(int[] lengths)
	{
		System.out.println("\n" + getPlayerName() + ", please positions your ships.\n");
		
		for (int i = 0; i < lengths.length; i++)
		{
			// Show the ship board to help player in selecting the new place
			getBoard().displayShipGrid();

			// Asks for new placing coordinate and direction
			Coordinate position = new Coordinate();
            Directions direction;
			while (true)
			{
                if (lengths[i] == 1) // 1-long ships don't need the user to input a direction
                {
    				System.out.println(getPlayerName() + ", input the coordinate to place a 1 tile ship at.");
    				
    				String input = null;		
    				while (true)
    				{
    					System.out.print("\nAccepted inputs look like \"A3\" or \"D5\": ");
    					input = Game.scan.nextLine();
    					
    					if (input.matches("[A-Za-z]\\d{1,2}")) break;
    					else System.out.println("Invalid input. Please try again.");
    				}
    				System.out.println();
    				
    				int x = Character.getNumericValue(input.charAt(0)) - Character.getNumericValue('A');
    				int y = Integer.parseInt(input.substring(1));
    				
    				position.set(x, y);
    				direction = Directions.UP;  // Adding a "NONE" direction and special-casing it in the ship placement code isn't worth it
                }
                else
                {
    				System.out.println(getPlayerName() + ", input the coordinate to place a " + lengths[i] + 
    						" tiles ship at,\nas well as the direction to place it in (R=right, L=left, U=up, D=down).");
    				
    				String input = null;		
    				while (true)
    				{
    					System.out.print("\nAccepted inputs look like \"A3 R\" or \"D5 D\": ");
    					input = Game.scan.nextLine();
    					if (input.matches("(?i)[A-Za-z]\\d{1,2}\\s(?:l|r|u|d)")) break;
    					else System.out.println("Invalid input. Please try again.");
    				}
    				System.out.println();
    				
    				String[] inputdata = input.split("\\s");
    				int x = Character.getNumericValue(inputdata[0].charAt(0)) - Character.getNumericValue('A');
    				int y = Integer.parseInt(inputdata[0].substring(1));
    				
    				position.set(x, y);
    				direction = Directions.fromFirstLetter(inputdata[1].toUpperCase());
                }
          		boolean validShip = getBoard().placeShip(position, direction, lengths[i]);
  				if (validShip) break;
  				else System.out.println("The specified ship could not be placed. Please try again.\n");
			}
		}
	}

	@Override
	public Coordinate getShot()
	{
		System.out.println("\nIt's " + getPlayerName() +"'s turn.");
		
		getBoard().displayShotGrid();
		
		Coordinate newShot = new Coordinate();

		while (true)
		{
			// Asks for new placing coordinate and direction.
			// Word "save" (case insensitive) can be used to stop immediately the game and save the state
			
			System.out.print("\nInput a coordinate, or \"exit\" to quit (and save the game if you wish): ");
			
			String input = Game.scan.nextLine();
			
			if (input.equalsIgnoreCase("exit"))
			{
				newShot = null;
				break;
			}
			else if (input.matches("[A-Za-z]\\d{1,2}"))
			{
				int x = Character.getNumericValue(input.charAt(0)) - Character.getNumericValue('A');
				int y = Integer.parseInt(input.substring(1));
				
				if (getBoard().isOutside(x, y))
					System.out.println("Input coordinates are out of bounds.\n");
				else
				{
					newShot.set(x, y);
					if (getBoard().getResultAt(newShot) == null) break;
					else System.out.println("The specified coordinate has already been targeted.\n");	
					// Just to give a little help to an absent-minded player ;)
				}
			}
			else
				System.out.println("Invalid input. Please try again.");
		}

		if (newShot != null)
			System.out.println("Shooting in " + (char)('A' + newShot.getX()) + newShot.getY());

		return newShot;
	}

	/**
	 * {@inheritDoc}
	 * It waits for a key to be pressed in order to let the player read the result of its shot.
	 */
	@Override
	public void recordShot(Coordinate c, ShotResults result)
	{
		super.recordShot(c, result);
		
		System.out.print("Press enter to continue. ");
		Game.scan.nextLine();
	}
}


























