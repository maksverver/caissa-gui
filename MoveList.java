import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class MoveList extends java.awt.List implements ActionListener
{
	private GameController controller;
	private GamePanel gamePanel;
	
	public MoveList(GameController controller, GamePanel gamePanel)
	{
		this.controller = controller;
		this.gamePanel = gamePanel;
		addActionListener(this);
	}
	
	public void reset(Collection moveCollection)
	{
		ArrayList moves = new ArrayList(moveCollection);
		Collections.sort(moves);
		setVisible(false); removeAll();
		Iterator i = moves.iterator();
		while(i.hasNext()) add(((Move)i.next()).toString());
		setVisible(true); 
	}
	
  public void actionPerformed(ActionEvent e)
  {
		if(!gamePanel.moveEnabled()) return;

  	String moveString = getSelectedItem();
  	if(moveString != null)
  		controller.request(Move.fromString(moveString));
  }  	
}