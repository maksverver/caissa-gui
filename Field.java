class Field
{
	public boolean hasTile = true;
	public Piece piece = null;
	public Position position;
	
	public Field(Field other)
	{
		hasTile = other.hasTile;
		piece = other.piece != null ? new Piece(other.piece) : null;
		position = new Position(other.position);
	}
	
	public Field(int x, int y)
	{
		this(new Position(x, y));
	}
	
	public Field(Position position)
	{
		this.position = position;
	}
}
