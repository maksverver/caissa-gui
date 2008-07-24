/* TODO!
		100 (?) zetten limiet 
*/

import java.util.*;

class MoveCollector implements MoveConsumer
{
	protected Collection collection;
	
	public MoveCollector(Collection collection)
	{
		this.collection = collection;		
	}

	public boolean consumeMove(Move move)
	{
		collection.add(move);
		return true;
	}
}

public class Game
{
	private GameState state = null;
	private Field[][] fields = new Field[7][7];
	private HashMap piecePositionMap = new HashMap();
	private Colour player = Colour.WHITE;
	private List history = new ArrayList();

	/* The available moves in this situation (if calculated) */
	private Collection moves = null;
	private Boolean check = null;
	
	public Game()
	{
		for(int x = 0; x < 7; ++x)
			for(int y = 0; y < 7; ++y)
				fields[x][y] = new Field(x, y);
		
		fields[3][5].piece = new Piece(Kind.BISHOP, Colour.WHITE);
		piecePositionMap.put(fields[3][5].piece, fields[3][5].position);
		fields[2][5].piece = new Piece(Kind.QUEEN, Colour.WHITE);
		piecePositionMap.put(fields[2][5].piece, fields[2][5].position);
		fields[1][5].piece = new Piece(Kind.KNIGHT, Colour.WHITE);
		piecePositionMap.put(fields[1][5].piece, fields[1][5].position);
		fields[2][4].piece = new Piece(Kind.ROOK, Colour.WHITE);
		piecePositionMap.put(fields[2][4].piece, fields[2][4].position);

		fields[3][1].piece = new Piece(Kind.BISHOP, Colour.BLACK);				
		piecePositionMap.put(fields[3][1].piece, fields[3][1].position);
		fields[4][1].piece = new Piece(Kind.QUEEN, Colour.BLACK);
		piecePositionMap.put(fields[4][1].piece, fields[4][1].position);
		fields[5][1].piece = new Piece(Kind.KNIGHT, Colour.BLACK);
		piecePositionMap.put(fields[5][1].piece, fields[5][1].position);
		fields[4][2].piece = new Piece(Kind.ROOK, Colour.BLACK);
		piecePositionMap.put(fields[4][2].piece, fields[4][2].position);
	}

	public Game(Game other)
	{
		for(int x = 0; x < 7; ++x)
			for(int y = 0; y < 7; ++y)
				fields[x][y] = new Field(other.fields[x][y]);

		piecePositionMap.putAll(other.piecePositionMap);
		history.addAll(other.history);
		player = other.player;
		moves = other.moves;
	}
	
	public Field field(Position p)
	{
		return field(p.x, p.y);
	}
	
	public Field field(int x, int y)
	{
		try
		{
			return fields[x][y];
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			return null;
		}
	}
	
	public Colour player()
	{
		return this.player;
	}
	
	public Move lastMove()
	{
		return history.isEmpty() ? null : (Move)history.get(history.size()-1);
	}
	
	private boolean isConnected()
	{
		boolean visited[][] = new boolean[7][7];
		for(int x = 0; x < 7; ++ x)
			for(int y = 0; y < 7; ++y)
				visited[x][y] = false;

		LinkedList positions = new LinkedList();

		scan_board:
		for(int x = 0; x < 7; ++ x)
			for(int y = 0; y < 7; ++y)
				if(fields[x][y].hasTile)
				{
					positions.add(new Position(x,y));
					break scan_board;
				}
		
		while(!positions.isEmpty())
		{
			Position curPos = (Position)positions.removeFirst();
			for(int dx = -1; dx <= 1; ++dx)						
				for(int dy = -1; dy <= 1; ++dy)
				{
					Position newPos = new Position(curPos.x+dx, curPos.y+dy);
					Field neighbour = field(newPos);
					if((neighbour != null) && (neighbour.hasTile) &&
						(!visited[newPos.x][newPos.y]))
					{
						visited[newPos.x][newPos.y] = true;
						positions.add(newPos);
					}
				}
		}
				
		for(int x = 0; x < 7; ++ x)
			for(int y = 0; y < 7; ++y)
				if(fields[x][y].hasTile && !visited[x][y])
					return false;

		return true;
	}

	public void perform(Move move)
	{
		if(move == null)
		{
			state = GameState.RESIGNED;
			return;
		}
		
		Field
			fieldFrom = fields[move.x1][move.y1],
			fieldTo = fields[move.x2][move.y2];
		Piece
			pieceFrom = fieldFrom.piece,
			pieceTo = fieldTo.piece;

		if(pieceFrom.kind == Kind.QUEEN)
		{
			fieldFrom.piece = null; fieldFrom.hasTile = false;
			fieldTo.piece = pieceFrom;
			if(pieceTo != null) piecePositionMap.put(pieceTo, null);
			piecePositionMap.put(pieceFrom, fieldTo.position);
		}
		else
		{
			fieldFrom.piece = pieceTo; fieldFrom.hasTile = fieldTo.hasTile;
			fieldTo.piece = pieceFrom; fieldTo.hasTile = true;
			if(pieceTo != null) piecePositionMap.put(pieceTo, fieldFrom.position);
			piecePositionMap.put(pieceFrom, fieldTo.position);
		}

		history.add(move);
		player = player.other();

		/* Clear cached information for the former position. */
		moves = null;	check = null;
	}

