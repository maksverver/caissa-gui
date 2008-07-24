public abstract class Kind
{
	public static final Kind
		QUEEN  = new QueenKind(),
		ROOK   = new RookKind(),
		BISHOP = new BishopKind(),
		KNIGHT = new KnightKind();
	
	abstract boolean produceMoves(Game game, int x, int y, MoveConsumer callback);

	protected boolean isValid(Game game, Move move)
	{
		if(move.equals(game.lastMove()))
			return false;

		Field
			fromField = game.field(move.x1, move.y1),
			toField   = game.field(move.x2, move.y2);

		if(fromField.piece.kind == Kind.QUEEN)
			return ( 
				(toField.hasTile) && (game.mayRemoveTile(move.x1, move.y1)) &&
				(toField.piece == null || toField.piece.color != fromField.piece.color)
			);		
		else
			return (
				(toField.hasTile || game.mayMoveTile(move.x1, move.y1, move.x2, move.y2)) &&
				(toField.piece == null || toField.piece.kind != Kind.QUEEN ||
				 toField.piece.color != fromField.piece.color)
			);
	}
}

class QueenKind extends Kind
{
	public boolean produceMoves(Game game, int x, int y, MoveConsumer callback)
	{
		if(game.check())
		{
			for(int dx = -1; dx <= 1; ++dx)
				for(int dy = -1; dy <= 1; ++dy)
					if(((dx != 0) || (dy != 0)) &&
						 (x + dx >= 0) && (x + dx < 7) &&
						 (y + dy >= 0) && (y + dy < 7))
					{
						Move move = new Move(x, y, x + dx, y + dy);
						if(isValid(game, move))
							if(!callback.consumeMove(move))
								return false;
					}
		}
		else
		{
			/* Reuse implementation of rook and bishop */
			if(!Kind.ROOK.produceMoves(game, x, y, callback)) return false;
			if(!Kind.BISHOP.produceMoves(game, x, y, callback)) return false;
		}
		return true;
	}
	
	static boolean inline(Game game, Position a)
	{
		int x, y;
		Piece piece;
		
		x = a.x; y = a.y;
		while(++x < 7)
			if((piece=game.field(x,y).piece)!=null)
				if(piece.kind == Kind.QUEEN) return true; else break;

		x = a.x; y = a.y;
		while(--x >= 0)
			if((piece=game.field(x,y).piece)!=null)
				if(piece.kind == Kind.QUEEN) return true; else break;

		x = a.x; y = a.y;
		while(++y < 7)
			if((piece=game.field(x,y).piece)!=null)
				if(piece.kind == Kind.QUEEN) return true; else break;

		x = a.x; y = a.y;
		while(--y >= 0)
			if((piece=game.field(x,y).piece)!=null)
				if(piece.kind == Kind.QUEEN) return true; else break;

		x = a.x; y = a.y;
		while((++x < 7) && (++y < 7))
		{
			if((piece=game.field(x,y).piece)!=null)
				if(piece.kind == Kind.QUEEN) return true; else break;
		}
		x = a.x; y = a.y;
		while((++x < 7) && (--y >= 0))
			if((piece=game.field(x,y).piece)!=null)
				if(piece.kind == Kind.QUEEN) return true; else break;

		x = a.x; y = a.y;
		while((--x >= 0) && (++y < 7))
			if((piece=game.field(x,y).piece)!=null)
				if(piece.kind == Kind.QUEEN) return true; else break;

		x = a.x; y = a.y;
		while((--x >= 0) && (--y >= 0))
			if((piece=game.field(x,y).piece)!=null)
				if(piece.kind == Kind.QUEEN) return true; else break;

		return false;
	}
}

class RookKind extends Kind
{
	boolean produceMoves(Game game, int x, int y, MoveConsumer callback)
	{
		{
			int nx = x;
			while(--nx >= 0)
			{
				Move move = new Move(x, y, nx, y);
				if(isValid(game, move))
					if(!callback.consumeMove(move))
						return false;
				if(game.field(nx, y).piece != null) break;
			}
		}
		{
			int nx = x;
			while(++nx < 7)
			{
				Move move = new Move(x, y, nx, y);
				if(isValid(game, move))
					if(!callback.consumeMove(move))
						return false;
				if(game.field(nx, y).piece != null) break;
			}
		}
		{
			int ny = y;
			while(--ny >= 0)
			{
				Move move = new Move(x, y, x, ny);
				if(isValid(game, move))
					if(!callback.consumeMove(move))
						return false;
				if(game.field(x, ny).piece != null) break;
			}
		}
		{
			int ny = y;
			while(++ny < 7)
			{
				Move move = new Move(x, y, x, ny);
				if(isValid(game, move))
					if(!callback.consumeMove(move))
						return false;
				if(game.field(x, ny).piece != null) break;
			}					
		}
		return true;
	}
}

class BishopKind extends Kind
{
	boolean produceMoves(Game game, int x, int y, MoveConsumer callback)
	{
		{
			int	nx = x, ny = y;
			while((--nx >= 0) && (--ny >= 0))
			{
				Move move = new Move(x, y, nx, ny);
				if(isValid(game, move))
					if(!callback.consumeMove(move))
						return false;
				if(game.field(nx, ny).piece != null) break;
			}
		}

		{
			int	nx = x, ny = y;
			while((++nx < 7) && (--ny >= 0))
			{
				Move move = new Move(x, y, nx, ny);
				if(isValid(game, move))
					if(!callback.consumeMove(move))
						return false;
				if(game.field(nx, ny).piece != null) break;
			}
		}

		{
			int	nx = x, ny = y;
			while((--nx >= 0) && (++ny < 7))
			{
				Move move = new Move(x, y, nx, ny);
				if(isValid(game, move))
					if(!callback.consumeMove(move))
						return false;
				if(game.field(nx, ny).piece != null) break;
			}				
		}

		{
			int	nx = x, ny = y;
			while((++nx < 7) && (++ny < 7))
			{
				Move move = new Move(x, y, nx, ny);
				if(isValid(game, move))
					if(!callback.consumeMove(move))
						return false;
				if(game.field(nx, ny).piece != null) break;
			}
		}
		
		return true;		
	}
}

class KnightKind extends Kind
{
	static final Position deltas[] = {
		new Position(-2, -1), new Position(-1, -2),
		new Position( 2, -1), new Position(-1,  2),
		new Position(-2,  1), new Position( 1, -2),
		new Position( 2,  1), new Position( 1,  2),
	};
		
	boolean produceMoves(Game game, int x, int y, MoveConsumer callback)
	{
		for(int n = 0; n < deltas.length; ++n)
			if((x + deltas[n].x >= 0) && (x + deltas[n].x < 7) &&
				 (y + deltas[n].y >= 0) && (y + deltas[n].y < 7))
				{
					Move move = new Move(x, y, x + deltas[n].x, y + deltas[n].y);
					if(isValid(game, move))
						if(!callback.consumeMove(move))
							return false;
				}
					
		return true;
	}
}
