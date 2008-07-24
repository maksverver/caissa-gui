import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GamePanel extends Panel
{
	private Game game;
	private Colour player;
	private GameController controller;
	private BoardPanel boardPanel;
	private Label statusLabel = new Label("", Label.CENTER);
	private MoveList moveList;
	private TextArea historyTextArea =
		new TextArea("", 10, 20, TextArea.SCROLLBARS_VERTICAL_ONLY);

/*
	private int
		whitePositions = 0, whiteMoves = 0,
		whiteCheckPositions = 0, whiteCheckMoves = 0,		
		blackPositions = 0, blackMoves = 0,
		blackCheckPositions = 0, blackCheckMoves = 0;
*/

	public GamePanel(GameController controller, Colour player,
	    ActionListener actionListener)
	{
		this.controller = controller;
		this.player = player;
		this.game = controller.game();

		moveList = new MoveList(controller, this);
		boardPanel = new BoardPanel(controller, this, player==Colour.BLACK);
		
		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		
		constraints.gridx = 0; // First column
		
		{
			// Add the game board
			constraints.gridy = 0; constraints.gridheight = 7;
			gridbag.setConstraints(boardPanel, constraints);
			add(boardPanel);
		}

		constraints.gridx = 1; // Second column
		constraints.insets.left = constraints.insets.right = 8;
		constraints.gridheight = 1;
		
		{
			// Add the game history
			Component historyLabel = new Label("History:");
			constraints.gridy = 0; constraints.weighty = 0.0;
			constraints.insets.top = 4;
			gridbag.setConstraints(historyLabel, constraints);
			
			constraints.gridy = 1; constraints.weighty = 1.0;
			constraints.insets.top = 0;
			historyTextArea.setEditable(false);
			historyTextArea.setBackground(SystemColor.text);
			historyTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
			gridbag.setConstraints(historyTextArea, constraints);

			Button storeHistoryButton = new Button("Save...");
			storeHistoryButton.setActionCommand("storeHistory");
			storeHistoryButton.addActionListener(actionListener);

			constraints.gridy = 2; constraints.weighty = 0.0;
			constraints.insets.top = 0;
			gridbag.setConstraints(storeHistoryButton, constraints);

			add(historyLabel); add(historyTextArea); add(storeHistoryButton);
		}

		{
			// Add the available moves
			Component movesLabel = new Label("Available moves:");			
			constraints.gridy = 3; constraints.weighty = 0.0;
			constraints.insets.top = 4;
			gridbag.setConstraints(movesLabel, constraints);
			constraints.gridy = 4; constraints.weighty = 1.0;
			constraints.insets.top = 0;
			gridbag.setConstraints(moveList, constraints);			
			add(movesLabel); add(moveList);
		}

		
		{
			// Add the flip board checkbox label
			constraints.gridy = 5; constraints.weighty = 0.0;
			constraints.insets.top = 4;
			Checkbox checkbox = new BoardFlipCheckbox(boardPanel, player==Colour.BLACK);
			gridbag.setConstraints(checkbox, constraints);
			add(checkbox);
		}		
		
		{
			// Add the status label
			constraints.gridy = 6; constraints.weighty = 0.0;
			constraints.insets.top = 4;
			gridbag.setConstraints(statusLabel, constraints);			
			add(statusLabel);
		}		
			
		{
			// Add the quit button
			constraints.insets.top = constraints.insets.bottom = 4;
			constraints.gridy = 7; constraints.weighty = 0.0;
			Component quitButton = new ResignButton(controller);
			gridbag.setConstraints(quitButton, constraints);			
			add(quitButton);
		}
		
		processMove(null);
	}
	
	public void update(Graphics g)
	{
		// Don't clear background - this component is opaque
		paint(g);
	}
	
	public void processMove(Move move)
	{
		// Update history
		if(move != null)
		{
			String moveString
				= game.lastMove().toString() + (game.check()?"+":" ");
			
			int moves = game.getHistorySize();
			historyTextArea.append((moves % 2) == 1 ?
				(moves/2 + 1 < 10 ? " " : "") +
					Integer.toString(moves/2 + 1) + ". " + moveString :
				" " + moveString + "\n");
		}
		
		Collection availableMoves = game.moves();

		// Update list of available moves
		moveList.reset(availableMoves);
		
		/* Update total number of moves (for game analysis) */
/*
		if(game.player()==Colour.WHITE)
		{
			if(game.check())
			{
				whiteCheckMoves += availableMoves.size();			
				++whiteCheckPositions;
			} else
			{
				whiteMoves += availableMoves.size();
				++whitePositions;
			}
		} else
		if(game.player()==Colour.BLACK)
		{
			if(game.check())
			{
				blackCheckMoves += availableMoves.size();			
				++blackCheckPositions;
			} else
			{
				blackMoves += availableMoves.size();
				++blackPositions;
			}
		}
	
		
		System.out.println("Average number of available moves per position: (all, non-check, check)");
		System.out.println("White: " + (whiteCheckPositions+whitePositions==0?"-":
			Float.toString((whiteCheckMoves+whiteMoves)/(whiteCheckPositions+whitePositions))) +
			" / " + (whitePositions==0?"-":Float.toString(whiteMoves/whitePositions)) +
			" / " + (whiteCheckPositions==0?"-":Float.toString(whiteCheckMoves/whiteCheckPositions)));
		System.out.println("Black: " + (blackCheckPositions+blackPositions==0?"-":
			Float.toString((blackCheckMoves+blackMoves)/(blackCheckPositions+blackPositions))) +
			" / " + (blackPositions==0?"-":Float.toString(blackMoves/blackPositions)) +
			" / " + (blackCheckPositions==0?"-":Float.toString(blackCheckMoves/blackCheckPositions)));
*/

		// Update game field view
		if(move != null)
			boardPanel.repaintForMove(move);

		// Show current player and game state	(maybe move state to Game?)
		String player = (game.player() == Colour.WHITE ? "White" : "Black");
		GameState state = game.state();
		String stateString = "";
		if(state == GameState.CHECK) stateString = "Check!";
		if(state == GameState.CHECKMATE) stateString = "Checkmate!";
		if(state == GameState.STALEMATE) stateString = "Stalemate.";
		if(state == GameState.RESIGNED) stateString = "Resigned.";
		if(state == GameState.DRAW) stateString = "Draw.";
		statusLabel.setText(state == GameState.NORMAL ?
			player + " to move" : player + " - " + stateString);
	}
	
	public boolean moveEnabled()
	{
		return ((!game.over())&&(game.player() == player));
	}

}