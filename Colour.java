public abstract class Colour
{
	public static final Colour
		WHITE = new WhiteColour(),
		BLACK = new BlackColour();

	abstract public Colour other();
}

class WhiteColour extends Colour
{
	public Colour other()
	{
		return Colour.BLACK;
	}
}

class BlackColour extends Colour
{
	public Colour other()
	{
		return Colour.WHITE;
	}
}

