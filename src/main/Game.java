package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.InputMismatchException;
import java.util.Scanner;

import player.*;
import types.Coordinate;
import types.ShotResults;

/**
 * Responsible for starting and running a game of battleships. Also contains the
 * entry point of the program
 * @author Alessandro Cavicchioli
 * @version 1.0
 */
public class Game
{
	private	static final int MIN_BOARD_SIZE = 10;
	private	static final int MAX_BOARD_SIZE = 26;
	private	static final int MIN_SHIPS_NUM = 4;
	public	static final int MAX_SHIP_LENGTH = 4;

	private AbstractPlayer attacker;
	private AbstractPlayer defender;
	
	private int	shipsNumber;
	private int[] shipLengths;
	private int	gridSize;
	
	private int	turn;
	
	public static final Scanner scan = new Scanner(System.in);

	
	public static void main(String[] args)
	{
		Game game = new Game();
		boolean created = game.newGame();
		if (created)
			game.gameLoop();
		scan.close();
	}


	/**
	 * Starts a new game asking for all game parameters.
	 * If requested load a previous game state from the disc.
	 */
	private boolean newGame()
	{
		System.out.println("BATTLESHIPS!!!");
		System.out.println("by Alessandro Cavicchioli - ITSD Course 2019.");
		
	    while (true)
	    {
	    	System.out.print("\nDo you want to load a saved game (Y/N)? ");
	        String input = scan.nextLine();
	        
	        if (input.equalsIgnoreCase("Y"))
	        {
	        	System.out.print("Input game name to load: ");
	        	String fileName = scan.nextLine() + ".sav";
	        	
	        	if (loadGame(fileName))
	        	{
		        	System.out.println("\nGame \"" + fileName + "\" loaded.");
		        	break;
	        	}
	        	else
	        		System.out.println("\nGame \"" + fileName + "\" cannot be loaded. Please try again.");
	        }
	        else if (input.equalsIgnoreCase("N"))
	        {
	        	System.out.println("\nStarting a new game.");
	        	createGame();
	        	break;
	        }
	        else System.out.println("Invalid input. Please try again.");
	    }
		System.out.print("\nPreparations completed. Press enter to start the game. ");
		Game.scan.nextLine();

	    return true;
	}
	
	/**
	 * Loops between players to make them play their turn.
	 * If requested, saves the game state to the disc and exits.
	 */
	private void gameLoop()
	{
		boolean exit = false; 
		
		do
		{
			System.out.println("\nStarting turn "+ (turn + 1) + ".");
			
			Coordinate shot = attacker.getShot();
			if (shot != null)	//a null shot represents the player wanting to exit the game
			{
				ShotResults result = defender.checkFiredShot(shot);
				attacker.recordShot(shot,  result);
				
				if (!defender.isDefeated())
				{
					AbstractPlayer swap = attacker;
					attacker = defender;
					defender = swap;
				}
				turn++;
			}
			else exit = true;
		} while (!defender.isDefeated() && !exit);
		
		if (exit)
		{
			while (true)
			{	
				System.out.print("\nDo you want to save the game before quitting (Y/N)? ");
		        String input = scan.nextLine();
		          
		        if (input.equalsIgnoreCase("Y"))
		        {
		        	System.out.print("Input game name to save: ");
		        	String fileName = scan.nextLine() + ".sav";
		        	
		        	if (saveGame(fileName))
		        	{
			        	System.out.println("Game \"" + fileName + "\" saved.");
			        	break;
		        	}
		        	else
		        		System.out.println("Game \"" + fileName + "\" cannot be saved. Please try again.");
		        }
		        else if (input.equalsIgnoreCase("N")) break;
		        else System.out.println("Invalid input. Please try again.");
			}
		}
		else // exit == false
		{
			System.out.println("\n=======================================");
			System.out.println("Game completed in " + turn + " turns.");
			System.out.println("The winner is " + attacker.getPlayerName() + "!");
			System.out.println();
			System.out.println("Final situation");
			System.out.println();
			System.out.println(attacker.getPlayerName() + " (Winner)");
			System.out.println();
			attacker.displayGrid();
			System.out.println();
			System.out.println(defender.getPlayerName() + " (Loser)");
			System.out.println();
			defender.displayGrid();
		}

    	System.out.println("\nBye!");
	}
	
