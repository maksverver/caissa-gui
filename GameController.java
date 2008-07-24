public interface GameController
{
	public Game game();
	public boolean request(Move move); /* null is resign */
}
