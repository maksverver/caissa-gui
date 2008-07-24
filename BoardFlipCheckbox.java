import java.awt.*;
import java.awt.event.*;

class BoardFlipCheckbox extends Checkbox implements ItemListener
{
	private BoardPanel board;
		
	public BoardFlipCheckbox(BoardPanel board, boolean checked)
	{
		super("Flip board");
		this.board = board;
		addItemListener(this);
		setState(checked);
	}
	
	public void itemStateChanged(ItemEvent e)
	{
		board.rebuildLayout(getState());
	}
}