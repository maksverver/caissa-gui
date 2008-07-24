public class Position /* Immutable */
{
	public final int x, y;
	
	public Position(Position other)
	{
		this(other.x, other. y);
	}
	
	public Position(int x, int y)
	{
		this.x = x; this.y = y;
	}
		
	public String toString()
	{
		return toString(x, y);
	}
	
	public static String toString(int x, int y)
	{
	    /* Stupid Java 1.3 API doesn't have a static toString method )-: */
		return
			(new Character("ABCDEFG".charAt(x)).toString()) +
			(new Integer((7 - y)).toString());
	}
	
	public static Position fromString(String str)
	{
		if(str.length() < 2) return null;
		char
			cx = str.charAt(0),
			cy = str.charAt(1);
		int
			x = "aAbBcCdDeEfFgG".indexOf(cx),
			y = "7654321".indexOf(cy);
		if((x < 0) || (y < 0)) return null;
		return new Position(x/2, y);
	}
	
	public boolean equals(Object other)
	{
		return (
			(other != null) &&
			(other instanceof Position) &&
			(x == ((Position)other).x) && (y == ((Position)other).y)
		);
	}
	
	public boolean odd()
	{
		return ((x + y) % 2) == 0;
	}
}