	/**
	 * Creates a new game, prompting the user to input all of the required parameters
	 */
	private void createGame()
	{
		// board size
    	while (true)
	    {
	    	try 
	    	{
	    		System.out.print("\nInput the board size (from " + MIN_BOARD_SIZE + " to " + MAX_BOARD_SIZE + "): ");
	    		gridSize = scan.nextInt();
	    		if (gridSize >= MIN_BOARD_SIZE && gridSize <= MAX_BOARD_SIZE) break;
	    		else System.out.println("Invalid input. Please try again.");
	    	}
	    	catch (InputMismatchException e)
	    	{
	    		System.out.println("Invalid input. Please input a numeric value.");
	    		scan.nextLine();
	    	}
	    }
	
    	// Number of ships
	    while (true)
	    {
	    	try
	    	{
		    	System.out.print("\nInput the number of ships to be positioned (from " + MIN_SHIPS_NUM + " to " + (6 * (gridSize / 6)) + "): ");
		        shipsNumber = scan.nextInt();
		        if (shipsNumber >= MIN_SHIPS_NUM && shipsNumber <= (6 * (gridSize / 6))) break;
		        else System.out.println("Input not valid. Please try again.");
	    	}
	    	catch (InputMismatchException e)
	    	{
	    		System.out.println("Invalid input. Please input a numeric value.");
	    		scan.nextLine();
	    	}
		}
	    System.out.println();
	    
	    // Lengths of the ships
	    shipLengths = new int[shipsNumber];
	    for (int i = 0; i < shipsNumber; i++)
	    {
	        while (true)
	        {
	        	try
	        	{
		        	System.out.print("Input the length of ship n. " + (i + 1) + " (from 1 to " + MAX_SHIP_LENGTH + "): ");
			        shipLengths[i] = scan.nextInt();
		            if (shipLengths[i] >= 1 && shipLengths[i] <= MAX_SHIP_LENGTH) break;
		            else System.out.println("Invalid input. Please try again.\n");
	        	}
	        	catch (InputMismatchException e)
		    	{
		    		System.out.println("Invalid input. Please input a numeric value.\n");
		    	}
	        	finally
	        	{
	        		scan.nextLine();
	        	}
	        }
	    }
		
		// Type of player1, who starts as attacker
		while (true)
		{
			System.out.println("\nInput the type of the first player (Who will attack first)");
			System.out.print("\"H\" for a human player, \"C\" for a CPU player: ");
			String input = scan.nextLine();
			if (input.equalsIgnoreCase("H"))
			{
				System.out.print("\nInput the player's name: ");
				String name = scan.nextLine();
				attacker = new HumanPlayer(name, gridSize);
				break;
			}
			else if (input.equalsIgnoreCase("C"))
			{
				attacker = new CPUPlayer("EDI", gridSize);
				break;
			}
			else
				System.out.println("Invalid input. Please try again.");
		}
		
		// Type of player2, who starts as defender
		while (true)
		{
			System.out.println("\nInput the type of the second player.");
			System.out.print("\"H\" for a human player, \"C\" for a CPU player: ");
			String input = scan.nextLine();
			if (input.equalsIgnoreCase("H"))
			{
				System.out.print("\nInput the player's name: ");
				String name = scan.nextLine();
				defender = new HumanPlayer(name, gridSize);
				break;
			}
			else if (input.equalsIgnoreCase("C"))
			{
				defender = new CPUPlayer("HAL 9000", gridSize);
				break;
			}
			else
				System.out.println("Invalid input. Please try again.");
		}

		//The parameters have been provided. Time to initialise the game.
		attacker.placeShips(shipLengths);
		defender.placeShips(shipLengths);
		turn = 0;
	}
	
	/**
	 * Attempts to save the current game state.
	 * 
	 * @throws FileNotFoundException if the file cannot be created or otherwise written to
	 * @throws IOException if an I/O error occurs
	 */
	private boolean saveGame(String fileName)
	{
		GameState gameState = new GameState(attacker, defender, turn);
		
		try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(fileName)))
		{
			writer.writeObject(gameState);
			return true;
		} 
		catch (FileNotFoundException e)
		{
			System.out.println("Cannot create or write file to disc.");
		} 
		catch (IOException e)
		{
			System.out.println("An I/O error has occurred.");
		}
		return false;
	}
	
	/**
	 * Attempts to load a previous game state.
	 * @throws FileNotFoundException if the file cannot be loaded
	 * @throws IOException if an I/O error occurs
	 */
	private boolean loadGame(String fileName)
	{
		try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(fileName)))
		{
			GameState gameState = (GameState) reader.readObject();
			attacker = gameState.getAttacker();
			defender = gameState.getDefender();
			turn = gameState.getTurn();
			return true;
		} 
		catch (ClassCastException e)
		{
			System.out.println("The specified file could not be read as a save file.");
		}
    	catch (ClassNotFoundException e)
		{
			System.out.println("The specified save file is corrupted.");
		} 
    	catch (FileNotFoundException e)
		{
			System.out.println("File not found.");
		} 
    	catch (IOException e)
		{
			System.out.println("An I/O error has occurred.");
		}
		return false;
	}
	
}