	protected Position positionOf(Kind kind, Colour color)
	{
		return (Position)piecePositionMap.get(new Piece(kind, color));
	}
	
	public Collection moves()
	{
		return moves == null ? moves = calculateMoves() : moves;
	}
	
	public boolean check()
	{
		return (check == null ? check = new Boolean(calculateCheck()) : check).booleanValue();
	}

	private boolean calculateCheck()
	{
		Collection moves = calculateBasicMoves(player.other());
		Iterator i = moves.iterator();
		while(i.hasNext())
		{
			Position to = ((Move)i.next()).to();
			if((fields[to.x][to.y].piece != null) &&
				 (fields[to.x][to.y].piece.kind == Kind.QUEEN) &&
				 (fields[to.x][to.y].piece.color == player))
				return true;
		}
				
		return false;
	}

	protected boolean mayMoveTile(int x1, int y1, int x2, int y2)
	{
		fields[x1][y1].hasTile = false;
		fields[x2][y2].hasTile = true;
		boolean result = isConnected();
		fields[x1][y1].hasTile = true;
		fields[x2][y2].hasTile = false;
		return result;
	}
	
	protected boolean mayRemoveTile(int x, int y)
	{
		fields[x][y].hasTile = false;
		boolean result = isConnected();
		fields[x][y].hasTile = true;
		return result;
	}

	private Collection calculateBasicMoves(Colour player)
	{
		Collection moves = new ArrayList();
		MoveCollector collector = new MoveCollector(moves);
		Position pos;
		
		if((pos = positionOf(Kind.ROOK, player)) != null)
			Kind.ROOK.produceMoves(this, pos.x, pos.y, collector);
		if((pos = positionOf(Kind.BISHOP, player)) != null)
			Kind.BISHOP.produceMoves(this, pos.x, pos.y, collector);
		if((pos = positionOf(Kind.KNIGHT, player)) != null)
			Kind.KNIGHT.produceMoves(this, pos.x, pos.y, collector);
		return moves;
	}
	
	private Collection calculateQueenMoves(boolean checked)
	{
		Collection moves = new ArrayList();
		MoveCollector collector = new MoveCollector(moves);
		Position position = positionOf(Kind.QUEEN, player);
		Kind.QUEEN.produceMoves(this, position.x, position.y, collector);
		return moves;
	}
	
	private Collection calculateMoves()
	{
		Collection moves = new LinkedList();
		{
			Collection candidates = new LinkedList();
			candidates.addAll(calculateBasicMoves(player));
			candidates.addAll(calculateQueenMoves(check()));

			Position queenPos = positionOf(Kind.QUEEN, player.other());
			Iterator i = candidates.iterator();
			while(i.hasNext())
			{
				Move move = (Move)i.next();

				Game game = new Game(this);
				game.perform(move);

				if(QueenKind.inline(game, queenPos))
					 continue; // Queens can see each other
					
				Collection otherMoves = game.calculateBasicMoves(game.player);
				Iterator j = otherMoves.iterator();
				boolean allowed = true;
				while(j.hasNext())
				{
					Position to = ((Move)j.next()).to();
					if((game.field(to).piece != null) &&
						 (game.field(to).piece.kind == Kind.QUEEN) &&
						 (game.field(to).piece.color == player))
					{
						allowed = false;
						break;
					}
				}
				if(allowed) moves.add(move);
			}
		}

		return moves;		
	}

	public boolean isValid(Move move)
	{
		return ((move==null)||(moves().contains(move)));
	}
	
	public int getHistorySize()
	{
		return history.size();
	}
	
	public Move[] getHistory()
	{
	    return (Move[])history.toArray((Object[])new Move[0]);
	}
	
	public GameState state()
	{
		if(state != null) return state;
		return check() ?
			(moves().isEmpty() ? GameState.CHECKMATE : GameState.CHECK) :
			(moves().isEmpty() ? GameState.STALEMATE :
				(history.size() < 200 ? GameState.NORMAL : GameState.DRAW));
	}
	
	public boolean over()
	{
		GameState state = state();
		return !(state == GameState.NORMAL || state == GameState.CHECK);
	}
	
	public void abort(Colour player)
	{
		if(over()) return;
		this.player = player;
		state = GameState.RESIGNED;
	}
}
