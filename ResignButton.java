import java.awt.*;
import java.awt.event.*;

class ResignButton extends Button implements ActionListener
{
	private GameController controller;
		
	public ResignButton(GameController controller)
	{
		super("Resign");
		this.controller = controller;
		addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		controller.request(null);
	}
}