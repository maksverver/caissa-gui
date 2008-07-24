public class Piece /* Immutable */
{
	public final Kind kind;
	public final Colour color;	
	
	public Piece(Piece other)
	{
		this(other.kind, other.color);
	}
	
	public Piece(Kind kind, Colour color)
	{
		this.kind = kind;
		this.color = color;
	}
	
	public boolean equals(Object obj)
	{
		return ((obj != null) && (obj instanceof Piece) &&
			(((Piece)obj).kind == kind) && (((Piece)obj).color == color));
	}
	
	public int hashCode()
	{
		return 65521 * kind.hashCode() + color.hashCode();
	}
}
