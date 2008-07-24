import java.awt.*;
import java.util.*;

public class BoardPanel extends Panel
{
	private Game game;
	private GamePanel gamePanel;
	private GameController controller;
	FieldComponent selectedFieldComponent = null;
	FieldComponent[][] fieldComponents = new FieldComponent[7][7];
	
	public BoardPanel(GameController controller, GamePanel gamePanel, boolean flipped)
	{
		this.controller = controller;
		this.game = controller.game();
		this.gamePanel = gamePanel;

		for(int y = 0; y < 7; ++y)
			for(int x = 0; x < 7; ++x)
				fieldComponents[x][y] = new FieldComponent(this, game.field(x, y));

		rebuildLayout(flipped);
	}

	public void rebuildLayout(boolean isFlipped)
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		removeAll();

		for(int y = 0; y < 7; ++y)
		{
			constraints.gridx = 0; constraints.gridy = isFlipped ? 6 - y : y;
			Label coordLabel = new Label(new Integer(7-y).toString(), Label.CENTER);
			gridbag.setConstraints(coordLabel, constraints);
			add(coordLabel);

			for(int x = 0; x < 7; ++x)
			{
				constraints.gridx = isFlipped ? 7 - x : x + 1;
				gridbag.setConstraints(fieldComponents[x][y], constraints);
				add(fieldComponents[x][y]);
			}
		}

		constraints.gridy = 7;
		for(int x = 0; x < 7; ++x)
		{
			Label coordLabel = new Label(new Character("ABCDEFGH".charAt(x)).toString(), Label.CENTER);
			constraints.gridx = isFlipped ? 7 - x : x + 1;
			gridbag.setConstraints(coordLabel, constraints);
			add(coordLabel);
		}

		setLayout(gridbag);
		validate();
	}
	
	public void fieldClicked(FieldComponent fieldComponent)
	{
		if(!gamePanel.moveEnabled()) return;
				
		if(selectedFieldComponent != null)
		{
				boolean
					deselect = selectedFieldComponent == fieldComponent,
					requestMove = fieldComponent.isHighlit;

				if(requestMove)
					controller.request(new Move (
						selectedFieldComponent.field().position,
						fieldComponent.field().position
					));
				
				/* Clear currently highlit fields */
				for(int x = 0; x < 7; ++x)
					for(int y = 0; y < 7; ++y)
						if(fieldComponents[x][y].isHighlit)
						{
							fieldComponents[x][y].isHighlit = false;
							fieldComponents[x][y].repaint();
						}

				if(!requestMove)
				{
					selectedFieldComponent.isSelected = false;
					selectedFieldComponent.repaint();
					selectedFieldComponent = null;
					if(deselect) return;
				}
		}
		
		Field field = fieldComponent.field();
		int fieldsFound = 0;
		
		Collection moves = game.moves();
		Iterator i = moves.iterator();
		while(i.hasNext())
		{
			Move move = (Move)i.next();
			if(move.from().equals(field.position))
			{
				++fieldsFound;
				FieldComponent hightlitComponent =
					fieldComponents[move.x2][move.y2];
				hightlitComponent.isHighlit = true;
				hightlitComponent.repaint();
			}
		}
		
		if(fieldsFound == 0)
		{
			selectedFieldComponent = null;
			return;
		}
				
		selectedFieldComponent = fieldComponent;
		fieldComponent.isSelected = true;
		fieldComponent.repaint();
		
	}
	
	public void repaintForMove(Move move)
	{
		if(selectedFieldComponent != null)
		{
			for(int x = 0; x < 7; ++x)
				for(int y = 0; y < 7; ++y)
					if(fieldComponents[x][y].isHighlit)
					{
						fieldComponents[x][y].isHighlit = false;
						fieldComponents[x][y].repaint();
					}
			selectedFieldComponent.isSelected = false;
			selectedFieldComponent.repaint();
			selectedFieldComponent = null;
		}
		fieldComponents[move.x1][move.y1].repaint();
		fieldComponents[move.x2][move.y2].repaint();
	}
	
	public void update(Graphics g)
	{
		// Don't clear background - this component is opaque
		paint(g);
	}

}