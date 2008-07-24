public abstract class GameState
{
	public static GameState
		NORMAL = new GameState() { },
		CHECK = new GameState() { },
		CHECKMATE = new GameState() { },
		STALEMATE = new GameState() { },
		RESIGNED = new GameState() { },
		DRAW = new GameState() { };
}
