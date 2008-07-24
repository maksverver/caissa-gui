import java.io.Serializable;

/**
 * @author       Maks Verver
 * @version      1.0
 *
 * The Move class represents a Move in a Caïssa game, consisting of an
 * origin and a destination. Within the context of a game, a valid move
 * at least requires the piece at the origin to be owned by the current
 * player and the origin and destination to be different.
 *
 * The origin and destination are represented as positions containting an
 * X and a Y coordinate; both integers in the range of zero to six,
 * inclusive. The X coordinate represents the column on the board (from
 * left to right; the first column has X coordinate zero). The Y coordinate
 * represents the row on the board (from bottom to top; the row at the
 * bottom has coordinate zero).
 *
 * This class is immutable.
 */
  
public class Move implements Comparable, Serializable /* Immutable */
{
	/** the X coordinate of the origin */
	public final int x1;
	
	/** the Y coordinate of the origin */
	public final int y1;

	/** the X coordinate of the destination */
	public final int x2;

	/** the Y coordinate of the destination */
	public final int y2;

	/**
	 * Constructs a new move from a given origin and destination.
	 *
	 * @param origin     	the origin for this move
	 * @param destination	the destination for this move
	 */
	public Move(Position origin, Position destination)
	{
		this(origin.x, origin.y, destination.x, destination.y);
	}

	/**
	 * Constructs a new move from the given coordinates.
	 * @param x1 the X coordinate of the origin for this move
	 * @param y1 the Y coordinate of the origin for this move
	 * @param x2 the X coordinate of the destination for this move
	 * @param y2 the Y coordinate of the destination for this move
	 */
	public Move(int x1, int y1, int x2, int y2)
	{
		this.x1 = x1; this.y1 = y1;
		this.x2 = x2; this.y2 = y2;
	}


	/** 
	 * Returns the destination of this move.
	 *
	 * @return the destination of this move
	 */
	public Position to()
	{
		return new Position(x2, y2);
	}

	/** 
	 * Returns the origin of this move.
	 *
	 * @return rhe origin of this move
	 */
	public Position from()
	{
		return new Position(x1, y1);
	}
	
	/** 
	 * Returns the string representation for this move: the string
	 * representation of the origin and destination, seperated by
	 * a dash.	 *
	 * @return the string representation of this move
	 */
	public String toString()
	{
		return toString(x1, y1, x2, y2);
	}
	
	/** 
	 * Returns the string representation for a move: the string
	 * representation of the origin and destination, seperated by
	 * a dash.
	 *
	 * @param x1 The X coordinate of the origin for the move
	 * @param y1 The Y coordinate of the origin for the move
	 * @param x2 The X coordinate of the destination for the move
	 * @param y2 The Y coordinate of the destination for the move
	 *
	 * @return the origin of this move
	 */
	public static String toString(int x1, int y1, int x2, int y2)
	{
		return Position.toString(x1, y1) + "-" + Position.toString(x2, y2);
	}

	public boolean equals(Object obj)
	{
		if((obj == null) || (!(obj instanceof Move))) return false;
		Move move = (Move)obj;
		return ((x1==move.x1)&&(y1==move.y1)&&(x2==move.x2)&&(y2==move.y2));
	}
	
	/**
	 * Converts a string representation of a move, conforming to the
	 * description for the CodeCup contest, into an instance of the Move
	 * class. If str does not contain a valid representation for a move,
	 * null may be returned.
	 *
	 * A valid string representation consists of two positions (the origin
	 * and destination) seperated  by a single hyphen character ('-'), 
	 * optionally followed by additional characters which are ignored.
	 *
	 * Note that if a string is converted to a move by this method and then 
	 * converted back to a string with the toString method, the two strings
	 * do not need to be identical. However, a move resulting by applying
	 * fromString to a string that was returned by the toString method, will
	 * be equal to the original move.
	 *
	 * @param str The string representation of the move. May not be null.
	 *
	 * @return An instance of the Move class representing the given string,
	 *         or null if the string is not formatted correctly.
	 */	
	public static Move fromString(String str)
	{
		Position from, to;
		if((str.length() >= 5) && (str.charAt(2) == '-') &&
			 ((from = Position.fromString(str.substring(0,2))) != null) &&
			 ((to = Position.fromString(str.substring(3,5))) != null))
			return new Move(from, to);
		return null;
	}
	
	public int compareTo(Object obj)
	{
		Move other = (Move)obj;
		int result;
		if((result = x1 - other.x1) == 0)
			if((result = other.y1 - y1) == 0)
				if((result = x2 - other.x2) == 0)
					result = other.y2 - y2;
		return result;
	}

	/** This method is identical to the toString() instance method, except
	  * that a null argument will result in "resign" being returned.
	  *	
	  * @param move The move to be converted to a string; may be null.
	  * 
	  *	@return The string representation of the given move, or "resign" if
	  *         the object reference passed was null.
	  */
	public static String toString(Move move)
	{
		return move==null?"resign":move.toString();
	}
}
