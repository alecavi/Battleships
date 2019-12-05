package main;

import java.io.Serializable;
import player.*;

/**
 * A class that holds two players and the current turn number. Used for serialization/deserialization
 * @author Alessandro
 * @version 1.0
 */
public class GameState
implements Serializable
{
	private static final long serialVersionUID = 1L;

	private AbstractPlayer attacker;
	private AbstractPlayer defender;
	private Integer turn;
	
	/**
	 * Initializes the fields in this object, making it ready to be serialized
	 * @param attacker the player currently attacking
	 * @param defender the player currently being attacked
	 * @param turn the current turn
	 */
	public GameState(AbstractPlayer attacker, AbstractPlayer defender, Integer turn)
	{
		this.attacker = attacker;
		this.defender = defender;
		this.turn = turn;
	}

	/**
	 * @return the attacker
	 */
	public AbstractPlayer getAttacker()
	{
		return attacker;
	}

	/**
	 * @return the defender
	 */
	public AbstractPlayer getDefender()
	{
		return defender;
	}

	/**
	 * @return the turn
	 */
	public int getTurn()
	{
		return turn;
	}
	
}
